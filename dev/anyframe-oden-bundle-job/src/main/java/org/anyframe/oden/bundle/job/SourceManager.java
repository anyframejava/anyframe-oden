package org.anyframe.oden.bundle.job;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.anyframe.oden.bundle.common.FileInfo;
import org.anyframe.oden.bundle.common.FileUtil;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.common.Pair;
import org.anyframe.oden.bundle.common.StringUtil;
import org.anyframe.oden.bundle.core.repository.RepositoryService;
import org.anyframe.oden.bundle.job.config.CfgMapping;
import org.anyframe.oden.bundle.job.config.CfgSource;
import org.anyframe.oden.bundle.job.config.CfgUtil;

public class SourceManager extends RepoManager{
	CfgSource src;
	List<Pair> dirSrcMap
			= new ArrayList<Pair>();
	
	public SourceManager(RepositoryService repo, CfgSource src){
		super(repo, CfgUtil.toRepoArg(src));
		this.src = src;
		List<CfgMapping> maps = new ArrayList<CfgMapping>(src.getMappings());
		sortMappings(maps);
		for(CfgMapping map : maps)
			dirSrcMap.add(new Pair(map.getDir().replaceAll("^\\.(/|$)", ""), 
					map.getCheckoutDir()));
	}
	
	private void sortMappings(List<CfgMapping> subs){
		Collections.sort(subs, new Comparator<CfgMapping>(){
			public int compare(CfgMapping o1, CfgMapping o2) {
				return countSlash(o2.getDir()) - countSlash(o1.getDir());
			}

			private int countSlash(String path){
				if(path.startsWith("*"))	// take to first
					return 100;		
				if(path.length() < 2)
					return 0;
				int cnt = 0;
				// ignore first & last slash
				for(int i=1; i<path.length()-1; i++){
					if(path.charAt(i) == '/' )	 
						cnt++;
				}
				return cnt;
			}
		});
	}

//	public List<String> getCandidates() throws OdenException{
//		List<String> ret = new ArrayList<String>();
//		Map<String, String> map = getCandidateMap();
//		for(String s : map.keySet()){
//			String sub = map.get(s);
//			ret.add(sub + "//" + FileUtil.getRelativePath(sub, s));
//		}
//		return ret;
//	}
	
//	public List<String> getCandidateNames() throws OdenException{
//		List<String> ret = new ArrayList<String>();
//		Map<String, String> map = getCandidateMap();
//		for(String s : map.keySet()){
//			String sub = map.get(s);
//			ret.add(FileUtil.getRelativePath(sub, s));
//		}
//		return ret;
//	}
	
//	public Set<RepoFile> getCandidates() throws OdenException {
//		Set<RepoFile> ret = new HashSet<RepoFile>();
//		
//		boolean asterisk = false;
//		List<String> asteriskExcludes = Collections.EMPTY_LIST; 
//		for(CfgRef sub : src.getSubs()){
//			if(sub.getDir().startsWith("*")){
//				asterisk = true;
//				asteriskExcludes = sub.getExcludes();
//				continue;
//			}
//			for(String s : repo.listAllFiles(repoArgs, sub.getDir(), 
//						sub.getExcludes())){
//				RepoFile rf = new RepoFile(sub.getDir(), s);
//				if(!ret.contains(rf))
//					ret.add(rf);
//			}
//		}
//		
//		// in case: <sub dir="*" />
//		if(asterisk){
//			for(String s : repo.listAllFiles(repoArgs, null, asteriskExcludes)){
//				RepoFile rf = new RepoFile("", s);
//				if(!ret.contains(rf))
//					ret.add(rf);
//			}
//		}
//		return ret;
//	}
	
	public List<RepoFile> getCandidates() throws OdenException {
		List<RepoFile> ret = new ArrayList<RepoFile>();
		for(String s : repo.listAllFiles(repoArgs, null, src.getExcludes())){ 
			ret.add(new RepoFile(s));
		}
		return ret;
	}
	
	public void getCandidatesFileInfo(Collection<FileInfo> ret) 
			throws OdenException {
		repo.listAllFilesFileInfo(repoArgs, null, dirSrcMap, 
				src.getExcludes(), ret);
	}
	
	public FileInfo getFileInfo(RepoFile rf) {
		List<String> mappings = new ArrayList<String>();
		for(CfgMapping mapping : src.getMappings()){
			if(mapping.getDir().equals("*") || 
					rf.getFile().startsWith(mapping.getDir())){
				if(!StringUtil.empty(mapping.getCheckoutDir())){
					String converted = rf.getFile().endsWith(".class") ?
							getSrcFileName(rf.getFile()) : rf.getFile();
					String child = converted.substring(
							mapping.getDir().length());
					String mappingFile= FileUtil.isAbsolutePath(mapping.getCheckoutDir()) ?
							FileUtil.combinePath(mapping.getCheckoutDir(), child) :
							FileUtil.resolveDotNatationPath(
									FileUtil.combinePath(src.getPath(), mapping.getCheckoutDir()))
									+ child;	
					mappings.add(mappingFile);
				}
			}
		}
		return repo.resolveAsFileInfo(
				makeToRepoArgs(rf.getSubdir()), mappings, rf.getFile());
	}
	
	private String getSrcFileName(String classFile) {
		int i = classFile.indexOf('$');
		if(i > 0)
			return classFile.substring(0, i) + ".java";
		return classFile.substring(0, 
				classFile.length() - ".class".length()) + ".java"; 
	}

}
