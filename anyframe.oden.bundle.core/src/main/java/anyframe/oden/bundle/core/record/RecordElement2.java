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
package anyframe.oden.bundle.core.record;

import java.io.Serializable;
import java.util.Set;

import anyframe.oden.bundle.common.ArraySet;
import anyframe.oden.bundle.core.DeployFile;

/**
 * This represents each deploy log. policy의 agent 하나당 RecordElement하나가 생김
 * 
 * @author joon1k
 * 
 */
public class RecordElement2 implements Serializable{
	private static final long serialVersionUID = -2377657865000902181L;
	
	private String id;
	private String user = "";
	private Set<DeployFile> files = new ArraySet<DeployFile>();
	private long date;
	private boolean success = true;
	private String log = "";
	private String desc = "";
	
	public RecordElement2(String id, Set<DeployFile> files, String user, long date, String desc) {
		this.id = id;
		this.files = files;
		this.user = user;
		this.date = date;
		this.desc = desc;
		if(files.size() > 0)
			for(DeployFile f : files)
				this.success = this.success & f.isSuccess();
		else
			this.success = false;
	}
	
	public RecordElement2(String id, Set<DeployFile> files, String user, long date, boolean success, String errorLog, String desc) {
		this.id = id;
		this.files = files;
		this.user = user;
		this.date = date;
		this.success = success;
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

	public Set<DeployFile> getDeployFiles() {
		return files;
	}
	
	public void setFiles(Set<DeployFile> s){
		this.files = s;
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
	
	public void setLog(String s){
		this.log = s;
	}
}
