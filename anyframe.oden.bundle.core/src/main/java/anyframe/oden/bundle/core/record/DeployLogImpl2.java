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
package anyframe.oden.bundle.core.record;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import anyframe.oden.bundle.common.ArraySet;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.OdenStoreException;
import anyframe.oden.bundle.core.DeployFile;

/**
 * @see anyframe.oden.bundle.core.record.DeployLogService
 * 
 * @author joon1k
 *
 */
public class DeployLogImpl2 implements DeployLogService2 {
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

	public void record(RecordElement2 r) throws OdenException {
		File f = getHistoryFile(r.getDate());
		File parent = f.getParentFile();
		if(!parent.exists())
			parent.mkdirs();
		synchronized (latch) {
			writeObject(f, r);	
		}
	}

	private File getHistoryFile(long date) {
		final String cache = context.getProperty("felix.cache.rootdir");
		final String fname = LOG_FILE_PREFIX + onlyDateForFile(date) + LOG_FILE_EXT;
		return new File(cache, fname);
	}
	
	private void writeObject(File f, RecordElement2 r) throws OdenException {
		FileObjectOutputStream fout = null;
		try{
			synchronized (latch) {
				fout = new FileObjectOutputStream(f, true);
				fout.writeObject(r);	
			}
		}catch(IOException e) {
			throw new OdenException("Couldn't write a deploy log to a file: " + 
					f.getAbsolutePath());
		}finally {
			try { if(fout != null) fout.close(); } catch (IOException e) { }
		}
	}

	private String onlyDateForFile(long date) {
		return new SimpleDateFormat(FILE_NAME_DATE_PATTERN).format(date);
	}
	
