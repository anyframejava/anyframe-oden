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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.oden.admin.common.CommonUtil;
import org.anyframe.oden.admin.common.OdenCommonDao;
import org.anyframe.oden.admin.dao.JobDao;
import org.anyframe.oden.admin.domain.Job;
import org.anyframe.oden.admin.domain.Mapping;
import org.anyframe.oden.admin.service.JobService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import anyframe.common.Page;
import anyframe.iam.core.acl.IViewResourceAccessService;
import anyframe.iam.core.reload.IResourceReloadService;

/**
 * @version 1.0
 * @created 14-7-2010 占쏙옙占쏙옙 10:13:30
 * @author LEE Sujeong
 */
@Service("jobService")
@Transactional(rollbackFor = { Exception.class })
public class JobServiceImpl implements JobService {

	private OdenCommonDao<Job> odenCommonDao = new OdenCommonDao<Job>();

	private String ahref_pre = "<a href=\"";
	private String ahref_mid = "\">";
	private String ahref_post = "</a>";

	private String doubleQuotation = "\"";

	private HashMap runningMap;
	private HashMap waitMap;

	@Value("#{contextProperties['pageUnit'] ?: 30}")
	int pageUnit;

	@Value("#{contextProperties['previewPageUnit'] ?: 100}")
	int previewPageUnit;

	@Inject
	@Named("jobDao")
	private JobDao jobDao;

	@Inject
	@Named("resourceReloadService")
	IResourceReloadService service;
	
	@Inject
	@Named("viewResourceAccessService")
	IViewResourceAccessService viewResourceAccessService;

