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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;

/**
 * Write OSGi log to file. Each day diffrent log files are used.
 * 
 * @author joon1k
 *
 */
public class LogWriteListener implements LogListener {
	private final static String FILE_NAME_DATE_PATTERN = "yyyyMMdd";
	private final static String LOG_DATE_PATTERN = "yyyy.MM.dd HH:mm:ss";
	
	private BundleContext context;
	
	protected void activate(ComponentContext context){
		this.context = context.getBundleContext();
	}
	
	protected void setLogReader(LogReaderService logreader) {
		logreader.addLogListener(this);
	}
	
	/**
	 * some messages are logged via LogService, this will be called.
	 */
	public void logged(LogEntry entry) {
		
		// check to write or not with log.level
		final int level = entry.getLevel();
		if(Integer.valueOf(context.getProperty("felix.log.level")) < level )
			return;
		
		final long date = entry.getTime();
		
		final Bundle bnd = entry.getBundle();
		
		final String who = bnd.getSymbolicName();
		
		final String msg = entry.getMessage();

		try {
			writeToCacheLog(format(who, level, date, msg != null ? msg : entry.getException().getMessage()), date);
			System.out.println(stackTrace(entry.getException()));
		} catch (IOException e) {
			System.err.println(format(this.getClass().getName(), 1, 
					System.currentTimeMillis(), "Fail to write logs." + e.getMessage() ));;
		}
	}
	
	private static String stackTrace(Throwable t) {
		if(t == null) return "";
		
		String s = t.getClass().getName();
		String msg = t.getLocalizedMessage();
		
		StringBuffer buf = new StringBuffer(msg != null ? s + ": " + msg : s);
		buf.append('\n');
		for(StackTraceElement trace : t.getStackTrace()){
			buf.append("\tat " + trace + "\n");
		}
		
		if(t.getCause() != null)
			buf.append(stackTrace(t.getCause()));
		
		return buf.toString();
	}

	private String format(String who, int level, long date, String msg) {
		return "!ENTRY " + toLogLevelString(level) + " " + toStringDate(date) + " " + who + "\n" +
				"!MESSAGE " + msg + "\n";
	}
	
	private void writeToCacheLog(String s, long date) throws IOException{
		final String cache = context.getProperty("felix.cache.rootdir");
		final String fname = "log_" + onlyDate(date) + ".log";
		File logf = new File(cache, fname);
		File parent = logf.getParentFile();
		if(!parent.exists())
			parent.mkdirs();
		
		writeToFile(logf, s);	
	}

	private String onlyDate(long date) {
		return new SimpleDateFormat(FILE_NAME_DATE_PATTERN).format(date);
	}
	
	public static String toStringDate(long date) {
		return new SimpleDateFormat(LOG_DATE_PATTERN).format(new Date(date));
	}

	private void writeToFile(File file, String s) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(file, true));
			pw.println(s);
		} catch (IOException e) {
		}finally{
			if(pw != null) pw.close();			
		}
	}
	
	protected String toLogLevelString(int level){
		switch(level){
		case 4:
			return "LOG_DEBUG";
		case 1:
			return "LOG_ERROR";
		case 3:
			return "LOG_INFO";
		case 2:
			return "LOG_WARNING";
		default:
			return "LOG_OTHER";	
		}
	}
	
}
