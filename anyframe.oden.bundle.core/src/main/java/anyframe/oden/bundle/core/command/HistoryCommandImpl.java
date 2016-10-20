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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import anyframe.oden.bundle.common.Assert;
import anyframe.oden.bundle.common.DateUtil;
import anyframe.oden.bundle.common.FileUtil;
import anyframe.oden.bundle.common.Logger;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.Utils;
import anyframe.oden.bundle.core.AgentLoc;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.DeployFileUtil;
import anyframe.oden.bundle.core.Repository;
import anyframe.oden.bundle.core.DeployFile.Mode;
import anyframe.oden.bundle.core.job.DeployFileResolver;
import anyframe.oden.bundle.core.job.DeployJob;
import anyframe.oden.bundle.core.job.Job;
import anyframe.oden.bundle.core.job.JobManager;
import anyframe.oden.bundle.core.record.DeployLogService;
import anyframe.oden.bundle.core.record.MiniRecordElement;
import anyframe.oden.bundle.core.record.RecordElement2;
import anyframe.oden.bundle.core.txmitter.TransmitterService;
import anyframe.oden.bundle.deploy.DeployerService;
import anyframe.oden.bundle.deploy.DoneFileInfo;

/**
 * Oden Shell commands to search deploy log.
 * 
 * @author joon1k
 *
 */
public class HistoryCommandImpl extends OdenCommand {
	private static final String[] AGENT_OP = {"agent", "a"};
	private static final String[] DATE_OP = {"date", "d"};
	private static final String[] PATH_OP = {"path", "p"};
	private static final String[] USER_OP = {"user", "u"};
	public final static String[] SYNC_OPT = {"sync"};
	public final static String[] DETAIL_OPT = {"detail"};
	private static final String[] FAILONLY_OP = {"failonly", "f"};
	private static final String UNDO_ACTION = "undo";
	private static final String REDEPLOY_ACTION = "redeploy";
	
	private BundleContext context;
	
	protected void activate(ComponentContext context){
		this.context = context.getBundleContext();
	}
	
	
	private DeployLogService deploylog;
	
	protected void setDeployLogService(DeployLogService recordsvc) {
		this.deploylog = recordsvc;
	}
	
	private TransmitterService txmitterService;
	
	protected void setTransmitterService(TransmitterService tx){
		this.txmitterService = tx;
	}
	
	protected JobManager jobManager;
	
