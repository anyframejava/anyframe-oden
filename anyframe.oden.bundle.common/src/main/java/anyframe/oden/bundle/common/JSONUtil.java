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
package anyframe.oden.bundle.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper class to handling JSON object or converting String to JSON form.
 * 
 * @author joon1k
 *
 */
public class JSONUtil {
	public final static String KNOWN_EXCEPTION = "KnownException";	
	public final static String UNKNOWN_EXCEPTION = "UnknownException";
	
	private static JSONArray toJsonArray(List list) throws JSONException {
		JSONArray ja = new JSONArray();
		for(Object o : list){
			ja.put(jsonize(o));
		}
		return ja;
	}
	
	private static JSONObject toJsonObject(Map map) throws JSONException {
		JSONObject jo = new JSONObject();
		for(Object k : map.keySet()){
			jo.put(jsonize(k).toString(), jsonize(map.get(k)));
		}
		
		return jo;
	}
	
	public static Object jsonize(Object o) throws JSONException{
		if(o == null)
			throw new JSONException("Couldn't jsonize the object: " + o);
		if(o instanceof List){
			return toJsonArray((List)o);
		}else if(o instanceof Map){
			return toJsonObject((Map)o);
		}else if(o instanceof JSONizable){
			return ((JSONizable) o).jsonize();
		}
		return o.toString();
	}
	
	public static JSONArray jsonArray(Object m) throws JSONException {
		List<Object> l = new ArrayList<Object>();
		l.add(m);
		return (JSONArray) JSONUtil.jsonize(l);
	}
	
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
		if(e instanceof OdenException){
			return jsonizedKnownException(e);
		}
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
