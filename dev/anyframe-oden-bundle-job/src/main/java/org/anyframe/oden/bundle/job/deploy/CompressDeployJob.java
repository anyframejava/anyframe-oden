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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.anyframe.oden.bundle.common.Assert;
import org.anyframe.oden.bundle.common.BundleUtil;
import org.anyframe.oden.bundle.common.FatInputStream;
import org.anyframe.oden.bundle.common.FileInfo;
import org.anyframe.oden.bundle.common.FileUtil;
import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.common.StringUtil;
import org.anyframe.oden.bundle.common.Utils;
import org.anyframe.oden.bundle.core.AgentLoc;
import org.anyframe.oden.bundle.core.DeployFile;
import org.anyframe.oden.bundle.core.RepositoryProviderService;
import org.anyframe.oden.bundle.core.DeployFile.Mode;
import org.anyframe.oden.bundle.core.command.DeployerManager;
import org.anyframe.oden.bundle.core.job.CompressFileResolver;
import org.anyframe.oden.bundle.core.job.CompressJob;
import org.anyframe.oden.bundle.core.job.DeployFileResolver;
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
 * 
 * Job to deploying by compressing file.
 * 
 * @author junghwan.hong
 * 
 */
public class CompressDeployJob extends CompressJob {
	RepositoryProviderService repositoryProvider;
	String backupLocation = null;
	Integer nSuccess = 0;
	JobLogService jobLogger;
	List<CfgTarget> targets = null;
	RepoManager reposvc;
	boolean useTmp = true;
	int backupcnt;
	protected DeployerManager deployerManager;
	String compSrc;
	// String compTarget;
	Map<String, String> compTargets = new HashMap<String, String>();

	protected String user;
	String undo;

	public CompressDeployJob(BundleContext context, CfgSource source,
			String user, String desc, List<CfgTarget> targets,
			CompressFileResolver compResolver, DeployFileResolver resolver)
			throws OdenException {
		super(context, desc, source.getPath(), resolver, compResolver);
		deployerManager = new DeployerManager(context, id, false);

		this.targets = targets;

		repositoryProvider = (RepositoryProviderService) BundleUtil.getService(
				context, RepositoryProviderService.class);
		Assert.check(repositoryProvider != null, "Fail to load service."
				+ repositoryProvider.getClass().getName());

		jobLogger = (JobLogService) BundleUtil.getService(context,
				JobLogService.class);
		Assert.check(jobLogger != null, "No proper Service: "
				+ jobLogger.getClass().getName());

		backupLocation = FileUtil.combinePath(getBackupLocation(context), id);
		this.reposvc = getRepoManager(source);
		this.useTmp = "true".equals(context.getProperty("deploy.tmpfile"));
		this.backupcnt = context.getProperty("deploy.backupcnt").equals("") ? 100
				: Integer.valueOf(context.getProperty("deploy.backupcnt"));
		this.user = user;
	}

	String getBackupLocation(BundleContext ctx) {
		undo = ctx.getProperty("deploy.undo");
		if (!"true".equals(undo))
			return "snapshot";

		String loc = ctx.getProperty("deploy.undo.loc");
		return loc == null ? "snapshots" : loc;
	}

	protected void run() {
		try {
			compDeploy(compFile);
			deploy(deployFiles);
		} catch (Exception e) {
			// Oden server temporary file delete
			try {
				cleanTemp();
			} catch (Exception e1) {
			}
			setError(e.getMessage());
			Logger.error(e);
		}
	}

