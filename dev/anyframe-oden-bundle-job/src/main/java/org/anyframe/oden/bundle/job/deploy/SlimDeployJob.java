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
package org.anyframe.oden.bundle.job.deploy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

import org.anyframe.oden.bundle.common.Assert;
import org.anyframe.oden.bundle.common.BundleUtil;
import org.anyframe.oden.bundle.common.FatInputStream;
import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.common.StringUtil;
import org.anyframe.oden.bundle.common.Utils;
import org.anyframe.oden.bundle.core.RepositoryProviderService;
import org.anyframe.oden.bundle.core.DeployFile.Mode;
import org.anyframe.oden.bundle.core.job.Job;
import org.anyframe.oden.bundle.core.repository.RepositoryService;
import org.anyframe.oden.bundle.deploy.ByteArray;
import org.anyframe.oden.bundle.deploy.DeployerService;
import org.anyframe.oden.bundle.deploy.DoneFileInfo;
import org.anyframe.oden.bundle.job.RepoFile;
import org.anyframe.oden.bundle.job.RepoManager;
import org.anyframe.oden.bundle.job.config.CfgSource;
import org.anyframe.oden.bundle.job.config.CfgTarget;
import org.anyframe.oden.bundle.job.config.CfgUtil;
import org.anyframe.oden.bundle.job.log.JobLogService;

/**
 * 
 * Job to deploying by task. 
 * 
 * @author joon1k
 *
 */
public class SlimDeployJob extends Job {
	List<CfgTarget> targets;
	Map<String, CfgTarget> targetCfgMap = new HashMap<String, CfgTarget>();
	String user;
	SlimDeployFileResolver resolver;
	Collection<SlimDeployFile> deployFiles = Collections.EMPTY_SET;
	
	BundleContext ctx;
	JobLogService jobLogger;
	RepoManager reposvc;
	
	String backupLocation;
	boolean useTmp = true;
	String errorMessage;
	
	Integer nSuccess = 0;
	
	public SlimDeployJob(BundleContext context, CfgSource source, 
			String user, String desc, List<CfgTarget> targets, 
			SlimDeployFileResolver resolver) throws OdenException {
		super(context, desc);
		this.ctx = context;
		this.targets = targets;
		this.user = user;
		this.resolver = resolver;
		
		for(CfgTarget t : targets)
			targetCfgMap.put(t.getName(), t);
		
		// to record deploy logs
		jobLogger = (JobLogService) BundleUtil.getService(
				context, JobLogService.class);
		Assert.check(jobLogger != null, "No proper Service: " 
				+ jobLogger.getClass().getName());
		
		// repository service
		RepositoryProviderService repoProvider = 
			(RepositoryProviderService) BundleUtil.getService(
					context, RepositoryProviderService.class);
		Assert.check(repoProvider != null, 
				"No proper Service: " 
				+ repoProvider.getClass().getName());
		this.reposvc = getRepoManager(repoProvider, source);
		
		backupLocation = getBackupLocation(context);
		
		this.useTmp = "true".equals(context.getProperty("deploy.tmpfile"));
	}

	
	private String getBackupLocation(BundleContext ctx){
		String undo = ctx.getProperty("deploy.undo");
		if(!"true".equals(undo)) return null;
		
		String loc = ctx.getProperty("deploy.undo.loc");
		return loc == null ? "snapshots" : loc;
	}
	
	
	private RepoManager getRepoManager(RepositoryProviderService repoProvider, 
			CfgSource source) throws OdenException{
		String[] args = CfgUtil.toRepoArg(source);
		RepositoryService repoSvc = repoProvider.getRepoServiceByURI(args);
		if (repoSvc == null)
			throw new OdenException("Invalid Repository: " + args);
		return new RepoManager(repoSvc, args);
	}
	
	
	@Override
	protected void run() {
		currentWork = "resolving deploy files...";
		totalWorks = -1;
		
		try {
			deployFiles = resolver.resolveDeployFiles();
		} catch (Exception e) {
			setError(e.getMessage());
			Logger.error(e);
			return;
		}
		
		totalWorks = deployFiles.size()+additionalWorks;
		finishedWorks = 1;	// 1 is kind of addtional work
		
		try {
			deploy();
		} catch (Exception e) {
			setError(e.getMessage());
			Logger.error(e);
			return;
		}
		
		finishedWorks = totalWorks -1;	//1 is kind of additional work
	}
	
	private Map<RepoFile, Collection<SlimDeployFile>> groupByPath(){
		Map<RepoFile, Collection<SlimDeployFile>> ret 
				= new HashMap<RepoFile, Collection<SlimDeployFile>>();
		for(SlimDeployFile f : deployFiles){
			RepoFile rf = new RepoFile("", f.getPath());
			Collection<SlimDeployFile> fs = ret.get(rf);
			if(fs == null)
				fs = new HashSet<SlimDeployFile>();
			fs.add(f);
			ret.put(rf, fs);
		}
		return ret;
	}
	
