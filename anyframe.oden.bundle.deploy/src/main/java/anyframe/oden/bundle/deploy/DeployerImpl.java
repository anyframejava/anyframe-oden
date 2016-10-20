/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package anyframe.oden.bundle.deploy;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.ComponentContext;

import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.FileUtil;


/**
 *  í•¨ìˆ˜ì�´ë¦„ë“¤ ëª¨ë‘� ìˆ˜ë�™ì �ìœ¼ë¡œ ì§€ì •í•  ê²ƒ.. ì–˜ëŠ” íŒ�ë‹¨í•˜ëŠ” ê¸°ëŠ¥ì�´ ì—†ì�Œ.
 *  
 * @see DeployerService
 * 
 * @author joon1k
 *
 */
public class DeployerImpl implements DeployerService, Serializable {
	
	private static final long serialVersionUID = -2192884775487476693L;

	private int MAX_UNDO = 10;
	
	private final static String TMP_PREFIX = "oden_";
	
	private String parentPath;
	private String filePath;
	private long date;
	private boolean updatejar = false;
	private OutputStream out;
	private File tmpfile;
	
	protected void activate(ComponentContext context){
		String p = context.getBundleContext().getProperty("undo.max");
		if(p != null)
			MAX_UNDO = Integer.valueOf(p);
	}
	
	public boolean exist(String parent, String child){
		return new File(parent, child).exists();
	}

	public boolean writable(String parent, String child){
		File f = new File(parent, child);
		return !f.exists() || f.canWrite();
	}
	
	public boolean init(String parent, String relpath, long date, boolean update){
		this.parentPath = parent;
		this.filePath = relpath;
		String fullPath = FileUtil.combinePath(parent, filePath);
		File target = new File(fullPath);
		
		try{
			if(!target.isAbsolute())
				throw new IOException("Absolute path is allowed only: " + fullPath);
			
			this.date = date;
			this.updatejar = updatejar(fullPath, update);
			
			tmpfile = File.createTempFile(TMP_PREFIX, String.valueOf(System.currentTimeMillis()) );
			tmpfile.deleteOnExit();
			
			this.out = new BufferedOutputStream(new FileOutputStream(tmpfile));
		}catch(IOException e){
			if(tmpfile != null) tmpfile.delete();
			try{ if(out != null) out.close(); }catch(IOException e2){}
			return false;
		}
		return true;
	}

	private boolean updatejar(String path, boolean update) {
    	return update && path.endsWith(".jar") && new File(path).exists();
	}
	
	public boolean write(byte[] buf) {
		return write(buf, buf.length);
	}

	public boolean write(byte[] buf, int size) {
		try {
			if(out != null){
				out.write(buf, 0, size);
				return true;
			}
		}catch(IOException e){
		}
		return false;
	}
	
	/**
	 * @param updatefiles updated files when updating jar. null if you do not want to update jar.
	 * @param bakdir dir for backup. null if you don't want backup.
	 * @throws IOException 
	 */
	public DoneFileInfo close(List<String> updatefiles, String bakdir) throws IOException {
		if(this.out != null) this.out.close();
		
		if(tmpfile == null || !tmpfile.exists())
			throw new IOException("Fail to transfer file: " + filePath);
		
		if(bakdir == null || !(new File(bakdir).isAbsolute()) ){
			tmpfile.delete();
			throw new IOException("Couldn't find any backup location: " + bakdir);
		}
		
		boolean isBackuped = false;
		File destfile = new File(parentPath, filePath);
		try{
			// backup
			if(destfile.exists()){	
				isBackuped = undoBackup(parentPath, filePath, bakdir);
			} else {
				FileUtil.createNewFile(destfile);
			}
			destfile.setLastModified(date);
			
			// copy
			if(updatejar && updatefiles != null){
				updatefiles.addAll(FileUtil.updateJar(tmpfile, destfile));
			}else {
				FileUtil.copy(tmpfile, destfile);
			}			
		}finally{
			tmpfile.delete();	
		}
		return new DoneFileInfo(
				filePath, 
				false, 
				destfile.lastModified(), 
				destfile.length(), 
				isBackuped, 
				true);
	}
	
	/**
	 * @deprecated
	 */
	public DeployerOutputStream getDeployerOutputStream(String fpath,  
			long date, boolean update) throws IOException {
		return new DeployerOutputStream(fpath, date, update);
	}
	
	public synchronized DoneFileInfo compress(String srcdir, String destdir) {
		File s = new File(srcdir);
		File d = new File(destdir);
		
		if(s.isAbsolute() && d.isAbsolute())
			return compress(new File(srcdir), new File(destdir));
		return null;
	}

	private synchronized DoneFileInfo compress(File srcdir, File destdir) {		
		File uniquef = null;
		boolean created = false;
		try {
			uniquef = FileUtil.uniqueFile(destdir, "ss" + today(), "");
			created = FileUtil.createNewFile(uniquef);
			FileUtil.compress(srcdir, uniquef);
			return new DoneFileInfo(uniquef.getName(), false, uniquef.lastModified(), uniquef.length(), false, true);
		}catch(IOException e) {
			if(uniquef != null && created)
				uniquef.delete();
		}
		return null;
	}

	private String today(){
		return new SimpleDateFormat("yyMMdd").format(System.currentTimeMillis());
	}
	
	public List<DoneFileInfo> extract(String srcdir, String zipname, String destdir) {
		try{
			File s = new File(srcdir);
			File d = new File(destdir);		
			if(s.isAbsolute() && d.isAbsolute())
				return extractSnapshot(new File(s, zipname), d);
		}catch(IOException e){
		}
		return new ArrayList<DoneFileInfo>();
	}

