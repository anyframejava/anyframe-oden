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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.log.LogService;

import anyframe.oden.bundle.common.JSONUtil;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.OdenParseException;
import anyframe.oden.bundle.core.FileMap;
import anyframe.oden.bundle.core.Logger;
import anyframe.oden.bundle.core.Policy;
import anyframe.oden.bundle.prefs.Prefs;

/**
 * Oden shell command to manipulate Oden's Policies.
 * 
 * @author joon1k
 *
 */
public class PolicyCommandImpl extends OdenCommand {
	public final static String POLICY_NODE = "policy";
	
	public final static String TEST_ACTION = "test";
	
	public final static String[] REPO_OPT = {"repo", "r"};
	
	public final static String[] INCLUDE_OPT = {"include", "i"};
	
	public final static String[] EXCLUDE_OPT = {"exclude", "e"};

	public final static String[] UPDATE_OPT = {"update", "u"};
	
	public final static String[] DEST_OPT = {"dest", "d"};
	
	public final static String[] DESC_OPT = {"desc"};

	public PolicyCommandImpl(){
	}
	
	/** 
	 * parse command line and dispatch action
	 */
	public void execute(String line, PrintStream out, PrintStream err) {
		String consoleResult = "";
		boolean isJSON = false;
		
		try{
			JSONArray ja = new JSONArray();
			
			Cmd cmd = new Cmd(line);
			String action = cmd.getAction();
			isJSON = cmd.getOption(Cmd.JSON_OPT) != null;
			
			if(Cmd.INFO_ACTION.equals(action)){
				String policyName = cmd.getActionArg();
				if(policyName.length() > 0){	
					ja = doInfoActionJ(policyName);
					if(ja.length() == 0)
						throw new OdenException("Couldn't find a policy: " + policyName);
				}else {
					ja = doListActionJ();	
				}
			}else if(Cmd.ADD_ACTION.equals(action)){
				if(cmd.getActionArg().length() > 0 && cmd.getOptions().size() > 0){
					addPolicy(cmd.getActionArg(), cmd.getOptionString());
					consoleResult = cmd.getActionArg() + " is added.";
				}else {
					throw new OdenException("Couldn't execute command.");
				}
			}else if(Cmd.REMOVE_ACTION.equals(action)){
				if(cmd.getActionArg().length() > 0){
					String policyName = cmd.getActionArg();
					if(policyInfo(policyName).length() == 0) 
						throw new OdenException("Couldn't find a policy: " + cmd.getActionArg());
					else {
						removePolicy(policyName);
						consoleResult = cmd.getActionArg() + " is removed.";	
					}
				}else {
					throw new OdenException("Couldn't execute command.");
				}
			}else if(TEST_ACTION.equals(action)){
				if(cmd.getActionArg().length() <= 1)
					throw new OdenException("Couldn't execute command.");
				
				String policyName = cmd.getActionArg();
				ja = doTestAction(policyName);
				if(!isJSON)
					consoleResult = formatTest(ja);
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

	private void removePolicy(String policyName) throws OdenException {
		getPrefs().remove(policyName);
	}

	private void addPolicy(String policyName, String args) throws OdenException {
		Cmd policyInfo = new Cmd(policyName, args);
		
		// dests is defined in config.xml ?
		String[] destargs = policyInfo.getOptionArgArray(PolicyCommandImpl.DEST_OPT);
		for(String destarg : destargs){
			new AgentLoc(destarg, configService);
		}
		
		if(!delegateService.availableRepository(
				policyInfo.getOptionArgArray(PolicyCommandImpl.REPO_OPT)))
			throw new OdenException("Invalid repository arguments: " + policyInfo.getOption(
					PolicyCommandImpl.REPO_OPT).toString());
		
		getPrefs().put(policyName, args);
	}

	private void validateDestination(String[] destargs) throws OdenException {
		for(String destarg : destargs){
			AgentLoc agent = new AgentLoc(destarg, configService);
			if(!delegateService.availableAgent(agent))
				throw new OdenException("Couldn't access the agent: " + destarg);
		}
	}

	private void validateRepository(String[] repoargs) throws OdenException {
		try {
			delegateService.getFilesFromRepo(repoargs);
		} catch (OdenException e) {
			throw new OdenException("Couldn't access the repository: " + Arrays.toString(repoargs));
		}
	}
	
	private String policyInfo(String policyName){
		return getPrefs().get(policyName); 
	}
	
	private JSONArray doInfoActionJ(String policyName) {
		JSONArray arr = new JSONArray();
		try {
			String info = getPrefs().get(policyName);
			if(info.length() > 0)
				arr.put(new JSONObject().put(policyName, info));
		} catch (JSONException e) {
			return null;
		}
		return arr;
	}
			
	private JSONArray doListActionJ() throws OdenException, JSONException {
		JSONArray arr = new JSONArray();
		for(String name : getPrefs().keys()){
			JSONObject jo = new JSONObject();
			jo.put(name, policyInfo(name));
			arr.put(jo);
		} 
		return arr;
	}	
	
	public void preview(Map<List<String>, FileMap> repomap, Cmd policyInfo) throws OdenException{
		String[] repo = policyInfo.getOptionArgArray(PolicyCommandImpl.REPO_OPT);
		List<String> includes = policyInfo.getOptionArgList(PolicyCommandImpl.INCLUDE_OPT);
		List<String> excludes = policyInfo.getOptionArgList(PolicyCommandImpl.EXCLUDE_OPT);
		boolean update = policyInfo.getOption(PolicyCommandImpl.UPDATE_OPT) != null;
		List<String> dests = policyInfo.getOptionArgList(PolicyCommandImpl.DEST_OPT);
		if(includes.size() <1 || dests.size() <1)
			throw new OdenParseException(policyInfo.toString());
					
		List<AgentLoc> agents = new ArrayList<AgentLoc>();
		for(String destargs : dests){
			AgentLoc ra = new AgentLoc(destargs, configService);
			if(!agents.contains(ra))
				agents.add(ra);
		}
		
		validateRepository(repo);
		
		delegateService.preview(repomap, repo, includes, excludes, update, agents);
	}
	
	public void deploy(Map<List<String>, FileMap> repomap, Cmd policyInfo, 
			String user, PrintStream out) throws OdenException {
		String[] repo = policyInfo.getOptionArgArray(PolicyCommandImpl.REPO_OPT);
		boolean update = policyInfo.getOption(PolicyCommandImpl.UPDATE_OPT) != null;

		FileMap files = repomap.get(Arrays.asList(repo));
		if(files == null)
			throw new OdenException("Couldn't find any files to deploy from: " + Arrays.toString(repo));
		
		delegateService.deploy(new Policy(repo, files, update, user), System.currentTimeMillis(), out);
	}
	
	private JSONArray doTestAction(String policyName) throws OdenException{
		Map<List<String>, FileMap> repomap = new HashMap<List<String>, FileMap>();
		preview(repomap, infoCmd(policyName));
		
		JSONArray ja = new JSONArray();
		try {
			ja = JSONUtil.jsonArray(repomap);
		} catch (JSONException e) {
			throw new OdenException(e);
		}
		return ja;
	}
	
	private String formatTest(JSONArray ja) throws JSONException {
		StringBuffer buf = new StringBuffer();
		for(int i=0; i< ja.length(); i++){
			JSONObject repos = ja.getJSONObject(i);
			for(Iterator<String> it = repos.keys(); it.hasNext();){
				String repo = it.next();
				buf.append("REPOSITORY: " + repo + "\nFILES:\n");
				JSONObject files = repos.getJSONObject(repo);
				for(Iterator<String> it2 = files.keys(); it2.hasNext();){
					String file = it2.next();
					buf.append("\t"+ file + " >> " + JSONUtil.toString(files.getJSONArray(file)));
				}
			}
		}
		return buf.toString();
	}
	
	public String getName() {
		return "policy";
	}

	public String getShortDescription() {
		return "manipulate policies having deploy information.";
	}

	public String getUsage() {
		return getName() + " " + Cmd.HELP_ACTION;
	}
	
	public String getFullUsage() throws OdenException {
		return getName() + " " + Cmd.ADD_ACTION + " <policy-name> " + 
				"\n\t-r[epo] " + getRepositoryUsages() + " " + 
				"\n\t-i[nclude]" + " <wildcard-location> ... " +
				"[" + "-e[xclude]" + " <wildcard-location> ...] " + 
				"\n\t[" + "-u[pdate]" + "] " + 
				"\n\t-d[est]" + " <agent-name>[/<location-variable-name>] ... " + 
				"\n\t[" + "-desc" + " <description>]" + "\n" +
				getName() + " " + Cmd.INFO_ACTION + " [<policy-name>]" + "\n" +
				getName() + " " + Cmd.REMOVE_ACTION + " <policy-name>" + "\n" + 
				getName() + " " + TEST_ACTION + " <policy-name>";
	}
	
	private String getRepositoryUsages() throws OdenException {
		StringBuffer usages = new StringBuffer();
		for(Iterator<String> it = delegateService.getRepositoryUsages().iterator(); it.hasNext();) {
			usages.append("[" + it.next() + "]");
			if(it.hasNext())
				usages.append(" | ");
		}
		if(usages.length() == 0)
			throw new OdenException("Couldn't find any repository services.");
		return usages.toString();
	}
	
	public Prefs getPrefs(){
		return getPrefs(POLICY_NODE);
	}
	
	public Cmd infoCmd(String name) throws OdenException {
		return toInfoCmd(POLICY_NODE, name);
	}
		
}
