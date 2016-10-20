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
import org.anyframe.oden.admin.domain.History;
import org.anyframe.oden.admin.domain.Job;
import org.anyframe.oden.admin.domain.Log;
import org.anyframe.oden.admin.service.HistoryService;
import org.anyframe.oden.admin.util.CommandUtil;
import org.anyframe.oden.admin.util.CommonUtil;
import org.anyframe.oden.admin.util.OdenConstants;
import org.anyframe.pagination.Page;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * This is HistoryServiceImpl Class
 * 
 * @author Junghwan Hong
 */
@Service("historyService")
public class HistoryServiceImpl implements HistoryService {

	@Value("#{contextProperties['pageUnit'] ?: 30}")
	int pageUnit;

	@Inject
	@Named("odenCommonDao")
	OdenCommonDao<History> odenCommonDao;

	/**
	 * Method for showing history detail information.
	 * 
	 * @param param
	 * @throws Exception
	 */
	public Page show(Object objPage, String param, String opt) throws Exception {
		List<Log> list = new ArrayList<Log>();

		int page = Integer.parseInt(String.valueOf(objPage + ""));
		int totalNum = 0;

		String txid = "";
		String option = "";

		if (param.indexOf("(") != -1) {
			int startTxid = param.indexOf("(");
			int endTxid = param.indexOf(")");
			txid = param.substring(startTxid + 1, endTxid);
		} else {
			txid = param;
		}
		if (param.indexOf("-path") != -1) {
			option = option.concat(param.substring(param.indexOf("-path"), param.length()));
		}

		if (opt == null || opt.equals("")) {
			option = option.concat(" ");
		} else {
			option = option.concat(opt + " ");
		}

		if (page == 0) {
			option = option.concat("-page" + " " + page);
		} else {
			option = option.concat("-page" + " " + (page - 1));
		}

		String command = CommandUtil.getBasicCommand("log", "show", txid + " " + option);
		List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

		for (JSONObject object : objectArray) {
			totalNum = Integer.parseInt(object.getString("total"));
			JSONArray data = (JSONArray) object.get("data");
			if (!(data.length() == 0)) {
				for (int j = 0; j < data.length(); j++) {
					JSONObject dataObj = (JSONObject) data.get(j);
					int no = 1;
					if (page == 0) {
						no = pageUnit * (page) + j + 1;
					} else {
						no = pageUnit * (page - 1) + j + 1;
					}
					String success = dataObj.getString("success");

					String successResult = "";
					if (success.equalsIgnoreCase("true")) {
						successResult = OdenConstants.IMG_TAG_SUCCESS;
					} else {
						successResult = OdenConstants.IMG_TAG_FAIL;
					}

					JSONArray targets = (JSONArray) dataObj.get("targets");
					String target = "";
					if (!(targets.length() == 0)) {
						for (int n = 0; n < targets.length(); n++) {
							target = target.concat("[" + targets.get(n) + "] ");
						}
						target = target.substring(0, target.length() - 1);
					}

					String path = dataObj.getString("path");
					String mode = dataObj.getString("mode");
					if ("A".equals(mode)) {
						mode = "Add";
					} else if ("U".equals(mode)) {
						mode = "Update";
					} else {
						mode = "Delete";
					}

					String errorlog = dataObj.getString("errorlog");

					Log l = new Log();
					l.setNo(no + "");
					l.setSuccess(successResult);
					l.setJob(target);
					l.setPath(path);
					l.setMode(mode);
					l.setErrorlog(errorlog);

					list.add(l);
				}
			}
		}
		return new Page(list, page, totalNum, pageUnit, pageUnit);
	}

