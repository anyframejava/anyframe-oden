/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package anyframe.oden.bundle.core.record;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.OdenStoreException;

/**
 * @see anyframe.oden.bundle.core.record.DeployLogService
 * 
 * @author joon1k
 *
 */
public class DeployLogImpl implements DeployLogService {
	private static final String START_TIME = "00";		// 00h 
	private static final String END_TIME = "23";			// 23h 
	
	private static final String LOG_FILE_PREFIX = "history_";
	private static final String LOG_FILE_EXT = ".log";
	
	private final static String FILE_NAME_DATE_PATTERN = "yyyyMMdd";
	
	private BundleContext context;
	
	private Object latch = new Object();
	
	protected void activate(ComponentContext context){
		this.context = context.getBundleContext();
	}

	public void record(String host, String agent, String agentRootPath,
			List<String> paths) throws OdenException {
		record(new RecordElement(host, agent, agentRootPath, paths, 
				System.currentTimeMillis()));		
	}

	public void record(RecordElement log) throws OdenException {
		File historyf = deployLogFile(log.getDate());
		File parent = historyf.getParentFile();
		if(!parent.exists())
			parent.mkdirs();
		
		synchronized (latch) {	
			writeObject(historyf, log);	
		}
	}
	
	private File deployLogFile(long date) {
		final String cache = context.getProperty("felix.cache.rootdir");
		final String fname = LOG_FILE_PREFIX + onlyDateForFile(date) + LOG_FILE_EXT;
		return new File(cache, fname);
	}
	
	private void writeObject(File historyf, RecordElement log) throws OdenException {
		FileObjectOutputStream fout = null;
		try{
			fout = new FileObjectOutputStream(historyf, true);
			fout.writeObject(log);
		}catch(IOException e) {
			throw new OdenException("Couldn't write a deploy log to a file: " + 
					historyf.getAbsolutePath());
		}finally {
			try { if(fout != null) fout.close(); } catch (IOException e) { }
		}
	}

	private String onlyDateForFile(long date) {
		return new SimpleDateFormat(FILE_NAME_DATE_PATTERN).format(date);
	}
	
	public List<RecordElement> search(String host, String agent, String path,
			String startdate, String enddate, String status) throws OdenException{
		
		// if there's no valid args, return latest log only.
		if(empty(host) && empty(agent) && empty(path) && empty(startdate) && empty(status)){
			List<RecordElement> records = new ArrayList<RecordElement>();
			RecordElement latest = latestRecord();
			if(latest != null)
				records.add(latest);
			return records;
		}
		
		// or convert date
		try{
			long start = empty(startdate) ? Long.MIN_VALUE : longTime(startdate + START_TIME);
			long end = empty(enddate) ? Long.MAX_VALUE : longTime(enddate + END_TIME);
			
			List<RecordElement> records = getRecords(start, end);
			
			if(!empty(host))
				records = searchByHost(host, records);
			
			if(!empty(agent))
				records = searchByAgent(agent, records);
			
			if(!empty(path))
				records = searchByRootpath(path, records);
			
			// TODO: search by status...
			
			return records;
		}catch(ParseException e){
			throw new OdenException("Fail to convert to date: " + startdate + " & " + enddate);
		}
	}
	
	private long longDate(String s) throws ParseException {
		return new SimpleDateFormat(FILE_NAME_DATE_PATTERN).parse(s).getTime();
	}
	
	private long longTime(String s) throws ParseException {
		return new SimpleDateFormat("yyyyMMddHH").parse(s).getTime();
	}
	
	private boolean empty(String s) {
		return s == null || s.length() == 0;
	}
	
	private File latestFile(File[] logfiles) {
		long latestdate = Long.MIN_VALUE;
		File latestf = null;
		for(File logfile : logfiles){
			try {
				long date = getDateFromFile(logfile.getName());
				if(date > latestdate){
					latestdate = date;
					latestf = logfile;
				}
			} catch (ParseException e) {
				// ignore this file
			}
		}
		return latestf;
	}

