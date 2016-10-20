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
package anyframe.oden.eclipse.core.brokers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.messages.CommonMessages;

/**
 * This class provides some methods to interface server 
 * 
 * @author HONG JungHwan
 *
 */
public class OdenBrokerImpl implements OdenBrokerService {
	private final int TIMEOUT = 15000; // millis
	public final String KNOWN_EXCEPTION = "KnownException";
	public final String UNKNOWN_EXCEPTION = "java.lang.Exception";
	
	/**
	 * Send request and get response, Interface server with http protocol
	 */
	public String sendRequest(String shellUrl, String msg) throws OdenException {
		PrintWriter writer = null;
		String result = null;

		if(shellUrl != null){
			try {
				
				URLConnection conn = init(shellUrl);
				
				// send
				writer = new PrintWriter(conn.getOutputStream());
				writer.println(msg);
				writer.flush();
				
				// receive
				result = handleResult(conn);
			} catch (OdenException e) {
					throw new OdenException(e.getMessage());	
			} catch (ConnectException e) {
				OdenActivator.warning(CommonMessages.ODEN_CommonMessages_UnableToConnectServer);
			} catch (Exception e) {
				OdenActivator.error(CommonMessages.ODEN_CommonMessages_Exception_KnownExceptionLog, new Throwable(e.getMessage()));
				throw new OdenException(e.getMessage());
			} finally {
				if (writer != null){
					writer.close();
				}
			}
			return result;
		}else{
			return ""; //$NON-NLS-1$
		}
	}

	private String handleResult(URLConnection conn) throws OdenException, IOException {
		String result = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(conn
					.getInputStream(),"utf-8"));

			result = readAll(reader);

			// check if shell exception or unknown exception
			JSONArray array = new JSONArray(result);
			determineException(array);

		} catch (Exception e) {
			throw new OdenException(e.getMessage());
		} finally {
			if (reader != null)
				reader.close();
		}
		return result;
	}
	
	/**
	 * ja가 exception이 들어있는 JSONArray인지 검사. exception이 들어있으면 해당 exception을
	 * throw..
	 * 
	 * @param ja
	 * @throws JSONException
	 * @throws OdenException
	 */
	private void determineException(JSONArray ja) throws OdenException,
			IOException {
		
		if (ja == null || ja.length() != 1) {
			return;
		}

		try {
			Object obj = ja.get(0);
			if (obj instanceof JSONObject) {
				final JSONObject json = (JSONObject) obj;
				if (json.has(KNOWN_EXCEPTION)) {
					
					Display.getDefault().asyncExec(new Runnable(){
						public void run() {
							try {
								MessageDialog.openError(Display.getDefault().getActiveShell(), CommonMessages.ODEN_CommonMessages_Exception_KnownDlgTitle, json
										.getString(KNOWN_EXCEPTION));
								OdenActivator.error(CommonMessages.ODEN_CommonMessages_Exception_KnownExceptionLog, new Throwable(json.getString(KNOWN_EXCEPTION)));
								
							} catch (JSONException e) {
								OdenActivator.error(CommonMessages.ODEN_CommonMessages_Exception_JSONExceptionLog, e);
							}
						}
					});
					throw new OdenException(json.getString(KNOWN_EXCEPTION));	
				} else if (json.has(UNKNOWN_EXCEPTION)) {
					
					Display.getDefault().asyncExec(new Runnable(){
						public void run() {
							try {
								MessageDialog.openError(Display.getDefault()
										.getActiveShell(), CommonMessages.ODEN_CommonMessages_Exception_UnknownDlgTitle , CommonMessages.ODEN_CommonMessages_Exception_UnknownDlgText);
								OdenActivator.error(CommonMessages.ODEN_CommonMessages_Exception_UnknownExceptionLog, new Throwable(json.getString(UNKNOWN_EXCEPTION)));
							} catch (JSONException e) {
								OdenActivator.error(CommonMessages.ODEN_CommonMessages_Exception_JSONExceptionLog, e);
							}
						}
					});
					throw new OdenException(json.getString(UNKNOWN_EXCEPTION));
				} else {
					// this is not a kind of exception
				}
			}
		} catch (JSONException jexc) {
			throw new IOException(jexc.getMessage());
		}
	}
	private String readAll(BufferedReader reader) throws IOException {
		StringBuffer buf = new StringBuffer();
		String line = null;
		while ((line = reader.readLine()) != null) {
			buf.append(line + "\n"); //$NON-NLS-1$
		}
		return buf.toString();
	}

	private URLConnection init(String url) throws MalformedURLException,
			IOException {
		URLConnection con = new URL(url).openConnection();
		con.setUseCaches(false);
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setConnectTimeout(TIMEOUT);
		con.setRequestProperty("Accept-Charset","utf-8");
		
		return con;
	}

}
