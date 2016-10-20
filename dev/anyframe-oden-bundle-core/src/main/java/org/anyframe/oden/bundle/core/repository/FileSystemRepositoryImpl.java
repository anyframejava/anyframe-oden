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
package org.anyframe.oden.bundle.core.repository;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.anyframe.oden.bundle.common.BundleUtil;
import org.anyframe.oden.bundle.common.FatInputStream;
import org.anyframe.oden.bundle.common.FileInfo;
import org.anyframe.oden.bundle.common.FileUtil;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.common.OdenIncompleteArgumentException;
import org.anyframe.oden.bundle.common.Pair;
import org.anyframe.oden.bundle.common.StringUtil;

/**
 * RepositoryService to access local file system.
 * 
 * @author joon1k
 *
 */
public class FileSystemRepositoryImpl extends AbstractRepositoryimpl {
	public static final String PROTOCOL = "file://";

	public String getProtocol(){
		return PROTOCOL;
	}

	protected InputStream getJarFile(String file)
			throws OdenException {
		try {
			return new BufferedInputStream(new FileInputStream(new File(file)));
		} catch (FileNotFoundException e) {
			throw new OdenException(e);
		}
	}

	protected File getFile(String file) {
		return new File(file);
	}

	public String getUsage() {
		return getProtocol() + "<path>";
	}

	/**
	 * @param args absolute path of this filesystem
	 * @param file file path from the repository's root(not including root).
	 */
	public FatInputStream resolve(String[] args, String file) throws OdenException {
		if(args.length < 1)
			throw new OdenIncompleteArgumentException(args);
		
		FatInputStream in = null;
		try {
			File f = new File(stripProtocol(args[0]), file);			
			in = new FatInputStream(new FileInputStream(f), 
					file, f.isDirectory(), f.lastModified(), f.length());
			return in;
		} catch (Exception e) {
			throw new OdenException(e);
		} 
	}
	
	public FileInfo resolveAsFileInfo(String[] args, String file) {
		if(args.length < 1)
			return null;
		args = normalizeArgs(args);
		String repoUri = args[0];

		File f = new File(stripProtocol(repoUri), file);			
		return new FileInfo(file, f.isDirectory(), f.lastModified(), f.length());
	}
	
	public FileInfo resolveAsFileInfo(String[] args, List<String> refs, 
			String file) {
		if(args.length < 1)
			return null;
		args = normalizeArgs(args);
		
		long date = -1L;
		for(String ref : refs){
			File f = new File(ref);
			if(f.exists())
				date = f.lastModified();
		}
		
		final String bindir = args[0];
		File bin = new File(stripProtocol(bindir), file);
		if(!bin.exists())
			return null;
		
		if(date == -1L)
			date = bin.lastModified();
		return new FileInfo(file, bin.isDirectory(), 
				date, bin.length());
	}

	public List<String> resolveFileRegex(String[] args, List<String> includes, List<String> excludes) 
			throws OdenException{
		if(args.length < 1)
			throw new OdenIncompleteArgumentException(args);
		args = normalizeArgs(args);
		String repoUri = args[0];

		String root = stripProtocol(repoUri);
		
		boolean recursive = hasRecursive(includes);
		String parent = FileUtil.commonParent(includes);
		if(parent != null){
			List<String> _incs = new ArrayList<String>(includes);
			for(String inc : includes)
				_incs.add(FileUtil.getRelativePath(parent, inc));
			return getMatchedFiles(root, FileUtil.combinePath(root, parent), _incs, excludes, recursive);
		}
		return getMatchedFiles(root, root, includes, excludes, recursive);
	}

	private boolean hasRecursive(List<String> includes) {
		for(String s : includes)
			if(s.contains("**"))
				return true;
		return false;
	}

	/**
	 * 
	 * @param root root path to calculate a relative path.
	 * @param parent path including root path. thus absolute path.
	 * @param includes
	 * @param excludes
	 * @return
	 * @throws OdenException
	 */
	private List<String> getMatchedFiles(final String root, String parent, 
			List<String> includes, List<String> excludes, boolean recursive)
			throws OdenException {
		
		List<String> matched = new ArrayList<String>();
		
		File[] files = new File(parent).listFiles();
		if(files != null){
			for(File file : files) {				
				String path = new File(parent, file.getName()).getPath(); 

				if(file.isFile()){
					String rpath = FileUtil.getRelativePath(root, path);
					if(FileUtil.matched(rpath, includes, excludes))
						matched.add(rpath);
				} else if(recursive) {	// directory
					matched.addAll(getMatchedFiles(root, path, includes, excludes, recursive));
				}
			}
		}
		return matched;
	}
	
