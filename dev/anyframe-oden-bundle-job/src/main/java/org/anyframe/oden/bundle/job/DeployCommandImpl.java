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
package org.anyframe.oden.bundle.job;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.anyframe.oden.bundle.common.Assert;
import org.anyframe.oden.bundle.common.FileInfo;
import org.anyframe.oden.bundle.common.FileUtil;
import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.common.StringUtil;
import org.anyframe.oden.bundle.core.AgentLoc;
import org.anyframe.oden.bundle.core.DeployFile;
import org.anyframe.oden.bundle.core.DeployFileUtil;
import org.anyframe.oden.bundle.core.Repository;
import org.anyframe.oden.bundle.core.RepositoryProviderService;
import org.anyframe.oden.bundle.core.SortedDeployFileSet;
import org.anyframe.oden.bundle.core.DeployFile.Mode;
import org.anyframe.oden.bundle.core.command.Cmd;
import org.anyframe.oden.bundle.core.command.JSONUtil;
import org.anyframe.oden.bundle.core.job.CompressFileResolver;
import org.anyframe.oden.bundle.core.job.DeployFileResolver;
import org.anyframe.oden.bundle.core.job.Job;
import org.anyframe.oden.bundle.core.job.JobManager;
import org.anyframe.oden.bundle.core.repository.RepositoryService;
import org.anyframe.oden.bundle.core.txmitter.DeployerHelper;
import org.anyframe.oden.bundle.core.txmitter.TransmitterService;
import org.anyframe.oden.bundle.deploy.DeployerService;
import org.anyframe.oden.bundle.gate.CustomCommand;
import org.anyframe.oden.bundle.job.config.CfgJob;
import org.anyframe.oden.bundle.job.config.CfgSource;
import org.anyframe.oden.bundle.job.config.CfgTarget;
import org.anyframe.oden.bundle.job.config.CfgUtil;
import org.anyframe.oden.bundle.job.config.JobConfigService;
import org.anyframe.oden.bundle.job.deploy.CompressDeployJob;
import org.anyframe.oden.bundle.job.deploy.JobDeployJob;
import org.anyframe.oden.bundle.job.deploy.RerunJob;
import org.anyframe.oden.bundle.job.deploy.UndoJob;
import org.anyframe.oden.bundle.job.log.JobLogService;
import org.anyframe.oden.bundle.job.log.ShortenRecord;
import org.anyframe.oden.bundle.job.page.PageHandler;
import org.anyframe.oden.bundle.job.page.PageHandlerOr;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

public class DeployCommandImpl implements CustomCommand {

	private BundleContext context;

	protected void activate(ComponentContext context) {
		this.context = context.getBundleContext();
	}

	RepositoryProviderService repositoryProvider;

	protected void setRepositoryProvider(RepositoryProviderService r) {
		this.repositoryProvider = r;
	}

	JobManager jobManager;

