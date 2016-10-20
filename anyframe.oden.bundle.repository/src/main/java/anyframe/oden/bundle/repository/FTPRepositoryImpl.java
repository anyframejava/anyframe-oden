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
package anyframe.oden.bundle.repository;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import anyframe.oden.bundle.common.FatInputStream;
import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.FileUtil;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.OdenIncompleteArgumentException;

/**
 * RepositoryService to access the ftp.
 * 
 * @author joon1k
 *
 */
public class FTPRepositoryImpl extends AbstractRepositoryimpl {
	public static final String PROTOCOL = "ftp://";

	public String getProtocol(){
		return PROTOCOL;
	}
	
	/**
	 * 
	 * @param addr
	 * @param id	null available
	 * @param pwd		null available
	 * @return
	 * @throws OdenException
	 */
	private FTPClient initFTP(String addr, String id, String pwd) throws OdenException{
		URL _url = null;
		try {
			_url = new URL(addr);
		} catch (MalformedURLException e) {
			throw new OdenException("Illegal URL syntax: " + addr);
		}
		int port = _url.getPort();
		
		FTPClient ftp = new FTPClient();
		try {
			ftp.setDefaultTimeout(10000);
			ftp.connect(_url.getHost(), port == -1 ? 21 : port);
			if(!FTPReply.isPositiveCompletion( ftp.getReplyCode()))
				throw new IOException();
		} catch (IOException e) {
			throw new OdenException("FTP connection failed: "
					+  ftp.getReplyString());
		}
		
		try {
			if(!ftp.login(id, pwd))
				throw new IOException();
		} catch (IOException e) {
			throw new OdenException("Could not login to FTP server. id: " +
					id + ", pwd: " + pwd);
		}
		
		try {
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			if (!FTPReply.isPositiveCompletion( ftp.getReplyCode()))
				throw new IOException();
		} catch (IOException e) {
			throw new OdenException("Could not set transfer type: "
	                +  ftp.getReplyString());
		}
        return ftp;
	        
	}
	
	private FTPClient initFTP(String[] repoargs) throws OdenException{
		if(repoargs.length < 2)
			throw new OdenIncompleteArgumentException(repoargs);
		String uri = repoargs[0];
		String id = repoargs.length > 2 ? repoargs[2] : null;
		String pwd = repoargs.length > 3 ? repoargs[3] : null;
		
		FTPClient ftp = initFTP(uri, id, pwd);
		
		String repoRoot = repoargs[1];
		try {
			if(repoRoot != null)
				ftp.changeWorkingDirectory(repoRoot);
		} catch (IOException e) {
			throw new OdenException(e);
		}
		return ftp;
	}
	
//	private FTPClient initFTP(String[] repoargs) throws OdenException{
//		return FTPConnectionPool.instance().connection(repoargs);
//	}

	private void releaseFTP(FTPClient ftp) {
		if(ftp != null && ftp.isConnected()){
			try{
				ftp.logout();
				ftp.disconnect();
			}catch(IOException e){}
		}
	}

	/**
	 * @param args arguments to access the ftp repository.
	 * @param file file path from the repository's root(not including root).
	 * @return
	 */
	public FatInputStream resolve(String[] args, String file) throws OdenException {	
		FatInputStream in = null;
		FTPClient ftp = null;
		try {
			ftp = initFTP(args);
			
			args[1] = args[1].startsWith("/") ? args[1] : "/" + args[1];
			String abspath = FileUtil.combinePath(args[1], file);
			FTPFile ffile = ftpfile(ftp, abspath);
			in = new FatInputStream(new FTPInputStream(
					ftp.retrieveFileStream(abspath), ftp), file, 
					ffile.isDirectory(),
					ffile.getTimestamp().getTimeInMillis());
			return in;
		} catch (Exception e) {
			throw new OdenException(e);
		} 
	}
	
	/**
	 * 
	 * @param ftp
	 * @param fpath ftp file's path. start with '/'. 
	 * @return
	 * @throws IOException
	 */
	private FTPFile ftpfile(FTPClient ftp, String fpath) throws IOException {
		fpath = fpath.startsWith("/") ? fpath : "/" + fpath;
		String parent = FileUtil.parentPath(fpath);
		String name = FileUtil.fileName(fpath);
		
		if(parent != null) 
			ftp.changeWorkingDirectory(parent);
		parent = ftp.printWorkingDirectory();
		
		for(FTPFile ffile : ftp.listFiles()){
			if(ffile.getName().equals(name))
				return ffile;
		}
		throw new IOException("Couldn't find that file: " + fpath);
	}
	
