package anyframe.oden.bundle.job.page;

import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.bundle.core.command.Cmd;

public interface PageHandler {
	public JSONObject getCachedData(Cmd cmd, int pgscale, PageHandlerOr or) 
			throws Exception;
	
	public JSONObject get(String cmd, int page, int pgscale) throws Exception;
	
	public void put(String cmd, JSONArray data);
}
