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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.oden.admin.common.OdenCommonDao;
import org.anyframe.oden.admin.convert.JsonConverter;
import org.anyframe.oden.admin.domain.Server;
import org.anyframe.oden.admin.domain.Target;
import org.anyframe.oden.admin.service.ServerService;
import org.anyframe.oden.admin.util.CommandUtil;
import org.anyframe.oden.admin.util.OdenConstants;
import org.anyframe.pagination.Page;
import org.hsqldb.lib.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * This is ServerServiceImpl Class
 * 
 * @author Sujeong Lee
 */
@Service("serverService")
public class ServerServiceImpl implements ServerService {

	@Inject
	@Named("odenCommonDao")
	OdenCommonDao<Server> odenCommonDao;

	/**
	 * 
	 * @param param
	 * @throws Exception
	 */
	public String start(String param) throws Exception {
		return odenCommonDao.getResultString("server", "start", param);
	}

	/**
	 * 
	 * @param param
	 * @throws Exception
	 */
	public String stop(String param) throws Exception {
		return odenCommonDao.getResultString("server", "stop", param);
	}

	/**
	 * 
	 * @param param
	 * @throws Exception
	 */
	public String status(String param) throws Exception {
		return odenCommonDao.getResultString("server", "status", param);
	}

	/**
	 * Method for getting Job mapping info in Job Detail page.
	 * 
	 * @param param
	 */
	public Page findListByPk(String param) throws Exception {

		List<Target> list = new ArrayList<Target>();
		if (!StringUtil.isEmpty(param)) {
			String command = CommandUtil.getBasicCommand("job", "info", OdenConstants.DOUBLE_QUOTATOIN + param + OdenConstants.DOUBLE_QUOTATOIN);
			List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

			for (JSONObject object : objectArray) {
				JSONArray targets = (JSONArray) object.get("targets");

				for (int num = 0; num < targets.length(); num++) {
					JSONObject targetObject = (JSONObject) targets.get(num);
					Target t = JsonConverter.jsonToTarget(targetObject);

					String status = targetObject.getString("status");

					String statusResult = "";
					if (status.equalsIgnoreCase("T")) {
						statusResult = OdenConstants.IMG_TAG_STATUS_GREEN;
					} else {
						statusResult = OdenConstants.IMG_TAG_STATUS_GRAY;
					}
					t.setStatus(statusResult);
					t.setHidden(OdenConstants.A_HREF_HEAD + "javascript:delServer('" + t.getName() + "');" + OdenConstants.A_HREF_MID
							+ OdenConstants.IMG_TAG_DEL + OdenConstants.A_HREF_TAIL);

					list.add(t);
				}
			}
		}

		if (list.isEmpty()) {
			return new Page(list, 1, list.size(), 1, 1);
		} else {
			return new Page(list, 1, list.size(), list.size(), list.size());
		}
	}
	
	/**
	 * Method for getting Job mapping info in Job Detail page.
	 * 
	 * @param param, objectArray
	 */
	public Page findListByPk(String param, List<JSONObject> objectArray) throws Exception {

		List<Target> list = new ArrayList<Target>();
		if (!StringUtil.isEmpty(param)) {
//			String command = CommandUtil.getBasicCommand("job", "info", OdenConstants.DOUBLE_QUOTATOIN + param + OdenConstants.DOUBLE_QUOTATOIN);
//			List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

			for (JSONObject object : objectArray) {
				JSONArray targets = (JSONArray) object.get("targets");

				for (int num = 0; num < targets.length(); num++) {
					JSONObject targetObject = (JSONObject) targets.get(num);
					Target t = JsonConverter.jsonToTarget(targetObject);

					String status = targetObject.getString("status");

					String statusResult = "";
					if (status.equalsIgnoreCase("T")) {
						statusResult = OdenConstants.IMG_TAG_STATUS_GREEN;
					} else {
						statusResult = OdenConstants.IMG_TAG_STATUS_GRAY;
					}
					t.setStatus(statusResult);
					t.setHidden(OdenConstants.A_HREF_HEAD + "javascript:delServer('" + t.getName() + "');" + OdenConstants.A_HREF_MID
							+ OdenConstants.IMG_TAG_DEL + OdenConstants.A_HREF_TAIL);

					list.add(t);
				}
			}
		}

		if (list.isEmpty()) {
			return new Page(list, 1, list.size(), 1, 1);
		} else {
			return new Page(list, 1, list.size(), list.size(), list.size());
		}
	}

}