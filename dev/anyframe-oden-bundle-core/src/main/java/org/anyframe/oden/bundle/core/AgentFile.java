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
package org.anyframe.oden.bundle.core;

/**
 * This class store's deployed file information in all agents.
 * 
 * @author Junghwan Hong
 */
public class AgentFile {
	private String agent;

	private String path;

	private long size;

	private long date;

	public AgentFile(String agent, String path) {
		this.agent = agent;
		this.path = path;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String agent() {
		return agent;
	}

	public String path() {
		return path;
	}

	public long size() {
		return size;
	}

	public long date() {
		return date;
	}
}
