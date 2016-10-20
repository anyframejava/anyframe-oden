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
package anyframe.oden.bundle.core.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

import anyframe.oden.bundle.common.Assert;
import anyframe.oden.bundle.common.BundleUtil;
import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.AgentFile;
import anyframe.oden.bundle.core.FileMap;
import anyframe.oden.bundle.core.config.AgentElement;
import anyframe.oden.bundle.core.config.AgentLocation;
import anyframe.oden.bundle.core.config.OdenConfigService;
import anyframe.oden.bundle.core.txmitter.TransmitterService;
import anyframe.oden.bundle.deploy.DeployerService;

/**
 * 
 * Job to compare agent's sync.
 * 
 * @author joon1k
 *
 */
public class CompareAgentsJob extends Job {
	
	Map<DeployerService, AgentLocation> agents = new HashMap<DeployerService, AgentLocation>();
	
	FileMap result = new FileMap();
	
	Exception error;
	
	public CompareAgentsJob(BundleContext ctx, 
			List<String> agentNames, String desc) throws OdenException {
		super(ctx, desc);
		TransmitterService txmitter = (TransmitterService) BundleUtil.getService(ctx, TransmitterService.class);
		OdenConfigService cfg = (OdenConfigService) BundleUtil.getService(ctx, OdenConfigService.class);
		Assert.check(txmitter != null && cfg != null, "Fail to load some services.");
		
		for(String agent : agentNames){
			AgentElement agentInfo = cfg.getAgent(agent);
			Assert.check(agentInfo != null, "Couldn't find the agent: " + agent);
			DeployerService ds = txmitter.getDeployer(agentInfo.getAddr());
			Assert.check(ds != null, "Couldn't connect to the agent: " + agent);
			agents.put(ds, agentInfo.getDefaultLoc());
		}
	}

	@Override
	protected void run() {
		List<Thread> threads = new ArrayList<Thread>();
		
		for(final DeployerService ds : agents.keySet()){
			final AgentLocation loc = agents.get(ds);
			Thread t = new Thread(){			
				public void run() {
					String agentName = loc.getAgentName();
					try{
						List<FileInfo> fs = ds.listAllFiles(id, loc.getValue());
						for(FileInfo f : fs){
							result.append(f.getPath(), 
									newAgentFile(agentName, f.getPath(), f.size(), f.lastModified()));
						}
					}catch(Exception e){
						if(error != null)
							error = e;
					}
				}
			};
			threads.add(t);
			t.start();
		}
		
		for(Thread t : threads){
			try {
				t.join();
			} catch (InterruptedException e) {
			}
		}
	}
	
	private AgentFile newAgentFile(String agent, String path, long size, long date){
		AgentFile af = new AgentFile(agent, path);
		af.setDate(date);
		af.setSize(size);
		return af;
	}
	
	@Override
	protected void done() {
	}

	@Override
	public void cancel() {
		super.cancel();
		if(this.status == Job.RUNNING){
			try {
				for(DeployerService ds : agents.keySet())
					ds.stop(id);
			} catch (Exception e) {
			}
		}
	}
	
	public FileMap result() throws Exception{
		if(error != null)
			throw error;
		return result;
	}

}
