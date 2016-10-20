/*
 * Copyright 2009, 2010 SAMSUNG SDS Co., Ltd. All rights reserved.
 *
 * No part of this "source code" may be reproduced, stored in a retrieval
 * system, or transmitted, in any form or by any means, mechanical,
 * electronic, photocopying, recording, or otherwise, without prior written
 * permission of SAMSUNG SDS Co., Ltd., with the following exceptions:
 * Any person is hereby authorized to store "source code" on a single
 * computer for personal use only and to print copies of "source code"
 * for personal use provided that the "source code" contains SAMSUNG SDS's
 * copyright notice.
 *
 * No licenses, express or implied, are granted with respect to any of
 * the technology described in this "source code". SAMSUNG SDS retains all
 * intellectual property rights associated with the technology described
 * in this "source code".
 *
 */
package anyframe.oden.bundle.ent.repository;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import anyframe.oden.bundle.common.FatInputStream;
import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.FileUtil;
import anyframe.oden.bundle.common.Logger;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.OdenIncompleteArgumentException;
import anyframe.oden.bundle.core.repository.AbstractRepositoryimpl;

/**
 * RepositoryService to access the ftp.
 * 
 * @author joon1k
 *
 */
public class FTPRepositoryImpl extends AbstractRepositoryimpl {
	public static final String PROTOCOL = "ftp://";

	private FTPConnectionPool connPool = new FTPConnectionPool();
	
	public String getProtocol(){
		return PROTOCOL;
	}

	private FTPClient initFTP(String addr, String id, String pwd) throws OdenException{
		return connPool.connection(addr, id, pwd);
	}
	
	private FTPClient initFTP(String[] repoargs) throws OdenException{
		if(repoargs.length < 2)
			throw new OdenIncompleteArgumentException(repoargs);
		String addr = repoargs[0];
		String repoRoot = repoargs[1];
		String id = repoargs.length > 2 ? repoargs[2] : null;
		String pwd = repoargs.length > 3 ? repoargs[3] : null;

		FTPClient ftp = initFTP(addr, id, pwd);
		try {
			cd(ftp, repoRoot);
		} catch (IOException e) {
			throw new OdenException(e);
		}
		return ftp;
	}

	private void releaseFTP(String[] args) {
		long thread = Thread.currentThread().getId();
		String addr = args[0];
		String id = args.length > 2 ? args[2] : null;
		String pwd = args.length > 3 ? args[3] : null;
		
		connPool.remove(FTPConnectionPool.key(thread, addr, id, pwd));
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
					ffile.getTimestamp().getTimeInMillis(),
					ffile.getSize());
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
		
		cd(ftp, parent);
		
