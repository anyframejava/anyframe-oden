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
package org.anyframe.oden.bundle.external.deploy;

import java.io.File;
import java.util.Iterator;

import org.anyframe.oden.bundle.common.Assert;
import org.anyframe.oden.bundle.common.BundleUtil;
import org.anyframe.oden.bundle.common.FileUtil;
import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.common.Utils;
import org.anyframe.oden.bundle.core.AgentLoc;
import org.anyframe.oden.bundle.core.DeployFile;
import org.anyframe.oden.bundle.core.DeployFile.Mode;
import org.anyframe.oden.bundle.core.command.DeployerManager;
import org.anyframe.oden.bundle.core.job.DeployFileResolver;
import org.anyframe.oden.bundle.core.job.DeployJob;
import org.anyframe.oden.bundle.core.record.RecordElement2;
import org.anyframe.oden.bundle.core.txmitter.TransmitterService;
import org.anyframe.oden.bundle.deploy.DeployerService;
import org.anyframe.oden.bundle.deploy.DoneFileInfo;
import org.anyframe.oden.bundle.job.log.JobLogService;
import org.osgi.framework.BundleContext;

/**
 * Undo Job & make cancel available.
 * 
 * @author junghwan.hong
 * 
 */
public class ExtUndoJob extends DeployJob {
	protected TransmitterService txmitterService;

	protected JobLogService jobLogger;

	protected DeployerManager deployerManager;

	protected String user;

	protected String bakLoc;

	protected String errorMessage;

	private String txid;
	
	Integer nSuccess = 0;
	
	Integer backupcnt = 0;
	
	private boolean deployExcOpt;
	
	private final String HOME = BundleUtil.odenHome().getPath();
	
	public ExtUndoJob(BundleContext context, String user, String desc,
			String txid, DeployFileResolver resolver) throws OdenException {
		super(context, user, desc, resolver);
		jobLogger = (JobLogService) BundleUtil.getService(context,
				JobLogService.class);
		Assert.check(jobLogger != null, "No proper Service: "
				+ jobLogger.getClass().getName());

		txmitterService = (TransmitterService) BundleUtil.getService(context,
				TransmitterService.class);
		Assert.check(txmitterService != null, "Fail to load service.");

		String undo = context.getProperty("deploy.undo");
		deployerManager = new DeployerManager(context, id,
				(undo != null && undo.equals("true")));
		this.txid = txid;
		this.user = user;
		this.backupcnt = context.getProperty("deploy.backupcnt").equals("") ? 100
				: Integer.valueOf(context.getProperty("deploy.backupcnt"));
		this.deployExcOpt = context.getProperty("deploy.exception.option")
				.equals("true") ? true : false;
	}

	protected void run() {
		try {
			undo();
		} catch (Exception e) {
			setError(e.getMessage());
			Logger.error(e);
		}
	}

	protected void undo() throws OdenException {
		currentWork = "ready to undo...";
		if (deployFiles.size() == 0)
			throw new OdenException("<txid> is not available.");
		Iterator<DeployFile> it = deployFiles.iterator();
		while (!stop && it.hasNext()) {
			DeployFile f = it.next();
			
			try {
				AgentLoc parent = f.getAgent();

				// backup directory setting....
				String oldbak = FileUtil.resolveDotNatationPath(
						FileUtil.combinePath(HOME, FileUtil.combinePath(context
								.getProperty("deploy.undo.loc"), txid)));
				boolean isbak = (new File(oldbak)).exists();
				
				String file = f.getPath();
				String parentLoc = parent.location();
				
				DeployerService ds = txmitterService.getDeployer(parent
						.agentAddr());
				if (ds == null)
					throw new OdenException("Couldn't connect to the agent: "
							+ parent.agentAddr());

				DoneFileInfo d = null;
				boolean isException = deployExcOpt && f.isSuccess() == false ? true
						: false; // deploy.exception.option=true 시 exception 파일은
									// 롤백 제외 
				if(! isException) { 
					if(isbak) {
						if (f.mode() == Mode.ADD ) {
							// 추가된 파일은 삭제
							d = ds.backupNRemove(parentLoc, file, FileUtil.combinePath(context
									.getProperty("deploy.undo.loc"), id), backupcnt);
							
							if (d != null)
								f.setMode(Mode.DELETE);
						} else if (f.mode() == Mode.DELETE || f.mode() == Mode.UPDATE) {
							// 삭제, 갱신 파일은 copy
							d = ds.backupNCopy(oldbak, file, parentLoc,
									FileUtil.combinePath(context
											.getProperty("deploy.undo.loc"), id),
									backupcnt) ;
							
							if (d != null)
								f.setMode(d.isUpdate()? Mode.UPDATE :Mode.ADD);
						}
					} else {
						// initial deploy file
						if(f.mode() == Mode.ADD)
							d = ds.backupNRemove(parentLoc, file, FileUtil.combinePath(
									context.getProperty("deploy.undo.loc"), id),
									backupcnt);
							if (d != null)
								f.setMode(Mode.DELETE);
					}
					if (d != null) {
						f.setSuccess(d.success());
						f.setDate(d.lastModified());
						f.setSize(d.size());
						nSuccess++;
					}
				}
			} catch (Exception e) {
				f.setErrorLog(Utils.rootCause(e));
				Logger.error(e);
			}

		}
	}

	@Override
	public int todoWorks() {
		return totalWorks - additionalWorks;
	}

	protected void done() {
		try {
			RecordElement2 r = new RecordElement2(id, deployFiles, user, System
					.currentTimeMillis(), desc);
			if (errorMessage != null) {
				r.setLog(errorMessage);
				r.setSucccess(false);
			} else if (deployFiles.size() == 0) {
				r.setSucccess(true);
			}
			r.setNSuccess(r.isSuccess() ? deployFiles.size() : nSuccess);
			jobLogger.record(r);
		} catch (OdenException e) {
			Logger.error(e);
		}
		deployFiles.clear();
		deployFiles = null;
	}

	protected void setError(String msg) {
		if (errorMessage == null)
			errorMessage = msg;
	}
}
