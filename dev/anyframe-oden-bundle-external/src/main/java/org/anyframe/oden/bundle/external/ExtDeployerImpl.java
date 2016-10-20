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
package org.anyframe.oden.bundle.external;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.anyframe.oden.bundle.build.config.CfgPmdReturnVO;
import org.anyframe.oden.bundle.build.config.CfgRunJob;
import org.anyframe.oden.bundle.build.util.HudsonRemoteAPI;
import org.anyframe.oden.bundle.common.Assert;
import org.anyframe.oden.bundle.common.BundleUtil;
import org.anyframe.oden.bundle.common.DateUtil;
import org.anyframe.oden.bundle.common.FileInfo;
import org.anyframe.oden.bundle.common.FileUtil;
import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.common.StringUtil;
import org.anyframe.oden.bundle.core.AgentLoc;
import org.anyframe.oden.bundle.core.DeployFile;
import org.anyframe.oden.bundle.core.DeployFile.Mode;
import org.anyframe.oden.bundle.core.DeployFileUtil;
import org.anyframe.oden.bundle.core.Repository;
import org.anyframe.oden.bundle.core.RepositoryProviderService;
import org.anyframe.oden.bundle.core.SortedDeployFileSet;
import org.anyframe.oden.bundle.core.job.CompressFileResolver;
import org.anyframe.oden.bundle.core.job.DeployFileResolver;
import org.anyframe.oden.bundle.core.job.Job;
import org.anyframe.oden.bundle.core.job.JobManager;
import org.anyframe.oden.bundle.core.repository.RepositoryService;
import org.anyframe.oden.bundle.core.txmitter.DeployerHelper;
import org.anyframe.oden.bundle.core.txmitter.TransmitterService;
import org.anyframe.oden.bundle.deploy.CfgReturnScript;
import org.anyframe.oden.bundle.deploy.DeployerService;
import org.anyframe.oden.bundle.external.config.CfgBuild;
import org.anyframe.oden.bundle.external.config.CfgBuildDetail;
import org.anyframe.oden.bundle.external.config.CfgBuildReturnVO;
import org.anyframe.oden.bundle.external.config.CfgFileInfo;
import org.anyframe.oden.bundle.external.config.CfgHistory;
import org.anyframe.oden.bundle.external.config.CfgHistoryDetail;
import org.anyframe.oden.bundle.external.config.CfgJob;
import org.anyframe.oden.bundle.external.config.CfgReturnErr;
import org.anyframe.oden.bundle.external.config.CfgReturnPreview;
import org.anyframe.oden.bundle.external.config.CfgReturnStatus;
import org.anyframe.oden.bundle.external.config.CfgReturnVO;
import org.anyframe.oden.bundle.external.config.CfgScript;
import org.anyframe.oden.bundle.external.config.CfgTarget;
import org.anyframe.oden.bundle.external.config.CfgUtil;
import org.anyframe.oden.bundle.external.deploy.ExtCompressDeployJob;
import org.anyframe.oden.bundle.external.deploy.ExtDeployerService;
import org.anyframe.oden.bundle.external.deploy.ExtJobDeployJob;
import org.anyframe.oden.bundle.external.deploy.ExtRerunJob;
import org.anyframe.oden.bundle.external.deploy.ExtUndoJob;
import org.anyframe.oden.bundle.external.deploy.SourceManager;
import org.anyframe.oden.bundle.job.log.JobLogService;
import org.anyframe.oden.bundle.job.log.ShortenRecord;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * all methods are throws Exception to catch ROSGiException(RuntimeExcetpion) on
 * the remote caller.
 * 
 * @author Junghwan Hong
 * @see ExtDeployerService
 * @ThreadSafe
 */
public class ExtDeployerImpl implements ExtDeployerService {

	private BundleContext context;

	private TransmitterService txmitter;

	private JobManager jobManager;

	private JobLogService jobLogger;

	private RepositoryProviderService repositoryProvider;

	private String port = null;

	private boolean deployExcOpt;

	private Object lock = new Object();

