/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.anyframe.oden.bundle.job.deploy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.anyframe.oden.bundle.common.Assert;
import org.anyframe.oden.bundle.common.BundleUtil;
import org.anyframe.oden.bundle.common.FatInputStream;
import org.anyframe.oden.bundle.common.FileUtil;
import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.common.StringUtil;
import org.anyframe.oden.bundle.common.Utils;
import org.anyframe.oden.bundle.core.AgentLoc;
import org.anyframe.oden.bundle.core.DeployFile;
import org.anyframe.oden.bundle.core.DeployFile.Mode;
import org.anyframe.oden.bundle.core.RepositoryProviderService;
import org.anyframe.oden.bundle.core.command.DeployerManager;
import org.anyframe.oden.bundle.core.job.DeployFileResolver;
import org.anyframe.oden.bundle.core.job.DeployJob;
import org.anyframe.oden.bundle.core.record.RecordElement2;
import org.anyframe.oden.bundle.core.repository.RepositoryService;
import org.anyframe.oden.bundle.core.txmitter.DeployerHelper;
import org.anyframe.oden.bundle.core.txmitter.TransmitterService;
import org.anyframe.oden.bundle.deploy.ByteArray;
import org.anyframe.oden.bundle.deploy.DeployerService;
import org.anyframe.oden.bundle.deploy.DoneFileInfo;
import org.anyframe.oden.bundle.job.RepoManager;
import org.anyframe.oden.bundle.job.log.JobLogService;
import org.osgi.framework.BundleContext;

/**
 * ReDeploy Job & make cancel available.
 * 
 * @author Junghwan Hong
 */
public class RerunJob extends DeployJob {
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

	private String backupLocation;

	// RepoManager reposvc;

	RepositoryProviderService repositoryProvider;

	public RerunJob(BundleContext context, String user, String desc,
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
		this.backupLocation = context.getProperty("deploy.undo.loc");
		repositoryProvider = (RepositoryProviderService) BundleUtil.getService(
				context, RepositoryProviderService.class);
	}

	protected void run() {
		try {
			rerun();
		} catch (Exception e) {
			setError(e.getMessage());
			Logger.error(e);
		}
	}

	@SuppressWarnings("PMD")
	protected void rerun() throws OdenException {
		currentWork = "ready to reDeploy...";
		if (deployFiles.size() == 0) {
			throw new OdenException("<txid> is not available.");
		}
		Iterator<DeployFile> it = deployFiles.iterator();
		while (!stop && it.hasNext()) {
			DeployFile f = it.next();
			long t = System.currentTimeMillis();
			Map<DeployFile, DeployerService> inProgressFiles = Collections
					.synchronizedMap(new HashMap<DeployFile, DeployerService>());
			try {
				AgentLoc parent = f.getAgent();
				DeployerService ds = txmitterService.getDeployer(parent
						.agentAddr());
				if (ds == null) {
					throw new OdenException("Couldn't connect to the agent: "
							+ parent.agentAddr());
				}

				DoneFileInfo d = null;

				if (f.mode() == Mode.DELETE) {
					// delete mode
					d = ds.backupNRemove(
							f.getAgent().location(),
							f.getPath(),
							backupLocation.equals("snapshot") ? null : FileUtil
									.combinePath(backupLocation, id), backupcnt);

					f.setSuccess(true);
					nSuccess++;
				} else {
					// add, update mode
					f.setDate(t);
					DeployerHelper.readyToDeploy(ds, f, true, backupcnt,false);
					inProgressFiles.put(f, ds);
				}

				FatInputStream in = null;
				String[] args = f.getRepo().args();
				RepositoryService repoSvc = repositoryProvider
						.getRepoServiceByURI(args);
				if (repoSvc == null) {
					throw new OdenException("Invalid Repository: " + args);
				}
				RepoManager repoMgr = new RepoManager(repoSvc, args);

				in = repoMgr.resolve(FileUtil.combinePath(
						f.getRepo().args()[0], f.getPath()));
				writeDeployFiles(in, inProgressFiles);

				closeDeployFiles(inProgressFiles, in.size());

				touchOtherTargets(inProgressFiles, t);

				// boolean isException = deployExcOpt && f.isSuccess() == false
				// ? true
				// : false; // deploy.exception.option=true 시 exception 파일은
				// 롤백 제외

			} catch (Exception e) {
				f.setErrorLog(Utils.rootCause(e));
				Logger.error(e);
				setError(e.getMessage());
			}

		}
	}