	private void cleanTemp(Collection<DeployFile> compfiles)
			throws OdenException {
		// local compress temp delete
		currentWork = "clean temporary file";
		
		FileUtil.removeDir(new File(compSrc));
		List<Thread> ths = new ArrayList<Thread>();

		for (final DeployFile f : compfiles)
			ths.add(new Thread() {
				public void run() {
					DeployerService ds = deployerManager.getDeployer(f);
					if (ds == null)
						return;
					try {

						ds.removeDirString(compTargets.get(f.getAgent()
								.agentName()));
					} catch (Exception e) {
						Logger.error(e);
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
		finishedWorks += 1;
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
			compSrc = f.getRepo().args()[1];
			RepoFile rf = new RepoFile("", f.getPath());
			Collection<DeployFile> fs = ret.get(rf);
			if (fs == null)
				fs = new HashSet<DeployFile>();
			fs.add(f);
			ret.put(rf, fs);
		}
		return ret;
	}

	private Map<AgentLoc, Collection<DeployFile>> groupByAgent(
			Collection<DeployFile> files) {
		Map<AgentLoc, Collection<DeployFile>> ret = new HashMap<AgentLoc, Collection<DeployFile>>();
		
		for (DeployFile f : files) {
			AgentLoc al = new AgentLoc(f.getAgent().agentName(), f.getAgent()
					.agentAddr(), f.getAgent().location());
			Collection<DeployFile> fs = ret.get(al);
			if (fs == null)
				fs = new HashSet<DeployFile>();
			fs.add(f);
			ret.put(al, fs);
		}
		return ret;
	}
	
	protected void compDeploy(Collection<DeployFile> compfiles)
			throws OdenException {
		// files having same path and diff agent
		currentWork = "ready to deploy...";
		Map<RepoFile, Collection<DeployFile>> fs = groupByPath(compfiles);

		for (RepoFile rf : fs.keySet()) {

			if (stop)
				break;
			currentWork = rf.getFile();

			// init deployer
			Map<DeployFile, DeployerService> inProgressFiles = new ConcurrentHashMap<DeployFile, DeployerService>();
			
			Collection<DeployFile> sameFiles = fs.get(rf);
			long t = System.currentTimeMillis();

			for (DeployFile f : sameFiles) {
				compTargets.put(f.getAgent().agentName(), f.getAgent()
						.location());

				if (f.mode() == Mode.NA)
					continue;
				
				try {
					DeployerService ds = deployerManager.getDeployer(f);
					if (ds == null)
						throw new OdenException("Invalid agent: "
								+ f.getAgent().agentName() + "["
								+ f.getAgent().agentAddr() + "]");
					f.setDate(t);
					DeployerHelper.readyToDeploy(ds, f, false, backupcnt);
					inProgressFiles.put(f, ds);
					
				} catch (Exception e) {
					Logger.debug(e.getMessage());
					setError(e.getMessage());
					f.setErrorLog(Utils.rootCause(e));
					f.setSuccess(false);
					closeCompressFiles(inProgressFiles);
				} 
			}
			if (inProgressFiles.size() == 0) { // no add or update
				finishedWorks += sameFiles.size();
				continue;
			}
			// get inputstream to write
			FatInputStream in = null;

			try {
				in = reposvc.resolve(FileUtil
						.combinePath(compSrc, rf.getFile()));
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

			closeCompressFiles(inProgressFiles);

			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}

		}

		finishedWorks += compfiles.size();

		// close repository service
		if (reposvc != null)
			reposvc.close();
	}

	protected void closeCompressFiles(Map<DeployFile, DeployerService> fmap) {
		for (DeployFile f : fmap.keySet()) {
			DeployerService deployer = fmap.get(f);
			
			try {
				DoneFileInfo info = DeployerHelper.close(deployer, f,
						null, null);
				if (info == null || info.size() == -1L)
					throw new IOException("Fail to close: "
							+ f.getPath());

			} catch (Exception e) {
				f.setSuccess(false);
				if (StringUtil.empty(f.errorLog()))
					f.setErrorLog(e.getMessage());
				Logger.error(e);
				setError(e.getMessage());
			}
			
		}
	}

	protected void deploy(Collection<DeployFile> repofiles)
			throws OdenException {
		// files having same path and diff agent
		currentWork = "ready to deploy...";

		final Map<AgentLoc, Collection<DeployFile>> fs = groupByAgent(repofiles);

		List<Thread> threads = new ArrayList<Thread>();
		
		final Map<String, DeployerService> inProgressFiles = Collections
				.synchronizedMap(new HashMap<String, DeployerService>());
		
		for(AgentLoc al : fs.keySet()) {
			inProgressFiles.put(al.agentName(), deployerManager
								.getDeployer(al.agentAddr()));
		}
		
		
		for (final AgentLoc al : fs.keySet()) {
			Map<String, FileInfo> rtn = new HashMap<String, FileInfo>();

			if (stop)
				break;
			currentWork = "extract to temp.zip @" + al.agentName();

			Thread th = new Thread() {

				public void run() {
					Collection<DeployFile> sameFiles = new ArrayList<DeployFile>();
					try {
						sameFiles = fs.get(al);

						DeployerService ds = inProgressFiles.get(al.agentName());
						if (ds == null)
							throw new OdenException("Invalid agent: "
									+ al.agentName());
						
//						File src = new File(FileUtil.combinePath(compTargets
//								.get(al.agentName()), "temp.zip"));
//						File dest = new File(al.location());
						
						String src = FileUtil.combinePath(compTargets
								.get(al.agentName()), "temp.zip");
						String dest = al.location();
						
						Map<String, FileInfo> rtn = null;
						try {
							if(ds.exist(compTargets
									.get(al.agentName()), "temp.zip") && ds.fileInfo(compTargets
											.get(al.agentName()), "temp.zip").size() !=-1L) 
								rtn = ds.zipCopy(src, dest, backupcnt,
										backupLocation, undo);
							else 
								throw new OdenException("Can not copy zip file to: "
										+ al.agentName());
						} catch(Exception e){
							throw new OdenException(e.getMessage());
						}
						
						if (rtn.size() == 0 || rtn == null)
							throw new OdenException("Do not copy to "
									+ al.agentName());

						long t = System.currentTimeMillis();

						finishedWorks += 1;

						for (DeployFile f : sameFiles) {
							currentWork = "copy and check @" + f.getPath()
									+ "@" + al.agentName();
							if (f.mode() == Mode.NA)
								continue;
							try {
								if(f.mode() == Mode.DELETE){
									// delete
									deployerManager.getDeployer(f)
											.backupNRemove(
													f.getAgent().location(),
													f.getPath(),
													backupLocation, backupcnt);
									f.setSuccess(true);
									nSuccess++;
								} else { // add or update
									f.setDate(t);
	
									// 비교 로직 exceptiion 인지
									if (!rtn.isEmpty())
										checkIntegrity(f, rtn);
									else
										throw new OdenException("Do not copy to "
												+ f.getPath());
								}
							} catch (Exception e) {
								Logger.error(e);
								setError(e.getMessage());
								f.setErrorLog(Utils.rootCause(e));
								f.setSuccess(false);
							}
							
							finishedWorks += 1;
						}
						
						// temp agent directory remove
						ds.removeDirString(compTargets.get(al.agentName()));

					} catch (Exception e) {
						// TODO Auto-generated catch block
						Logger.error(e);
						setError(e.getMessage());
						for (DeployFile f : sameFiles) {
							f.setSuccess(false);
							f.setErrorLog(e.getMessage());
						}
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
		
		// clean oden server temp directory
		try {
			cleanTemp();
		} catch (OdenException e) {
			Logger.error(e);
		}
	}

	private void cleanTemp() throws OdenException {
		// local compress temp delete
		currentWork = "clean temporary file";
		FileUtil.removeDir(new File(compSrc));

		finishedWorks += 1;
	}
	
	protected void checkIntegrity(final DeployFile f,
			final Map<String, FileInfo> zfmap) {

		try {
			FileInfo info = zfmap.get(f.getPath());
			if (info == null || info.size() == -1L)
				throw new IOException("Fail to close: " + f.getPath());
			if (info.isSuccess()) {
				f.setSuccess(true);
				synchronized (nSuccess) {
					nSuccess++;
				}
				f.setMode(info.isUpdate() ? Mode.UPDATE : Mode.ADD);
			} else {
				f.setSuccess(false);
				f.setErrorLog(info.getException());
				setError(info.getException());
			}
		} catch (Exception e) {
			f.setSuccess(false);
			if (StringUtil.empty(f.errorLog()))
				f.setErrorLog(e.getMessage());
			Logger.error(e);
			setError(e.getMessage());
		}
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
			byte[] buf = new byte[1024 * 512];
			int size = 0;
			while ((size = in.read(buf)) != -1) {
				for (final DeployFile f : fmap.keySet()) {
					DeployerService ds = fmap.get(f);
					try {
						if (!DeployerHelper.write(ds, f, new ByteArray(buf,
								size)))
							throw new OdenException("Fail to write: "
									+ f.getPath());
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

	protected void writeDeployFilesAsThread(FatInputStream in,
			final Map<DeployFile, DeployerService> fmap) {
		// add or update
		byte[] buf = new byte[1024 * 512];
		int size = 0;
		try {
			while ((size = in.read(buf)) != -1) {
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

	protected void closeDeployFiles(Map<DeployFile, DeployerService> fmap) {
		List<Thread> threads = new ArrayList<Thread>();

		for (final DeployFile f : fmap.keySet()) {
			final DeployerService deployer = fmap.get(f);
			Thread th = new Thread() {
				public void run() {
					try {
						DoneFileInfo info = DeployerHelper.close(deployer, f,
								null, backupLocation.equals("snapshot") ? null
										: FileUtil.combinePath(backupLocation,
												id));
						if (info == null || info.size() == -1L)
							throw new IOException("Fail to close: "
									+ f.getPath());
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
		finishedWorks += 1;
	}

	protected void done() {
		try {
			RecordElement2 r = new RecordElement2(id, deployFiles, user, System
					.currentTimeMillis(), desc);
			if (errorMessage != null) {
				r.setLog(errorMessage);
				r.setSucccess(false);
			} else {
				r.setSucccess(true);
			}

			r.setNSuccess(r.isSuccess() ? deployFiles.size() : nSuccess);
			jobLogger.record(r);
		} catch (OdenException e) {
			Logger.error(e);
		}
//		try {
//			cleanTemp(compFile);
//		} catch (OdenException e) {
//			Logger.error(e);
//		}
		deployFiles.clear();
		deployFiles = null;
		compFile.clear();
		compFile = null;
	}

	protected void setError(String msg) {
		if (errorMessage == null)
			errorMessage = msg;
	}
}
