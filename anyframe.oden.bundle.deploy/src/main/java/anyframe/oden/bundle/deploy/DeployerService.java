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

import java.io.IOException;
import java.util.List;

/**
 * This class provides some methods to manipulate remote files. 
 * If you want to handling remote files, register this service to the R-OSGi bundle.
 * Your service can access remote files using that registered service.
 * 
 * @author joon1k
 *
 */
public interface DeployerService {

	public boolean init(String parent, String relpath, long date, boolean update);
	
	public boolean write(byte[] buf);

	public boolean write(byte[] buf, int size);
	
	public DoneFileInfo close(List<String> updatefiles, String bakdir) throws IOException;
	
	
	public boolean exist(String parent, String child);
	
	public boolean writable(String parent, String child);
	
    /**
     * compress srcdir to destdir/filename
     * 
     * @param srcdir
     * @param destdir
     * @param filename
     * @return size of the compressed file.
     */
    public DoneFileInfo compress(String srcdir, String destdir);

    /**
     * extract srcdir/zipname to destdir
     * 
     * @param srcdir
     * @param zipname
     * @param destdir
     * @return file list which are extracted.
     */
	public List<DoneFileInfo> extract(String srcdir, String zipname, String destdir);

    /**
     * remove file dir/filename
     * 
     * @param dir
     * @param filename
     */
	public void removeFile(String dir, String filename);
	
	/**
	 * get last modified date for parentpath/path
	 * 
	 * @param parentpath
	 * @param path
	 * @return 0L if the file does not exist or if an I/O error occurs
	 */
	public long getDate(String parentpath, String path) ;
	

	public List<DoneFileInfo> backupNRemoveDir(String dir, String bak);
	
	public DoneFileInfo backupNCopy(String srcPath, String filePath, String destPath, String bakPath);
	
	public DoneFileInfo backupNRemove(String srcPath, String filePath, String bakPath);
	
}