	/**
	 * Get file list, if dir, it will be ignored.
	 */
	public List<String> resolveFileRegex(String[] args,
			List<String> includes, List<String> excludes) throws OdenException{
		List<String> matched = null;
		FTPClient ftp = null;		
		try {
			ftp = initFTP(args);
			String repoRoot = args[1].startsWith("/") ? args[1] : "/" + args[1];
			
			String parent = FileUtil.commonParent(includes);
			if(parent != null){
				List<String> _incs = new ArrayList<String>(includes);
				for(String inc : includes)
					_incs.add(FileUtil.getRelativePath(parent, inc));				
				matched = getMatchedFiles(ftp, repoRoot, FileUtil.combinePath(repoRoot, parent), _incs, excludes);
			}else 
				matched = getMatchedFiles(ftp, repoRoot, repoRoot, includes, excludes);
		} catch (IOException e) {
			throw new OdenException("Fail to run ftp command", e);
		} finally {
			releaseFTP(ftp);
		}		
		return matched;
		
	}
	
	/**
	 * 
	 * @param ftp
	 * @param root	root path to calculate a relative path.
	 * @param parent path including root path. must start with '/'.
	 * @param includes
	 * @param excludes
	 * @return
	 * @throws IOException
	 */
	private List<String> getMatchedFiles(final FTPClient ftp, final String root, String parent, 
			final List<String> includes, final List<String> excludes) 
					throws IOException{
		List<String> matched = new ArrayList<String>();
		
//		String currentdir = ftp.printWorkingDirectory(); 
		ftp.changeWorkingDirectory(parent);
		parent = ftp.printWorkingDirectory();
		FTPFile[] files = ftp.listFiles();		
//		ftp.changeWorkingDirectory(currentdir);
		if(files != null){
			for(FTPFile file : files) { 
				if(file == null || !file.hasPermission(FTPFile.USER_ACCESS, 
						FTPFile.READ_PERMISSION)) 
					continue;

				String path = FileUtil.combinePath(parent, file.getName());
				String rpath = FileUtil.getRelativePath(root, path);
				if(file.isDirectory())
					matched.addAll(getMatchedFiles(ftp, root, path, includes, excludes));
				else
					if(FileUtil.matched(rpath, includes, excludes))
						matched.add(rpath);				
			}
		}
		return matched;
	}
	
	public FTPRepositoryModel getRepoInfo(String args) throws OdenException{
		String[] splited = args.split("\\s");
		if(splited.length < 3)
			throw new OdenException("Illegal ftp repo args. Usage: " + getUsage());
		FTPRepositoryModel model = new FTPRepositoryModel();
		// splited[0] is repo type
		model.uri = splited[1];
		model.root = splited[2];
		return model;
	}
	
	public List<FileInfo> getFileList(String[] args) throws OdenException {
		List<FileInfo> files = new ArrayList<FileInfo>();
		FTPClient ftp = null;
		try {
			ftp = initFTP(args);
			String path = args[1];
			
			for(FTPFile ff : ftp.listFiles()){
				if(ff == null || !ff.hasPermission(FTPFile.USER_ACCESS, FTPFile.READ_PERMISSION)) 
					continue;
				String ffpath = FileUtil.combinePath(path, ff.getName());
				FileInfo mf = new FileInfo(ffpath, ff.isDirectory(), 
						ff.getTimestamp().getTimeInMillis());
				files.add(mf);
			}
		} catch (IOException e) {
			throw new OdenException("Fail to run ftp command", e);
		} finally {
			releaseFTP(ftp);
		}
		return files;
	}
	
	public String getUsage() {
		return getProtocol() + "<host> <path> [<id> <password>]";
	}
	
	protected class FTPRepositoryModel {
		String uri;
		String root;
	}
	
	/**
	 * BufferedInputStream & if close, ftp will be disconnected
	 *
	 */
	protected class FTPInputStream extends BufferedInputStream {
		private FTPClient ftp = null;
		
		public FTPInputStream(InputStream in, FTPClient ftp) {
			super(in);
			this.ftp = ftp;
		}

		@Override
		public void close() throws IOException {
			try{
				super.close();
			} finally {
				releaseFTP(ftp);
			}	// end of try catch
			
		}
		
	}
	
}
