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
package org.anyframe.oden.bundle.deploy;

import java.util.List;
import java.util.Set;

import org.anyframe.oden.bundle.common.FileInfo;

/**
 * This class provides some methods to manipulate remote files. If you want to
 * handling remote files, register this service to the R-OSGi bundle. Your
 * service can access remote files using that registered service.
 * 
 * @author joon1k
 * 
 */
public interface DeployerService {
	/**
	 * initialize the DeployerStream to write file here
	 * 
	 * @param parent
	 * @param relpath
	 * @param date
	 * @throws Exception
	 */
	// public void init(String parent, String relpath, long date) throws
	// Exception;

	public void init(String parent, String path, long date, boolean useTmp,
			int backupcnt) throws Exception;

	public boolean write(String parent, String path, ByteArray buf)
			throws Exception;

	/**
	 * close DeployerStream and the information about the file
	 * 
	 * @param updatefiles
	 * @param bakdir
	 * @return
	 * @throws Exception
	 */
	public DoneFileInfo close(String parent, String path,
			List<String> updatefiles, String bakdir) throws Exception;

	/**
	 * get this JVM's stat
	 * 
	 * @return
	 * @throws Exception
	 */
	public String jvmStat() throws Exception;

	/**
	 * check if the specified file is exist
	 * 
	 * @param parent
	 * @param child
	 * @return
	 * @throws Exception
	 */
	public boolean exist(String parent, String child) throws Exception;

	/**
	 * check if the specified file is writable
	 * 
	 * @param parent
	 * @param child
	 * @return
	 * @throws Exception
	 */
	public boolean writable(String parent, String child) throws Exception;

	/**
	 * get the specified file's information
	 * 
	 * @param parent
	 * @param child
	 * @return
	 * @throws Exception
	 */
	public FileInfo fileInfo(String parent, String child) throws Exception;

	/**
	 * get the file list which are matched with the arguments. (path matched at
	 * least one includes and not matched all excludes)
	 * 
	 * @param parent
	 * @param includes
	 * @param excludes
	 * @return
	 * @throws Exception
	 */
	public List<String> resolveFileRegex(String parent, List<String> includes,
			List<String> excludes) throws Exception;

	/**
	 * stop the job having the specified id
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void stop(String id) throws Exception;

	/**
	 * compress srcdir to destdir/filename
	 * 
	 * @param srcdir
	 * @param destdir
	 * @param filename
	 * @return size of the compressed file.
	 * @throws Exception
	 */
	public DoneFileInfo compress(String id, String srcdir, String destFile)
			throws Exception;

	/**
	 * extract srcdir/zipname to destdir
	 * 
	 * @param srcdir
	 * @param zipname
	 * @param destdir
	 * @return file list which are extracted.
	 * @throws Exception
	 */
	public List<DoneFileInfo> extract(String id, String srcdir, String zipname,
			String destdir) throws Exception;

	/**
	 * remove file dir/filename
	 * 
	 * @param dir
	 * @param filename
	 * @throws Exception
	 */
	public void removeFile(String dir, String filename) throws Exception;

	/**
	 * get last modified date for parentpath/path
	 * 
	 * @param parentpath
	 * @param path
	 * @return 0L if the file does not exist or if an I/O error occurs
	 * @throws Exception
	 */
	public long getDate(String parentpath, String path) throws Exception;

	public void setDate(String parent, String path, long date) throws Exception;

	/**
	 * backup directory to the bak and remove it
	 * 
	 * @param id
	 * @param dir
	 * @param bak
	 * @return
	 * @throws Exception
	 */
	// public List<DoneFileInfo> backupNRemoveDir(String id, String dir, String
	// bak) throws Exception;

	/**
	 * backup the dest file to the bakPath and overwrite src file to the dest
	 * file.
	 * 
	 * @param srcPath
	 * @param filePath
	 * @param destPath
	 * @param bakPath
	 * @return
	 * @throws Exception
	 */
	public DoneFileInfo backupNCopy(String srcPath, String filePath,
			String destPath, String bakPath, int backupcnt) throws Exception;

	/**
	 * backup the dest file to the bakPath and remove the dest file.
	 * 
	 * @param srcPath
	 * @param filePath
	 * @param bakPath
	 * @return
	 * @throws Exception
	 */
	public DoneFileInfo backupNRemove(String srcPath, String filePath,
			String bakPath, int backupcnt) throws Exception;

	/**
	 * Oden Server use this method to check if this agent is alive.
	 * 
	 * @return
	 */
	public boolean alive() throws Exception;

	/**
	 * get all nested files information which are in the path.
	 * 
	 * @param id
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public List<FileInfo> listAllFilesAsJob(String id, String path)
			throws Exception;

	public List<FileInfo> listAllFiles(String dir) throws Exception;

	public Set<FileInfo> listAllFilesAsSet(String dir) throws Exception;

	public boolean touchAvailable() throws Exception;

	public String execShellCommand(String command, String dir, long timeout)
			throws Exception;

	public String odenHome() throws Exception;

}
