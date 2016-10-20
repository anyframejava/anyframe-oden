/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package anyframe.oden.bundle.core;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import anyframe.oden.bundle.common.JSONizable;




/**
 * This class store's file information which will be deployed.
 * 
 * @author joon1k
 *
 */
public class DeployFile implements JSONizable, Serializable{
	private static final long serialVersionUID = 6130062622167186765L;

	public enum Mode {ADD, UPDATE, DELETE, NA};
	
	private Repository repo;
	
	private String path;

	private AgentLoc agent;
	
	private String backupLocation;
	
	private long size;
	
	private long date;
	
	private String comment;
	
	private Mode mode = Mode.NA;
	
	private boolean success = false;

	public DeployFile(Repository repo, String path, AgentLoc agent, long size, long date, Mode mode){
		this(repo, path, agent, null, size, date, mode, false);
	}
	
	public DeployFile(Repository repo, String path, AgentLoc agent, String bak, long size, long date, Mode mode){
		this(repo, path, agent, bak, size, date, mode, false);
	}
	
	public DeployFile(Repository repo, String path, AgentLoc agent, String bak, long size, long date, String comment, Mode mode){
		this(repo, path, agent, bak, size, date, mode, false);
		this.comment = comment;
	}
	
	public DeployFile(Repository repo, String path, AgentLoc agent, String bak, long size, 
			long date, Mode mode, boolean success){
		this.repo = repo;
		this.path = path;
		this.agent = agent == null ? new AgentLoc("", "", "") : agent;
		this.backupLocation = bak;
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

	public String backupLocation(){
		return backupLocation;
	}
	
	public void setBackupLocation(String s){
		this.backupLocation = s;
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
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof DeployFile))
			return false;
		DeployFile d = (DeployFile)o;
		return repo.equals(d.repo) && path.equals(d.path) && agent.equals(d.agent); 
	}

	public Object jsonize() {
		try {
			return new JSONObject()
					.put("repo", repo.jsonize())
					.put("path", path)
					.put("agent", agent.jsonize())
					.put("size", String.valueOf(size))
					.put("date", String.valueOf(date))
					.put("comment", comment)
					.put("mode", DeployFileUtil.modeToString(mode))
					.put("success", String.valueOf(success));
		} catch (JSONException e) {
		}
		return new JSONObject();
	}

}
