/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.anyframe.oden.bundle.job;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.anyframe.oden.bundle.gate.CustomCommand;
import org.anyframe.oden.bundle.common.DateUtil;
import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.common.StringUtil;
import org.anyframe.oden.bundle.core.DeployFile;
import org.anyframe.oden.bundle.core.DeployFileUtil;
import org.anyframe.oden.bundle.core.SortedDeployFileSet;
import org.anyframe.oden.bundle.core.DeployFile.Mode;
import org.anyframe.oden.bundle.core.command.Cmd;
import org.anyframe.oden.bundle.core.command.JSONUtil;
import org.anyframe.oden.bundle.job.log.JobLogService;
import org.anyframe.oden.bundle.job.log.LogError;
import org.anyframe.oden.bundle.job.log.ShortenRecord;
import org.anyframe.oden.bundle.job.page.PageHandler;
import org.anyframe.oden.bundle.job.page.PageHandlerOr;

public class LogCommandImpl implements CustomCommand{
	JobLogService jobLogger;
	protected void setJobLogService(JobLogService jobLogger){
		this.jobLogger = jobLogger;
	}
	
	PageHandler pageHandler;
	protected void setPageHandler(PageHandler pageHandler){
		this.pageHandler = pageHandler;
	}
	
	public void execute(String line, PrintStream out, PrintStream err) {
		boolean isJSON = false;
		try {
			Cmd cmd = new Cmd(line);
			isJSON = cmd.getOption(Cmd.JSON_OPT) != null;

			if (cmd.getAction().length() == 0 || cmd.getAction().equals("help")) {
				out.println(getFullUsage());
				return;
			}

			out.println(execute(cmd, isJSON));
		} catch (Exception e) {
			err.println(isJSON ? JSONUtil.jsonizedException(e) : e.getMessage());
			Logger.error(e);
		}
	}

	private String execute(Cmd cmd, boolean isJSON) throws Exception {
		String action = cmd.getAction();
		if (action.equals("search")) {
			final String job = cmd.getOptionArg(new String[]{"job"});
			final String user = cmd.getOptionArg(new String[]{"user"});
			final String path = cmd.getOptionArg(new String[]{"path"});
			final boolean isFailOnly = cmd.getOption("failonly") != null;
			String pgscale0 = cmd.getOptionArg(new String[]{"pgscale"});
			final int pgscale = StringUtil.empty(pgscale0) ? -1 : Integer.valueOf(pgscale0);
			
			if(isJSON){
				return new JSONArray().put(pageHandler.getCachedData(cmd, 
						pgscale, new PageHandlerOr(){
					public JSONArray run() throws Exception {
						final List<ShortenRecord> ret = jobLogger.search(
								job, user, path, isFailOnly);
						
						JSONArray ja = new JSONArray();
						for(ShortenRecord r : ret){
							String status = r.isSuccess() ? "S" : "F";
							ja.put( new JSONObject().put("txid", r.getId())
								.put("date", DateUtil.toStringDate(r.getDate()))
								.put("status", status)
								.put("job", r.getJob())
								.put("user", r.getUser())
								.put("total", r.getTotal())
								.put("nsuccess", r.getnSuccess()) );
						}
						return ja;
					}
				})).toString();
			}
			
			List<ShortenRecord> ret = jobLogger.search(
					job, user, path, isFailOnly);
			
			StringBuffer buf = new StringBuffer();
			for(ShortenRecord r : ret){
				String status = r.isSuccess() ? "Success" : "Fail";
				buf.append(r.getId() + "\t" + DateUtil.toStringDate(r.getDate())
						+ "\t" + status + " (" + r.getnSuccess() + "/" + r.getTotal() + ")\t" + r.getJob() + "(" + r.getUser() + ")\n");
			}
			buf.append("To see more details, use this command: log show <id>");
			return buf.toString();
		}else if(action.equals("show")) {
			final String txid = cmd.getActionArg();
			final String _mode = cmd.getOptionArg(new String[]{"mode"});
			final Mode mode = StringUtil.empty(_mode) ? Mode.NA : 
				DeployFileUtil.stringToMode(_mode);
			final boolean failonly = cmd.getOption("failonly") != null; 
			final String path = cmd.getOptionArg(new String[]{"path"});
			String pgscale0 = cmd.getOptionArg(new String[]{"pgscale"});
			final int pgscale = StringUtil.empty(pgscale0) ? -1 : Integer.valueOf(pgscale0);
			
			ShortenRecord r = jobLogger.search(txid);
			if(r == null)
				throw new OdenException("No proper log: " + txid);
			
			JSONObject obj = pageHandler.getCachedData(cmd, 
					pgscale, new PageHandlerOr(){
				public JSONArray run() throws Exception {
					return (JSONArray) toJSONArray2(
							new SortedDeployFileSet(
									jobLogger.show(txid, path, mode, failonly)));
				}
			});
			
			if(isJSON){
				return new JSONArray().put(new JSONObject()
						.put("txid", r.getId())
						.put("user", r.getUser())
						.put("date", DateUtil.toStringDate(r.getDate()))
						.put("status", r.isSuccess() ? "S" : "F")
						.put("data", obj.getJSONArray("data"))
						.put("total", obj.getString("total"))
						.put("job", r.getJob())
						.put("log", r.getLog()) ).toString();
			}
			
			StringBuffer buf = new StringBuffer();
			buf.append(r.getId() + " " + 
					(r.isSuccess() ? "Success" : "Fail") + " " + 
					r.getJob() + "(" + r.getTotal() + ") " + 
					DateUtil.toStringDate(r.getDate()) + " " + 
					r.getLog() + "\n\n");
			JSONArray arr = obj.getJSONArray("data");
			for(int i=0; i<arr.length(); i++){
				JSONObject o = arr.getJSONObject(i);
				buf.append((o.getString("success").equals("true") ? "S " : "F ") +
						o.getString("mode") + " " +
						o.getString("path") + " -> " + 
						o.getString("targets") + " " +
						o.getString("errorlog") + "\n");
			}
			return buf.toString();
		}else if(action.equals("error")) {
			String date = cmd.getOptionArg(new String[]{"date"});
			LogError r = jobLogger.getErrorLog(date);
			if(r == null)
				throw new OdenException("No proper error log: " + date);
			
			if(isJSON) {
				return new JSONArray().put(
						new JSONObject().put("contents", r.getContents()).put("date", r.getDate()))
						.toString();
			} else {	
				return r.getContents();
			}
		} else {
			throw new OdenException("Invalid Action: " + action);
		}
	}
	
