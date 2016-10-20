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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.anyframe.oden.bundle.common.StringUtil;
import org.anyframe.oden.bundle.common.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is CfgSource Class
 * 
 * @author Junghwan Hong
 */
public class CfgSource {
	String path;
	List<CfgMapping> mappings = null;
	List<String> excludes = null;

	public CfgSource(String path, String excludes, List<CfgMapping> mappings) {
		this(path, excludes == null ? Collections.EMPTY_LIST : Arrays
				.asList(excludes.split(",(\\s)*")), mappings);
	}

	public CfgSource(String path, List<String> excludes,
			List<CfgMapping> mappings) {
		this.path = path;
		this.excludes = excludes;
		this.mappings = mappings;
	}

	public List<CfgMapping> getMappings() {
		return mappings != null ? mappings : Collections.EMPTY_LIST;
	}

	public void setMappings(List<CfgMapping> mappings) {
		this.mappings = mappings;
	}

	public String getPath() {
		return path;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public List<String> getExcludes() {
		return excludes;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject o = new JSONObject();
		o.put("dir", path);
		JSONArray arr = new JSONArray();
		for (CfgMapping mapping : mappings) {
			arr.put(mapping.toJSON());
		}
		o.put("mappings", arr);
		o.put("excludes", new JSONArray(excludes));
		return o;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CfgSource) {
			CfgSource ct = (CfgSource) obj;
			if (StringUtil.equals(path, ct.getPath())
					&& mappings.equals(ct.getMappings())
					&& excludes.equals(ct.getExcludes())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Utils.hashCode(path, mappings, excludes);
	}
}
