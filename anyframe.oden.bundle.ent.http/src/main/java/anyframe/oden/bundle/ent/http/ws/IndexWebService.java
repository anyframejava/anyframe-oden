package anyframe.oden.bundle.ent.http.ws;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.ungoverned.osgi.service.shell.ShellService;

import anyframe.oden.bundle.ent.http.WebService;

public class IndexWebService implements WebService {
	public String name() {
		return "index";
	}
	
	ShellService shell;
	public void setShellService(ShellService shell){
		this.shell = shell;
	}
	
	public void onload(HttpServletRequest req, HttpServletResponse res)
			throws Exception{
		PrintWriter w = null;
		try{
			w = res.getWriter();
			w.print(new JSONArray(taskList()).toString());
		}finally{
			w.close();
		}
	}

	public void stop(HttpServletRequest req, HttpServletResponse res)
			throws Exception{
		PrintWriter w = res.getWriter();
		w.close();
	}
	
	public void clean_deploy(HttpServletRequest req, HttpServletResponse res)
			throws Exception{
		PrintWriter w = res.getWriter();
		w.close();
	}
	
	public void del(HttpServletRequest req, HttpServletResponse res)
			throws Exception{
		PrintWriter w = res.getWriter();
		w.close();
	}
	
	private List<Map<String, String>> taskList(){
		List<Map<String, String>> tasks = new LinkedList<Map<String, String>>();
		
		Map<String, Boolean> actives = activeJobList();
		Map<String, Boolean> statuss = recentStatuss();
		
		for(String t : tasknames()){
			Map m = new HashMap<String, String>();
			Boolean status = statuss.get(t); 
			m.put("status", status == null ? "X" : status ? "T" : "F");
			m.put("task", t);
			Boolean active = actives.get(t);
			m.put("ready", active != null && active ? "F" : "T");
			tasks.add(m);
		}
		return tasks;
	}
	
	private Map<String, Boolean> recentStatuss() {
		Map<String, Boolean> m = new HashMap<String, Boolean>();
		m.put("portal-deploy", true);
		m.put("batch-deploy", false);
		return m;
	}

	private Map<String, Boolean> activeJobList() {
		Map<String, Boolean> m = new HashMap<String, Boolean>();
		m.put("channel-deploy", true);
		return m;
	}

	private List<String> tasknames(){
		List<String> l = new LinkedList<String>();
		l.add("portal-deploy");
		l.add("channel-deploy");
		l.add("batch-deploy");
		return l;
	}
}
