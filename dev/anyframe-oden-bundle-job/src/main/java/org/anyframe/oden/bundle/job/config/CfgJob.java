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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.anyframe.oden.bundle.common.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is CfgJob Class
 * 
 * @author Junghwan Hong
 */
public class CfgJob {
	String name;
	CfgSource source;
	List<CfgTarget> targets;
	List<CfgCommand> commands;

	public CfgJob(String name, CfgSource source, List<CfgTarget> targets,
			List<CfgCommand> commands) {
		this.name = name;
		this.source = source;
		this.targets = targets;
		this.commands = commands;
	}

	public String getName() {
		return name;
	}

	public List<CfgCommand> getCommands() {
		return commands;
	}

	/**
	 * get commands which are matched with commandNames.
	 * 
	 * @param commandNames
	 * @return
	 */
	public List<CfgCommand> getCommands(List<String> commandNames) {
		List<CfgCommand> cs = new ArrayList<CfgCommand>();
		Set<String> names = new HashSet(commandNames);
		for (CfgCommand c : commands) {
			if (names.contains(c.getName())) {
				cs.add(c);
			}
		}
		return cs;
	}

	public CfgSource getSource() {
		return source;
	}

	public List<CfgTarget> getTargets() {
		return targets;
	}

	/**
	 * Get targets which are matched with targetNames. If targetNames is empty,
	 * all targets will be returned.
	 * 
	 * @param targetNames
	 * @return
	 */
	@SuppressWarnings("PMD")
	public List<CfgTarget> getAllTargets(List<String> targetNames) {
		if (targetNames == null || targetNames.size() == 0) {
			return targets;
		}

		List<CfgTarget> ts = new ArrayList<CfgTarget>();
		Set<String> names = new HashSet(targetNames);
		for (CfgTarget t : targets) {
			if (names.contains(t.getName())) {
				ts.add(t);
			}
		}
		return ts;
	}

	public JSONArray toJSON() throws JSONException {
		JSONObject o = new JSONObject();
		o.put("name", name);
		o.put("source", source.toJSON());

		JSONArray ts = new JSONArray();
		for (CfgTarget t : targets) {
			ts.put(t.toJSON());
		}
		o.put("targets", ts);

		JSONArray cs = new JSONArray();
		for (CfgCommand c : commands) {
			cs.put(c.toJSON());
		}
		o.put("commands", cs);
		return new JSONArray(o);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CfgJob) {
			CfgJob cg = (CfgJob) obj;
			if (equals(name, cg.getName()) && source.equals(cg.getSource())
					&& targets.equals(cg.getTargets())
					&& commands.equals(cg.getCommands())) {
				return true;
			}
		}
		return false;
	}

	private boolean equals(String s0, String s1) {
		return (s0 == null && s1 == null) || (s0 != null && s0.equals(s1));
	}

	@Override
	public int hashCode() {
		return Utils.hashCode(name, source, targets, commands);
	}
}
