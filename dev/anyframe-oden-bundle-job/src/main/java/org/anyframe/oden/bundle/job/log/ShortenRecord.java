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
package org.anyframe.oden.bundle.job.log;

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
