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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is CfgRunJob Class
 * 
 * @author Junghwan Hong
 */
public class CfgRunJob implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	private String buildNo;
	
	private String consoleUrl;
	
	private String status;
	
	private String timeStamp;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBuildNo() {
		return buildNo;
	}

	public void setBuildNo(String buildNo) {
		this.buildNo = buildNo;
	}

	public String getConsoleUrl() {
		return consoleUrl;
	}

	public void setConsoleUrl(String consoleUrl) {
		this.consoleUrl = consoleUrl;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public JSONArray toJSON() throws JSONException {
		JSONObject o = new JSONObject();
		o.put("name", name);
		o.put("buildNo", buildNo);
		o.put("consoleUrl", consoleUrl);
		o.put("status", status);
		o.put("timeStamp", timeStamp);
		
		return new JSONArray(o);
	}
}
