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
package org.anyframe.oden.bundle.common;

import org.osgi.service.log.LogService;

/**
 * To contact the OSGi LogService, this class will be used. This class can convert
 * Throwable instance to the String having its Stack Trace.
 * 
 * @author Junghwan Hong
 */
public class Logger {
	
	private static LogService log;
	
	protected void setlog(LogService log){
		Logger.log = log;
	}	
	
	/**
	 * log msg via OSGi LogService
	 * 
	 * @param level
	 * @param msg
	 */
	public static void log(int level, String msg){
		if(log == null) {
			System.out.println(msg);
		} else {
			log.log(level, msg);
		}
	}
	
	/**
	 * log msg & exception via OSGi LogService
	 * 
	 * @param level
	 * @param msg
	 * @param e
	 */
	public static void log(int level, String msg, Exception e){
		if(log == null) {
			System.out.println(msg + "\n\t" + stackTrace(e));
		} else {
			log.log(level, msg + "\n\t" + stackTrace(e));
		}
	}
	
	/**
	 * log exception via OSGi LogService
	 * 
	 * @param e
	 */
	public static void error(Exception e){
		log(LogService.LOG_ERROR, e.getMessage(), e);
	}
	
	public static void debug(String msg){
		log(LogService.LOG_DEBUG, msg);
	}
	
	public static void info(String msg){
		log(LogService.LOG_INFO, msg);
	}
	
	private static String stackTrace(Throwable t) {
		if(t == null) { 
			return "";
		}
		
		String s = t.getClass().getName();
		String msg = t.getLocalizedMessage();
		
		String bufString = msg != null ? s + ": " + msg : s;
		StringBuffer buf = new StringBuffer(bufString);
		buf.append('\n');
		for(StackTraceElement trace : t.getStackTrace()){
			buf.append("\tat " + trace + "\n");
		}
		
		if(t.getCause() != null) {
			buf.append(stackTrace(t.getCause()));
		}
		
		return buf.toString();
	}
}
