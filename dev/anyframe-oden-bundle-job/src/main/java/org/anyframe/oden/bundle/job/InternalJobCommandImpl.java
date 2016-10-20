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
import java.util.List;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ungoverned.osgi.service.shell.Command;

import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.core.command.JSONUtil;
import org.anyframe.oden.bundle.job.config.CfgCommand;
import org.anyframe.oden.bundle.job.config.CfgJob;
import org.anyframe.oden.bundle.job.config.CfgSource;
import org.anyframe.oden.bundle.job.config.CfgMapping;
import org.anyframe.oden.bundle.job.config.CfgTarget;
import org.anyframe.oden.bundle.job.config.JobConfigService;

public class InternalJobCommandImpl implements Command{

	JobConfigService jobConfig;
	protected void setJobConfigService(JobConfigService jobConfig){
		this.jobConfig = jobConfig;
	}
	
	public void execute(String line, PrintStream out, PrintStream err) {
		try{
			String action = null;
			StringTokenizer tok = new StringTokenizer(line, " ");
			tok.nextToken();	// name
			if(tok.hasMoreTokens())
				action = tok.nextToken();	// action
			
			if(action == null || action.equals("help")){
				out.println(getFullUsage());
				return;
			}else if(action.equals("add")){
				if(!tok.hasMoreTokens())
					throw new OdenException("Invalid Arguments");
				int start = line.indexOf(tok.nextToken());
				int end = line.lastIndexOf('}');
				if(end <= start)
					throw new OdenException("Invalid Arguments");
				
				addJob(new JSONObject(line.substring(start, end+1)));
				out.println("[]");
			}else if(action.equals("del")){
				if(!tok.hasMoreTokens())
					throw new OdenException("Invalid Arguments");
				jobConfig.removeJob(tok.nextToken());
				out.println("[]");
			}else{
				throw new OdenException("Invalid action: " + action);
			}
		}catch(Exception e){
			err.println(JSONUtil.jsonizedException(e));
			Logger.error(e);
		}
	}
	
	private void addJob(JSONObject jjob) throws Exception{
		CfgJob job = new CfgJob(jjob.getString("name"), 
				makeSource(jjob.getJSONObject("source")), 
				makeTargets(jjob.getJSONArray("targets")), 
				makeCommands(jjob.getJSONArray("commands")));
		jobConfig.addJob(job);
	}
	
	private List<CfgCommand> makeCommands(JSONArray jcommands) throws JSONException {
		List<CfgCommand> commands = new ArrayList<CfgCommand>();
		for(int i=0; i<jcommands.length(); i++){
			JSONObject jcommand = jcommands.getJSONObject(i);
			commands.add(new CfgCommand(jcommand.getString("name"), 
					jcommand.getString("command"), 
					jcommand.getString("dir")));
		}
		return commands;
	}

	private List<CfgTarget> makeTargets(JSONArray jtargets) throws JSONException {
		List<CfgTarget> targets = new ArrayList<CfgTarget>();
		for(int i=0; i<jtargets.length(); i++){
			JSONObject jtarget = jtargets.getJSONObject(i);
			targets.add(new CfgTarget(jtarget.getString("name"), 
					jtarget.getString("address"), 
					jtarget.getString("dir")));
		}
		return targets;
	}

	private CfgSource makeSource(JSONObject jsource) throws JSONException {
		List<CfgMapping> mappings = new ArrayList<CfgMapping>();
		JSONArray arr = jsource.getJSONArray("mappings");
		for(int i=0; i<arr.length(); i++){
			JSONObject o = arr.getJSONObject(i); 
			mappings.add(new CfgMapping(o.getString("dir"), o.getString("checkout-dir")));
		}
		return new CfgSource(jsource.getString("dir"), 
				jsource.getString("excludes"), mappings);
	}

	private String getFullUsage() {
		return "_job add {" +
		"\n\t\"name\": \"\"," +
		"\n\t\"source\": {\"dir\": \"\", \"excludes\": \"\" , \"mappings\": [ {\"dir\": \"\", \"checkout-dir\": \"\"}, ... ] }," +
		"\n\t\"targets\": [ {\"name\": \"\", \"address\": \"\", \"dir\": \"\"}, ... ]," +
		"\n\t\"commands\": [ {\"name\": \"\", \"command\": \"\", \"dir\": \"\"}, ... ]" +
		"\n\t}" +
		"\n_job del <job>";
	}
	
	public String getName() {
		return "_job";
	}

	public String getShortDescription() {
		return "add / del Job";
	}

	public String getUsage() {
		return "_job help"; 
	}

}
