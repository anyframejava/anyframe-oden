package org.anyframe.oden.bundle.external;

import org.anyframe.oden.bundle.common.Utils;
import org.anyframe.oden.bundle.job.RepoFile;

public class ExtRepoFile extends RepoFile{
	//String subdir;
	String file;
	public ExtRepoFile(String file){
		super(file);
		this.file = file;
	}
	
//	public String getSubdir(){
//		return subdir;
//	}
	public String getFile(){
		return file;
	}
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ExtRepoFile))
			return false;
		ExtRepoFile f = (ExtRepoFile)o;
		return file.equals(f.getFile());
	}
	@Override
	public int hashCode() {
		return Utils.hashCode(file);
	}
}
