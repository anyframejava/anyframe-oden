package org.anyframe.oden.bundle.job;

import java.util.List;

import org.anyframe.oden.bundle.common.FatInputStream;
import org.anyframe.oden.bundle.common.FileInfo;
import org.anyframe.oden.bundle.common.FileUtil;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.common.StringUtil;
import org.anyframe.oden.bundle.core.Repository;
import org.anyframe.oden.bundle.core.repository.RepositoryService;

public class RepoManager {
	protected RepositoryService repo;
	protected String[] repoArgs;

	public RepoManager(RepositoryService repo, String[] repoArgs){
		this.repo = repo;
		this.repoArgs = repoArgs;
	}
	
	public void close() {
		repo.close(repoArgs);
	}
	
	public FileInfo resolveAsFileInfo(RepoFile rf) {
		return repo.resolveAsFileInfo(makeToRepoArgs(rf.getSubdir()), 
				rf.getFile());
	}
	
	public FileInfo resolveAsFileInfo(String s) {
		return repo.resolveAsFileInfo(repoArgs, s);
	}
	
	public Repository getRepository(){
		return new Repository(repoArgs);
	}
	
	public Repository getRepository(RepoFile rf){
		return new Repository(new String[]{rf.getSubdir()});
	}
	
	public FatInputStream resolve(RepoFile rf) throws OdenException{
		return repo.resolve(makeToRepoArgs(rf.getSubdir()), rf.getFile());
	}
	
	protected String[] makeToRepoArgs(String subdir){
		if(StringUtil.empty(subdir))
			return repoArgs;
		return new String[]{FileUtil.combinePath(repoArgs[0], subdir)};	
	}

	public String findDir(String dirName, String exclude) {
		return repo.findDir(repoArgs, dirName, exclude);
	}

	public List<String> getSourceDirs() {
		return repo.getSourceDirs(repoArgs);
	}
	
	public boolean isDirExisted(String dirName){
		return repo.isDirExisted(repoArgs, dirName);
	}
	
	public String getAbolutePathFromParent(String dir){
		return repo.getAbsolutePathFromParent(repoArgs, dir);
	}
}
