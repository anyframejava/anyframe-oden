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
package org.anyframe.oden.bundle.core.job;

import org.osgi.framework.BundleContext;

/**
 * 
 * This represents job's information.
 * 
 * @author joon1k
 *
 */
public abstract class Job {
	public static final int NONE = 0;
	public static final int SLEEPING = 0x01;
	public static final int WAITING = 0x02;
	public static final int RUNNING = 0x04;
	
	protected String id;
	protected int status = WAITING;
	protected boolean stop = false;
	protected long date;
	protected String desc = "";
	protected String currentWork = "";
	protected BundleContext context = null;
	
	protected int additionalWorks = 1; 
	protected int finishedWorks = 0;
	protected int totalWorks;
	
	public Job(BundleContext context, String desc){
		date = System.currentTimeMillis();
		id = String.valueOf(date);
		this.context = context;
		this.desc = desc;
	}
	
	public String id() {
		return id;
	}
	
	public int status() {
		return status;
	}

	public long date() {
		return date;
	}
	
	public String desc() {
		return desc;
	}
	
	public String getCurrentWork(){
		return currentWork;
	}
		
	public int progress(){
		return Math.round((float)(finishedWorks*100) / totalWorks);
	}
	
	public int todoWorks() {
		return totalWorks;
	}
	
	void stop() {
		cancel();
	}
	
	protected void cancel() {
		this.stop = true;
	}
	
	protected boolean isStopped() {
		return stop;
	}

	void start() {
		status = RUNNING;
		totalWorks = 1 + additionalWorks;
		run();
		finishedWorks = totalWorks-1;
	}
	
	void dispose() {
		done();
		finishedWorks = totalWorks;
		status = NONE;
		synchronized (this) {
			notifyAll();
		}
	}
	
	/**
	 * If you want to make this job stop available, check stop in this method.
	 */
	protected abstract void run();
	
	protected abstract void done();

}
