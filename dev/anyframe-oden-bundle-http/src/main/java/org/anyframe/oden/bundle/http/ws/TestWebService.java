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
package org.anyframe.oden.bundle.http.ws;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.anyframe.oden.bundle.http.WebService;
import org.json.JSONArray;
import org.ungoverned.osgi.service.shell.ShellService;

/**
 * This is TestWebService class.
 * 
 * @author Junghwan Hong
 */
public class TestWebService implements WebService {
	ShellService shell;

	public void setShellService(ShellService shell) {
		this.shell = shell;
	}

	public String name() {
		return "test";
	}

	public void main(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		req.setAttribute("list", new JSONArray(taskList()).toString());
		req.getRequestDispatcher("index.jsp").forward(req, res);
	}

	public void test(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		req.setAttribute("list", new JSONArray(taskList()).toString());
		req.getRequestDispatcher("wadmin.html").forward(req, res);
	}

	public void test2(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		req.getRequestDispatcher("wadmin.html").forward(req, res);
	}

	@SuppressWarnings("PMD")
	private List<Map<String, String>> taskList() {
		List<Map<String, String>> tasks = new LinkedList<Map<String, String>>();

		Map<String, Boolean> actives = activeJobList();
		Map<String, Boolean> statuss = recentStatuss();

		for (String t : tasknames()) {
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

	private List<String> tasknames() {
		List<String> l = new LinkedList<String>();
		l.add("portal-deploy");
		l.add("channel-deploy");
		l.add("batch-deploy");
		return l;
	}
}
