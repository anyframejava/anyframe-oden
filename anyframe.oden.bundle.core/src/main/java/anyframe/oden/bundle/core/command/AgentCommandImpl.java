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

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import anyframe.common.bundle.gate.CustomCommand;
import anyframe.oden.bundle.common.JSONUtil;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.config.AgentElement;
import anyframe.oden.bundle.core.config.OdenConfigService;

/**
 * Oden Shell command to get agent information in the config.xml
 * 
 * @author joon1k
 *
 */
public class AgentCommandImpl implements CustomCommand {

	private OdenConfigService configsvc;
	
	protected void setConfigService(OdenConfigService configsvc){
		this.configsvc = configsvc;
	}
	
	protected void unsetConfigService(OdenConfigService configsvc) {
		this.configsvc = null;
	}
	
	public void execute(String line, PrintStream out, PrintStream err) {
		boolean isJSON = false;
		try{
			Cmd cmd = new Cmd(line);
			String action = cmd.getAction();
			isJSON = cmd.getOption(Cmd.JSON_OPT) != null;
			
			if("info".equals(action)){
				if(isJSON)
					out.println(agentList().toString());
				else
					out.println(formatAgentList(agentList()));
			}else if("help".equals(action)){
				out.println(getUsage());
			}else {
				throw new OdenException("Couldn't execute specified action: " + action);
			}
		}catch(OdenException e){
			err.println(isJSON ? JSONUtil.jsonizedException(e) : e.getMessage());
		}catch(Exception e){
			err.println(isJSON ? JSONUtil.jsonizedException(e) : e.getMessage());
		}
	}

	// [{"name":"", "host":"", "port":"", "loc":"", "locs", {"":"", "":""}}, {..} ]
	private JSONArray agentList() throws OdenException {
		JSONArray jagents = new JSONArray();
		try {
			for(String name : configsvc.getAgentNames()){
				JSONObject jagent = new JSONObject();
				jagents.put(jagent);
				
				AgentElement agent = configsvc.getAgent(name);
				jagent.put("name", agent.getName());
				jagent.put("host", agent.getHost());
				jagent.put("port", agent.getPort());
				jagent.put("loc", agent.getDefaultLoc().getValue());
				jagent.put("locs", getLocs(agent));
			}
		} catch (FileNotFoundException e) {
			throw new OdenException(e);
		} catch (JSONException e) {
			throw new OdenException("Fail to jsonize. " + e.getMessage());
		}
		return jagents;
	}
	
	private String formatAgentList(JSONArray agents) throws OdenException {
		StringBuffer buf = new StringBuffer();
		try{
			for(int i=0; i<agents.length(); i++){
				JSONObject agent = agents.getJSONObject(i);
				buf.append("Agent: " + agent.get("name") + "\n");
				String port = agent.getString("port");
				buf.append("Address: " + agent.getString("host") + (port == null ? "" : ":" + port) + "\n");
				buf.append("Default Location: " + agent.getString("loc") + "\n");
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
		return "agent info";
	}

}
