package org.anyframe.oden.bundle.external.deploy;

import java.util.ArrayList;
import java.util.List;

import org.anyframe.oden.bundle.common.Pair;
import org.anyframe.oden.bundle.core.repository.RepositoryService;
import org.anyframe.oden.bundle.external.RepoManager;
import org.anyframe.oden.bundle.external.config.CfgFileInfo;
import org.anyframe.oden.bundle.external.config.CfgUtil;


public class SourceManager extends RepoManager{
	CfgFileInfo fileInfo;
	List<Pair> dirSrcMap
			= new ArrayList<Pair>();
	
	public SourceManager(RepositoryService repo, String srcPath){
		super(repo, CfgUtil.toRepoArg(srcPath));
		this.fileInfo = fileInfo;
	}
	
		
//	public List<RepoFile> getCandidates() throws OdenException {
//		List<RepoFile> ret = new ArrayList<RepoFile>();
//		for(String s : repo.listAllFiles(repoArgs, null, src.getExcludes())){ 
//			ret.add(new RepoFile(s));
//		}
//		return ret;
//	}
//	
//	public void getCandidatesFileInfo(Collection<FileInfo> ret) 
//			throws OdenException {
//		repo.listAllFilesFileInfo(repoArgs, null, dirSrcMap, 
//				src.getExcludes(), ret);
//	}
//	
//	public FileInfo getFileInfo(RepoFile rf) {
//		List<String> mappings = new ArrayList<String>();
//		for(CfgMapping mapping : src.getMappings()){
//			if(mapping.getDir().equals("*") || 
//					rf.getFile().startsWith(mapping.getDir())){
//				if(!StringUtil.empty(mapping.getCheckoutDir())){
//					String converted = rf.getFile().endsWith(".class") ?
//							getSrcFileName(rf.getFile()) : rf.getFile();
//					String child = converted.substring(
//							mapping.getDir().length());
//					String mappingFile= FileUtil.isAbsolutePath(mapping.getCheckoutDir()) ?
//							FileUtil.combinePath(mapping.getCheckoutDir(), child) :
//							FileUtil.resolveDotNatationPath(
//									FileUtil.combinePath(src.getPath(), mapping.getCheckoutDir()))
//									+ child;	
//					mappings.add(mappingFile);
//				}
//			}
//		}
//		return repo.resolveAsFileInfo(
//				makeToRepoArgs(rf.getSubdir()), mappings, rf.getFile());
//	}
//	
//	private String getSrcFileName(String classFile) {
//		int i = classFile.indexOf('$');
//		if(i > 0)
//			return classFile.substring(0, i) + ".java";
//		return classFile.substring(0, 
//				classFile.length() - ".class".length()) + ".java"; 
//	}

}
