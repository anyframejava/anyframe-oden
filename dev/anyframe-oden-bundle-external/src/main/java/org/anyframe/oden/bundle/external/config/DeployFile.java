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
import org.anyframe.oden.bundle.core.AgentLoc;
import org.anyframe.oden.bundle.core.Repository;

/**
 * This class store's file information which will be deployed.
 * 
 * @author Junghwan Hong
 */
public class DeployFile implements Serializable {
	private static final long serialVersionUID = 6130062622167186765L;

	public enum Mode {
		ADD, UPDATE, DELETE, NA
	};

	private Repository repo;

	private String path;

	private AgentLoc agent;

	private long size;

	private long date;

	private String comment;

	private String errorLog;

	private Mode mode = Mode.NA;

	private boolean success = false;

	public DeployFile(Repository repo, String path, AgentLoc agent, long size,
			long date, Mode mode) {
		this(repo, path, agent, size, date, mode, false);
	}

	public DeployFile(Repository repo, String path, AgentLoc agent, long size,
			long date, String comment, Mode mode) {
		this(repo, path, agent, size, date, mode, false);
		this.comment = comment;
	}

	public DeployFile(Repository repo, String path, AgentLoc agent, long size,
			long date, Mode mode, boolean success) {
		this.repo = repo;
		this.path = path;
		this.agent = agent == null ? new AgentLoc("", "", "") : agent;
		this.size = size;
		this.date = date;
		this.mode = mode;
		this.success = success;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public AgentLoc getAgent() {
		return agent;
	}

	public void setAgent(AgentLoc agent) {
		this.agent = agent == null ? new AgentLoc("", "", "") : agent;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public Mode mode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
		if (success)
			this.errorLog = "";
	}

	public Repository getRepo() {
		return repo;
	}

	public void setRepo(Repository repo) {
		this.repo = repo;
	}

	public String comment() {
		return comment;
	}

	public void setComment(String s) {
		this.comment = s;
	}

	public String errorLog() {
		return errorLog;
	}

	public void setErrorLog(String s) {
		if (s == null)
			return;

		this.errorLog = s.trim();
		this.success = false;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DeployFile))
			return false;
		DeployFile d = (DeployFile) o;
		return repo.equals(d.repo) && path.equals(d.path)
				&& agent.agentName().equals(d.agent.agentName());
	}

	@Override
	public int hashCode() {
		return Utils.hashCode(repo, path, agent.agentName());
	}

}