	public CfgReturnVO execute(final CfgJob job) throws Exception {
		// service instance start
		initServices();
		synchronized (lock) {
			return executeRun(job, false);
		}
	}

	/*
	 * batch deploy , using by multi thread
	 * 
	 * @param job
	 */
	public LinkedList<CfgReturnVO> executeBatch(LinkedList<CfgJob> job)
			throws Exception {
		initServices();
		synchronized (lock) {
			return executeRunBatch(job);
		}
	}

	/*
	 * Root directory 하단의 파일들을 배포
	 * 
	 * @param job
	 */
	public CfgReturnVO executeRoot(CfgJob job) throws Exception {
		// service instance start
		initServices();
		synchronized (lock) {
			return executeRun(job, true);
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

		if (undo == null || !undo.startsWith("true")) {
			throw new OdenException(
					"Undo function is not activated. Check 'deploy.undo' property in oden.ini."
							+ undo);
		}
		try {
			if (txid.length() == 0 || txid.equals("")) {
				throw new OdenException(
						"Undo function is not activated. Check 'transaction id'");
			}
		} catch (Exception e) {
			throw new OdenException(
					"Undo function is not activated. Check 'transaction id'");
		}
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

		Logger.debug(j.id() + " " + (System.currentTimeMillis() - tm) + "ms");

		return new CfgReturnVO(j.id(), j.id(), r.isSuccess(), String.valueOf(r
				.getTotal()), null, null);
	}

	public String stop(String id) throws Exception {
		initJobManager();

		Job j = id.length() == 0 ? jobManager.job() : jobManager.job(id);
		if (j == null) {
			throw new OdenException("Couldn't find that job: " + id);
		}
		jobManager.cancel(j);
		return j.getCurrentWork();
	}

	/*
	 * 배포전 preview
	 * 
	 * CfgJob job
	 * org.anyframe.oden.bundle.external.deploy.ExtDeployerService#rollback(
	 * java.lang.String)
	 */
	@SuppressWarnings("PMD")
	public Map<String, List<CfgReturnPreview>> test(final CfgJob job)
			throws Exception {
		initServices();

		Collection<DeployFile> files = preview_(job);
		Map<String, List<CfgReturnPreview>> previews = new HashMap<String, List<CfgReturnPreview>>();

		for (DeployFile f : files) {
			String agentName = f.getAgent().agentName();
			List<CfgReturnPreview> fs = previews.get(f.getAgent().agentName());
			String mode = "";
			if (f.mode().equals(Mode.ADD)) {
				mode = "A";
			} else if (f.mode().equals(Mode.UPDATE)) {
				mode = "U";
			} else if (f.mode().equals(Mode.DELETE)) {
				mode = "D";
			} else {
				mode = "NA";
			}
			if (fs == null)
				fs = new ArrayList<CfgReturnPreview>();

			fs.add(new CfgReturnPreview(f.getAgent().location(), f.getPath(),
					mode));

			previews.put(agentName, fs);
		}
		return previews;
	}

	public CfgHistory log(final String id, int pageIndex, int pageSize)
			throws Exception {
		// services initialize
		initServicesLog();
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss",
				Locale.getDefault()); // HH=24h,
		// hh=12h

		ShortenRecord r = jobLogger.search(id);
		final Mode mode = Mode.NA;
		if (r == null) {
			throw new OdenException("No proper log: " + id);
		}

		List<CfgHistoryDetail> det = toListHistory(new SortedDeployFileSet(
				jobLogger.show(id, "", mode, false)));

		List<CfgHistoryDetail> historydet = pageFilter(det, pageIndex, pageSize);

		CfgHistory history = new CfgHistory(r.getId(), r.getUser(), r.getJob(),
				String.valueOf(r.getTotal()), historydet,
				df.format(r.getDate()), String.valueOf(r.isSuccess()));
		return history;
	}

	public List<CfgHistoryDetail> logDetail(String id) throws Exception {
		// services initialize
		initServicesLog();

		List<CfgHistoryDetail> det = toListHistory(new SortedDeployFileSet(
				jobLogger.show(id, "", Mode.NA, false)));

		return det;

	}

