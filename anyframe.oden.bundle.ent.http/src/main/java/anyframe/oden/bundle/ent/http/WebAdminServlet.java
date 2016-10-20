/*
 * Copyright 2009, 2010 SAMSUNG SDS Co., Ltd. All rights reserved.
 *
 * No part of this "source code" may be reproduced, stored in a retrieval
 * system, or transmitted, in any form or by any means, mechanical,
 * electronic, photocopying, recording, or otherwise, without prior written
 * permission of SAMSUNG SDS Co., Ltd., with the following exceptions:
 * Any person is hereby authorized to store "source code" on a single
 * computer for personal use only and to print copies of "source code"
 * for personal use provided that the "source code" contains SAMSUNG SDS's
 * copyright notice.
 *
 * No licenses, express or implied, are granted with respect to any of
 * the technology described in this "source code". SAMSUNG SDS retains all
 * intellectual property rights associated with the technology described
 * in this "source code".
 *
 */
package anyframe.oden.bundle.ent.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.ungoverned.osgi.service.shell.ShellService;

/**
 * Servlet to make availble to execute commands in the OSGi Shell.
 * 
 * @author joon1k
 *
 */
public class WebAdminServlet extends HttpServlet {
	public final static String NAME = "wadmin";
	
	private ShellService shellService;
	
	private HttpContext httpContext;
	
	private SecurityHandler securityHandler;
	
	protected void setHttpService(HttpService hs){
		try {
			hs.registerServlet(
					"/" + WebAdminServlet.NAME, 
					this, 
					null, 
					httpContext = new ShellHttpContext(hs, securityHandler));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void setSecurityHandler(SecurityHandler handler){
		this.securityHandler = handler;
		if(httpContext != null)		// handler is already binded.
			((ShellHttpContext)httpContext).setSecurityHandler(handler);
	}
	
	protected void unsetSecurityHandler(SecurityHandler handler) {
		setSecurityHandler(null);
	}
	
	public void setShellService(ShellService sh) {
		this.shellService = sh;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		res.setContentType("charset=utf-8");
		res.setCharacterEncoding("utf-8");
		
		PrintWriter w = res.getWriter();
		w.print(new JSONArray(taskList()).toString());
		w.close();
	}
	
	public List<Map<String, String>> taskList(){
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
