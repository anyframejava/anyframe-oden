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
package org.anyframe.oden.bundle.external;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.anyframe.oden.bundle.common.Assert;
import org.anyframe.oden.bundle.common.BundleUtil;
import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.common.StringUtil;
import org.anyframe.oden.bundle.core.DeployFile;
import org.anyframe.oden.bundle.core.DeployFileUtil;
import org.anyframe.oden.bundle.core.RepositoryProviderService;
import org.anyframe.oden.bundle.core.SortedDeployFileSet;
import org.anyframe.oden.bundle.core.DeployFile.Mode;
import org.anyframe.oden.bundle.core.job.DeployFileResolver;
import org.anyframe.oden.bundle.core.job.Job;
import org.anyframe.oden.bundle.core.job.JobManager;
import org.anyframe.oden.bundle.core.repository.RepositoryService;
import org.anyframe.oden.bundle.core.txmitter.TransmitterService;
import org.anyframe.oden.bundle.deploy.DeployerService;
import org.anyframe.oden.bundle.external.config.CfgFileInfo;
import org.anyframe.oden.bundle.external.config.CfgHistory;
import org.anyframe.oden.bundle.external.config.CfgHistoryDetail;
import org.anyframe.oden.bundle.external.config.CfgJob;
import org.anyframe.oden.bundle.external.config.CfgReturnVO;
import org.anyframe.oden.bundle.external.config.CfgTarget;
import org.anyframe.oden.bundle.external.config.CfgUtil;
import org.anyframe.oden.bundle.external.deploy.ExtDeployerService;
import org.anyframe.oden.bundle.external.deploy.ExtJobDeployJob;
import org.anyframe.oden.bundle.external.deploy.ExtUndoJob;
import org.anyframe.oden.bundle.external.deploy.SourceManager;
import org.anyframe.oden.bundle.job.log.JobLogService;
import org.anyframe.oden.bundle.job.log.ShortenRecord;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * all methods are throws Exception to catch ROSGiException(RuntimeExcetpion) on
 * the remote caller.
 * 
 * @see ExtDeployerService
 * @ThreadSafe
 * 
 * @author junghwan.hong
 * 
 */
public class ExtDeployerImpl implements ExtDeployerService {

	private BundleContext context;

	private TransmitterService txmitter;

	private JobManager jobManager;

	private JobLogService jobLogger;

	private RepositoryProviderService repositoryProvider;

	private String port = null;

	private boolean deployExcOpt;

	public CfgReturnVO execute(final CfgJob job) throws Exception {
		// service instance start
		initServices();
		List<CfgTarget> targets = getTargets(job);

		long tm = System.currentTimeMillis();

		Job j = new ExtJobDeployJob(context, job.getFileInfo(),
				job.getUserId(), job.getId(), targets,
				new DeployFileResolver() {
					public Collection<DeployFile> resolveDeployFiles()
							throws OdenException {
						return new SortedDeployFileSet(preview(job));
					}
				});

		if (job.isSync()) {
			jobManager.syncRun(j);
			ShortenRecord r = jobLogger.search(j.id());

			Assert.check(r != null, "Fail to get log: " + j.id());

			Logger.debug(j.id() + " " + (System.currentTimeMillis() - tm)
					+ "ms");
			
			// deploy exception option check. When true is, transaction is all or nothing 
			if(! r.isSuccess() && deployExcOpt )
				rollback(j.id());
			return new CfgReturnVO(job.getId(), j.id(), r.isSuccess(), ! r.isSuccess() && deployExcOpt ? "0" : String
					.valueOf(r.getTotal()));

		} else {
			jobManager.schedule(j);
			Logger.debug(j.id() + " " + (System.currentTimeMillis() - tm)
					+ "ms");

			return new CfgReturnVO(job.getId(), j.id(), null, "0");
		}
	}

	/*
	 * undo 기능 구현(txid를 키로 무조건 rollback을 수행한다)
	 * 
	 * @see
	 * org.anyframe.oden.bundle.external.deploy.ExtDeployerService#rollback(
	 * java.lang.String)
	 */
	public CfgReturnVO rollback(final String txid) throws Exception {
		// TODO Auto-generated method stub
		initServices();
		long tm = System.currentTimeMillis();
		
		String undo = context.getProperty("deploy.undo");

		if (undo == null || !undo.startsWith("true"))
			throw new OdenException(
					"Undo function is not activated. Check 'deploy.undo' property in oden.ini."
							+ undo);

		if (txid.length() == 0 || txid.equals(""))
			throw new OdenException(
					"Undo function is not activated. Check 'transaction id'");
		Job j = new ExtUndoJob(context, "user", "deploy undo:" + txid, txid,
				new DeployFileResolver() {
					public Set<DeployFile> resolveDeployFiles()
							throws OdenException {
						return jobLogger.show(txid, "", Mode.NA, false);
					}
				});
		jobManager.syncRun(j);
		ShortenRecord r = jobLogger.search(j.id());

		Assert.check(r != null, "Fail to get log: " + j.id());

		Logger.debug(j.id() + " " + (System.currentTimeMillis() - tm)
				+ "ms");

		
		return new CfgReturnVO(j.id(), j.id(), r.isSuccess(), String
				.valueOf(r.getTotal()));
	}

	public String stop(String id) throws Exception {
		Job j = id.length() == 0 ? jobManager.job() : jobManager.job(id);
		if (j == null)
			throw new OdenException("Couldn't find that job: " + id);
		jobManager.cancel(j);
		return j.getCurrentWork();
	}