	public List<FileInfo> getFileList(String[] args) throws OdenException {
		List<FileInfo> files = new ArrayList<FileInfo>();
		if(args.length < 1)
			throw new OdenIncompleteArgumentException(args);
		args = normalizeArgs(args);
		String path = stripProtocol(args[0]);
		
		File[] children = new File(path).listFiles();
		if(children == null)
			throw new OdenException("Invalid location: " + path);
		for(File f : children){
			FileInfo mf = new FileInfo(f.getPath(), 
					f.isDirectory(), f.lastModified());
			files.add(mf);
		}
		return files;
	}
	
	public boolean isDirExisted(String[] args, String name) {
		File path = new File(FileUtil.combinePath(normalizedPath(args), name));
		return path.exists() && path.isDirectory();
	}
	
	public List<FileInfo> listAllFiles(String[] args) throws OdenException {
		if(args.length < 1)
			throw new OdenIncompleteArgumentException(args);
		args = normalizeArgs(args);
		String path = stripProtocol(args[0]);
		
		List<FileInfo> ret = new ArrayList<FileInfo>();
		FileUtil.listAllFiles(ret, path, new File(path));
		return ret;
	}
	
	public List<String> listAllFiles(String[] args, String subdir, 
			List<String> excludes) throws OdenIncompleteArgumentException{
		if(args.length < 1)
			throw new OdenIncompleteArgumentException(args);
		args = normalizeArgs(args);
		final String root = stripProtocol(args[0]);
		final String rootAndSub = FileUtil.combinePath(root, subdir);
		return listAllFiles(rootAndSub, root, rootAndSub, excludes);
	}
	
	private List<String> listAllFiles(String dir, String root, String rootAndSub, 
			List<String> excludes){
		List<String> ret = new ArrayList<String>();
		File[] files = new File(dir).listFiles();
		if(files == null)
			return ret;
		
		for(File file : files) {
			String path = FileUtil.combinePath(dir, file.getName());
			if(file.isFile()){
				String pathAfterSub = FileUtil.getRelativePath(rootAndSub, path);
				if(FileUtil.matched(pathAfterSub, excludes))
					continue;
				ret.add(pathAfterSub);
			} else {	// directory
				ret.addAll(listAllFiles(path, root, rootAndSub, excludes));
			}
		}
		return ret;
	}
	
	public void listAllFilesFileInfo(String[] args, String subdir,
			List<Pair> dirSrcMap, 
			List<String> excludes, Collection<FileInfo> ret) 
			throws OdenIncompleteArgumentException{
		if(args.length < 1)
			throw new OdenIncompleteArgumentException(args);
		
		final String root = normalizedPath(args);
		final String rootAndSub = FileUtil.combinePath(root, subdir);
		listAllFilesFileInfo(rootAndSub, rootAndSub, dirSrcMap, excludes, ret);
	}
	
	private void listAllFilesFileInfo(String dir, String root,
			List<Pair> dirSrcMap, 
			List<String> excludes, Collection<FileInfo> ret){
		File[] files = new File(dir).listFiles();
		if(files == null) return;
		
		for(File file : files) {
			String path = FileUtil.combinePath(dir, file.getName());
			if(file.isFile()){
				String pathAfterSub = FileUtil.getRelativePath(root, path);
				if(FileUtil.matched(pathAfterSub, excludes))
					continue;
				long date = getSrcDate(pathAfterSub, dirSrcMap);
				ret.add(new FileInfo(pathAfterSub, false, 
						date == 0L ? file.lastModified() : date, 
						file.length()));
			} else {	// directory
				listAllFilesFileInfo(path, root, dirSrcMap, excludes, ret);
			}
		}
	}
	
	private long getSrcDate(String path, List<Pair> dirSrcMap){
		for(Pair p : dirSrcMap){
			String dir = p.getArg0();
			if(path.startsWith(dir)){
				long date = getDate(p.getArg1(), 
						path.substring(dir.length()));
				if(date != 0L) 
					return date;
			}
		}
		return 0L;
	}
	
