/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
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
package anyframe.oden.bundle.core.command;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.log.LogService;
import org.ungoverned.osgi.service.shell.Command;

import anyframe.oden.bundle.common.JSONUtil;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.OdenParseException;
import anyframe.oden.bundle.core.FileMap;
import anyframe.oden.bundle.core.Logger;
import anyframe.oden.bundle.core.Policy;
import anyframe.oden.bundle.prefs.Prefs;

/**
 * Oden shell command to manipulate Oden's Task.
 * 
 * @author joon1k
 *
 */
public class TaskCommandImpl extends OdenCommand {	
	public final static String TASK_NODE = "task";
	
	public final static String TEST_ACTION = "test";
	
	public final static String[] DESC_OPT = {"desc"};
	
	public final static String[] POLICY_OPT = {"policy", "p"};
	
	protected PolicyCommandImpl policyCommand;
	
	protected void setPolicyCommand(Command cmd){
		if(cmd instanceof PolicyCommandImpl)
			this.policyCommand = (PolicyCommandImpl) cmd;
	}
	
	protected void unsetPolicyCommand(Command cmd){
		this.policyCommand = null;
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
				String task = cmd.getActionArg();
				if(task.length() == 0){
					ja = doListActionJ();
				}else { 
					ja = doInfoActionJ(task);
					if(ja.length() == 0)
						throw new OdenException("Couldn't find a task: " + task);
				}
			}else if(Cmd.ADD_ACTION.equals(action)){
				if(cmd.getActionArg().length() < 1 || cmd.getOptions().size() < 1)
					throw new OdenException("Couldn't execute command.");
					
				// valid policy?
				String[] policynames = cmd.getOptionArgArray(POLICY_OPT);
				if(policynames == null || policynames.length == 0)
					throw new OdenException("Couldn't find any -p option.");
				
				for(String policyname : policynames){
					if(!existPolicy(policyname))
						throw new OdenException("Couldn't find a policy: " + policyname);
				}

				addTask(cmd.getActionArg(), cmd.getOptionString());
				consoleResult = cmd.getActionArg() + " is added.";
			}else if(Cmd.REMOVE_ACTION.equals(action)){
				if(cmd.getActionArg().length() < 1)
					throw new OdenException("Couldn't execute command.");
				
				String task = cmd.getActionArg();
				
				if(doInfoAction(task).length() == 0) 
					throw new OdenException("Couldn't find a task: " + cmd.getActionArg());
				
				removeTask(task);
				consoleResult = task + " is removed.";
			}else if(Cmd.RUN_ACTION.equals(action)){
				if(cmd.getActionArg().length() < 1)
					throw new OdenException("Couldn't execute command.");
				
				String task = cmd.getActionArg();
				
				if(doInfoAction(task).length() == 0) 
					throw new OdenException("Couldn't find a task: " + cmd.getActionArg());
				
				String txid = doRunAction(task, extractUserName(cmd), isJSON ? new PrintStream(System.out) : out);
				if(isJSON)
					ja.put(new JSONObject().put("txid", txid));
				else
					consoleResult = "Task is finished. Transaction id: " + txid;
			}else if(TEST_ACTION.equals(action)){
				if(cmd.getActionArg().length() < 1)
					throw new OdenException("Couldn't execute command.");
				
				String task = cmd.getActionArg();
				
				if(doInfoAction(task).length() == 0) 
					throw new OdenException("Couldn't find a task: " + cmd.getActionArg());
				
				ja = doTestAction(task);
				if(!isJSON)
					consoleResult = formatTest(ja);
			}else if(action.length() == 0 || Cmd.HELP_ACTION.equals(action)){
				consoleResult = getFullUsage();
			}else {
				throw new OdenException("Couldn't the run specified action: " + action);
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

	private String formatTest(JSONArray ja) throws JSONException {
		StringBuffer buf = new StringBuffer();
		for(int i=0; i< ja.length(); i++){
			JSONObject repos = ja.getJSONObject(i);
			for(Iterator<String> it = repos.keys(); it.hasNext();){
				String repo = it.next();
				buf.append("REPOSITORY: " + repo + "\nFILES:\n");
				JSONObject files = repos.getJSONObject(repo);
				for(Iterator<String> it2 = files.keys(); it2.hasNext();){
					String file = it2.next();
					buf.append("\t"+ file + " >> " + JSONUtil.toString(files.getJSONArray(file)));
				}
			}
		}
		return buf.toString();
	}
	
	private void removeTask(String taskName) throws OdenException {
		getTaskPrefs().remove(taskName);
	}
	
	private void addTask(String task, String args) throws OdenException {
		getTaskPrefs().put(task, args);
	}

	private JSONArray doInfoActionJ(String taskName) {
		JSONArray arr = new JSONArray();
		try {
			String info = getTaskPrefs().get(taskName);
			if(info.length() > 0)
				arr.put(new JSONObject().put(taskName, info));
		} catch (JSONException e) {
			return null;
		}
		return arr;
	}
	
	private String doInfoAction(String taskName){
		return getTaskPrefs().get(taskName);
	}

	private JSONArray doListActionJ() throws OdenException, JSONException {
		JSONArray arr = new JSONArray();
		for(String name : getTaskPrefs().keys()){
			arr.put(doInfoActionJ(name).getJSONObject(0));
		} 
		return arr;
	}	
	
	private String doRunAction(String taskName, String user, PrintStream out) 
			throws OdenException {
		Map<List<String>, FileMap> repomap = preview(taskName);
		
		// check update. To deploy as update mode, all policies should have update option.
		boolean update = true;
		Cmd cmd = infoCmd(taskName);
		Opt op = cmd.getOption(POLICY_OPT);
		for(String policy : op.getArgArray()){
			Cmd policyInfo = policyCommand.infoCmd(policy);
			boolean pUpdate = policyInfo.getOption(PolicyCommandImpl.UPDATE_OPT) != null;
			update = update & pUpdate; 
		}
		return delegateService.deployAll(repomap, update, user, out);
	}

	private Map<List<String>, FileMap> preview(String taskName) throws OdenException{
		Cmd cmd = infoCmd(taskName);
		Opt op = cmd.getOption(POLICY_OPT);
		if(op == null) 
			throw new OdenParseException(cmd.toString());
		
		Map<List<String>, FileMap> repomap = new HashMap<List<String>, FileMap>();
		
		String[] policies = op.getArgArray();
		for(String policy : policies){
			if(!existPolicy(policy))
				throw new OdenException("Couldn't find a policy: " + policy);
			
			Cmd policyInfo = policyCommand.infoCmd(policy);
			policyCommand.preview(repomap, policyInfo);
		}
		return repomap;
	}
	
	private JSONArray doTestAction(String taskName) throws OdenException{		
		Map<List<String>, FileMap> repomap = preview(taskName);
		
		JSONArray ja = new JSONArray();
		try {
			ja = JSONUtil.jsonArray(repomap);
		} catch (JSONException e) {
			throw new OdenException(e);
		}		
		return ja;
	}
	
	private boolean existPolicy(String policyname) {
		return policyCommand.getPrefs().get(policyname).length() > 0;
	}

	public String getName() {
		return "task";
	}

	public String getShortDescription() {
		return "manipulate tasks consisted of policies.";
	}

	public String getUsage() {
		return getName() + " " + Cmd.HELP_ACTION;
	}
	
	public String getFullUsage() {
		return getName() + " " + Cmd.INFO_ACTION + " [<task-name>]" + "\n" +
				getName() + " " + Cmd.ADD_ACTION + " <task-name> "+
				"\n\t-p[olicy]" +" <policy-name> ... " + 
				"\n\t[-desc" + " <description>]" + "\n" +
				getName() + " " + Cmd.REMOVE_ACTION + " <task-name>" + "\n" +
				getName() + " " + Cmd.RUN_ACTION + " <task-name>" + "\n" +
				getName() + " " + TEST_ACTION + " <task-name>";
	}

	private Prefs getTaskPrefs(){
		return getPrefs(TASK_NODE);
	}

	private Cmd infoCmd(String name) throws OdenException {
		return toInfoCmd(TASK_NODE, name);
	}

}
