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
package org.anyframe.oden.bundle.external.config;

import java.io.Serializable;

import org.anyframe.oden.bundle.common.Utils;

public class CfgReturnStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String id;

	int status;

	int progress;

	String currentWork;

	int totalWorks;

	long date;

	String desc;

	public CfgReturnStatus(String id, int status, int progress,
			String currentWork, int totalWorks, long date, String desc) {
		this.id = id;
		this.status = status;
		this.progress = progress;
		this.currentWork = currentWork;
		this.totalWorks = totalWorks;
		this.date = date;
		this.desc = desc;
	}

	public String getId() {
		return id;
	}

	public int getStatus() {
		return status;
	}

	public int getProgress() {
		return progress;
	}

	public String getCurrentWork() {
		return currentWork;
	}

	public int getTotalWorks() {
		return totalWorks;
	}

	public long getDate() {
		return date;
	}

	public String getDesc() {
		return desc;
	}

	@Override
	public int hashCode() {
		return Utils.hashCode(id, status, progress, currentWork, totalWorks,
				date, desc);
	}
}
