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
package anyframe.oden.bundle.prefs;

import anyframe.oden.bundle.common.OdenStoreException;

/**
 * This has similar interface to java.util.Properties. But contents
 * are saved by OSGi Preferences Service.
 * 
 * @author Joonil Kim
 *
 */
public interface Prefs {
	public void put(String key, String value) throws OdenStoreException;
	
	public String get(String key);
	
	public String[] keys() throws OdenStoreException;
	
	public void remove(String key) throws OdenStoreException;
	
	public void clear() throws OdenStoreException;
}