	private long getDate(String parent, String child) {
		if(child.endsWith(".class"))
			return new File(parent, getSrcFileName(child)).lastModified();
		return new File(parent, child).lastModified();
	}

	private String getSrcFileName(String classFile) {
		int i = classFile.indexOf('$');
		if(i > 0)
			return classFile.substring(0, i) + ".java";
		return classFile.substring(0, 
				classFile.length() - ".class".length()) + ".java"; 
	}
	
	public void close(String[] args) {
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
		repoargs = normalizeArgs(repoargs);
		destpath = toAbsolutePath(destpath);
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
		return System.currentTimeMillis();
	}
	
	protected String[] normalizeArgs(String[] args) {
		String protocol = getProtocol();
		return new String[]{ protocol + toAbsolutePath(
				args[0].substring(protocol.length())) };
	}
	
	protected String normalizedPath(String[] args){
		if(args.length < 1) return null;
		return toAbsolutePath(args[0].substring(
				getProtocol().length()));
	}

	private String toAbsolutePath(String path) {
		if(StringUtil.empty(path) || FileUtil.isAbsolutePath(path))
			return path;
		return FileUtil.resolveDotNatationPath(
				BundleUtil.odenHome().getPath() + "/" + path);
	}
	
	/**
	 * find dir whose name is src/main/java, src/main/resources,
	 * src/test/java, src/test/resources, src
	 */
	public List<String> getSourceDirs(String[] repoArgs){
		List<String> ret = new ArrayList<String>();
		String root = normalizedPath(repoArgs);
		File dir = new File(root);
		if(!dir.exists()) return ret;
		
		File src = findDirFile(dir, "src", null);
		if(src == null) return ret;
		for(File f : src.listFiles()){
			if(f.isFile()) continue;
			if(f.getName().equals("main") ||
					f.getName().equals("test")){
				for(File ff: f.listFiles()){
					if(ff.isFile()) continue;
					if(ff.getName().equals("java") || 
							ff.getName().equals("resources")){
						// src/main/java or src/main/resources
						ret.add(FileUtil.getRelativePath(
								root, ff.getAbsolutePath()));	
					}
				}
				if(ret.isEmpty()) 
					ret.add(FileUtil.getRelativePath(
							root, f.getAbsolutePath()));	// src/main
			}
			if(!ret.isEmpty()) break;
		}
		if(ret.isEmpty()) 
			ret.add(FileUtil.getRelativePath(
					root, src.getAbsolutePath()));	// src
		return ret;
	}

	public String getAbsolutePathFromParent(String[] repoArgs, String name){
		File dir = new File(normalizedPath(repoArgs));
		
		File parent = dir.getParentFile();
		for(int i=0; i<4; i++){
			if(parent == null) break;
			File f = new File(parent, name);
			if(f.exists() && f.isDirectory()){
				return f.getAbsolutePath();
			}
			parent = parent.getParentFile();
		}
		return null;
	}
	
	/**
	 * get dir whose name is dirName. if there are one more dirs,
	 * outer most dir is first. <br/>
	 * e.g. if you want to find WEB-INF, but there are some WEB-INF
	 * like a/WEB-INF, a/b/WEB-INF, a/b/c/WEB-INF, this method will
	 * return a/WEB-INF
	 */
	public String findDir(String[] repoArgs, String dirName,
			String exclude){
		String root = normalizedPath(repoArgs);
		File dir = new File(root);
		if(!dir.exists()) return null;
		
		File f = findDirFile(dir, dirName, 
				exclude == null ? null : new File(exclude));
		return f == null ? null : 
			FileUtil.getRelativePath(root, f.getAbsolutePath());
	}
	
	private File findDirFile(File dir, String dirName,
			File exclude){
		Queue<File> q = new LinkedList<File>();
		q.add(dir);
		while(!q.isEmpty()){
			File f = q.remove();
			if(f.isFile()) continue;
			if(f.getName().equals(dirName) && ( exclude == null ||
					!f.getAbsolutePath().equals(exclude.getAbsolutePath()) ) )
				return f;
			q.addAll(Arrays.asList(f.listFiles()));
		}
		return null;
	}
}
