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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.log.LogService;

import anyframe.oden.bundle.common.JSONUtil;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.Logger;
import anyframe.oden.bundle.prefs.Prefs;

/**
 * 
 * @author joon1k
 *
 */
public class RollbackCommandImpl extends OdenCommand {

	public RollbackCommandImpl(){
	}
	
	public void execute(String line, PrintStream out, PrintStream err) {
		String consoleResult = "";
		boolean isJSON = false;
		
		try{
			JSONArray ja = new JSONArray();
			
			Cmd cmd = new Cmd(line);
			String action = cmd.getAction();
			isJSON = cmd.getOption(Cmd.JSON_OPT) != null;
			
			if(Cmd.RUN_ACTION.equals(action)){
				if(cmd.getActionArg().length() > 0){
					String fname = cmd.getActionArg();
					if(!isJSON && doFileInfoActionJ(fname).length() == 0) {
						throw new OdenException("Couldn't find that file: " + cmd.getActionArg());
					} else {
						String planName = findPlanNameWithFileName(fname);
						doRollback(fname, planName, extractUserName(cmd)); 
						consoleResult = "Finished.";
					}
				}else {
					throw new OdenException("Couldn't execute command.");
				}
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
				out.println(JSONUtil.toString(ja));
			
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
	
	private void doRollback(String fname, String planName, String user) throws OdenException {
		if(planName.length() == 0)
			throw new OdenException("Couldn't find a plan info for the " + planName);
		
		Cmd planCmd = planInfo(planName);
		if(planCmd == null)
			throw new OdenException("Couldn't find a plan: " + planName);
		
		String destArgs = planCmd.getOptionArg(SnapshotCommandImpl.DEST_OPT);
		AgentLoc agent = new AgentLoc(destArgs, configService);
		
		String target = new AgentLoc(agent.agentName() + 
				planCmd.getOptionArg(SnapshotCommandImpl.SOURCE_OPT) , configService).location();
		
		delegateService.rollback(agent.agentAddr(), agent.location(), fname, target, user);
	}

	public String getName() {
		return "rollback";
	}

	public String getShortDescription() {
		return "rollback files from snapshot-files.";
	}

	public String getUsage() {
		return getName() + " " + Cmd.HELP_ACTION;
	}

	public String getFullUsage() {
		return getName() + " " + Cmd.RUN_ACTION + " <snapshot-file-name>";
	}

	private Cmd planInfo(String name) throws OdenException {
		return toInfoCmd(SnapshotConstants.PLAN_NODE, name);
	}
	
	private String findPlanNameWithFileName(String fname){
		// 0: size, 1: plan name
		String planName = "";
		try {
			planName = toInfoCmd(SnapshotConstants.FILE_NODE, fname).
					getOptionArg(new String[]{"plan"});
		} catch (OdenException e) {
		}
		return planName; 
	}	
	
	private Prefs getFilePrefs() {
		return getPrefs(SnapshotConstants.FILE_NODE);
	}
	
	private JSONArray doFileInfoActionJ(String fname) {
		JSONArray arr = new JSONArray();
		try {
			String info = getFilePrefs().get(fname);
			if(info.length() > 0)
				arr.put(new JSONObject().put(fname, info));
		} catch (JSONException e) {
			return null;
		}
		return arr;
	}
}