	protected void deploy() throws OdenException {
		// files having same path and diff agent
		currentWork = "ready to deploy...";

		String undo = context.getProperty("deploy.undo");
		int backupcnt = context.getProperty("deploy.backupcnt").equals("") ? 100
				: Integer.valueOf(context.getProperty("deploy.backupcnt"));
		
		SlimDeployManager deployMgr = 
			new SlimDeployManager(context, id, "true".equals(undo));
		
		Map<RepoFile, Collection<SlimDeployFile>> fs = groupByPath();
		for(RepoFile rf : fs.keySet()) {
			if(stop) break;
			currentWork = rf.getFile();
			
			// init deployer
			Map<SlimDeployFile, DeployerService> inProgressFiles 
					= Collections.synchronizedMap(
							new HashMap<SlimDeployFile, DeployerService>());
			
			Collection<SlimDeployFile> sameFiles = fs.get(rf);			
			long t = System.currentTimeMillis();
			for(SlimDeployFile f : sameFiles){				
				if(f.getMode() == Mode.NA)
					continue;
				
				try{
					DeployerService ds = deployMgr.getDeployer(
							targetCfgMap.get(f.getTarget()).getAddress());
					if(ds == null)
						throw new OdenException("Invalid target: " 
								+ f.getTarget());
			
					if(f.getMode() == Mode.DELETE){
						ds.backupNRemove(targetCfgMap.get(f.getTarget()).getPath(), 
								f.getPath(), backupLocation , backupcnt);
						f.setSuccess(true);
						nSuccess++;
					}else{	// add or update
						try{
							ds.init(targetCfgMap.get(f.getTarget()).getPath(), 
									f.getPath(), t, useTmp , 0);
						}catch(Exception e){
							try{ 
								ds.close(targetCfgMap.get(f.getTarget()).getPath(),
										f.getPath(), null, null);
							}catch(Exception ee){
							}
							throw e;	
						}
						inProgressFiles.put(f, ds);	
					}
				}catch(Exception e){
					Logger.error(e);
					setError(e.getMessage());
					f.setError(Utils.rootCause(e));
				}
			}
			if(inProgressFiles.size() == 0){	// no add or update
				finishedWorks += sameFiles.size();
				continue;
			}

			// get inputstream to write
			FatInputStream in = null;
			try {
				in = reposvc.resolve(rf);
			} catch(OdenException e){
				for(SlimDeployFile f : fs.get(rf)){	
					f.setError(Utils.rootCause(e));
				}
				Logger.error(e);
				setError(e.getMessage());
				
				try { if(in != null) in.close(); } catch (IOException ioe) { }
				break;
			}
			
			if(hasSameTargets(inProgressFiles))
				writeDeployFiles(in, inProgressFiles);
			else
				writeDeployFilesAsThread(in, inProgressFiles);
						
			closeDeployFiles(inProgressFiles, in.size());
			
			touchOtherTargets(inProgressFiles, t, deployMgr);
			
			try { if(in != null) in.close(); } catch (IOException e) { }
			
			finishedWorks += sameFiles.size();
		}
		
		// close repository service
		if(reposvc != null) reposvc.close();
	}

	private boolean hasSameTargets(Map<SlimDeployFile, DeployerService> fmap){
		String addr = null;
		for(SlimDeployFile f : fmap.keySet()){
			String _addr = targetCfgMap.get(f.getTarget()).getAddress();
			if(addr == null)
				addr = _addr;
			else if(!_addr.equals(addr))
				return false;
		}
		return true;
		
	}
	
	/**
	 *  When deploying files with update mode, some files will not be
	 *  deployed to some targets. Because those targets already have those
	 *  files. In that case, those files' last modified date become to be
	 *  different.
	 * 
	 * @param fmap
	 * @param date
	 */
	private void touchOtherTargets(Map<SlimDeployFile, DeployerService> fmap, 
			long date, SlimDeployManager deployMgr) {
		// get path & other AgentLocs from DeployFiles
		String _path = null;
		Collection<CfgTarget> _targets = new HashSet<CfgTarget>();
		for(SlimDeployFile f : fmap.keySet()){
			if(_path == null) _path = f.getPath();
			_targets.add(targetCfgMap.get(f));
		}

		for(CfgTarget t : otherTargets(_targets)){
			DeployerService ds = deployMgr.getDeployer(t.getAddress());
			if(ds == null) continue;
			
			try{
				ds.setDate(t.getPath(), _path, date);
			}catch(Exception e){
				Logger.error(e);
			}
		}
	}
	
