/*
 * Copyright 2010 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package anyframe.oden.ant;

import java.util.ArrayList;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import anyframe.oden.ant.brokers.OdenBrokerImpl;
import anyframe.oden.ant.brokers.OdenBrokerService;
import anyframe.oden.ant.exception.CommandNotFoundException;
import anyframe.oden.ant.exception.OdenException;

/**
 * 
 * @author LEE Sujeong
 * 
 */
public class OdenTask extends Task {

	@Override
	public void execute() throws BuildException {
		checkTaskInfo();
		if (isExistTask) {
			runExistTask();
		} else {
			packDeploys();
			packRemoves();
			packTasks();
			runTask();
			deleteTempPolicyTask();
		}
	}

	private void checkTaskInfo() {
		if (OdenAntUtil.isNull(name)) {
			isExistTask = false;
			name = "";
		} else {
			isExistTask = true;
			if (deploy.size() != 0) {
				OdenAntUtil
						.buildFailMsg(
								"If task is already exist, can't use <deploy/> or <remove/>.",
								null);
			}
		}
		if (OdenAntUtil.isNull(failundo) || failundo.equals("false")) {
			failundo = "";
		} else if (failundo.equals("true")) {

		} else {
			OdenAntUtil
					.buildFailMsg(
							"<oden-task/>'s attribute named \"failundo\" can have value only \"true\" or \"false\".",
							null);
			failundo = "";
		}
		if (OdenAntUtil.isNull(server)) {
			OdenAntUtil
					.buildFailMsg(
							"<oden-task/>'s attribute named \"server\" must have value.",
							null);
			server = "";
		}
		if (OdenAntUtil.isNull(port)) {
			OdenAntUtil.buildFailMsg(
					"<oden-task/>'s attribute named \"port\" must have value.",
					null);
			port = "";
		}
		OdenBroker.setPw(pw);
		OdenBroker.setId(id);
	}

	private void runExistTask() {

		// ArrayList taskNameList = OdenAntUtil.parseArg(name);
		// String taskName = "";
		// for (int i = 0; i < taskNameList.size(); i++) {
		// taskName += taskNameList.get(i) + " ";
		// }
		if (name.indexOf(",") == -1) {
			String command = "task run" + " \"" + name + "\" " + "-sync" + " "
					+ "-json";
			String result = cmdConnect(command);
			if (result != null) {
				parseJSONTaskRunResult(result);
			}
		} else {
			OdenAntUtil.buildFailMsg("<deploy/>'s attribute named \"name\" can have only one value.",null);
		}
	}

	private void packDeploys() {
		for (int i = 0; i < deploy.size(); i++) {
			Deploy d = (Deploy) deploy.get(i);
			String command = packDeployCommand(d);
			cmdConnect(command);
		}
	}

	private String packDeployCommand(Deploy deploy) {
		String tempPolicyName = "oden_temp_policy_" + nPolicies;
		tempPolicyNameList.add(tempPolicyName);
		nPolicies++;

		String command = "policy add" + " " + tempPolicyName + " ";

		checkDeployInfo(deploy);

		String undateonly = deploy.getUpdateonly();
		String repository = deploy.getRepository();
		String includes = deploy.getIncludes();
		String excludes = deploy.getExcludes();

		if (!undateonly.equals("false")) {
			command += "-u" + " ";
		}
		if (!repository.equals("")) {
			ArrayList repoList = OdenAntUtil.parseArg(repository);
			String repo = "";
			for (int i = 0; i < repoList.size(); i++) {
				repo += "\"" + repoList.get(i) + "\" ";
			}
			command += "-repo" + " " + repo + " ";
		}
		if (!includes.equals("")) {
			ArrayList includeList = OdenAntUtil.parseArg(includes);
			String include = "";
			for (int i = 0; i < includeList.size(); i++) {
				include += "\"" + includeList.get(i) + "\" ";
			}
			command += "-include" + " " + include;
		}
		if (!excludes.equals("")) {
			ArrayList excludeList = OdenAntUtil.parseArg(excludes);
			String exclude = "";
			for (int i = 0; i < excludeList.size(); i++) {
				exclude += "\"" + excludeList.get(i) + "\" ";
			}
			command += "-exclude" + " " + exclude;
		}

		command += appendAgent(deploy.getAgents());
		command += " " + "-json";
		return command;
	}