	private List<DoneFileInfo> extractSnapshot(File src, File dest) throws IOException {
		List<DoneFileInfo> result = new ArrayList<DoneFileInfo>();
		Map<FileInfo, Boolean> m = FileUtil.extractZip(src, dest);
		for(FileInfo info : m.keySet()){
			result.add(new DoneFileInfo(info.getPath(), info.isDir(), info.lastModified(), info.size(), false, true));
		}
		return result;
	}

	public void removeFile(String dir, String filename){
		File s = new File(dir, filename);
		if(s.isAbsolute())
			s.delete();
	}

	public List<DoneFileInfo> backupNRemoveDir(String dir, String bak) {
		List<DoneFileInfo> infos = new ArrayList<DoneFileInfo>();
		try {
			File f = new File(dir);
			if(f.isAbsolute());
				backupRemoveDir(infos, f, dir, bak);
		} catch (IOException e) {
		}
		return infos;
	}
	
	private void backupRemoveDir(List<DoneFileInfo> infos, File dir, final String root, final String bak) throws IOException {
		if(dir != null && dir.exists()) {
			File[] files = dir.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					backupRemoveDir(infos, files[i], root, bak);
				}else {
					DoneFileInfo info = new DoneFileInfo(
							FileUtil.getRelativePath(root, files[i].getPath()), 
							false, 
							files[i].lastModified(), 
							files[i].length(), false, false);
					infos.add(info);
					
					info.setUpdate(undoBackup(root, info.getPath(), bak));
					info.setSuccess(files[i].delete());
				}
			}
	        dir.delete();
        }
	}
	
	/**
	 * @return date of the file (parentpath/path). -1 if there's no such file.
	 */
	public long getDate(String parentpath, String path){
		File f = new File(parentpath, path);
		if(f.isAbsolute())
			return f.lastModified();
		return -1L;
	}
		
	/**
	 * make destination's backup & copy src to dest
	 * 
	 * @param parentPath
	 * @param filePath
	 * @param destPath
	 * @param bakPath null if no backup
	 * @return fileinfo copied
	 */
	public DoneFileInfo backupNCopy(String srcPath, String filePath, String destPath, String bakPath) {
		boolean success = true;
		boolean srcExist = false;
		try{
			if(!new File(srcPath).isAbsolute() || 
					!new File(destPath).isAbsolute() ||
					!new File(srcPath, filePath).exists())
				throw new IOException();
			
			//backup
			if(bakPath != null && new File(bakPath).isAbsolute() )
				srcExist = undoBackup(destPath, filePath, bakPath);
			
			//copy
			copy(srcPath, filePath, destPath);
		}catch(IOException e){
			success = false;
		}
		
		File copied = new File(destPath, filePath); 
		return new DoneFileInfo(FileUtil.combinePath(srcPath, filePath), false, copied.lastModified(), copied.length(), srcExist, success);
		
	}
	
	/**
	 * make source's backup & remove
	 * 
	 * @param srcPath
	 * @param filePath
	 * @param bakPath
	 * @return fileinfo removed
	 */
	public DoneFileInfo backupNRemove(String srcPath, String filePath, String bakPath) {
		boolean success = true;
		boolean srcExist = false;
		try{
			if(!new File(srcPath).isAbsolute() || 
					!new File(bakPath).isAbsolute() ||
					!new File(srcPath, filePath).exists())
				throw new IOException();
			
			//backup
			if(bakPath != null && new File(bakPath).isAbsolute())
				srcExist = undoBackup(srcPath, filePath, bakPath);
			
			//copy
			new File(srcPath, filePath).delete();
		}catch(IOException e){
			success = false;
		}	
		
		File baked = new File(bakPath, filePath);
		return new DoneFileInfo(FileUtil.combinePath(srcPath, filePath), false, baked.lastModified(), baked.length(), srcExist, success);
		
	}
	
	private void copy(String parentPath, String filePath, String destPath) throws IOException{
		File destFile = new File(destPath, filePath);
		boolean created = false;
		try{
		created = FileUtil.createNewFile(destFile);
		FileUtil.copy(new File(parentPath, filePath), destFile);
		}catch(IOException e){
			if(destFile != null && created) destFile.delete();
		}
	}
	
	/**
	 * 
	 * @param parentPath
	 * @param filePath
	 * @param bakPath
	 * @return is backuped?
	 * @throws IOException
	 */
	private boolean undoBackup(String parentPath, String filePath, String bakPath) throws IOException {
		if(!new File(parentPath, filePath).exists()
				|| !new File(bakPath).isAbsolute())
			return false;

		removeOldUndos(new File(bakPath).getParent());
			
		copy(parentPath, filePath, bakPath);
		return true;
	}
	
	private void removeOldUndos(String bakDir) {
		File bak = new File(bakDir);
		if(!bak.exists())
			return;
		
		List<Long> list = new ArrayList<Long>();
		File[] fs = bak.listFiles();
		for(File f : fs){
			if(f.isDirectory()){
				try{
					long date = Long.parseLong(f.getName());
					list.add(date);
				}catch(Exception e){}
			}
		}
		
		if(list.size() > MAX_UNDO){
			Collections.sort(list, Collections.reverseOrder());
			for(int i=MAX_UNDO; i<list.size(); i++)
				FileUtil.removeDir(new File(bakDir, String.valueOf(list.get(i)) ) );
		}
	}

}