/*
 * Copyright 2010 SAMSUNG SDS Co., Ltd. All rights reserved.
 *
 * No part of this "source code" may be reproduced, stored in a retrieval
 * system, or transmitted, in any form or by any means, mechanical,
 * electronic, photocopying, recording, or otherwise, without prior written
 * permission of SAMSUNG SDS Co., Ltd., with the following exceptions:
 * Any person is hereby authorized to store "source code" on a single
 * computer for personal use only and to print copies of "source code"
 * for personal use provided that the "source code" contains SAMSUNG SDS's
 * copyright notice.
 *
 * No licenses, express or implied, are granted with respect to any of
 * the technology described in this "source code". SAMSUNG SDS retains all
 * intellectual property rights associated with the technology described
 * in this "source code".
 *
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