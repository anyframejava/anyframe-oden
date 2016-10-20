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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;

import anyframe.oden.bundle.common.BundleUtil;
import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.FileUtil;
import anyframe.oden.bundle.common.Logger;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.AgentFile;
import anyframe.oden.bundle.core.FileMap;
import anyframe.oden.bundle.core.config.AgentElement;
import anyframe.oden.bundle.core.config.AgentLocation;
import anyframe.oden.bundle.core.config.OdenConfigService;
import anyframe.oden.bundle.core.txmitter.TransmitterService;
import anyframe.oden.bundle.deploy.ByteArray;
import anyframe.oden.bundle.deploy.DeployerService;
import anyframe.oden.bundle.deploy.DoneFileInfo;

/**
 * 
 * Job to compare agent's sync.
 * 
 * @author joon1k
 *
 */
public class CompareAgentsJob extends Job {
	private List<String> agentNames = new ArrayList<String>();
	
	private Map<DeployerService, AgentLocation> agents = 
			new HashMap<DeployerService, AgentLocation>();
	
	private FileMap result = new FileMap();
	
	private Exception error;
	
	private TimeDiffManager timediff = null;
	
	private int spanmilli = 0;
		
	public CompareAgentsJob(BundleContext ctx, 
			List<String> agentNames, String desc) throws OdenException {
		super(ctx, desc);
		this.agentNames = agentNames;
	}

	@Override
	protected void run() {
		TransmitterService txmitter = (TransmitterService) BundleUtil.getService(context, TransmitterService.class);
		if(txmitter == null){
			error = new OdenException("Fail to load service: " + TransmitterService.class.getName());
			return;
		}
		
		OdenConfigService cfg = (OdenConfigService) BundleUtil.getService(context, OdenConfigService.class);
		if(cfg == null){
			error = new OdenException("Fail to load service: " + OdenConfigService.class.getName());
			return;
		}
		
		for(String agent : agentNames){
			AgentElement agentInfo = cfg.getAgent(agent);
			if(agentInfo == null){
				error = new OdenException("Couldn't find the agent: " + agent);
				return;
			}
			DeployerService ds = txmitter.getDeployer(agentInfo.getAddr());
			if(ds == null){
				error = new OdenException("Couldn't connect to the agent: " + ds);
				return;
			}
			agents.put(ds, agentInfo.getDefaultLoc());
		}
		
		Set<DeployerService> dss = agents.keySet();
		
		// touch check
		for(DeployerService ds : dss){		
			try {
				if(!ds.touchAvailable()){
					timediff = new TimeDiffManager();
					spanmilli = 60000;
				}
			} catch (Exception e) {
				if(error != null)
					error = e;
				return;
			} 
			break;
		}
		
		List<Thread> threads = new ArrayList<Thread>();
		for(final DeployerService ds : dss){
			final AgentLocation loc = agents.get(ds);
			
			final long diffmilli = timediff == null ? 0 : timediff.getDiff(ds);
			
			Thread t = new Thread(){			
				public void run() {
					String agentName = loc.getAgentName();
					try{
						List<FileInfo> fs = ds.listAllFiles(id, loc.getValue());
						for(FileInfo f : fs){
							result.append(f.getPath(), 
									newAgentFile(agentName, f.getPath(), f.size(), 
											f.lastModified() + diffmilli));
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

	public int spanMilli(){
		return this.spanmilli;
	}
	
	class TimeDiffManager {
		private Map<DeployerService, Long> diffs = new HashMap<DeployerService, Long>();
		
		public TimeDiffManager() throws IOException{
			final Set<DeployerService> deploys = agents.keySet();
			
			// get tmp dir
			File tmpdir = FileUtil.temporaryDir();
			
			// get unique file name
			String uniqName = uniqueFile(tmpdir.getPath(), deploys);
			if(uniqName == null)
				throw new IOException("Fail to find unique file name.");
			
			Map<DeployerService, Long> dstimes = Collections.EMPTY_MAP; 
			// get ds times
			dstimes = getDSTimes(deploys, uniqName);
			if(dstimes.size() != deploys.size())
				throw new IOException("Fail to transfer file: " + uniqName);

			// adjust diffs
			long standard = 0;
			for(DeployerService ds : deploys){
				final Long time = dstimes.get(ds);
				if(time == null)
					throw new IOException("Cannot be occured.");
				System.out.println("time: " + time);
				if(standard == 0) {
					standard = time;
				} else {
					diffs.put(ds, standard - time);
				}
			}
		}

		private Map<DeployerService, Long> getDSTimes(Set<DeployerService> deploys, 
				final String uniqName){
			final Map dstimes = new HashMap<DeployerService, Long>();
			try{
				// write to
				for(DeployerService ds : deploys){
					ds.init(agents.get(ds).getValue(), uniqName, 0);
					// some ftp can't transfer 0 byte file
					ds.write(agents.get(ds).getValue(), uniqName, 
							new ByteArray(new byte[4]));	
				}
			}catch(Exception e){
				Logger.error(e);
			}finally{
				// close concurrently
				List<Thread> ths = new ArrayList<Thread>();
				for(final DeployerService ds : deploys){
					ths.add(new Thread(){
						public void run() {
							try { 
								DoneFileInfo info = ds.close(
										agents.get(ds).getValue(), uniqName, null, null); 
								dstimes.put(ds, info.lastModified());
							} catch (Exception e) {
								error = e;
							}
						}
					});
				}
				for(Thread th : ths)
					th.start();
				for(Thread th : ths)
					try { th.join(); } catch (InterruptedException e) { }
				
				for(DeployerService ds : deploys)
					try {
						ds.removeFile(agents.get(ds).getValue(), uniqName);
					} catch (Exception e) {
						if(error != null) error = e;
					}
			}
			return dstimes;
		}
		
		public long getDiff(DeployerService ds){
			Long diff = diffs.get(ds);
			return diff == null ? 0 : diff;
		}
		
		private String uniqueFile(String tmpdir, Set<DeployerService> dss) {
			for(DeployerService ds : dss){
				final String uniq = uniqueFile(tmpdir, ds);
				if(isUnique(uniq, dss))
					return uniq;
			}
			return null;
		}
		
		private boolean isUnique(String name, Set<DeployerService> dss){
			for(DeployerService ds : dss){
				try {
					if(ds.exist(agents.get(ds).getValue(), name))
						return false;
				} catch (Exception e) {
					return false;
				}
			}
			return true;
		}
		
		private String uniqueFile(String tmpdir, DeployerService ds){
			Exception excepn= null;
			
			final int maxiter = 100;
			for(int i=0; i<maxiter; i++){
				try{
					File tmp = new File(tmpdir, "oden" + String.valueOf(i) + ".tmp");
					if(tmp.exists())
						continue;
					
					if(!ds.exist(agents.get(ds).getValue(), tmp.getName()))
						return tmp.getName();
				}catch(Exception e){
					excepn = e;
				}
			}
			
			if(excepn != null)		// write one exception only 
				Logger.error(excepn);
			return null;
		}
	}
}
