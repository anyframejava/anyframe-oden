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
package org.anyframe.oden.bundle.job.config;

import org.json.JSONException;
import org.json.JSONObject;

import org.anyframe.oden.bundle.common.Utils;

public class CfgCommand {
	String name;
	String command;
	String path;
	
	public CfgCommand(String name, String command, String path) {
		this.name = name;
		this.command = command;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public String getCommand() {
		return command;
	}

	public String getPath() {
		return path;
	}

	public JSONObject toJSON() throws JSONException{
		JSONObject o = new JSONObject();
		o.put("name", name);
		o.put("command", command);
		o.put("dir", path);
		return o;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CfgCommand){
			CfgCommand ct = (CfgCommand)obj;
			if(equals(name, ct.getName()) && 
					equals(command, ct.getCommand()) && 
					equals(path, ct.getPath()))
				return true;
		}
		return false;
	}
	
	private boolean equals(String s0, String s1){
		return (s0 == null && s1 == null) ||
			(s0 != null && s0.equals(s1));
	}
	
	@Override
	public int hashCode() {
		return Utils.hashCode(name, command, path);
	}
}
