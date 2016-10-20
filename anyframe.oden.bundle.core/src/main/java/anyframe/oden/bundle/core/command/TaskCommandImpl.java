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
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
import org.ungoverned.osgi.service.shell.Command;

import anyframe.common.bundle.log.Logger;
import anyframe.oden.bundle.common.ArraySet;
import anyframe.oden.bundle.common.JSONUtil;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.OdenParseException;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.DeployFileUtil;
import anyframe.oden.bundle.core.RepositoryProviderService;
import anyframe.oden.bundle.core.job.Job;
import anyframe.oden.bundle.core.job.JobManager;
import anyframe.oden.bundle.core.record.DeployLogService2;
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
	
	
	private BundleContext context;
	
	protected void activate(ComponentContext context){
		this.context = context.getBundleContext();
	}
	
	protected JobManager jobManager;
	
	protected void setJobManager(JobManager jm){
		this.jobManager = jm;
	}
	
	protected PolicyCommandImpl policyCommand;
	
	protected void setPolicyCommand(Command cmd){
		if(cmd instanceof PolicyCommandImpl)
			this.policyCommand = (PolicyCommandImpl) cmd;
	}
	
	protected RepositoryProviderService repositoryProvider;
	
	protected void setRepositoryProvider(RepositoryProviderService r){
		this.repositoryProvider = r;
	}
	
	private DeployLogService2 deploylog;
	
	protected void setDeployLogService(DeployLogService2 deploylog) {
		this.deploylog = deploylog;
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
				
				Set<DeployFile> dfiles = preview(task);
				String txid = deploy(dfiles, task, extractUserName(cmd));
				if(isJSON)
					ja.put(new JSONObject().put("txid", txid));
				else
					consoleResult = "Task is scheduled. Transaction id is: " + txid;
			}else if(TEST_ACTION.equals(action)){
				if(cmd.getActionArg().length() < 1)
					throw new OdenException("Couldn't execute command.");
				
				String task = cmd.getActionArg();
				
				if(doInfoAction(task).length() == 0) 
					throw new OdenException("Couldn't find a task: " + cmd.getActionArg());
				
				Set<DeployFile> dfiles = preview(task);
				if(isJSON){
					ja = (JSONArray) JSONUtil.jsonize(dfiles);
				} else {
					StringBuffer buf = new StringBuffer();
					for(DeployFile dfile : dfiles){
						buf.append(DeployFileUtil.modeToString(dfile.mode()) +  ": " + 
								dfile.getRepo().toString() + " " + dfile.getPath() + 
								" >> " + dfile.getAgent().agentName() + "\n");
					}
					consoleResult = buf.toString();
				}
			}else if(action.length() == 0 || Cmd.HELP_ACTION.equals(action)){
				consoleResult = getFullUsage();
			}else {
				throw new OdenException("Couldn't execute that action: " + action);
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
		
	private String deploy(final Set<DeployFile> dfiles, String taskName, final String user) throws OdenException {
		Job j = new TaskDeployJob(context, dfiles, user);
		j.schedule("task run " + taskName);	
		return j.id();	// txid
	}

	private Set<DeployFile> preview(String taskName) throws OdenException{
		Cmd cmd = infoCmd(taskName);
		Opt op = cmd.getOption(POLICY_OPT);
		if(op == null) 
			throw new OdenParseException(cmd.toString());
		
		Set<DeployFile> dfiles = new ArraySet<DeployFile>();
		
		String[] policies = op.getArgArray();
		for(String policy : policies){
			if(!existPolicy(policy))
				throw new OdenException("Couldn't find a policy: " + policy);
			
			Cmd policyInfo = policyCommand.infoCmd(policy);
			policyCommand.preview(dfiles, policyInfo);
		}
		return dfiles;
	}
	
	private boolean existPolicy(String policyname) {
		if(policyCommand == null)
			return false;
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
