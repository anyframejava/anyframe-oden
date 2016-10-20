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
package org.anyframe.oden.bundle.job.page;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.ComponentContext;

import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.common.StringUtil;
import org.anyframe.oden.bundle.core.command.Cmd;
import org.anyframe.oden.bundle.core.command.Opt;

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
