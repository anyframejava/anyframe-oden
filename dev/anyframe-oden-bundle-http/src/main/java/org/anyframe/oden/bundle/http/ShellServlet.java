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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.anyframe.oden.bundle.common.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.ungoverned.osgi.service.shell.ShellService;

/**
 * Servlet to make availble to execute commands in the OSGi Shell.
 * 
 * @author Junghwan Hong
 */
public class ShellServlet extends HttpServlet {
	private static final long serialVersionUID = -2045509211013138867L;

	public final static String NAME = "shell";

	private ShellService shellService;

	private HttpContext httpContext;

	private SecurityHandler securityHandler;

	protected void setHttpService(HttpService hs) {
		try {
			hs.registerServlet("/" + ShellServlet.NAME, this, null,
					httpContext = new ShellHttpContext(hs, securityHandler));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void setSecurityHandler(SecurityHandler handler) {
		this.securityHandler = handler;
		if (httpContext != null) // handler is already binded.
			((ShellHttpContext) httpContext).setSecurityHandler(handler);
	}

	protected void unsetSecurityHandler(SecurityHandler handler) {
		setSecurityHandler(null);
	}

	public void setShellService(ShellService sh) {
		this.shellService = sh;
	}

	protected void activate(ComponentContext context) {
		String ext = System.getProperty("os.name").startsWith("Windows") ? "cmd"
				: "sh";
		System.out
				.println("::: You can access Oden in the Command Line. (e.g. runc."
						+ ext + " help)");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	/**
	 * 요청 분석하여 ShellService의 적당한 Command 실행 Command는 JSONizedCommand 타입이어야 하며
	 * Command실행시 err에 값이 있으면 err의 내용을 response로 보내고,, 없으면 out의 내용을 response로 보냄 <br/>
	 * Exception은 ["UnknowException": stackTrace] 혹은 ["ShellException": msg] 형태를 가짐
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		PrintStream out = null;

		req.setCharacterEncoding("utf-8");
		res.setContentType("charset=utf-8");
		res.setCharacterEncoding("utf-8");
		try {
			out = new PrintStream(new BufferedOutputStream(
					res.getOutputStream()));
			// execute command & its results are already written to the out or
			// err.
			BufferedReader reader = null;
			try {

				reader = req.getReader();
				String cmd = readCmdLine(reader);
				if (cmd == null)
					throw new Exception();
				if (cmd.trim().endsWith(";")) { // for osgi original commands
					cmd = cmd.substring(0, cmd.lastIndexOf(";"));
				} else if (!cmd.trim().contains("_user")) { // for oden commands
															// in user attribute
					cmd += " -_user " + userName(req);
				}

				long t = System.currentTimeMillis();
				Logger.info("Command Launched: "
						+ URLDecoder.decode(cmd, "utf-8") + "\n");
				shellService.executeCommand(URLDecoder.decode(cmd, "utf-8"),
						out, out);
				Logger.info("Command Finished in "
						+ (System.currentTimeMillis() - t) + "ms\n");
			} catch (Exception e) {
				throw new ServletException("Couldn't execute a command", e);
			} finally {
				if (reader != null)
					reader.close();
			}

		} finally {
			if (out != null)
				out.close();
		}
	}

	private String userName(HttpServletRequest req) {
		Object o = req.getAttribute(HttpContext.REMOTE_USER);
		return o == null ? req.getRemoteAddr() : o.toString();
	}

	private String readCmdLine(BufferedReader reader) throws IOException {
		return reader.readLine();
	}
}
