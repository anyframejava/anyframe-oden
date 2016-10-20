package anyframe.oden.bundle.job.page;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.ComponentContext;

import anyframe.oden.bundle.common.Logger;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.StringUtil;
import anyframe.oden.bundle.core.command.Cmd;
import anyframe.oden.bundle.core.command.Opt;

public class PageMemoryHandlerImpl implements PageHandler{
	private int CACHE_SZ = 20;
	
	private JSONArrayFilter filter = null;
	int pgscale = 20;

	Map<String, JSONArray> pgcache = new HashMap<String, JSONArray>();
	Queue<String> datecache = new LinkedList<String>();
	
	Object latch = new Object();
	
	protected void activate(ComponentContext context) {
		String cacheSz = context.getBundleContext().getProperty("page.cache.size");
		this.CACHE_SZ = StringUtil.empty(cacheSz) ? 20 : Integer.valueOf(cacheSz);
		
		String _scale = context.getBundleContext().getProperty("page.scale");
		this.pgscale = StringUtil.empty(_scale) ? 20 : Integer.valueOf(_scale);
		this.filter = new JSONArrayFilter();
	}
	
	public void setFilter(JSONArrayFilter filter){
		this.filter = filter;
	}
		
	/**
	 * 
	 * @param cmd
	 * @return null if no appropriate data
	 */
	public JSONObject getCachedData(Cmd cmd, int pgscale, PageHandlerOr or) 
			throws Exception{
		if(pgscale < 1)
			pgscale = this.pgscale;
		
		String _cmd = cmd.toString();
		String _page = cmd.getOptionArg(new String[]{"page"});
		if(StringUtil.empty(_page)){
			JSONArray arr = or.run();
			return makeJSONObject(arr, arr.length());
		}
		
		int page = Integer.valueOf(_page);
		if(page == 0)
			return runCachedOr(_cmd, page, pgscale, or);
		
		JSONObject ret = get(_cmd, page, pgscale);
		if(ret == null)		// cache hit fail
			return runCachedOr(_cmd, page, pgscale, or);
		return ret;
	}
	
	private JSONObject runCachedOr(String cmd, int page, 
			int pgscale, PageHandlerOr or) throws Exception {
		put(cmd, or.run());
		JSONObject ret = get(cmd, page, pgscale);
		return ret != null ? ret : new JSONObject().put("total", 0)
				.put("data", new JSONArray());
	}
	
	public JSONObject get(String cmd, int page, int pgscale) 
			throws Exception{
		if(pgscale < 1)
			pgscale = this.pgscale;
		synchronized (latch) {
			String cmdkey = removeRedundancy(cmd);
			
			JSONArray found = pgcache.get(cmdkey);
			if(found == null) return null;
			
			return makeJSONObject(filter.run(found, page, pgscale), found.length());
		}
	}
	
	public void put(String cmd, JSONArray data){
		synchronized (latch) {
			try{
				String cmdkey = removeRedundancy(cmd);
				pgcache.put(cmdkey, data);
				datecache.add(cmdkey);
	
				while( ((Runtime.getRuntime().totalMemory() / Runtime.getRuntime().freeMemory()) < 0.3) 
						&& pgcache.size() > 1){
					String key = datecache.poll();
					pgcache.remove(key);
				}
			}catch(OdenException e){
				Logger.error(e);
			}
		}
	}
	
	private String removeRedundancy(String s) throws OdenException{
		Cmd cmd = new Cmd(s);
		List<Opt> opts = cmd.getOptions();
		int i=0; 
		while(i<opts.size()){
			Opt opt = opts.get(i);
			if(!opt.getName().equals("json") &&
					!opt.getName().equals("_user") &&
					!opt.getName().equals("page")) 
				i++;
			opts.remove(i);
		}
		return cmd.toString();
	}
	
	private JSONObject makeJSONObject(JSONArray arr, int total) throws JSONException{
		return new JSONObject().put("total", total).put("data", arr);
	}
}
