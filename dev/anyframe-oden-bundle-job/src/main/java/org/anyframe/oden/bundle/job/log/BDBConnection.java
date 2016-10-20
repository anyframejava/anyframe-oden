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
package org.anyframe.oden.bundle.job.log;

import java.io.File;

import org.anyframe.oden.bundle.common.BundleUtil;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;

/**
 * This is BDBConnection Class
 * 
 * @author Junghwan Hong
 */
public class BDBConnection {
	Environment env;
	boolean readonly;

	public BDBConnection(boolean readonly) {
		this("meta", readonly);
	}

	public BDBConnection(String loc, boolean readonly) {
		this.readonly = readonly;
		env = getEnvironment(loc, readonly);
	}

	private Environment getEnvironment(String loc, boolean readonly) {
		EnvironmentConfig envCfg = new EnvironmentConfig();
		envCfg.setReadOnly(readonly);
		envCfg.setAllowCreate(!readonly);
		envCfg.setTransactional(!readonly);
		return new Environment(new File(BundleUtil.odenHome(), loc), envCfg);
	}

	public Database openDB(String name) {
		return env.openDatabase(null, name, getDBConfig(readonly));
	}

	private DatabaseConfig getDBConfig(boolean readonly) {
		DatabaseConfig dbcfg = new DatabaseConfig();
		dbcfg.setReadOnly(readonly);
		dbcfg.setAllowCreate(!readonly);
		dbcfg.setTransactional(!readonly);
		return dbcfg;
	}

	public void close() {
		try {
			if (env != null) {
				env.close();
			}
		} catch (Exception e) {
		}
	}

	public Transaction beginTransaction(Transaction parent,
			TransactionConfig txConfig) {
		return env.beginTransaction(parent, txConfig);
	}
}
