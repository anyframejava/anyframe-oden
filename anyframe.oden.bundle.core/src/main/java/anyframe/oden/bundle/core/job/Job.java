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
package anyframe.oden.bundle.core.job;

import org.osgi.framework.BundleContext;

import anyframe.oden.bundle.common.Assert;
import anyframe.oden.bundle.common.BundleUtil;

public abstract class Job {
	public static final int NONE = 0;
	public static final int SLEEPING = 0x01;
	public static final int WAITING = 0x02;
	public static final int RUNNING = 0x04;
	
	protected String id;
	protected int status = NONE;
	protected boolean stop = false;
	protected long date;
	protected String desc = "";
	protected BundleContext context = null;
	protected JobManager manager = null;
	protected Exception exception = null;
	
	public Job(BundleContext context){
		date = System.currentTimeMillis();
		id = String.valueOf(date);
		this.context = context;
		manager = (JobManager) BundleUtil.getService(context, JobManager.class);
		Assert.check(manager != null, "Fail to load " + JobManager.class.getName());
	}
	
	public String id() {
		return id;
	}
	
	public int status() {
		return status;
	}

	void stop() {
		this.stop = true;
	}
	
	Exception exception(){
		return exception;
	}
	
	public long date() {
		return date;
	}
	
	public String desc() {
		return desc;
	}
	
	void setStatus(int status){
		this.status = status;
	}
		
	public void schedule(String desc) {
		this.desc = desc;
		manager.schedule(this);
	}
	
	public void cancel() {
		manager.cancelJob(this);
	}
		
//	void finished() {
//		manager.finished();
//	}
	
	void start() {
		try {
			run();
		} catch (Exception e) {
			exception = e;
		}
	}
	
	/**
	 * If you want to make this job stop available, check stop in this method.
	 */
	protected abstract void run() throws Exception;
	
	protected abstract void done(Exception e);

}