	/**
	 * Method for history list.
	 * 
	 * @throws Exception
	 */
	public Page findByPk(Object objPage, String param) throws Exception {
		List<Log> list = new ArrayList<Log>();

		int page = Integer.parseInt(objPage + "");
		int totalNum = 0;
		String option = "";

		if (page == 0) {
			option = option.concat("-page" + " " + page);
		} else {
			option = option.concat("-page" + " " + (page - 1));
		}

		String command = CommandUtil.getBasicCommand("log", "search", param + " " + option);
		List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

		for (JSONObject object : objectArray) {
			totalNum = Integer.parseInt(object.getString("total"));
			JSONArray data = (JSONArray) object.get("data");
			if (!(data.length() == 0)) {
				for (int j = 0; j < data.length(); j++) {
					JSONObject dataObj = (JSONObject) data.get(j);
					Log log = JsonConverter.jsonToLog(dataObj);

					String txidAndStatus = "";
					String txid = log.getTxid();
					if (log.getStatus().equalsIgnoreCase("S")) {
						txidAndStatus = OdenConstants.IMG_TAG_SUCCESS + "(" + OdenConstants.A_HREF_HEAD
								+ "javascript:fn_addTab('04History', 'Historydetail', 'historydetail', " + txid + ", $('#itemname').val());"
								+ OdenConstants.A_HREF_MID + txid + OdenConstants.A_HREF_TAIL + ")";
					} else if (log.getStatus().equalsIgnoreCase("F")) {
						txidAndStatus = OdenConstants.IMG_TAG_FAIL + "(" + OdenConstants.A_HREF_HEAD
								+ "javascript:fn_addTab('04History', 'Historydetail', 'historydetail', " + txid + ", $('#itemname').val());"
								+ OdenConstants.A_HREF_MID + txid + OdenConstants.A_HREF_TAIL + ")";
					}
					log.setTxid(txidAndStatus);
					list.add(log);
				}
			}
		}

		return new Page(list, page, totalNum, pageUnit, pageUnit);

	}

	/**
	 * Method for job list when init dropdown menu.
	 * 
	 * @throws Exception
	 */
	public List<Job> findJob(String role) throws Exception {
		return odenCommonDao.findJob("job", "info", CommonUtil.getRoleList(role));
	}

	/**
	 * 
	 * @param param
	 * @throws Exception
	 */
	public String undo(String param) throws Exception {
		return odenCommonDao.getResultString("history", "undo", param);
	}

	/**
	 * 
	 * @param param
	 * @throws Exception
	 */
	public String redeploy(String param) throws Exception {
		return odenCommonDao.getResultString("history", "redeploy", param);
	}

	/**
	 * Methode for downloading excel file with history list.
	 * 
	 * @param param
	 * @throws Exception
	 */
	public List<Log> findByPkExcel(String param) throws Exception {
		String command = CommandUtil.getBasicCommand("log", "search", param);
		List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

		List<Log> list = new ArrayList<Log>();

		for (JSONObject object : objectArray) {
			JSONArray data = (JSONArray) object.get("data");
			if (!(data.length() == 0)) {
				for (int j = 0; j < data.length(); j++) {
					JSONObject dataObj = (JSONObject) data.get(j);
					Log log = JsonConverter.jsonToLog(dataObj);
					list.add(log);
				}
			}
		}
		return list;
	}

	/**
	 * Methode for downloading excel file with history detail information of
	 * certain transaction.
	 * 
	 * @param param
	 * @throws Exception
	 */
	public List<Log> showExcel(String param, String opt) throws Exception {
		String txid = "";

		if (param.indexOf("(") != -1) {
			int startTxid = param.indexOf("(");
			int endTxid = param.indexOf(")");
			txid = param.substring(startTxid + 1, endTxid);
		} else {
			txid = param;
		}

		String command = CommandUtil.getBasicCommand("log", "show", txid + " " + opt);
		List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

		List<Log> list = new ArrayList<Log>();

		for (JSONObject object : objectArray) {
			JSONArray data = (JSONArray) object.get("data");
			if (!(data.length() == 0)) {
				for (int j = 0; j < data.length(); j++) {
					JSONObject dataObj = (JSONObject) data.get(j);
					String success = dataObj.get("success").equals("true") ? "Success" : "Fail";

					JSONArray targets = (JSONArray) dataObj.get("targets");
					String target = "";
					if (!(targets.length() == 0)) {
						for (int n = 0; n < targets.length(); n++) {
							target = target.concat("[" + targets.get(n) + "] ");
						}
						target = target.substring(0, target.length() - 1);
					}

					String path = dataObj.getString("path");
					String mode = dataObj.getString("mode");
					if ("A".equals(mode)) {
						mode = "Add";
					} else if ("U".equals(mode)) {
						mode = "Update";
					} else {
						mode = "Delete";
					}

					String errorlog = dataObj.getString("errorlog");

					Log l = new Log();
					l.setNo(j + 1 + "");
					l.setSuccess(success);
					l.setJob(target);
					l.setPath(path);
					l.setMode(mode);
					l.setErrorlog(errorlog);

					list.add(l);
				}
			}
		}
		return list;
	}
}