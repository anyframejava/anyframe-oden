package anyframe.oden.bundle.job;

import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import anyframe.common.bundle.gate.CustomCommand;
import anyframe.oden.bundle.common.Assert;
import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.Logger;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.StringUtil;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.DeployFileUtil;
import anyframe.oden.bundle.core.RepositoryProviderService;
import anyframe.oden.bundle.core.SortedDeployFileSet;
import anyframe.oden.bundle.core.DeployFile.Mode;
import anyframe.oden.bundle.core.command.Cmd;
import anyframe.oden.bundle.core.command.JSONUtil;
import anyframe.oden.bundle.core.job.DeployFileResolver;
import anyframe.oden.bundle.core.job.Job;
import anyframe.oden.bundle.core.job.JobManager;
import anyframe.oden.bundle.core.repository.RepositoryService;
import anyframe.oden.bundle.core.txmitter.DeployerHelper;
import anyframe.oden.bundle.core.txmitter.TransmitterService;
import anyframe.oden.bundle.deploy.DeployerService;
import anyframe.oden.bundle.job.config.CfgJob;
import anyframe.oden.bundle.job.config.CfgSource;
import anyframe.oden.bundle.job.config.CfgTarget;
import anyframe.oden.bundle.job.config.CfgUtil;
import anyframe.oden.bundle.job.config.JobConfigService;
import anyframe.oden.bundle.job.deploy.JobDeployJob;
import anyframe.oden.bundle.job.log.JobLogService;
import anyframe.oden.bundle.job.log.ShortenRecord;
import anyframe.oden.bundle.job.page.PageHandler;
import anyframe.oden.bundle.job.page.PageHandlerOr;

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
	protected void setPageHandler(PageHandler pageHandler){
		this.pageHandler = pageHandler;
	}

	ExecCommandImpl execCommand;
	protected void setExecCommand(CustomCommand cmd){
		if(cmd instanceof ExecCommandImpl)
			this.execCommand = (ExecCommandImpl) cmd;
	}
	
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
			
			if(cmd.getOption("after") != null){
				executeExec(cmd, out, err);
			}
		} catch (Exception e) {
			err.println(isJSON ? JSONUtil.jsonizedException(e) : e.getMessage());
			Logger.error(e);
		}
	}

	private void executeExec(Cmd cmd, PrintStream out, PrintStream err) throws Exception {
		CfgJob job = jobConfig.getJob(cmd.getActionArg());
		
		String c_script = "exec run";
		c_script += " "+ "\"" + job.getName()+ "\"";
		
		c_script += " " + "-t";
		List<String> targets = cmd.getOptionArgList(new String[] { "t" });
		List<CfgTarget> activeTargets  = getActiveTargets(job.getAllTargets(targets));
		for(CfgTarget t : activeTargets){
			c_script += " " + "\"" + t.getName()+ "\"";
		}
		
		c_script += " " + "-c";
		List<String> commands = cmd.getOptionArgList(new String[] { "after" });
		for(String c : commands){
			c_script += " "+ "\""+ c+ "\"";
		}
		
		execCommand.execute(c_script, out, err);
	}

	private String execute(final Cmd cmd, boolean isJSON) throws Exception {
		String action = cmd.getAction();
		String pgscale0 = cmd.getOptionArg(new String[]{"pgscale"});
		final int pgscale = StringUtil.empty(pgscale0) ? -1 : Integer.valueOf(pgscale0);
		
		if (action.equals("test")) {
			if(isJSON){
				return new JSONArray().put(pageHandler.getCachedData(cmd, 
						pgscale, new PageHandlerOr(){
					public JSONArray run() throws Exception { 
						return toJSONArray(new SortedDeployFileSet(doTestAction(cmd))); 
					}
				})).toString();
			}

			SortedDeployFileSet fs = new SortedDeployFileSet(doTestAction(cmd));
			return toPreviewString(fs);
		} else if (action.equals("run")) {
			final CfgJob job = jobConfig.getJob(cmd.getActionArg());
			if(job == null) throw new OdenException("Invalid Job Name: " + cmd.getActionArg());
			boolean _u = cmd.getOption("u") != null;
			boolean _i = cmd.getOption("i") != null;
			boolean _del = cmd.getOption("del") != null;
			if(_u && _i)
				throw new OdenException("Allowed only: -u or -i");
			if(!_i && !_u && !_del)
				_u = _del = true;
			final boolean isUpdate = _u;
			final boolean hasInclude = _i;
			final boolean isDelete = _del;
			final List<String> deployCandidates = cmd.getOptionArgList(new String[] { "i" });
			final List<String> deleteCandidates = cmd.getOptionArgList(new String[] { "del" });
			final List<String> targets = cmd.getOptionArgList(new String[] { "t" });
			
			long tm = System.currentTimeMillis();
			Job j = new JobDeployJob(context, 
					job.getSource(),
					getUser(cmd), 
					cmd.getActionArg(),
					getActiveTargets(job.getAllTargets(targets)),
					new DeployFileResolver() {
				public Collection<DeployFile> resolveDeployFiles() throws OdenException {
					return new SortedDeployFileSet(
							preview(job, isUpdate, hasInclude, deployCandidates, isDelete, deleteCandidates, targets));
				}
			});
			jobManager.syncRun(j);
			ShortenRecord r = jobLogger.search(j.id());
			Assert.check(r != null, "Fail to get log: " + j.id());
			
			Logger.debug(j.id() + " " + (System.currentTimeMillis() - tm) + "ms");
			if(isJSON){
				return new JSONArray().put(
						new JSONObject().put("txid", j.id()).put("status",
								r.isSuccess() ? "S" : "F").put("count",
								r.getTotal()).put("nsuccess", r.getnSuccess())).toString();
			}
			
			return (r.isSuccess() ? "[S]" : "[F]") + " " + j.id() 
					+ "(" + r.getTotal() + ")";
		} else {
			throw new OdenException("Invalid Action: " + action);
		}
	}

	/**
	 * [ {"path":"", "mode": "", "targets": ["target", ... ]}, ... ]
	 * @return
	 * @throws JSONException 
	 */
	private JSONArray toJSONArray(SortedDeployFileSet fs) throws JSONException{
		JSONArray list = new JSONArray();
		
		String prevPath = null;
		Mode prevMode = Mode.NA;
		List<String> targets = null;
		for(Iterator<DeployFile> it = fs.iterator(); it.hasNext();){
			DeployFile current = it.next();						
			if(current.getPath().equals(prevPath)){
				targets.add(current.getAgent().agentName());
				prevMode = DeployFileUtil.mergeMode(prevMode, current.mode());
				continue;
			}
			
			// if not equal to previous element, print previous one.
			if(prevPath != null)
				list.put(new JSONObject().
						put("path", prevPath).
						put("mode", prevMode).
						put("targets", targets));	
			
			prevPath = current.getPath();
			prevMode = current.mode();
			targets = new ArrayList<String>();
			targets.add(current.getAgent().agentName());
		}
		if(prevPath != null)
			list.put(new JSONObject().
					put("path", prevPath).
					put("mode", prevMode).
					put("targets", targets));
		return list;
	}

	private String toPreviewString(Collection<DeployFile> fs) {
		StringBuffer buf = new StringBuffer("total: " + fs.size() + "\n\n");
		
		String prevPath = null;
		Mode prevMode = Mode.NA;
		List<String> targets = null;
		for(Iterator<DeployFile> it = fs.iterator(); it.hasNext();){
			DeployFile current = it.next();
			if(current.getPath().equals(prevPath)){
				targets.add(current.getAgent().agentName());
				prevMode = DeployFileUtil.mergeMode(prevMode, current.mode());
				continue;
			}
			
			// if not equal to previous element, print previous one.
			if(prevPath != null)
				buf.append(DeployFileUtil.modeToString(prevMode) + " " + 
						prevPath + " " + targets+ "\n");
			
			prevPath = current.getPath();
			prevMode = current.mode();
			targets = new ArrayList<String>();
			targets.add(current.getAgent().agentName());
		}
		if(prevPath != null)
			buf.append(DeployFileUtil.modeToString(prevMode) + " " + 
					prevPath + " " + targets+ "\n");
		return buf.toString();
	}
	
	private Collection<DeployFile> doTestAction(Cmd cmd) throws Exception {
		final CfgJob job = jobConfig.getJob(cmd.getActionArg());
		if(job == null) throw new OdenException("Invalid Job Name: " + cmd.getActionArg());
		boolean _u = cmd.getOption("u") != null;
		boolean _i = cmd.getOption("i") != null;
		boolean _del = cmd.getOption("del") != null;
		if(_u && _i)
			throw new OdenException("Allowed only: -u or -i");
		if(!_i && !_u && !_del)
			_u = _del = true;
		final boolean isUpdate = _u;
		final boolean hasInclude = _i;
		final boolean isDelete = _del;
		final List<String> deployCandidates = cmd.getOptionArgList(new String[] { "i" });
		final List<String> deleteCandidates = cmd.getOptionArgList(new String[] { "del" });
		final List<String> targets = cmd.getOptionArgList(new String[] { "t" });

		return preview(job, isUpdate, hasInclude, deployCandidates, isDelete, deleteCandidates, targets);
	}

	private SourceManager getSourceManager(CfgSource src) throws OdenException{
		RepositoryService repoSvc = repositoryProvider.getRepoServiceByURI(
				CfgUtil.toRepoArg(src));
		if (repoSvc == null)
			throw new OdenException("Invalid Repository: " + src.getPath());
		return new SourceManager(repoSvc, src);
	}
	
	
	class RepoCollector extends Thread{
		Collection<FileInfo> ret = null;
		SourceManager srcmgr = null;
		
		RepoCollector(Collection<FileInfo> ret, SourceManager srcmgr){
			super();
			this.ret= ret;
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
	
	private List<CfgTarget> getActiveTargets(List<CfgTarget> targets){
		List<CfgTarget> activeTargets = new ArrayList<CfgTarget>();
		for(CfgTarget t : targets){
			DeployerService ds = txmitter.getDeployer(t.getAddress());
			if (ds != null) activeTargets.add(t);
		}
		return activeTargets;
	}
	
	private Collection<DeployFile> preview(final CfgJob job,
			final boolean isUpdate, final boolean hasInclude, 
			final List<String> deployCandidates,
			final boolean isDelete, final List<String> deleteCandidates,
			final List<String> targets) throws OdenException {
		final SourceManager srcmgr = getSourceManager(job.getSource());
		final List<CfgTarget> activeTargets = getActiveTargets(job.getAllTargets(targets));
		
		final Collection<DeployFile> ret = new Vector<DeployFile>();

		// collect repo files. collect targets files 
		List<Thread> ths = new ArrayList<Thread>();
		if(!deployCandidates.isEmpty() || !deleteCandidates.isEmpty() ){
			if(!deployCandidates.isEmpty()){
				ths.add(new Thread(){
					@Override
					public void run() {
						for(String s : deployCandidates){
							for(CfgTarget t : activeTargets) {
								ret.add(new DeployFile(srcmgr.getRepository(), 
										s, CfgUtil.toAgentLoc(t), 
										0L, 0L, Mode.ADD) );
							}		
						}
					}
				});
			}
			
			if(!deleteCandidates.isEmpty()){
				ths.add(new Thread(){
					@Override
					public void run() {
						for(String s : deleteCandidates){
							for(CfgTarget t : activeTargets) {
								ret.add(new DeployFile(srcmgr.getRepository(), 
										s, CfgUtil.toAgentLoc(t), 
										0L, 0L, Mode.DELETE) );
							}		
						}
					}
				});
			}

			for(Thread t : ths) t.start();
			for(Thread t : ths) try{t.join();}catch(InterruptedException e){}
		}else{
			// repo collector thread
			final Set<FileInfo> repofs = new HashSet<FileInfo>();
			ths.add(new RepoCollector(repofs, srcmgr));

			// targets collector thread
			final Map<CfgTarget, Map<FileInfo, FileInfo>> targetfsmap 
					= new ConcurrentHashMap<CfgTarget, Map<FileInfo, FileInfo>>();
			if(isUpdate || isDelete){
				for (final CfgTarget t : activeTargets) {
					ths.add(new Thread() {
						public void run() {
							DeployerService ds = txmitter.getDeployer(t.getAddress());
							if (ds == null) return;
							Map<FileInfo, FileInfo> result 
								= new HashMap<FileInfo, FileInfo>();
							try {
								for(FileInfo fi : ds.listAllFiles(t.getPath()))
									result.put(fi, fi);
							} catch (Exception e) {
								Logger.error(e);
							}
							targetfsmap.put(t, result);
						}
					});
				}
			}

			for(Thread t : ths) t.start();
			for(Thread t : ths) try{t.join();}catch(InterruptedException e){}
			
			// compare threads
			ths.clear();
			if(hasInclude){
				ths.add(new Thread(){
					public void run() {
						for(FileInfo rf : repofs){
							for (final CfgTarget t : activeTargets) {
								ret.add(new DeployFile(srcmgr.getRepository(), 
										rf.getPath(), CfgUtil.toAgentLoc(t), 
										0L, 0L, Mode.ADD) );		
							}
						}
					}
				});
			}
			
			if(isUpdate || isDelete){
				for (final CfgTarget t : targetfsmap.keySet()) {
					if(isUpdate){
						ths.add(new Thread(){
							public void run() {
								DeployerService ds = txmitter.getDeployer(t.getAddress());
								if(ds == null) return;
								
								Map<FileInfo, FileInfo> targetfs = targetfsmap.get(t);
								for(final FileInfo rf : repofs){
									FileInfo dest = targetfs.get(rf);
									if(dest != null && 
											!DeployerHelper.isNewFile(rf, dest))
										continue;
									
									ret.add(new DeployFile(
											srcmgr.getRepository(), 
											rf.getPath(), CfgUtil.toAgentLoc(t), 
											0L, 0L, Mode.ADD) );			
								}		
							}
						});
					}
					
					if(isDelete){
						ths.add(new Thread(){
							public void run() {
								for (FileInfo f : targetfsmap.get(t).keySet()) {
									if (!repofs.contains(f)) {
										ret.add(new DeployFile(srcmgr.getRepository(), 
												f.getPath(), CfgUtil.toAgentLoc(t), 
												0L, 0L, Mode.DELETE));
									}
								}
							}
						});
					}
				}	// end of for
			}	// end of if(isUpdate || isDelete)
			
			for(Thread t : ths) t.start();
			for(Thread t : ths) try{t.join();}catch(InterruptedException e){}
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

	private void getUpdateList(final Collection<DeployFile> result, 
			final SourceManager srcmgr, final List<RepoFile> repofs, 
			List<CfgTarget> targets) throws OdenException{
		List<Thread> ths = new ArrayList<Thread>();
		for(final CfgTarget t : targets) {
			Thread th = new Thread(){
				public void run() {
					DeployerService ds = txmitter.getDeployer(t.getAddress());
					if(ds == null)
						return;
					
					for(final RepoFile rf : repofs){
						try{
							FileInfo src = srcmgr.getFileInfo(rf);
							if(src == null)
								continue;
							
							FileInfo dest = ds.fileInfo(t.getPath(), src.getPath());
							if(dest != null && !DeployerHelper.isNewFile(src, dest))
								continue;
							
							result.add(new DeployFile(
									srcmgr.getRepository(rf), 
									rf.getFile(), CfgUtil.toAgentLoc(t), 
									src.size(), 0L, Mode.ADD) );			
						}catch(Exception e){
							Logger.error(e);	
						}
					}		
				}
			};
			ths.add(th);
			th.start();
		}
		
		for(Thread th : ths)
			try { th.join(); }catch(InterruptedException e){}
	}

	private void getDeployList(final Collection<DeployFile> result, 
			final SourceManager srcmgr, final List<RepoFile> repofs, 
			List<CfgTarget> targets) throws OdenException{
		for(RepoFile rf : repofs){
			FileInfo src = srcmgr.resolveAsFileInfo(rf);
			if(src == null)
				continue;
			
			for(CfgTarget t : targets) {
				result.add(new DeployFile(srcmgr.getRepository(rf), 
						rf.getFile(), CfgUtil.toAgentLoc(t), 
						src.size(), 0L, Mode.ADD) );
			}		
		}
	}

	private void getDeleteList(final Collection<DeployFile> result, 
			final SourceManager srcmgr, final List<String> list, 
			List<CfgTarget> targets) {
		for (CfgTarget t : targets) {
			// get file list to remove by comparing repofs
			for (String path : list) {
				result.add(new DeployFile(srcmgr.getRepository(), path, 
						CfgUtil.toAgentLoc(t), 0L, 0L, Mode.DELETE));
			}
		}
	}
	
	private void getDeleteList(final Collection<DeployFile> result, 
			final SourceManager srcmgr, List<CfgTarget> targets) 
			throws OdenException {
		// get all file list from the repository
		final Set<String> repofset = new HashSet<String>();
		Thread repoCollector = new Thread() {
			public void run() {
				try {
					List<RepoFile> fs = srcmgr.getCandidates();
					for(RepoFile f : fs)
						repofset.add(f.getFile());
				} catch (OdenException e) {
					Logger.error(e);
				}
			}
		};
		repoCollector.start();

		// get every target's file list
		List<Thread> targetCollectors = new ArrayList<Thread>();
		final Map<CfgTarget, List<FileInfo>> targetFileMap = new ConcurrentHashMap<CfgTarget, List<FileInfo>>();
		for (final CfgTarget t : targets) {
			Thread targetCollector = new Thread() {
				public void run() {
					DeployerService ds = txmitter.getDeployer(t.getAddress());
					if (ds == null)
						return;
					// get target's file list
					List<FileInfo> result = null;
					try {
						result = ds.listAllFiles(t.getPath());
					} catch (Exception e) {
						Logger.error(e);
						result = Collections.EMPTY_LIST;
					}
					targetFileMap.put(t, result);
				}
			};
			targetCollectors.add(targetCollector);
			targetCollector.start();
		}

		try {
			repoCollector.join();
			for (Thread t : targetCollectors)
				t.join();
		} catch (InterruptedException e) {
			throw new OdenException(e);
		}

		for (CfgTarget t : targetFileMap.keySet()) {
			// get file list to remove by comparing repofs
			for (FileInfo f : targetFileMap.get(t)) {
				if (!repofset.contains(f.getPath())) {
					result.add(new DeployFile(srcmgr.getRepository(), 
							f.getPath(), CfgUtil.toAgentLoc(t), 
							0L, 0L, Mode.DELETE));
				}
			}
		}
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
				+ "\n\t[-u | -i ] [ -del ]" + "\n\t[-after <command-name> ...]";
	}
}
