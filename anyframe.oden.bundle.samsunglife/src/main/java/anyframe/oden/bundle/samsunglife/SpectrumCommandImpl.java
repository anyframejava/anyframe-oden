package anyframe.oden.bundle.samsunglife;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import anyframe.common.bundle.gate.CustomCommand;
import anyframe.oden.bundle.common.ArraySet;
import anyframe.oden.bundle.common.Assert;
import anyframe.oden.bundle.common.BundleUtil;
import anyframe.oden.bundle.common.FileUtil;
import anyframe.oden.bundle.common.JSONUtil;
import anyframe.oden.bundle.common.Logger;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.AgentFile;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.DeployFileUtil;
import anyframe.oden.bundle.core.FileMap;
import anyframe.oden.bundle.core.RepositoryProviderService;
import anyframe.oden.bundle.core.command.Cmd;
import anyframe.oden.bundle.core.config.OdenConfigService;
import anyframe.oden.bundle.core.job.CompareAgentsJob;
import anyframe.oden.bundle.core.job.Job;
import anyframe.oden.bundle.core.job.JobManager;
import anyframe.oden.bundle.core.job.DeployFileResolver;
import anyframe.oden.bundle.core.record.DeployLogService2;
import anyframe.oden.bundle.core.record.RecordElement2;
import anyframe.oden.bundle.core.repository.RepositoryService;

/**
 * 
 * Oden Shell command about samsunglife. Deploy by file, redploy, compare agents
 * sync and transfer felchlog.
 * 
 * @author joon1k
 * 
 */
public class SpectrumCommandImpl implements CustomCommand {

	public final static String[] SYNC_OPT = { "sync" };

	public final static String[] INCLUDE_OPT = { "i" };

	public final static String[] TARGET_OPT = { "t", "target" };

	private final static String CMD_NAME = "spectrum";

	private BundleContext context;

	protected void activate(ComponentContext context) {
		this.context = context.getBundleContext();
	}

	protected SpectrumNotifier notifier;

	protected void setSpectrumNotifier(SpectrumNotifier notifier) {
		this.notifier = notifier;
	}

	protected RepositoryProviderService repositoryProvider;

	protected void setRepositoryProvider(RepositoryProviderService r) {
		this.repositoryProvider = r;
	}

	protected OdenConfigService configService;

	public void setConfigService(OdenConfigService configService) {
		this.configService = configService;
	}

	private DeployLogService2 deploylog;

	protected void setDeployLogService(DeployLogService2 recordsvc) {
		this.deploylog = recordsvc;
	}

	protected JobManager jobManager;

	protected void setJobManager(JobManager jm) {
		this.jobManager = jm;
	}