	private String appendAgent(Vector agents) {
		String agentCommand = "-d" + " ";
		if (agents.size() == 0) {
			OdenAntUtil
					.buildFailMsg(
							"<deploy/> and <remove/> must have at least one of the agent.",
							null);
		} else {
			for (int i = 0; i < agents.size(); i++) {
				Agent agent = (Agent) agents.get(i);
				String command = packAgentCommand(agent);
				agentCommand += command + " ";
			}
		}
		return agentCommand;
	}

	private String packAgentCommand(Agent agent) {
		String command = "";
		checkAgentInfo(agent);

		String name = agent.getName();

		if (name.equals("")) {
			command = getCommandFileImport(agent);
		} else {
			command = "\"" + getCommandAgent(agent) + "\"";
		}

		return command;
	}

	private String getCommandFileImport(Agent agent) {
		String command = "";

		String fileimport = agent.getImport();
		String groups = agent.getGroups();

		AgentInfoUtil util = new AgentInfoUtil();
		ArrayList list = util.getAgents(fileimport, groups);

		for (int j = 0; j < list.size(); j++) {
			Agent info = (Agent) list.get(j);
			String agentCmd = "\"" + getCommandAgent(info) + "\"";
			command += agentCmd + " ";
		}
		return command;
	}

	private String getCommandAgent(Agent agent) {
		String command = "";

		String name = agent.getName();
		String path = agent.getPath();
		String locvar = agent.getLocvar();

		command += name + ":";
		if (locvar.equals("")) {
			if (path.equals("")) {
				command += "~";
			} else {
				if (path.length() > 1) {
					if (path.charAt(0) == '/' || path.charAt(1) == ':') {
						command += path;
					} else {
						command += "~" + "/" + path;
					}
				} else {
					command += "~" + "/" + path;
				}
			}
		} else {
			command += "$" + locvar;
			if (path.equals("")) {
			} else if (path.charAt(0) == '/' || path.charAt(1) == ':') {
				command += path;
			} else {
				command += "/" + path;
			}
		}
		return command;
	}

	private void checkAgentInfo(Agent agent) {
		String name = agent.getName();
		String path = agent.getPath();
		String locvar = agent.getLocvar();
		String fileimport = agent.getImport();
		String groups = agent.getGroups();

		if (OdenAntUtil.isNull(name)) {
			if (!OdenAntUtil.isNull(fileimport) && !OdenAntUtil.isNull(groups)) {
			} else {
				OdenAntUtil
						.buildFailMsg(
								"<agent/>'s attribute named \"fileimport\" and \"groups\" must have value.",
								null);
			}
			if (!OdenAntUtil.isNull(locvar)) {
				OdenAntUtil.buildFailMsg(
						"Can't use \"locvar\" without \"name\".", null);
			}
			agent.setName("");
		} else {
			if (OdenAntUtil.isNull(fileimport) && OdenAntUtil.isNull(groups)) {
				agent.setImport("");
				agent.setGroups("");
			} else {
				OdenAntUtil
						.buildFailMsg(
								"Can't use \"fileimport\" and \"groups\" with \"name\".",
								null);
			}
			if (OdenAntUtil.isNull(locvar)) {
				agent.setLocvar("");
			}
		}
		if (OdenAntUtil.isNull(path)) {
			agent.setPath("");
		}

	}

	private void checkDeployInfo(Deploy deploy) {

		String undateonly = deploy.getUpdateonly();
		String repository = deploy.getRepository();
		String includes = deploy.getIncludes();
		String excludes = deploy.getExcludes();

		if (OdenAntUtil.isNull(undateonly) || undateonly.equals("false")) {
			deploy.setUpdateonly("false");
		} else if (undateonly.equals("true")) {

		} else {
			OdenAntUtil
					.buildFailMsg(
							"<deploy/>'s attribute named \"undateonly\" can have value only \"true\" or \"false\".",
							null);
			deploy.setUpdateonly("false");
		}
		if (OdenAntUtil.isNull(repository)) {
			OdenAntUtil
					.buildFailMsg(
							"<deploy/>'s attribute named \"repository\" must have value.",
							null);
			deploy.setRepository("");
		}
		if (OdenAntUtil.isNull(includes)) {
			OdenAntUtil
					.buildFailMsg(
							"<deploy/>'s attribute named \"includes\" must have value.",
							null);
			deploy.setIncludes("");
		}
		if (OdenAntUtil.isNull(excludes)) {
			deploy.setExcludes("");
		}
	}