	protected void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}

	JobLogService jobLogger;

	protected void setJobLogService(JobLogService jobLogger) {
		this.jobLogger = jobLogger;
	}

	JobConfigService jobConfig;

	protected void setJobConfigService(JobConfigService jobConfig) {
		this.jobConfig = jobConfig;
	}

	TransmitterService txmitter;

	protected void setTransmitterService(TransmitterService tx) {
		this.txmitter = tx;
	}

	PageHandler pageHandler;

	protected void setPageHandler(PageHandler pageHandler) {
		this.pageHandler = pageHandler;
	}

	ExecCommandImpl execCommand;

	protected void setExecCommand(CustomCommand cmd) {
		if (cmd instanceof ExecCommandImpl)
			this.execCommand = (ExecCommandImpl) cmd;
	}

	String compPath = null;

	public void execute(String line, PrintStream out, PrintStream err) {
		boolean isJSON = false;
		try {
			Cmd cmd = new Cmd(line);
			isJSON = cmd.getOption(Cmd.JSON_OPT) != null;

			if (cmd.getAction().length() == 0 || cmd.getAction().equals("help")) {
				out.println(getFullUsage());
				return;
			}

			out.println(execute(cmd, isJSON));

			if (cmd.getOption("after") != null) {
				executeExec(cmd, out, err);
			}
		} catch (Exception e) {
			err
					.println(isJSON ? JSONUtil.jsonizedException(e) : e
							.getMessage());
			Logger.error(e);
		}
	}

	private void executeExec(Cmd cmd, PrintStream out, PrintStream err)
			throws Exception {
		CfgJob job = jobConfig.getJob(cmd.getActionArg());

		String c_script = "exec run";
		c_script += " " + "\"" + job.getName() + "\"";

		c_script += " " + "-t";
		List<String> targets = cmd.getOptionArgList(new String[] { "t" });
		List<CfgTarget> activeTargets = getActiveTargets(job
				.getAllTargets(targets));
		for (CfgTarget t : activeTargets) {
			c_script += " " + "\"" + t.getName() + "\"";
		}

		c_script += " " + "-c";
		List<String> commands = cmd.getOptionArgList(new String[] { "after" });
		for (String c : commands) {
			c_script += " " + "\"" + c + "\"";
		}

		execCommand.execute(c_script, out, err);
	}

	private String execute(final Cmd cmd, boolean isJSON) throws Exception {
		String action = cmd.getAction();
		String pgscale0 = cmd.getOptionArg(new String[] { "pgscale" });
		final int pgscale = StringUtil.empty(pgscale0) ? -1 : Integer
				.valueOf(pgscale0);

		if (action.equals("test")) {
			if (isJSON) {
				return new JSONArray().put(
						pageHandler.getCachedData(cmd, pgscale,
								new PageHandlerOr() {
									public JSONArray run() throws Exception {
										return toJSONArray(new SortedDeployFileSet(
												doTestAction(cmd)));
									}
								})).toString();
			}

			SortedDeployFileSet fs = new SortedDeployFileSet(doTestAction(cmd));
			return toPreviewString(fs);
		} else if (action.equals("run")) {
			boolean deployExcOpt = context.getProperty(
					"deploy.exception.option").equals("true") ? true : false;

			final CfgJob job = jobConfig.getJob(cmd.getActionArg());
			if (job == null)
				throw new OdenException("Invalid Job Name: "
						+ cmd.getActionArg());
			boolean _u = cmd.getOption("u") != null;
			boolean _i = cmd.getOption("i") != null;
			boolean _del = cmd.getOption("del") != null;
			// 압축 전송 시 옵션
			boolean _c = cmd.getOption("c") != null;

			if (_u && _i)
				throw new OdenException("Allowed only: -u or -i");
			if (!_i && !_u && !_del)
//				default option -i
				_i = true;
			final boolean isUpdate = _u;
			final boolean hasInclude = _i;
			final boolean isDelete = _del;
			final boolean isCompress = _c;

			final List<String> deployCandidates = cmd
					.getOptionArgList(new String[] { "i" });
			final List<String> deleteCandidates = cmd
					.getOptionArgList(new String[] { "del" });
			final List<String> targets = cmd
					.getOptionArgList(new String[] { "t" });

			long tm = System.currentTimeMillis();
			Job j;
			
			if (!isCompress) {
				j = new JobDeployJob(context, job.getSource(), getUser(cmd),
						cmd.getActionArg(), getActiveTargets(job
								.getAllTargets(targets)),
						new DeployFileResolver() {
							public Collection<DeployFile> resolveDeployFiles()
									throws OdenException {
								return new SortedDeployFileSet(preview(job,
										isUpdate, hasInclude, deployCandidates,
										isDelete, deleteCandidates, targets));
							}
						});
			} else {
				// 압축 전송
				j = new CompressDeployJob(context, job.getSource(),
						getUser(cmd), cmd.getActionArg(), getActiveTargets(job
								.getAllTargets(targets)),
						new CompressFileResolver() {
							public Collection<DeployFile> compressDeployFiles()
									throws OdenException {
								return new SortedDeployFileSet(compPreview(job,
										isUpdate, hasInclude, deployCandidates,
										isDelete, deleteCandidates, targets));
							}
						},
						new DeployFileResolver() {
							public Collection<DeployFile> resolveDeployFiles()
									throws OdenException {
								return new SortedDeployFileSet(preview(job,
										isUpdate, hasInclude, deployCandidates,
										isDelete, deleteCandidates, targets));
							}
						});
			}
			jobManager.syncRun(j);
			ShortenRecord r = jobLogger.search(j.id());
			Assert.check(r != null, "Fail to get log: " + j.id());

			Logger.debug(j.id() + " " + (System.currentTimeMillis() - tm)
					+ "ms");

			// deploy exception option check. When exception option is true , transaction is all
			// or nothing
			if (!r.isSuccess() && deployExcOpt)
				rollback(getUser(cmd), j.id());

			if (isJSON) {
				return new JSONArray().put(
						new JSONObject().put("txid", j.id()).put("status",
								r.isSuccess() ? "S" : "F").put(
								"count",
								!r.isSuccess() && deployExcOpt ? "0" : r
										.getTotal()).put("nsuccess",
								r.getnSuccess())).toString();
			}
			String total = !r.isSuccess() && deployExcOpt ? "0" : String
					.valueOf(r.getTotal());

			return (r.isSuccess() ? "[S]" : "[F]") + " " + j.id() + "(" + total
					+ ")";
		} else if (action.equals("rerun")) {
//			오류 난 배포 물만 재배포 
			final String id = cmd.getActionArg();
			
			if (id.length() == 0)
				throw new OdenException("<txid> is required.");
			
			try {
				jobLogger.show(id, "", Mode.NA, true);
			} catch(Exception e) {
				throw new OdenException("Couldn't find a log: " + id);
			}
			
			long tm = System.currentTimeMillis();
			
			
			Job j = new RerunJob(context, getUser(cmd), "deploy rerun:" + id, id,
					new DeployFileResolver() {
						public Set<DeployFile> resolveDeployFiles()
								throws OdenException {
							return jobLogger.show(id, "", Mode.NA, true);
						}
					});

			jobManager.syncRun(j);

			ShortenRecord r = jobLogger.search(j.id());
			Assert.check(r != null, "Fail to get log: " + j.id());

			Logger.debug(j.id() + " " + (System.currentTimeMillis() - tm)
					+ "ms");
			if (isJSON) {
				return new JSONArray().put(
						new JSONObject().put("txid", j.id()).put("status",
								r.isSuccess() ? "S" : "F").put("count",
								r.getTotal()).put("nsuccess", r.getnSuccess()))
						.toString();
			}

			return (r.isSuccess() ? "[S]" : "[F]") + " " + j.id() + "("
					+ r.getTotal() + ")";
		} else if (action.equals("undo")) {
			final String id = cmd.getActionArg();
			String undo = context.getProperty("deploy.undo");

			if (id.length() == 0)
				throw new OdenException("<txid> is required.");

			if (undo == null || !undo.startsWith("true"))
				throw new OdenException(
						"Undo function is not activated. Check 'deploy.undo' property in oden.ini."
								+ undo);

			long tm = System.currentTimeMillis();

			Job j = new UndoJob(context, getUser(cmd), "deploy undo:" + id, id,
					new DeployFileResolver() {
						public Set<DeployFile> resolveDeployFiles()
								throws OdenException {
							return jobLogger.show(id, "", Mode.NA, false);
						}
					});

			jobManager.syncRun(j);

			ShortenRecord r = jobLogger.search(j.id());
			Assert.check(r != null, "Fail to get log: " + j.id());

			Logger.debug(j.id() + " " + (System.currentTimeMillis() - tm)
					+ "ms");
			if (isJSON) {
				return new JSONArray().put(
						new JSONObject().put("txid", j.id()).put("status",
								r.isSuccess() ? "S" : "F").put("count",
								r.getTotal()).put("nsuccess", r.getnSuccess()))
						.toString();
			}

			return (r.isSuccess() ? "[S]" : "[F]") + " " + j.id() + "("
					+ r.getTotal() + ")";
		} else {
			throw new OdenException("Invalid Action: " + action);
		}
	}

	/**
	 * [ {"path":"", "mode": "", "targets": ["target", ... ]}, ... ]
	 * 
	 * @return
	 * @throws JSONException
	 */
	private JSONArray toJSONArray(SortedDeployFileSet fs) throws JSONException {
		JSONArray list = new JSONArray();

		String prevPath = null;
		Mode prevMode = Mode.NA;
		List<String> targets = null;
		for (Iterator<DeployFile> it = fs.iterator(); it.hasNext();) {
			DeployFile current = it.next();
			if (current.getPath().equals(prevPath)) {
				targets.add(current.getAgent().agentName());
				prevMode = DeployFileUtil.mergeMode(prevMode, current.mode());
				continue;
			}

			// if not equal to previous element, print previous one.
			if (prevPath != null)
				list.put(new JSONObject().put("path", prevPath).put("mode",
						prevMode).put("targets", targets));

			prevPath = current.getPath();
			prevMode = current.mode();
			targets = new ArrayList<String>();
			targets.add(current.getAgent().agentName());
		}
		if (prevPath != null)
			list.put(new JSONObject().put("path", prevPath).put("mode",
					prevMode).put("targets", targets));
		return list;
	}

	private String toPreviewString(Collection<DeployFile> fs) {
		StringBuffer buf = new StringBuffer("total: " + fs.size() + "\n\n");

		String prevPath = null;
		Mode prevMode = Mode.NA;
		List<String> targets = null;
		for (Iterator<DeployFile> it = fs.iterator(); it.hasNext();) {
			DeployFile current = it.next();
			if (current.getPath().equals(prevPath)) {
				targets.add(current.getAgent().agentName());
				prevMode = DeployFileUtil.mergeMode(prevMode, current.mode());
				continue;
			}

			// if not equal to previous element, print previous one.
			if (prevPath != null)
				buf.append(DeployFileUtil.modeToString(prevMode) + " "
						+ prevPath + " " + targets + "\n");

			prevPath = current.getPath();
			prevMode = current.mode();
			targets = new ArrayList<String>();
			targets.add(current.getAgent().agentName());
		}
		if (prevPath != null)
			buf.append(DeployFileUtil.modeToString(prevMode) + " " + prevPath
					+ " " + targets + "\n");
		return buf.toString();
	}

	private Collection<DeployFile> doTestAction(Cmd cmd) throws Exception {
		final CfgJob job = jobConfig.getJob(cmd.getActionArg());
		if (job == null)
			throw new OdenException("Invalid Job Name: " + cmd.getActionArg());
		boolean _u = cmd.getOption("u") != null;
		boolean _i = cmd.getOption("i") != null;
		boolean _del = cmd.getOption("del") != null;
		if (_u && _i)
			throw new OdenException("Allowed only: -u or -i");
		if (!_i && !_u && !_del)
			_u = _del = true;
		final boolean isUpdate = _u;
		final boolean hasInclude = _i;
		final boolean isDelete = _del;
		final List<String> deployCandidates = cmd
				.getOptionArgList(new String[] { "i" });
		final List<String> deleteCandidates = cmd
				.getOptionArgList(new String[] { "del" });
		final List<String> targets = cmd.getOptionArgList(new String[] { "t" });

		return preview(job, isUpdate, hasInclude, deployCandidates, isDelete,
				deleteCandidates, targets);
	}

	private SourceManager getSourceManager(CfgSource src) throws OdenException {
		RepositoryService repoSvc = repositoryProvider
				.getRepoServiceByURI(CfgUtil.toRepoArg(src));
		if (repoSvc == null)
			throw new OdenException("Invalid Repository: " + src.getPath());
		return new SourceManager(repoSvc, src);
	}

	class RepoCollector extends Thread {
		Collection<FileInfo> ret = null;
		SourceManager srcmgr = null;

		RepoCollector(Collection<FileInfo> ret, SourceManager srcmgr) {
			super();
			this.ret = ret;
			this.srcmgr = srcmgr;
		}

		public void run() {
			try {
				srcmgr.getCandidatesFileInfo(ret);
			} catch (OdenException e) {
				Logger.error(e);
			}
		}
	}

	private List<CfgTarget> getActiveTargets(List<CfgTarget> targets) {
		List<CfgTarget> activeTargets = new ArrayList<CfgTarget>();
		for (CfgTarget t : targets) {
			DeployerService ds = txmitter.getDeployer(t.getAddress());
			if (ds != null)
				activeTargets.add(t);
		}
		return activeTargets;
	}
	
	private Collection<DeployFile> compPreview(final CfgJob job,
			final boolean isUpdate, final boolean hasInclude,
			final List<String> deployCandidates, final boolean isDelete,
			final List<String> deleteCandidates, final List<String> targets) throws OdenException {
		final SourceManager srcmgr = getSourceManager(job.getSource());
		final List<CfgTarget> CfgTargets = job.getAllTargets(targets);
		final Collection<DeployFile> ret = new Vector<DeployFile>();

		List<Thread> ths = new ArrayList<Thread>();

//		final Set<FileInfo> repofs = new HashSet<FileInfo>();
//		ths.add(new RepoCollector(repofs, srcmgr));

		final String srcTmp;
		try {
			srcTmp = FileUtil.temporaryDir().toString();
		} catch (IOException e1) {
			throw new OdenException("Don't create src temp directory");
		}
		
		ths.add(new Thread() {
			public void run() {
				
				for (final CfgTarget t : CfgTargets) {
					
					final String targetTmp = getTargetTmp(t);
					ret.add(new DeployFile(new Repository(
							new String[] { srcmgr.getRepository().args()[0] , srcTmp }), "temp.zip", new AgentLoc(
							t.getName(), t.getAddress(), targetTmp), 0L, 0L,
							Mode.ADD));
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
	
	private String getTargetTmp(CfgTarget t){
		DeployerService ds = txmitter.getDeployer(t.getAddress());
		if(ds != null)
			try {
				return ds.getTempDirectory();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return "";
	}
	
	private Collection<DeployFile> preview(final CfgJob job,
			final boolean isUpdate, final boolean hasInclude,
			final List<String> deployCandidates, final boolean isDelete,
			final List<String> deleteCandidates, final List<String> targets) throws OdenException {
		final SourceManager srcmgr = getSourceManager(job.getSource());
		// final List<CfgTarget> activeTargets =
		// getActiveTargets(job.getAllTargets(targets));
		final List<CfgTarget> activeTargets = job.getAllTargets(targets);

		final Collection<DeployFile> ret = new Vector<DeployFile>();

		// collect repo files. collect targets files
		List<Thread> ths = new ArrayList<Thread>();
		if (!deployCandidates.isEmpty() || !deleteCandidates.isEmpty()) {
			if (!deployCandidates.isEmpty()) {
				ths.add(new Thread() {
					@Override
					public void run() {
						for (String s : deployCandidates) {
							for (CfgTarget t : activeTargets) {
								ret.add(new DeployFile(srcmgr.getRepository(),
										s, CfgUtil.toAgentLoc(t), 0L, 0L,
										Mode.ADD));
							}
						}
					}
				});
			}

			if (!deleteCandidates.isEmpty()) {
				ths.add(new Thread() {
					@Override
					public void run() {
						for (String s : deleteCandidates) {
							for (CfgTarget t : activeTargets) {
								ret.add(new DeployFile(srcmgr.getRepository(),
										s, CfgUtil.toAgentLoc(t), 0L, 0L,
										Mode.DELETE));
							}
						}
					}
				});
			}

			for (Thread t : ths)
				t.start();
			for (Thread t : ths)
				try {
					t.join();
				} catch (InterruptedException e) {
				}
		} else {
			// repo collector thread
			final Set<FileInfo> repofs = new HashSet<FileInfo>();
			ths.add(new RepoCollector(repofs, srcmgr));

			// targets collector thread
			final Map<CfgTarget, Map<FileInfo, FileInfo>> targetfsmap = new ConcurrentHashMap<CfgTarget, Map<FileInfo, FileInfo>>();
			if (isUpdate || isDelete) {
				for (final CfgTarget t : activeTargets) {
					ths.add(new Thread() {
						public void run() {
							DeployerService ds = txmitter.getDeployer(t
									.getAddress());
							if (ds == null)
								return;
							Map<FileInfo, FileInfo> result = new HashMap<FileInfo, FileInfo>();
							try {
								for (FileInfo fi : ds.listAllFiles(t.getPath()))
									result.put(fi, fi);
							} catch (Exception e) {
								Logger.error(e);
							}
							targetfsmap.put(t, result);
						}
					});
				}
			}

			for (Thread t : ths)
				t.start();
			for (Thread t : ths)
				try {
					t.join();
				} catch (InterruptedException e) {
				}

			// compare threads
			ths.clear();
			if (hasInclude) {
				ths.add(new Thread() {
					public void run() {
						for (FileInfo rf : repofs) {
							for (final CfgTarget t : activeTargets) {
								ret.add(new DeployFile(srcmgr.getRepository(),
										rf.getPath(), CfgUtil.toAgentLoc(t),
										0L, 0L, Mode.ADD));
							}
						}
					}
				});
			}

			if (isUpdate || isDelete) {
				for (final CfgTarget t : targetfsmap.keySet()) {
					if (isUpdate) {
						ths.add(new Thread() {
							public void run() {
								DeployerService ds = txmitter.getDeployer(t
										.getAddress());
								if (ds == null)
									return;

								Map<FileInfo, FileInfo> targetfs = targetfsmap
										.get(t);
								for (final FileInfo rf : repofs) {
									FileInfo dest = targetfs.get(rf);
									if (dest != null
											&& !DeployerHelper.isNewFile(rf,
													dest))
										continue;

									ret.add(new DeployFile(srcmgr
											.getRepository(), rf.getPath(),
											CfgUtil.toAgentLoc(t), 0L, 0L,
											Mode.ADD));
								}
							}
						});
					}

					if (isDelete) {
						ths.add(new Thread() {
							public void run() {
								for (FileInfo f : targetfsmap.get(t).keySet()) {
									if (!repofs.contains(f)) {
										ret.add(new DeployFile(srcmgr
												.getRepository(), f.getPath(),
												CfgUtil.toAgentLoc(t), 0L, 0L,
												Mode.DELETE));
									}
								}
							}
						});
					}
				} // end of for
			} // end of if(isUpdate || isDelete)

			for (Thread t : ths)
				t.start();
			for (Thread t : ths)
				try {
					t.join();
				} catch (InterruptedException e) {
				}
		}

		srcmgr.close();
		return ret;
	}

	private String getUser(Cmd cmd) {
		String user = cmd.getOptionArg(new String[] { Cmd.USER_OPT });
		try {
			if (StringUtil.empty(user))
				user = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			user = "";
		}
		return user;
	}

	private void rollback(String user, final String txid) throws OdenException {
		String undo = context.getProperty("deploy.undo");

		if (txid.length() == 0)
			throw new OdenException("<txid> is required.");

		if (undo == null || !undo.startsWith("true"))
			throw new OdenException(
					"Undo function is not activated. Check 'deploy.undo' property in oden.ini."
							+ undo);

		long tm = System.currentTimeMillis();

		Job j = new UndoJob(context, user, "deploy undo:" + txid, txid,
				new DeployFileResolver() {
					public Set<DeployFile> resolveDeployFiles()
							throws OdenException {
						return jobLogger.show(txid, "", Mode.NA, false);
					}
				});

		jobManager.syncRun(j);

	}

	public String getName() {
		return "deploy";
	}

	public String getShortDescription() {
		return "deploy files";
	}

	public String getUsage() {
		return "deploy help";
	}

	public String getFullUsage() {
		return "deploy test <job> [ -t <target> ... ] "
				+ "\n\t[-u | -i ] [ -del ]"
				+ "\ndeploy run <job> [ -t <target> ... ] "
				+ "\n\t[-u | -i ] [ -del ] [ -c ]"
				+ "\n\t[-after <command-name> ...]" 
				+ "\ndeploy rerun <txid> "
				+ "\ndeploy undo <txid> ";
	}
}
