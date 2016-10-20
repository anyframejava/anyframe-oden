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
import java.util.List;
import java.util.Map;

import org.anyframe.oden.bundle.common.Assert;
import org.anyframe.oden.bundle.common.BundleUtil;
import org.anyframe.oden.bundle.common.CipherUtil;
import org.anyframe.oden.bundle.common.FatInputStream;
import org.anyframe.oden.bundle.common.FileUtil;
import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.common.StringUtil;
import org.anyframe.oden.bundle.common.Utils;
import org.anyframe.oden.bundle.core.AgentLoc;
import org.anyframe.oden.bundle.core.DeployFile;
import org.anyframe.oden.bundle.core.RepositoryProviderService;
import org.anyframe.oden.bundle.core.DeployFile.Mode;
import org.anyframe.oden.bundle.core.job.DeployFileResolver;
import org.anyframe.oden.bundle.core.job.DeployJob;
import org.anyframe.oden.bundle.core.record.RecordElement2;
import org.anyframe.oden.bundle.core.repository.RepositoryService;
import org.anyframe.oden.bundle.core.txmitter.DeployerHelper;
import org.anyframe.oden.bundle.deploy.ByteArray;
import org.anyframe.oden.bundle.deploy.DeployerService;
import org.anyframe.oden.bundle.deploy.DoneFileInfo;
import org.anyframe.oden.bundle.job.RepoFile;
import org.anyframe.oden.bundle.job.RepoManager;
import org.anyframe.oden.bundle.job.config.CfgSource;
import org.anyframe.oden.bundle.job.config.CfgTarget;
import org.anyframe.oden.bundle.job.config.CfgUtil;
import org.anyframe.oden.bundle.job.log.JobLogService;
import org.osgi.framework.BundleContext;

/**
 * Job to deploying by task.
 * 
 * @author Junghwan Hong
 */
@SuppressWarnings("PMD")
public class JobDeployJob extends DeployJob {
	RepositoryProviderService repositoryProvider;
	String backupLocation = null;
	Integer nSuccess = 0;
	JobLogService jobLogger;
	List<CfgTarget> targets = null;
	RepoManager reposvc;
	boolean useTmp = true;
	int backupcnt;
	String securitykey = "";

	public JobDeployJob(BundleContext context, CfgSource source, String user,
			String desc, List<CfgTarget> targets, DeployFileResolver resolver)
			throws OdenException {
		super(context, user, desc, resolver);
		this.targets = targets;

		repositoryProvider = (RepositoryProviderService) BundleUtil.getService(
				context, RepositoryProviderService.class);
		Assert.check(repositoryProvider != null, "Fail to load service."
				+ repositoryProvider.getClass().getName());

		jobLogger = (JobLogService) BundleUtil.getService(context,
				JobLogService.class);
		Assert.check(jobLogger != null, "No proper Service: "
				+ jobLogger.getClass().getName());

		backupLocation = getBackupLocation(context);
		this.reposvc = getRepoManager(source);
		this.useTmp = "true".equals(context.getProperty("deploy.tmpfile"));
		this.backupcnt = context.getProperty("deploy.backupcnt").equals("") ? 100
				: Integer.valueOf(context.getProperty("deploy.backupcnt"));

		if (context.getProperty("security.key") != null) {
			securitykey = context.getProperty("security.key");
		}

	}

	String getBackupLocation(BundleContext ctx) {
		String undo = ctx.getProperty("deploy.undo");
		if (!"true".equals(undo))
			return "snapshot";

		String loc = ctx.getProperty("deploy.undo.loc");
		return loc == null ? "snapshots" : loc;
	}

	protected void run() {
		try {
			deploy(deployFiles);
		} catch (Exception e) {
			setError(e.getMessage());
			Logger.error(e);
		}
	}

	private RepoManager getRepoManager(CfgSource source) throws OdenException {
		String[] args = CfgUtil.toRepoArg(source);
		RepositoryService repoSvc = repositoryProvider
				.getRepoServiceByURI(args);
		if (repoSvc == null)
			throw new OdenException("Invalid Repository: " + args);
		return new RepoManager(repoSvc, args);
	}

