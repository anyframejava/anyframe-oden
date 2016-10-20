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

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.osgi.service.component.ComponentContext;


public class JobManagerImpl implements JobManager{
	private List<Job> jobQueue = new Vector<Job>();
	
	private Object latch = new Object();
	
	private Thread worker = null;
	
	
	protected void activate(ComponentContext context){
		if(worker != null) return;
		worker = new Thread(){
			public void run() {
				while(true){
					if(jobQueue.size() > 0){
						Job j = jobQueue.get(0);
						startJob(j);	
					}else {
						try{
							synchronized (this) { wait(); }
						}catch(InterruptedException e){
						}
					}
				}	
			}
		};
		worker.start();
	}

	public Collection<Job> jobs(){
		return jobQueue;
	}
	
	public Job job(String id){
		for(Job j : jobQueue)
			if(j.id().equals(id))
				return j;
		return null;
	}
	
	// invoked by Job
	public void cancelJob(Job j) {
		if(j.status == Job.RUNNING)
			stopJob(j);
		else		// just remove the job from the queue
			jobQueue.remove(j);
	}
	
	// invoked by Job
	public void schedule(Job j) {
		synchronized (latch) {
			j.setStatus(Job.WAITING);
			jobQueue.add(j);			
		}
		synchronized (worker) {
			worker.notify();	
		}
	}
	
	private void stopJob(Job j) {
		j.stop();
	}
	
	private void startJob(Job j) {
		j.setStatus(Job.RUNNING);
		j.start();	
		j.done(j.exception());
		jobQueue.remove(j);
	}
}
