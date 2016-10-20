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
package org.anyframe.oden.admin.domain;

/**
 * Domain class for Build History info.
 * 
 * @author Junghwan Hong
 * 
 */
public class BuildHistory {
	private long date;
	
	private String consoleUrl;
	
	private boolean success;
	
	private String jobName;
	
	private String buildNo;
	
	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getConsoleUrl() {
		return consoleUrl;
	}

	public void setConsoleUrl(String consoleUrl) {
		this.consoleUrl = consoleUrl;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

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

	@Override
	public String toString() {
		return "BuildHistory [date=" + date + ", consoleUrl=" + consoleUrl + ", success=" + success + ", jobName=" + jobName
				+ ", buildNo=" + buildNo + "]";
	}
	
	
}