	/*
	 * script 실행을 위한 메소드
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.anyframe.oden.bundle.external.deploy.ExtDeployerService#run(org.anyframe
	 * .oden.bundle.external.config.CfgScript)
	 */
	@SuppressWarnings("PMD")
	public Map<String, CfgReturnScript> run(CfgScript script) throws Exception {
		// TODO Auto-generated method stub
		initTxmintter();
		String _timeout = context.getProperty("exec.timeout");
		long timeout = _timeout == null ? -1L : Long.parseLong(_timeout);

		Map<String, CfgReturnScript> result = new HashMap<String, CfgReturnScript>();

		for (CfgTarget target : script.getTargets()) {
			DeployerService ds = txmitter.getDeployer(target.getAddress()
					.contains(":") ? target.getAddress() : target.getAddress()
					+ port);
			if (ds == null) {
				result.put(
						target.getName(),
						new CfgReturnScript("Invalid agent: "
								+ target.getName(), "executed: "
								+ script.getCommand() + " in the "
								+ target.getPath(), 1));
				continue;
			}
			result.put(target.getName(), ds.execShellCommand(
					script.getCommand(), target.getPath(), timeout));
		}
		return result;
	}

	/*
	 * Agent 활성/비활성을 체크 하기 위한 메소드
	 * 
	 * (non-Javadoc)
	 * 
	 * @see String[] agents
	 */
	public Map<String, Boolean> checkAgent(List<CfgTarget> agents)
			throws Exception {
		// TODO Auto-generated method stub
		Map<String, Boolean> rtn = new HashMap<String, Boolean>();
		initTxmintter();

		try {
			if (agents.isEmpty() || agents == null) {
				throw new OdenException(
						"Agent list is empty. Check 'agent list'");
			}
		} catch (Exception e) {
			throw new OdenException("Agent list is empty. Check 'agent list'");
		}

		for (CfgTarget agent : agents) {
			try {
				DeployerService ds = txmitter.getDeployer(agent.getAddress()
						.contains(":") ? agent.getAddress() : agent
						.getAddress() + port);

				if (ds.alive()) {
					rtn.put(agent.getName(), true);
				}
			} catch (Exception ex) {
				rtn.put(agent.getName(), false);
			}
		}
		return rtn;
	}

	/*
	 * 배포 진행 사항을 모니터링 하기 위한 메소드
	 * 
	 * (non-Javadoc)
	 * 
	 * @see String id
	 */
	@SuppressWarnings("PMD")
	public List<CfgReturnStatus> status(String id) throws Exception {
		// TODO Auto-generated method stub
		initJobManager();
		Job[] jobs = jobManager.jobs();
		List<CfgReturnStatus> status = new ArrayList<CfgReturnStatus>();
		for (Job j : jobs) {
			status.add(new CfgReturnStatus(j.id(), j.status(), j.progress(), j
					.getCurrentWork(), j.todoWorks(), j.date(), j.desc()));
		}
		return status;
	}

