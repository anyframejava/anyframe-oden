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

import java.util.HashMap;
import java.util.List;

/**
 * This is GroupService Interface
 * 
 * @author JungHwan Hong
 */
public interface GroupService {
	
	/**
	 * Job Group 가져오기.
	 * 
	 * @throws Exception
	 * 
	 */
	public List<String> findGroup() throws Exception;
	
	/**
	 * Job Group명으로 ODEN Job 가져오기.
	 * 
	 * @param name
	 * @throws Exception
	 * 
	 */
	public List<String> findByName(String name) throws Exception;
	
	/**
	 * Group 이 등록되지 않는 ODEN Job 가져오기.
	 * 
	 * @throws Exception
	 * 
	 */
	public List<String> findByUngroup() throws Exception;
	
	/**
	 * Group, Group이 등록되지 않는 ODEN Job 가져오기.
	 * 
	 * @throws Exception
	 * 
	 */
	public HashMap<String, Object> findGroupAndUngroup() throws Exception;
	
	/**
	 * Group, Build Job 가져오기.
	 * 
	 * @throws Exception
	 * 
	 */
	public HashMap<String, Object> findGroupAndBuildJob() throws Exception; 

	/**
	 * ODEN Job에서 등록된 Group 삭제하기.
	 * 
	 * @param name
	 * @throws Exception
	 * 
	 */
	public void remove(String name) throws Exception;
	
	/**
	 * 그룹 생성 후 ODEN Job 매핑하기.
	 * 
	 * @param groupName
	 * @param checkeds
	 * @param unCheckeds
	 * 
	 * @throws Exception
	 * 
	 */
	public void createGroupByJob(String groupName, String[] checkeds, String[] unCheckeds) throws Exception;
	
	/**
	 * 중복 Group이름 check.
	 * 
	 * @param groupName
	 * @throws Exception
	 * 
	 */
	public boolean existGroup(String groupName) throws Exception;
}
