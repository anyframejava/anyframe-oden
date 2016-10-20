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
package org.anyframe.oden.bundle.core.record;

import java.io.Serializable;

/**
 * This represents each deploy log. policy의 agent 하나당 RecordElement하나가 생김
 * 
 * @author Junghwan Hong
 */
public class MiniRecordElement implements Serializable {
	private String id;
	private String user = "";
	private int nDeploys;
	private long date;
	private boolean success = true;
	private String log = "";
	private String desc = "";

	public MiniRecordElement(String id, int nDeploys, String user, long date,
			boolean success, String errorLog, String desc) {
		this.id = id;
		this.nDeploys = nDeploys;
		this.user = user;
		this.date = date;
		this.success = success;
		this.log = errorLog;
		this.desc = desc;
	}

	public String desc() {
		return desc;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getNDeploys() {
		return nDeploys;
	}

	public void setFiles(int nDeploys) {
		this.nDeploys = nDeploys;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSucccess(boolean success) {
		this.success = success;
	}

	public String id() {
		return id;
	}

	public String log() {
		return log;
	}

	public void setLog(String s) {
		this.log = s;
	}

}
