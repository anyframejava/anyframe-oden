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

import java.util.Set;

import org.osgi.framework.BundleContext;

import anyframe.common.bundle.log.Logger;
import anyframe.oden.bundle.common.Assert;
import anyframe.oden.bundle.common.BundleUtil;
import anyframe.oden.bundle.common.FileUtil;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.command.DeployerManager;
import anyframe.oden.bundle.core.config.OdenConfigService;
import anyframe.oden.bundle.core.record.DeployLogService2;
import anyframe.oden.bundle.core.record.RecordElement2;
import anyframe.oden.bundle.core.txmitter.TransmitterService;

/**
 * Job to write log & make cancel available.
 * 
 * @author joon1k
 *
 */
public abstract class DeployJob extends Job{
	
	protected TransmitterService txmitterService;
	
	protected OdenConfigService configService;
	
	protected DeployLogService2 deploylog;
	
	protected DeployerManager deployerManager;

	protected Set<DeployFile> deployFiles;
	
	protected String user;
	
	
	public DeployJob(BundleContext context, Set<DeployFile> dfiles, String user) throws OdenException{
		super(context);
		txmitterService = (TransmitterService) BundleUtil.getService(context, TransmitterService.class);
		configService = (OdenConfigService) BundleUtil.getService(context, OdenConfigService.class);
		deploylog = (DeployLogService2) BundleUtil.getService(context, DeployLogService2.class);
		Assert.check(txmitterService != null && configService != null && deploylog != null, "Fail to load service.");
		
		deployerManager = new DeployerManager(txmitterService);
		this.deployFiles = dfiles;
		this.user = user;
		
		for(DeployFile d : deployFiles)
			d.setBackupLocation(FileUtil.combinePath(
					configService.getBackupLocation(d.getAgent().agentName()), id));
	}
	
	protected void done(Exception e){
		if(e != null)
			Logger.error(e);
		
		try {
			deploylog.record(new RecordElement2(id, deployFiles, user, 
					System.currentTimeMillis(), desc));
		} catch (OdenException e1) {
			Logger.error(e1);
		}
	}	
}
