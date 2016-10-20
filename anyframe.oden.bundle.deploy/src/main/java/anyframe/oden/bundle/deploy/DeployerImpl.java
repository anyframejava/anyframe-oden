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
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipException;

import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.FileUtil;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.PairValue;

/**
 * @see DeployerService
 * 
 * @author joon1k
 *
 */
public class DeployerImpl implements DeployerService, Serializable {
	
	private static final long serialVersionUID = -2192884775487476693L;

	private final static String TMP_PREFIX = "oden_";
	
	private String fpath;
	private long date;
	private boolean updatejar = false;
	private OutputStream out;
	private File tmpfile;
	
	public void init(String fpath, long date, boolean update) 
			throws IOException {
		try{
			this.fpath = new File(fpath).getPath();		// to get normilized one.
			this.date = date;
			this.updatejar = updatejar(fpath, update);
			
			tmpfile = File.createTempFile(TMP_PREFIX, String.valueOf(System.currentTimeMillis()) );
			tmpfile.deleteOnExit();
			
			this.out = new BufferedOutputStream(new FileOutputStream(tmpfile));
		}catch(RuntimeException e){
			throw new IOException(e.getMessage());
		}
	}

	private boolean updatejar(String path, boolean update) {
    	return update && path.endsWith(".jar") && new File(path).exists();
	}
	
	public void write(byte[] buf) throws IOException {
		out.write(buf);
	}

	public void write(byte[] buf, int size) throws IOException {
		out.write(buf, 0, size);
	}
	
	public long close(List<String> updatefiles) throws IOException {
		if(this.out != null)
			this.out.close();
		
		if(tmpfile == null || !tmpfile.exists())
			return 0;
		
		try{
			File destfile = new File(fpath);
			FileUtil.createNewFile(destfile);
			destfile.setLastModified(date);
			
			try{
				if(updatejar){
					updatefiles.addAll(FileUtil.updateJar(destfile, tmpfile));
				}else {
					FileUtil.copy(tmpfile, destfile);
				}
				tmpfile.delete();
			}catch(ZipException e){
				throw new IOException("Fail to update jar: " + fpath);
			}
			return destfile.length();
		}catch(RuntimeException e){
			throw new IOException(e.getMessage());
		}
	}
	
	/**
	 * @deprecated
	 */
	public DeployerOutputStream getDeployerOutputStream(String fpath,  
			long date, boolean update) throws IOException {
		return new DeployerOutputStream(fpath, date, update);
	}
	
	public synchronized FileInfo compress(String srcdir, String destdir) 
			throws OdenException {
		try{
			return compress(new File(srcdir), new File(destdir));
		}catch(RuntimeException e){
			throw new OdenException(e);
		}
	}

	private synchronized FileInfo compress(File srcdir, File destdir) 
			throws OdenException {
		File uniquef = null;
		try {
			uniquef = FileUtil.uniqueFile(destdir, "ss" + today(), "");
			FileUtil.createNewFile(uniquef);
			FileUtil.compress(srcdir, uniquef);
			return new FileInfo(uniquef.getName(), false, uniquef.lastModified(), uniquef.length());
		} catch (IOException e) {
			if(uniquef != null && uniquef.exists())
				FileUtil.removeFile(uniquef);
			throw new OdenException("Fail to compress: " + srcdir.getPath(), e);
		}
	}

	private String today(){
		return new SimpleDateFormat("yyMMdd").format(System.currentTimeMillis());
	}
	
	public List<PairValue<String, Boolean>> extract(String srcdir, String zipname, String destdir)
			throws OdenException {
		try{
			cleanRollbackDestination(new File(destdir));
			return extractSnapshot(new File(srcdir, zipname), new File(destdir));
		}catch(RuntimeException e){
			throw new OdenException(e.getMessage());
		}
	}

	private List<PairValue<String, Boolean>> extractSnapshot(File src, File dest) throws OdenException {
		List<PairValue<String, Boolean>> extractedfiles = null;
		try {
			extractedfiles = FileUtil.extractZip(src, dest);
		} catch (ZipException e) {
			throw new OdenException("Illegal snapshot format: " + src.getPath(), e);
		} catch (Exception e) {
			throw new OdenException("Fail to restore snapshot: " + src.getPath(), e);
		}
		return extractedfiles;
	}

	public void removeFile(String dir, String filename) throws OdenException {
		try{
			FileUtil.removeFile(new File(dir, filename));
		}catch(RuntimeException e){
			throw new OdenException("Fail to remove the file: " + filename, e);
		}
	}

	private void cleanRollbackDestination(File dir) {
		FileUtil.removeDir(dir);
	}
	
	public long getDate(String parentpath, String path) throws OdenException {
		try{
			return new File(parentpath, path).lastModified();
		}catch(RuntimeException e){
			throw new OdenException("Fail to get the date of the file: " + path, e);
		}
	}
	
}