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
package org.anyframe.oden.admin.service;

import java.util.HashMap;
import java.util.List;

import org.anyframe.oden.admin.domain.Job;

import anyframe.common.Page;

/**
 * @version 1.0
 * @created 14-7-2010 占쏙옙占쏙옙 10:13:31
 * @author LEE Sujeong
 */
public interface JobService {

	/**
	 * 
	 * @param param
	 * @throws Exception
	 */
	public Page test(Object page, String param, String opt) throws Exception;

	/**
	 * 
	 * @param param
	 * @throws Exception
	 */
	public String run(String[] param, String opt, String job, Object objPage,
			String cmd, String userid) throws Exception;

	/**
	 * 
	 * @param param
	 * @throws Exception
	 */
	public Page compare(Object objPage, String param, String flag)
			throws Exception;

	public void stop(String param) throws Exception;

	public HashMap compareHeader(String param) throws Exception;

	public Page findList(String param) throws Exception;

	public Job findByName(String param) throws Exception;

	public void update(String[] param, String[] cmd, String[] mappings,
			String jobname, String repository, String excludes)
			throws Exception;
	
	public void insert(String[] param, String[] cmd, String[] mappings,
			String jobname, String repository, String excludes)
			throws Exception;

	public void remove(String name) throws Exception;

	public List<HashMap> excel(String param) throws Exception;

	public Page loadMappings(String param) throws Exception;

	public Page findMappings(String param) throws Exception;
	
	public List<Job> findJob() throws Exception;
	
	public String rollback(String txid) throws Exception;

}