	@Override
	public int todoWorks() {
		return totalWorks - additionalWorks;
	}

	@SuppressWarnings("PMD")
	protected void done() {
		try {
			RecordElement2 r = new RecordElement2(id, deployFiles, user,
					System.currentTimeMillis(), desc);
			if (errorMessage != null) {
				r.setLog(errorMessage);
				r.setSucccess(false);
				// } else if (deployFiles.size() == 0) {
			} else {
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
		if (errorMessage == null) {
			errorMessage = msg;
		}
	}

	@SuppressWarnings("PMD")
	protected void writeDeployFiles(FatInputStream in,
			Map<DeployFile, DeployerService> fmap) {
		try {
			// add or update
			byte[] buf = new byte[1024 * 64];
			int size = 0;
			while ((size = in.read(buf)) != -1) {
				for (final DeployFile f : fmap.keySet()) {
					DeployerService ds = fmap.get(f);
					try {
						if (!DeployerHelper.write(ds, f, new ByteArray(buf,
								size))) {
							throw new OdenException("Fail to write: "
									+ f.getPath());
						}
					} catch (Exception e) { // while writing..
						fmap.remove(f);
						f.setSuccess(false);
						f.setErrorLog(Utils.rootCause(e));
					}
				}
			}
		} catch (Exception e) { // while reading
			for (DeployFile f : fmap.keySet()) {
				f.setSuccess(false);
				f.setErrorLog("Fail to write: " + f.getPath());
			}
			Logger.error(e);
			setError(e.getMessage());
		}
	}

	@SuppressWarnings("PMD")
	protected void closeDeployFiles(Map<DeployFile, DeployerService> fmap,
			final long originalFileSz) {
		List<Thread> threads = new ArrayList<Thread>();

		for (final DeployFile f : fmap.keySet()) {
			final DeployerService deployer = fmap.get(f);
			Thread th = new Thread() {
				public void run() {
					try {
						DoneFileInfo info = DeployerHelper.close(
								deployer,
								f,
								null,
								backupLocation.equals("snapshot") ? null
										: FileUtil.combinePath(backupLocation,
												id));
						if (info == null || info.size() == -1L)
							throw new IOException("Fail to close: "
									+ f.getPath());
						if (info.size() != originalFileSz)
							throw new IOException("Diffrent size: "
									+ info.size() + "/" + originalFileSz);
						f.setSuccess(true);
						synchronized (nSuccess) {
							nSuccess++;
						}
						f.setMode(info.isUpdate() ? Mode.UPDATE : Mode.ADD);
					} catch (Exception e) {
						f.setSuccess(false);
						if (StringUtil.empty(f.errorLog()))
							f.setErrorLog(e.getMessage());
						Logger.error(e);
						setError(e.getMessage());
					}
				}
			};
			threads.add(th);
			th.start();
		}

		for (Thread th : threads) {
			try {
				th.join();
			} catch (InterruptedException e) {
			}
		}
	}

	private void touchOtherTargets(Map<DeployFile, DeployerService> fmap,
			long date) {
		// get path & other AgentLocs from DeployFiles
		String _path = null;
		Collection<AgentLoc> _targets = new HashSet<AgentLoc>();
		for (DeployFile f : fmap.keySet()) {
			if (_path == null) {
				_path = f.getPath();
			}
			_targets.add(f.getAgent());
		}

		for (AgentLoc t : _targets) {
			DeployerService ds = deployerManager.getDeployer(t.agentAddr());
			if (ds == null) {
				continue;
			}

			try {
				ds.setDate(t.location(), _path, date);
			} catch (Exception e) {
				Logger.error(e);
			}
		}
	}

}
