package org.anyframe.oden.bundle.job.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.common.StringUtil;
import org.anyframe.oden.bundle.core.AgentLoc;
import org.anyframe.oden.bundle.core.DeployFile;
import org.anyframe.oden.bundle.core.DeployFile.Mode;
import org.anyframe.oden.bundle.core.record.RecordElement2;
import org.anyframe.oden.bundle.job.deploy.SlimDeployFile;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

public class BDBJobLogger implements JobLogService {	
	private static final String LOG_FILE_PREFIX = "log_";
	private static final String LOG_FILE_EXT = ".log";
	
	private final static String FILE_NAME_DATE_PATTERN = "yyyyMMdd";
	
	private final String INFO_DB = "RecordInfoDB";
	private final String FILE_DB = "DeployFileListDB";
	
	private int LOG_DURATION = 365;
	
	private BundleContext context;
	
	private Object latch = new Object();
	
	protected void activate(ComponentContext context) {
		this.context = context.getBundleContext();
		
		String dur = this.context.getProperty("log.duration");
		LOG_DURATION = getDuration(dur); 
		
		BDBConnection conn = null;
		Database db1 = null;
		Database db2 = null;
		synchronized (latch) {
			try{
				conn = new BDBConnection(false);
				db1 = conn.openDB(INFO_DB);
				db2 = conn.openDB(FILE_DB);
			}catch(Exception e){
				Logger.error(e);
			}finally{
				try{ if(db1 != null) db1.close(); } catch(Exception e){ }
				try{ if(db2 != null) db2.close(); } catch(Exception e){ }
				try{ if(conn != null) conn.close(); } catch(Exception e){ }	
			}
		}
	}
	
	private int getDuration(String dur) {
		if(dur == null) return LOG_DURATION;
		try{
			return Integer.valueOf(dur);
		}catch(NumberFormatException e){
			return LOG_DURATION;
		}
	}

	/**
	 * date: null if the latest log is requested.
	 */
	public LogError getErrorLog(String date) throws OdenException {	
		if(StringUtil.empty(date))
			return getLogContents(latestFile(logfiles()));
		else
			return getLogContents(getDateFile(logfiles(), date));
		
	}
	
//	private LogError getLogContents0(File f) throws OdenException{
//		RandomAccessFile rf = null;
//		try{
//			rf = new RandomAccessFile(f, "r");
//			rf.
//			String date = f.getName().substring(LOG_FILE_PREFIX.length(), 
//					f.getName().length() - LOG_FILE_EXT.length());
//			return new LogError(date, rf.readUTF());
//		}catch(IOException e){
//			throw new OdenException(e);
//		}finally{
//			try{ if(rf != null)rf.close(); }catch(IOException e){}
//		}
//	}
	
	private LogError getLogContents(File f ) throws OdenException {
		if(f==null) return null;
		
		StringBuffer contents = new StringBuffer();
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			String line = null;
			while( (line = in.readLine()) != null)
				contents.append(line+"\n");
		} catch (Exception e) {
			throw new OdenException(e);
		} finally {
			try{ if(in != null) in.close(); }catch(Exception e){ }
		}
		
