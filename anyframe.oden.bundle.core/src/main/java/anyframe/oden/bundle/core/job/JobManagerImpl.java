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

import java.util.List;
import java.util.Vector;

import org.osgi.service.component.ComponentContext;

import anyframe.oden.bundle.common.Logger;

/**
 * 
 * @see anyframe.oden.bundle.core.job.JobManager
 * 
 * @author joon1k
 *
 */
public class JobManagerImpl implements JobManager{
	
	private List<Job> jobQueue = new Vector<Job>();
	
	private Thread worker = null;
	
	protected void activate(ComponentContext context){
		if(worker != null) return;
		worker = new Thread(){
			public void run() {
				try{
					while(true){
						Job j = null;
						synchronized (jobQueue) {
							if(jobQueue.isEmpty())
								jobQueue.wait();
							else
								j = jobQueue.get(0); 
						}
						if(j != null){
							System.gc();
							try{ j.start(); } catch (RuntimeException re){}
							try { j.dispose(); } catch (RuntimeException re){}
							jobQueue.remove(j);
							System.gc();		// to prohibit the increase of the jvm's system memory.
						}
					}
				}catch(InterruptedException e){
					Logger.error(e);
				}

			}
		};
		worker.start();
	}

	public Job[] jobs(){
		synchronized (jobQueue) {
			return jobQueue.toArray(new Job[jobQueue.size()]);	
		}
	}
	
	public Job job(String id){
		synchronized (jobQueue) {
			for(Job j : jobQueue)
				if(j.id().equals(id))
					return j;
			return null;
		}
	}
	
	// invoked by Job
	public void cancel(final Job j) {
		if(j.status() == Job.RUNNING){
			j.stop();
		} else {
			synchronized (jobQueue) {
				jobQueue.remove(j);
			}
		}
	}
	
	// invoked by Job
	public void schedule(Job j) {
		synchronized (jobQueue) {
			jobQueue.add(j);		
			jobQueue.notify();
		}
	}
	
	public void syncRun(Job j) {
		schedule(j);
		try {
			synchronized (j) {
				j.wait();
			}
		} catch (InterruptedException e) {
		}
	}
	
}
