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
package anyframe.oden.bundle.repository;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import anyframe.oden.bundle.common.FatInputStream;
import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.FileUtil;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.OdenIncompleteArgumentException;

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
		String repoUri = args[0];

		FatInputStream in = null;
		try {
			File f = new File(stripProtocol(repoUri), file);			
			in = new FatInputStream(new FileInputStream(f), 
					file, f.isDirectory(), f.lastModified(), f.length());
			return in;
		} catch (Exception e) {
			throw new OdenException(e);
		} 
	}

	public List<String> resolveFileRegex(String[] args, List<String> includes, List<String> excludes) 
			throws OdenException{
		if(args.length < 1)
			throw new OdenIncompleteArgumentException(args);
		String repoUri = args[0];

		String root = stripProtocol(repoUri);
		
		String parent = FileUtil.commonParent(includes);
		if(parent != null){
			List<String> _incs = new ArrayList<String>(includes);
			for(String inc : includes)
				_incs.add(FileUtil.getRelativePath(parent, inc));
			return getMatchedFiles(root, FileUtil.combinePath(root, parent), _incs, excludes);
		}
		return getMatchedFiles(root, root, includes, excludes);
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
			List<String> includes, List<String> excludes)
			throws OdenException {
		
		List<String> matched = new ArrayList<String>();
		
		File[] files = new File(parent).listFiles();
		if(files != null){
			for(File file : files) {				
				String path = new File(parent, file.getName()).getPath(); 
				if(file == null || file.isHidden() || !file.canRead() || !file.canWrite()) 
					continue;

				String rpath = FileUtil.getRelativePath(root, path);				
				if(file.isDirectory())
					matched.addAll(getMatchedFiles(root, path, includes, excludes));
				else
					if(FileUtil.matched(rpath, includes, excludes))
						matched.add(rpath);				
			}
		}
		return matched;
	}
	
//	private List<String> getMatchedFiles(final String root, String parent, 
//			List<String> includes, List<String> excludes)
//			throws OdenException {
//		
//		List<String> matched = new ArrayList<String>();
//		
//		File[] files = new File(parent).listFiles();
//		if(files != null){
//			for(File file : files) {				
//				String path = new File(parent, file.getName()).getPath(); 
//				if(file == null || file.isHidden() || !file.canRead() || !file.canWrite()) 
//					continue;
//
//				String rpath = FileUtil.getRelativePath(root, path);				
//				if(file.isDirectory())
//					if(FileUtil.matched(rpath, includes, excludes)){	// TODO * 만일때는 체크하지 말아야하는듯? 
//						List<String> _i = new ArrayList<String>(); _i.add("**");
//						matched.addAll(getMatchedFiles(root, path, _i, new ArrayList()) );
//					}else
//						matched.addAll(getMatchedFiles(root, path, includes, excludes));
//				else
//					if(FileUtil.matched(rpath, includes, excludes))
//						matched.add(rpath);				
//			}
//		}
//		return matched;
//	}

	public List<FileInfo> getFileList(String[] args) throws OdenException {
		List<FileInfo> files = new ArrayList<FileInfo>();
		if(args.length < 1)
			throw new OdenIncompleteArgumentException(args);
		String path = stripProtocol(args[0]);
		
		File[] children = new File(path).listFiles();
		if(children == null)
			throw new OdenException("Invalid location: " + path);
		for(File f : children){
			if(f == null || f.isHidden() || !f.canRead() || !f.canWrite()) 
				continue;
			FileInfo mf = new FileInfo(f.getPath(), 
					f.isDirectory(), f.lastModified());
			files.add(mf);
		}
		return files;
	}
	
	public void close(String[] args) {
	}

}
