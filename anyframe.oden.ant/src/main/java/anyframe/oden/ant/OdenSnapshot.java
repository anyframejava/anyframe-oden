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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import anyframe.oden.ant.brokers.OdenBrokerImpl;
import anyframe.oden.ant.brokers.OdenBrokerService;
import anyframe.oden.ant.exception.CommandNotFoundException;
import anyframe.oden.ant.exception.OdenException;

/**
 * 
 * @author LEE Sujeong
 *
 */
public class OdenSnapshot extends Task {

	@Override
	public void execute() throws BuildException {
		checkSnapshotInfo();
		packSnapshot();

	}

	private void checkSnapshotInfo() {
		if (OdenAntUtil.isNull(name)) {
			OdenAntUtil
					.buildFailMsg(
							"<oden-snapshot/>'s attribute named \"name\" must have value.",
							null);
		}
		if (OdenAntUtil.isNull(server)) {
			OdenAntUtil
					.buildFailMsg(
							"<oden-snapshot/>'s attribute named \"server\" must have value.",
							null);
		}
		if (OdenAntUtil.isNull(port)) {
			OdenAntUtil
					.buildFailMsg(
							"<oden-snapshot/>'s attribute named \"port\" must have value.",
							null);
		}
	}

	private void packSnapshot() {
		String command = "snapshot run" + " " + name + " " + "-sync" + " "
				+ "-json";
//		System.out.println("Snapshot Run : " + command);
		log("Snapshot Log : "+command, Project.MSG_ERR);
		cmdConnect(command);
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

	String name = "";
	String server = "";
	String port = "";

	private OdenBrokerService OdenBroker = new OdenBrokerImpl();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

}