	protected void setJobManager(JobManager jm){
		this.jobManager = jm;
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
				List<RecordElement2> list = new ArrayList<RecordElement2>();
				RecordElement2 r = deploylog.search(cmd.getActionArg(), 
						cmd.getOptionArg(USER_OP),
						cmd.getOptionArg(AGENT_OP),
						cmd.getOptionArg(PATH_OP),
						cmd.getOption(FAILONLY_OP) != null);
				if(r != null)
					list.add(r);
				if(isJSON){
					ja = jsonize(list);
				} else {
					consoleResult = serialize(list);
				}
			}else if(Cmd.INFO_ACTION.equals(action)){
				String[] date = cmd.getOptionArgArray(DATE_OP);
				
				// show all histories of that date
				List<MiniRecordElement> list = deploylog.search(  
						date.length > 0 ? date[0] : null, date.length > 1 ? date[1] : (date.length > 0 ? date[0] : null),
								null, false);
				if(isJSON){
					for(MiniRecordElement r : list){
						String status = r.isSuccess() ? "S" : "F";
						JSONObject jo = new JSONObject().put("id", r.id()).put("date", r.getDate()).put("status", status).put("desc", r.desc());
						if(cmd.getOption(DETAIL_OPT) != null)
							jo.put("nitems", r.getNDeploys()).put("nsuccess", r.getNDeploys()).put("total", r.getNDeploys());
						ja.put(jo);
					}
				} else {
					StringBuffer buf = new StringBuffer();
					for(MiniRecordElement r : list){
						String status = r.isSuccess() ? "Success" : "Fail";
						buf.append(r.id() + "\t" + DateUtil.toStringDate(r.getDate())
								+ "\t" + status + " (" + r.getNDeploys() + ")\t" + r.desc() + "\n");
					}
					buf.append("To see more details, use this command: history show <id>");
					consoleResult = buf.toString();
				}
			}else if(UNDO_ACTION.equals(action)){
				String id = cmd.getActionArg();
				List<String> paths = cmd.getOptionArgList(PATH_OP);
				String user = extractUserName(cmd); 
				
				String undo = context.getProperty("deploy.undo");
				if(undo == null || !undo.startsWith("true"))
					throw new OdenException("Undo function is not activated. Check 'deploy.undo' property in oden.ini." + undo);
				
				boolean isSync = cmd.getOption(SYNC_OPT) != null;
				String txid = undo(id, paths, isSync, user);
				if(isSync){
					RecordElement2 r = deploylog.search(txid,
							null, null, null, false);
					Assert.check(r != null, "Couldn't find a log: " + txid);
					if(isJSON)
						ja.put(new JSONObject()
								.put("txid", txid)
								.put("status", r.isSuccess() ? "S" : "F")
								.put("count", r.getDeployFiles().size()));
					else
						consoleResult = "Undo is finished. Transaction id: " + txid + 
								(r.isSuccess() ? " Success" : " Fail") + "(" + r.getDeployFiles().size() + ")";
				}else{
					if(isJSON)
						ja.put(new JSONObject().put("txid", txid));
					else
						consoleResult = "Undo is scheduled. Transaction id is: " + txid;
				}
			}else if(REDEPLOY_ACTION.equals(action)){
				String id = cmd.getActionArg();
				String user = extractUserName(cmd); 
				
				boolean isSync = cmd.getOption(SYNC_OPT) != null;
				String txid = redeploy(id, isSync, user);
				if(isSync){
					RecordElement2 r = deploylog.search(txid,
							null, null, null, false);
					Assert.check(r != null, "Couldn't find a log: " + txid);
					if(isJSON)
						ja.put(new JSONObject()
								.put("txid", txid)
								.put("status", r.isSuccess() ? "S" : "F")
								.put("count", r.getDeployFiles().size()));
					else
						consoleResult = "Redeploy is finished. Transaction id: " + txid + 
								(r.isSuccess() ? " Success" : " Fail") + "(" + r.getDeployFiles().size() + ")";
				}else{
					if(isJSON)
						ja.put(new JSONObject().put("txid", txid));
					else
						consoleResult = "Redeploy is scheduled. Transaction id is: " + txid;
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
		System.gc();
	}
	
	private void appendDetails(JSONObject jo, Set<DeployFile> deployFiles) {
		int nsuccess = 0;
		Set<String> items = new HashSet<String>();
		for(DeployFile f : deployFiles){
			items.add(f.getPath());
			if(f.isSuccess())
				nsuccess++;
		}
		try {
			jo.put("nitems", items.size()).put("nsuccess", nsuccess).put("total", deployFiles.size());
		} catch (JSONException e) {
			// ignore
		}
	}

	private String redeploy(final String id, boolean isSync, String user) throws OdenException {
		// composite deploy job & schedule it
		Job j = new TaskDeployJob(context, user,
				getName() + " " + REDEPLOY_ACTION + " " + id,
				new DeployFileResolver() {
					public Set<DeployFile> resolveDeployFiles() throws OdenException {
						// get deployfiles from the history regarding the specified id.
						RecordElement2 r = deploylog.search(id, null, null, null, false);
						Assert.check(r != null, "Fail to find a history for " + id);
						
						// filter deployfiles to get the failed files & their related files.
						return DeployFileUtil.filterToRedeploy(r.getDeployFiles());
					}
				});
		
		if(isSync)
			jobManager.syncRun(j);
		else
			jobManager.schedule(j);
		return j.id();	// txid
	}

	private String undo(String id, List<String> paths, boolean isSync, String user) throws OdenException {
		if(id.length() == 0)
			throw new OdenException("<txid> is required.");
		if( (paths.size() % 2) != 0)
			throw new OdenException("Illegal <path> arguments error.");
		
		Map<AgentLoc, String> m = new HashMap<AgentLoc, String>();
		for(int i=0; i<paths.size(); i++){
			m.put(new AgentLoc(paths.get(i), configService), paths.get(++i));
		}
		return undo(id, m, isSync, user);
	}
	
	private String undo(final String txid, final Map<AgentLoc, String> paths, 
			boolean isSync, String user) throws OdenException {		
		
		DeployFileResolver resolver = new DeployFileResolver() {
			public Set<DeployFile> resolveDeployFiles() throws OdenException {
				return undoFiles(txid, paths);
			}
		};
		
		Job j = new DeployJob(context, user, "history undo", resolver) {
			@Override
			protected void run() {
				Iterator<DeployFile> it = deployFiles.iterator();
				while(!stop && it.hasNext()) {
					DeployFile f = it.next(); 
					try{
						String[] repo = f.getRepo().args();
						AgentLoc parent = f.getAgent();
						String oldbak = repo[1];
						String file = f.getPath();
						String parentLoc = parent.location();
						
						DeployerService ds = txmitterService.getDeployer(parent.agentAddr());
						if(ds == null)
							throw new OdenException("Couldn't connect to the agent: " + parent.agentAddr());
						
						DoneFileInfo d = null;
						if(f.mode() == Mode.ADD || f.mode() == Mode.UPDATE) {
							d = ds.backupNCopy(oldbak, file, parentLoc, deployerManager.backupLocation(f));
							if(d != null) f.setMode(d.isUpdate() ? Mode.UPDATE : Mode.ADD);
						}else if(f.mode() == Mode.DELETE){
							d = ds.backupNRemove(parentLoc, file, deployerManager.backupLocation(f));
						}
						if(d != null){
							f.setSuccess(d.success());
							f.setDate(d.lastModified());
							f.setSize(d.size());
						}
					}catch(Exception e){
						f.setErrorLog(Utils.rootCause(e));
						Logger.error(e);
					}
					
				}
			}
		};
		if(isSync)
			jobManager.syncRun(j);
		else
			jobManager.schedule(j);
		return j.id();
	}

	private Set<DeployFile> undoFiles(String id, Map<AgentLoc, String> paths) throws OdenException {
		Set<DeployFile> undos = new HashSet<DeployFile>();
		
		RecordElement2 record = deploylog.search(id, null, null, null, false);
		if(record == null)
			throw new OdenException("Couldn't retrieve the history : " + id);
		
		for(DeployFile f : record.getDeployFiles()){
			boolean matched = false;
			for(AgentLoc loc : paths.keySet()){
				if(f.getAgent().equals(loc) && f.getPath().equals(paths.get(loc))){
					matched = true;
					break;
				}
			}
						
			if(paths.size() > 0 && !matched)		// cancel 대상 아님 
				continue;
			
			AgentLoc dest = f.getAgent();
			String addr = dest.agentAddr();
			String path = f.getPath();
			String bakloc = FileUtil.combinePath(
					configService.getBackupLocation(dest.agentName()), id);
						
			DeployFile.Mode mode = cancelMode(f.mode());
			if(mode == Mode.NA)
				throw new OdenException("Can't be canceled: " + addr + "/" + path);
			
			undos.add(new DeployFile(
					new Repository(addr, bakloc),
					path,
					dest,
					0L,
					0L,
					mode, false ));
		}
		return undos;
	}
	
	private DeployFile.Mode cancelMode(DeployFile.Mode m){
		switch (m) {
		case ADD:
			return Mode.DELETE;
		case UPDATE:
			return Mode.UPDATE;
		case DELETE:
			return Mode.ADD;
		default:
			return Mode.NA;
		}
	}
	
	private String serialize(List<RecordElement2> list) throws JSONException {
		StringBuffer buf = new StringBuffer();
		for(RecordElement2 r : list){
			buf.append("=============================\n");
			buf.append("txid: " + r.id() + ", user: " + r.getUser() + 
					", date: " + DateUtil.toStringDate(r.getDate()) +
					", status: " + (r.isSuccess() ? "Success" : "Fail") + 
					", total: " + r.getDeployFiles().size() +
					", desc: " + r.desc() + 
					", log: " + (r.log() != null ? r.log() : "") + "\n");
			
			for(DeployFile f : r.getDeployFiles()){
				String status = f.isSuccess() ? "S" : "F";
				buf.append("(" + status  + ") "  + DeployFileUtil.modeToString(f.mode()) + " " + 
						f.getRepo() + "\t" + f.getAgent().agentAddr() + ":" + f.getAgent().location() + "\t" +
						f.getPath() + "\t" + 
						DateUtil.toStringDate(f.getDate()) + " (" + (f.getSize()) + " bytes)" + 
						(f.errorLog() != null ? " [" + f.errorLog() + "]\n" : "\n"));
			}
		}
		return buf.toString();
	}

	private JSONArray jsonize(List<RecordElement2> list) throws OdenException{
		JSONArray ja = new JSONArray();
		for(RecordElement2 r : list){
			try {
				ja.put(new JSONObject()
						.put("txid", r.id())
						.put("user", r.getUser())
						.put("date", String.valueOf(r.getDate()))
						.put("success", String.valueOf(r.isSuccess()))
						.put("files", JSONUtil.jsonize(r.getDeployFiles())) 
						.put("desc", r.desc())
						.put("log", r.log()));
			} catch (JSONException e) {
				throw new OdenException("Fail to jsonize.");
			}
		}
		return ja;
	}

	public String getName() {
		return "history";
	}

	public String getShortDescription() {
		return "list / re-deploy / undo Finished Tasks";
	}

	public String getUsage() {
		return getName() + " " + Cmd.HELP_ACTION;
	}

	public String getFullUsage() {
		return getName() + " " + Cmd.INFO_ACTION + " -d[ate] <start-date: yyyyMMdd> [<end-date: yyyyMMdd>]" + 
				"\n\t[-u[ser] <user-access-ip>] " +				
				"\n\t[-f[ailonly]]" + "\n" +
				getName() + " " + Cmd.SHOW_ACTION + " [<txid>]" +
				"\n\t[-u[ser] <user-access-ip>] " +
				"\n\t[-a[gent] <host-name>] " +
				"\n\t[-p[ath] <path>] " +
				"\n\t[-f[ailonly]]" + 
				"\n" + getName() + " " + UNDO_ACTION + " <txid> -sync" +
				"\n\t[-p[ath] <agent-name>:<absolute-path> <file-path> ...]" +
				"\n" + getName() + " " + REDEPLOY_ACTION + " <txid> -sync";		
	}
	
}
