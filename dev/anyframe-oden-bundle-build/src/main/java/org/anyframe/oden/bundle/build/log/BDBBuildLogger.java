/*
 * Copyright 2002-2014 the original author or authors.
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
package org.anyframe.oden.bundle.build.log;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.anyframe.oden.bundle.build.config.BrecordElement;
import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.common.StringUtil;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

/**
 * This is BDBBuildLogger Class
 * 
 * @author Junghwan Hong
 */
@SuppressWarnings("PMD")
public class BDBBuildLogger implements BuildLogService {

	private final String BUILD_DB = "RecordBuildDB";

	private int LOG_DURATION = 365;

	private BundleContext context;

	private Object latch = new Object();

	protected void activate(ComponentContext context) {
		this.context = context.getBundleContext();

		String dur = this.context.getProperty("log.duration");
		LOG_DURATION = getDuration(dur);

		BDBConnection conn = null;
		Database db1 = null;

		synchronized (latch) {
			try {
				conn = new BDBConnection(false);
				db1 = conn.openDB(BUILD_DB);

			} catch (Exception e) {
				Logger.error(e);
			} finally {
				try {
					if (db1 != null)
						db1.close();
				} catch (Exception e) {
				}

				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
				}
			}
		}
	}

	private int getDuration(String dur) {
		if (dur == null)
			return LOG_DURATION;
		try {
			return Integer.valueOf(dur);
		} catch (NumberFormatException e) {
			return LOG_DURATION;
		}
	}

	public BrecordElement search(String jobName) throws OdenException {
		BDBConnection conn = null;
		Database buildDb = null;
		Cursor cur = null;
		BrecordElement element = new BrecordElement();
		
		synchronized (latch) {
			try {
				conn = new BDBConnection(true);
				buildDb = conn.openDB(BUILD_DB);

				DatabaseEntry data = new DatabaseEntry();
				DatabaseEntry key = new DatabaseEntry();

				if (!StringUtil.empty(jobName)) {
					BRecordElementBinding binding = new BRecordElementBinding(
							jobName);

					cur = buildDb.openCursor(null, null);

					if (cur.getLast(key, data, LockMode.DEFAULT) != OperationStatus.SUCCESS)
						throw new OdenException("Fail to get: " + BUILD_DB);
					do {
						Object o = binding.entryToObject(data);
						if (o == null)
							continue;
						return (BrecordElement) o;

					} while (cur.getPrev(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS);
				}
				return element;
			} catch (Exception e) {
				throw new OdenException(e);
			} finally {
				try {
					if (cur != null)
						cur.close();
				} catch (Exception e) {
				}
				try {
					if (buildDb != null)
						buildDb.close();
				} catch (Exception e) {
				}
				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public void record(BrecordElement record) throws OdenException {
		BDBConnection conn = null;
		Transaction tx = null;
		Database buildDb = null;

		synchronized (latch) {
			try {
				conn = new BDBConnection(false);
				tx = conn.beginTransaction(null, null);
				buildDb = conn.openDB(BUILD_DB);

				DatabaseEntry key = new DatabaseEntry(record.getId().getBytes(
						"utf-8"));
				DatabaseEntry infoData = new DatabaseEntry();

				BRecordElementBinding binding = new BRecordElementBinding();
				binding.objectToEntry(record, infoData);
				buildDb.put(tx, key, infoData);

				tx.commit();
			} catch (Exception e) {
				if (tx != null)
					tx.abort();
				throw new OdenException(e);
			} finally {
				try {
					if (buildDb != null)
						buildDb.close();
				} catch (Exception e) {
				}

				try {
					if (conn != null)
						conn.close();
				} catch (Exception e) {
				}
			}
		}

	}

}
