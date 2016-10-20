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
 * Domain class for status info.
 * 
 * @author Junghwan Hong
 * @author Sujeong Lee
 */
@SuppressWarnings("serial")
public class Status implements Serializable {

	private String jobname;
	private String id;
	private String date;
	private String jobStatus;
	private String desc;
	private String progress;
	private String totalWorks;

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

	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
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

	public String getStatus() {
		return jobStatus;
	}

	/**
	 * 
	 * @param status
	 */
	public void setStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	public String getDesc() {
		return desc;
	}

	/**
	 * 
	 * @param desc
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getProgress() {
		return progress;
	}

	/**
	 * 
	 * @param progress
	 */
	public void setProgress(String progress) {
		this.progress = progress;
	}

	public String getTotalWorks() {
		return totalWorks;
	}

	/**
	 * @param totalWorks
	 */
	public void setTotalWorks(String totalWorks) {
		this.totalWorks = totalWorks;
	}

}