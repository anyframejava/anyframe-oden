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
import java.util.List;

/**
 * Domain class for server info in job.
 * 
 * @author Hong JungHwan
 * @author LEE Sujeong
 *
 */
@SuppressWarnings("serial")
public class Server implements Serializable {

//	private static final long serialVersionUID = 1L;
	private String status;
	private String file;
	private List<Fileinfo> fileinfo;
	private String jobname;
	private String jobId;
	private String date;

	public String getStatus() {
		return status;
	}

	/**
	 * 
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public String getFile() {
		return file;
	}

	/**
	 * 
	 * @param file
	 */
	public void setFile(String file) {
		this.file = file;
	}

	public List<Fileinfo> getFileinfo() {
		return fileinfo;
	}

	/**
	 * 
	 * @param file
	 */
	public void setFileinfo(List<Fileinfo> fileinfo) {
		this.fileinfo = fileinfo;
	}

	public String getJobname() {
		return jobname;
	}

	/**
	 * 
	 * @param jobname
	 */
	public void setJobname(String jobname) {
		this.jobname = jobname;
	}

	public String getJobid() {
		return jobId;
	}

	/**
	 * 
	 * @param jobid
	 */
	public void setJobid(String jobId) {
		this.jobId = jobId;
	}

	public String getDate() {
		return date;
	}

	/**
	 * 
	 * @param date
	 */
	public void setDate(String date) {
		this.date = date;
	}

}