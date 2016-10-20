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
package org.anyframe.oden.bundle.build;

import java.io.PrintStream;
import java.util.StringTokenizer;

import org.anyframe.oden.bundle.build.config.BrecordElement;
import org.anyframe.oden.bundle.build.log.BuildLogService;
import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.core.command.JSONUtil;
import org.json.JSONObject;
import org.ungoverned.osgi.service.shell.Command;

/**
 * This is InternalBuildCommandImpl Class
 * 
 * @author Junghwan Hong
 */
public class InternalBuildCommandImpl implements Command {
	
	BuildLogService buildLogger;

	protected void setBuildLogService(BuildLogService buildLogger) {
		this.buildLogger = buildLogger;
	}
	
	public void execute(String line, PrintStream out, PrintStream err) {
		try {
			String action = null;
			StringTokenizer tok = new StringTokenizer(line, " ");
			tok.nextToken(); // name
			if (tok.hasMoreTokens()) {
				action = tok.nextToken(); // action
			}
	
			if (action == null || action.equals("help")) {
				out.println(getFullUsage());
				return;
			} else if (action.equals("add")) {
				if (!tok.hasMoreTokens()) {
					throw new OdenException("Invalid Arguments");
				}
				int start = line.indexOf(tok.nextToken());
				int end = line.lastIndexOf('}');
				if (end <= start) {
					throw new OdenException("Invalid Arguments");
				}
	
				addBuild(new JSONObject(line.substring(start, end + 1)));
				out.println("[]");
			} else {
				throw new OdenException("Invalid action: " + action);
			}
		} catch (Exception e) {
			err.println(JSONUtil.jsonizedException(e));
			Logger.error(e);
		}	
	}
	
	private void addBuild(JSONObject jjob) throws Exception {
		long tm = System.currentTimeMillis();
		
		BrecordElement r = new BrecordElement(String.valueOf(tm),
				jjob.getString("job"), tm, jjob.getString("buildno"),
				"true".equals(jjob.getString("success")) ? true : false);
		buildLogger.record(r);
	}
	
	private String getFullUsage() {
		return "_build add {"
				+ "\n\t\"job\": \"\","
				+ "\n\t\"buildno\": \"\","
				+ "\n\t\"success\": \"\""
				+ "\n\t}";
	}
	
	public String getName() {
		return "_build";
	}

	public String getUsage() {
		return "_build help";
	}

	public String getShortDescription() {
		return "add Build History";
	}

	

}