	private List<CfgTarget> otherTargets(Collection<CfgTarget> _targets){
		if(targets.size() == _targets.size())
			return Collections.EMPTY_LIST;
		
		List<CfgTarget> ret = new ArrayList<CfgTarget>();
		for(CfgTarget t : targets){
			if(!_targets.contains(t))
				ret.add(t);
		}
		return ret;
	}

	protected SlimDeployFile getSameTargetFile(Collection<SlimDeployFile> fs, 
			CfgTarget t){
		for(SlimDeployFile f : fs)
			if(t.equals(targetCfgMap.get(f)) )
				return f;
		return null;
	}
	
	protected void writeDeployFiles(FatInputStream in, 
			Map<SlimDeployFile, DeployerService> fmap) {
		try{
			// add or update
			byte[] buf = new byte[1024*64];
			int size = 0;
			while((size = in.read(buf)) != -1){
				for(final SlimDeployFile f : fmap.keySet()){
					DeployerService ds = fmap.get(f);
					try{
						if(!ds.write(targetCfgMap.get(f).getPath(), 
								f.getPath(), new ByteArray(buf, size)))
							throw new OdenException("Fail to write: " + 
									f.getPath());
					}catch(Exception e){	// while writing..
						fmap.remove(f);
						f.setSuccess(false);
						f.setError(Utils.rootCause(e));
					}		
				} 
			}
		} catch (Exception e) {	// while reading
			for(SlimDeployFile f : fmap.keySet()){	
				f.setSuccess(false);
				f.setError("Fail to write: " + 
						f.getPath());
			}
			Logger.error(e);
			setError(e.getMessage());
		}
	}
	
	protected void writeDeployFilesAsThread(FatInputStream in, 
			final Map<SlimDeployFile, DeployerService> fmap) {
		// add or update
		byte[] buf = new byte[1024*64];
		int size = 0;
		try{
		while((size = in.read(buf)) != -1){
			final ByteArray ba = new ByteArray(buf, size);
			List<Thread> ths = new ArrayList<Thread>();
			for(final SlimDeployFile f : fmap.keySet())
				ths.add(new Thread(){
					public void run(){
						DeployerService ds = fmap.get(f);
						try{
							if(!ds.write(targetCfgMap.get(f).getPath(), 
									f.getPath(), ba))
								throw new OdenException("Fail to write: " + 
										f.getPath());
						}catch(Exception e){	// while writing..
							fmap.remove(f);
							f.setSuccess(false);
							f.setError(Utils.rootCause(e));
						}	
					}
				});
			
			for(Thread th : ths)
				th.start();
			
			for(Thread th : ths)
				try{ th.join(); }catch(InterruptedException e){}				
		}
		} catch (Exception e) {	// while reading
			for(SlimDeployFile f : fmap.keySet()){	
				f.setSuccess(false);
				f.setError("Fail to write: " + f.getPath());
			}
			Logger.error(e);
			setError(e.getMessage());
		}
	}
	
	protected void closeDeployFiles(Map<SlimDeployFile, DeployerService> fmap,
			final long originalFileSz) {
		List<Thread> threads = new ArrayList<Thread>();
		
		for(final SlimDeployFile f : fmap.keySet()){
			final DeployerService ds = fmap.get(f);
			Thread th = new Thread(){			
				public void run() {
					try {
						DoneFileInfo info = ds.close(targetCfgMap.get(f).getPath(), 
								f.getPath(), null, backupLocation);
						if(info == null || info.size() == -1L)
							throw new IOException("Fail to close: " + 
									f.getPath());
						if(info.size() != originalFileSz)
							throw new IOException("Diffrent size: " + 
									info.size() + "/" + originalFileSz);
						f.setSuccess(true);
						synchronized (nSuccess) {
							nSuccess++;	
						}
						f.setMode(info.isUpdate() ? Mode.UPDATE : Mode.ADD);
					} catch (Exception e) {
						f.setSuccess(false);
						if(StringUtil.empty(f.getError())) 
							f.setError(e.getMessage());
						Logger.error(e);
						setError(e.getMessage());
					}
				}
			};
			threads.add(th);
			th.start();
		}
		
		for(Thread th : threads){
			try {
				th.join();
			} catch (InterruptedException e) {
			}
		}
	}
	
	protected void done(){
		try {
			jobLogger.record(id, user, System.currentTimeMillis(), desc, 
					nSuccess.intValue(), errorMessage, deployFiles);
		} catch (OdenException e) {
			Logger.error(e);
		}
		deployFiles.clear();
		deployFiles = null;
	}
	
	protected void setError(String msg){
		if(errorMessage == null)
			errorMessage = msg;
	}
}