	/*
	 * 오류 건 재 배포 메소드 (non-Javadoc)
	 * 
	 * @see String id
	 * 
	 * @see String user
	 */
	@SuppressWarnings("PMD")
	public CfgReturnVO reexecute(final String txid, String user)
			throws Exception {
		// TODO Auto-generated method stub
		if (txid.length() == 0)
			throw new OdenException("<txid> is required.");

		try {
			jobLogger.show(txid, "", Mode.NA, true);
		} catch (Exception e) {
			throw new OdenException("Couldn't find a log: " + txid);
		}

		long tm = System.currentTimeMillis();

		Job j = new ExtRerunJob(context, user, "deploy rerun:" + txid, txid,
				new DeployFileResolver() {
					public Set<DeployFile> resolveDeployFiles()
							throws OdenException {
						return jobLogger.show(txid, "", Mode.NA, true);
					}
				});

		jobManager.syncRun(j);

		ShortenRecord r = jobLogger.search(j.id());
		Assert.check(r != null, "Fail to get log: " + j.id());

		Logger.debug(j.id() + " " + (System.currentTimeMillis() - tm) + "ms");

		Map<String, List<CfgReturnErr>> errs = new HashMap<String, List<CfgReturnErr>>();

		Set<DeployFile> error = jobLogger.show(j.id(), "", Mode.NA, true);
		Iterator its = error.iterator();

		while (its.hasNext()) {
			DeployFile data = (DeployFile) its.next();
			String agentName = data.getAgent().agentName();
			List<CfgReturnErr> fs = errs.get(agentName);

			if (fs == null) {
				fs = new ArrayList<CfgReturnErr>();
				fs.add(new CfgReturnErr(data.getPath(), data.errorLog()));
			} else {
				fs.add(new CfgReturnErr(data.getPath(), data.errorLog()));
			}

			errs.put(agentName, fs);
		}

		return new CfgReturnVO(txid, j.id(), r.isSuccess(), String.valueOf(r
				.getTotal()), String.valueOf(r.getnSuccess()), errs);
	}

	/*
	 * Hidden SR 조회를 위한 Agent 파일, 사이즈 , 일자 조회
	 * 
	 * @see
	 * org.anyframe.oden.bundle.external.deploy.ExtDeployerService#compareAgent
	 * (java.lang.String, java.lang.String, java.util.List, java.util.List)
	 */
	public List<FileInfo> listAllFiles(String fromDate, String toDate,
			CfgTarget agent, List<String> excludes) throws Exception {

		// initialize services
		initTxmintter();

		if (fromDate.equals("") || toDate.equals("") || agent == null) {
			throw new OdenException(
					"Information is empty. Check 'From/To Date, Agent List'");
		}

		DeployerService ds = txmitter.getDeployer(agent.getAddress().contains(
				":") ? agent.getAddress() : agent.getAddress() + port);
		// check agent alive
		if (ds == null)
			throw new OdenException("Invalid agent: " + agent.getName() + "["
					+ agent.getAddress() + "]");
		return ds.listAllFilesAsCondition(fromDate, toDate, agent.getPath(),
				excludes);

	}

	@SuppressWarnings("PMD")
	private List<CfgHistoryDetail> toListHistory(SortedDeployFileSet fs)
			throws Exception {
		List<CfgHistoryDetail> data = new ArrayList<CfgHistoryDetail>();

		for (Iterator<DeployFile> it = fs.iterator(); it.hasNext();) {
			DeployFile current = it.next();
			CfgHistoryDetail detail = new CfgHistoryDetail(current.getPath(),
					current.getRepo().toString(),
					DeployFileUtil.modeToString(current.mode()),
					StringUtil.makeEmpty(current.errorLog()), current
							.getAgent().agentName(), String.valueOf(current
							.isSuccess()), DateUtil.toStringDate(current
							.getDate()), current.getSize());
			data.add(detail);
		}
		return data;
	}

