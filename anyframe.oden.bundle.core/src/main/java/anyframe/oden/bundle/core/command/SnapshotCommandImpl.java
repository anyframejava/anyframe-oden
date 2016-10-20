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

import anyframe.common.bundle.log.Logger;
import anyframe.oden.bundle.common.ArraySet;
import anyframe.oden.bundle.common.DateUtil;
import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.JSONUtil;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.OdenStoreException;
import anyframe.oden.bundle.core.AgentLoc;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.Repository;
import anyframe.oden.bundle.core.DeployFile.Mode;
import anyframe.oden.bundle.core.job.DeployJob;
import anyframe.oden.bundle.core.job.Job;
import anyframe.oden.bundle.core.record.DeployLogService2;
import anyframe.oden.bundle.core.record.RecordElement2;
import anyframe.oden.bundle.core.txmitter.TransmitterService;
import anyframe.oden.bundle.prefs.Prefs;

/**
 * @author joon1k
 *
 */
public class SnapshotCommandImpl extends OdenCommand {
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
		Job j = new Job(context) {
			@Override
			protected void run() throws Exception {
				
				Iterator<String> it = fnames.iterator();
				while(!stop && it.hasNext()){
					// get agent info which having snapshot file to remove
					String fname = it.next();
					String planName = getPlanNameForFile(fname);
					Cmd planCmd = getPlanCmd(planName);
					AgentLoc snapshotLocation = null;
					try{
						AgentLoc orignalSourceLocation = new AgentLoc(planCmd.getOptionArg(SOURCE_OPT), configService); 
						snapshotLocation = new AgentLoc(orignalSourceLocation.agentName(),
								orignalSourceLocation.agentAddr(),
								configService.getBackupLocation(orignalSourceLocation.agentName()));
					}catch(Exception e){
						Logger.error(e);
					}
					
					// get remove file info
					Cmd finfo = getFileCmd(fname);
					String fsize = finfo.getOptionArg(new String[]{"size"});
					String fdate = finfo.getOptionArg(new String[]{"date"});
					DeployFile f = new DeployFile(
							new Repository(new String[0]), 
							fname, snapshotLocation, null, Long.valueOf(fsize), DateUtil.toLongDate(fdate), Mode.NA);
					removedFiles.add(f);
					
					try{	
						txmitterService.removeSnapshot(snapshotLocation.agentAddr(), snapshotLocation.location(), fname);
						getFilePrefs().remove(fname);
						f.setSuccess(true);
					}catch(Exception e){
						Logger.error(e);
					}
				}
			}
			
			@Override
			protected void done(Exception e) {
				boolean success = true;
				if(e != null){
					success = false;
					Logger.error(e);
				}
				
				try {
					deploylog.record(new RecordElement2(id, removedFiles, user, 
							System.currentTimeMillis(), success, desc));
				} catch (OdenException e1) {
					Logger.error(e1);
				}
			}
		};
		
		StringBuffer buf = new StringBuffer("snapshot del ");
		for(String fname : fnames)
			buf.append(fname + " ");
		j.schedule(buf.toString());
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
		AgentLoc srcloc = new AgentLoc(srcArgs, configService);
		String bak = configService.getBackupLocation(srcloc.agentName());
		if(bak == null)
			throw new OdenException("Couldn't find any backup location from config.xml");

		Set<DeployFile> deploys = new ArraySet<DeployFile>();
		deploys.add( new DeployFile(
				new Repository(srcloc), 
				"", 
				new AgentLoc(srcloc.agentName(), srcloc.agentAddr(), bak),
				null, 0L, 0L, Mode.NA, false));
		
		Job j = new DeployJob(context, deploys, user) {

			@Override
			protected void run() throws Exception {				
				DeployFile d = deployFiles.iterator().next();
				String[] repo = d.getRepo().args();
				FileInfo info = txmitterService.backup(repo[0], repo[1], 
						configService.getBackupLocation(d.getAgent().agentName()) );
				d.setPath(info.getPath());
				d.setDate(info.lastModified());
				d.setSize(info.size());
				d.setSuccess(true);
			}
			
			@Override
			protected void done(Exception e) {
				DeployFile d = deployFiles.iterator().next();
				if(d.isSuccess()){
					try {
						String date = DateUtil.toStringDate(d.getDate());
						getFilePrefs().put(d.getPath(), "-size " + String.valueOf(d.getSize()) + 
								" -plan \"" + planName + "\"" + " -date \"" + date + "\"");
					} catch (OdenStoreException e1) {
						d.setSuccess(false);
						Logger.error(e1);
					}
				}
				
				super.done(e);
			}
		};
		j.schedule("snapshot run " + planName);
		return j.id();
	}

	private Map validateSourceNDest(Cmd infocmd) throws OdenException {
		// valid source location ?
		String srcArgs = infocmd.getOptionArg(SOURCE_OPT);
		AgentLoc srcloc = new AgentLoc(srcArgs, configService);
		if(!txmitterService.available(srcloc.agentAddr()))
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