	private void packRemoves() {
		for (int i = 0; i < removes.size(); i++) {
			Remove remove = (Remove) removes.get(i);
			String command = packRemoveCommand(remove);
			cmdConnect(command);
		}

	}

	private String packRemoveCommand(Remove remove) {
		String tempPolicyName = "oden_temp_policy_" + nPolicies;
		tempPolicyNameList.add(tempPolicyName);
		nPolicies++;

		String command = "policy add" + " " + tempPolicyName + " ";

		checkRemoveInfo(remove);

		String includes = remove.getIncludes();
		String excludes = remove.getExcludes();

		if (!includes.equals("")) {
			ArrayList includeList = OdenAntUtil.parseArg(includes);
			String include = "";
			for (int i = 0; i < includeList.size(); i++) {
				include += "\"" + includeList.get(i) + "\" ";
			}
			command += "-include" + " " + include;
		}
		if (!excludes.equals("")) {
			ArrayList excludeList = OdenAntUtil.parseArg(excludes);
			String exclude = "";
			for (int i = 0; i < excludeList.size(); i++) {
				exclude += "\"" + excludeList.get(i) + "\" ";
			}
			command += "-exclude" + " " + exclude;
		}

		command += appendAgent(remove.getAgents());
		command += " " + "-del";
		command += " " + "-json";
		return command;
	}

	private void checkRemoveInfo(Remove remove) {
		String includes = remove.getIncludes();
		String excludes = remove.getExcludes();

		if (OdenAntUtil.isNull(includes)) {
			OdenAntUtil
					.buildFailMsg(
							"<remove/>'s attribute named \"includes\" must have value.",
							null);
		} else {
			if (OdenAntUtil.isNull(excludes)) {
				remove.setExcludes("");
			}
		}
	}

	private void packTasks() {
		tempTaskName = "oden_ant_task";
		String taskCmd = "task add" + " " + tempTaskName + " " + "-policy"
				+ " ";
		for (int i = 0; i < tempPolicyNameList.size(); i++) {
			taskCmd += tempPolicyNameList.get(i) + " ";
		}
		taskCmd += " " + "-json";
		cmdConnect(taskCmd);
	}

	private void runTask() {
		String taskRunCmd = "task run" + " " + tempTaskName + " " + "-sync"
				+ " " + "-json";
		String result = cmdConnect(taskRunCmd);
		if (result != null) {
			parseJSONTaskRunResult(result);
		}
	}

	private void deleteTempPolicyTask() {
		String deleteTask = "task del" + " " + tempTaskName + " " + "-json";
		cmdConnect(deleteTask);

		for (int i = 0; i < tempPolicyNameList.size(); i++) {
			String deletePolicy = "policy del" + " "
					+ tempPolicyNameList.get(i) + " " + "-json";
			cmdConnect(deletePolicy);
		}
	}

	private String cmdConnect(String cmd) {
		String result = "";
		try {
			result = OdenBroker.sendRequest("http://" + server + ":" + port
					+ "/shell", cmd);
		} catch (OdenException e) {
			OdenAntUtil.buildFailMsg("Connection Error", e);
		} catch (CommandNotFoundException e) {
			OdenAntUtil.buildFailMsg("Command Error", e);
		}
		return result;
	}

	private void undoFailTask(String txid) {
		displayFailCause(txid);

		String undoCmd = "history undo" + " " + txid + " " + "-json" + " "
				+ "-sync";
		String result = cmdConnect(undoCmd);

		deleteTempPolicyTask();
	}

