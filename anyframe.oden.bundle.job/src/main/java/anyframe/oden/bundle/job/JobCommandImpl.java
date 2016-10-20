package anyframe.oden.bundle.job;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import anyframe.common.bundle.gate.CustomCommand;
import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.FileUtil;
import anyframe.oden.bundle.common.Logger;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.Pair;
import anyframe.oden.bundle.common.StringUtil;
import anyframe.oden.bundle.core.AgentFile;
import anyframe.oden.bundle.core.FileMap;
import anyframe.oden.bundle.core.RepositoryProviderService;
import anyframe.oden.bundle.core.command.Cmd;
import anyframe.oden.bundle.core.command.JSONUtil;
import anyframe.oden.bundle.core.job.JobManager;
import anyframe.oden.bundle.core.repository.RepositoryService;
import anyframe.oden.bundle.core.txmitter.TransmitterService;
import anyframe.oden.bundle.deploy.DeployerService;
import anyframe.oden.bundle.job.config.CfgCommand;
import anyframe.oden.bundle.job.config.CfgJob;
import anyframe.oden.bundle.job.config.CfgSource;
import anyframe.oden.bundle.job.config.CfgTarget;
import anyframe.oden.bundle.job.config.CfgUtil;
import anyframe.oden.bundle.job.config.JobConfigService;
import anyframe.oden.bundle.job.page.PageHandler;
import anyframe.oden.bundle.job.page.PageHandlerOr;

public class JobCommandImpl implements CustomCommand{

	BundleContext context;
	protected void activate(ComponentContext context){
		this.context = context.getBundleContext();
	}
	
	JobConfigService jobConfig;
	protected void setJobConfigService(JobConfigService jobConfig){
		this.jobConfig = jobConfig;
	}

	JobManager jobManager;
	protected void setJobManager(JobManager jobManager){
		this.jobManager = jobManager;
	}
	
	RepositoryProviderService repositoryProvider;
	protected void setRepositoryProvider(RepositoryProviderService r) {
		this.repositoryProvider = r;
	}
	
	TransmitterService txmitter;
	protected void setTransmitterService(TransmitterService tx){
		this.txmitter = tx;
	}
	
	PageHandler pageHandler;
	protected void setPageHandler(PageHandler pageHandler){
		this.pageHandler = pageHandler;
	}
	
	public void execute(String line, PrintStream out, PrintStream err) {
		boolean isJSON = false;
		try{
			Cmd cmd = new Cmd(line);
			isJSON = cmd.getOption(Cmd.JSON_OPT) != null;
			
			if(cmd.getAction().length() == 0 || cmd.getAction().equals("help")){
				out.println(getFullUsage());
				return;
			}
			
			out.println( execute(cmd, isJSON) );
		}catch(Exception e){
			err.println(isJSON ? JSONUtil.jsonizedException(e) : e.getMessage());
			Logger.error(e);
		}
	}
	
