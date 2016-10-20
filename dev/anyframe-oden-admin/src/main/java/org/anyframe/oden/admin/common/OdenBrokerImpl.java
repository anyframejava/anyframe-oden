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
package org.anyframe.oden.admin.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.anyframe.idgen.impl.Base64;
import org.anyframe.oden.admin.exception.BrokerException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class provides some methods to interface server
 * 
 * @author Junghwan Hong
 */
public class OdenBrokerImpl implements OdenBrokerService {
	private final static int TIMEOUT = 15000; // millis
	public final static String KNOWN_EXCEPTION = "KnownException";
	public final static String UNKNOWN_EXCEPTION = "UnknownException";

	/**
	 * Send request and get response, Interface server with http protocol
	 */
	public String sendRequest(String shellUrl, String msg) throws BrokerException {
		PrintWriter writer = null;
		String result = null;

		if (shellUrl != null && !(shellUrl.equals(""))) {
			try {
				URLConnection conn = init(shellUrl);

				// send
				writer = new PrintWriter(conn.getOutputStream());
				writer.println(URLEncoder.encode(msg, "utf-8"));

				writer.flush();

				// receive
				result = handleResult(conn);
			} catch (BrokerException e) {
				if (msg.startsWith("build") && e.getMessage().equals("Connection refused: connect")) {
				}else{
					throw new BrokerException(e.getMessage());
				}
			} catch (ConnectException e) {
				// throw new BrokerException("broker.info.notavail");
				throw new BrokerException("Oden Server is not available");
			} catch (Exception e) {
				throw new BrokerException("broker.exception");
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
			return result;
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	public boolean checkUser(String shellUrl, String userid, String password) throws BrokerException {
		PrintWriter writer = null;

		if (shellUrl != null && !(shellUrl.equals(""))) {
			try {

				URLConnection conn = init(shellUrl);

				// send
				writer = new PrintWriter(conn.getOutputStream());
				writer.println("help");
				writer.flush();

				// receive

				BufferedReader reader = null;

				reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));

				readAll(reader);

			} catch (ConnectException e) {
				// throw new BrokerException("broker.info.notavail");
				throw new BrokerException("oden.server.notrunning");
			} catch (IOException e) {
				throw new BrokerException("broker.info.notauth");
			} finally {
				if (writer != null) {
					writer.close();
				}
			}

		}
		return true;
	}

	private String handleResult(URLConnection conn) throws BrokerException, IOException {
		String result = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));

			result = readAll(reader);
			// check if shell exception or unknown exception
			JSONArray array = new JSONArray(result);
			determineException(array);

		} catch (JSONException e) {
			if (result.contains("Command not found")) {
				throw new BrokerException("broker.exception.notcommand");
			}
		} catch (BrokerException e) {
			throw new BrokerException(e.getMessage());
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return result;
	}

	/**
	 * ja가 exception이 들어있는 JSONArray인지 검사. exception이 들어있으면 해당 exception을
	 * throw..
	 * 
	 * @param ja
	 * @throws JSONException
	 * @throws BrokerException
	 */
	private void determineException(JSONArray ja) throws BrokerException, IOException {

		if (ja == null || ja.length() != 1) {
			return;
		}

		try {
			Object obj = ja.get(0);
			if (obj instanceof JSONObject) {
				final JSONObject json = (JSONObject) obj;
				if (json.has(UNKNOWN_EXCEPTION)) {
					// TODO unknown
					throw new BrokerException(json.getString(UNKNOWN_EXCEPTION));
				}
			}
		} catch (JSONException jexc) {
			throw new IOException(jexc.getMessage());
		}
	}

	private String readAll(BufferedReader reader) throws IOException {
		StringBuffer buf = new StringBuffer();

		for (String line; (line = reader.readLine()) != null;) {
			buf.append(line);
			buf.append("\n");
		}
		return buf.toString();
	}

	private URLConnection init(String url) throws MalformedURLException, IOException {
		URLConnection con = new URL(url).openConnection();

		con.setUseCaches(false);
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setConnectTimeout(TIMEOUT);

		// 우선 Oden Server 계정 상수로 세팅
		if (url != null) {
			con.addRequestProperty("Authorization", "Basic " + encode("oden", "oden0"));
		}

		return con;
	}

	private static String encode(String id, String pwd) {
		return Base64.encode((id + ":" + pwd).getBytes());
	}

}
