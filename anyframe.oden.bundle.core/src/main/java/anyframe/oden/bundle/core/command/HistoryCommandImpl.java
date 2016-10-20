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
import java.util.HashMap;
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

import anyframe.common.bundle.log.Logger;
import anyframe.oden.bundle.common.ArraySet;
import anyframe.oden.bundle.common.DateUtil;
import anyframe.oden.bundle.common.FileUtil;
import anyframe.oden.bundle.common.JSONUtil;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.AgentLoc;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.DeployFileUtil;
import anyframe.oden.bundle.core.Repository;
import anyframe.oden.bundle.core.DeployFile.Mode;
import anyframe.oden.bundle.core.job.DeployJob;
import anyframe.oden.bundle.core.job.Job;
import anyframe.oden.bundle.core.record.DeployLogService2;
import anyframe.oden.bundle.core.record.RecordElement2;
import anyframe.oden.bundle.core.txmitter.TransmitterService;

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
	private static final String[] FAILONLY_OP = {"failonly", "f"};
	private static final String UNDO_ACTION = "undo";
	
	
	private BundleContext context;
	
	protected void activate(ComponentContext context){
		this.context = context.getBundleContext();
	}
	
	
	private DeployLogService2 deploylog;
	
	protected void setDeployLogService(DeployLogService2 recordsvc) {
		this.deploylog = recordsvc;
	}
	
	private TransmitterService txmitterService;
	
	protected void setTransmitterService(TransmitterService tx){
		this.txmitterService = tx;
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
				List<RecordElement2> list = deploylog.search(cmd.getActionArg(), 
						cmd.getOptionArg(USER_OP),
						cmd.getOptionArg(AGENT_OP),
						cmd.getOptionArg(PATH_OP),
						date.length > 0 ? date[0] : "",
						date.length > 1 ? date[1] : (date.length > 0 ? date[0] : ""),
						cmd.getOption(FAILONLY_OP) != null);
				if(isJSON){
					ja = jsonize(list);
				} else {
					consoleResult = serialize(list);
				}
			}else if(Cmd.INFO_ACTION.equals(action)){
				String[] date = cmd.getOptionArgArray(DATE_OP);
				if(date.length == 0){		// show dates having history file
					List<String> list = deploylog.recordedDateList();
					if(isJSON){ 
						ja.put(list);
					} else {
						StringBuffer buf = new StringBuffer();
						for(String d : list){
							buf.append(d + "\n");
						}
						buf.append("To see more details, use this command: history info [-d[ate] <start-date: yyyyMMdd> <end-date: yyyyMMdd>]");
						consoleResult = buf.toString();
					}
				}else {		// show all histories of that date
					List<RecordElement2> list = deploylog.search(null, null, null, null, 
							date.length > 0 ? date[0] : "", date.length > 1 ? date[1] : (date.length > 0 ? date[0] : ""),false);
					if(isJSON){
						for(RecordElement2 r : list){
							String status = r.isSuccess() ? "S" : "F";
							ja.put(new JSONObject().put("id", r.id()).put("date", r.getDate()).put("status", status).put("desc", r.desc()));
						}
					} else {
						StringBuffer buf = new StringBuffer();
						for(RecordElement2 r : list){
							String status = r.isSuccess() ? "Success" : "Fail";
							buf.append(r.id() + "\t" + DateUtil.toStringDate(r.getDate())
									+ "\t" + status + "\t" + r.desc() + "\n");
						}
						buf.append("To see more details, use this command: history show <id>");
						consoleResult = buf.toString();
					}
				}
			}else if(UNDO_ACTION.equals(action)){
				String id = cmd.getActionArg();
				List<String> paths = cmd.getOptionArgList(PATH_OP);
				String user = extractUserName(cmd); 
				
				String txid = undo(id, paths, user);
				
				if(isJSON)
					ja.put(new JSONObject().put("txid", txid));
				else
					consoleResult = "Undo is scheduled. Transaction id is: " + txid;
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

	}
	
	private String undo(String id, List<String> paths, String user) throws OdenException {
		if(id.length() == 0)
			throw new OdenException("<txid> is required.");
		if( (paths.size() % 2) != 0)
			throw new OdenException("Illegal <path> arguments error.");
		
		Map<AgentLoc, String> m = new HashMap<AgentLoc, String>();
		for(int i=0; i<paths.size(); i++){
			m.put(new AgentLoc(paths.get(i), configService), paths.get(++i));
		}
		return undo(id, m, user);
	}
	
	private String undo(String id, Map<AgentLoc, String> paths, String user) throws OdenException {		
		Set<DeployFile> undos = undoFiles(id, paths);
		
		Job j = new DeployJob(context, undos, user) {
			
			@Override
			protected void run() throws Exception {
				Iterator<DeployFile> it = deployFiles.iterator();
				while(!stop && it.hasNext()) {
					DeployFile f = it.next(); 
					try{
						String[] repo = f.getRepo().args();
						AgentLoc parent = f.getAgent();
						AgentLoc oldbak = new AgentLoc(parent.agentName(), parent.agentAddr(), repo[1]);
						String file = f.getPath();
						String parentLoc = parent.location();
						String thisbak = FileUtil.combinePath(
								configService.getBackupLocation(parent.agentName()), id);
						
						DeployFile d = null;
						if(f.mode() == Mode.ADD || f.mode() == Mode.UPDATE) {
							d = txmitterService.backupCopy(oldbak, file, parentLoc, thisbak);
						}else if(f.mode() == Mode.DELETE){
							d = txmitterService.backupRemove(parent, file, thisbak);
						}
						if(d != null){
							f.setSuccess(d.isSuccess());
							f.setDate(d.getDate());
							f.setSize(d.getSize());
						}
					}catch(OdenException e){
						Logger.error(e);
					}
					
				}
			}
		};
		j.schedule("history undo");
		return j.id();
	}

	private Set<DeployFile> undoFiles(String id, Map<AgentLoc, String> paths) throws OdenException {
		Set<DeployFile> undos = new ArraySet<DeployFile>();
		
		List<RecordElement2> record = deploylog.search(id, null, null, null, null, null, false);
		if(record.size() != 1 || record.get(0).getDeployFiles().size() == 0)
			throw new OdenException("Couldn't retrieve the history : " + id);
		
		for(DeployFile f : record.get(0).getDeployFiles()){
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
					null,
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
					", status: " + (r.isSuccess() ? "Success" : "Fail") + ", desc: " + r.desc() + "\n");
			
			for(DeployFile f : r.getDeployFiles()){
				String status = f.isSuccess() ? "S" : "F";
				buf.append("(" + status  + ") "  + DeployFileUtil.modeToString(f.mode()) + " " + 
						f.getRepo() + "\t" + f.getAgent().agentAddr() + ":" + f.getAgent().location() + "\t" +
						DateUtil.toStringDate(f.getDate()) + "\t" + 
						f.getPath() + " (" + (f.getSize()) + " bytes)" + "\n");
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
						.put("desc", r.desc()) );
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
		return "inquiry deploy logs.";
	}

	public String getUsage() {
		return getName() + " " + Cmd.HELP_ACTION;
	}

	public String getFullUsage() {
		return getName() + " " + Cmd.INFO_ACTION + " [-d[ate] <start-date: yyyyMMdd> <end-date: yyyyMMdd>]" + "\n" +
				getName() + " " + Cmd.SHOW_ACTION + " [<txid>]" +
				"\n\t[-u[ser] <user-access-ip>] " +
				"\n\t[-a[gent] <host-name>] " +
				"\n\t[-p[ath] <path>] " +
				"\n\t[-d[ate] <start-date: yyyyMMdd> <end-date: yyyyMMdd>]" +
				"\n\t[-f[ailonly]]" + "\n" +
				getName() + " " + UNDO_ACTION + " <txid>" +
				"\n\t[-p[ath] <agent-name>:<absolute-path> <file-path> ...]";
				
	}
	
}
