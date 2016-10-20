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
package org.anyframe.oden.bundle.core.prefs;

import org.anyframe.oden.bundle.common.OdenStoreException;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @see anyframe.oden.bundle.core.prefs.Prefs
 * 
 * @author Junghwan Hong
 */
public class PrefsImpl implements Prefs {
	private String name;

	private Preferences prefs;

	PrefsImpl(Preferences prefs, String name) {
		this.prefs = prefs;
		this.name = name;
	}

	public synchronized void put(String key, String value) throws OdenStoreException {
		prefs.put(key, value);
		flush();
	}

	public synchronized String get(String key) {
		return prefs.get(key, "");
	}

	public synchronized String[] keys() throws OdenStoreException {
		String[] keys = null;
		try {
			keys = prefs.keys();
		} catch (BackingStoreException e) {
			throw new OdenStoreException(name);
		}
		return keys == null ? new String[0] : keys;
	}

	public synchronized void remove(String key) throws OdenStoreException {
		prefs.remove(key);
		flush();
	}

	private synchronized void flush() throws OdenStoreException {
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			throw new OdenStoreException(name);
		}
	}

	public synchronized void clear() throws OdenStoreException {
		try {
			prefs.clear();
		} catch (BackingStoreException e) {
			throw new OdenStoreException(name);
		}
	}
}
