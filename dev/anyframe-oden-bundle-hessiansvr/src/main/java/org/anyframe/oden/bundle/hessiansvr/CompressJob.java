/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.anyframe.oden.bundle.hessiansvr;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.anyframe.oden.bundle.common.FileUtil;
import org.anyframe.oden.bundle.deploy.DoneFileInfo;
import org.anyframe.oden.bundle.deploy.StoppableJob;

/**
 * 
 * Job to compress files. 
 * @see anyframe.oden.bundle.deploy.StoppableJob
 * 
 * @author joon1k
 *
 */
public class CompressJob extends StoppableJob {
	String srcdir;
	String destFile;
	
	public CompressJob(String id, String srcdir, String destFile){
		super(id);
		this.srcdir = srcdir;
		this.destFile = destFile;
	}
	
	
	@Override
	protected Object execute() throws IOException {
		File s = new File(srcdir);
		File d = new File(destFile);
		
		if(!s.isAbsolute())
			throw new IOException("Absolute path is allowed only: " + s);
		if(!d.isAbsolute())
			throw new IOException("Absolute path is allowed only: " + d);
		
		return compress(s, d);		
	}

	private DoneFileInfo compress(File srcdir, File destFile) throws IOException {		
		try {
			FileUtil.mkdirs(destFile);
			_compress(srcdir, destFile);
			return new DoneFileInfo(destFile.getName(), false, destFile.lastModified(), destFile.length(), false, true);
		}catch(IOException e) {
			if(destFile != null)
				destFile.delete();
			throw e;
		}
	}
	
	/**
	 * dir을 jar로 묶음.
	 * jar가 이미 존재하면, 기존꺼 새걸로 바꿈.
	 * @param dir
	 * @param jar
	 * @return size of the compressed file
	 * @throws IOException 
	 */
	private long _compress(File dir, File jar) throws IOException{
		if(!dir.exists() || !dir.isDirectory())
			throw new IOException("Couldn't find: " + dir);
		if(jar.exists()){
			if(jar.isDirectory())
				throw new IOException("Fail to write: " + jar.getPath());
			jar.delete();
		}

		ZipOutputStream jout = null;
		try{
			jout = new ZipOutputStream(
					new BufferedOutputStream(new FileOutputStream(jar) ));
			compressDir(dir, dir, jout);
			return jar.length();
		} finally {
			if(jout != null) jout.close();
		}
	}
	
	/**
	 * dir의 파일들을 out으로 압축. root는 dir과 동일하게 적으면 됨
	 * 파일들의 시간 그대로 유지.
	 * @param root
	 * @param dir
	 * @param out
	 * @return size of the orginal dir
	 * @throws IOException
	 */
	private void compressDir(final File root, File dir, ZipOutputStream out) 
			throws IOException {
		File[] files = dir.listFiles();
		if(files != null){
			for(File file : files) {
				if(stop)
					throw new IOException("stopped by a user.");
				
				if(file.isDirectory()){
					compressDir(root, file, out);
					continue;
				}
				
				InputStream in = null;
				try {
					in = new BufferedInputStream(
							new FileInputStream(file));
					ZipEntry entry = new ZipEntry(FileUtil.getRelativePath(root.getPath(), file.getPath()));
					out.putNextEntry(entry);
					
					FileUtil.copy(in, out);
					entry.setTime(file.lastModified());

				} finally {
					try { out.closeEntry(); } catch (IOException e) {}
					try{ if(in != null) in.close(); } catch(IOException e) {}
					
				}
			}
						
		}
	}
}
