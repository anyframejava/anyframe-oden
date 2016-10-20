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
package org.anyframe.oden.bundle.hessiansvr;

import java.util.HashMap;
import java.util.Map;

import org.anyframe.oden.bundle.deploy.TimeChecker;

/**
 * 
 * @see anyframe.oden.bundle.deploy.TimeChecker
 * 
 * @author joon1k
 *
 */
public class TimeCheckerImpl implements TimeChecker{
	private Map<String, Long> m = new HashMap<String, Long>();
	
	private Map<String, Long> totals = new HashMap<String, Long>();
	
	public void tick(String s){
		long t = System.currentTimeMillis();
		if(m.containsKey(s)){
			long total = totals.containsKey(s) ? totals.get(s) : 0L;
			totals.put(s, total + ( t - m.remove(s)));
		}else{
			m.put(s, System.currentTimeMillis());
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for(String s : totals.keySet()){
			buf.append(s + ": " + totals.get(s) + "\n");
		}
		m = new HashMap<String, Long>();
		totals = new HashMap<String, Long>();
		return buf.toString();
	}
}
