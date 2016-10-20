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

import org.anyframe.oden.admin.common.OdenCommonDao;
import org.anyframe.oden.admin.domain.Server;
import org.anyframe.oden.admin.domain.Target;
import org.anyframe.oden.admin.service.ServerService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import anyframe.common.Page;

/**
 * @version 1.0
 * @created 14-7-2010 ���� 10:13:27
 * @author LEE Sujeong
 */
@Service("serverService")
public class ServerServiceImpl implements ServerService {
	private OdenCommonDao<Server> odenCommonDao = new OdenCommonDao<Server>();

	private String ahref_pre = "<a href=\"";
	private String ahref_mid = "\">";
	private String ahref_post = "</a>";

	private String doubleQuotation = "\"";
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

		if (param.equals("")) {
			List list = new ArrayList();
			return new Page(list, 1, list.size(), 1, 1);
		} else {
			List list = new ArrayList();

			String result = odenCommonDao.getResultString("job", "info",
					doubleQuotation + param + doubleQuotation);

			String imgDel = "<img src='images/ico_del.gif'/>";
			
			String imgStatusGreen = "<img src='images/status_green.png'/>";
			String imgStatusGray = "<img src='images/status_gray.png'/>";
			
			if (!(result == null) && !result.equals("")) {
				JSONArray array = new JSONArray(result);
				if (!(array.length() == 0)) {
					int recordSize = array.length();
					for (int i = 0; i < recordSize; i++) {

						JSONObject object = (JSONObject) array.get(i);
						JSONArray targets = (JSONArray) object.get("targets");

						for (int num = 0; num < targets.length(); num++) {
							Target t = new Target();

							JSONObject target = (JSONObject) targets.get(num);
							String address = target.getString("address");
							String name = target.getString("name");
							String path = target.getString("dir");
							String status = target.getString("status");

							String statusResult = "";
							if(status.equalsIgnoreCase("T")){
								statusResult = imgStatusGreen;
							}else{
								statusResult = imgStatusGray;
							}
							
							t.setName(name);
							t.setUrl(address);
							t.setPath(path);
							t.setStatus(statusResult);
							t.setHidden(ahref_pre + "javascript:delServer('"
									+ name + "');" + ahref_mid + imgDel
									+ ahref_post);
							t.setHiddenname(name);
							
							list.add(t);
						}
					}
				}
			}
			if (list.size() == 0) {
				return new Page(list, 1, list.size(), 1, 1);
			} else {
				return new Page(list, 1, list.size(), list.size(), list.size());
			}
		}
	}

}