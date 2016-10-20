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
package org.anyframe.oden.bundle.core.job;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.anyframe.oden.bundle.common.Logger;
import org.osgi.service.component.ComponentContext;

/**
 * This is JobManagerImpl class.
 * 
 * @author Junghwan Hong
 * @see anyframe.oden.bundle.core.job.JobManager
 */
@SuppressWarnings("PMD")
public class JobManagerImpl implements JobManager {

	private List<Job> jobQueue = new Vector<Job>();

	private Thread worker = null;
	
	private int threadNum = 0; 

	protected void activate(ComponentContext context) {
		threadNum = Integer.valueOf(context.getBundleContext().getProperty(
				"deploy.threadcnt")==null ? "0" : context.getBundleContext().getProperty(
						"deploy.threadcnt"));
		
		if (worker != null)
			return;
		worker = new Thread() {
			public void run() {
				try {
					while (true) {
						Job j = null;
						synchronized (jobQueue) {
							while (jobQueue.isEmpty())
								jobQueue.wait();
							j = jobQueue.get(0);
						}
						if (j != null) {
							try {
								j.start();
							} catch (RuntimeException re) {
							}
							try {
								j.dispose();
							} catch (RuntimeException re) {
							}
							jobQueue.remove(j);
						}
					}
				} catch (InterruptedException e) {
					Logger.error(e);
				}

			}
		};
		worker.start();
	}

	public Job[] jobs() {
		synchronized (jobQueue) {
			return jobQueue.toArray(new Job[jobQueue.size()]);
		}
	}

	public Job job(String id) {
		synchronized (jobQueue) {
			for (Job j : jobQueue) {
				if (j.id().equals(id)) {
					return j;
				}
			}
			return null;
		}
	}

	public Job job() {
		synchronized (jobQueue) {
			return jobQueue.get(0);
		}
	}

	// invoked by Job
	public void cancel(final Job j) {
		if (j.status() == Job.RUNNING) {
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
			jobQueue.notifyAll();
		}
	}

	public void syncRun(Job j) {
		schedule(j);
		try {
			synchronized (j) {
				while (j.status() != Job.NONE)
					j.wait();
			}
		} catch (InterruptedException e) {
		}
	}

	public LinkedList<String> batchRun(LinkedList<Job> deployJobs) {
		if(threadNum ==0)
			threadNum = Runtime.getRuntime().availableProcessors() / 2;
		

		// m-thread Job processing implements
		LinkedList<String> rtnList = new LinkedList<String>();
		ExecutorService pool = Executors.newFixedThreadPool(threadNum);

		List<Future<Job>> futures = null;
		
		
		int iNum = deployJobs.size() <= threadNum ? 1 : (int) Math
				.ceil((double) deployJobs.size() / threadNum);
		int jNum = deployJobs.size() <= threadNum ? deployJobs.size() : threadNum;

		for (int i = 0; i < iNum; i++) {
			Collection<Callable<Job>> list = new LinkedList<Callable<Job>>();
			if( (i == iNum -1 && i != 0) && (deployJobs.size() % threadNum != 0))
				jNum = deployJobs.size() % threadNum;
			
			for (int j = 0; j < jNum; j++) {
				Job job = deployJobs.get(i * threadNum + j);
				jobQueue.add(job);
				list.add(new batchThread(job));
			}
			try {
				futures = pool.invokeAll(list);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			for (Future<Job> f : futures) {
				try {
					jobQueue.remove(f.get());
					rtnList.add(((Job) f.get()).id());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		return rtnList;
	}

}

class batchThread implements Callable<Job> {
	Job j;

	public batchThread() {
	}

	public batchThread(Job j) {
		this.j = j;
	}

	public Job call() throws Exception {
		// TODO Auto-generated method stub
		if (j != null) {
			try {
				j.start();
			} catch (RuntimeException re) {
			}
			try {
				j.dispose();
			} catch (RuntimeException re) {
			}
		}
		return j;
	}

}
