package org.anyframe.oden.admin.vo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CompareTarget implements Serializable {

	// -----------------------------------------//
	// Oden Server Compare Target Json Mapping info
	private String name;
	private String date;
	private String size;

	// -----------------------------------------//
	// additional info
	// for History detail
	private String status;
	private String errorLog;
	private String mode;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorLog() {
		return errorLog;
	}

	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}
