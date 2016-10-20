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
package org.anyframe.oden.admin.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.oden.admin.common.OdenCommonDao;
import org.anyframe.oden.admin.convert.JsonConverter;
import org.anyframe.oden.admin.domain.Command;
import org.anyframe.oden.admin.domain.Server;
import org.anyframe.oden.admin.service.ScriptService;
import org.anyframe.oden.admin.util.CommandUtil;
import org.anyframe.oden.admin.util.OdenConstants;
import org.anyframe.pagination.Page;
import org.hsqldb.lib.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * This is ScriptServiceimpl Class
 * 
 * @author Sujeong Lee
 */
@Service("scriptService")
public class ScriptServiceimpl implements ScriptService {

	@Inject
	@Named("odenCommonDao")
	OdenCommonDao<Server> odenCommonDao;

	/**
	 * Method for getting script list in certain job.
	 * 
	 * @param param
	 * @param opt
	 */
	public Page findListByPk(String param, String opt) throws Exception {
		List<Command> list = new ArrayList<Command>();

		if (!StringUtil.isEmpty(param)) {
			String command = CommandUtil.getBasicCommand("job", "info", OdenConstants.DOUBLE_QUOTATOIN + param + OdenConstants.DOUBLE_QUOTATOIN);
			List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

			for (JSONObject object : objectArray) {
				JSONArray commands = (JSONArray) object.get("commands");

				for (int num = 0; num < commands.length(); num++) {
					JSONObject cmdObject = (JSONObject) commands.get(num);
					Command c = JsonConverter.jsonToCommand(cmdObject);

					String event = "";
					event = event.concat(OdenConstants.A_HREF_HEAD + "javascript:runScript('" + c.getName() + "');" + OdenConstants.A_HREF_MID);
					if ("run".equals(opt)) {
						event = event.concat(OdenConstants.IMG_TAG_RUN);
					}
					if ("del".equals(opt)) {
						event = event.concat(OdenConstants.IMG_TAG_DEL);
					}

					event = event.concat(OdenConstants.A_HREF_TAIL);

					c.setHidden(event);
					list.add(c);
				}
			}
		}

		if (list.isEmpty()) {
			return new Page(list, 1, list.size(), 1, 1);
		} else {
			return new Page(list, 1, list.size(), list.size(), list.size());
		}
	}

	/**
	 * Method for running script in some targets.
	 * 
	 * @param targets
	 * @param jobName
	 * @param scriptName
	 */
	public String run(String[] targets, String jobName, String scriptName) throws Exception {
		String returnString = "";

		String options = "\"" + jobName + "\" " + "-t" + " ";

		for (int i = 0; i < targets.length; i++) {
			options = options.concat("\"" + targets[i] + "\" ");
		}
		options = options.concat("-c" + " \"" + scriptName + "\"");

		String command = CommandUtil.getBasicCommand("exec", "run", options);
		List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

		for (JSONObject object : objectArray) {
			String target = object.getString("target");
			String resultScript = object.getString("result");

			returnString = returnString.concat("[" + target + "]\r" + resultScript + "\r\r\r");
		}

		return returnString;
	}

	/**
	 * Method for running script , Before(After) Deploying.
	 * 
	 * @param jobName
	 * 
	 * 
	 */
	public List<Command> getCommandList(String jobName) throws Exception {
		List<Command> cmds = new ArrayList<Command>();

		String command = CommandUtil.getBasicCommand("job", "info", OdenConstants.DOUBLE_QUOTATOIN + jobName + OdenConstants.DOUBLE_QUOTATOIN);
		List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

		for (JSONObject object : objectArray) {
			JSONArray commands = (JSONArray) object.get("commands");

			for (int num = 0; num < commands.length(); num++) {
				JSONObject cmdObject = (JSONObject) commands.get(num);
				Command c = JsonConverter.jsonToCommand(cmdObject);
				cmds.add(c);
			}
		}

		return cmds;
	}

}