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
package org.anyframe.oden.admin.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.oden.admin.common.OdenCommonDao;
import org.anyframe.oden.admin.domain.Status;
import org.anyframe.oden.admin.service.StatusService;
import org.anyframe.oden.admin.util.CommandUtil;
import org.anyframe.pagination.Page;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * This is StatusServiceImpl Class
 * 
 * @author Junghwan Hong
 */
@Service("statusService")
public class StatusServiceImpl implements StatusService {

	@Inject
	@Named("odenCommonDao")
	OdenCommonDao<Status> odenCommonDao;

	/**
	 * Method for getting job list in progess.
	 * 
	 * @param domain
	 * @throws Exception
	 */
	public Page findList(String domain) throws Exception {
		return odenCommonDao.getList("status", "info");
	}

	/**
	 * Method for getting job list in progess.
	 * 
	 * @param domain
	 * @throws Exception
	 */
	public boolean checkRunning(String domain) throws Exception {
		Page page = odenCommonDao.getList("status", "info");

		int totalCount = page.getTotal();

		String command = CommandUtil.getBasicCommand("build", "status");
		List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

		for (JSONObject object : objectArray) {
			String status = object.getString("status");
			if (status.equals("B")) {
				page.setTotal(totalCount++);
			}
		}

		if (totalCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Method for stopping job in progess.
	 * 
	 * @param param
	 * @throws Exception
	 */
	public String stop(String param) throws Exception {
		return odenCommonDao.getResultString("status", "stop", param);
	}

}