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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import anyframe.oden.bundle.common.ArraySet;
import anyframe.oden.bundle.common.DateUtil;
import anyframe.oden.bundle.common.FileUtil;
import anyframe.oden.bundle.common.JSONUtil;
import anyframe.oden.bundle.common.Logger;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.OdenStoreException;
import anyframe.oden.bundle.common.Utils;
import anyframe.oden.bundle.core.AgentLoc;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.Repository;
import anyframe.oden.bundle.core.DeployFile.Mode;
import anyframe.oden.bundle.core.command.Cmd;
import anyframe.oden.bundle.core.command.OdenCommand;
import anyframe.oden.bundle.core.command.Opt;
import anyframe.oden.bundle.core.job.DeployFileResolver;
import anyframe.oden.bundle.core.job.DeployJob;
import anyframe.oden.bundle.core.job.Job;
import anyframe.oden.bundle.core.job.JobManager;
import anyframe.oden.bundle.core.keygen.KeyGenerator;
import anyframe.oden.bundle.core.prefs.Prefs;
import anyframe.oden.bundle.core.record.DeployLogService2;
import anyframe.oden.bundle.core.record.RecordElement2;
import anyframe.oden.bundle.core.txmitter.TransmitterService;
import anyframe.oden.bundle.deploy.DeployerService;
import anyframe.oden.bundle.deploy.DoneFileInfo;

/**
 * Oden shell command to get snapshot.
 * 
 * @author joon1k
 *
 */
public class SnapshotCommandImpl extends OdenCommand {
	public final static String SNAPSHOT_PREFIX = "ss";
	
	public final static String TEST_ACTION = "test";
		
	public final static String[] SOURCE_OPT = {"source", "s"};
	
	public final static String[] PLAN_OPT = {"plan", "p"};
	
	public final static String[] FILE_OPT = {"file", "f"};
	
	public final static String[] DESC_OPT = {"desc"};
	
	public final static String[] DATE_OPT = {"date"};
	
	public final static String FILE_ACTION = "file";
	
	public final static String PLAN_ACTION = "plan";
		
	
	private BundleContext context;
	
	protected void activate(ComponentContext context){
		this.context = context.getBundleContext();
	}
	
	
	private TransmitterService txmitterService;
	
	protected void setTransmitterService(TransmitterService tx){
		this.txmitterService = tx;
	}
	
	private DeployLogService2 deploylog;
	
	protected void setDeployLogService(DeployLogService2 deploylog) {
		this.deploylog = deploylog;
	}
	
	protected JobManager jobManager;
	
	protected void setJobManager(JobManager jm){
		this.jobManager = jm;
	}
	
	protected KeyGenerator keygen;
	
