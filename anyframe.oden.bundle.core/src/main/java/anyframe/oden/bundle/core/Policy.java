package anyframe.oden.bundle.core;

public class Policy {
	private String[] repoargs;
	
	private FileMap files;
	
	private boolean update;
	
	private String user;

	public Policy(String[] repoargs, FileMap files, boolean update, String user){
		this.repoargs = repoargs;
		this.files = files;
		this.update = update;
		this.user = user;
	}
	
	public String[] getRepoargs() {
		return repoargs;
	}

	public void setRepoargs(String[] repoargs) {
		this.repoargs = repoargs;
	}

	public FileMap getFiles() {
		return files;
	}

	public void setFiles(FileMap files) {
		this.files = files;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
}
