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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.iam.core.acl.IViewResourceAccessService;
import org.anyframe.iam.core.reload.IResourceReloadService;
import org.anyframe.oden.admin.common.OdenCommonDao;
import org.anyframe.oden.admin.convert.JsonConverter;
import org.anyframe.oden.admin.dao.JobDao;
import org.anyframe.oden.admin.domain.BuildHistory;
import org.anyframe.oden.admin.domain.Command;
import org.anyframe.oden.admin.domain.Job;
import org.anyframe.oden.admin.domain.Mapping;
import org.anyframe.oden.admin.service.BuildService;
import org.anyframe.oden.admin.service.GroupService;
import org.anyframe.oden.admin.service.JobService;
import org.anyframe.oden.admin.service.ScriptService;
import org.anyframe.oden.admin.service.ServerService;
import org.anyframe.oden.admin.util.CommandUtil;
import org.anyframe.oden.admin.util.CommonUtil;
import org.anyframe.oden.admin.util.DateUtil;
import org.anyframe.oden.admin.util.MapUtil;
import org.anyframe.oden.admin.util.OdenConstants;
import org.anyframe.pagination.Page;
import org.hsqldb.lib.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is JobServiceImpl Class
 * 
 * @author Sujeong Lee
 */
@Service("jobService")
@Transactional(rollbackFor = { Exception.class })
public class JobServiceImpl implements JobService {

	private Map<String, String> runningMap = new HashMap<String, String>();
	private Map<String, String> waitMap = new HashMap<String, String>();
	private Map<String, String> runningBuildMap = new HashMap<String, String>();
	private Map<String, String> runningBuildURLMap = new HashMap<String, String>();

	@Value("#{contextProperties['pageUnit'] ?: 30}")
	int pageUnit;

	@Value("#{contextProperties['previewPageUnit'] ?: 100}")
	int previewPageUnit;

	@Inject
	@Named("odenCommonDao")
	OdenCommonDao<Job> odenCommonDao;

	@Inject
	@Named("jobDao")
	private JobDao jobDao;

	@Inject
	@Named("groupService")
	GroupService groupservice;

	@Inject
	@Named("buildService")
	BuildService buildservice;

	@Inject
	@Named("serverService")
	ServerService serverService;