	private void initServices() throws OdenException {
		this.context = FrameworkUtil.getBundle(this.getClass())
				.getBundleContext();
		// this.port = context.getProperty("http.port") == null ? ":9872" :
		// null;
		this.port = ":9872";

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

	private void initTxmintter() throws OdenException {
		this.context = FrameworkUtil.getBundle(this.getClass())
				.getBundleContext();
		this.port = ":9872";
		this.txmitter = (TransmitterService) BundleUtil.getService(context,
				TransmitterService.class);
		Assert.check(txmitter != null, "Fail to load service."
				+ txmitter.getClass().getName());
	}

	private void initJobManager() throws OdenException {
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

	@SuppressWarnings("PMD")
	private Collection<DeployFile> preview_(final CfgJob job)
			throws OdenException {
		final Collection<DeployFile> ret = new Vector<DeployFile>();
		final Map<String, DeployerService> targets = new HashMap<String, DeployerService>();

		// get Deployer Service
		for (CfgFileInfo fileInfo : job.getFileInfo()) {
			for (CfgTarget target : fileInfo.getTargets()) {
				if (!targets.containsKey(target.getName())) {
					targets.put(
							target.getName(),
							txmitter.getDeployer(target.getAddress().contains(
									":") ? target.getAddress() : target
									.getAddress() + port));
				}
			}
		}
		SourceManager srcmgr = null;

		for (CfgFileInfo fileInfo : job.getFileInfo()) {
			srcmgr = getSourceManager(fileInfo.getExeDir());
			for (CfgTarget t : fileInfo.getTargets()) {

				try {
					DeployerService ds = targets.get(t.getName());

					if (ds == null)
						throw new OdenException("Invalid agent: " + t.getName()
								+ "[" + t.getAddress() + "]");

					if (fileInfo.isDelete())
						ret.add(new DeployFile(srcmgr.getRepository(), fileInfo
								.getCiId(), CfgUtil.toAgentLoc(t), 0L, 0L,
								Mode.DELETE));
					else if (DeployerHelper.isNewFile(ds, t.getPath(),
							fileInfo.getCiId()))
						ret.add(new DeployFile(srcmgr.getRepository(), fileInfo
								.getCiId(), CfgUtil.toAgentLoc(t), 0L, 0L,
								Mode.ADD));
					else
						ret.add(new DeployFile(srcmgr.getRepository(), fileInfo
								.getCiId(), CfgUtil.toAgentLoc(t), 0L, 0L,
								Mode.UPDATE));
				} catch (Exception e) {
					ret.add(new DeployFile(srcmgr.getRepository(), fileInfo
							.getCiId(), CfgUtil.toAgentLoc(t), 0L, 0L, Mode.NA));
				}
			}
		}

		srcmgr.close();

		return ret;
	}

	@SuppressWarnings("PMD")
	private Collection<DeployFile> preview(final CfgJob job,
			final Boolean isRoot) throws OdenException {
		final Collection<DeployFile> ret = new Vector<DeployFile>();
		// collect repo files. collect targets files
		List<Thread> ths = new ArrayList<Thread>();

		ths.add(new Thread() {
			@Override
			public void run() {
				for (CfgFileInfo fileInfo : job.getFileInfo()) {
					SourceManager srcmgr = null;
					try {
						srcmgr = getSourceManager(fileInfo.getExeDir());
						File sourceF = new File(fileInfo.getExeDir());

						for (CfgTarget t : fileInfo.getTargets()) {
							File targetF = new File(t.getPath());
							// executeRoot() 일때
							if (isRoot == true) {
								copy(sourceF, targetF, srcmgr, fileInfo, t,
										ret, null);
								// execute() 일때
							} else {
								ret.add(new DeployFile(srcmgr.getRepository(),
										fileInfo.getCiId(), CfgUtil
												.toAgentLoc(t), 0L, 0L,
										fileInfo.isDelete() ? Mode.DELETE
												: Mode.UPDATE));
							}
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

	/*
	 * Root directory 하단의 파일들을 폴더구조 형태로 배포하기 위한 job구성 재귀식을 이용하여 폴더구조를 구성한다.
	 * 
	 * @param File sourceF, File targetF, File sourceF, File targetF,
	 * SourceManager srcmgr, CfgFileInfo fileInfo, CfgTarget t,
	 * Collection<DeployFile> ret, String filePath
	 */
	@SuppressWarnings("PMD")
	private void copy(File sourceF, File targetF, SourceManager srcmgr,
			CfgFileInfo fileInfo, CfgTarget t, Collection<DeployFile> ret,
			String filePath) {
		File[] ff = sourceF.listFiles();
		for (File file : ff) {
			File temp = new File(targetF.getAbsolutePath() + File.separator
					+ file.getName());
			// Root 하위 디렉토리 구조, directory가 아닌 file이 나올때까지 계속 dierctory를 붙여나간다.
			String filePaths = null;
			if (filePath != null) {
				filePaths = filePath + File.separator + file.getName();
			} else
				filePaths = File.separator + file.getName();
			// file이 아니면 recursive
			if (file.isDirectory()) {
				copy(file, temp, srcmgr, fileInfo, t, ret, filePaths);
			} else {
				ret.add(new DeployFile(srcmgr.getRepository(), filePaths,
						CfgUtil.toAgentLoc(t), 0L, 0L,
						fileInfo.isDelete() ? Mode.DELETE : Mode.UPDATE));
			}
		}
	}

	@SuppressWarnings("PMD")
	private Collection<DeployFile> compPreview(final CfgJob job)
			throws OdenException {
		final Collection<DeployFile> ret = new Vector<DeployFile>();

		List<Thread> ths = new ArrayList<Thread>();

		final String srcTmp;
		try {
			srcTmp = FileUtil.temporaryDir().toString();
		} catch (IOException e1) {
			throw new OdenException("Don't create src temp directory");
		}

		ths.add(new Thread() {
			public void run() {
				SourceManager srcmgr = null;
				for (CfgFileInfo fileInfo : job.getFileInfo()) {
					try {
						srcmgr = getSourceManager(fileInfo.getExeDir());
						for (CfgTarget t : fileInfo.getTargets()) {
							final String targetTmp = getTargetTmp(t);
							ret.add(new DeployFile(
									new Repository(new String[] {
											srcmgr.getRepository().args()[0],
											srcTmp }), "temp.zip",
									new AgentLoc(t.getName(), t.getAddress()
											.contains(":") ? t.getAddress() : t
											.getAddress() + port, targetTmp),
									0L, 0L, Mode.ADD));
						}
						srcmgr.close();
					} catch (OdenException e) {
						e.printStackTrace();
					}
					break;
				}

			}
		});

		for (Thread t : ths) {
			t.start();
		}
		for (Thread t : ths) {
			try {
				t.join();
			} catch (InterruptedException e) {
			}
		}

		return ret;
	}

	private String getTargetTmp(CfgTarget t) {
		DeployerService ds = txmitter
				.getDeployer(t.getAddress().contains(":") ? t.getAddress() : t
						.getAddress() + port);
		if (ds != null) {
			try {
				return ds.getTempDirectory();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}

	private SourceManager getSourceManager(String srcPath) throws OdenException {
		RepositoryService repoSvc = repositoryProvider
				.getRepoServiceByURI(CfgUtil.toRepoArg(srcPath));
		if (repoSvc == null) {
			throw new OdenException("Invalid Repository: " + srcPath);
		}
		return new SourceManager(repoSvc, srcPath);
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

	@SuppressWarnings("PMD")
	private CfgReturnVO executeRun(final CfgJob job, final boolean isRoot)
			throws Exception {
		List<CfgTarget> targets = getTargets(job);

		if (job == null || targets == null)
			throw new OdenException(
					"Deploy function is not activated. Check 'job'");

		if (job.isCompress() && isRoot)
			throw new OdenException(
					"This option is not activated. Check 'isRoot option and compress option'");

		long tm = System.currentTimeMillis();

		Job j;

		if (!job.isCompress())
			// 일반전송
			j = new ExtJobDeployJob(context, job.getFileInfo(),
					job.getUserId(), job.getId(), targets,
					new DeployFileResolver() {
						public Collection<DeployFile> resolveDeployFiles()
								throws OdenException {
							return new SortedDeployFileSet(preview(job, isRoot));
						}
					}, isRoot);
		else
			// 압축전송
			j = new ExtCompressDeployJob(context, job, job.getUserId(),
					job.getId(), targets, new CompressFileResolver() {
						public Collection<DeployFile> compressDeployFiles()
								throws OdenException {
							return new SortedDeployFileSet(compPreview(job));
						}
					}, new DeployFileResolver() {
						public Collection<DeployFile> resolveDeployFiles()
								throws OdenException {
							return new SortedDeployFileSet(preview(job, isRoot));
						}
					});

		if (job.isSync()) {
			jobManager.syncRun(j);
			ShortenRecord r = jobLogger.search(j.id());

			Map<String, List<CfgReturnErr>> errs = new HashMap<String, List<CfgReturnErr>>();

			Set<DeployFile> error = jobLogger.show(j.id(), "", Mode.NA, true);
			Iterator its = error.iterator();

			while (its.hasNext()) {
				DeployFile data = (DeployFile) its.next();
				String agentName = data.getAgent().agentName();
				List<CfgReturnErr> fs = errs.get(agentName);

				if (fs == null) {
					fs = new ArrayList<CfgReturnErr>();
					fs.add(new CfgReturnErr(data.getPath(), data.errorLog()));
				} else {
					fs.add(new CfgReturnErr(data.getPath(), data.errorLog()));
				}

				errs.put(agentName, fs);
			}

			Assert.check(r != null, "Fail to get log: " + j.id());

			Logger.debug(j.id() + " " + (System.currentTimeMillis() - tm)
					+ "ms");

			// deploy exception option check. When true is, transaction is all
			// or nothing
			if (!r.isSuccess() && deployExcOpt) {
				rollback(j.id());
			}

			return new CfgReturnVO(job.getId(), j.id(), r.isSuccess(),
					!r.isSuccess() && deployExcOpt ? "0" : String.valueOf(r
							.getTotal()), String.valueOf(r.getnSuccess()), errs);

		} else {
			jobManager.schedule(j);
			Logger.debug(j.id() + " " + (System.currentTimeMillis() - tm)
					+ "ms");

			return new CfgReturnVO(job.getId(), j.id(), null, "0", null, null);
		}
	}

	@SuppressWarnings("PMD")
	private LinkedList<CfgReturnVO> executeRunBatch(
			final LinkedList<CfgJob> jobs) throws Exception {

		if (jobs.isEmpty())
			throw new OdenException(
					"Deploy function is not activated. Check 'jobs'");

		long tm = System.currentTimeMillis();

		LinkedList<Job> deployJobs = new LinkedList<Job>();

		for (final CfgJob job : jobs) {
			Job j;
			List<CfgTarget> targets = getTargets(job);
			if (!job.isCompress()) {
				// 일반전송
				j = new ExtJobDeployJob(context, job.getFileInfo(),
						job.getUserId(), job.getId(), targets,
						new DeployFileResolver() {
							public Collection<DeployFile> resolveDeployFiles()
									throws OdenException {
								return new SortedDeployFileSet(preview(job,
										false));
							}
						}, false);
			} else {
				// 압축전송
				j = new ExtCompressDeployJob(context, job, job.getUserId(),
						job.getId(), targets, new CompressFileResolver() {
							public Collection<DeployFile> compressDeployFiles()
									throws OdenException {
								return new SortedDeployFileSet(compPreview(job));
							}
						}, new DeployFileResolver() {
							public Collection<DeployFile> resolveDeployFiles()
									throws OdenException {
								return new SortedDeployFileSet(preview(job,
										false));
							}
						});
			}
			deployJobs.add(j);
		}

		jobManager.batchRun(deployJobs);

		LinkedList<CfgReturnVO> rtnList = new LinkedList<CfgReturnVO>();

		for (Job j : deployJobs) {

			ShortenRecord r = jobLogger.search(j.id());

			Map<String, List<CfgReturnErr>> errs = new HashMap<String, List<CfgReturnErr>>();

			Set<DeployFile> error = jobLogger.show(j.id(), "", Mode.NA, true);
			Iterator its = error.iterator();

			while (its.hasNext()) {
				DeployFile data = (DeployFile) its.next();
				String agentName = data.getAgent().agentName();
				List<CfgReturnErr> fs = errs.get(agentName);

				if (fs == null) {
					fs = new ArrayList<CfgReturnErr>();
					fs.add(new CfgReturnErr(data.getPath(), data.errorLog()));
				} else {
					fs.add(new CfgReturnErr(data.getPath(), data.errorLog()));
				}

				errs.put(agentName, fs);
			}

			Assert.check(r != null, "Fail to get log: " + j.id());

			Logger.debug(j.id() + " " + (System.currentTimeMillis() - tm)
					+ "ms");

			// deploy exception option check. When true is, transaction is all
			// or nothing
			if (!r.isSuccess() && deployExcOpt) {
				rollback(j.id());
			}

			rtnList.add(new CfgReturnVO(j.id(), j.id(), r.isSuccess(), !r
					.isSuccess() && deployExcOpt ? "0" : String.valueOf(r
					.getTotal()), String.valueOf(r.getnSuccess()), errs));
		}

		return rtnList;

	}

	public CfgBuildReturnVO executeBuild(CfgBuild build) throws Exception {
		// CfgBuild Logging
		inputParamLogging(build);

		// call build Parameter
		CfgRunJob run = HudsonRemoteAPI.executeBuildWithArg(build.getAddress(),
				build.getUserId(), build.getPwd(), build.getDbName(), build
						.getDbConnection(), build.getServer(), build
						.getProductName(), build.getProjectName(), build
						.getBuildName(), toJSONArray(build.getRequest())
						.toString().replace("\"", ""), build.getRepoPath(),
				false);

		return new CfgBuildReturnVO(run.getName(), run.getBuildNo(),
				run.getConsoleUrl());
	}

	public CfgBuildReturnVO rollbackBuild(String address, String buildName,
			List<String> build) throws Exception {
		CfgRunJob run = HudsonRemoteAPI.rollbackBuildWithArg(address,
				buildName, toJSONForRollback(build).toString()
						.replace("\"", ""));
		return new CfgBuildReturnVO(run.getName(), run.getBuildNo(),
				run.getConsoleUrl());
	}

	public int checkBuild(String address, String buildName, String buildNo)
			throws Exception {
		Logger.debug("address:" + " " + address);
		Logger.debug("buildName:" + " " + buildName);
		Logger.debug("buildNo:" + " " + buildNo);

		return HudsonRemoteAPI.getStatusWithArg(address, buildName, buildNo);
	}

	private void inputParamLogging(CfgBuild build) throws Exception {
		Logger.debug("address:" + " " + build.getAddress());
		Logger.debug("userId:" + " " + build.getUserId());
		Logger.debug("pwd:" + " " + build.getPwd());
		Logger.debug("dbName:" + " " + build.getDbName());
		Logger.debug("dbConnection:" + " " + build.getDbConnection());
		Logger.debug("server:" + " " + build.getServer());
		Logger.debug("productName:" + " " + build.getProductName());
		Logger.debug("projectName:" + " " + build.getProductName());
		Logger.debug("buildName:" + " " + build.getBuildName());
		Logger.debug("request:" + " "
				+ new JSONArray(build.getRequest()).toString());
		Logger.debug("repoPath:" + " " + build.getRepoPath());
	}

	private JSONArray toJSONArray(List<CfgBuildDetail> builds)
			throws JSONException {
		JSONArray list = new JSONArray();

		for (CfgBuildDetail build : builds) {
			list.put(new JSONObject().put("requestId", build.getRequestId())
					.put("buildId", build.getBuildId()));
		}

		return list;
	}

	private JSONArray toJSONForRollback(List<String> builds)
			throws JSONException {
		JSONArray list = new JSONArray();

		for (String build : builds) {
			list.put(new JSONObject().put("buildId", build));
		}

		return list;
	}

	public CfgBuildReturnVO executePmd(CfgBuild build) throws Exception {
		// call build Parameter & run pmd
		CfgRunJob run = HudsonRemoteAPI.executeBuildWithArg(build.getAddress(),
				build.getUserId(), build.getPwd(), build.getDbName(), build
						.getDbConnection(), build.getServer(), build
						.getProductName(), build.getProjectName(), build
						.getBuildName(), toJSONArray(build.getRequest())
						.toString().replace("\"", ""), build.getRepoPath(),
				true);
		return new CfgBuildReturnVO(run.getName(), run.getBuildNo(),
				run.getConsoleUrl());
	}

	public CfgPmdReturnVO returnPmd(String address, String buildName,
			String buildNo) throws Exception {
		return HudsonRemoteAPI.returnPmd(address, buildName, buildNo);
	}
}