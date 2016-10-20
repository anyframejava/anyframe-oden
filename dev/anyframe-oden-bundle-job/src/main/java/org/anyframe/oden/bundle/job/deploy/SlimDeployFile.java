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
package org.anyframe.oden.bundle.job.deploy;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import org.anyframe.oden.bundle.common.StringUtil;
import org.anyframe.oden.bundle.common.Utils;
import org.anyframe.oden.bundle.core.DeployFileUtil;
import org.anyframe.oden.bundle.core.DeployFile.Mode;
import org.anyframe.oden.bundle.core.command.JSONizable;

public class SlimDeployFile implements JSONizable, Serializable{
	private static final long serialVersionUID = 1L;

	
	private String path;
	private String target;
	private Mode mode = Mode.NA;
	private String error;
	private boolean success = false;

	
	public SlimDeployFile(String path, String target){
		this(path, target, Mode.NA, null, false);
	}
	public SlimDeployFile(String path, String target, Mode mode){
		this(path, target, mode, null, false);
	}
	public SlimDeployFile(String path, String target, Mode mode, 
			String error, boolean success){
		this.path = path;
		this.target = target;
		this.mode = mode;
		this.error = error;
		this.success = success;
	}
	
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public Mode getMode() {
		return mode;
	}
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}

	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof SlimDeployFile))
			return false;
		SlimDeployFile d = (SlimDeployFile)o;
		return path.equals(d.path) 
				&& target.equals(target); 
	}
	
	@Override
	public int hashCode() {
		return Utils.hashCode(path, target);
	}

	public Object jsonize() {
		try {
			return new JSONObject()
					.put("path", path)
					.put("agent", target)
					.put("mode", DeployFileUtil.modeToString(mode))
					.put("success", String.valueOf(success))
					.put("errorlog", StringUtil.makeEmpty(error));
		} catch (JSONException e) {
		}
		return new JSONObject();
	}
}
