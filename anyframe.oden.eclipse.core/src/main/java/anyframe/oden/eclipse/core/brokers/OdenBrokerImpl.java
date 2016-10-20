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
package anyframe.oden.eclipse.core.brokers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.CommandNotFoundException;
import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.utils.CommonUtil;


/**
 * This class provides some methods to interface server 
 * 
 * @author HONG JungHwan
 *
 */
public class OdenBrokerImpl implements OdenBrokerService {
	private final int TIMEOUT = 15000; // millis
	public final String KNOWN_EXCEPTION = "KnownException";
	public final String UNKNOWN_EXCEPTION = "UnknownException";
	protected CommonUtil util;
	/**
	 * Send request and get response, Interface server with http protocol
	 */
	public String sendRequest(String shellUrl, String msg) throws CommandNotFoundException, OdenException {
		PrintWriter writer = null;
		String result = null;
		
		if(shellUrl != null && ! (shellUrl.equals(""))){
			try {
				
				URLConnection conn = init(shellUrl);
				
				// send
				writer = new PrintWriter(conn.getOutputStream());
				writer.println(msg);
				writer.flush();
				
				// receive
				result = handleResult(conn);
			} catch (CommandNotFoundException e){
				throw new CommandNotFoundException(e.getMessage());	
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

	private String handleResult(URLConnection conn) throws CommandNotFoundException, OdenException, IOException {
		String result = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(conn
					.getInputStream(),"utf-8"));

			result = readAll(reader);

			// check if shell exception or unknown exception
			JSONArray array = new JSONArray(result);
			determineException(array);

		}catch (JSONException e){
			if(result.contains("Command not found")){
				throw new CommandNotFoundException(e.getMessage());
			}else{
				throw new OdenException(e.getMessage());
			}
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
		
		Server server = getServer(url);
		
		con.setUseCaches(false);
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setConnectTimeout(TIMEOUT);
		con.setRequestProperty("Accept-Charset","utf-8");
		
		// Base 64 encoding using encode method
		if(server != null)
			con.addRequestProperty("Authorization", "Basic "
				+ encode(server.getUser(), server.getPassword()));
		
		return con;
	}

	private Server getServer(String url) {
		Collection<Server> servers = OdenActivator.getDefault().getAliasManager().getServerManager().getServers();
		
		for(Server server : servers) {
			String parseUrl = CommonUtil.replaceIgnoreCase(url, CommonMessages.ODEN_CommonMessages_ProtocolString_HTTP, "");
			parseUrl = CommonUtil.replaceIgnoreCase(parseUrl, CommonMessages.ODEN_CommonMessages_ProtocolString_HTTPsuf, "");
			
			if(server.getUrl().equals(parseUrl))
				return server;
		}
		
		return null;
	}
	private static String encode(String id, String pwd) {
		try {
			return new String(
					Base64.encodeBase64((id + ":" + pwd).getBytes()), "ASCII");
			
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}
}
