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

import anyframe.common.bundle.log.Logger;
import anyframe.oden.bundle.common.ArraySet;
import anyframe.oden.bundle.common.FileUtil;
import anyframe.oden.bundle.common.JSONUtil;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.AgentLoc;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.Repository;
import anyframe.oden.bundle.core.DeployFile.Mode;
import anyframe.oden.bundle.core.job.DeployJob;
import anyframe.oden.bundle.core.job.Job;
import anyframe.oden.bundle.core.record.DeployLogService2;
import anyframe.oden.bundle.core.txmitter.TransmitterService;
import anyframe.oden.bundle.prefs.Prefs;

/**
 * 
 * @author joon1k
 *
 */
public class RollbackCommandImpl extends OdenCommand {	
	private BundleContext context;
	
	protected void activate(ComponentContext context){
		this.context = context.getBundleContext();
	}
	
	private DeployLogService2 deploylog;
	
	protected void setDeployLogService(DeployLogService2 deploylog) {
		this.deploylog = deploylog;
	}
	
	private TransmitterService txmitterService;
	
	protected void setTransmitterService(TransmitterService tx){
		this.txmitterService = tx;
	}
	
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
						String txid = doRollback(fname, planName, extractUserName(cmd)); 
						if(isJSON)
							ja.put(new JSONObject().put("txid", txid));
						else
							consoleResult = "Rollback is scheduled. Transaction id: " + txid;
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
	
	private String doRollback(String fname, String planName, String user) throws OdenException {
		if(planName.length() == 0)
			throw new OdenException("Couldn't find a plan info for the " + planName);
		
		Cmd planCmd = planInfo(planName);
		if(planCmd == null)
			throw new OdenException("Couldn't find a plan: " + planName);
		
		String srcArgs = planCmd.getOptionArg(SnapshotCommandImpl.SOURCE_OPT);
		// Because this is rollback action, dest is src & src is dest.
		AgentLoc dest = new AgentLoc(srcArgs, configService);
		String bak = configService.getBackupLocation(dest.agentName()); 
		if(bak == null)
			throw new OdenException("Couldn't fina a backup location from config.xml.");
		AgentLoc src = new AgentLoc(dest.agentName(), dest.agentAddr(), bak);
		
		return rollback(src, fname, dest, user);
	}

	private String rollback(AgentLoc src, String file, AgentLoc dest, String user) 
			throws OdenException {
		final DeployFile toRollback = new DeployFile(
				new Repository(src), file, dest, 0L, 0L, Mode.NA);
		
		Set<DeployFile> fs = new ArraySet<DeployFile>();
		Job j = new DeployJob(context, fs, user) {
			
			@Override
			protected void run() throws Exception {
				toRollback.setBackupLocation(FileUtil.combinePath(
						configService.getBackupLocation(toRollback.getAgent().agentName()), id));
				String addr = toRollback.getRepo().args()[0];
				String parent = toRollback.getRepo().args()[1];
				String path = toRollback.getPath();
				if(txmitterService.exist(addr, parent, path) &&
						txmitterService.removeDir(deployFiles, toRollback))
					txmitterService.restore(deployFiles, toRollback);
			}
			
		};
		j.schedule("rollback run " + file);
		return j.id();
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