	private void displayFailCause(String txid) {
		String infoCmd = "history show" + " " + txid + " " + "-failonly" + " "
				+ "-json";
		String result = cmdConnect(infoCmd);
		parseJSONHistoryInfoResult(result);
	}

	private void parseJSONTaskRunResult(String result) {
		try {
			JSONArray array = new JSONArray(result);
			if (array.length() > 0) {
				for (int n = 0; n < array.length(); n++) {
					String status = (String) ((JSONObject) array.get(n))
							.get("status"); //$NON-NLS-1$
					String txid = (String) ((JSONObject) array.get(n))
							.get("txid"); //$NON-NLS-1$
					if (status.equalsIgnoreCase("F")) {
						log("Fail TxID : " + txid, Project.MSG_ERR);
						if (failundo.equalsIgnoreCase("true")) {
							undoFailTask(txid);
						}

						String resultForLog = cmdConnect("history show" + " "
								+ txid + " " + "-json");
						String log = "";
						if (result != null) {
							JSONArray ja = new JSONArray(resultForLog);
							if (ja.length() > 0) {
								for (int i = 0; i < ja.length(); i++) {
									log = (String) ((JSONObject) ja.get(i))
											.get("log"); //$NON-NLS-1$
								}
							}
						}
						OdenAntUtil.buildFailMsg(log, null);
					} else {
						log("Success TxID : " + txid, Project.MSG_INFO);
					}
				}
			} else {
				OdenAntUtil.buildFailMsg("", null);
			}
		} catch (JSONException e) {
			OdenAntUtil.buildFailMsg("", e);
		}
	}

	private void parseJSONHistoryInfoResult(String result) {
		try {
			JSONArray array = new JSONArray(result);
			if (array.length() > 0) {
				for (int n = 0; n < array.length(); n++) {
					JSONObject object = (JSONObject) array.get(n); //$NON-NLS-1$
					JSONArray files = (JSONArray) (object.get("files"));
					if (files.length() > 0) {
						for (int i = 0; i < files.length(); i++) {
							String errorlog = (String) ((JSONObject) files
									.get(i)).get("errorlog"); //$NON-NLS-1$
							String path = (String) ((JSONObject) files.get(i))
									.get("path"); //$NON-NLS-1$

							String agentName = "";
							String addr = "";
							String loc = "";

							JSONObject agent = (JSONObject) ((JSONObject) files
									.get(i)).get("agent");
							if (agent != null) {
								agentName = (String) agent.get("name"); //$NON-NLS-1$
								addr = (String) agent.get("addr");//$NON-NLS-1$
								loc = (String) agent.get("loc");; //$NON-NLS-1$
							}

							log("===========================", Project.MSG_ERR);
							String prefix = "[Oden Fail]";
							log(prefix + " Agent : " + agentName,
									Project.MSG_ERR);
							log(prefix + " Path : " + addr + ":" + loc,
									Project.MSG_ERR);
							log(prefix + " File : " + path, Project.MSG_ERR);
							log(prefix + " Log : " + errorlog, Project.MSG_ERR);
						}
					}
				}
			} else {
				OdenAntUtil.buildFailMsg("", null);
			}
		} catch (JSONException e) {
			OdenAntUtil.buildFailMsg("", e);
		}
	}

	private String tempTaskName = "";
	private int nPolicies = 0;
	private ArrayList tempPolicyNameList = new ArrayList();

	private OdenBrokerService OdenBroker = new OdenBrokerImpl();

	boolean isExistTask = false;

	String server = "";
	String port = "";
	String name = "";
	String failundo = "";
	
	String id = "";
	String pw = "";

	Vector deploy = new Vector();
	Vector removes = new Vector();

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFailundo() {
		return failundo;
	}

	public void setFailundo(String failundo) {
		this.failundo = failundo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public Deploy createDeploy() {
		Deploy d = new Deploy();
		deploy.add(d);
		return d;
	}

	public Remove createRemove() {
		Remove r = new Remove();
		removes.add(r);
		return r;
	}

	// log("error", Project.MSG_ERR); //red
	// log("info", Project.MSG_INFO); //blue
	// log("warn", Project.MSG_WARN); //orange

}
