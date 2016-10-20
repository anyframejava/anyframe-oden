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
package org.anyframe.oden.bundle.job.page;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * This is JSONArrayFilter Class
 * 
 * @author Junghwan Hong
 */
@SuppressWarnings("PMD")
public class JSONArrayFilter {
	public JSONArrayFilter() {
	}

	public JSONArray run(JSONArray in, int page, int pgscale)
			throws JSONException {
		int start = pgscale * page;
		int end = start + pgscale;

		JSONArray ret = new JSONArray();
		for (int i = start; i < in.length() && i < end; i++) {
			ret.put(in.getJSONObject(i));
		}
		return ret;
	}
}
