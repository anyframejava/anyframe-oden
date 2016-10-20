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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import anyframe.common.bundle.gate.CustomCommand;
import anyframe.common.bundle.log.Logger;
import anyframe.oden.bundle.common.JSONUtil;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.config.AgentElement;
import anyframe.oden.bundle.core.config.OdenConfigService;
import anyframe.oden.bundle.core.txmitter.TransmitterService;

/**
 * Oden Shell command to get agent information in the config.xml
 * 
 * @author joon1k
 *
 */
public class AgentCommandImpl implements CustomCommand {
	
	public final static String[] DETAIL_OPT = {"detail"};
	
	private OdenConfigService configsvc;
	
	protected void setConfigService(OdenConfigService configsvc){
		this.configsvc = configsvc;
	}
	
	private TransmitterService txmitterService;
	
	protected void setTransmitterService(TransmitterService tx){
		this.txmitterService = tx;
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
					if(!isJSON) consoleResult = formatAgentList(ja);
				} else if(cmd.getOption(DETAIL_OPT) != null){
					ja = agentList();
				} else {
					ja = simpleAgentList();
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
			boolean alive = txmitterService.available(agent.getAddr());
			jagent.put("status", String.valueOf(alive));
		} catch (JSONException e) {
			throw new OdenException("Fail to jsonize. " + e.getMessage());
		}
		return jagent;
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
		return "agent info [<agent-name>]";
	}

}
