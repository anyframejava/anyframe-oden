/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package anyframe.common.bundle.gate;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpService;
import org.ungoverned.osgi.service.shell.ShellService;

/**
 * Servlet to make availble to execute commands in the OSGi Shell.
 * 
 * @author joon1k
 *
 */
public class ShellServlet extends HttpServlet {
	public final static String NAME = "shell";
	
	private ShellService shellService;
	
	protected void setHttpService(HttpService hs){
		try {
			hs.registerServlet(
					"/" + ShellServlet.NAME, 
					this, 
					null, 
					null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setShellService(ShellService sh) {
		this.shellService = sh;
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
				cmd+= " -_user " + req.getRemoteAddr();
				
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
	
	private String readCmdLine(BufferedReader reader) throws IOException{
		return reader.readLine();
	}
}
