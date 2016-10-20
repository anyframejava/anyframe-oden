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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * This is ShellServletTest class.
 * 
 * @author Junghwan Hong
 */
@SuppressWarnings("PMD")
public class ShellServletTest {
	private final static String SHELL_URL = "http://localhost:9860/shell";
	public final static String KNOWN_EXCEPTION = "KnownException";
	public final static String UNKNOWN_EXCEPTION = "UnknownException";
	private final static int TIMEOUT = 15000; // millis

	public static void main(String... args) {
		String msg = "help"; // same with shell command line
		try {
			System.out.println(sendRequest(SHELL_URL, msg));
		} catch (ShellException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String sendRequest(String url, String msg)
			throws ShellException, Exception {
		PrintWriter writer = null;
		String result = null;
		try {
			URLConnection conn = init(url);

			// send
			writer = new PrintWriter(conn.getOutputStream());
			writer.println(msg);
			writer.flush();

			// receive
			result = handleResult(conn);

		} finally {
			if (writer != null)
				writer.close();
		}
		return result;

	}

	private static String handleResult(URLConnection conn)
			throws ShellException, Exception {
		String result = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			result = readAll(reader);

			// // check if shell exception or unknown exception
			// JSONObject json = new JSONObject(result);
			// if(json.has(KNOWN_EXCEPTION)) {
			// 		throw new ShellException(json.getString(KNOWN_EXCEPTION));
			// }else if(json.has(UNKNOWN_EXCEPTION)) {
			// 		throw new Exception(json.getString(UNKNOWN_EXCEPTION));
			// }

		} catch (IOException e) {
			throw new ShellException(e);
		} finally {
			if (reader != null)
				reader.close();
		}
		return result;
	}

	private static URLConnection init(String url) throws MalformedURLException,
			IOException {
		URLConnection con = new URL(url).openConnection();
		con.setUseCaches(false);
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setConnectTimeout(TIMEOUT);
		return con;
	}

	private static String readAll(BufferedReader reader) throws IOException {
		StringBuffer buf = new StringBuffer();
		String line = null;
		while ((line = reader.readLine()) != null) {
			buf.append(line + "\n");
		}
		return buf.toString();
	}
}
