package org.anyframe.oden.bundle.job.log;

import java.io.File;

import org.anyframe.oden.bundle.common.BundleUtil;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;

public class BDBConnection {
	Environment env;
	boolean readonly;
	
	public BDBConnection(boolean readonly){
		this("meta", readonly);
	}
	
	public BDBConnection(String loc, boolean readonly){
		this.readonly = readonly;
		env = getEnvironment(loc, readonly);
	}
	
	private Environment getEnvironment(String loc, boolean readonly){
		EnvironmentConfig envCfg = new EnvironmentConfig();
		envCfg.setReadOnly(readonly);
		envCfg.setAllowCreate(!readonly);
		envCfg.setTransactional(!readonly);
		return new Environment(
				new File(BundleUtil.odenHome(), loc), envCfg);
	}
	
	public Database openDB(String name){
		return env.openDatabase(null, name, getDBConfig(readonly));	
	}
	
	private DatabaseConfig getDBConfig(boolean readonly){
		DatabaseConfig dbcfg = new DatabaseConfig();
		dbcfg.setReadOnly(readonly);
		dbcfg.setAllowCreate(!readonly);
		dbcfg.setTransactional(!readonly);
		return dbcfg;
	}
	
	public void close(){
		try{ if(env != null) env.close(); }catch(Exception e){}
	}
	
	public Transaction beginTransaction(Transaction parent, 
			TransactionConfig txConfig){
		return env.beginTransaction(parent, txConfig);
	}
}
