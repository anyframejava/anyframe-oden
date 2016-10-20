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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import org.anyframe.oden.bundle.gate.CustomCommand;
import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.core.command.Cmd;
import org.anyframe.oden.bundle.core.command.JSONUtil;
import org.anyframe.oden.bundle.core.txmitter.TransmitterService;
import org.anyframe.oden.bundle.deploy.DeployerService;
import org.anyframe.oden.bundle.job.config.CfgCommand;
import org.anyframe.oden.bundle.job.config.CfgJob;
import org.anyframe.oden.bundle.job.config.CfgTarget;
import org.anyframe.oden.bundle.job.config.JobConfigService;

public class ExecCommandImpl implements CustomCommand{
	
	private BundleContext context;
	protected void activate(ComponentContext context) {
		this.context = context.getBundleContext();
	}
	
	JobConfigService jobConfig;
	protected void setJobConfigService(JobConfigService jobConfig) {
		this.jobConfig = jobConfig;
	}
	
	TransmitterService txmitter;
	protected void setTransmitterService(TransmitterService tx) {
		this.txmitter = tx;
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
		if (action.equals("run")) {
			CfgJob job = jobConfig.getJob(cmd.getActionArg());
			if(job == null) throw new OdenException("Invalid Job Name: " + cmd.getActionArg());
			List<String> commands = cmd.getOptionArgList(new String[]{"c"});
			if(commands.size() == 0)
				throw new OdenException("Illegal arguments: -c");
			List<CfgCommand> _cmds = job.getCommands(commands);
			if(_cmds.size() == 0)
				throw new OdenException("Invalid command: " + commands);
			List<String> targets = cmd.getOptionArgList(new String[]{"t"});
			List<CfgTarget> _targets= getActiveTargets(job.getAllTargets(targets));
			if(_targets.size() == 0)
				throw new OdenException("Invalid target: " + targets);
			Map<String, String> result = execCommand(_cmds, _targets); 

			if(isJSON){
				JSONArray arr = new JSONArray();
				for(String targetName : result.keySet()){
					arr.put(new JSONObject().put("target", targetName)
							.put("result", result.get(targetName)));
				}
				return arr.toString();
			}
			
			StringBuffer buf = new StringBuffer();
			for(String targetName : result.keySet())
				buf.append("::: " + targetName + " :::\n" + 
						result.get(targetName));
			return buf.toString();
		} else {
			throw new OdenException("Invalid Action: " + action);
		}
	}

	private List<CfgTarget> getActiveTargets(List<CfgTarget> targets){
		List<CfgTarget> activeTargets = new ArrayList<CfgTarget>();
		for(CfgTarget t : targets){
			DeployerService ds = txmitter.getDeployer(t.getAddress());
			if (ds != null) activeTargets.add(t);
		}
		return activeTargets;
	}
	
	private Map<String, String> execCommand(List<CfgCommand> commands, 
			List<CfgTarget> targets){
		String _timeout = context.getProperty("exec.timeout");
		long timeout = _timeout == null ? -1L : Long.parseLong(_timeout);
			
		List<ExecWorker> workers = new ArrayList<ExecWorker>();
		for(CfgTarget target: targets){
			DeployerService ds = txmitter.getDeployer(target.getAddress());
			
			ExecWorker worker = new ExecWorker(ds, target, commands, timeout);
			workers.add(worker);
			worker.start();
		}
		
		for(ExecWorker worker : workers){
			try {
				worker.join();
			} catch (InterruptedException e) {
				Logger.error(e);
			}
		}
		
		Map<String, String> result = new HashMap<String, String>();
		for(ExecWorker worker : workers)
			result.put(worker.getTargetName(), worker.getResult());
		return result;
	}

	public String getName() {
		return "exec";
	}

	public String getShortDescription() {
		return "execute commands";
	}

	public String getUsage() {
		return "exec help";
	}

	public String getFullUsage() {
		return "exec run <job> [ -t <target> ... ] -c <command-name>...";
	}
}