	private void initServices() throws OdenException {
		this.context = FrameworkUtil.getBundle(this.getClass())
				.getBundleContext();
		this.port = context.getProperty("http.port") == null ? ":9872" : null;

		this.txmitter = (TransmitterService) BundleUtil.getService(context,
				TransmitterService.class);
		Assert.check(txmitter != null, "Fail to load service."
				+ txmitter.getClass().getName());
		this.jobManager = (JobManager) BundleUtil.getService(context,
				JobManager.class);
		Assert.check(jobManager != null, "Fail to load service."
				+ jobManager.getClass().getName());
		this.jobLogger = (JobLogService) BundleUtil.getService(context,
				JobLogService.class);
		Assert.check(jobLogger != null, "Fail to load service."
				+ jobLogger.getClass().getName());
		this.repositoryProvider = (RepositoryProviderService) BundleUtil
				.getService(context, RepositoryProviderService.class);
		Assert.check(repositoryProvider != null, "Fail to load service."
				+ repositoryProvider.getClass().getName());
		deployExcOpt = context.getProperty("deploy.exception.option").equals(
				"true") ? true : false;

	}

	private void initServicesLog() throws OdenException {
		this.context = FrameworkUtil.getBundle(this.getClass())
				.getBundleContext();
		this.jobLogger = (JobLogService) BundleUtil.getService(context,
				JobLogService.class);
		Assert.check(jobLogger != null, "Fail to load service."
				+ jobLogger.getClass().getName());
	}

	private List<CfgTarget> getTargets(CfgJob jobs) throws OdenException {
		Map<String, CfgTarget> agent = new HashMap<String, CfgTarget>();
		List<CfgTarget> targets = new ArrayList<CfgTarget>();

		if (jobs != null) {
			for (CfgFileInfo fileinfo : jobs.getFileInfo()) {
				for (CfgTarget target : fileinfo.getTargets()) {
					if (agent.get(target.getName()) == null) {
						agent.put(target.getName(), target);
						targets.add(target);
					}
				}
			}
		}

		return targets;
	}

	private Collection<DeployFile> preview(final CfgJob job)
			throws OdenException {
		final Collection<DeployFile> ret = new Vector<DeployFile>();
		// collect repo files. collect targets files
		List<Thread> ths = new ArrayList<Thread>();

		ths.add(new Thread() {
			@Override
			public void run() {
				SourceManager srcmgr = null;
				for (CfgFileInfo fileInfo : job.getFileInfo()) {
					try {
						srcmgr = getSourceManager(fileInfo.getExeDir());
						List<CfgTarget> activeTargets = getActiveTargets(fileInfo
								.getTargets());
						for (CfgTarget t : activeTargets) {
							ret.add(new DeployFile(srcmgr.getRepository(),
									fileInfo.getCiId(), CfgUtil.toAgentLoc(t),
									0L, 0L, Mode.ADD));
						}
					} catch (OdenException e) {
						e.printStackTrace();
					}
					srcmgr.close();
				}
			}
		});
		for (Thread t : ths)
			t.start();
		for (Thread t : ths)
			try {
				t.join();
			} catch (InterruptedException e) {
			}

		return ret;
	}

	private SourceManager getSourceManager(String srcPath) throws OdenException {
		RepositoryService repoSvc = repositoryProvider
				.getRepoServiceByURI(CfgUtil.toRepoArg(srcPath));
		if (repoSvc == null)
			throw new OdenException("Invalid Repository: " + srcPath);
		return new SourceManager(repoSvc, srcPath);
	}

	private List<CfgTarget> getActiveTargets(List<CfgTarget> targets) {
		List<CfgTarget> activeTargets = new ArrayList<CfgTarget>();
		for (CfgTarget t : targets) {
			DeployerService ds = txmitter.getDeployer(t.getAddress() + port);
			if (ds != null)
				activeTargets.add(t);
		}
		return activeTargets;
	}

	public CfgHistory log(final String id, int pageIndex, int pageSize)
			throws Exception {
		// services initialize
		initServicesLog();
		DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss"); // HH=24h,
																	// hh=12h

		ShortenRecord r = jobLogger.search(id);
		final Mode mode = Mode.NA;
		if (r == null)
			throw new OdenException("No proper log: " + id);

		List<CfgHistoryDetail> det = toListHistory(new SortedDeployFileSet(
				jobLogger.show(id, "", mode, false)));

		List<CfgHistoryDetail> historydet = pageFilter(det, pageIndex, pageSize);

		CfgHistory history = new CfgHistory(r.getId(), r.getUser(), r.getJob(),
				String.valueOf(r.getTotal()), historydet, df
						.format(r.getDate()), String.valueOf(r.isSuccess()));
		return history;
	}

	private List<CfgHistoryDetail> toListHistory(SortedDeployFileSet fs)
			throws Exception {
		List<CfgHistoryDetail> data = new ArrayList<CfgHistoryDetail>();

		for (Iterator<DeployFile> it = fs.iterator(); it.hasNext();) {
			DeployFile current = it.next();
			CfgHistoryDetail detail = new CfgHistoryDetail(current.getRepo()
					.toString(), current.getPath(), DeployFileUtil
					.modeToString(current.mode()), StringUtil.makeEmpty(current
					.errorLog()), current.getAgent().agentName(), String
					.valueOf(current.isSuccess()));
			data.add(detail);
		}
		return data;
	}

	private List<CfgHistoryDetail> pageFilter(List<CfgHistoryDetail> in,
			int page, int pgscale) throws OdenException {
		int start = pgscale * (page - 1);
		int endPage = (in.size() - 1) / pgscale + 1;
		int end = page == endPage ? in.size() : start + pgscale;

		List<CfgHistoryDetail> detail = new ArrayList<CfgHistoryDetail>();
		for (int i = start; i < end; i++) {
			detail.add(in.get(i));
		}
		return detail;
	}
}