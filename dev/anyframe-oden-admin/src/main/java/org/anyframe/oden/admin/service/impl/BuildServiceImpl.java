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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.oden.admin.common.OdenCommonDao;
import org.anyframe.oden.admin.convert.JsonConverter;
import org.anyframe.oden.admin.domain.BuildHistory;
import org.anyframe.oden.admin.domain.BuildRun;
import org.anyframe.oden.admin.domain.Job;
import org.anyframe.oden.admin.service.BuildService;
import org.anyframe.oden.admin.util.CommandUtil;
import org.anyframe.oden.admin.util.OdenConstants;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * This is BuildServiceImpl Class
 * 
 * @author JungHwan Hong
 */
@Service("buildService")
public class BuildServiceImpl implements BuildService {

	@Inject
	@Named("odenCommonDao")
	OdenCommonDao<Job> odenCommonDao;

	/**
	 * Build Job 가져오기.
	 * 
	 * @throws Exception
	 * 
	 */
	public List<String> findBuild() throws Exception {
		return odenCommonDao.getStringList("build", "info");
	}

	/**
	 * Build Job 구동하기.
	 * 
	 * @throws Exception
	 * 
	 */
	public BuildRun runBuild(String buildName) throws Exception {
		BuildRun run = new BuildRun();

		String command = CommandUtil.getBasicCommand("build", "run", OdenConstants.DOUBLE_QUOTATOIN + buildName + OdenConstants.DOUBLE_QUOTATOIN);
		List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

		for (JSONObject object : objectArray) {
			run = JsonConverter.jsonToBuildRun(object);
		}
		return run;
	}

	/**
	 * Build Job(성공여부) 갱신하기.
	 * 
	 * @param jobName
	 *            (Jenkins Job 이름)
	 * @param buildNo
	 *            (Build Job 구동 후 받은 buildNo)
	 * @param success
	 *            (성공은 "true")
	 * @throws Exception
	 * 
	 */
	public void updateBuild(String jobName, String buildNo, String success) throws Exception {
		JSONObject jo = new JSONObject();

		jo.put("job", jobName);
		jo.put("buildno", buildNo);
		jo.put("success", success);

		String cmd = "_build add" + " " + jo;

		odenCommonDao.update(cmd);
	}

	/**
	 * 해당 Build Job의 최신 이력 조회.
	 * 
	 * @param jobName
	 *            (빌드 job 이름)
	 * @throws Exception
	 * 
	 */
	public BuildHistory findByName(String jobName) throws Exception {
		String command = CommandUtil.getBasicCommand("build", "log", OdenConstants.DOUBLE_QUOTATOIN + jobName + OdenConstants.DOUBLE_QUOTATOIN);
		List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

		BuildHistory history = new BuildHistory();
		if (objectArray.size() == 1) { // 둘 이상 될수 없음
			history = JsonConverter.jsonToBuildHistory(objectArray.get(0));
		}
		return history;
	}

}