	private RecordElement latestRecord() throws OdenException {
		final String cache = context.getProperty("felix.cache.rootdir");
		File parent = new File(cache);
		File[] logfiles = parent.listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.startsWith(LOG_FILE_PREFIX) && name.endsWith(LOG_FILE_EXT);
			}
		});
		
		File latestf = latestFile(logfiles);
		if(latestf.exists())
			synchronized (latch) {
				return latestRecord(latestf);	
			}
		else
			return null;
	}
	
	private RecordElement latestRecord(File logfile) throws OdenException {
		FileObjectInputStream fin = null;
		RecordElement latest = null;
		try{
			fin = new FileObjectInputStream(logfile);
			
			Object o = null;
			while(fin.available() > 0)
				o = fin.readObject();
			
			if(o != null)
				latest = (RecordElement)o;
		}catch(ClassNotFoundException e){
			e.printStackTrace();
			throw new OdenException("Illegal format: " + logfile.getAbsolutePath());
		}catch(IOException e) {
			throw new OdenStoreException(logfile.getAbsolutePath());
		}finally{
			try { if(fin != null) fin.close(); } catch (IOException e) { }
		}
		return latest;
	}

	private List<RecordElement> getRecords(long startdate, long enddate) throws OdenException {
		final String cache = context.getProperty("felix.cache.rootdir");
		File parent = new File(cache);
		File[] logfiles = parent.listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.startsWith(LOG_FILE_PREFIX) && name.endsWith(LOG_FILE_EXT);
			}
		});
		
		List<RecordElement> records = new ArrayList<RecordElement>();
		File latest = latestFile(logfiles);
		for(File logfile : logfiles){
			if(acceptedFile(logfile, startdate, enddate)){
				if(latest != null && logfile.getName().equals(latest.getName())) {
					synchronized (latch) {
						collectRecords(logfile, records);	
					}
				} else {
					collectRecords(logfile, records);
				}
			}
		}
		return records;
	}
	
	private void collectRecords(File logfile, List<RecordElement> records) throws OdenException {
		FileObjectInputStream fin = null;
		try{
			fin = new FileObjectInputStream(logfile);
			while(fin.available() > 0)
				records.add((RecordElement)fin.readObject());
			
		}catch(ClassNotFoundException e){
			throw new OdenException("Illegal format: " + logfile.getAbsolutePath());
		}catch(IOException e) {
			throw new OdenStoreException(logfile.getAbsolutePath());
		}finally{
			try { if(fin != null) fin.close(); } catch (IOException e) { }
		}
	}

	private boolean acceptedFile(File logfile, long startdate, long enddate) {
		try {
			long date = getDateFromFile(logfile.getName());
			return date >= startdate && date <= enddate;
		} catch (ParseException e) {
		}
		return false;
	}

	private long getDateFromFile(String fname) throws ParseException {
		String sdate = fname.substring(LOG_FILE_PREFIX.length(), 
				fname.length() - LOG_FILE_EXT.length());
		return longDate(sdate);
	}
	
	private List<RecordElement> searchByHost(String host, List<RecordElement> records) {
		List<RecordElement> result = new ArrayList<RecordElement>();
		for(RecordElement record : records){
			if(record.getHost().startsWith(host))
				result.add(record);
		}
		return result;
	}

	private List<RecordElement> searchByAgent(String agent, List<RecordElement> records) {
		List<RecordElement> result = new ArrayList<RecordElement>();
		for(RecordElement record : records){
			if(record.getAgent().startsWith(agent))
				result.add(record);
		}
		return result;
	}

	private List<RecordElement> searchByRootpath(String path, List<RecordElement> records) {
		List<RecordElement> result = new ArrayList<RecordElement>();
		for(RecordElement record : records){
			String root = record.getRootpath();
			if(root.contains(path))
				result.add(record);
			else {
				RecordElement refined = refineMatchedPathOnly(record, path);
				if(refined != null)
					result.add(refined);
			}
		}
		return result;
	}
	
	private RecordElement refineMatchedPathOnly(RecordElement src, String path){
		List<String> matched = new ArrayList<String>();
		for(String srcPath : src.getPaths()){
			if(srcPath.contains(path))
				matched.add(srcPath);
		}
		
		if(matched.size() > 0)
			return new RecordElement(src.getHost(), src.getAgent(), 
					src.getRootpath(), matched, src.getDate());
		
		return null;
	}
		
}