	private String execute(Cmd cmd, boolean isJSON) throws Exception{
		String action = cmd.getAction();
		if(action.equals("info")){
			if(StringUtil.empty(cmd.getActionArg())){
				List<String> jobs = jobConfig.listJobs();
				if(isJSON)
					return new JSONArray(jobs).toString();
				
				StringBuffer buf = new StringBuffer();
				for(String s : jobs)
					buf.append(s+"\n");
				return buf.toString();
			}
			
			CfgJob job = jobConfig.getJob(cmd.getActionArg());
			if(job == null) throw new OdenException("Invalid Job Name: " + cmd.getActionArg());
			if(isJSON){
				JSONObject jo = new JSONObject();
				jo.put("name", job.getName());
				jo.put("sources", job.getSource().toJSON());
				jo.put("targets", new JSONArray(
						allTargetStatus(job.getTargets()) ));
				
				JSONArray cs = new JSONArray();
				for(CfgCommand c : job.getCommands())
					cs.put(c.toJSON());
				jo.put("commands", cs);
				return new JSONArray().put(jo).toString();
			}
			
			StringBuffer buf = new StringBuffer();
			buf.append("name: " + job.getName()+"\n");
			buf.append("source: " + job.getSource().toJSON() + "\n");
			buf.append("targets: \n");
			for(JSONObject targetStatus : allTargetStatus(job.getTargets()) )
				buf.append("\t"+targetStatus+"\n");
			buf.append("commands: \n");
			for(CfgCommand c : job.getCommands())
				buf.append("\t" + c.toJSON() + "\n");
			return buf.toString();
		}else if(action.equals("compare")){
			final CfgJob job = jobConfig.getJob(cmd.getActionArg());
			if(job == null)
				throw new OdenException("Invalid Job Name: " + cmd.getActionArg());
			final List<String> targetNames = cmd.getOptionArgList(new String[]{"t"});
			final List<CfgTarget> targets = getActiveTargets(job.getAllTargets(targetNames));
			final boolean failonly = cmd.getOption("failonly") != null;
			String pgscale0 = cmd.getOptionArg(new String[]{"pgscale"});
			final int pgscale = StringUtil.empty(pgscale0) ? -1 : Integer.valueOf(pgscale0);
			
			if(isJSON){
				return new JSONArray().put(pageHandler.getCachedData(cmd, 
						pgscale, new PageHandlerOr(){
					public JSONArray run() throws Exception {
						FileMap m = getAllTargetFiles(targets);
						
						Set<String> paths = new TreeSet<String>(m.keySet());
						
						JSONArray ja = new JSONArray();
						for(String path : paths){
							JSONObject jo = new JSONObject();
							jo.put("path", path);
							
							List<AgentFile> fs = m.get(path);
							boolean equal = (fs.size() == targets.size());
							long _date = -1;
							long _size = -1;
							JSONArray ja2 = new JSONArray();
							for(AgentFile f : fs){
								JSONObject o = new JSONObject();
								o.put("name", f.agent());
								o.put("date", String.valueOf(f.date()));
								o.put("size", String.valueOf(f.size()));
								ja2.put(o);
								
								if(equal){
									if(_date == -1){
										_date = f.date();
										_size = f.size();
									}else {
										equal = (_date == f.date() && _size == f.size());
									}
								}
							}
							if(failonly && equal) continue;
							
							jo.put("equal", equal ? "T" : "F");
							jo.put("targets", ja2);
							ja.put(jo);
						}
						return ja; 
					}
				})).toString();
			}
			
			FileMap m = getAllTargetFiles(targets);
			
			Set<String> paths = new TreeSet<String>(m.keySet());

			StringBuffer buf = new StringBuffer();
			for(String path : paths){
				StringBuffer tbuf = new StringBuffer();
				List<AgentFile> fs = m.get(path);
				boolean equal = (fs.size() == targets.size());
				long _date = -1;
				long _size = -1;
				for(AgentFile f : fs){
					tbuf.append("{"+ f.agent() + "/" + f.size() +"b/" + f.date() +"}" );
					
					if(equal){
						if(_date == -1){
							_date = f.date();
							_size = f.size();
						}else {
							equal = (_date == f.date() && _size == f.size());
						}
					}
				}
				if(failonly && equal) continue;
				
				buf.append((equal ? "(T)" : "(F)") + " " + path + " [" + tbuf.toString() + "]\n");
			}
			return buf.toString();
		}else if(action.equals("mapping-scan")){
			CfgJob job = jobConfig.getJob(cmd.getActionArg());
			if(job == null) throw new OdenException(
					"Invalid job: " + cmd.getActionArg());
			
			List<Pair> maps = getSCMRoot(job);
			JSONArray ret = new JSONArray();
			for(Pair p : maps)
				ret.put(new JSONObject().put("dir", p.getArg0())
						.put("checkout-dir", p.getArg1()));
			return ret.toString();
		}else {
			throw new OdenException("Invalid Action: " + action);
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
	
	private List<Pair> getMappings(CfgJob job, String chkoutDir) 
			throws OdenException{
		RepositoryService repoSvc = repositoryProvider.getRepoServiceByURI(
				CfgUtil.toRepoArg(job.getSource()));
		RepoManager repo = new RepoManager(repoSvc, 
				CfgUtil.toRepoArg(job.getSource()));
		if (repo == null)
			throw new OdenException("Invalid Repository: " 
					+ job.getSource().getPath());
		
		RepoManager scm = new RepoManager(repoSvc, new String[]{
				"file://" + chkoutDir});
		if (scm == null)
			throw new OdenException("Invalid Checkout dir: " 
					+ chkoutDir);
		
		List<Pair> ret = new ArrayList<Pair>();
		String webinf = repo.findDir("WEB-INF", null);
		if(webinf == null) return ret;
		String webinfOriginal = scm.findDir("WEB-INF", 
				FileUtil.combinePath(job.getSource().getPath(), webinf));
		if(webinfOriginal == null) return ret;
		if(webinf.length() == 0){	// when repo dir is WEB-INF
			ret.add(new Pair(".", 
					FileUtil.combinePath(chkoutDir, webinfOriginal)));
		}else{
			String parent = FileUtil.parentPath(webinf);
			ret.add(new Pair(parent.length() == 0 ? "." : parent, 
					FileUtil.combinePath(chkoutDir, 
							FileUtil.parentPath(webinfOriginal))));
		}
		
		String classes = FileUtil.combinePath(webinf, "classes");
		for(String d : scm.getSourceDirs()){
			ret.add(new Pair(classes, 
					FileUtil.combinePath(chkoutDir, d)));
		}
		return ret;
	}
	
	private List<Pair> getSCMRoot(CfgJob job) throws OdenException{
		RepositoryService repoSvc = repositoryProvider.getRepoServiceByURI(
				CfgUtil.toRepoArg(job.getSource()));
		
		// find src dir
		RepoManager repo = new RepoManager(repoSvc, 
				CfgUtil.toRepoArg(job.getSource()));
		String srcpath = repo.getAbolutePathFromParent("src");
		if(srcpath == null)
			return Collections.EMPTY_LIST;
		return getMappings(job, FileUtil.parentPath(srcpath));
	}
	
	private SourceManager getSourceManager(CfgSource src){
		RepositoryService repoSvc = repositoryProvider.getRepoServiceByURI(
				CfgUtil.toRepoArg(src));
		return new SourceManager(repoSvc, src);
	}
		
	private FileMap getAllTargetFiles(List<CfgTarget> targets) throws OdenException{
		// get target:ds map
		final Map<CfgTarget, DeployerService> agents = 
				new HashMap<CfgTarget, DeployerService>();
		for(CfgTarget ct : targets){
			DeployerService ds = txmitter.getDeployer(ct.getAddress());
			if(ds == null)
				throw new OdenException("Invalid address: " + ct.getAddress());
			agents.put(ct, ds);
		}
		
		final FileMap ret = new FileMap();
		List<Thread> threads = new ArrayList<Thread>();
		for(final CfgTarget ct : agents.keySet()){
			Thread t = new Thread(){			
				public void run() {
					DeployerService ds = agents.get(ct);
					try{
						List<FileInfo> fs = ds.listAllFiles(ct.getPath());
						for(FileInfo f : fs){
							ret.append(f.getPath(), newAgentFile(
									ct.getName(), f.getPath(), 
									f.size(), f.lastModified()));
						}
					}catch(Exception e){
						Logger.error(e);
					}
				}
			};
			threads.add(t);
			t.start();
		}
		
		for(Thread t : threads){
			try {
				t.join();
			} catch (InterruptedException e) {
			}
		}
		return ret;
	}
	
	private AgentFile newAgentFile(String agent, String path, long size, 
			long date){
		AgentFile af = new AgentFile(agent, path);
		af.setDate(date);
		af.setSize(size);
		return af;
	}
	
	private JSONObject targetStatus(CfgTarget t) throws JSONException{
		DeployerService ds = txmitter.getDeployer(t.getAddress());
		boolean alive = false;
		try{
			alive = ds != null;
		}catch(Exception e){
			Logger.error(e);
		}
		return t.toJSON().put("status", (alive?"T":"F"));
	}
	
	private Collection<JSONObject> allTargetStatus(
			Collection<CfgTarget> targets) {
		final Collection<JSONObject> ret = new Vector<JSONObject>();
		List<Thread> ths = new ArrayList<Thread>();
		for(final CfgTarget t : targets){
			ths.add(new Thread(){
				public void run() {
					try {
						ret.add(targetStatus(t));
					} catch (JSONException e) {
						// should not be occured.
						Logger.error(e);
					}
				}
			});
		}
		for(Thread th : ths)
			th.start();
		for(Thread th : ths)
			try{ th.join(); }catch(InterruptedException e){ }
		return ret;
	}
	
	private String getFullUsage() {
		return "job info [ <job> ]" +
			"\njob compare <job> [ -t <target> ... ] [ -failonly ]" +
			"\njob mapping-scan <job>";
	}
	
	public String getName() {
		return "job";
	}

	public String getShortDescription() {
		return "info / compare Job";
	}

	public String getUsage() {
		return "job help"; 
	}

}
