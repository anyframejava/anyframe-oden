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
package org.anyframe.oden.bundle.build.config;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is CfgBuildJob Class
 * 
 * @author Junghwan Hong
 */
public class CfgBuildJob implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String type;
	private String workspace;
	private String scm;
	private String scmurl;
	private String schedule;
	private String otherproject;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getWorkspace() {
		return workspace;
	}
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}
	public String getScm() {
		return scm;
	}
	public void setScm(String scm) {
		this.scm = scm;
	}
	public String getScmurl() {
		return scmurl;
	}
	public void setScmurl(String scmurl) {
		this.scmurl = scmurl;
	}
	public String getSchedule() {
		return schedule;
	}
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	public String getOtherproject() {
		return otherproject;
	}
	public void setOtherproject(String otherproject) {
		this.otherproject = otherproject;
	}
	
	public JSONArray toJSON() throws JSONException {
		JSONObject o = new JSONObject();
		o.put("name", name);
		o.put("type", type);
		o.put("workspace", workspace);
		o.put("scm", scm);
		o.put("scmurl", scmurl);
		o.put("schedule", schedule);
		o.put("otherproject", otherproject);
		
		return new JSONArray(o);
	}
}