		String date = f.getName().substring(LOG_FILE_PREFIX.length(), 
				f.getName().length() - LOG_FILE_EXT.length());
		return new LogError(date,contents.toString());
	}
	
	private File[] logfiles(){
		final String cache = context.getProperty("felix.cache.rootdir");
		File parent = new File(cache);
		return parent.listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.startsWith(LOG_FILE_PREFIX) && name.endsWith(LOG_FILE_EXT);
			}
		});
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
	
	private File getDateFile(File[] logfiles , String date) {
		File f = null;
		for(File logfile: logfiles) {
			try {
				long filedate = getDateFromFile(logfile.getName());
				long tdate = new SimpleDateFormat(FILE_NAME_DATE_PATTERN)
						.parse(date).getTime();
				if(tdate == filedate) {
					f = logfile;
				}
			} catch (ParseException e) {
				// ignore this file
			}
		}
		return f; 
	}
	
	private long getDateFromFile(String fname) throws ParseException {
		String sdate = fname.substring(LOG_FILE_PREFIX.length(), 
				fname.length() - LOG_FILE_EXT.length());
		return new SimpleDateFormat(FILE_NAME_DATE_PATTERN).parse(sdate).getTime();
	}
	
	public List<ShortenRecord> search(String job, String user, String path, 
			boolean isFailOnly) throws OdenException {
		BDBConnection conn = null;
		Database infoDb = null;
		Cursor infoCursor = null;
		synchronized (latch) {
			try{
				conn = new BDBConnection(true);
				infoDb = conn.openDB(INFO_DB);
				
				List<ShortenRecord> ret = new ArrayList<ShortenRecord>(10);
				
				DatabaseEntry key = new DatabaseEntry();
				DatabaseEntry data = new DatabaseEntry();
				RecordInfoBinding recordInfoBinding = new RecordInfoBinding(
						job, user, isFailOnly);
				
				infoCursor = infoDb.openCursor(null, null);
				if(infoCursor.getLast(key, data, LockMode.DEFAULT)
						!= OperationStatus.SUCCESS)
					return ret;						
				do{
					Object o = recordInfoBinding.entryToObject(data);
					if(o == null) continue;
					
					ShortenRecord found = (ShortenRecord) o;
					if(!StringUtil.empty(path) && show(conn, found.getId(), path, 
							Mode.NA, isFailOnly).size() == 0)
						continue;
					ret.add(found);
				}while(infoCursor.getPrev(key, data, LockMode.DEFAULT)
						== OperationStatus.SUCCESS);
				return ret;
			}catch(Exception e){
				throw new OdenException(e);
			}finally{
				try{ if(infoCursor != null) infoCursor.close(); }catch(Exception e){ }
				try{ if(infoDb != null) infoDb.close(); }catch(Exception e){ }
				try{ if(conn != null) conn.close(); }catch(Exception e){ }
			}
		}
	}

	public ShortenRecord search(String id) throws OdenException {
		BDBConnection conn = null;
		Database infoDb = null;
		Cursor cur = null;
		synchronized (latch) {
			try{
				conn = new BDBConnection(true);
				infoDb = conn.openDB(INFO_DB);
				
				DatabaseEntry data = new DatabaseEntry();
				if(StringUtil.empty(id)){
					cur = infoDb.openCursor(null, null);
					if(cur.getLast(new DatabaseEntry(), data, LockMode.DEFAULT)
							!= OperationStatus.SUCCESS)
						throw new OdenException("Fail to get: " + INFO_DB);
				}else {
					if(infoDb.get(null, new DatabaseEntry(id.getBytes("utf-8")), 
							data, LockMode.DEFAULT) != OperationStatus.SUCCESS)
						throw new OdenException("Fail to get: " + INFO_DB);
				}
				
				RecordInfoBinding recordInfoBinding = new RecordInfoBinding();
				Object found = recordInfoBinding.entryToObject(data);
				if(found == null) return null;
				return (ShortenRecord)found; 
			}catch(Exception e){
				throw new OdenException(e);
			}finally{
				try{ if(cur != null) cur.close(); }catch(Exception e){ }
				try{ if(infoDb != null) infoDb.close(); }catch(Exception e){ }
				try{ if(conn != null) conn.close(); }catch(Exception e){ }
			}
		}
	}
	
	public Set<DeployFile> show(String id, String path, Mode mode, 
			boolean isFailOnly) throws OdenException {
		BDBConnection conn = null;
		synchronized (latch) {
			try{
				conn = new BDBConnection(true);
				return show(conn, id, path, mode, isFailOnly);
			}finally{
				try{ if(conn != null) conn.close(); }catch(Exception e){ }
			}
		}
	}
	
	private Set<DeployFile> show(BDBConnection conn, String id, String path, 
			Mode mode, boolean isFailOnly) 
			throws OdenException {
		Database filesDb = null;
		Cursor cur = null;
		try{
			filesDb = conn.openDB(FILE_DB);
			
			DatabaseEntry data = new DatabaseEntry();
			if(StringUtil.empty(id)){
				cur = filesDb.openCursor(null, null);
				if(cur.getLast(new DatabaseEntry(), data, LockMode.DEFAULT)
						!= OperationStatus.SUCCESS)
					throw new OdenException("Fail to get: " + FILE_DB);
			}else {
				if(filesDb.get(null, new DatabaseEntry(id.getBytes("utf-8")), 
						data, LockMode.DEFAULT) != OperationStatus.SUCCESS)
					throw new OdenException("Fail to get: " + FILE_DB);
			}
			
			DeployFileListBinding deployFileListBinding = 
				new DeployFileListBinding(path, mode, isFailOnly);
			
			return (Set<DeployFile>)deployFileListBinding.entryToObject(data);  
		}catch(Exception e){
			throw new OdenException(e);
		}finally{
			try{ if(cur != null) cur.close(); }catch(Exception e){ }
			try{ if(filesDb != null) filesDb.close(); }catch(Exception e){ }
		}
	}

	public void record(RecordElement2 record) throws OdenException{
		BDBConnection conn = null;
		Transaction tx = null;
		Database infoDb = null;
		Database filesDb = null;
		
		synchronized (latch) {
			try{
				conn = new BDBConnection(false);
				tx = conn.beginTransaction(null, null);
				infoDb = conn.openDB(INFO_DB);
				filesDb = conn.openDB(FILE_DB);
	
				DatabaseEntry key = new DatabaseEntry(record.id().getBytes("utf-8"));
				DatabaseEntry infoData = new DatabaseEntry();
				RecordInfoBinding recordInfoBinding = new RecordInfoBinding();
				recordInfoBinding.objectToEntry(record, infoData);
				infoDb.put(tx, key, infoData);
				
				DatabaseEntry filesData = new DatabaseEntry();
				DeployFileListBinding deployFileListBinding = new DeployFileListBinding();
				deployFileListBinding.objectToEntry(record.getDeployFiles(), filesData);
				filesDb.put(tx, key, filesData);
				
				removeOld(tx, infoDb, recordInfoBinding, filesDb);
				tx.commit();
			}catch(Exception e){
				if(tx != null) tx.abort();
				throw new OdenException(e);
			}finally{
				try{ if(filesDb != null) filesDb.close(); }catch(Exception e){ }
				try{ if(infoDb != null) infoDb.close(); }catch(Exception e){ }
				try{ if(conn != null) conn.close(); }catch(Exception e){ }
			}
		}
	}
	
	
	public void record(String id, String user, long time, String desc, 
			int nSuccess, String error, 
			Collection<SlimDeployFile> fs) throws OdenException{
		BDBConnection conn = null;
		Transaction tx = null;
		Database infoDb = null;
		Database filesDb = null;
		
		synchronized (latch) {
			try{
				conn = new BDBConnection(false);
				tx = conn.beginTransaction(null, null);
				infoDb = conn.openDB(INFO_DB);
				filesDb = conn.openDB(FILE_DB);
	
				DatabaseEntry key = new DatabaseEntry(id.getBytes("utf-8"));
				DatabaseEntry infoData = new DatabaseEntry();
				RecordInfoBinding recordInfoBinding = new RecordInfoBinding();
				recordInfoBinding.objectToEntry(new RecordElement2(
						id, new FooCollection<DeployFile>(fs.size()), 
						nSuccess, user, time, error, desc), infoData);
				infoDb.put(tx, key, infoData);
				
				DatabaseEntry filesData = new DatabaseEntry();
				DeployFileListBinding deployFileListBinding = new DeployFileListBinding();
				deployFileListBinding.objectToEntry(fs, filesData);
				filesDb.put(tx, key, filesData);
				
				removeOld(tx, infoDb, recordInfoBinding, filesDb);
				tx.commit();
			}catch(Exception e){
				if(tx != null) tx.abort();
				throw new OdenException(e);
			}finally{
				try{ if(filesDb != null) filesDb.close(); }catch(Exception e){ }
				try{ if(infoDb != null) infoDb.close(); }catch(Exception e){ }
				try{ if(conn != null) conn.close(); }catch(Exception e){ }
			}
		}
	}
	

	private void removeOld(Transaction tx, Database infoDb, 
			RecordInfoBinding infoBind, 
			Database filesDb) throws OdenException {
		long minLimit = getMinLimitDate();
		Cursor cur = null;
		try{
			cur = infoDb.openCursor(tx, null);
			DatabaseEntry key = new DatabaseEntry();
			DatabaseEntry data = new DatabaseEntry();
			while(cur.getFirst(key, data, LockMode.DEFAULT)
					== OperationStatus.SUCCESS){
				Object o = infoBind.entryToObject(data);
				if(o == null) continue;
				ShortenRecord found = (ShortenRecord)o;
				
				if(found.getDate() > minLimit) break;
				cur.delete();		
				filesDb.delete(tx, key);
			}
		}finally{
			if(cur != null) cur.close(); 
		}
		
	}

	private long getMinLimitDate() {
		GregorianCalendar _cal = new GregorianCalendar();
		GregorianCalendar cal = new GregorianCalendar(
				_cal.get(Calendar.YEAR), _cal.get(Calendar.MONTH), 
				_cal.get(Calendar.DAY_OF_MONTH));
		try{
			cal.add(GregorianCalendar.DATE, -LOG_DURATION);
		}catch(Exception e){
			cal.add(GregorianCalendar.DATE, -365);
		}
		return cal.getTimeInMillis();
	}
	
	class FooCollection<T> implements Collection<T>{
		int sz=0;
		public FooCollection(int sz){
			this.sz = sz;
		}
		public boolean add(T o) {
			return false;
		}
		public boolean addAll(Collection<? extends T> c) {
			return false;
		}
		public void clear() {
		}
		public boolean contains(Object o) {
			return false;
		}
		public boolean containsAll(Collection<?> c) {
			return false;
		}
		public boolean isEmpty() {
			return false;
		}
		public Iterator<T> iterator() {
			return null;
		}
		public boolean remove(Object o) {
			return false;
		}
		public boolean removeAll(Collection<?> c) {
			return false;
		}
		public boolean retainAll(Collection<?> c) {
			return false;
		}
		public int size() {
			return sz;
		}
		public Object[] toArray() {
			return null;
		}
		public <T> T[] toArray(T[] a) {
			return null;
		}
	}
}
