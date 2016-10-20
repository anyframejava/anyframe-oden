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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.ungoverned.osgi.service.shell.ShellService;

/**
 * Servlet to make availble to execute commands in the OSGi Shell.
 * 
 * @author joon1k
 *
 */
public class ShellServlet extends HttpServlet {
	private static final long serialVersionUID = -2045509211013138867L;

	public final static String NAME = "shell";
	
	private ShellService shellService;
	
	private HttpContext httpContext;
	
	private SecurityHandler securityHandler;
	
	protected void setHttpService(HttpService hs){
		try {
			hs.registerServlet(
					"/" + ShellServlet.NAME, 
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
	
	protected void activate(ComponentContext context){
		String ext = System.getProperty("os.name").startsWith("Windows") ? "cmd" : "sh";
		System.out.println("::: You can access Oden in the Command Line. (e.g. runc."+ ext +" help)");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}
	
	/**
	 * 요청 분석하여 ShellService의 적당한 Command 실행
	 * Command는 JSONizedCommand 타입이어야 하며
	 * Command실행시 err에 값이 있으면 err의 내용을 response로 보내고,,
	 * 없으면 out의 내용을 response로 보냄
	 * <br/>
	 * Exception은 ["UnknowException": stackTrace] 혹은
	 * ["ShellException": msg] 형태를 가짐 
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		
		PrintStream out = null;
		PrintStream err = null;

		req.setCharacterEncoding("utf-8");
		res.setContentType("charset=utf-8");
		res.setCharacterEncoding("utf-8");
		try {
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			ByteArrayOutputStream bErr = new ByteArrayOutputStream();
			out = new PrintStream(bOut);
			err = new PrintStream(bErr);
			
			// execute command & its results are already written to the out or err.
			BufferedReader reader = null;
			try {
				reader = req.getReader();
				String cmd = readCmdLine(reader);
				if(cmd == null) throw new Exception();
				if(cmd.trim().endsWith(";")) {		// for osgi original commands
					cmd = cmd.substring(0, cmd.lastIndexOf(";"));
				} else {	// for oden commands
					cmd+= " -_user " + userName(req);
				}
				
				shellService.executeCommand(cmd, out, err);
			} catch (Exception e) {
				throw new ServletException("Couldn't execute a command", e);
			} finally {
				if(reader != null) reader.close();
			}
		
			String sOut = bOut.toString();
			String sErr = bErr.toString();			
			PrintWriter writer = null; 
			
			try{
				writer = res.getWriter();
				if(sErr.length() > 0){	// has error
					sOut = sErr;
				}
				writer.print(sOut);
			}finally {
				if(writer != null)
					writer.close();
			}
		} finally {
			if(out != null)
				out.close();
			
			if(err != null)
				err.close();
			
		}
	}
	
	private String userName(HttpServletRequest req){
		Object o = req.getAttribute(HttpContext.REMOTE_USER);
		return o == null ? req.getRemoteAddr() : o.toString();
	}
	
	private String readCmdLine(BufferedReader reader) throws IOException{
		return reader.readLine();
	}
}
