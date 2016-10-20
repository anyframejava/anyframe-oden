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
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.log.LogService;

import anyframe.oden.bundle.common.DateUtil;
import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.JSONUtil;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.Logger;
import anyframe.oden.bundle.prefs.Prefs;

/**
 * @author joon1k
 *
 */
public class SnapshotCommandImpl extends OdenCommand {
	public final static String TEST_ACTION = "test";
	
	public final static String[] DEST_OPT = {"dest", "d"};
	
	public final static String[] SOURCE_OPT = {"source", "s"};
	
	public final static String[] PLAN_OPT = {"plan", "p"};
	
	public final static String[] FILE_OPT = {"file", "f"};
	
	public final static String[] DESC_OPT = {"desc"};
	
	public final static String[] DATE_OPT = {"date"};
	
	public final static String FILE_ACTION = "file";
	
	public final static String PLAN_ACTION = "plan";
	
	public SnapshotCommandImpl(){
	}
	
	public void execute(String line, PrintStream out, PrintStream err) {
		String consoleResult = "";
		boolean isJSON = false;
		
		try{
			JSONArray ja = new JSONArray();
			
			Cmd cmd = new Cmd(line);
			String action = cmd.getAction();
			isJSON = cmd.getOption(Cmd.JSON_OPT) != null;
			
			if(Cmd.INFO_ACTION.equals(action)){
				Opt fileopt = cmd.getOption(FILE_OPT);
				if(fileopt != null){
					String fileName = cmd.getOptionArg(FILE_OPT);
					if(fileName.length() == 0){
						ja = doFileListActionJ();
					}else {
						ja = doFileInfoActionJ(fileName);
						if(ja.length() == 0)
							throw new OdenException("Couldn't find that file: " + fileName);
					}
				}else {
					String planName = cmd.getOptionArg(PLAN_OPT);
					if(planName.length() == 0){
						ja = doPlanListActionJ();
					}else {
						ja = doPlanInfoActionJ(planName);
						if(ja.length() == 0)
							throw new OdenException("Couldn't find a plan: " + planName);
					}
				}
			}else if(Cmd.REMOVE_ACTION.equals(action)){
				String planName = cmd.getOptionArg(PLAN_OPT);
				String fileName = cmd.getOptionArg(FILE_OPT);
				if(planName.length() > 0){	
					if(doPlanInfoActionJ(planName).length() > 0){
						ja = removePlan(planName);
						consoleResult = planName + " is removed.\nRelated snapshot files are also removed: " + ja.toString() ;	
					}else {
						throw new OdenException("Couldn't find a plan: " + planName);
					}
				}else if(fileName.length() > 0){	
					if(doFileInfoActionJ(fileName).length() > 0){
						String fname = cmd.getOptionArg(FILE_OPT);
						removeFile(fname);
						consoleResult = fileName + " is removed.";	
					}else {
						throw new OdenException("Couldn't find that file: " + fileName);
					}
				}else
					throw new OdenException("Couldn't execute command.");
			}else if(Cmd.RUN_ACTION.equals(action)){
				String planName = cmd.getActionArg();
				if(planName.length() > 0) 
					if(doPlanInfoActionJ(planName).length() == 0) 
						throw new OdenException("Couldn't find that plan: " + planName);
					else{
						doSnapshot(planName, extractUserName(cmd));
						consoleResult = "Backup is done.";
					}
				else
					throw new OdenException("Couldn't execute command.");
			}else if(Cmd.ADD_ACTION.equals(action)){
				if(cmd.getActionArg().length() > 0 && cmd.getOptions().size() > 0){
					String planName = cmd.getActionArg(); 
					addPlan(planName, cmd.getOptionString(), extractUserName(cmd));
					consoleResult = cmd.getActionArg() + " is added.";
				}else 
					throw new OdenException("Couldn't add a plan."); 
			}else if(TEST_ACTION.equals(action)){
				String planname = cmd.getActionArg(); 
				if(planname.length() <= 1)
					throw new OdenException("Couldn't execute command.");
				
				Cmd infocmd = getPlanCmd(planname);
				Map detail = validateSourceNDest(infocmd);
				ja.put(new JSONObject(detail));
				String _agent = "[" + detail.get("agent") + "]";
				consoleResult = "Source(Backup Location): " + _agent + detail.get("source") + 
						"\nDestination(Snapshot Location): " + _agent + detail.get("dest");
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

	private void removeFile(String fname) throws OdenException {
		String planName = getPlanNameForFile(fname);
		Cmd planCmd = getPlanCmd(planName);
		AgentLoc agent = new AgentLoc(
				planCmd.getOptionArg(DEST_OPT), configService);
		
		delegateService.removeSnapshot(agent.agentAddr(), agent.location(), fname, "");
		getFilePrefs().remove(fname);
	}
	
	private JSONArray removePlan(String planname) throws OdenException, JSONException {
		JSONArray fnames = new JSONArray();
		// remove related files
		for(String fname : getFilePrefs().keys()){
			Cmd filecmd = getFileCmd(fname);
			if(planname.equals(filecmd.getOptionArg(PLAN_OPT))){
				removeFile(fname);
				fnames.put(fname);
			}
		}
		
		getPlanPrefs().remove(planname);
		
		return fnames;
	}

	private void addPlan(String planName, String args, String user) throws OdenException, JSONException {
		// exist?
		if(getPlanPrefs().get(planName).length() > 0)
			removePlan(planName);
		
		// valid?
		Cmd infocmd = new Cmd(planName, args);
		String destArgs = infocmd.getOptionArg(DEST_OPT);
		AgentLoc destAgent = new AgentLoc(destArgs, configService);
		String srcArgs = infocmd.getOptionArg(SOURCE_OPT);
		if(!srcArgs.startsWith("/"))
			throw new OdenException("Source option should be started with '/'.");
		new AgentLoc(destAgent.agentName() + srcArgs, configService);
		
		// user
		String userop = "";
		if(user != null && user.length() > 0)
			userop = " -" + Cmd.USER_OPT + " \"" + user + "\"";
		
		getPlanPrefs().put(planName, args + userop + 
				" -" + DATE_OPT[0] + " \"" + DateUtil.toStringDate(System.currentTimeMillis()) + "\"");
	}

	private JSONArray doFileListActionJ() throws OdenException, JSONException {
		JSONArray arr = new JSONArray();
		for(String name : getFilePrefs().keys()){
			arr.put(doFileInfoActionJ(name).getJSONObject(0));
		}
		return arr;
	}	
	
	private JSONArray doPlanListActionJ() throws OdenException, JSONException {
		JSONArray arr = new JSONArray();
		for(String name : getPlanPrefs().keys()){
			arr.put(doPlanInfoActionJ(name).getJSONObject(0));
		}
		return arr;
	}	
	
	private JSONArray doPlanInfoActionJ(String plan) {
		JSONArray arr = new JSONArray();
		try {
			String info = getPlanPrefs().get(plan);
			if(info.length() > 0)
				arr.put(new JSONObject().put(plan, info));
		} catch (JSONException e) {
			return null;
		}
		return arr;
	}
	
	private JSONArray doFileInfoActionJ(String file) {
		JSONArray arr = new JSONArray();
		try {
			String info = getFilePrefs().get(file);
			if(info.length() > 0)
				arr.put(new JSONObject().put(file, info));
		} catch (JSONException e) {
			return null;
		}
		return arr;
	}
	
	private void doSnapshot(String planName, String user) throws OdenException {
		Cmd planCmd = getPlanCmd(planName);
		String destArgs = planCmd.getOptionArg(DEST_OPT);
		AgentLoc agent = new AgentLoc(destArgs, configService);

		String srcArgs = planCmd.getOptionArg(SOURCE_OPT);
		String src = new AgentLoc(agent.agentName() + 
				(srcArgs.startsWith("/") ? srcArgs : "/" + srcArgs) , configService).location();
		
		FileInfo info = delegateService.snapshot(
				src, agent.agentAddr(), agent.location(), user);
	
		String date = DateUtil.toStringDate(info.lastModified());
		getFilePrefs().put(info.getPath(), "-size " + String.valueOf(info.size()) + 
				" -plan \"" + planName + "\"" + " -date \"" + date + "\"");
	}

	private Map validateSourceNDest(Cmd infocmd) throws OdenException {
		String destArgs = infocmd.getOptionArg(DEST_OPT);
		AgentLoc destAgent = new AgentLoc(destArgs, configService);
		String srcArgs = infocmd.getOptionArg(SOURCE_OPT);
		AgentLoc srcAgent = new AgentLoc(destAgent.agentName() + 
				(srcArgs.startsWith("/") ? srcArgs : "/" + srcArgs) , configService);
		
		if(!delegateService.availableAgent(destAgent))
			throw new OdenException("Couldn't access the agent: " + infocmd.getOptionArg(DEST_OPT));
		if(!delegateService.availableAgent(srcAgent))
			throw new OdenException("Couldn't access the agent: " + infocmd.getOptionArg(SOURCE_OPT));
		
		Map planDetail = new TreeMap();
		planDetail.put("agent", srcAgent.agentAddr());
		planDetail.put("source", srcAgent.location());
		planDetail.put("dest", destAgent.location());
		return planDetail;
	}
	
	public String getName() {
		return "snapshot";
	}

	public String getShortDescription() {
		return "manipulate snapshot-plans and snapshot-files";
	}

	public String getUsage() {
		return getName() + " " + Cmd.HELP_ACTION;
	}
	
	public String getFullUsage() {
		return getName() + " " + Cmd.RUN_ACTION + " <plan-name>" + "\n" +
				getName() + " " + Cmd.ADD_ACTION + " <plan-name> " +
				"\n\t-s[ource]" + " /[<location-variable-name>] " + 
				"\n\t-d[est]" + " <agent-name>[/<location-variable-name>] " +
				"\n\t[-desc" + " <description>]" + "\n" +
				getName() + " " + Cmd.INFO_ACTION + " " + 
				"\n\t-p[lan]" + " [<plan-name>] | " + 
				"-f[ile]" + " [<file-name>]" + "\n" +
				getName() + " " + Cmd.REMOVE_ACTION + " " + 
				"\n\t-p[lan]" + " <plan-name> | " + 
				"-f[ile]" + " <file-name>" + "\n" +
				getName() + " " + TEST_ACTION + " <plan-name>";
	}	
	
	public String getPlanNameForFile(String file) throws OdenException{
		Cmd cmd = getFileCmd(file);
		return cmd.getOptionArg(PLAN_OPT);
	}	

	private Cmd getPlanCmd(String name) throws OdenException {
		return toInfoCmd(SnapshotConstants.PLAN_NODE, name);
	}
	
	private Cmd getFileCmd(String name) throws OdenException {
		return toInfoCmd(SnapshotConstants.FILE_NODE, name);
	}

	private Prefs getFilePrefs() {
		return getPrefs(SnapshotConstants.FILE_NODE);
	}
	
	private Prefs getPlanPrefs() {
		return getPrefs(SnapshotConstants.PLAN_NODE);
	}

}
