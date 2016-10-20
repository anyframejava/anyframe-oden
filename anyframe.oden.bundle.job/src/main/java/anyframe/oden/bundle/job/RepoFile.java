package anyframe.oden.bundle.job;

import anyframe.oden.bundle.common.Utils;

public class RepoFile {
	String subdir;
	String file;
	public RepoFile(String subdir, String file){
		this.subdir = subdir;
		this.file = file;
	}
	public RepoFile(String arg){
		int i = arg.indexOf("//");
		if(i == -1){
			subdir = "";
			file = arg;
		}else {
			subdir = arg.substring(0, i);
			file = arg.substring(i+2);
		}
	}
	public String getSubdir(){
		return subdir;
	}
	public String getFile(){
		return file;
	}
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof RepoFile))
			return false;
		RepoFile f = (RepoFile)o;
		return subdir.equals(f.getSubdir()) && file.equals(f.getFile());
	}
	@Override
	public int hashCode() {
		return Utils.hashCode(subdir, file);
	}
}
