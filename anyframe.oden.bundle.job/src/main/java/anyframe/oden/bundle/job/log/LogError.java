package anyframe.oden.bundle.job.log;

public class LogError {
	private String date;
	private String contents;

	public LogError() {
	}

	public LogError(String date, String contents) {
		this.date = date;
		this.contents = contents;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

}