	@Inject
	@Named("scriptService")
	ScriptService scriptService;

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
			option = option.concat("-i ");
		} else if (opt.indexOf("u") != -1) {
			option = option.concat("-u ");
		}
		if (opt.indexOf("d") != -1) {
			option = option.concat("-del ");
		}

		option = option.concat("-page" + " " + (page - 1) + " ");
		option = option.concat("-pgscale" + " " + previewPageUnit);

		String toggle = OdenConstants.A_HREF_HEAD + "javascript:toggleRemoveList();" + OdenConstants.A_HREF_MID + OdenConstants.IMG_TAG_CROSS
				+ OdenConstants.A_HREF_TAIL;

		List<Job> jobList = new ArrayList<Job>();

		String command = CommandUtil.getBasicCommand("deploy", "test", OdenConstants.DOUBLE_QUOTATOIN + param + OdenConstants.DOUBLE_QUOTATOIN + " "
				+ option);
		List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

		int totalNum = 0;
		for (JSONObject object : objectArray) {
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
						target = target.concat("[" + targets.get(n) + "] ");
					}
					target = target.substring(0, target.length() - 1);

					Job job = new Job();
					job.setMode(mode); // ADD-DELETE
					job.setFile(path);
					job.setDestination(target);
					job.setToggle(toggle);
					job.setHidden("");
					jobList.add(job);
				}
			}
		}
		return new Page(jobList, page, totalNum, previewPageUnit, previewPageUnit);
	}

	/**
	 * Method for Job Deploy page.
	 * 
	 * @param param
	 * @param opt
	 * @param job
	 * @param objPage
	 * @throws Exception
	 */
	public String run(String[] param, String opt, String job, Object objPage, String cmd, String userid) throws Exception {

		String option_result = "";
		String option_iu = "";
		String option_d = "";
		String option_c = "";
		String option_script = "";
		String option_user = "";

		if (opt.indexOf("i") != -1) {
			option_iu = option_iu.concat("-i ");
		} else if (opt.indexOf("u") != -1) {
			option_iu = option_iu.concat("-u ");
		}
		if (opt.indexOf("d") != -1) {
			option_d = option_d.concat("-del ");
		}
		if (opt.indexOf("c") != -1) {
			option_c = option_c.concat("-c ");
		}

		List<String> paramList = new ArrayList<String>();

		boolean boolDeployAll = false;

		for (int i = 0; i < param.length; i++) {
			String[] values = param[i].split("@oden@");

			String mode = values[0];
			String file = values[1];

			String newParam = mode + "@oden@" + file;

			if (i == 0 && mode.equalsIgnoreCase(".")) {
				// deploy all one page
				break;
			}

			if (i == 0 && mode.equalsIgnoreCase("..")) {
				// deploy all whole page
				boolDeployAll = true;
				break;
			}

			paramList.add(newParam);
		}

		if (!boolDeployAll) {
			Set<String> wholeList = getListByPage(job, option_iu + option_d, objPage);

			option_iu = option_iu.replaceAll("u", "i");

			Iterator<String> itr = wholeList.iterator();
			while (itr.hasNext()) {
				String strList = itr.next() + "";
				if (!paramList.contains(strList)) {
					String[] values = strList.split("@oden@");

					String mode = values[0];
					String file = values[1];

					if (mode.equalsIgnoreCase("add")) {
						option_iu = option_iu.concat(OdenConstants.DOUBLE_QUOTATOIN + file + OdenConstants.DOUBLE_QUOTATOIN + " ");
					} else if (mode.equalsIgnoreCase("update")) {
						option_iu = option_iu.concat(OdenConstants.DOUBLE_QUOTATOIN + file + OdenConstants.DOUBLE_QUOTATOIN + " ");
					} else if (mode.equalsIgnoreCase("delete")) {
						option_d = option_d.concat(OdenConstants.DOUBLE_QUOTATOIN + file + OdenConstants.DOUBLE_QUOTATOIN + " ");
					}
				}
			}
		}
		// 배포 후 스크립트 구동 옵션
		if (!(cmd == null || "".equals(cmd))) {
			option_script = "-after" + " " + cmd + " ";
		}
		// 배포 실행 계정
		if (!(userid == null || "".equals(userid))) {
			option_user = "-_user" + " " + userid + " ";
		}
		option_result = option_iu + option_d + option_c + option_script + option_user;

		return odenCommonDao.getResultString("deploy", "run", OdenConstants.DOUBLE_QUOTATOIN + job + OdenConstants.DOUBLE_QUOTATOIN + " "
				+ option_result);
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

	private Set<String> getListByPage(String job, String opt, Object objPage) throws Exception {
		Set<String> result = new HashSet<String>();

		int page = Integer.parseInt(String.valueOf(objPage)) - 1;

		String pageOpt = "-page" + " " + page;
		pageOpt = pageOpt.concat(" " + "-pgscale" + " " + previewPageUnit);

		String command = CommandUtil.getBasicCommand("deploy", "test", OdenConstants.DOUBLE_QUOTATOIN + job + OdenConstants.DOUBLE_QUOTATOIN + " "
				+ opt + " " + pageOpt);
		List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

		for (JSONObject object : objectArray) {
			JSONArray data = (JSONArray) object.get("data");
			if (!(data.length() == 0)) {
				for (int j = 0; j < data.length(); j++) {
					JSONObject dataObj = (JSONObject) data.get(j);
					String path = dataObj.getString("path");
					String mode = dataObj.getString("mode");

					String param = mode + "@oden@" + path;
					result.add(param);
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
	@SuppressWarnings("null")
	public Page findList(String cmd, String buildName, String group) throws Exception {
		List<String> roles = CommonUtil.getRoleList(cmd);

		List<String> jobNameList = null;
		if (group.equals("ALL")) {
			jobNameList = odenCommonDao.getStringList("job", "info");
		} else {
			jobNameList = groupservice.findByName(group);
		}
		List<Job> list = new ArrayList<Job>();

		init();
		boolean isRunning = runningStatus();
		boolean buildCheck = getBuildCheck();
		boolean isBuilding = false;
		if(buildCheck != false) {
			isBuilding = runningBuildStatus(buildName);
		}

		for (int i = 0; i < jobNameList.size(); i++) {
			String jobStatus = "";
			String jobTxid = "";
			String jobName = jobNameList.get(i) + "";
			String jobBuild = "";

			Job job = new Job();
			// 2014.11.19 job info를 한번만 날리도록 수정
			String buildCmd = CommandUtil.getBasicCommand("job", "info", OdenConstants.DOUBLE_QUOTATOIN + jobName + OdenConstants.DOUBLE_QUOTATOIN);
			List<JSONObject> objectArr = odenCommonDao.jsonObjectArrays(buildCmd);
			job.setBuild(getBuildByobjectArray(objectArr));
			
			if (jobName.indexOf("\\") != -1) {
				jobName = jobName.replaceAll("\\\\", "/");
			}

			if (roles.contains(jobName) || "ROLE_ADMIN".equals(roles.get(0))) {
				job.setName(OdenConstants.A_HREF_HEAD + "javascript:fn_addTab('03job', 'JobDeatil', 'jobdetail', '" + jobName
						+ "', '&amp;initdataService=groupService.findGroupAndBuildJob()&amp;initdataResult=groupBuildJobs', currentSelectedTab);"
						+ OdenConstants.A_HREF_MID + jobName + OdenConstants.A_HREF_TAIL);

				if (runningMap.containsValue(jobName)) {
					jobTxid = MapUtil.getKeyFromMapByValue(runningBuildMap, jobName);

					jobStatus = OdenConstants.IMG_TAG_RUNNING;
					job.setTxId(jobTxid);
					job.setDate(jobStatus);
					job.setMode(runningJobAction(jobTxid));

				} else if (waitMap.containsValue(jobName)) {
					jobTxid = MapUtil.getKeyFromMapByValue(waitMap, jobName);

					jobStatus = OdenConstants.IMG_TAG_WAIT;
					job.setTxId(jobTxid);
					job.setDate(jobStatus);
					job.setMode(runningJobAction(jobTxid));

				} else if (runningBuildMap.containsValue(job.getBuild())) {
					String build = job.getBuild();
					if (!StringUtil.isEmpty(build) || !"None".equals(build)) {
						job.setBuildDate(OdenConstants.IMG_TAG_RUNNING);
						String consoleUrl = runningBuildURLMap.get(build);
						job.setMode(OdenConstants.A_HREF_HEAD + "javascript:popupOpen('" + consoleUrl + "');" + OdenConstants.A_HREF_MID
								+ OdenConstants.IMG_TAG_MONITOR + OdenConstants.A_HREF_TAIL);
					}
				} else {
					String para = "-job " + OdenConstants.DOUBLE_QUOTATOIN + jobName + OdenConstants.DOUBLE_QUOTATOIN;
					String command = CommandUtil.getBasicCommand("log", "search", para);
					List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

					jobTxid = jobName; // default

					if (objectArray.size() > 0) {
						JSONObject object = objectArray.get(0);
						String total = object.getString("total");
						JSONArray data = (JSONArray) object.get("data");
						if (Integer.parseInt(total) > 0) {
							JSONObject dataObj = (JSONObject) data.get(0);
							if (!isUndoLog(dataObj)) {
								jobTxid = dataObj.getString("txid");
								String status = dataObj.getString("status");
								String date = dataObj.getString("date");

								if (status.equalsIgnoreCase("S")) {
									jobStatus = OdenConstants.A_HREF_HEAD + "javascript:fn_addTab('04History', 'History', 'historydetail', '"
											+ jobTxid + "');" + OdenConstants.A_HREF_MID + OdenConstants.IMG_TAG_SUCCESS + "(" + date + ")"
											+ OdenConstants.A_HREF_TAIL;
								} else if (status.equalsIgnoreCase("F")) {
									jobStatus = OdenConstants.A_HREF_HEAD + "javascript:fn_addTab('04History', 'History', 'historydetail', '"
											+ jobTxid + "');" + OdenConstants.A_HREF_MID + OdenConstants.IMG_TAG_FAIL + "(" + date + ")"
											+ OdenConstants.A_HREF_TAIL;
								} else {
									jobStatus = "";
								}
							}
						}
					}

					if (job.getBuild() != "All") {
						BuildHistory buildHistory = null;
						if(buildCheck != false) {
							buildHistory = buildservice.findByName(job.getBuild());
						}

						if (buildHistory != null && buildHistory.getDate() > 0) {
							if (buildHistory.isSuccess()) {
								job.setBuildDate(OdenConstants.A_HREF_HEAD + "javascript:popupOpen('" + buildHistory.getConsoleUrl() + "');"
										+ OdenConstants.A_HREF_MID + OdenConstants.IMG_TAG_SUCCESS + "("
										+ DateUtil.toStringDate(buildHistory.getDate()) + ")" + OdenConstants.A_HREF_TAIL);
							} else {
								job.setBuildDate(OdenConstants.A_HREF_HEAD + "javascript:popupOpen('" + buildHistory.getConsoleUrl() + "');"
										+ OdenConstants.A_HREF_MID + OdenConstants.IMG_TAG_FAIL + "(" + DateUtil.toStringDate(buildHistory.getDate())
										+ ")" + OdenConstants.A_HREF_TAIL);
							}
						} else {
							job.setBuildDate("");
						}
						jobBuild = job.getBuild();
					}

					job.setTxId(jobTxid);
					job.setDate(jobStatus);
					job.setMode(stoppingJobAction(jobName, jobTxid, jobStatus, jobBuild, isRunning, isBuilding, job, objectArr, buildCheck));
				}
				list.add(job);
			}
		}

		if (list.isEmpty()) {
			return new Page(list, 1, list.size(), 1, 1);
		} else {
			return new Page(list, 1, list.size(), list.size(), list.size());
		}
	}

	private void init() {
		runningMap = new HashMap<String, String>();
		waitMap = new HashMap<String, String>();
		runningBuildMap = new HashMap<String, String>();
		runningBuildURLMap = new HashMap<String, String>();
	}

	private boolean isUndoLog(JSONObject object) throws Exception {
		String txid = object.getString("txid");
		if (isUndoId(txid)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isUndoId(String txid) throws Exception {
		String para = "-job " + OdenConstants.DOUBLE_QUOTATOIN + "deploy undo:" + txid + OdenConstants.DOUBLE_QUOTATOIN;

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
	private boolean runningStatus() throws Exception {
		String command = CommandUtil.getBasicCommand("status", "info");
		List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

		String pre = "deploy undo:";

		for (JSONObject object : objectArray) {
			String jobName = object.getString("desc");
			jobName = jobName.startsWith(pre) ? getJobById(jobName.substring(jobName.indexOf(pre) + pre.length(), jobName.indexOf(pre) + pre.length()
					+ 13)) : object.getString("desc");
			String txid = object.getString("id");
			int status = object.getInt("status");
			if (status == 4) {
				runningMap.put(txid, jobName);
			} else if (status == 2) {
				waitMap.put(txid, jobName);
			}
		}
		return false;
	}

	/**
	 * Methods for getting build list in progress.
	 * 
	 * @throws Exception
	 */
	private boolean runningBuildStatus(String currentBuildName) throws Exception {
		String command = CommandUtil.getBasicCommand("build", "status");
		List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

		for (JSONObject object : objectArray) {
			String buildName = object.getString("jobName");
			String txid = object.getString("buildNo");
			String status = object.getString("status");
			String consoleUrl = object.getString("consoleUrl");
			boolean isCurrentBuildAdd = true;
			if (!currentBuildName.equals("") && buildName.equals(currentBuildName)) {
				int duration = 0;
				while (isCurrentBuildAdd) {

					String detailCommand = CommandUtil.getBasicCommand("build", "status", OdenConstants.DOUBLE_QUOTATOIN + currentBuildName
							+ OdenConstants.DOUBLE_QUOTATOIN);
					List<JSONObject> detailArray = odenCommonDao.jsonObjectArrays(detailCommand);

					if (detailArray.size() == 1) { // 둘 이상 될수 없음
						status = String.valueOf(detailArray.get(0).get("status"));
					}

					if (!status.equals("B")) {
						Thread.sleep(2000);
						duration += 2000;
						if (duration == 30000) {
							return false;
						}

					} else {
						runningBuildMap.put(txid, buildName);
						runningBuildURLMap.put(buildName, consoleUrl);
						isCurrentBuildAdd = false;
						return true;
					}
				}
			} else if (currentBuildName.equals("") || !buildName.equals(currentBuildName)) {
				if (status.equals("B")) {
					runningBuildMap.put(txid, buildName);
					runningBuildURLMap.put(buildName, consoleUrl);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Methods for getting job name by transaction ID.
	 * 
	 * @throws Exception
	 */
	private String getJobById(String txid) throws Exception {
		String command = CommandUtil.getBasicCommand("log", "show", txid);
		List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

		String jobName = "";

		for (JSONObject object : objectArray) {
			jobName = object.getString("job");
			if (!StringUtil.isEmpty(jobName)) {
				return jobName;
			}
		}
		return jobName;
	}

	/**
	 * Method for getting actions for Job List in progress.
	 * 
	 * @param txId
	 * @return String
	 */
	private String runningJobAction(String txId) {
		String result = "";
		result = result.concat(OdenConstants.A_HREF_HEAD + "javascript:stopDeployJob('" + txId + "');" + OdenConstants.A_HREF_MID
				+ OdenConstants.IMG_TAG_STOP + OdenConstants.A_HREF_TAIL);
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
	private String stoppingJobAction(String jobName, String txId, String gStatus, String gBuild, boolean isRunning, boolean isBuilding, Job jobInfo, List<JSONObject> objectArr, boolean buildCheck)
			throws Exception {

		//String command = CommandUtil.getBasicCommand("job", "info", OdenConstants.DOUBLE_QUOTATOIN + jobName + OdenConstants.DOUBLE_QUOTATOIN);
		//List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);
		//List<JSONObject> objectArray1 = odenCommonDao.jsonObjectArrays(command);
		//List<JSONObject> objectArray2 = odenCommonDao.jsonObjectArrays(command);
		
		Page g = serverService.findListByPk(jobName, objectArr);
		Page c = scriptService.findListByPk(jobName, "del", objectArr);

		String result = "";
		List<Integer> permList = new ArrayList<Integer>();
		permList.add(4);

		boolean access = viewResourceAccessService.isGranted("addUser", permList);

		//Job jobInfo = findByName(jobName);

		/**
		 * <pre>
		 * 7 :: operation count 
		 * 0 - Build Run 
		 * 1 - Deploy 
		 * 2 - Clean Deploy 
		 * 3 - Compare 
		 * 4 - Restart 
		 * 5 - Delete Job 
		 * 6 - Rollback Job
		 * </pre>
		 */
		int optCount = 0;
		while (optCount < 7) {
			String link = "";
			String action = "";
			boolean activeLink = true;

			switch (optCount) {
			case 0:
				link = "javascript:runBuild('" + jobInfo.getBuild() + "');";
				if (gBuild == null || gBuild.equals("") || gBuild.equals("None") || isBuilding == true || isRunning == true || buildCheck == false) {
					action = OdenConstants.IMG_TAG_BUILD_UNABLE;
					activeLink = false;
				} else {
					action = OdenConstants.IMG_TAG_BUILD;
				}

				break;
			case 1:
				String deploy_init = "&initdataService=scriptService.getCommandList(param)&initdataResult=cmds&param=" + jobName;
				link = "javascript:fn_addTab('03job', 'Deploy', 'deploy', '" + jobName + "', '" + deploy_init + "', currentSelectedTab);";
				if (isBuilding == true) {
					action = OdenConstants.IMG_TAG_DEPLOY_UNABLE;
					activeLink = false;
				} else {
					action = OdenConstants.IMG_TAG_DEPLOY;
				}

				break;
			case 2:
				link = "javascript:cleanDeploy('" + jobName + "');";
				if (isBuilding == true) {
					action = OdenConstants.IMG_TAG_CLEANDEPLOY_UNABLE;
					activeLink = false;
				} else {
					action = OdenConstants.IMG_TAG_CLEANDEPLOY;
				}
				break;
			case 3:
				link = "javascript:fn_addTab('03job', 'Compare', 'compare', '" + jobName + "', + currentSelectedTab);";
				if (g.getList().size() > 1 && isBuilding == false) {
					action = OdenConstants.IMG_TAG_COMPARE;
				} else {
					action = OdenConstants.IMG_TAG_COMPARE_UNABLE;
					activeLink = false;
				}
				break;
			case 4:
				link = "javascript:fn_addTab('03job', 'Restart', 'script', '" + jobName + "', + currentSelectedTab);";
				if (c.getList().size() > 0 && isBuilding == false) {
					action = OdenConstants.IMG_TAG_SCRIPT;
				} else {
					action = OdenConstants.IMG_TAG_SCRIPT_UNABLE;
					activeLink = false;
				}
				break;
			case 5:
				link = "javascript:delJob('" + jobName + "');";
				if (access && isBuilding == false) {
					action = OdenConstants.IMG_TAG_DEL;
				} else {
					action = OdenConstants.IMG_TAG_DEL_UNABLE;
					activeLink = false;
				}
				break;
			case 6:
				link = "javascript:rollbackJob('" + txId + "');";
				if (!"".equals(gStatus) && isBuilding == false) {
					action = OdenConstants.IMG_TAG_ROLLBACK;
				} else {
					action = OdenConstants.IMG_TAG_ROLLBACK_UNABLE;
					activeLink = false;
				}
				break;
			default:
				break;
			}

			if (activeLink) {
				result = result.concat(OdenConstants.A_HREF_HEAD + link + OdenConstants.A_HREF_MID + action + OdenConstants.A_HREF_TAIL
						+ "&nbsp;&nbsp;&nbsp;");
			} else {
				result = result.concat(action + "&nbsp;&nbsp;&nbsp;");
			}
			optCount++;
		}
		return result;
	}

	/**
	 * Method for Job Detail page.
	 * 
	 * @param param
	 * @throws Exception
	 */
	public Job findByName(String param) throws Exception {
		if ("".equals(param)) {
			return new Job();
		} else {
			String command = CommandUtil.getBasicCommand("job", "info", OdenConstants.DOUBLE_QUOTATOIN + param + OdenConstants.DOUBLE_QUOTATOIN);
			List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

			Job job = new Job();
			if (objectArray.size() == 1) { // 둘 이상 될수 없음
				job = JsonConverter.jsonToJob(objectArray.get(0));
			}
			return job;
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
	public void insert(String[] param, String[] cmds, String[] mappings, String jobname, String repository, String excludes, String groupName,
			String build) throws Exception {
		insertToOdenServer(param, cmds, mappings, jobname, repository, excludes, groupName, build);
		createRole(jobname);
	}
	
	public boolean existJob(String jobName) throws Exception{
		List<String> jobNameList = odenCommonDao.getStringList("job", "info");
		
		if(jobNameList.contains(jobName)){
			return true;
		}else{
			return false;
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
	public void update(String[] param, String[] cmds, String[] mappings, String jobname, String repository, String excludes, String groupName,
			String build) throws Exception {
		insertToOdenServer(param, cmds, mappings, jobname, repository, excludes, groupName, build);
	}

	private void insertToOdenServer(String[] param, String[] cmds, String[] mappings, String jobname, String repository, String excludes,
			String groupName, String build) throws Exception {
		JSONObject jo = new JSONObject();
		jo.put("name", jobname);

		// group 추가(14.08.12 by junghwan.hong)
		jo.put("group", groupName);
		jo.put("build", build);

		JSONObject joSource = new JSONObject();
		joSource.put("dir", repository);

		String exclu = "";
		StringTokenizer token = new StringTokenizer(excludes, ",");
		while (token.hasMoreTokens()) {
			exclu = exclu.concat(token.nextToken().trim() + ",");
		}
		if (exclu.length() != 0) {
			exclu = exclu.substring(0, exclu.length() - 1);
		}
		joSource.put("excludes", exclu);

		JSONArray mappingArray = new JSONArray();
		for (int i = 0; i < mappings.length; i++) {
			JSONObject mapping = JsonConverter.mappingToJson(mappings[i]);
			if (i == 0) {
				if (mapping.get("dir").equals(".") && mapping.get("checkout-dir").equals(".")) {
					break;
				}
			}
			mappingArray.put(mapping);
		}

		joSource.put("mappings", mappingArray);
		jo.put("source", joSource);

		JSONArray jaTarget = new JSONArray();
		for (int i = 0; i < param.length; i++) {
			JSONObject target = JsonConverter.targetToJson(param[i]);
			jaTarget.put(target);
		}
		jo.put("targets", jaTarget);

		JSONArray jaCommands = new JSONArray();
		for (int i = 0; i < cmds.length; i++) {
			JSONObject cmd = JsonConverter.commandToJson(cmds[i]);
			if (i == 0 && cmd.get("name").equals(".")) {
				break;
			}
			jaCommands.put(cmd);
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
		List<Mapping> list = new ArrayList<Mapping>();
		if (!StringUtil.isEmpty(param)) {
			String command = CommandUtil.getBasicCommand("job", "info", OdenConstants.DOUBLE_QUOTATOIN + param + OdenConstants.DOUBLE_QUOTATOIN);
			List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

			for (JSONObject object : objectArray) {
				JSONObject sources = (JSONObject) object.get("source");
				JSONArray mappings = (JSONArray) sources.get("mappings");

				if (!(mappings == null) && !"".equals(mappings)) {
					for (int n = 0; n < mappings.length(); n++) {
						JSONObject mapping = (JSONObject) mappings.get(n);

						Mapping m = JsonConverter.jsonToMapping(mapping);
						String event = OdenConstants.A_HREF_HEAD + "javascript:delSource('" + m.getHiddenname() + "');" + OdenConstants.A_HREF_MID
								+ OdenConstants.IMG_TAG_DEL + OdenConstants.A_HREF_TAIL;
						m.setHidden(event);
						list.add(m);
					}
				}
			}

		}

		if (list.isEmpty()) {
			return new Page(list, 1, list.size(), 1, 1);
		} else {
			return new Page(list, 1, list.size(), list.size(), list.size());
		}
	}

	public Page findMappings(String param) throws Exception {
		List<Mapping> list = new ArrayList<Mapping>();
		if (!StringUtil.isEmpty(param)) {
			String command = CommandUtil.getBasicCommand("job", "mapping-scan", OdenConstants.DOUBLE_QUOTATOIN + param
					+ OdenConstants.DOUBLE_QUOTATOIN);
			List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

			for (JSONObject object : objectArray) {
				Mapping m = JsonConverter.jsonToMapping(object);

				String event = OdenConstants.A_HREF_HEAD + "javascript:delSource('" + m.getHiddenname() + "');" + OdenConstants.A_HREF_MID
						+ OdenConstants.IMG_TAG_DEL + OdenConstants.A_HREF_TAIL;
				m.setHidden(event);
				list.add(m);
			}
		}

		if (list.isEmpty()) {
			return new Page(list, 1, list.size(), 1, 1);
		} else {
			return new Page(list, 1, list.size(), list.size(), list.size());
		}
	}

	/**
	 * Method to compare targets in same Job.
	 * 
	 * @param objPage
	 * @param param
	 * @param opt
	 */
	public Page compare(Object objPage, String param, String opt) throws Exception {

		int page = Integer.parseInt(String.valueOf(objPage));
		int totalNum = 0;

		String option = opt + " ";

		if (page == 0) {
			option = option.concat("-page" + " " + page);
		} else {
			option = option.concat("-page" + " " + (page - 1));
		}

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		String command = CommandUtil.getBasicCommand("job", "compare", OdenConstants.DOUBLE_QUOTATOIN + param + OdenConstants.DOUBLE_QUOTATOIN + " "
				+ option);
		List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);

		for (JSONObject object : objectArray) {
			totalNum = Integer.parseInt(object.getString("total"));
			JSONArray data = (JSONArray) object.get("data");

			if (!(data.length() == 0)) {
				for (int j = 0; j < data.length(); j++) {
					Map<String, String> map = new HashMap<String, String>();
					JSONObject dataObj = (JSONObject) data.get(j);

					String path = dataObj.getString("path");
					String equal = dataObj.getString("equal");

					String eqaulResult = "";
					if ("T".equals(equal)) {
						eqaulResult = OdenConstants.IMG_TAG_SUCCESS;
					} else {
						eqaulResult = OdenConstants.IMG_TAG_FAIL;
					}
					map.put("status", eqaulResult);
					map.put("file", path);

					JSONArray targets = (JSONArray) dataObj.get("targets");
					if (!(targets.length() == 0)) {
						for (int n = 0; n < targets.length(); n++) {
							JSONObject t = (JSONObject) targets.get(n);
							String name = t.getString("name");
							String date = DateUtil.toStringDate(Long.parseLong(t.getString("date")));
							String size = t.getString("size");
							map.put(name, date + "<br/>" + size + "byte");
						}
					}
					list.add(map);
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
	public Map<String, Object> compareHeader(String param) throws Exception {

		int numHeader = 0;

		List<String> header = new ArrayList<String>();
		header.add("STATUS");
		header.add("FILE");

		String result = odenCommonDao.getResultString("job", "info", OdenConstants.DOUBLE_QUOTATOIN + param + OdenConstants.DOUBLE_QUOTATOIN);

		if (!(result == null) && !"".equals(result)) {
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
		List<Map<String, Object>> model = new ArrayList<Map<String, Object>>();

		// status
		Map<String, Object> map_model_status = new HashMap<String, Object>();
		map_model_status.put("name", ((String) header.get(0)).toLowerCase());
		map_model_status.put("index", ((String) header.get(0)).toLowerCase());
		map_model_status.put("align", "center");
		map_model_status.put("width", "60");
		map_model_status.put("hidedlg", "true");
		map_model_status.put("sortable", false);
		model.add(map_model_status);

		// file
		Map<String, Object> map_model_file = new HashMap<String, Object>();
		map_model_file.put("name", ((String) header.get(1)).toLowerCase());
		map_model_file.put("index", ((String) header.get(1)).toLowerCase());
		map_model_file.put("align", "left");
		map_model_file.put("width", "245");
		map_model_file.put("hidedlg", "true");
		map_model_file.put("sortable", false);
		model.add(map_model_file);

		// target servers
		for (int i = 2; i < numHeader + 2; i++) {
			Map<String, Object> map_model = new HashMap<String, Object>();
			map_model.put("name", header.get(i));
			map_model.put("index", header.get(i));
			map_model.put("align", "center");
			map_model.put("width", "180");
			map_model.put("resizable", true);
			map_model.put("sortable", false);
			model.add(map_model);
		}
		Map<String, Object> map_result = new HashMap<String, Object>();
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
		odenCommonDao.remove("_job", OdenConstants.DOUBLE_QUOTATOIN + name + OdenConstants.DOUBLE_QUOTATOIN);
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

	/**
	 * Methode for downloading excel file with job comparing information.
	 * 
	 * @param param
	 * @throws Exception
	 */
	public List<Map<String, String>> excel(String param) throws Exception {

		String command = CommandUtil.getBasicCommand("job", "compare", OdenConstants.DOUBLE_QUOTATOIN + param + OdenConstants.DOUBLE_QUOTATOIN);
		List<JSONObject> objectArray = odenCommonDao.jsonObjectArrays(command);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		for (JSONObject object : objectArray) {
			JSONArray data = (JSONArray) object.get("data");

			if (!(data.length() == 0)) {

				for (int j = 0; j < data.length(); j++) {
					Map<String, String> map = new HashMap<String, String>();
					JSONObject dataObj = (JSONObject) data.get(j);

					String path = dataObj.getString("path");
					String equal = dataObj.getString("equal");

					map.put("status", equal);
					map.put("file", path);

					JSONArray targets = (JSONArray) dataObj.get("targets");
					if (!(targets.length() == 0)) {
						for (int n = 0; n < targets.length(); n++) {
							JSONObject t = (JSONObject) targets.get(n);
							String name = t.getString("name");
							String date = DateUtil.toStringDate(Long.parseLong(t.getString("date")));
							String size = t.getString("size");
							map.put(name, date + " " + size + "byte");
						}
					}
					list.add(map);
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
		List<String> roles = new ArrayList<String>();
		roles.add("ROLE_ADMIN");
		return odenCommonDao.findJob("job", "info", roles);
	}
	
	/**
	 * Get build Name
	 * 
	 * @return
	 * @throws Exception
	 */
	
	public String getBuildByobjectArray(List<JSONObject> objectArray) throws Exception {
		String buildName = null;
		for (JSONObject object : objectArray) {
			buildName = (String) object.get("build");

		}
		return buildName;
	}
	
	/**
	 * Get build Check
	 * 
	 * @return
	 * @throws Exception
	 */
	
	public boolean getBuildCheck() throws Exception {
		boolean buildCheck = false;
		String buildCmd = CommandUtil.getBasicCommand("build", "check");
		List<JSONObject> objectArr = odenCommonDao.jsonObjectArrays(buildCmd);
		for (JSONObject object : objectArr) {
			buildCheck = (Boolean) object.get("serverStatus");

		}
		return buildCheck;
	}
}