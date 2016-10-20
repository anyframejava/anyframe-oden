package anyframe.oden.bundle.job.log;

public class ShortenRecord {
	String id;
	String job;
	long date;
	String user;
	boolean isSuccess = true;
	int total;
	int nSuccess;
	String log;
	
	public ShortenRecord(){}
	
	public ShortenRecord(String id, String job, long date, String user,
			boolean isSuccess, int total, int nSuccess, String log){
		this.id = id;
		this.job = job;
		this.date = date;
		this.user = user;
		this.isSuccess = isSuccess;
		this.total = total;
		this.nSuccess = nSuccess;
		this.log = log;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getnSuccess() {
		return nSuccess;
	}

	public void setnSuccess(int nSuccess) {
		this.nSuccess = nSuccess;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}
	
}
