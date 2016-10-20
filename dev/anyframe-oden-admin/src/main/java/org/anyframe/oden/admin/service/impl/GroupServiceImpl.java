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
package org.anyframe.oden.admin.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.oden.admin.common.OdenCommonDao;
import org.anyframe.oden.admin.domain.Job;
import org.anyframe.oden.admin.service.GroupService;
import org.anyframe.oden.admin.util.CommandUtil;
import org.anyframe.oden.admin.util.OdenConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * This is GroupServiceImpl Class
 * 
 * @author JungHwan Hong
 */
@Service("groupService")
public class GroupServiceImpl implements GroupService {

	@Inject
	@Named("odenCommonDao")
	OdenCommonDao<Job> odenCommonDao;

	/**
	 * Job Group 가져오기.
	 * 
	 * @throws Exception
	 * 
	 */
	public List<String> findGroup() throws Exception {
		return odenCommonDao.getStringList("job", "group");
	}

	/**
	 * Job Group명으로 ODEN Job 가져오기.
	 * 
	 * @param name
	 * @throws Exception
	 * 
	 */
	public List<String> findByName(String name) throws Exception {
		List<String> retList = new ArrayList<String>();

		String command = CommandUtil.getBasicCommand("job", "group", OdenConstants.DOUBLE_QUOTATOIN + name + OdenConstants.DOUBLE_QUOTATOIN);
		List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

		if (objectArray.size() == 1) { // 둘 이상 될수 없음
			JSONArray jobs = objectArray.get(0).getJSONArray("group");
			for (int i = 0; i < jobs.length(); i++) {
				retList.add(jobs.getString(i));
			}
		}

		return retList;
	}

	/**
	 * Group 이 등록되지 않는 ODEN Job 가져오기.
	 * 
	 * @throws Exception
	 * 
	 */
	public List<String> findByUngroup() throws Exception {
		return odenCommonDao.getStringList("job", "group -ungroup");
	}

	/**
	 * Group, Group이 등록되지 않는 ODEN Job 가져오기.
	 * 
	 * @throws Exception
	 * 
	 */
	public HashMap<String, Object> findGroupAndUngroup() throws Exception {
		HashMap<String, Object> groupUngroup = new HashMap<String, Object>();
		List<String> unGroups = odenCommonDao.getStringList("job", "group -ungroup");
		List<String> groups = odenCommonDao.getStringList("job", "group");

		groupUngroup.put("unGroups", unGroups);
		groupUngroup.put("groups", groups);

		return groupUngroup;
	}

	/**
	 * Group, Build Job 가져오기.
	 * 
	 * @throws Exception
	 * 
	 */
	public HashMap<String, Object> findGroupAndBuildJob() throws Exception {
		HashMap<String, Object> groupBuildJob = new HashMap<String, Object>();
		List<String> buildJobs = odenCommonDao.getStringList("build", "info");
		List<String> groups = odenCommonDao.getStringList("job", "group");

		groupBuildJob.put("buildJobs", buildJobs);
		groupBuildJob.put("groups", groups);

		return groupBuildJob;
	}

	/**
	 * ODEN Job에서 등록된 Group 삭제하기.
	 * 
	 * @param name
	 * @throws Exception
	 * 
	 */
	public void remove(String name) throws Exception {
		odenCommonDao.getResultString("job", "group -del", OdenConstants.DOUBLE_QUOTATOIN + name + OdenConstants.DOUBLE_QUOTATOIN);
	}

	/**
	 * 그룹 생성 후 ODEN Job 매핑하기.
	 * 
	 * @param groupName
	 * @param jobNames
	 * @throws Exception
	 * 
	 */
	public void createGroupByJob(String groupName, String[] checkeds, String[] unCheckeds) throws Exception {

		for (String jobName : checkeds) {
			JSONObject jo = new JSONObject();
			jo.put("name", jobName);
			jo.put("group", groupName);

			String cmd = "_job update" + " " + jo;
			odenCommonDao.update(cmd);
		}
		for (String jobName : unCheckeds) {
			JSONObject jo = new JSONObject();
			jo.put("name", jobName);
			jo.put("group", "");

			String cmd = "_job update" + " " + jo;
			odenCommonDao.update(cmd);
		}
	}
	/**
	 * 중복 Group이름 check.
	 * 
	 * @param groupName
	 * @throws Exception
	 * 
	 */
	public boolean existGroup(String groupName) throws Exception {
		List<String> groupNameList = odenCommonDao.getStringList("job", "group");
		if(groupNameList.contains(groupName)) {
			return true;
		} else {
			return false;
		}
	}
}
