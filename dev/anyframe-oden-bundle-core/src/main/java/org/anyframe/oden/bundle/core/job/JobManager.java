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

import java.util.LinkedList;

/**
 * This class managing jobs in queue.
 * 
 * @author Junghwan Hong
 */
public interface JobManager {
	/**
	 * get the job list which are registered here
	 * 
	 * @return
	 */
	public Job[] jobs();

	/**
	 * get the job which are matched with the id.
	 * 
	 * @param id
	 * @return
	 */
	public Job job(String id);

	public Job job();

	/**
	 * canceling the job which are running or waiting
	 * 
	 * @param j
	 */
	public void cancel(Job j);

	/**
	 * scheduling the job
	 * 
	 * @param j
	 */
	public void schedule(Job j);

	/**
	 * scheduling the job & waiting to finish
	 * 
	 * @param j
	 */
	public void syncRun(Job j);
	
	public LinkedList<String> batchRun(LinkedList<Job> deployJobs);
}
