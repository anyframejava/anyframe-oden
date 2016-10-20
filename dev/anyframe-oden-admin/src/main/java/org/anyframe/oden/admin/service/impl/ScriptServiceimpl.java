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

import org.anyframe.oden.admin.common.OdenCommonDao;
import org.anyframe.oden.admin.domain.Command;
import org.anyframe.oden.admin.domain.Server;
import org.anyframe.oden.admin.service.ScriptService;
import org.anyframe.pagination.Page;
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

	private final OdenCommonDao<Server> odenCommonDao = new OdenCommonDao<Server>();

	String ahrefPre = "<a href=\"";
	String ahrefMid = "\">";
	String ahrefPost = "</a>";

	String doubleQuotation = "\"";

	/**
	 * Method for getting script list in certain job.
	 * 
	 * @param param
	 * @param opt
	 */
	@SuppressWarnings("PMD")
	public Page findListByPk(String param, String opt) throws Exception {

		if ("".equals(param)) {
			List list = new ArrayList();
			return new Page(list, 1, list.size(), 1, 1);
		} else {
			List list = new ArrayList();

			String result = odenCommonDao.getResultString("job", "info",
					doubleQuotation + param + doubleQuotation);

			String imgRun = "<img src='images/play.png'/>";
			String imgDel = "<img src='images/ico_del.gif'/>";

			if (result == null || "".equals(result)) {
				return new Page(list, 1, list.size(), 1, 1);
			}
			JSONArray array = new JSONArray(result);
			if (array.length() == 0) {
				return new Page(list, 1, list.size(), 1, 1);
			}

			int recordSize = array.length();

			for (int i = 0; i < recordSize; i++) {

				JSONObject object = (JSONObject) array.get(i);
				JSONArray commands = (JSONArray) object.get("commands");

				for (int num = 0; num < commands.length(); num++) {
					Command c = new Command();

					JSONObject command = (JSONObject) commands.get(num);
					String name = command.getString("name");
					String path = command.getString("dir");
					String cmd = command.getString("command");

					String event = "";
					event = event.concat(ahrefPre + "javascript:runScript('"
							+ name + "');" + ahrefMid);
					if ("run".equals(opt)) {
						event = event.concat(imgRun + ahrefPost);
					}
					if ("del".equals(opt)) {
						event = event.concat(imgDel + ahrefPost);
					}

					c.setName(name);
					c.setPath(path);
					c.setCmd(cmd);
					c.setHidden(event);
					c.setHiddenname(name);

					list.add(c);
				}
			}

			if (list.isEmpty()) {
				return new Page(list, 1, list.size(), 1, 1);
			} else {
				return new Page(list, 1, list.size(), list.size(), list.size());
			}
		}
	}

	/**
	 * Method for running script in some targets.
	 * 
	 * @param targets
	 * @param jobName
	 * @param scriptName
	 */
	public String run(String[] targets, String jobName, String scriptName)
			throws Exception {
		String returnString = "";

		String options = "\"" + jobName + "\" " + "-t" + " ";

		for (int i = 0; i < targets.length; i++) {
			options = options.concat("\"" + targets[i] + "\" ");
		}

		options = options.concat("-c" + " \"" + scriptName + "\"");

		String result = odenCommonDao.getResultString("exec", "run", options);

		if (!(result == null) && !"".equals(result)) {
			JSONArray array = new JSONArray(result);
			if (!(array.length() == 0)) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = (JSONObject) array.get(i);
					String target = object.getString("target");
					String resultScript = object.getString("result");

					returnString = returnString.concat("[" + target + "]\r"
							+ resultScript + "\r\r\r");
				}
			}
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
	@SuppressWarnings("PMD")
	public List<Command> getCommandList(String jobName) throws Exception {
		List<Command> cmds = new ArrayList<Command>();

		String result = odenCommonDao.getResultString("job", "info",
				doubleQuotation + jobName + doubleQuotation);

		if (!(result == null) && !"".equals(result)) {
			JSONArray array = new JSONArray(result);
			if (!(array.length() == 0)) {
				int recordSize = array.length();

				for (int i = 0; i < recordSize; i++) {

					JSONObject object = (JSONObject) array.get(i);
					JSONArray commands = (JSONArray) object.get("commands");

					for (int num = 0; num < commands.length(); num++) {
						Command c = new Command();

						JSONObject command = (JSONObject) commands.get(num);
						String name = command.getString("name");

						c.setName(name);
						cmds.add(c);
					}
				}
			}
		}

		return cmds;
	}

}