	private Map<RepoFile, Collection<DeployFile>> groupByPath(
			Collection<DeployFile> files) {
		Map<RepoFile, Collection<DeployFile>> ret = new HashMap<RepoFile, Collection<DeployFile>>();
		for (DeployFile f : files) {
			RepoFile rf = new RepoFile("", f.getPath());
			Collection<DeployFile> fs = ret.get(rf);
			if (fs == null)
				fs = new HashSet<DeployFile>();
			fs.add(f);
			ret.put(rf, fs);
		}
		return ret;
	}

	protected void deploy(Collection<DeployFile> repofiles)
			throws OdenException {
		// files having same path and diff agent
		currentWork = "ready to deploy...";
//		Map<RepoFile, Collection<DeployFile>> fs = groupByPath(repofiles);
		Map<RepoFile, Collection<DeployFile>> fs = Collections.synchronizedMap(groupByPath(repofiles)); 
			
		for (RepoFile rf : fs.keySet()) {
			if (stop)
				break;
			currentWork = rf.getFile();

			// init deployer
			Map<DeployFile, DeployerService> inProgressFiles = Collections
					.synchronizedMap(new HashMap<DeployFile, DeployerService>());

			Collection<DeployFile> sameFiles = fs.get(rf);
			long t = System.currentTimeMillis();
			for (DeployFile f : sameFiles) {
				if (f.mode() == Mode.NA)
					continue;

				try {
					DeployerService ds = deployerManager.getDeployer(f);
					if (ds == null)
						throw new OdenException("Invalid agent: "
								+ f.getAgent().agentName() + "["
								+ f.getAgent().agentAddr() + "]");

					if (f.mode() == Mode.DELETE) {
						ds.backupNRemove(f.getAgent().location(), f.getPath(),
								backupLocation.equals("snapshot") ? null
										: FileUtil.combinePath(backupLocation,
												id), backupcnt);
						f.setSuccess(true);
						nSuccess++;
					} else { // add or update
						f.setDate(t);
						DeployerHelper.readyToDeploy(ds, f, useTmp, backupcnt,
								false);
						inProgressFiles.put(f, ds);
					}
				} catch (Exception e) {
					Logger.debug(e.getMessage());
					setError(e.getMessage());
					f.setErrorLog(Utils.rootCause(e));
					f.setSuccess(false);
				}
			}
			if (inProgressFiles.size() == 0) { // no add or update
				finishedWorks += sameFiles.size();
				continue;
			}

			// get inputstream to write
			FatInputStream in = null;
			try {
				in = reposvc.resolve(rf);
			} catch (OdenException e) {
				for (DeployFile f : fs.get(rf)) {
					f.setSuccess(false);
					f.setErrorLog(Utils.rootCause(e));
				}
				Logger.error(e);
				setError(e.getMessage());

				try {
					if (in != null)
						in.close();
				} catch (IOException ioe) {
				}
				break;
			}

			if (hasSameTargets(inProgressFiles))
				writeDeployFiles(in, inProgressFiles);
			else
				writeDeployFilesAsThread(in, inProgressFiles);

			closeDeployFiles(inProgressFiles, in.size());

			touchOtherTargets(inProgressFiles, t);

			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}

			finishedWorks += sameFiles.size();
		}

