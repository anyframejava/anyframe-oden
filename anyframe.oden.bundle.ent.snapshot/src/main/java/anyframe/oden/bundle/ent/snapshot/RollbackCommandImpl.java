/*
 * Copyright 2009, 2010 SAMSUNG SDS Co., Ltd. All rights reserved.
 *
 * No part of this "source code" may be reproduced, stored in a retrieval
 * system, or transmitted, in any form or by any means, mechanical,
 * electronic, photocopying, recording, or otherwise, without prior written
 * permission of SAMSUNG SDS Co., Ltd., with the following exceptions:
 * Any person is hereby authorized to store "source code" on a single
 * computer for personal use only and to print copies of "source code"
 * for personal use provided that the "source code" contains SAMSUNG SDS's
 * copyright notice.
 *
 * No licenses, express or implied, are granted with respect to any of
 * the technology described in this "source code". SAMSUNG SDS retains all
 * intellectual property rights associated with the technology described
 * in this "source code".
 *
 */
package anyframe.oden.bundle.ent.snapshot;

import java.io.PrintStream;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import anyframe.oden.bundle.common.ArraySet;
import anyframe.oden.bundle.common.Logger;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.Utils;
import anyframe.oden.bundle.core.AgentLoc;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.Repository;
import anyframe.oden.bundle.core.DeployFile.Mode;
import anyframe.oden.bundle.core.command.Cmd;
import anyframe.oden.bundle.core.command.JSONUtil;
import anyframe.oden.bundle.core.command.OdenCommand;
import anyframe.oden.bundle.core.job.DeployFileResolver;
import anyframe.oden.bundle.core.job.DeployJob;
import anyframe.oden.bundle.core.job.Job;
import anyframe.oden.bundle.core.job.JobManager;
import anyframe.oden.bundle.core.prefs.Prefs;
import anyframe.oden.bundle.core.record.RecordElement2;
import anyframe.oden.bundle.core.txmitter.DeployerHelper;
import anyframe.oden.bundle.deploy.DeployerService;

/**
 * Oden shell command to rollback Oden's snapshot.
 * 
 * @author joon1k
 *
 */
public class RollbackCommandImpl extends OdenCommand {	
	private BundleContext context;
	
	protected void activate(ComponentContext context){
		this.context = context.getBundleContext();
	}
	
	protected JobManager jobManager;
	
	protected void setJobManager(JobManager jm){
		this.jobManager = jm;
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
		AgentLoc src = new AgentLoc(dest.agentName(), dest.agentAddr(), bak);
		
		return rollback(src, fname, dest, user);
	}

	private String rollback(final AgentLoc src, final String file, final AgentLoc dest, String user) 
			throws OdenException {
		
		DeployFileResolver resolver = new DeployFileResolver() {
			public Set<DeployFile> resolveDeployFiles() throws OdenException {
				return new ArraySet<DeployFile>();
			}
		};
		
		Job j = new DeployJob(context, user, "rollback run " + file, resolver) {	
			String errorMessage;
			
			@Override
			protected void run() {
				DeployFile snapshot = new DeployFile(new Repository(src), file, dest, 0L, 0L, Mode.NA);
				String addr = src.agentAddr();
				String parent = src.location();
				try{
					DeployerService ds = txmitterService.getDeployer(addr);
					if(ds == null)
						throw new OdenException("Couldn't connect to the agent: " + addr);
					
					if(ds.exist(parent, snapshot.getPath()) ){
						if(stop)
							throw new OdenException("stopped.");
						if(!DeployerHelper.removeDir(ds, id, deployFiles, 
									snapshot.getAgent(), deployerManager.backupLocation(snapshot)))
							throw new OdenException("Fail to initialize target dir: " + snapshot.getAgent().location());
						
						if(stop)
							throw new OdenException("stopped.");
						DeployerHelper.restore(ds, id, deployFiles, snapshot);
						if(stop)
							throw new OdenException("stopped.");
					}
				}catch(Exception e){
					errorMessage = Utils.rootCause(e);
				}
			}
			
			@Override
			public void cancel() {
				super.cancel();
				if(this.status == Job.RUNNING){
					try {
						DeployerService ds = txmitterService.getDeployer(src.agentAddr());
						if(ds != null)
							ds.stop(id);
					} catch (Exception e) {
					}
				}
			}
			
			@Override
			protected void done(){
				try {
					RecordElement2 r = new RecordElement2(id, deployFiles, user, System.currentTimeMillis(), desc);
					if(errorMessage != null){
						r.setLog(errorMessage);
						r.setSucccess(false);
					}
					deploylog.record(r);
				} catch (OdenException e) {
					Logger.error(e);
				}
			}
			
		};
		jobManager.schedule(j);
		return j.id();
	}
	
	public String getName() {
		return "rollback";
	}

	public String getShortDescription() {
		return "restore files from backup";
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
