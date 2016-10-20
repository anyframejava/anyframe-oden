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

import org.anyframe.oden.admin.common.CommonUtil;
import org.anyframe.oden.admin.common.OdenCommonDao;
import org.anyframe.oden.admin.domain.History;
import org.anyframe.oden.admin.domain.Job;
import org.anyframe.oden.admin.domain.Log;
import org.anyframe.oden.admin.service.HistoryService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import anyframe.common.Page;


/**
 * @version 1.0
 * @created 14-7-2010 ���� 10:13:32
 * @author HONG JungHwan
 */
@Service("historyService")

public class HistoryServiceImpl implements HistoryService {
	private OdenCommonDao<History> odenCommonDao = new OdenCommonDao<History>();
	
	@Value("#{contextProperties['pageUnit'] ?: 30}")
	int pageUnit;
	
	private String ahref_pre = "<a href=\"";
	private String ahref_mid = "\">";
	private String ahref_post = "</a>";
	
	/**
	 * Method for showing history detail information.
	 * 
	 * @param param
	 * @throws Exception
	 */
	public Page show(Object objPage, String param, String opt) throws Exception {
		// return odenCommonDao.getList("log", "show", param);
		ArrayList list = new ArrayList();
		int page = Integer.parseInt(objPage + "");
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
		if (param.indexOf("-path") != -1)
			option += param.substring(param.indexOf("-path"), param.length());

		if (opt == null || opt.equals("")) {
			option += " ";
		} else {
			option += opt + " ";
		}

		if (page == 0) {
			option += "-page" + " " + page;
		} else {
			option += "-page" + " " + (page - 1);
		}

		String result = odenCommonDao.getResultString("log", "show", txid + " "
				+ option);

		String imgSuccess = "<img src='images/accept.png' style='vertical-align:middle;'/>";
		String imgFail = "<img src='images/exclamation.png' style='vertical-align:middle;'/>";
		
		if (!(result == null) && !result.equals("")) {
			JSONArray array = new JSONArray(result);
			if (!(array.length() == 0)) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = (JSONObject) array.get(i);
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
								successResult = imgSuccess;
							} else{
								successResult = imgFail;
							}
							
							JSONArray targets = (JSONArray) dataObj.get("targets");
							String target = "";
							if (!(targets.length() == 0)) {
								for (int n = 0; n < targets.length(); n++) {
									target += "[" + targets.get(n) + "] ";
								}
								target = target.substring(0, target.length() - 1);
							}
							
//							String target = ((JSONObject) dataObj.get("agent"))
//									.getString("name");
							String path = dataObj.getString("path");
							String mode = dataObj.getString("mode");
							if (mode.equals("A"))
								mode = "Add";
							else if (mode.equals("U"))
								mode = "Update";
							else
								mode = "Delete";

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
			}
		}
		return new Page(list, page, totalNum, pageUnit, pageUnit);
	}

	/**
	 * Method for history list.
	 * 
	 * @throws Exception
	 */
	public Page findByPk(Object objPage, String param ) throws Exception {
		ArrayList list = new ArrayList();
		
		int page = Integer.parseInt(objPage + "");
		int totalNum = 0;
		String option = "";

		if (page == 0) {
			option += "-page" + " " + page;
		} else {
			option += "-page" + " " + (page - 1);
		}
		
		String result = odenCommonDao.getResultString("log", "search", param
				+ " " + option);

		String imgSuccess = "<img src='images/accept.png' style='vertical-align:middle;'/>";
		String imgFail = "<img src='images/exclamation.png' style='vertical-align:middle;'/>";

		if (!(result == null) && !result.equals("")) {
			JSONArray array = new JSONArray(result);
			if (!(array.length() == 0)) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = (JSONObject) array.get(i);
					totalNum = Integer.parseInt(object.getString("total"));
					JSONArray data = (JSONArray) object.get("data");
					if (!(data.length() == 0)) {
						for (int j = 0; j < data.length(); j++) {
							JSONObject dataObj = (JSONObject) data.get(j);
							
							
							int total = dataObj.getInt("total");
							int nsuccess = dataObj.getInt("nsuccess");
							String status = dataObj.getString("status");
							String txid = dataObj.getString("txid");
							String job = dataObj.getString("job");
							String date = dataObj.getString("date");
							String user = dataObj.getString("user");

							String txidAndStatus = "";
							if (status.equalsIgnoreCase("S")) {
								txidAndStatus = imgSuccess + "(" + ahref_pre
								+ "javascript:fn_addTab('04History', 'Historydetail', 'historydetail', "+txid+", $('#itemname').val());" + ahref_mid + txid + ahref_post + ")";
							} else if (status.equalsIgnoreCase("F")) {
								txidAndStatus = imgFail + "(" + ahref_pre
								+ "javascript:fn_addTab('04History', 'Historydetail', 'historydetail', "+txid+", $('#itemname').val());" + ahref_mid + txid + ahref_post + ")";
							}

							Log l = new Log();
							l.setTxid(txidAndStatus);
							l.setStatus(status);
							l.setDate(date);
							l.setJob(job);
							l.setCounts(nsuccess + "/" + total);
							l.setUser(user);

							list.add(l);
						}
					}
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
		return odenCommonDao.findJob("job", "info",CommonUtil.getRoleList(role));
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
		String result = odenCommonDao.getResultString("log", "search", param);

		List<Log> list = new ArrayList<Log>();

		if (!(result == null) && !result.equals("")) {
			JSONArray array = new JSONArray(result);
			if (!(array.length() == 0)) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = (JSONObject) array.get(i);
					JSONArray data = (JSONArray) object.get("data");
					if (!(data.length() == 0)) {
						for (int j = 0; j < data.length(); j++) {
							JSONObject dataObj = (JSONObject) data.get(j);

							int total = dataObj.getInt("total");
							int nsuccess = dataObj.getInt("nsuccess");
							String status = dataObj.getString("status");
							String txid = dataObj.getString("txid");
							String job = dataObj.getString("job");
							String date = dataObj.getString("date");
							String user = dataObj.getString("user");

							Log l = new Log();
							l.setTxid(txid);
							l.setStatus(status);
							l.setDate(date);
							l.setJob(job);
							l.setCounts(nsuccess + "/" + total);
							l.setUser(user);

							list.add(l);
						}
					}
				}
			}
		}

		return list;
	}

	/**
	 * Methode for downloading excel file with history detail information of certain transaction.
	 * 
	 * @param param
	 * @throws Exception
	 */
	public List<Log> showExcel(String param , String opt) throws Exception {
		String txid = "";

		if (param.indexOf("(") != -1) {
			int startTxid = param.indexOf("(");
			int endTxid = param.indexOf(")");
			txid = param.substring(startTxid + 1, endTxid);
		} else {
			txid = param;
		}
		
		String result = odenCommonDao.getResultString("log", "show", txid + " " + opt);

		List<Log> list = new ArrayList<Log>();
		
		if (!(result == null) && !result.equals("")) {
			JSONArray array = new JSONArray(result);
			if (!(array.length() == 0)) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = (JSONObject) array.get(i);
					JSONArray data = (JSONArray) object.get("data");
					if (!(data.length() == 0)) {
						for (int j = 0; j < data.length(); j++) {
							JSONObject dataObj = (JSONObject) data.get(j);
							String success = dataObj.get("success").equals(
									"true") ? "Success" : "Fail";

							JSONArray targets = (JSONArray) dataObj.get("targets");
							String target = "";
							if (!(targets.length() == 0)) {
								for (int n = 0; n < targets.length(); n++) {
									target += "[" + targets.get(n) + "] ";
								}
								target = target.substring(0, target.length() - 1);
							}
							
							String path = dataObj.getString("path");
							String mode = dataObj.getString("mode");
							if (mode.equals("A"))
								mode = "Add";
							else if (mode.equals("U"))
								mode = "Update";
							else
								mode = "Delete";

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
			}
		}
		return list;
	}
}