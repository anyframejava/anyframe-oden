/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package anyframe.oden.bundle.core.command;

import java.io.PrintStream;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.log.LogService;

import anyframe.common.bundle.gate.CustomCommand;
import anyframe.oden.bundle.common.DateUtil;
import anyframe.oden.bundle.common.JSONUtil;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.Logger;
import anyframe.oden.bundle.core.record.DeployLogService;
import anyframe.oden.bundle.core.record.RecordElement;

/**
 * Oden Shell commands to search deploy log.
 * 
 * @author joon1k
 *
 */
public class HistoryCommandImpl implements CustomCommand {
	private static final String[] AGENT_OP = {"agent", "a"};
	private static final String[] DATE_OP = {"date", "d"};
	private static final String[] PATH_OP = {"path", "p"};
	private static final String[] USER_OP = {"user", "u"};
	private static final String[] STATUS_OP = {"status", "s"};
	
	private DeployLogService deploylog;
	
	protected void setDeployLogService(DeployLogService recordsvc) {
		this.deploylog = recordsvc;
	}
	
	protected void unsetDeployRecordHandlerService(DeployLogService recordsvc) {
		this.deploylog = null;
	}
	
	public void execute(String line, PrintStream out, PrintStream err) {
		String consoleResult = "";
		boolean isJSON = false;

		try {
			JSONArray ja = new JSONArray();
			
			Cmd cmd = new Cmd(line);
			String action = cmd.getAction();
			isJSON = cmd.getOption(Cmd.JSON_OPT) != null;
			
			if(Cmd.SHOW_ACTION.equals(action)) {
				String[] date = cmd.getOptionArgArray(DATE_OP);
				ja = search(cmd.getOptionArg(USER_OP),
						cmd.getOptionArg(AGENT_OP),
						cmd.getOptionArg(PATH_OP),
						date.length > 0 ? date[0] : "",
						date.length > 1 ? date[1] : "",
						null);
			}else if(action.length() == 0 || Cmd.HELP_ACTION.equals(action)){
				consoleResult = getFullUsage();
			}else {
				throw new OdenException("Couldn't execute specified action: " + action);
			}
			
			if(isJSON)
				out.println(ja.toString());
			else if(consoleResult.length() > 0)
				out.println(consoleResult);
			else
				out.println(serialize(ja));
			
		}catch(OdenException e){
			if(isJSON){
				err.println(JSONUtil.jsonizedException(e));
			}else {
				err.println(e.getMessage());
				Logger.log(LogService.LOG_ERROR, e.getMessage(), e);
			}
		}catch(Exception e){
			if(isJSON){
				err.println(JSONUtil.jsonizedException(e));
			}else {
				err.println("Couldn't execute command. See log. " + e.getMessage());
				Logger.log(LogService.LOG_ERROR, e.getMessage(), e);	
			}
		}

	}
	
	private String serialize(JSONArray ja) throws JSONException {
		StringBuffer buf = new StringBuffer();
		for(int i=0; i<ja.length(); i++){
			JSONObject record = (JSONObject)ja.get(i);
			buf.append("=============================\n");
			buf.append("host: " + record.getString("host") + 
					", agent: " + record.getString("agent") + 
					", date: " + record.getString("date") +
					", root: " + record.getString("root") + "\n");
			
			JSONArray paths = (JSONArray)record.getJSONArray("paths");
			for(int j=0; j<paths.length(); j++)
				buf.append(paths.getString(j) + "\n");
		}
		return buf.toString();
	}

	private JSONArray search(String host, String agent, String path, 
			String startdate, String enddate, String status) throws OdenException{
		JSONArray jresult = new JSONArray();
		List<RecordElement> results = deploylog.search(host, agent, path, 
				startdate, enddate, status);
		for(RecordElement record : results){
			JSONObject jrecord = new JSONObject();
			jresult.put(jrecord);
			try {
				jrecord.put("host", record.getHost());
				jrecord.put("agent", record.getAgent());
				jrecord.put("date", DateUtil.toStringDate(record.getDate()));
				jrecord.put("root", record.getRootpath());
				jrecord.put("paths", new JSONArray(record.getPaths()));
			} catch (JSONException e) {
				throw new OdenException("Fail to jsonize. " + e.getMessage());
			}
		}
		return jresult;
		
	}

	public String getName() {
		return "history";
	}

	public String getShortDescription() {
		return "inquiry deploy logs.";
	}

	public String getUsage() {
		return getName() + " " + Cmd.HELP_ACTION;
	}

	public String getFullUsage() {
		return "history show " +
				"\n\t[-u[ser] <user-access-ip>] " +
				"\n\t[-a[gent] <host-name>] " +
				"\n\t[-p[ath] <path>] " +
				"\n\t[-d[ate] <start-date: yyyyMMdd> <end-date: yyyyMMdd>]";
	}
	
}
