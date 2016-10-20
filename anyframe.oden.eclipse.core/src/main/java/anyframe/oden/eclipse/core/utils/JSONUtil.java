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
package anyframe.oden.eclipse.core.utils;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSONUtil class
 * parsing array to string
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC2
 * 
 */
public class JSONUtil {
	public final static String KNOWN_EXCEPTION = "KnownException";	
	public final static String UNKNOWN_EXCEPTION = "UnknownException";

	private static String jsonizedException(String key, Exception e) {
		JSONArray jarr = new JSONArray();
		try {
			jarr.put(new JSONObject().put(key, e.getMessage()) );
		} catch (JSONException e1) {
			try {
				jarr.put(new JSONObject().put(KNOWN_EXCEPTION, 
						JSONException.class.toString()) );
			} catch (JSONException e2) {
			}
		}
		return jarr.toString();
	}

	public static String jsonizedUnknowException(Exception e) {
		return jsonizedException(UNKNOWN_EXCEPTION, e);
	}

	public static String jsonizedKnownException(Exception e){
		return jsonizedException(KNOWN_EXCEPTION, e);
	}

	public static String jsonizedException(Exception e) {

		return jsonizedUnknowException(e);
	}

	public static String toString(JSONArray jArr) {
		StringBuffer buf = new StringBuffer();
		try{
			for(int i=0; i<jArr.length(); i++){
				Object o = jArr.get(i);
				if(o instanceof JSONArray) {
					buf.append(toStringWithoutNewLine((JSONArray)o));
				}else if(o instanceof JSONObject) {
					buf.append(toString((JSONObject)o));
				}else {
					buf.append(o.toString());
				}
				if(i < jArr.length())
					buf.append("\n");
			}
		}catch(JSONException e) {
			jArr.toString();
		}
		return buf.toString();
	}

	private static String toStringWithoutNewLine(JSONArray jArr) {
		StringBuffer buf = new StringBuffer();
		try{
			for(int i=0; i<jArr.length(); i++){
				Object o = jArr.get(i);
				if(o instanceof JSONArray) {
					buf.append(toStringWithoutNewLine((JSONArray)o));
				}else if(o instanceof JSONObject) {
					buf.append(toString((JSONObject)o));
				}else {
					buf.append(o.toString());
				}
				if(i < jArr.length())
					buf.append(", ");
			}
		}catch(JSONException e) {
			return jArr.toString();
		}
		return buf.toString();
	}

	private static String toString(JSONObject jObj) {
		StringBuffer buf = new StringBuffer();
		try{
			for(Iterator<String> i = jObj.keys();i.hasNext();){
				String key = i.next();
				buf.append(key).append(" = ").append(jObj.get(key));
				if(i.hasNext())
					buf.append(", ");
			}
		}catch(JSONException e){
			return jObj.toString();
		}
		return buf.toString();
	}
}
