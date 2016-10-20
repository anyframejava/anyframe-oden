/*
 * Copyright 2002-2014 the original author or authors.
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
package org.anyframe.oden.bundle.build.config;

import java.io.Serializable;

/**
 * This represents each build log.
 * 
 * @author Junghwan Hong
 */
public class BrecordElement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private String jobName;

	private long date;

	private String buildNo;

	private boolean success = true;

	@SuppressWarnings("PMD")
	
	public BrecordElement() {
	}
	
	public BrecordElement(String jobName) {
		this.jobName = jobName;
	}
	
	public BrecordElement(String id, String jobName, long date, String buildNo,
			boolean success) {
		this.id = id;
		this.jobName = jobName;
		this.date = date;
		this.buildNo = buildNo;
		this.success = success;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getBuildNo() {
		return buildNo;
	}

	public void setBuildNo(String buildNo) {
		this.buildNo = buildNo;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
