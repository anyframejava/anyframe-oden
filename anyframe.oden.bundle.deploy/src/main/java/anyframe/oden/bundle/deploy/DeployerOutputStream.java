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
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipException;

import anyframe.oden.bundle.common.FileUtil;

/**
 * Because Outputstream can't be serializable, this class doesn't work well.
 * 
 * @deprecated
 * @author joon1k
 *
 */
public class DeployerOutputStream implements Serializable {
	private static final long serialVersionUID = -8235930197949746453L;

	private final static String TMP_PREFIX = "oden_";
	
	private String fpath;
	private long date;
	private boolean updatejar = false;
	private OutputStream out;
	private File tmpfile;
	
	public DeployerOutputStream(String fpath, long date, boolean update) 
			throws IOException {
		this.fpath = fpath;
		this.date = date;
		this.updatejar = updatejar(fpath, update);
		
		tmpfile = File.createTempFile(TMP_PREFIX, String.valueOf(System.currentTimeMillis()) );
		tmpfile.deleteOnExit();
		
		this.out = new BufferedOutputStream(new FileOutputStream(tmpfile));
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
    
	public void close() throws IOException {
    	closeAll();
    }
	
	public List<String> closeAll() throws IOException {
		this.out.close();
		
		if(tmpfile == null || !tmpfile.exists())
			return Collections.EMPTY_LIST;
		
		File destfile = new File(fpath);
		destfile.setLastModified(date);
		
		List<String> updatedfiles = Collections.EMPTY_LIST;
		try{
			if(updatejar){
				updatedfiles = FileUtil.updateJar(destfile, tmpfile);
			}else {
				FileUtil.copy(tmpfile, destfile);
			}
			tmpfile.delete();
		}catch(ZipException e){
			throw new IOException("Fail to update jar: " + fpath);
		}
		return updatedfiles;
	}
}
