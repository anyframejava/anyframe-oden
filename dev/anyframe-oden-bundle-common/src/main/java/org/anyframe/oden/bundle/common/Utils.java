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
package org.anyframe.oden.bundle.common;

import java.util.Map;

/**
 * Utility class for Oden
 * 
 * @author joon1k
 *
 */
public class Utils {
	/**
	 * get the root message about the exception
	 * 
	 * @param t
	 * @return
	 */
	public static String rootCause(Throwable t){
		if(t.getCause() != null)
			return rootCause(t.getCause());
		return t.getMessage() == null ? t.toString() : t.getMessage();
	}
	
	/**
	 * get this jvm's memory usage & stack trace 
	 * 
	 * @return
	 */
	public static String jvmStat(){
		StringBuffer buf = new StringBuffer();
//		JavaSysMon monitor = new JavaSysMon();
//		CpuTimes initTimes = monitor.cpuTimes();
//		Thread.sleep(500);
//		buf.append("CPU usage: " + Math.round(monitor.cpuTimes().getCpuUsage(initTimes)*100) + "%\n");

		buf.append("Memory usage: " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024) +"kb\n");
		buf.append("Thread: " + Thread.activeCount() + " threads are running.\n");
		Map<Thread, StackTraceElement[]> ttraces = Thread.getAllStackTraces();
		for(Thread t : ttraces.keySet()){
			buf.append("#" + t.getId() + "\n");
			for(StackTraceElement ele : ttraces.get(t)){
				buf.append(ele + "\n");
			}
		}
		return buf.toString();
	}
	
	public static int hashCode(Object... args){
		int hash = 17;
		for(Object o : args){
			hash = 37*hash + o.hashCode();
		}
		return hash;
	}
}
