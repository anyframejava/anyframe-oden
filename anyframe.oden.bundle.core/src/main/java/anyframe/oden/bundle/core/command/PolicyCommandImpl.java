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

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import anyframe.oden.bundle.common.ArraySet;
import anyframe.oden.bundle.common.Assert;
import anyframe.oden.bundle.common.FatInputStream;
import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.Logger;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.OdenParseException;
import anyframe.oden.bundle.core.AgentLoc;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.DeployFileUtil;
import anyframe.oden.bundle.core.Repository;
import anyframe.oden.bundle.core.RepositoryProviderService;
import anyframe.oden.bundle.core.DeployFile.Mode;
import anyframe.oden.bundle.core.prefs.Prefs;
import anyframe.oden.bundle.core.repository.RepositoryService;
import anyframe.oden.bundle.core.txmitter.DeployerHelper;
import anyframe.oden.bundle.core.txmitter.TransmitterService;
import anyframe.oden.bundle.deploy.DeployerService;

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
	
	public final static String[] DELETE_OPT = {"del"};

	
	private BundleContext context;
	
	protected void activate(ComponentContext context){
		this.context = context.getBundleContext();
	}
	
	
	protected RepositoryProviderService repositoryProvider;
	
	protected void setRepositoryProvider(RepositoryProviderService r){
		this.repositoryProvider = r;
	}

	private TransmitterService txmitterService;
	
	protected void setTransmitterService(TransmitterService tx){
		this.txmitterService = tx;
	}

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
					consoleResult = "Policy " + cmd.getActionArg() + " is added.";
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
				Set<DeployFile> dfiles = doTestAction(policyName);
				if(isJSON){
					ja.put(dfiles);
				} else {
					StringBuffer buf = new StringBuffer();
					for(DeployFile dfile : dfiles){
						buf.append(DeployFileUtil.modeToString(dfile.mode()) +  ": " + 
								dfile.getRepo().toString() + " " + dfile.getPath() + 
								" >> " + dfile.getAgent().agentName() + "\n");
					}
					consoleResult = buf.toString();
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
		Cmd policyInfo = new Cmd("c a \"" + policyName + "\" " + args);
		
		// dests is defined in config.xml ?
		String[] destargs = policyInfo.getOptionArgArray(PolicyCommandImpl.DEST_OPT);
		if(destargs.length != 1 && !destargs[0].startsWith("*:")){
			for(String destarg : destargs){
				new AgentLoc(destarg, configService);
			}
		}
		
		String[] repos = policyInfo.getOptionArgArray(PolicyCommandImpl.REPO_OPT);
		if(repos.length == 0 && policyInfo.getOption(PolicyCommandImpl.DELETE_OPT) == null)
			throw new OdenException("-repo or -del option is required.");
		if(repos.length > 0 && !repositoryProvider.availableRepository(repos))
			throw new OdenException("Invalid repository arguments: " + policyInfo.getOption(
					PolicyCommandImpl.REPO_OPT).toString());
		
		getPrefs().put(policyName, args);
	}

	private void validateDestination(String[] destargs) throws OdenException {
		for(String destarg : destargs){
			AgentLoc agent = new AgentLoc(destarg, configService);
			if(txmitterService.getDeployer(agent.agentAddr()) == null)
				throw new OdenException("Couldn't access the agent: " + destarg);
		}
	}

	private void availableRepository(Repository repo) throws OdenException {
		try {
			repositoryProvider.getFilesFromRepo(repo.args());
		} catch (OdenException e) {
			throw new OdenException("Couldn't access the repository: " + repo);
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
	
	public void preview(Set<DeployFile> dfiles, Cmd policyInfo) throws OdenException {
		Repository repo = new Repository(
				policyInfo.getOptionArgArray(PolicyCommandImpl.REPO_OPT));
		List<String> includes = policyInfo.getOptionArgList(PolicyCommandImpl.INCLUDE_OPT);
		List<String> excludes = policyInfo.getOptionArgList(PolicyCommandImpl.EXCLUDE_OPT);
		boolean update = policyInfo.getOption(PolicyCommandImpl.UPDATE_OPT) != null;
		List<String> dests = policyInfo.getOptionArgList(PolicyCommandImpl.DEST_OPT);
		boolean del = policyInfo.getOption(PolicyCommandImpl.DELETE_OPT) != null;
		if(includes.size() <1 || dests.size() <1)
			throw new OdenParseException(policyInfo.toString());
					
		resolveDests(dests);
		Set<AgentLoc> agents = new ArraySet<AgentLoc>();
		for(String destargs : dests){
			AgentLoc ra = new AgentLoc(destargs, configService);
			if(!agents.contains(ra))
				agents.add(ra);
		}

		if(del){
			Assert.check(repo.args().length == 0, "When removing files, -r option is not allowed.");
			previewToRemove(dfiles, includes, excludes, agents);
		} else{
			availableRepository(repo);
			preview(dfiles, repo, includes, excludes, update, agents);
		}
	}
	
	private void resolveDests(List<String> dests) throws OdenException {
		if(dests.size() != 1 || !dests.get(0).startsWith("*:"))
			return;
		
		final String loc = dests.remove(0).substring(2);
		for(String agent : configService.getAgentNames()){
			dests.add(agent + ":" + loc);
		}
	}

	private void previewToRemove(Set<DeployFile> dfiles, List<String> includes, 
			List<String> excludes, Set<AgentLoc> agents) throws OdenException {
		for(AgentLoc agent : agents) {		
			try{
				DeployerService ds = txmitterService.getDeployer(agent.agentAddr());			
				if(ds == null)
					throw new OdenException("Couldn't connect to the agent: " + 
							agent.agentName() + "(" + agent.agentAddr() + ")");
				
				List<String> fs = ds.resolveFileRegex(agent.location(), includes, excludes);
				for(String path : fs){
					FileInfo f = ds.fileInfo(agent.location(), path);
					if(!ds.writable(agent.location(), path))
						throw new OdenException("Not writable file.");
					DeployFileUtil.updateDeployFiles(dfiles, 
							DeployFileUtil.beRemovedFile(
									agent, path, f.size(), f.lastModified()));
				}
			}catch(Exception e){
				// put files which will not be removed.
				for(String s : includes){
					DeployFileUtil.updateDeployFiles(dfiles, 
							DeployFileUtil.notBeRemovedFile(agent, s, e));			
				}
				Logger.error(e);
			}
		}
	}
	
	private void preview(Set<DeployFile> dfiles, 
			Repository repo, List<String> includes, List<String> excludes,
			boolean update, Set<AgentLoc> agents) throws OdenException {
		RepositoryService reposvc = repositoryProvider.getRepoServiceByURI(repo.args());
		if(reposvc == null)
			throw new OdenException("Couldn't find a RepositoryService for " + repo.toString());
		
		DeployerManager deployerMgr = new DeployerManager(context, null, false);
		
		List<String> files = reposvc.resolveFileRegex(repo.args(), includes, excludes);
		for(String file : files){
			FatInputStream in = null;
			try{
				in = reposvc.resolve(repo.args(), file);			
				if(in == null)
					throw new OdenException("Couldn't find that file: " + file);
				
				for(AgentLoc agent : agents) {	
					try{
						DeployerService ds = deployerMgr.getDeployer(agent.agentAddr());
						if(ds == null)
							throw new OdenException("Couldn't connect to the agent or access denied: " + 
									agent.agentName() + "(" + agent.agentAddr() + ")");
						Mode m = Mode.NA;
						if(ds.exist(agent.location(), in.getPath()) ) {
							if(!ds.writable(agent.location(), in.getPath()))
								throw new OdenException("Not writable file.");
							if(update && !DeployerHelper.isNewFile(ds, in.getLastModified(), 
									agent.location(), in.getPath()))
								continue;
							m = Mode.UPDATE;
						}else {
							m = Mode.ADD;
						}
						DeployFileUtil.updateDeployFiles(dfiles, 
								new DeployFile(repo, file, agent, in.size(), in.getLastModified(), m) );
					}catch(Exception e){
						DeployFileUtil.updateDeployFiles(dfiles, 
								DeployFileUtil.notBeDeployedFile(repo, in.getPath(), agent, e) );
					}
				}
			}catch(Exception e){
				for(AgentLoc agent : agents) {	
					DeployFileUtil.updateDeployFiles(dfiles, 
							DeployFileUtil.notBeDeployedFile(repo, file, agent, e) );
				}
			}finally{
				try { if(in != null) in.close(); } catch (IOException e) { }
			}
		}
		reposvc.close(repo.args());
	}
	
	private Set<DeployFile> doTestAction(String policyName) throws OdenException{
		Set<DeployFile> dfiles = new ArraySet<DeployFile>();
		preview(dfiles, infoCmd(policyName));
		return dfiles;
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
		return "add / remove / test Policies";
	}

	public String getUsage() {
		return getName() + " " + Cmd.HELP_ACTION;
	}
	
	public String getFullUsage() throws OdenException {
		return getName() + " " + Cmd.ADD_ACTION + " <policy-name> " + 
				"\n\t[-r[epo] " + getRepositoryUsages() + "] " + 
				"\n\t-i[nclude]" + " <wildcard-location> ... " +
				"[" + "-e[xclude]" + " <wildcard-location> ...] " + 
				"\n\t[" + "-u[pdate] | -del" + "] " + 
				"\n\t-d[est]" + " <agent-name>:<$<location-var>[/<path> | ~[/<path>] | <absolute-path>]> ... " + 
				"\n\t[" + "-desc" + " <description>]" + "\n" +
				getName() + " " + Cmd.INFO_ACTION + " [<policy-name>]" + "\n" +
				getName() + " " + Cmd.REMOVE_ACTION + " <policy-name>" + "\n" + 
				getName() + " " + TEST_ACTION + " <policy-name>";
	}
	
	private String getRepositoryUsages() throws OdenException {
		StringBuffer usages = new StringBuffer();
		for(Iterator<String> it = repositoryProvider.getRepositoryUsages().iterator(); it.hasNext();) {
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
