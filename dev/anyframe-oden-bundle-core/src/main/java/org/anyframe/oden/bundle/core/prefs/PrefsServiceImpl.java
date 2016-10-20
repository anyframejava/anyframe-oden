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

import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;

/**
 * @see anyframe.oden.bundle.core.prefs.PrefsService
 * 
 * @author joon1k
 *
 */
public class PrefsServiceImpl implements PrefsService{
	private PreferencesService prefsvc;
	
	protected void setPrefsService(PreferencesService prefsvc) {
		this.prefsvc = prefsvc;
	}
	
	public Prefs getPrefs(String name) {
		Preferences prefs = prefsvc.getUserPreferences(name).node("/");
		return new PrefsImpl(prefs, name);
	}
}
