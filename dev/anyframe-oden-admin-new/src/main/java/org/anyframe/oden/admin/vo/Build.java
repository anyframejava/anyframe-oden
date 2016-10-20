package org.anyframe.oden.admin.vo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class Build implements Serializable {

	private String jobName;
	private String buildNo;
	private String consoleUrl;
	private String date;
	private String success;

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getBuildNo() {
		return buildNo;
	}

	public void setBuildNo(String buildNo) {
		this.buildNo = buildNo;
	}

	public String getConsoleUrl() {
		return consoleUrl;
	}

	public void setConsoleUrl(String consoleUrl) {
		this.consoleUrl = consoleUrl;
	}

	public String getDate() {
//		return date;
		if(date != null){
			return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date(Long.valueOf(date)));
		}
		return date;
		
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

}