		// close repository service
		if (reposvc != null)
			reposvc.close();
	}

	private boolean hasSameTargets(Map<DeployFile, DeployerService> fmap) {
		String addr = null;
		for (DeployFile f : fmap.keySet()) {
			if (addr == null)
				addr = f.getAgent().agentAddr();
			else if (!f.getAgent().agentAddr().equals(addr))
				return false;
		}
		return true;

	}

	private void touchOtherTargets(Map<DeployFile, DeployerService> fmap,
			long date) {
		// get path & other AgentLocs from DeployFiles
		String _path = null;
		Collection<AgentLoc> _targets = new HashSet<AgentLoc>();
		for (DeployFile f : fmap.keySet()) {
			if (_path == null)
				_path = f.getPath();
			_targets.add(f.getAgent());
		}

		// for(AgentLoc t : otherTargets(_targets)){
		for (AgentLoc t : _targets) {
			DeployerService ds = deployerManager.getDeployer(t.agentAddr());
			if (ds == null)
				continue;

			try {
				ds.setDate(t.location(), _path, date);
			} catch (Exception e) {
				Logger.error(e);
			}
		}
	}

	private List<AgentLoc> otherTargets(Collection<AgentLoc> _targets) {
		if (targets.size() == _targets.size())
			return Collections.EMPTY_LIST;

		List<AgentLoc> ret = new ArrayList<AgentLoc>();
		for (CfgTarget t : targets) {
			AgentLoc al = CfgUtil.toAgentLoc(t);
			if (!_targets.contains(al))
				ret.add(al);
		}
		return ret;
	}

	protected DeployFile getSameTargetFile(Collection<DeployFile> fs,
			CfgTarget tg) {
		AgentLoc t = CfgUtil.toAgentLoc(tg);
		for (DeployFile f : fs)
			if (f.getAgent().equals(t))
				return f;
		return null;
	}

	protected void writeDeployFiles(FatInputStream in,
			Map<DeployFile, DeployerService> fmap) {
		try {
			// add or update
			// byte[] buf = new byte[1024 * 64];

			byte[] buf = null;

			Long filesize = in.size();
			if (!securitykey.equals("") && in.size() != 0L)
				buf = new byte[filesize.intValue()];
			else
				buf = new byte[1024 * 64];

			int size = 0;
			while ((size = in.read(buf)) != -1) {
				if (!securitykey.equals("") && in.size() != 0L) {
					// jasypt
					buf = CipherUtil.encrypt(buf);
					size = buf.length;
				}
				for (final DeployFile f : fmap.keySet()) {

					DeployerService ds = fmap.get(f);
					try {
						if (!DeployerHelper.write(ds, f, new ByteArray(buf,
								size)))
							throw new OdenException("Fail to write: "
									+ f.getPath());
					} catch (Exception e) { // while writing..
						DeployerHelper.close(ds, f, null, backupLocation
								.equals("snapshot") ? null : FileUtil
								.combinePath(backupLocation, id));
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

	protected void writeDeployFilesAsThread(FatInputStream in,
			final Map<DeployFile, DeployerService> fmap) {
		// add or update
		// byte[] buf = new byte[1024 * 64];
		byte[] buf = null;

		Long filesize = in.size();
		if (!securitykey.equals("") && in.size() != 0L)
			buf = new byte[filesize.intValue()];
		else
			buf = new byte[1024 * 64];
		int size = 0;

		try {
			while ((size = in.read(buf)) != -1) {
				if (!securitykey.equals("") && in.size() != 0L) {
					// jasypt
					buf = CipherUtil.encrypt(buf);
					size = buf.length;
				}

				final ByteArray ba = new ByteArray(buf, size);

				List<Thread> ths = new ArrayList<Thread>();
				for (final DeployFile f : fmap.keySet())
					ths.add(new Thread() {
						public void run() {
							DeployerService ds = fmap.get(f);
							try {
								if (!DeployerHelper.write(ds, f, ba))
									throw new OdenException("Fail to write: "
											+ f.getPath());
							} catch (Exception e) { // while writing..
								fmap.remove(f);
								f.setSuccess(false);
								f.setErrorLog(Utils.rootCause(e));
							}
						}
					});

				for (Thread th : ths)
					th.start();

				for (Thread th : ths)
					try {
						th.join();
					} catch (InterruptedException e) {
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

	protected void closeDeployFiles(Map<DeployFile, DeployerService> fmap,
			final long originalFileSz) {
		List<Thread> threads = new ArrayList<Thread>();

		for (final DeployFile f : fmap.keySet()) {
			final DeployerService deployer = fmap.get(f);
			Thread th = new Thread() {
				public void run() {
					try {
						// DoneFileInfo info = DeployerHelper.close(
						// deployer, f, null, backupLocation);
						DoneFileInfo info = DeployerHelper.close(deployer, f,
								null, backupLocation.equals("snapshot") ? null
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
						// add the size of deploy file
						f.setSize(info.size());
					} catch (Exception e) {
						f.setSuccess(false);
						// add the size of deploy file
						f.setSize(0L);
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
		if (errorMessage == null) {
			errorMessage = msg;
		}
	}
}
