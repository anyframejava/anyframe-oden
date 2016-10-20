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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;

import anyframe.common.bundle.log.Logger;
import anyframe.oden.bundle.common.Assert;
import anyframe.oden.bundle.common.BundleUtil;
import anyframe.oden.bundle.common.FatInputStream;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.DeployFileUtil;
import anyframe.oden.bundle.core.Repository;
import anyframe.oden.bundle.core.RepositoryProviderService;
import anyframe.oden.bundle.core.DeployFile.Mode;
import anyframe.oden.bundle.core.job.DeployJob;
import anyframe.oden.bundle.deploy.DeployerService;
import anyframe.oden.bundle.deploy.DoneFileInfo;
import anyframe.oden.bundle.repository.RepositoryService;

public class TaskDeployJob extends DeployJob {
	
	protected RepositoryProviderService repositoryProvider;
	
	public TaskDeployJob(BundleContext context, Set<DeployFile> dfiles,
			String user) throws OdenException {
		super(context, dfiles, user);
		repositoryProvider = (RepositoryProviderService) BundleUtil.getService(context, RepositoryProviderService.class);
		Assert.check(repositoryProvider != null, "Fail to load service." + repositoryProvider.getClass().getName());
	}

	
	protected void run() throws OdenException{
		final Map<Repository, Set<DeployFile>> rdfs = DeployFileUtil.groupByRepository(deployFiles);
		
		Iterator<Repository> i = rdfs.keySet().iterator();
		while(!stop && i.hasNext()){
			Repository repo = i.next();
			deploy(repo, rdfs.get(repo), user);
		}
	}
	
	protected void deploy(Repository repo, Set<DeployFile> repofiles, String user) throws OdenException {		
		RepositoryService reposvc = repositoryProvider.getRepoServiceByURI(repo.args());
		if(reposvc == null)
			throw new OdenException("Couldn't find a RepositoryService for " + repo);
		
		// files having same path and diff agent
		Map<String, Set<DeployFile>> fs = DeployFileUtil.groupByPath(repofiles);
		
		Iterator<String> it = fs.keySet().iterator();
		while(!stop && it.hasNext()) {
			String path = it.next();
			Set<DeployFile> files = fs.get(path);
			
			FatInputStream in = null;
			Map<DeployFile, DeployerService> initializedDeployers = new HashMap<DeployFile, DeployerService>();
			try{		
				// init deployer
				for(DeployFile f : files){
					DeployerService ds = deployerManager.getDeployer(f.getAgent().agentAddr());
					if(txmitterService.readyToDeploy(ds, f));
						initializedDeployers.put(f, ds);
				}
				
				// write or delete
				in = reposvc.resolve(repo.args(), path);
				byte[] buf = new byte[1024*8];
				int size = 0;
				while((size = in.read(buf)) != -1){
					for(DeployFile f : files){
						if(f.mode() == Mode.ADD || f.mode() == Mode.UPDATE){	
							DeployerService deployer = initializedDeployers.get(f);
							if(deployer != null){
								if(!deployer.write(buf, size))
									throw new Exception("Fail to write: " + f.getPath());
							}
						}
					}
				}
			} catch (Exception e) {
				Logger.error(e);
			}
			
			// close
			for(DeployFile f : files){
				DeployerService deployer = initializedDeployers.get(f);
				if(deployer != null){
					try {
						DoneFileInfo info = deployer.close(new ArrayList<String>(), f.backupLocation() );
						if(info.size() == -1L)
							throw new IOException("Fail to close: " + f.getPath());
						if(info.size() == in.size())
							f.setSuccess(true);
						f.setSize(info.size());
						f.setMode(info.isUpdate() ? Mode.UPDATE : Mode.ADD);
						f.setDate(info.lastModified());
					} catch (IOException e) {
						Logger.error(e);
					}
				}
			}
			try { if(in != null) in.close(); } catch (IOException e) { }		
		}
		
		// close repository service
		reposvc.close(repo.args());
	}
	
	protected void done(Exception e){
		super.done(e);
	}
}
