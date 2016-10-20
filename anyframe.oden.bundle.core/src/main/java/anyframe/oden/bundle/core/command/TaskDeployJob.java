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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;

import anyframe.oden.bundle.common.Assert;
import anyframe.oden.bundle.common.BundleUtil;
import anyframe.oden.bundle.common.FatInputStream;
import anyframe.oden.bundle.common.Logger;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.StringUtil;
import anyframe.oden.bundle.common.Utils;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.DeployFileUtil;
import anyframe.oden.bundle.core.Repository;
import anyframe.oden.bundle.core.RepositoryProviderService;
import anyframe.oden.bundle.core.DeployFile.Mode;
import anyframe.oden.bundle.core.job.DeployFileResolver;
import anyframe.oden.bundle.core.job.DeployJob;
import anyframe.oden.bundle.core.repository.RepositoryService;
import anyframe.oden.bundle.core.txmitter.DeployerHelper;
import anyframe.oden.bundle.deploy.ByteArray;
import anyframe.oden.bundle.deploy.DeployerService;
import anyframe.oden.bundle.deploy.DoneFileInfo;

/**
 * 
 * Job to deploying by task. 
 * 
 * @author joon1k
 *
 */
public class TaskDeployJob extends DeployJob {
	
	protected RepositoryProviderService repositoryProvider;
	
	protected Map<DeployFile, DeployerService> inProgressFiles = Collections.EMPTY_MAP;
	
	protected Set<DeployFile> sameNameFiles = Collections.EMPTY_SET;
	
	protected FatInputStream in = null;
	
	public TaskDeployJob(BundleContext context, String user, String desc,
			DeployFileResolver resolver) throws OdenException {
		super(context, user, desc, resolver);
		repositoryProvider = (RepositoryProviderService) BundleUtil.getService(context, RepositoryProviderService.class);
		Assert.check(repositoryProvider != null, "Fail to load service." + repositoryProvider.getClass().getName());
	}

	protected void run() {
		final Map<Repository, Set<DeployFile>> rdfs = DeployFileUtil.groupByRepository(deployFiles);
		
		Iterator<Repository> i = rdfs.keySet().iterator();
		while(!stop && i.hasNext()){
			Repository repo = i.next();
			try {
				deploy(repo, rdfs.get(repo), user);
			} catch (OdenException e) {
				if(errorMessage == null) // record first error log only
					errorMessage = e.getMessage();
				Logger.error(e);
			}
		}
	}
	
	protected void deploy(Repository repo, Set<DeployFile> repofiles, String user) throws OdenException {
		RepositoryService reposvc = null;
		String[] rargs = repo.args();
		if(rargs == null || rargs.length > 0){
			reposvc = repositoryProvider.getRepoServiceByURI(repo.args());
			if(reposvc == null)
				throw new OdenException("Couldn't find a RepositoryService for " + repo);
		}
		
		// files having same path and diff agent
		Map<String, Set<DeployFile>> fs = DeployFileUtil.groupByPath(repofiles);
		for(String path : fs.keySet()) {
			if(stop) break;
			
			inProgressFiles = new HashMap<DeployFile, DeployerService>();
			sameNameFiles = fs.get(path);
			
			// init deployer
			long t = System.currentTimeMillis();
			for(DeployFile f : sameNameFiles){
				if(f.mode() == Mode.NA){
					// do nothing
				}else if(f.mode() == Mode.DELETE){
					try{
						DeployerService ds = deployerManager.getDeployer(f);
						if(ds == null)
							throw new OdenException("Couldn't connect to the agent: " + f.getAgent());
						
						ds.backupNRemove(f.getAgent().location(), f.getPath(), deployerManager.backupLocation(f));
						f.setSuccess(true);
					}catch(Exception e){
						Logger.error(e);
						f.setErrorLog(Utils.rootCause(e));
						f.setSuccess(false);
					}
				}else if(reposvc != null){
					DeployerService ds = deployerManager.getDeployer(f);
					f.setDate(t);
					try{
						if(ds != null){
							DeployerHelper.readyToDeploy(ds, f);
							inProgressFiles.put(f, ds);
						}else{ 
							f.setErrorLog("Couldn't access the agent: " + f.getAgent().agentName());
						}
					}catch(Exception e){
						f.setErrorLog(e.getMessage());
					}
				}
			}
			if(inProgressFiles.size() == 0)	// no add or update
				continue;
			
			try {
				in = reposvc.resolve(repo.args(), path);
			} catch(OdenException e){
				for(DeployFile f : inProgressFiles.keySet()){	
					f.setSuccess(false);
					f.setErrorLog(Utils.rootCause(e));
				}
				Logger.error(e);
				
				try { if(in != null) in.close(); } catch (IOException ioe) { }
				break;
			}
			
			writeDeployFiles();

			closeDeployFiles();
			
			try { if(in != null) in.close(); } catch (IOException e) { }
			
			finishedWorks += sameNameFiles.size();
		}
		
		// close repository service
		if(reposvc != null) reposvc.close(repo.args());
	}

	protected void writeDeployFiles() {
		try{
			// add or update
			byte[] buf = new byte[1024*8];
			int size = 0;
			while((size = in.read(buf)) != -1){
				for(DeployFile f : sameNameFiles){	
					DeployerService deployer = inProgressFiles.get(f);
					try{
						if(deployer != null && 
								!DeployerHelper.write(deployer, f, new ByteArray(buf, size)))
							throw new OdenException("Fail to write: " + f.getPath());
					}catch(Exception e){	// while writing..
						inProgressFiles.remove(f);
						f.setSuccess(false);
						f.setErrorLog(Utils.rootCause(e));
					}
				}
			}
		} catch (Exception e) {	// while read..
			for(DeployFile f : sameNameFiles){	
				f.setSuccess(false);
				f.setErrorLog("Fail to write: " + f.getPath());
			}
			Logger.error(e);
		}
	}
	
	protected void closeDeployFiles() {
		List<Thread> threads = new ArrayList<Thread>();
		
		for(final DeployFile f : sameNameFiles){
			final DeployerService deployer = inProgressFiles.get(f);
			if(deployer == null)
				continue;
			Thread th = new Thread(){			
				public void run() {
					try {
						DoneFileInfo info = DeployerHelper.close(
								deployer, f, null, deployerManager.backupLocation(f));
						if(info == null || info.size() == -1L)
							throw new IOException("Fail to close: " + f.getPath());
						if(info.size() == in.size())
							f.setSuccess(true);
						f.setSize(info.size());
						f.setMode(info.isUpdate() ? Mode.UPDATE : Mode.ADD);
						f.setDate(info.lastModified());
					} catch (Exception e) {
						f.setSuccess(false);
						if(StringUtil.empty(f.errorLog())) 
							f.setErrorLog(e.getMessage());
						Logger.error(e);
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
}
