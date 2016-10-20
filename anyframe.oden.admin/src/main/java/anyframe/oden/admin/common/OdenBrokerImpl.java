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
package anyframe.oden.admin.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import anyframe.common.exception.BaseException;
import anyframe.core.idgen.impl.Base64;
import anyframe.oden.admin.domain.User;
import anyframe.oden.admin.exception.BrokerException;

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
	private static String userid = "";
	private static String password = "";
	
	/**
	 * Send request and get response, Interface server with http protocol
	 */
	public String sendRequest(String shellUrl, String msg) throws BrokerException {
		PrintWriter writer = null;
		String result = null;
		
		if(shellUrl != null && ! (shellUrl.equals(""))){
			try {
				URLConnection conn = init(shellUrl);
				
				// send
				writer = new PrintWriter(conn.getOutputStream());
				writer.println(URLEncoder.encode(msg, "utf-8"));
				
				writer.flush();
				
				// receive
				result = handleResult(conn);
			} catch (BrokerException e) {
					throw new BrokerException(e.getMessage());	
			} catch (ConnectException e) {
				throw new BrokerException("broker.info.notavail");
			} catch (Exception e) {
				throw new BrokerException("broker.exception");
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

	public boolean checkUser(String shellUrl , String userid, String password) throws Exception {
		PrintWriter writer = null;
	    this.userid = userid;
	    this.password = password;
	    
		if (shellUrl != null && !(shellUrl.equals(""))) {
			try {

				URLConnection conn = init(shellUrl);
			
				// send
				writer = new PrintWriter(conn.getOutputStream());
				writer.println("help");
				writer.flush();

				// receive

				BufferedReader reader = null;

				reader = new BufferedReader(new InputStreamReader(conn
						.getInputStream(), "utf-8"));
				
				String result = readAll(reader);
				
			} catch (ConnectException e) {
				throw new BrokerException("broker.info.notavail");
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
			reader = new BufferedReader(new InputStreamReader(conn
					.getInputStream(),"utf-8"));

			result = readAll(reader);
			// check if shell exception or unknown exception
			JSONArray array = new JSONArray(result);
			determineException(array);

		}catch (JSONException e){
			if(result.contains("Command not found")){
				throw new BrokerException("broker.exception.notcommand");
			}else{
				//throw new BrokerException("broker.info.notdata");
			}
		} catch (BrokerException e) {
			throw new BrokerException(e.getMessage());
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
	 * @throws BrokerException
	 */
	private void determineException(JSONArray ja) throws BrokerException,
			IOException {
		
		if (ja == null || ja.length() != 1) {
			return;
		}

		try {
			Object obj = ja.get(0);
			if (obj instanceof JSONObject) {
				final JSONObject json = (JSONObject) obj;
				if (json.has(KNOWN_EXCEPTION)) {
					//TODO known
					//throw new BrokerException(json.getString(KNOWN_EXCEPTION));
					//throw new BrokerException("broker.info.notdata");
				} else if (json.has(UNKNOWN_EXCEPTION)) {
					//TODO unknown
					throw new BrokerException(json.getString(UNKNOWN_EXCEPTION));
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
		User user;
		URLConnection con = new URL(url).openConnection();
		
		con.setUseCaches(false);
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setConnectTimeout(TIMEOUT);
		
		// 우선 Oden Server 계정 상수로 세팅
		if(url != null)
			con.addRequestProperty("Authorization", "Basic "
				+ encode("oden", "oden0"));
		
		return con;
	}
	
	private static String encode(String id, String pwd) {
		return Base64.encode((id + ":" + pwd).getBytes());
	}

}
