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
package org.anyframe.oden.bundle.external.deploy;

import java.util.ArrayList;
import java.util.List;

import org.anyframe.oden.bundle.common.Pair;
import org.anyframe.oden.bundle.core.repository.RepositoryService;
import org.anyframe.oden.bundle.external.RepoManager;
import org.anyframe.oden.bundle.external.config.CfgFileInfo;
import org.anyframe.oden.bundle.external.config.CfgUtil;

/**
 * This is SourceManager Class
 * 
 * @author Junghwan Hong
 */
public class SourceManager extends RepoManager {
	CfgFileInfo fileInfo;
	List<Pair> dirSrcMap = new ArrayList<Pair>();

	public SourceManager(RepositoryService repo, String srcPath) {
		super(repo, CfgUtil.toRepoArg(srcPath));
		this.fileInfo = fileInfo;
	}
//	public List<RepoFile> getCandidates() throws OdenException {
//	List<RepoFile> ret = new ArrayList<RepoFile>();
//	for(String s : repo.listAllFiles(repoArgs, null, src.getExcludes())){ 
//		ret.add(new RepoFile(s));
//	}
//	return ret;
//}
//
//public void getCandidatesFileInfo(Collection<FileInfo> ret) 
//		throws OdenException {
//	repo.listAllFilesFileInfo(repoArgs, null, dirSrcMap, 
//			src.getExcludes(), ret);
//}
//
//public FileInfo getFileInfo(RepoFile rf) {
//	List<String> mappings = new ArrayList<String>();
//	for(CfgMapping mapping : src.getMappings()){
//		if(mapping.getDir().equals("*") || 
//				rf.getFile().startsWith(mapping.getDir())){
//			if(!StringUtil.empty(mapping.getCheckoutDir())){
//				String converted = rf.getFile().endsWith(".class") ?
//						getSrcFileName(rf.getFile()) : rf.getFile();
//				String child = converted.substring(
//						mapping.getDir().length());
//				String mappingFile= FileUtil.isAbsolutePath(mapping.getCheckoutDir()) ?
//						FileUtil.combinePath(mapping.getCheckoutDir(), child) :
//						FileUtil.resolveDotNatationPath(
//								FileUtil.combinePath(src.getPath(), mapping.getCheckoutDir()))
//								+ child;	
//				mappings.add(mappingFile);
//			}
//		}
//	}
//	return repo.resolveAsFileInfo(
//			makeToRepoArgs(rf.getSubdir()), mappings, rf.getFile());
//}
//
//private String getSrcFileName(String classFile) {
//	int i = classFile.indexOf('$');
//	if(i > 0)
//		return classFile.substring(0, i) + ".java";
//	return classFile.substring(0, 
//			classFile.length() - ".class".length()) + ".java"; 
//}
}