	private JSONArray toJSONArray(SortedDeployFileSet fs) throws JSONException{
		JSONArray list = new JSONArray();
		
		DeployFile prev = null;
		List<String> targets = null;
		for(Iterator<DeployFile> it = fs.iterator(); it.hasNext();){
			DeployFile current = it.next();						
			if(prev != null && current.getPath().equals(prev.getPath())){
				targets.add(current.getAgent().agentName());
				continue;
			}
			
			// if not equal to previous element, print previous one.
			if(prev != null)
				list.put(new JSONObject()
					.put("path", prev.getPath())
					.put("targets", new JSONArray(targets))
					.put("mode", DeployFileUtil.modeToString(prev.mode()))
					.put("success", String.valueOf(prev.isSuccess()))
					.put("errorlog", StringUtil.makeEmpty(prev.errorLog())) );	
			
			prev = current;
			targets = new ArrayList<String>();
			targets.add(current.getAgent().agentName());
		}
		if(prev != null)
			list.put(new JSONObject()
				.put("path", prev.getPath())
				.put("targets", targets)
				.put("mode", DeployFileUtil.modeToString(prev.mode()))
				.put("success", String.valueOf(prev.isSuccess()))
				.put("errorlog", StringUtil.makeEmpty(prev.errorLog())) );
		return list;
	}
	
	private JSONArray toJSONArray2(SortedDeployFileSet fs) throws JSONException{
		JSONArray list = new JSONArray();
		
		for(Iterator<DeployFile> it = fs.iterator(); it.hasNext();){
			DeployFile current = it.next();						

			list.put(new JSONObject()
				.put("path", current.getPath())
				.put("targets", new JSONArray().put(current.getAgent().agentName()))
				.put("mode", DeployFileUtil.modeToString(current.mode()))
				.put("success", String.valueOf(current.isSuccess()))
				.put("errorlog", StringUtil.makeEmpty(current.errorLog())) );	
		}
		return list;
	}

	public String getName() {
		return "log";
	}

	public String getShortDescription() {
		return "search deploy or error log";
	}

	public String getUsage() {
		return "log help";
	}
	
	public String getFullUsage(){
		return "log show <txId> [ -mode <A | U | D> ] [-path <path>] [-failonly]" 
				+ "\nlog search [-job <job>] [-user <user>] [-path <path>] [-failonly]"
				+ "\nlog error [-date <date>]";
	}

}
