/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.anyframe.oden.admin.domain;

import java.io.Serializable;

/**
 * Domain class for log info.
 * 
 * @author Hong JungHwan
 * @author LEE Sujeong
 * 
 */
@SuppressWarnings("serial")
public class Log implements Serializable {

	/**
	 * 
	 */
	private String filename;
	private String contents;
	private String total;
	private String txid;
	private String nsuccess;
	private String status;
	private String job;
	private String date;
	private String counts;
	private String success;
	private String path;
	private String errorlog;
	private String mode;
	private String no;
	private String user;

	public String getFilename() {
		return filename;
	}

	/**
	 * 
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getContents() {
		return contents;
	}

	/**
	 * 
	 * @param contents
	 */
	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getTotal() {
		return total;
	}

	/**
	 * @param total
	 */
	public void setTotal(String total) {
		this.total = total;
	}

	public String getTxid() {
		return txid;
	}

	/**
	 * @param id
	 */
	public void setTxid(String txid) {
		this.txid = txid;
	}

	public String getNsuccess() {
		return nsuccess;
	}

	/**
	 * @param nsuccess
	 */
	public void setNsuccess(String nsuccess) {
		this.nsuccess = nsuccess;
	}

	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public String getJob() {
		return job;
	}

	/**
	 * @param job
	 */
	public void setJob(String job) {
		this.job = job;
	}

	public String getDate() {
		return date;
	}

	/**
	 * @param date
	 */
	public void setDate(String date) {
		this.date = date;
	}

	public String getCounts() {
		return counts;
	}

	/**
	 * @param counts
	 */
	public void setCounts(String counts) {
		this.counts = counts;
	}

	public String getSuccess() {
		return success;
	}

	/**
	 * @param success
	 */
	public void setSuccess(String success) {
		this.success = success;
	}

	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	public String getErrorlog() {
		return errorlog;
	}

	/**
	 * @param errorlog
	 */
	public void setErrorlog(String errorlog) {
		this.errorlog = errorlog;
	}

	public String getMode() {
		return mode;
	}

	/**
	 * @param mode
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getNo() {
		return no;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}
}