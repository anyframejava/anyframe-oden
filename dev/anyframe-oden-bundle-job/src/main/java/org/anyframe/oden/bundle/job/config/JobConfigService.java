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
package org.anyframe.oden.bundle.job.config;

import java.util.List;

/**
 * This is JobConfigService Interface
 * 
 * @author Junghwan Hong
 */
public interface JobConfigService {
	public void addJob(CfgJob job) throws Exception;
	
	public void updateJobByGroup(CfgJob job) throws Exception;

	public void removeJob(String name) throws Exception;

	public List<String> listJobs() throws Exception;

	public CfgJob getJob(String name) throws Exception;

	public List<String> listGroups() throws Exception;

	public List<String> listUnGroups() throws Exception;

	public List<String> getGroup(String name) throws Exception;

	public void removeGroup(List<String> delNames) throws Exception;
}