		for(FTPFile ffile : ftp.listFiles()){
			if(ffile.getName().equals(name))
				return ffile;
		}
		throw new IOException("Couldn't find that file: " + fpath + 
				" from the FTP location: " + ftp.printWorkingDirectory());
	}
	
	private void cd(FTPClient ftp, String dir) throws IOException {
		if(!ftp.changeWorkingDirectory(dir))
			throw new IOException("Fail to access the dir: " + dir);		
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
			
			boolean recursive = hasRecursive(includes);
			String parent = FileUtil.commonParent(includes);
			if(parent != null){
				List<String> _incs = new ArrayList<String>(includes);
				for(String inc : includes)
					_incs.add(FileUtil.getRelativePath(parent, inc));				
				matched = getMatchedFiles(ftp, repoRoot, FileUtil.combinePath(repoRoot, parent), _incs, excludes, recursive);
			}else 
				matched = getMatchedFiles(ftp, repoRoot, repoRoot, includes, excludes, recursive);
		} catch (IOException e) {
			throw new OdenException("Fail to run ftp command", e);
		} finally {
			releaseFTP(args);
		}		
		return matched;
		
	}
	
	private boolean hasRecursive(List<String> includes) {
		for(String s : includes)
			if(s.contains("**"))
				return true;
		return false;
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
			final List<String> includes, final List<String> excludes, boolean recursive) 
					throws IOException{
		List<String> matched = new ArrayList<String>();
		 
		cd(ftp, parent);
		parent = ftp.printWorkingDirectory();
		FTPFile[] files = ftp.listFiles();		
		if(files != null){
			for(FTPFile file : files) { 
				if(file == null || !file.hasPermission(FTPFile.USER_ACCESS, 
						FTPFile.READ_PERMISSION)) 
					continue;

				String path = FileUtil.combinePath(parent, file.getName());
				String rpath = FileUtil.getRelativePath(root, path);
				if(file.isDirectory() && recursive)
					matched.addAll(getMatchedFiles(ftp, root, path, includes, excludes, recursive));
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
			releaseFTP(args);
		}
		return files;
	}
	
	public String getUsage() {
		return getProtocol() + "<host> <path> [<id> <password>]";
	}
	
	public void close(String[] args) {
		releaseFTP(args);
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
				if(!ftp.completePendingCommand())
					throw new IOException("Fail to read data from FTP.");
			} finally {
//				releaseFTP(ftp);
			}	// end of try catch
			
		}
		
	}

	/**
	 * copy repo[]/fpath to destpath/fpath
	 * 
	 * @param repo
	 * @param fname
	 * @param destpath
	 * @return
	 * @throws OdenException
	 */
	public File getFile(String[] repoargs, String fpath, String destpath) throws OdenException {		
		File result = null;
		
		FatInputStream in = null;
		OutputStream out = null;
		try{
			result = new File(destpath, fpath); 
			FileUtil.mkdirs(result);
			
			in = resolve(repoargs, fpath);
			out = new BufferedOutputStream(new FileOutputStream(result));
			
			byte[] buf = new byte[1024*8];
			int size = 0;
			while((size = in.read(buf)) != -1){
				out.write(buf, 0, size);
			}
		}catch(Exception e){
			if(result != null){
				result.delete();
				result = null;
			}
			throw new OdenException(e);
		}finally {
			try { if(out != null) out.close(); } catch (IOException e) { }
			try { if(in != null) in.close(); } catch (IOException e) { }
		}
		return result;
	}
	
	public long getDate(String[] args) throws IOException{
		FTPClient ftp;
		try {
			ftp = initFTP(args);
		} catch (OdenException e) {
			throw new IOException(e.getMessage());
		}
		
		File uniqf = uniqueFile(ftp);
		if(uniqf == null || !uniqf.createNewFile())
			throw new IOException("Fail to create unique file.");
		
		InputStream in = null;
		try{
			in = new BufferedInputStream(new FileInputStream(uniqf));
			if(!ftp.storeFile(uniqf.getName(), in))
				throw new IOException(ftp.getReplyString());
			// not necessary to call completePending..
		}finally{
			if(in != null)
				in.close();
		}
		
		FTPFile transfered = ftpfile(ftp, uniqf.getName());
		if(transfered == null)
			throw new IOException("Couldn't find that file: " + uniqf.getName());
		long t = transfered.getTimestamp().getTimeInMillis();
		
		if(!ftp.deleteFile(uniqf.getName()))
			throw new IOException(ftp.getReplyString());
		return t;
	}

	private File uniqueFile(FTPClient ftp) {
		Exception excepn= null;
		File tmpdir;
		try {
			tmpdir = FileUtil.temporaryDir();
		} catch (IOException e1) {
			return null;
		}
		
		final int maxiter = 100;
		for(int i=0; i<maxiter; i++){
			File tmp = new File(tmpdir, "oden0" + String.valueOf(i) + ".tmp");
			if(tmp.exists())
				continue;
			
			try{	
				ftpfile(ftp, tmp.getName());
			}catch(IOException e){
				return tmp;
			}
		}
		
		if(excepn != null)		// write one exception only 
			Logger.error(excepn);
		return null;
	}
}
