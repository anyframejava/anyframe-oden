/*
 * Copyright 2002-2014 the original author or authors.
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

import java.util.List;

import org.anyframe.oden.admin.domain.BuildHistory;
import org.anyframe.oden.admin.domain.BuildRun;

/**
 * This is BuildService Interface
 * 
 * @author JungHwan Hong
 */
public interface BuildService {
	
	/**
	 * Build Job 가져오기.
	 * 
	 * @throws Exception
	 * 
	 */
	public List<String> findBuild() throws Exception;
	
	/**
	 * Build Job 구동하기.
	 * 
	 * @param buildName(Jenkins Job 이름)
	 * @throws Exception
	 * 
	 */
	public BuildRun runBuild(String buildName) throws Exception;
	
	/**
	 * Build Job(성공여부) 갱신하기.
	 * 
	 * @param jobName(Jenkins Job 이름)
	 * @param buildNo(Build Job 구동 후 받은 buildNo)
	 * @param success(성공은 "true")
	 * @throws Exception
	 * 
	 */
	public void updateBuild(String jobName, String buildNo, String success)
			throws Exception;
	
	/**
	 * 해당 Build Job의 최신 이력 조회.
	 * 
	 * @param jobName(빌드 job 이름)
	 * @throws Exception
	 * 
	 */
	public BuildHistory findByName(String jobName) throws Exception;
}
