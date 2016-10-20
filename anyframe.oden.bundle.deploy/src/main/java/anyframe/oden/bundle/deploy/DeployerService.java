/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package anyframe.oden.bundle.deploy;

import java.io.IOException;
import java.util.List;

import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.OdenException;

/**
 * This class provides some methods to manipulate remote files. 
 * If you want to handling remote files, register this service to the R-OSGi bundle.
 * Your service can access remote files using that registered service.
 * 
 * @author joon1k
 *
 */
public interface DeployerService {
	public void init(String fpath, long date, boolean update) throws IOException;
	
	public void write(byte[] buf) throws IOException;

	public void write(byte[] buf, int size) throws IOException;
	
    public List<String> close() throws IOException;
        
    /**
     * compress srcdir to destdir/filename
     * 
     * @param srcdir
     * @param destdir
     * @param filename
     * @return size of the compressed file.
     * @throws OdenException
     */
    public FileInfo compress(String srcdir, String destdir) 
    		throws OdenException;

    /**
     * extract srcdir/zipname to destdir
     * 
     * @param srcdir
     * @param zipname
     * @param destdir
     * @return file list which are extracted.
     * @throws OdenException
     */
	public List<String> extract(String srcdir, String zipname, String destdir)
			throws OdenException;

    /**
     * remove file dir/filename
     * 
     * @param dir
     * @param filename
     */
	public void removeFile(String dir, String filename) throws OdenException;
	
	/**
	 * get last modified date for parentpath/path
	 * 
	 * @param parentpath
	 * @param path
	 * @return 0L if the file does not exist or if an I/O error occurs
	 */
	public long getDate(String parentpath, String path) throws OdenException;
}