	/**
	 * Method for Job Deploy Preveiw page.
	 * 
	 * @param objPage
	 * @param param
	 * @param opt
	 * @throws Exception
	 */
	public Page test(Object objPage, String param, String opt) throws Exception {

		String option = "";

		int page = Integer.parseInt(objPage + "");

		if (opt.indexOf("i") != -1) {
			option += "-i ";
		} else if (opt.indexOf("u") != -1) {
			option += "-u ";
		}
		if (opt.indexOf("d") != -1) {
			option += "-del ";
		}

		option += "-page" + " " + (page - 1) + " ";

		option += "-pgscale" + " " + previewPageUnit;

		String result = odenCommonDao.getResultString("deploy", "test",
				doubleQuotation + param + doubleQuotation + " " + option);

		String imgSuccess = "<img src='images/cross.png' style='vertical-align:middle;'/>";

		// String toggle =
		// "<input type='button' onClick='javascript:toggleRemoveList();'>"+
		// imgSuccess +"</input>";
		String toggle = ahref_pre + "javascript:toggleRemoveList();"
				+ ahref_mid + imgSuccess + ahref_post;

		List list = new ArrayList();

		int totalNum = 0;
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

							String mode = dataObj.getString("mode");
							String path = dataObj.getString("path");
							JSONArray targets = dataObj.getJSONArray("targets");
							String target = "";
							for (int n = 0; n < targets.length(); n++) {
								target += "[" + targets.get(n) + "] ";
							}
							target = target.substring(0, target.length() - 1);

							Job g = new Job();
							g.setMode(mode); // ADD-DELETE
							g.setFile(path);
							g.setDestination(target);
							g.setToggle(toggle);
							g.setHidden("");
							list.add(g);
						}
					}
				}
			}
		}

		// if (list.size() == 0) {
		// return new Page(list, 1, list.size(), 1, 1);
		// } else {
		return new Page(list, page, totalNum, previewPageUnit, previewPageUnit);
		// }

	}

	/**
	 * Method for Job Deploy Preveiw page.
	 * 
	 * @param param
	 * @param opt
	 * @param job
	 * @param objPage
	 * @throws Exception
	 */
	public String run(String[] param, String opt, String job, Object objPage,
			String cmd, String userid) throws Exception {

		String option_result = "";
		String option_iu = "";
		String option_d = "";
		String option_script = "";
		String option_user = "";

		if (opt.indexOf("i") != -1) {
			option_iu += "-i ";
		} else if (opt.indexOf("u") != -1) {
			option_iu += "-u ";
		}
		if (opt.indexOf("d") != -1) {
			option_d += "-del ";
		}

		List paramList = new ArrayList();

		boolean boolDeployAll = false;

		for (int i = 0; i < param.length; i++) {
			String[] values = param[i].split("@oden@");

			String mode = values[0];
			String file = values[1];

			String temp = mode + "@oden@" + file;

			if (i == 0 && mode.equalsIgnoreCase(".")) {
				// deploy all one page
				break;
			}

			if (i == 0 && mode.equalsIgnoreCase("..")) {
				// deploy all whole page
				boolDeployAll = true;
				break;
			}

			paramList.add(temp);
		}

		if (boolDeployAll) {
			// deploy all whole page
		} else {
			Set wholeList = getListByPage(job, option_iu + option_d, objPage);

			option_iu = option_iu.replaceAll("u", "i");
			// mode + "@oden@" + file
			Iterator itr = wholeList.iterator();
			while (itr.hasNext()) {
				String strList = itr.next() + "";
				if (paramList.contains(strList)) {
				} else {
					String[] values = strList.split("@oden@");

					String mode = values[0];
					String file = values[1];

					if (mode.equalsIgnoreCase("add")) {
						option_iu += doubleQuotation + file + doubleQuotation
								+ " ";
					} else if (mode.equalsIgnoreCase("update")) {
						option_iu += doubleQuotation + file + doubleQuotation
								+ " ";
					} else if (mode.equalsIgnoreCase("delete")) {
						option_d += doubleQuotation + file + doubleQuotation
								+ " ";
					}
				}
			}
		}
		// 배포 후 스크립트 구동 옵션
		if (!(cmd.equals(null) || cmd.equals("")))
			option_script = "-after" + " " + cmd + " ";

		// 배포 실행 계정
		if (!(userid.equals(null) || userid.equals("")))
			option_user = "-_user" + " " + userid + " ";

		option_result = option_iu + option_d + option_script + option_user;

		return odenCommonDao.getResultString("deploy", "run", doubleQuotation
				+ job + doubleQuotation + " " + option_result);
	}
	
	/**
	 * Method for rollback of Job Deploy.
	 * 
	 * @param txid
	 * @throws Exception
	 * 
	 */
	public String rollback(String txid) throws Exception {
		return odenCommonDao.getResultString("deploy", "undo", txid);
	}

	private Set getListByPage(String job, String opt, Object objPage)
			throws Exception {
		Set result = new HashSet();

		int page = Integer.parseInt(objPage + "") - 1;

		String pageOpt = "-page" + " " + page;
		pageOpt += " " + "-pgscale" + " " + previewPageUnit;

		String strWhole = odenCommonDao.getResultString("deploy", "test",
				doubleQuotation + job + doubleQuotation + " " + opt + " "
						+ pageOpt);

		if (!(strWhole == null) && !strWhole.equals("")) {
			JSONArray array = new JSONArray(strWhole);
			if (!(array.length() == 0)) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = (JSONObject) array.get(i);
					JSONArray data = (JSONArray) object.get("data");
					if (!(data.length() == 0)) {
						for (int j = 0; j < data.length(); j++) {
							JSONObject dataObj = (JSONObject) data.get(j);
							String path = dataObj.getString("path");
							String mode = dataObj.getString("mode");

							String temp = mode + "@oden@" + path;
							result.add(temp);
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * Method for Job List with Job status, Latest History and Job Actions.
	 * 
	 * @param cmd
	 * @throws Exception
	 */
	public Page findList(String cmd) throws Exception {
		ArrayList<String> roles = CommonUtil.getRoleList(cmd);

		runningMap = new HashMap();
		waitMap = new HashMap();
		List _list = odenCommonDao.getListByList("job", "info");
		List list = new ArrayList();

		runningStatus();
		Collection runningJob = runningMap.values();
		Collection waitJob = waitMap.values();

		for (int i = 0; i < _list.size(); i++) {
			Job g = new Job();

			String imgSuccess = "<img src='images/accept.png' style='vertical-align:middle;'/>";
			String imgFail = "<img src='images/exclamation.png' style='vertical-align:middle;'/>";
			String imgRunning = "<img src='images/progress.gif' style='vertical-align:middle;'/>";
			String imgWait = "<img src='images/progress_1.gif' style='vertical-align:middle;'/>";

			String gStatus = "";
			String gTxid = "";
			String gName = _list.get(i) + "";
			if (gName.indexOf("\\") != -1) {
				gName = gName.replaceAll("\\\\", "/");
			}

			if (roles.contains(gName) || roles.get(0).equals("ROLE_ADMIN")) {
				g
						.setJobname(ahref_pre
								+ "javascript:fn_addTab('03job', 'JobDeatil', 'jobdetail', '"
								+ gName + "');" + ahref_mid + gName
								+ ahref_post);
				
				if (runningJob.contains(gName)) {
					Iterator itr = runningMap.keySet().iterator();
					String id = "";
					while (itr.hasNext()) {
						id = itr.next() + "";
						String name = runningMap.get(id) + "";
						if (name.equalsIgnoreCase(gName)) {
							break;
						} else {
							id = "";
						}
					}

					gStatus = imgRunning;
					g.setTxId(id);
					g.setDate(gStatus);
					g.setMode(runningJobAction(id));
				} else if (waitJob.contains(gName)) {
					Iterator itr = waitMap.keySet().iterator();
					String id = "";
					while (itr.hasNext()) {
						id = itr.next() + "";
						String name = waitMap.get(id) + "";
						if (name.equalsIgnoreCase(gName)) {
							break;
						} else {
							id = "";
						}
					}

					gStatus = imgWait;
					g.setTxId(id);
					g.setDate(gStatus);
					g.setMode(runningJobAction(id));
					
				} else {
					String para = "-job " + doubleQuotation + gName
							+ doubleQuotation;
					String result = odenCommonDao.getResultString("log",
							"search", para);

					if (!(result == null) && !result.equals("")) {
						JSONArray array = notUndoLog(new JSONArray(result));
						
						if (!(array.length() == 0)) {
//							int n = array.length() - 1; // latest history
							JSONObject object = (JSONObject) array.get(0); // latest history							
							gTxid = object.getString("txid");
							String status = object.getString("status");
							String date = object.getString("date");

							if (status.equalsIgnoreCase("S")) {
								gStatus = ahref_pre
										+ "javascript:fn_addTab('04History', 'History', 'historydetail', '"
										+ gTxid + "');" + ahref_mid
										+ imgSuccess + "(" + date + ")"
										+ ahref_post;
								;
							} else if (status.equalsIgnoreCase("F")) {
								gStatus = ahref_pre
										+ "javascript:fn_addTab('04History', 'History', 'historydetail', '"
										+ gTxid + "');" + ahref_mid
										+ imgFail + "(" + date + ")"
										+ ahref_post;
								;
							} else {
								gStatus = "";
							}
						
//							String total = object.getString("total");
//							JSONArray data = (JSONArray) object.get("data");
//							if (!(data.length() == 0)) {
//								JSONObject dataObj = (JSONObject) data.get(0);
//
//								gTxid = dataObj.getString("txid");
//								String status = dataObj.getString("status");
//								String date = dataObj.getString("date");
//
//								if (status.equalsIgnoreCase("S")) {
//									gStatus = ahref_pre
//											+ "javascript:fn_addTab('04History', 'History', 'historydetail', '"
//											+ gTxid + "');" + ahref_mid
//											+ imgSuccess + "(" + date + ")"
//											+ ahref_post;
//									;
//								} else if (status.equalsIgnoreCase("F")) {
//									gStatus = ahref_pre
//											+ "javascript:fn_addTab('04History', 'History', 'historydetail', '"
//											+ gTxid + "');" + ahref_mid
//											+ imgFail + "(" + date + ")"
//											+ ahref_post;
//									;
//								} else {
//									gStatus = "";
//								}
						} else {
							gTxid = gName;
						}
					}

					g.setTxId(gTxid);
					g.setDate(gStatus);
					g.setMode(stoppingJobAction(gName, gTxid, gStatus));
				}
				list.add(g);
			}
		}

		if (list.size() == 0) {
			return new Page(list, 1, list.size(), 1, 1);
		} else {
			return new Page(list, 1, list.size(), list.size(), list.size());
		}
	}

	private JSONArray notUndoLog(JSONArray array) throws Exception {
		JSONArray rtnArr = new JSONArray();
		
		if (!(array.length() == 0)) {
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = (JSONObject) array.get(i);
				JSONArray data = (JSONArray) object.get("data");
				if (!(data.length() == 0)) {
					for(int j=0 ; j < data.length() ; j++) {
						JSONObject dataObj = (JSONObject) data.get(j);
						String txid = dataObj.getString("txid");
						if(! isUndoId(txid)) {
							rtnArr.put(dataObj);
							break;
						}
						
					}
						
				}
			}
		}
		return rtnArr;
	}
	
	private boolean isUndoId(String txid) throws Exception {
		String para = "-job " + doubleQuotation + "deploy undo:" + txid
				+ doubleQuotation;
		
		String result = odenCommonDao.getResultString("log", "search", para);
		JSONArray array = new JSONArray(result);
		JSONObject object = (JSONObject) array.get(0);
		JSONArray data = (JSONArray) object.get("data");
		
		return data.length() == 0 ? false : true;
	}
	
	/**
	 * Methods for getting job list in progress.
	 * 
	 * @throws Exception
	 */
	private void runningStatus() throws Exception {
		// HashMap resultMap = new HashMap();
		String result = odenCommonDao.getResultString("status", "info");
		String pre = "deploy undo:";
		
		if (!(result == null) && !result.equals("")) {
			JSONArray array = new JSONArray(result);
			if (!(array.length() == 0)) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = (JSONObject) array.get(i);
					String jobName = object.getString("desc");
					jobName = jobName.startsWith(pre) ? getJobById(jobName
							.substring(jobName.indexOf(pre) + pre.length(),
									jobName.indexOf(pre) + pre.length() + 13))
							: object.getString("desc");
					String txid = object.getString("id");
					int status = object.getInt("status");
					if (status == 4) {
						runningMap.put(txid, jobName);
					} else if (status == 2) {
						waitMap.put(txid, jobName);
					} else {
					}
				}
			}
		}
	}
	
	/**
	 * Methods for getting job name by transaction ID.
	 * 
	 * @throws Exception
	 */
	private String getJobById(String txid) throws Exception {
		String result = odenCommonDao.getResultString("log", "show", txid);
		if (!(result == null) && !result.equals("")) {
			JSONArray array = new JSONArray(result);
			if (!(array.length() == 0)) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = (JSONObject) array.get(i);
					String jobName = object.getString("job");
					if(! jobName.equals(""))
						return jobName;
				}
			}
		}
		return "";
	}
	
	/**
	 * Method for getting actions for Job List in progress.
	 * 
	 * @param txId
	 * @return String
	 */
	private String runningJobAction(String txId) {

		String imgStop = "<img src='images/stop.png' style='vertical-align:middle;'/>";

		String result = "";
		result += ahref_pre + "javascript:stopDeployJob('" + txId + "');"
				+ ahref_mid + imgStop + ahref_post;
		return result;
	}

	/**
	 * Method for getting actions for Job List stopped.
	 * 
	 * @param jobName
	 * @param txId
	 * @return String
	 * @throws Exception
	 */
	private String stoppingJobAction(String jobName, String txId,String gStatus)
			throws Exception {

		String imgDeploy = "<img src='images/ico_deploy.gif' alt='Job Deploy' title='Job Deploy' style='vertical-align:middle;'/>";
		String imgCleanDeploy = "<img src='images/ico_celandeploy.gif' alt='Clean Deploy' title='Clean Deploy' style='vertical-align:middle;'/>";
		String imgCompare = "<img src='images/ico_compare.gif' alt='Compare Targets' title='Compare Targets' style='vertical-align:middle;'/>";
		String imgCompareUnable = "<img src='images/ico_compare_d.gif' alt='Compare Targets(Unable)' title='Compare Targets(Unable)' style='vertical-align:middle;'/>";
		String imgScript = "<img src='images/ico_runscript.gif' alt='Run Script' title='Run Script' style='vertical-align:middle;'/>";
		String imgScriptUnable = "<img src='images/ico_runscript_d.gif' alt='Run Script(Unable)' title='Run Script(Unable)' style='vertical-align:middle;'/>";
		String imgDel = "<img src='images/ico_del.gif' alt='Delete Job' title='Delete Job' style='vertical-align:middle;'/>";
		String imgDelUnable = "<img src='images/ico_del_d.gif' alt='Delete Job' title='Delete Job' style='vertical-align:middle;'/>";
		String imgRollback = "<img src='images/ico_rollback.gif' alt='Job Rollback' title='Job Rollback' style='vertical-align:middle;'/>";
		String imgRollbackUnable = "<img src='images/ico_rollback_d.gif' alt='Job Rollback(Unable)' title='Job Rollback(Unable)' style='vertical-align:middle;'/>";

		Page g = new ServerServiceImpl().findListByPk(jobName);
		Page c = new ScriptServiceimpl().findListByPk(jobName, "del");

		String result = "";
		List<Integer> permList = new ArrayList<Integer>();
		permList.add(4);
		
		boolean access = viewResourceAccessService.isGranted("addUser", permList);

		int optCount = 0;
		while (optCount < 6) {
			String link = "";
			String action = "";
			boolean b = true;
			String deploy_init = "&initdataService=scriptService.getCommandList(param)&initdataResult=cmds&param="
					+ jobName;
			switch (optCount) {
			case 0:
				link = "javascript:fn_addTab('03job', 'Deploy', 'deploy', '"
						+ jobName + "','" + deploy_init + "');";
				action = imgDeploy;
				break;
			case 1:
				link = "javascript:cleanDeploy('" + jobName + "');";
				action = imgCleanDeploy;
				break;
			case 2:
				link = "javascript:fn_addTab('03job', 'Compare', 'compare', '"
						+ jobName + "');";
				if (g.getList().size() > 1) {
					action = imgCompare;
				} else {
					action = imgCompareUnable;
					b = false;
				}
				break;
			case 3:
				link = "javascript:fn_addTab('03job', 'Restart', 'script', '"
						+ jobName + "');";
				if (c.getList().size() > 0) {
					action = imgScript;
				} else {
					action = imgScriptUnable;
					b = false;
				}
				break;
			case 4:
				link = "javascript:delJob('" + jobName + "');";
				if(access) {
					action = imgDel;
				} else {
					action = imgDelUnable;
					b = false;
				}
				break;
			case 5:
				link = "javascript:rollbackJob('" + txId + "');";
				if(! gStatus.equals("")) {
					action = imgRollback;
				} else {
					action = imgRollbackUnable;
					b = false;
				}
				break;	
			default:
				break;
			}

			if (b) {
				result += ahref_pre + link + ahref_mid + action + ahref_post
						+ "&nbsp;&nbsp;&nbsp;";
			} else {
				result += action + "&nbsp;&nbsp;&nbsp;";
			}
			// result += ahref_pre + link + ahref_mid + action
			// + ahref_post + "   ";

			optCount++;
		}

		// result = result.substring(0, result.length() - 3);
		return result;
	}

	/**
	 * Method for Job Detail page.
	 * 
	 * @param param
	 * @throws Exception
	 */
	public Job findByName(String param) throws Exception {

		if (param.equals("")) {
			return new Job();
		} else {
			String result = odenCommonDao.getResultString("job", "info",
					doubleQuotation + param + doubleQuotation);
			Job g = new Job();

			if (!(result == null) && !result.equals("")) {
				JSONArray array = new JSONArray(result);
				if (!(array.length() == 0)) {
					int recordSize = array.length();
					for (int i = 0; i < recordSize; i++) {
						JSONObject object = (JSONObject) array.get(i);
						String name = object.get("name") + "";
						JSONObject sources = (JSONObject) object.get("sources");

						String repo = sources.getString("dir");
						// JSONArray mappings = (JSONArray) sources
						// .get("mappings");
						//

						JSONArray excludes = (JSONArray) sources
								.get("excludes");
						String strExcludes = "";
						for (int num = 0; num < excludes.length(); num++) {
							strExcludes += excludes.get(num) + ", ";
						}
						if (!strExcludes.equals("")) {
							strExcludes = strExcludes.substring(0, strExcludes
									.length() - 2);
						}

						g.setJobname(name);
						g.setRepo(repo);
						g.setExcludes(strExcludes);
					}
				}
			}

			return g;
		}

	}

	/**
	 * Method for Job info save new one.
	 * 
	 * @param params
	 * @param cmds
	 * @param mappings
	 * @param jobname
	 * @param repository
	 * @param excludes
	 * @throws Exception
	 */
	public void insert(String[] param, String[] cmds, String[] mappings,
			String jobname, String repository, String excludes)
			throws Exception {
		insertToOdenServer(param, cmds, mappings, jobname, repository, excludes);
		// DB Role Insert
		try {
			this.createRole(jobname);
		} catch (Exception e) {
			// 추가 된 job add rollback
			this.remove(jobname);
			throw e;
		}
	}

	/**
	 * Method for Job info update one.
	 * 
	 * @param params
	 * @param cmds
	 * @param mappings
	 * @param jobname
	 * @param repository
	 * @param excludes
	 * @throws Exception
	 */
	public void update(String[] param, String[] cmds, String[] mappings,
			String jobname, String repository, String excludes)
			throws Exception {
		insertToOdenServer(param, cmds, mappings, jobname, repository, excludes);
	}

	private void insertToOdenServer(String[] param, String[] cmds,
			String[] mappings, String jobname, String repository,
			String excludes) throws Exception {
		JSONObject jo = new JSONObject();
		jo.put("name", jobname);

		JSONObject joSource = new JSONObject();
		joSource.put("dir", repository);

		String exclu = "";
		StringTokenizer token2 = new StringTokenizer(excludes, ",");
		while (token2.hasMoreTokens()) {
			exclu += token2.nextToken().trim() + ",";
		}
		if (exclu.length() == 0) {
		} else {
			exclu = exclu.substring(0, exclu.length() - 1);
		}
		joSource.put("excludes", exclu);

		JSONArray mappingArray = new JSONArray();
		for (int i = 0; i < mappings.length; i++) {
			String key = mappings[i];
			String[] values = key.split("@oden@");
			String dir = values[0];
			String checkout = values[1];

			if (i == 0 && dir.equalsIgnoreCase(".")
					&& checkout.equalsIgnoreCase(".")) {
				break;
			}

			JSONObject mapping = new JSONObject();
			mapping.put("dir", dir);
			mapping.put("checkout-dir", checkout);

			mappingArray.put(mapping);
		}

		joSource.put("mappings", mappingArray);
		jo.put("source", joSource);

		JSONArray jaTarget = new JSONArray();
		for (int i = 0; i < param.length; i++) {
			String para = param[i];
			String[] values = para.split("@oden@");
			String name = values[0];
			String url = values[1];
			String path = values[2];

			JSONObject innerJO = new JSONObject();
			innerJO.put("name", name);
			innerJO.put("address", url);
			innerJO.put("dir", path);

			jaTarget.put(innerJO);
		}
		jo.put("targets", jaTarget);

		JSONArray jaCommands = new JSONArray();
		for (int i = 0; i < cmds.length; i++) {
			String para = cmds[i];
			String[] values = para.split("@oden@");
			String name = values[0];
			String path = values[1];
			String script = values[2];

			if (i == 0 && name.equalsIgnoreCase(".")) {
				break;
			}

			JSONObject innerJO = new JSONObject();
			innerJO.put("name", name);
			innerJO.put("dir", path);
			innerJO.put("command", script);

			jaCommands.put(innerJO);
		}
		jo.put("commands", jaCommands);

		String cmd = "_job add" + " " + jo;
		odenCommonDao.update(cmd);
	}

	/**
	 * Method for creating role info.
	 * 
	 * @param jobname
	 * @throws Exception
	 */
	private void createRole(String jobname) throws Exception {
		// insertRoles
		jobDao.insertRoles(jobname);
		// insertSecuredResRoles(2 roles)
		jobDao.insertSecuredResRoles("WEB-000001", jobname);
		jobDao.insertSecuredResRoles("WEB-000002", jobname);

	}

	/**
	 * Method for deleting role info.
	 * 
	 * @param jobname
	 * @throws Exception
	 */
	private void removeRole(String jobname) throws Exception {
		// removeSecuredResRoles
		jobDao.removeSecuredResRoles(jobname);
		// removeAuthorities
		jobDao.removeAuthorities(jobname);
		// removeRoles
		jobDao.removeRoles(jobname);

	}

	/**
	 * Method for getting Job mapping info in Job Detail page.
	 * 
	 * @param param
	 * @throws Exception
	 */
	public Page loadMappings(String param) throws Exception {
		if (param.equals("")) {
			List list = new ArrayList();
			return new Page(list, 1, list.size(), 1, 1);
		} else {
			List list = new ArrayList();

			String result = odenCommonDao.getResultString("job", "info",
					doubleQuotation + param + doubleQuotation);

			String imgDel = "<img src='images/ico_del.gif'/>";

			if (!(result == null) && !result.equals("")) {
				JSONArray array = new JSONArray(result);
				if (!(array.length() == 0)) {
					int recordSize = array.length();
					for (int i = 0; i < recordSize; i++) {
						JSONObject object = (JSONObject) array.get(i);
						JSONObject sources = (JSONObject) object.get("sources");

						JSONArray mappings = (JSONArray) sources
								.get("mappings");
						if (!(mappings == null) && !mappings.equals("")) {
							for (int n = 0; n < mappings.length(); n++) {
								JSONObject mapping = (JSONObject) mappings
										.get(n);
								String dir = mapping.getString("dir");
								String checkout = mapping
										.getString("checkout-dir");

								String key = dir + "@oden@" + checkout;

								String event = ahref_pre
										+ "javascript:delSource('" + key
										+ "');" + ahref_mid + imgDel
										+ ahref_post;

								Mapping m = new Mapping();
								m.setDir(dir);
								m.setCheckout(checkout);
								m.setHidden(event);
								m.setHiddenname(key);

								list.add(m);
							}
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

	public Page findMappings(String param) throws Exception {
		if (param.equals("")) {
			List list = new ArrayList();
			return new Page(list, 1, list.size(), 1, 1);
		} else {
			List list = new ArrayList();

			String opt = doubleQuotation + param + doubleQuotation;

			String result = odenCommonDao.getResultString("job",
					"mapping-scan", opt);

			String imgDel = "<img src='images/ico_del.gif'/>";

			if (!(result == null) && !result.equals("")) {
				JSONArray array = new JSONArray(result);
				if (!(array.length() == 0)) {
					int recordSize = array.length();
					for (int i = 0; i < recordSize; i++) {
						JSONObject object = (JSONObject) array.get(i);

						String dir = object.getString("dir");
						String checkout = object.getString("checkout-dir");

						String key = dir + "@oden@" + checkout;

						String event = ahref_pre + "javascript:delSource('"
								+ key + "');" + ahref_mid + imgDel + ahref_post;

						Mapping m = new Mapping();
						m.setDir(dir);
						m.setCheckout(checkout);
						m.setHidden(event);
						m.setHiddenname(key);

						list.add(m);
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

	/**
	 * Method to compare targets in same Job.
	 * 
	 * @param objPage
	 * @param param
	 * @param opt
	 */
	public Page compare(Object objPage, String param, String opt)
			throws Exception {

		int page = Integer.parseInt(objPage + "");
		int totalNum = 0;

		String option = opt + " ";

		String imgSuccess = "<img src='images/accept.png' style='vertical-align:middle;'/>";
		String imgFail = "<img src='images/exclamation.png' style='vertical-align:middle;'/>";

		if (page == 0) {
			option += "-page" + " " + page;
		} else {
			option += "-page" + " " + (page - 1);
		}

		String result = odenCommonDao.getResultString("job", "compare",
				doubleQuotation + param + doubleQuotation + " " + option);

		List list = new ArrayList();

		if (!(result == null) && !result.equals("")) {
			JSONArray array = new JSONArray(result);
			if (!(array.length() == 0)) {
				int recordSize = array.length();
				for (int i = 0; i < recordSize; i++) {
					JSONObject object = (JSONObject) array.get(i);

					totalNum = Integer.parseInt(object.getString("total"));
					JSONArray data = (JSONArray) object.get("data");

					if (!(data.length() == 0)) {
						for (int j = 0; j < data.length(); j++) {
							HashMap map = new HashMap();
							JSONObject dataObj = (JSONObject) data.get(j);

							String path = dataObj.getString("path");
							String equal = dataObj.getString("equal");

							String eqaulResult = "";
							if (equal.equals("T")) {
								eqaulResult = imgSuccess;
							} else {
								eqaulResult = imgFail;
							}
							map.put("status", eqaulResult);
							map.put("file", path);

							JSONArray targets = (JSONArray) dataObj
									.get("targets");
							if (!(targets.length() == 0)) {
								for (int n = 0; n < targets.length(); n++) {
									JSONObject t = (JSONObject) targets.get(n);
									String name = t.getString("name");
									String date = chgDateFormat(Long
											.parseLong((t.getString("date"))));
									String size = t.getString("size");
									map.put(name, date + "<br/>" + size
											+ "byte");
								}
							}
							list.add(map);
						}
					}
				}
			}
		}

		return new Page(list, page, totalNum, pageUnit, pageUnit);
	}

	/**
	 * Method to getting target server names for setting to grid header.
	 * 
	 * @param param
	 */
	public HashMap compareHeader(String param) throws Exception {

		int numHeader = 0;

		ArrayList header = new ArrayList();
		header.add("STATUS");
		header.add("FILE");

		String result = odenCommonDao.getResultString("job", "info",
				doubleQuotation + param + doubleQuotation);

		if (!(result == null) && !result.equals("")) {
			JSONArray array = new JSONArray(result);
			if (!(array.length() == 0)) {
				int tarLeng = array.length();
				for (int i = 0; i < tarLeng; i++) {

					JSONObject object = (JSONObject) array.get(i);
					JSONArray targets = (JSONArray) object.get("targets");

					numHeader = targets.length();
					for (int num = 0; num < numHeader; num++) {
						JSONObject target = (JSONObject) targets.get(num);
						String name = target.getString("name");
						header.add(name);
					}
				}
			}
		}
		ArrayList model = new ArrayList();

		// status
		HashMap map_model_status = new HashMap();
		map_model_status.put("name", ((String) header.get(0)).toLowerCase());
		map_model_status.put("index", ((String) header.get(0)).toLowerCase());
		map_model_status.put("align", "center");
		map_model_status.put("width", "60");
		map_model_status.put("hidedlg", "true");
		map_model_status.put("sortable", false);
		model.add(map_model_status);

		// file
		HashMap map_model_file = new HashMap();
		map_model_file.put("name", ((String) header.get(1)).toLowerCase());
		map_model_file.put("index", ((String) header.get(1)).toLowerCase());
		map_model_file.put("align", "left");
		map_model_file.put("width", "245");
		map_model_file.put("hidedlg", "true");
		map_model_file.put("sortable", false);
		model.add(map_model_file);

		// target servers
		for (int i = 2; i < numHeader + 2; i++) {
			HashMap map_model = new HashMap();
			map_model.put("name", header.get(i));
			map_model.put("index", header.get(i));
			map_model.put("align", "center");
			map_model.put("width", "180");
			map_model.put("resizable", true);
			map_model.put("sortable", false);
			model.add(map_model);
		}
		HashMap map_result = new HashMap();
		map_result.put("header", header);
		map_result.put("model", model);

		return map_result;
	}

	/**
	 * Method to remove job
	 * 
	 * @param name
	 */
	public void remove(String name) throws Exception {
		odenCommonDao.remove("_job", name);
		this.removeRole(name);
	}

	/**
	 * Method to stop the job what is running
	 * 
	 * @param param
	 */
	public void stop(String param) throws Exception {
		odenCommonDao.getResultString("status", "stop", param);
	}

	private String chgDateFormat(Long input) {
		return new SimpleDateFormat("yyyy.MM.dd aa hh:mm:ss").format(input);
	}

	/**
	 * Methode for downloading excel file with job comparing information.
	 * 
	 * @param param
	 * @throws Exception
	 */
	public List<HashMap> excel(String param) throws Exception {

		String result = odenCommonDao.getResultString("job", "compare", param);

		List list = new ArrayList();

		if (!(result == null) && !result.equals("")) {
			JSONArray array = new JSONArray(result);
			if (!(array.length() == 0)) {
				int recordSize = array.length();
				for (int i = 0; i < recordSize; i++) {
					JSONObject object = (JSONObject) array.get(i);
					JSONArray data = (JSONArray) object.get("data");

					if (!(data.length() == 0)) {
						for (int j = 0; j < data.length(); j++) {
							HashMap map = new HashMap();
							JSONObject dataObj = (JSONObject) data.get(j);

							String path = dataObj.getString("path");
							String equal = dataObj.getString("equal");

							map.put("status", equal);
							map.put("file", path);

							JSONArray targets = (JSONArray) dataObj
									.get("targets");
							if (!(targets.length() == 0)) {
								for (int n = 0; n < targets.length(); n++) {
									JSONObject t = (JSONObject) targets.get(n);
									String name = t.getString("name");
									String date = chgDateFormat(Long
											.parseLong((t.getString("date"))));
									String size = t.getString("size");
									map.put(name, date + " " + size + "byte");
								}
							}
							list.add(map);
						}
					}
				}
			}
		}

		return list;
	}

	/**
	 * Job list for User page.(Fixed role is ROLE_ADMIN)
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Job> findJob() throws Exception {
		ArrayList roles = new ArrayList();
		roles.add("ROLE_ADMIN");
		return odenCommonDao.findJob("job", "info", roles);
	}
}