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
package anyframe.oden.bundle.hessiansvr;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import anyframe.oden.bundle.common.FileUtil;
import anyframe.oden.bundle.deploy.DoneFileInfo;

/**
 * 
 * This class make files with BufferedOutputStream class.
 * 
 * @author joon1k
 *
 */
public class DeployOutputStream{
	private final static String TMP_PREFIX = "oden_";
	
	private String parentPath;
	private String filePath;
	private OutputStream out;
	private File tmpfile;
	private long date;

	public DeployOutputStream(String parent, 
			String path, long date) throws IOException{
		String fullPath = FileUtil.combinePath(parent, path);
		if(!new File(fullPath).isAbsolute())
			throw new IOException("Absolute path is allowed only: " + fullPath);
		
		this.parentPath = parent;
		this.filePath = path;
		
		try{
			tmpfile = File.createTempFile(TMP_PREFIX, String.valueOf(System.currentTimeMillis()) );
			this.date = date;
			
			this.out = new BufferedOutputStream(new FileOutputStream(tmpfile));
		}catch(IOException e){
			try{ if(out != null) out.close(); }catch(IOException e2){}
			if(tmpfile != null) tmpfile.delete();
			throw e;
		}
	}
	
	/**
	 * write bytes to this file
	 * 
	 * @param buf
	 * @param size
	 * @return
	 */
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
	
	public boolean write(byte[] buf) {
		return write(buf, buf.length);
	}
	
	/**
	 * close this stream and copy temp file to original one. Before copying,
	 * backup original one to the bakdir.
	 * 
	 * @param updatefiles
	 * @param bakdir
	 * @return
	 * @throws IOException
	 */
	public DoneFileInfo close(List<String> updatefiles, String bakdir) throws IOException {
		boolean isUpdated = false;
		try{				
			if(this.out != null) this.out.close();
			// tmpfile can have 0 size cause File.createTempFile method.
			if(tmpfile == null || !tmpfile.exists())
				throw new IOException("Fail to transfer file: " + filePath);
			if(date > -1) tmpfile.setLastModified(date);
			
			File destfile = new File(parentPath, filePath);
			if(bakdir != null && !(new File(bakdir).isAbsolute()) )
				throw new IOException("Backup location should be a absolute path: " + bakdir);
			
			// backup			
			if(destfile.exists()){	
				isUpdated = DeployerUtils.undoBackup(parentPath, filePath, bakdir);
			} else {
				FileUtil.mkdirs(destfile);
			}
			
			// copy
			if(updatefiles != null){	// jar update. this is not used
				updatefiles.addAll(FileUtil.updateJar(tmpfile, destfile));
			}else {
				FileUtil.copy(tmpfile, destfile);
			}				
			return new DoneFileInfo(
					filePath, 
					false, 
					destfile.lastModified(), 
					destfile.length(), 
					isUpdated, 
					true);
		}finally{
			if(tmpfile != null) tmpfile.delete();	
		}
	}

}
