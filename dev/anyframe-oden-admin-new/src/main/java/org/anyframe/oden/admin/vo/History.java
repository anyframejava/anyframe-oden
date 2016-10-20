package org.anyframe.oden.admin.vo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class History implements Serializable {

	// -----------------------------------------//
	// Oden Server History Json Mapping info
	private String txid;
	private String status;
	private String total;
	private String nsuccess;
	private String job;
	private String date;
	private String user;

	// -----------------------------------------//
	// additional info
	
	public String getTxid() {
		return txid;
	}

	public void setTxid(String txid) {
		this.txid = txid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getNsuccess() {
		return nsuccess;
	}

	public void setNsuccess(String nsuccess) {
		this.nsuccess = nsuccess;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
