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
package anyframe.oden.bundle.ent.misc;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import anyframe.common.bundle.gate.CustomCommand;
import anyframe.oden.bundle.common.JSONUtil;
import anyframe.oden.bundle.common.Logger;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.AgentFile;
import anyframe.oden.bundle.core.FileMap;
import anyframe.oden.bundle.core.command.Cmd;
import anyframe.oden.bundle.core.config.AgentElement;
import anyframe.oden.bundle.core.config.OdenConfigService;
import anyframe.oden.bundle.core.job.CompareAgentsJob;
import anyframe.oden.bundle.core.job.JobManager;
import anyframe.oden.bundle.core.txmitter.TransmitterService;
import anyframe.oden.bundle.deploy.DeployerService;

/**
 * Oden Shell command to get agent information in the config.xml
 * 
 * @author joon1k
 *
 */
public class AgentCommandImpl implements CustomCommand {
	
	public final static String[] DETAIL_OPT = {"detail"};
	
	public final static String[] TARGET_OPT = {"t", "target"};
	
	private BundleContext context;
	
	protected void activate(ComponentContext context){
		this.context = context.getBundleContext();
	}
	
	private OdenConfigService configsvc;
	
	protected void setConfigService(OdenConfigService configsvc){
		this.configsvc = configsvc;
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
		JSONArray ja = new JSONArray();
		String consoleResult = "";
		boolean isJSON = false;
		try{
			Cmd cmd = new Cmd(line);
			String action = cmd.getAction();
			isJSON = cmd.getOption(Cmd.JSON_OPT) != null;
			
			if("info".equals(action)){
				String agentName = cmd.getActionArg();
				if(agentName.length() > 0) {
					ja.put(agentInfo(agentName));
					if(!isJSON && cmd.getOption(DETAIL_OPT) != null) {
						consoleResult = formatAgentList(ja) + jvmStat(agentName);
					}
				} else if(cmd.getOption(DETAIL_OPT) != null){
					ja = agentList();
				} else {
					ja = simpleAgentList();
				}
			}else if("compare".equals(action)){
				List<String> agentNames = cmd.getOptionArgList(TARGET_OPT);
				if(agentNames.size() == 0)
					throw new OdenException("Require more arguments.");
				
				Map<PathObj, List<AgentFile>> result = compare(agentNames);
				if(isJSON){
					for(PathObj o : result.keySet()){
						List<AgentFile> fs = result.get(o);
						ja.put(new JSONObject()
								.put("path", o.path)
								.put("match", o.success)
								.put("agents", agentsToJSON(fs)) );
					}
				}else{
					StringBuffer buf = new StringBuffer();
					for(PathObj o : result.keySet()){
						buf.append(o.path + "(" + o.success + "): [");
						List<AgentFile> fs = result.get(o);
						for(int i=0; i<fs.size(); i++){
							buf.append(fs.get(i).agent());
							if(i+1<fs.size())
								buf.append(", ");
						}
						buf.append("]\n");
					}
					consoleResult = buf.toString();
				}
			}else if(action.length() == 0 || "help".equals(action)){
				out.println(getFullUsage());
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
			err.println(isJSON ? JSONUtil.jsonizedException(e) : e.getMessage());
		}catch(Exception e){
			err.println(isJSON ? JSONUtil.jsonizedException(e) : e.getMessage());
		}
	}

	private String jvmStat(String name) {
		AgentElement agent = configsvc.getAgent(name);
		if(agent == null) return "";
		DeployerService ds = txmitterService.getDeployer(agent.getAddr());
		if(ds == null) return "";
		try {
			return ds.jvmStat();
		} catch (Exception e) {
		}
		return "";
	}

	private JSONArray agentsToJSON(List<AgentFile> fs) throws JSONException {
		JSONArray arr = new JSONArray();
		for(AgentFile f : fs){
			arr.put(new JSONObject()
					.put("agent", f.agent())
					.put("size", f.size())
					.put("date", f.date()) );
		}
		return arr;
	}

	private JSONArray simpleAgentList() throws OdenException { 
		JSONArray jagents = new JSONArray();
		for(String name : configsvc.getAgentNames()){
			try{
				AgentElement agent = configsvc.getAgent(name);
				if(agent == null)
					throw new OdenException("Fail to get agent information: " + name);
				
				JSONObject jagent = new JSONObject();
				try{
					jagent.put("name", agent.getName());
					jagent.put("addr", agent.getHost() + ":" + agent.getPort());
					jagents.put(jagent);
				}catch(JSONException e){
					Logger.error(e);
				}
			}catch(OdenException e){
				Logger.error(e);
			}
		}
		return jagents;
	}
	
	// [{"name":"", "host":"", "port":"", "loc":"", "locs", {"":"", "":""}}, {..} ]
	private JSONArray agentList() throws OdenException {
		JSONArray jagents = new JSONArray();
		for(String name : configsvc.getAgentNames()){
			try{
				JSONObject jagent = agentInfo(name);
				jagents.put(jagent);
			}catch(OdenException e){
				Logger.error(e);
			}
		}
		return jagents;
	}
	
	private JSONObject agentInfo(String name) throws OdenException {
		JSONObject jagent = new JSONObject();
		try {
			AgentElement agent = configsvc.getAgent(name);
			if(agent == null)
				throw new OdenException("Fail to get agent information: " + name);
			jagent.put("name", agent.getName());
			jagent.put("host", agent.getHost());
			jagent.put("port", agent.getPort());
			jagent.put("loc", agent.getDefaultLoc().getValue());
			jagent.put("backup", agent.getBackupLoc().getValue());
			jagent.put("locs", getLocs(agent));
			boolean alive = txmitterService.getDeployer(agent.getAddr()) != null;
			jagent.put("status", String.valueOf(alive));
		} catch (JSONException e) {
			throw new OdenException("Fail to jsonize. " + e.getMessage());
		}
		return jagent;
	}
	
	private Map<PathObj, List<AgentFile>> compare(List<String> agentNames) throws Exception{
		CompareAgentsJob j = new CompareAgentsJob(context, agentNames, "agent compare");
		jobManager.syncRun(j);
		FileMap m = j.result();
		
		Map<PathObj, List<AgentFile>> result = new TreeMap<PathObj, List<AgentFile>>();
		for(String path : m.keySet()){
			AgentFile prev = null;
			List<AgentFile> fs = m.get(path);
			boolean success = agentNames.size() == fs.size();
			for(AgentFile f : fs){
				if(success){
					if(prev == null)
						prev = f;
					else
						success = prev.size() == f.size() && prev.date() == f.date();	
				}
			}
			result.put(new PathObj(path, success), fs);
		}
		return result;
	}
	
	class PathObj implements Comparable<PathObj>{
		String path;
		boolean success;
		PathObj(String path, boolean success){
			this.path = path;
			this.success = success;
		}
		public int compareTo(PathObj o) {
			return path.compareTo(o.path);
		}
	}
	
	private String formatAgentList(JSONArray agents) throws OdenException {
		StringBuffer buf = new StringBuffer();
		try{
			for(int i=0; i<agents.length(); i++){
				JSONObject agent = agents.getJSONObject(i);
				buf.append("Agent: " + agent.get("name") + "\n");
				String port = agent.getString("port");
				buf.append("Address: " + agent.getString("host") + (port == null ? "" : ":" + port) + "\n");
				buf.append("Status: " + agent.getString("status") + "\n");				
				buf.append("Default Location: " + agent.getString("loc") + "\n");
				buf.append("Backup Location: " + agent.getString("backup") + "\n");
				buf.append("Locations:\n");
				JSONObject locs = agent.getJSONObject("locs");
				for(Iterator<String> it = locs.keys(); it.hasNext();){
					String locname = it.next();
					buf.append("\t" + locname + ": " + locs.getString(locname) + "\n");
				}
					
			}
		}catch(JSONException e){
			throw new OdenException(e);
		}
		return buf.toString();
	}

	private JSONObject getLocs(AgentElement agent) 
			throws JSONException {
		
		JSONObject jlocs = new JSONObject();
		for(String name : agent.getLocNames()){
			jlocs.put(name, agent.getLoc(name).getValue());
		}
		return jlocs;
	}

	public String getName() {
		return "agent";
	}

	public String getShortDescription() {
		return "inquiry agent information.";
	}

	public String getUsage() {
		return "agent help";
	}
	
	private String getFullUsage() {
		return "agent info [<agent-name>]\n" +
				"agent compare -t[arget] <agent-name> ...";
	}

}