	protected void setKeyGenerator(KeyGenerator keygen){
		this.keygen = keygen;
	}
	
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
				List<String> fnames = cmd.getOptionArgList(FILE_OPT);
				if(planName.length() > 0){			// remove plan
					if(doPlanInfoActionJ(planName).length() > 0){
						removePlan(planName);
						consoleResult = planName + " is removed.";	
					}else {
						throw new OdenException("Couldn't find a plan: " + planName);
					}
				}else if(fnames.size() > 0){			// remove file
					for(String fname : fnames)		// exist file?
						if(doFileInfoActionJ(fname).length() == 0)
							throw new OdenException("Couldn't find that file: " + fname);
					
					removeFile(fnames, extractUserName(cmd));
					consoleResult = "Removing Snapshot is scheduled.";	
				}else
					throw new OdenException("Illegal arguments error.");
			}else if(Cmd.RUN_ACTION.equals(action)){
				String planName = cmd.getActionArg();
				if(planName.length() > 0) 
					if(doPlanInfoActionJ(planName).length() == 0) 
						throw new OdenException("Couldn't find that plan: " + planName);
					else{
						Cmd infocmd = getPlanCmd(planName);
						validateSourceNDest(infocmd);
						String txid = doSnapshot(planName, extractUserName(cmd));
						if(isJSON){
							ja = new JSONArray().put(new JSONObject()
									.put("txid", txid));
						} else {
							consoleResult = "Backup is scheduled. Transaction id: "  + txid;
						}
					}
				else
					throw new OdenException("Couldn't execute command.");
			}else if(Cmd.ADD_ACTION.equals(action)){
				if(cmd.getActionArg().length() > 0 && cmd.getOptions().size() > 0){
					String planName = cmd.getActionArg();
					String args = cmd.getOptionString();
					validateSourceNDest(new Cmd("c a \"" + planName + "\" " + args));
					addPlan(planName, args, extractUserName(cmd));
					consoleResult = cmd.getActionArg() + " is added.";
				}else 
					throw new OdenException("Couldn't add a plan."); 
			}else if(TEST_ACTION.equals(action)){
				String planName = cmd.getActionArg(); 
				if(planName.length() <= 1)
					throw new OdenException("Couldn't execute command.");
				
				Cmd infocmd = getPlanCmd(planName);
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

	private String removeFile(final List<String> fnames, final String user) throws OdenException {
		final Set<DeployFile> removedFiles = new ArraySet<DeployFile>(); 
		
		StringBuffer buf = new StringBuffer("snapshot del ");
		for(String fname : fnames)
			buf.append(fname + " ");
		
		Job j = new Job(context, buf.toString()) {
			@Override
			protected void run() {
				
				Iterator<String> it = fnames.iterator();
				while(!stop && it.hasNext()){
					// get agent info which having snapshot file to remove
					String fname = it.next();
					DeployFile f = new DeployFile(
							new Repository(new String[0]), 
							fname, null, 0L, 0L, Mode.NA);
					removedFiles.add(f);
					try{
						String planName = getPlanNameForFile(fname);
						Cmd planCmd = getPlanCmd(planName);
						AgentLoc orignalSourceLocation = new AgentLoc(planCmd.getOptionArg(SOURCE_OPT), configService); 
						AgentLoc snapshotLocation = new AgentLoc(orignalSourceLocation.agentName(),
								orignalSourceLocation.agentAddr(),
								configService.getBackupLocation(orignalSourceLocation.agentName()));
						
						// get remove file info
						Cmd finfo = getFileCmd(fname);
						String fsize = finfo.getOptionArg(new String[]{"size"});
						String fdate = finfo.getOptionArg(new String[]{"date"});
						
						f.setAgent(snapshotLocation);
						f.setSize(Long.valueOf(fsize));
						f.setDate(DateUtil.toLongDate(fdate));
					
						DeployerService ds = txmitterService.getDeployer(snapshotLocation.agentAddr());
						if(ds == null)
							throw new OdenException("Couldn't connect to the agent: " + snapshotLocation.agentAddr());
						ds.removeFile(snapshotLocation.location(), fname);
						getFilePrefs().remove(fname);
						f.setSuccess(true);
					}catch(Exception e){
						f.setErrorLog(Utils.rootCause(e));
						Logger.error(e);
					}
				}
			}
			
			@Override
			protected void done() {
				try {
					deploylog.record(new RecordElement2(id, removedFiles, user, 
							System.currentTimeMillis(), true, "", desc));
				} catch (OdenException e1) {
					Logger.error(e1);
				}
			}
		};
		jobManager.schedule(j);
		return j.id();
	}
	
	private void removePlan(String planName) throws OdenException {
		if(hasRelatedFiles(planName))
			throw new OdenException("Fail to remove plan. Referenced Snapshots should be removed.");
		
		getPlanPrefs().remove(planName);
	}
	
	private boolean hasRelatedFiles(String planName) throws OdenException{
		for(String fname : getFilePrefs().keys()){
			Cmd fileInfo = getFileCmd(fname);
			if(planName.equals(fileInfo.getOptionArg(PLAN_OPT))){
				return true;
			}
		}
		return false;
	}

	private void addPlan(String planName, String args, String user) throws OdenException, JSONException {
		// exist?
		if(getPlanPrefs().get(planName).length() > 0)
			removePlan(planName);
		
		// user
		String userop = "";
		Cmd infocmd = new Cmd("c a \"" + planName + "\" " + args);
		if(infocmd.getOption(Cmd.USER_OPT) == null)
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
	
	private String doSnapshot(final String planName, String user) throws OdenException {
		Cmd planCmd = getPlanCmd(planName);
		String srcArgs = planCmd.getOptionArg(SOURCE_OPT);
		final AgentLoc srcloc = new AgentLoc(srcArgs, configService);
		final String bak = configService.getBackupLocation(srcloc.agentName());
		if(bak == null)
			throw new OdenException("Couldn't find any backup location from config.xml");
		
		DeployFileResolver resolver = new DeployFileResolver() {
			public Set<DeployFile> resolveDeployFiles() throws OdenException {
				Set<DeployFile> deploys = new ArraySet<DeployFile>();
				deploys.add( new DeployFile(
						new Repository(srcloc), 
						"", 
						new AgentLoc(srcloc.agentName(), srcloc.agentAddr(), bak),
						0L, 0L, Mode.NA, false));
				return deploys;
			}
		};
		
		Job j = new DeployJob(context, user, "snapshot run " + planName, resolver) {
			DeployFile d;
			
			@Override
			protected void run() {				
				d = deployFiles.iterator().next();
				
				String[] repo = d.getRepo().args();
				try{
					DeployerService ds = txmitterService.getDeployer(repo[0]);
					if(ds == null)
						throw new OdenException("Couldn't connect to the agent: " + repo[0]);
					DoneFileInfo info = ds.compress(id, repo[1], 
							FileUtil.combinePath(d.getAgent().location(),keygen.next(SNAPSHOT_PREFIX)));
					d.setPath(info.getPath());
					d.setDate(info.lastModified());
					d.setSize(info.size());
					d.setSuccess(info.success());
				}catch(Exception e){
					d.setSuccess(false);
					d.setErrorLog(Utils.rootCause(e));
				}
			}

			@Override
			protected void done() {
				if(d.isSuccess()){
					try {
						String date = DateUtil.toStringDate(d.getDate());
						getFilePrefs().put(d.getPath(), "-size " + String.valueOf(d.getSize()) + 
								" -plan \"" + planName + "\"" + " -date \"" + date + "\"");
					} catch (OdenStoreException e1) {
						d.setSuccess(false);
						d.setErrorLog(Utils.rootCause(e1));
						Logger.error(e1);
					}
				}
				
				super.done();
			}
			
			@Override
			public void cancel() {
				super.cancel();
				if(this.status == Job.RUNNING)
					try {
						DeployerService ds = txmitterService.getDeployer(d.getRepo().args()[0]);
						if(ds != null)
							ds.stop(id);
					} catch (Exception e) {
					}
			}
		};
		jobManager.schedule(j);
		return j.id();
	}

	private Map validateSourceNDest(Cmd infocmd) throws OdenException {
		// valid source location ?
		String srcArgs = infocmd.getOptionArg(SOURCE_OPT);
		AgentLoc srcloc = new AgentLoc(srcArgs, configService);
		if(txmitterService.getDeployer(srcloc.agentAddr()) == null)
			throw new OdenException("Couldn't access the agent: " + infocmd.getOptionArg(SOURCE_OPT));

		// backup-location?
		String bakloc = configService.getBackupLocation(srcloc.agentName());
		
		Map planDetail = new TreeMap();
		planDetail.put("agent", srcloc.agentAddr());
		planDetail.put("source", srcloc.location());
		planDetail.put("dest", bakloc);
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
				"\n\t-s[ource]" + " <agent-name>:<$<location-var>[/<path> | ~[/<path>] | <absolute-path>]> " + 
				"\n\t[-desc" + " <description>]" + "\n" +
				getName() + " " + Cmd.INFO_ACTION + " " + 
				"\n\t-p[lan]" + " [<plan-name>] | " + 
				"-f[ile]" + " [<file-name>]" + "\n" +
				getName() + " " + Cmd.REMOVE_ACTION + " " + 
				"\n\t-p[lan]" + " <plan-name> | " + 
				"-f[ile]" + " <file-name> ..." + "\n" +
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
