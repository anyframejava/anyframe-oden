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
package anyframe.oden.bundle.core.repository;

import java.io.File;
import java.util.List;

import anyframe.oden.bundle.common.FatInputStream;
import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.OdenException;

/**
 * Oden Service to access some file repositories like local filesystem or ftp.
 * 
 * @author joon1k
 *
 */
public interface RepositoryService {
	public boolean matchedURI(String[] args);
	
	public String getProtocol();

	/**
	 * 
	 * @param repoArgs
	 * @param string2 
	 * @param string 
	 * @param regex should be finished with file or wildcard file or **
	 * @param isRecentOnly
	 * @return
	 * @throws OdenException
	 */
	public List<String> resolveFileRegex(String[] args, 
			List<String> includes, List<String> excludes) 
			throws OdenException;

	/**
	 * Get file inputstream & its info using args. If there're some files to resolve, 
	 * after resolving all files. close method should be called.
	 * 
	 * @param args
	 * @param file
	 * @return
	 * @throws OdenException
	 */
	public FatInputStream resolve(String[] args, String file) throws OdenException;

	/**
	 * return required arguments to access repository service.
	 * 커맨드라인에서 usage 로 사용 될 것임. 계정관련 인자는 사용법에서 제외
	 * 
	 * @return
	 */
	public String getUsage();

	public List<FileInfo> getFileList(String[] args) throws OdenException;
	
	/**
	 * This makes RepositoryService to close its connection. Some RepositoryService
	 * may sustain its connection for your Thread. In that case, because there's no way
	 * to know if that Thread is alive, that connection will not be closed until program
	 * is died. So, if you think your Thread will not use RepositoryService for some time,
	 * call close method to close the connection. After being closed, if your Thread calls 
	 * RepositoryService, a new connection will be created.
	 */
	public void close(String[] args);
	
	/**
	 * copy repo[]/fpath to destpath/fpath
	 * 
	 * @param repo
	 * @param fname
	 * @param destpath
	 * @return
	 * @throws OdenException
	 */
	public File getFile(String[] repoargs, String fpath, String destpath) throws OdenException;
}
