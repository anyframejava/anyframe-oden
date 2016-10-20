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
package org.anyframe.oden.bundle.http;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is OdenServlet class.
 * 
 * @author Junghwan Hong
 */
public class OdenServlet extends SecuredServlet {
	List<WebService> wss = new ArrayList<WebService>();

	protected void addWebService(WebService ws) {
		wss.add(ws);
	}

	protected void removeWebService(WebService ws) {
		wss.remove(ws);
	}

	public OdenServlet() {
		super("oden");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	@Override
	@SuppressWarnings("PMD")
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String method = req.getParameter("method");
		if (method == null || method.length() == 0) {
			String forward = req.getParameter("forward");
			if (forward == null || forward.length() == 0) {
				forward = "index";
			}
			req.getRequestDispatcher(forward + ".html").forward(req, res);
			return;
		}

		// find class name from parameter
		int classidx = method.indexOf('_');
		if (classidx == -1) {
			throw new ServletException(
					"Allowed parameter: classname_methodname: " + method);
		}
		String classname = method.substring(0, classidx);
		method = method.length() > classidx + 1 ? method
				.substring(classidx + 1) : null;

		// find matched WebService
		WebService obj = null;
		for (WebService ws : wss) {
			if (classname.equals(ws.name())) {
				obj = ws;
				break;
			}
		}
		if (obj == null) {
			throw new ServletException("No proper WebService: " + classname);
		}

		// call matched method
		try {
			Method m = obj.getClass().getDeclaredMethod(method,
					HttpServletRequest.class, HttpServletResponse.class);

			req.setCharacterEncoding("utf-8");
			res.setContentType("charset=utf-8");
			res.setCharacterEncoding("utf-8");
			m.invoke(obj, req, res);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

}