	public List<RecordElement2> search(String txid, String user, String agent, String path,
			String startdate, String enddate, boolean failonly) throws OdenException{
		List<RecordElement2> result = new ArrayList<RecordElement2>();
		try{
			if(!empty(txid)){		// retrieve data by transaction id
				RecordElement2 r = getRecord(txid);
				if(r == null)
					throw new OdenException("Couldn't find that transaction log: " + txid);
				result.add(r);
			}else if(!empty(startdate)){		// retrieve data by date
				long start = empty(startdate) ? Long.MIN_VALUE : longTime(startdate + START_TIME);
				long end = empty(enddate) ? Long.MAX_VALUE : longTime(enddate + END_TIME);			
				result = getRecords(start, end);
			}else {		// retrieve latest data
				RecordElement2 r = latestRecord(failonly);
				if(r != null)
					result.add(r);
			}
			
			if(!empty(user))
				result = searchByUser(user, result);
			
			if(!empty(agent))
				result = searchByAgent(agent, result);
			
			if(!empty(path))
				result = searchByPath(path, result);
			
			if(failonly)
				result = refineFailonly(result);
			
			return result;
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

	private File[] logfiles(){
		final String cache = context.getProperty("felix.cache.rootdir");
		File parent = new File(cache);
		synchronized (latch) {
			return parent.listFiles(new FilenameFilter(){
				public boolean accept(File dir, String name) {
					return name.startsWith(LOG_FILE_PREFIX) && name.endsWith(LOG_FILE_EXT);
				}
			});
		}
	}

	private List<Long> _recordedDateList() {
		List<Long> dates= new ArrayList<Long>();
		for(File log : logfiles()){
			try{
				long d = getDateFromFile(log.getName());
				dates.add(d);
			}catch(ParseException e) {
				// ignore
			}
		}
		Collections.sort(dates);
		return dates;
	}
	
	public List<String> recordedDateList() {
		List<String> dates = new ArrayList<String>();
		for(long date : _recordedDateList()){
			dates.add(onlyDateForFile(date));
		}
		return dates;
	}
	
	private RecordElement2 latestRecord(boolean failonly) throws OdenException {
		File f = latestFile(logfiles());
		if(f != null && f.exists())
			synchronized (latch) {
				return latestRecord(f, failonly);	
			}
		return null;
	}
	
	private RecordElement2 latestRecord(File f, boolean failonly) throws OdenException {
		Object o = null;		
		FileObjectInputStream fin = null;
		try{
			synchronized (latch) {
				fin = new FileObjectInputStream(f);
				while(fin.available() > 0){
					o = fin.readObject();
				}	
			}
		}catch(ClassNotFoundException e){
			throw new OdenException("Illegal format: " + f.getAbsolutePath());
		}catch(IOException e) {
			throw new OdenStoreException(f.getAbsolutePath());
		}finally{
			try { if(fin != null) fin.close(); } catch (IOException e) { }
		}
		if(o == null)
			return null;
		RecordElement2 r = (RecordElement2)o;
		if(failonly)
			refineFailonly(r);
		return r;
	}

	private List<RecordElement2> getRecords(long startdate, long enddate) throws OdenException {		
		List<RecordElement2> records = new ArrayList<RecordElement2>();
		File[] logfiles = logfiles();
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
	
	private File matchedFile(String txid) {
		long txdate = -1;
		try{
			txdate = Long.parseLong(txid);
		}catch(NumberFormatException e){
			return null;
		}
		for(File logf : logfiles()) {
			String fname = logf.getName();
			String logdate = fname.substring(LOG_FILE_PREFIX.length(), 
					fname.length() - LOG_FILE_EXT.length());
			if(onlyDateForFile(txdate).equals(logdate))
				return logf;
		}
		return null;
	}
	
	private RecordElement2 getRecord(String txid) throws OdenException{
		File f = matchedFile(txid);
		if(f == null)
			return null;
		
		FileObjectInputStream fin = null;
		try{
			synchronized (latch) {
				fin = new FileObjectInputStream(f);
				while(fin.available() > 0){
					RecordElement2 r = (RecordElement2)fin.readObject();
					if(txid.equals(r.id()))
						return r;
				}	
			}
		}catch(ClassNotFoundException e){
			throw new OdenException("Illegal format: " + f.getAbsolutePath());
		}catch(IOException e) {
			throw new OdenStoreException(f.getAbsolutePath());
		}finally{
			try { if(fin != null) fin.close(); } catch (IOException e) { }
		}
		return null;
	}
	
	private void collectRecords(File logfile, List<RecordElement2> records) throws OdenException {
		FileObjectInputStream fin = null;
		try{
			synchronized (latch) {
				fin = new FileObjectInputStream(logfile);
				while(fin.available() > 0)
					records.add((RecordElement2)fin.readObject());	
			}
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
	
	private List<RecordElement2> searchByUser(String user, List<RecordElement2> records) {
		List<RecordElement2> result = new ArrayList<RecordElement2>();
		for(RecordElement2 record : records){
			if(record.getUser().startsWith(user))
				result.add(record);
		}
		return result;
	}

	private List<RecordElement2> searchByAgent(String agent, List<RecordElement2> records) {
		List<RecordElement2> result = new ArrayList<RecordElement2>();
		for(RecordElement2 record : records){
			refineMatchedAgentOnly(record, agent);
			if(record.getDeployFiles().size() > 0)
				result.add(record);
		}
		return result;
	}
	
	private void refineMatchedAgentOnly(RecordElement2 r, String agentAddr){
		Set<DeployFile> s = new ArraySet<DeployFile>();
		for(DeployFile f : r.getDeployFiles())
			if(f.getAgent().agentAddr().startsWith(agentAddr))
				s.add(f);
		r.setFiles(s);
	}

	private List<RecordElement2> searchByPath(String fname, List<RecordElement2> records) {
		List<RecordElement2> result = new ArrayList<RecordElement2>();
		for(RecordElement2 record : records){
			refineMatchedPathOnly(record, fname);
			if(record.getDeployFiles().size() > 0)
				result.add(record);
		}
		return result;
	}
	
	private void refineMatchedPathOnly(RecordElement2 r, String fname){
		Set<DeployFile> s = new ArraySet<DeployFile>();
		for(DeployFile f : r.getDeployFiles())
			if(new File(f.getPath()).getName().equals(fname))
				s.add(f);
		r.setFiles(s);
	}
	
	private List<RecordElement2> refineFailonly(List<RecordElement2> records) {
		List<RecordElement2> result = new ArrayList<RecordElement2>();
		for(RecordElement2 record : records){
			refineFailonly(record);
			if(!record.isSuccess())
				result.add(record);
		}
		return result;
	}
	
	private void refineFailonly(RecordElement2 r) {
		Set<DeployFile> s = new ArraySet<DeployFile>();
		for(DeployFile f : r.getDeployFiles())
			if(!f.isSuccess())
				s.add(f);
		r.setFiles(s);
	}
		
}