	public void execute(String line, PrintStream out, PrintStream err) {
		String consoleResult = "";
		boolean isJSON = true;
		try {
			// init
			JSONArray ja = new JSONArray();
			Cmd cmd = new Cmd(line);
			isJSON = cmd.getOption(Cmd.JSON_OPT) != null;
			String action = cmd.getAction();
			String user = CommandHelper.extractUserName(cmd);

			if ("fetchlog".equals(action)) {
				List<String> ids = cmd.getOptionArgList(INCLUDE_OPT);
				if (ids.size() == 0)
					throw new OdenException("Required more arguments.");
				for (String id : ids)
					ja.put(new JSONObject().put(id,
							notifier.notifyResult(id) ? "S" : "F"));
			} else if ("test".equals(action)) {
				String[] loc = cmd.getOptionArgArray(new String[] { "r" });
				List<String> inc = cmd.getOptionArgList(new String[] { "i" });
				if (loc.length == 0 || inc.isEmpty())
					throw new OdenException("Required more arguments.");
				List<File> fs = dragFiles(loc, inc);

				Set<DeployFile> toDeploys = preview(fs);

				// formatting outputs
				if (isJSON) {
					ja = (JSONArray) JSONUtil.jsonize(toDeploys);
				} else {
					StringBuffer buf = new StringBuffer();
					for (DeployFile dfile : toDeploys) {
						buf.append(DeployFileUtil.modeToString(dfile.mode())
								+ ": " + dfile.getRepo().toString() + " "
								+ dfile.getPath() + " >> "
								+ dfile.getAgent().agentAddr() + "/"
								+ dfile.getAgent().location() + "\n");
					}
					consoleResult = buf.toString();
				}
			} else if ("run".equals(action)) {
				String[] loc = cmd.getOptionArgArray(new String[] { "r" });
				List<String> inc = cmd.getOptionArgList(new String[] { "i" });
				if (loc.length == 0 || inc.isEmpty())
					throw new OdenException("Required more arguments.");
				List<File> fs = dragFiles(loc, inc);
				boolean isSync = cmd.getOption(SYNC_OPT) != null;
				String txid = run(fs, isSync, user, inc);
				if (isSync) {
					List<RecordElement2> list = deploylog.search(txid, null,
							null, null, null, null, false);
					Assert.check(list.size() == 1, "Couldn't find a log: "
							+ txid);
					RecordElement2 r = list.get(0);
					ja.put(new JSONObject().put("txid", txid).put("status",
							r.isSuccess() ? "S" : "F").put("count",
							r.getDeployFiles().size()));
				} else {
					ja.put(new JSONObject().put("txid", txid));
				}
			} else if ("redeploy".equals(action)) {
				String id = cmd.getActionArg();
				boolean isSync = cmd.getOption(SYNC_OPT) != null;
				String txid = redeploy(id, isSync, user);
				if (isSync) {
					List<RecordElement2> list = deploylog.search(txid, null,
							null, null, null, null, false);
					Assert.check(list.size() == 1, "Couldn't find a log: "
							+ txid);
					RecordElement2 r = list.get(0);
					ja.put(new JSONObject().put("txid", txid).put("status",
							r.isSuccess() ? "S" : "F").put("count",
							r.getDeployFiles().size()));
				} else {
					ja.put(new JSONObject().put("txid", txid));
				}
			} else if ("fetchlist".equals(action)) {
				ja = new JSONArray(notifier.notifiedIds());
			} else if ("compare".equals(action)) {
				List<String> agentNames = cmd.getOptionArgList(TARGET_OPT);
				if (agentNames.size() == 0)
					throw new OdenException("Require more arguments.");

				Map<PathObj, List<AgentFile>> result = compare(agentNames);
				for (PathObj o : result.keySet()) {
					List<AgentFile> fs = result.get(o);
					ja.put(new JSONObject().put("path", o.path).put("match",
							o.success).put("agents", agentsToJSON(fs)));
				}
				writeToFile(result);
			} else {
				ja.put(getFullUsage());
			}

			if (isJSON)
				out.println(ja.toString());
			else if (consoleResult.length() > 0)
				out.println(consoleResult);
			else
				out.println(JSONUtil.toString(ja));

		} catch (Exception e) {
			err.println(JSONUtil.jsonizedException(e));
			Logger.error(e);
		}
	}

	private File writeToFile(Map<PathObj, List<AgentFile>> m) {
		String d = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(System
				.currentTimeMillis());
		File f = new File("spectrum", "cmp" + d + ".log");
		PrintStream out = null;
		try {
			FileUtil.mkdirs(f);
			out = new PrintStream(new FileOutputStream(f));
			for (PathObj o : m.keySet()) {
				List<AgentFile> fs = m.get(o);
				StringBuffer buf = new StringBuffer("[");
				for (int i = 0; i < fs.size(); i++) {
					buf.append(fs.get(i).agent());
					if (i + 1 < fs.size())
						buf.append(", ");
				}
				buf.append("]");

				out.println(o.path + "(" + o.success + "): " + buf.toString());
			}
		} catch (IOException e) {
			Logger.error(e);
			return null;
		} finally {
			if (out != null)
				out.close();
		}
		return f;
	}

	private String redeploy(final String id, boolean isSync, String user)
			throws OdenException {

		// composite deploy job & schedule it
		Job j = new SpectrumDeployJob(context, user, getName() + " redeploy " + id,
				new DeployFileResolver() {
					
					@Override
					public Set<DeployFile> resolveDeployFiles() throws OdenException {
						// get deployfiles from the history regarding the specified id.
						List<RecordElement2> list = deploylog.search(id, null, null, null,
								null, null, false);
						if (list.size() != 1)
							throw new OdenException("Fail to find a history for " + id);

						// filter deployfiles to get the failed files & their related files.
						return DeployFileUtil.filterToRedeploy(list.get(0)
								.getDeployFiles());
					}
				});
		if (isSync)
			jobManager.syncRun(j);
		else
			jobManager.schedule(j);
		return j.id(); // txid
	}

	private String run(final List<File> files, boolean isSync, String user,
			List<String> list) throws OdenException {
		Job j = new SpectrumDeployJob(context, user, "spectrum run " + list,
				new DeployFileResolver() {
					@Override
					public Set<DeployFile> resolveDeployFiles() throws OdenException {
						return preview(files);
					}
				});
		if (isSync)
			jobManager.syncRun(j);
		else
			jobManager.schedule(j);
		return j.id(); // txid
	}

	private Set<DeployFile> preview(List<File> fs) throws OdenException {
		Set<DeployFile> toDeploys = new ArraySet<DeployFile>();
		try {
			for (File f : fs) {
				preview(toDeploys, f);
			}
		} finally {
			for (File f : fs)
				f.delete();
		}
		return toDeploys;
	}

	private void preview(Set<DeployFile> toDeploys, File f)
			throws OdenException {
		SPFFile spf = SPFReader.read(f);
		for (SPFDeployFile df : spf.deployFiles()) {
			toDeploys.addAll(DeployFileHelper.toDeployFiles(df, configService));
		}
	}

	private List<File> dragFiles(String[] loc, List<String> inc)
			throws OdenException {
		RepositoryService reposvc = repositoryProvider.getRepoServiceByURI(loc);
		if (reposvc == null)
			throw new OdenException("Couldn't find a RepositoryService for "
					+ Arrays.toString(loc));

		List<File> fs = new ArrayList<File>();
		for (String path : reposvc.resolveFileRegex(loc, inc,
				Collections.EMPTY_LIST)) {
			File f = reposvc.getFile(loc, path, new File(BundleUtil.odenHome(), CMD_NAME).getPath());
			if (f == null)
				throw new OdenException("Fail to read: " + path);
			fs.add(f);
		}
		return fs;
	}

	public String getName() {
		return CMD_NAME;
	}

	public String getShortDescription() {
		return "";
	}

	public String getUsage() {
		return getName() + " help";
	}

	public String getFullUsage() {
		String rusage = getRepositoryUsages();
		return getName() + " test -r " + rusage + " -i <wildcard-location> ..."
				+ "\n\t run -r " + rusage
				+ " -i <wildcard-location> ... [-sync]"
				+ "\n\t fetchlog -i <txid> ..."
				+ "\n\t redeploy <txid> [-sync]" + "\n\t fetchlist"
				+ "\n\t compare -t <agent-name> ...";
	}

	private String getRepositoryUsages() {
		StringBuffer usages = new StringBuffer();
		for (Iterator<String> it = repositoryProvider.getRepositoryUsages()
				.iterator(); it.hasNext();) {
			usages.append("[" + it.next() + "]");
			if (it.hasNext())
				usages.append(" | ");
		}
		return usages.toString();
	}

	private Map<PathObj, List<AgentFile>> compare(List<String> agentNames)
			throws Exception {
		CompareAgentsJob j = new CompareAgentsJob(context, agentNames,
				"agent compare");
		jobManager.syncRun(j);
		FileMap m = j.result();

		Map<PathObj, List<AgentFile>> result = new TreeMap<PathObj, List<AgentFile>>();
		for (String path : m.keySet()) {
			AgentFile prev = null;
			List<AgentFile> fs = m.get(path);
			boolean success = agentNames.size() == fs.size();
			for (AgentFile f : fs) {
				if (success) {
					if (prev == null)
						prev = f;
					else
						success = prev.size() == f.size()
								&& prev.date() == f.date();
				}
			}
			result.put(new PathObj(path, success), fs);
		}
		return result;
	}

	class PathObj implements Comparable<PathObj> {
		String path;
		boolean success;

		PathObj(String path, boolean success) {
			this.path = path;
			this.success = success;
		}

		public int compareTo(PathObj o) {
			return path.compareTo(o.path);
		}
	}

	private JSONArray agentsToJSON(List<AgentFile> fs) throws JSONException {
		JSONArray arr = new JSONArray();
		for (AgentFile f : fs) {
			arr.put(new JSONObject().put("agent", f.agent()).put("size",
					f.size()).put("date", f.date()));
		}
		return arr;
	}

}
