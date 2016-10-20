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
package org.anyframe.oden.bundle.build;

import java.io.PrintStream;
import java.util.List;

import org.anyframe.oden.bundle.build.config.CfgBuildJob;
import org.anyframe.oden.bundle.build.config.CfgRunJob;
import org.anyframe.oden.bundle.build.log.BuildLogService;
import org.anyframe.oden.bundle.build.util.HudsonRemoteAPI;
import org.anyframe.oden.bundle.common.DateUtil;
import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.common.StringUtil;
import org.anyframe.oden.bundle.core.command.Cmd;
import org.anyframe.oden.bundle.core.command.JSONUtil;
import org.anyframe.oden.bundle.gate.CustomCommand;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * This is BuildCommandImpl Class
 * 
 * @author Junghwan Hong
 */
public class BuildCommandImpl implements CustomCommand {

	private HudsonRemoteAPI hudson = new HudsonRemoteAPI();

	private String buildUrl = "";

	BuildLogService buildLogger;

	protected void setBuildLogService(BuildLogService buildLogger) {
		this.buildLogger = buildLogger;
	}

	public String getName() {
		return "build";
	}

	public String getUsage() {
		return "build help";
	}

	public String getShortDescription() {
		return "build files";
	}

	public void execute(String line, PrintStream out, PrintStream err) {
		boolean isJSON = false;
		// get build.url(ex.http://localhost:9090)
		BundleContext context = FrameworkUtil.getBundle(this.getClass())
				.getBundleContext();

		if (context.getProperty("build.url") != null) {
			buildUrl = context.getProperty("build.url");
			hudson.setHudsonURL(buildUrl);
		}

		try {
			Cmd cmd = new Cmd(line);
			isJSON = cmd.getOption(Cmd.JSON_OPT) != null;

			if (cmd.getAction().length() == 0 || cmd.getAction().equals("help")) {
				out.println(getFullUsage());
				return;
			}

			out.println(execute(cmd, isJSON));

		} catch (Exception e) {
			err.println(isJSON ? JSONUtil.jsonizedException(e) : e.getMessage());
			Logger.error(e);
		}

	}

	@SuppressWarnings("PMD")
	private String execute(Cmd cmd, boolean isJSON) throws Exception {
		// check ci engine url
		if ("".equals(buildUrl) || buildUrl.indexOf("http://") < 0) {
			throw new OdenException("Check the build server url.");
		}
		String action = cmd.getAction();

		if ("info".equals(action)) {
			if (StringUtil.empty(cmd.getActionArg())) {
				// 계정이 필요하면, 기능 추가가 필요
				List<String> elements = hudson.getJobListWithAuth();

				if (isJSON) {
					return new JSONArray(elements).toString();
				}
				StringBuffer buf = new StringBuffer();
				for (String element : elements) {
					buf.append(element + "\n");
				}
				return buf.toString();
			}
			CfgBuildJob job = hudson.getJob(cmd.getActionArg());

			if (job == null) {
				throw new OdenException("Invalid Job Name: "
						+ cmd.getActionArg());
			}
			if (isJSON) {
				return job.toJSON().toString();
			}

			StringBuffer buf = new StringBuffer();
			buf.append("name: " + job.getName() + "\n");
			buf.append("type: " + job.getType() + "\n");
			buf.append("workspace: " + job.getWorkspace() + "\n");
			buf.append("scm: " + job.getScm() + "\n");
			buf.append("scmurl: " + job.getScmurl() + "\n");
			buf.append("schedule: " + job.getSchedule() + "\n");
			buf.append("otherproject: " + job.getOtherproject() + "\n");
			return buf.toString();
		} else if ("run".equals(action)) {
			final CfgBuildJob job = hudson.getJob(cmd.getActionArg());

			if (job == null) {
				throw new OdenException("Invalid Job Name: "
						+ cmd.getActionArg());
			}
			CfgRunJob bjob = hudson.executeBuild(cmd.getActionArg());

			if (isJSON) {
				JSONObject jo = new JSONObject();
				jo.put("name", bjob.getName());
				jo.put("buildNo", bjob.getBuildNo());
				jo.put("consoleUrl", bjob.getConsoleUrl());

				return new JSONArray().put(jo).toString();
			}

			StringBuffer buf = new StringBuffer();
			buf.append("name: " + bjob.getName() + "\n");
			buf.append("buildNo: " + bjob.getBuildNo() + "\n");
			buf.append("consoleUrl: " + bjob.getConsoleUrl());

			return buf.toString();
		} else if ("log".equals(action)) {
			String jobName = cmd.getActionArg();
			if ("".equals(jobName)) {
				throw new OdenException("Invalid Job Name: "
						+ cmd.getActionArg());
			}
// ODEN 자체 DB가 아니라 Jenkins 이력으로 변경
//			BrecordElement r = buildLogger.search(jobName);
//
//			if (isJSON) {
//				if(r.getId() == null) {
//					return "";
//				}
//				
//				JSONObject jo = new JSONObject();
//				if (r.getId() != null) {
//					jo.put("id", r.getId());
//					jo.put("jobName", r.getJobName());
//					jo.put("date", r.getDate());
//					jo.put("buildNo", r.getBuildNo());
//					jo.put("success", r.isSuccess());
//					jo.put("consoleUrl", buildUrl + "/job/" + r.getJobName()
//							+ "/" + r.getBuildNo() + "/console");
//				}
//				return new JSONArray().put(jo).toString();
//			}
//
//			StringBuffer buf = new StringBuffer();
//
//			if (r.getId() != null) {
//				buf.append("id: " + r.getId() + "\n");
//				buf.append("jobName: " + r.getJobName() + "\n");
//				buf.append("date: " + r.getDate() + "\n");
//				buf.append("buildNo: " + r.getBuildNo() + "\n");
//				buf.append("success: " + r.isSuccess() + "\n");
//				buf.append("consoleUrl: " + buildUrl + "/job/" + r.getJobName()
//						+ "/" + r.getBuildNo() + "/console" + "\n");
//			}
			
			List<CfgRunJob> builds = hudson.getStatus(jobName);
			
			if(builds.size() == 0) {
				return "";
			}
			CfgRunJob r = (CfgRunJob)builds.get(0);
			
			if (isJSON) {
				JSONObject jo = new JSONObject();
				
				jo.put("jobName", jobName);
				jo.put("date", r.getTimeStamp());
				jo.put("buildNo", r.getBuildNo());
				jo.put("success", r.getStatus().equals("S")? "true": "false");
				jo.put("consoleUrl", buildUrl + "/job/" + jobName
						+ "/" + r.getBuildNo() + "/console");
				
				return new JSONArray().put(jo).toString();
			}
			
			StringBuffer buf = new StringBuffer();
			
			if (builds.size() > 0) {
				String isSuccess = r.getStatus().equals("S")? "true": "false";
				buf.append("jobName: " + jobName + "\n");
				buf.append("date: " + r.getTimeStamp() + "\n");
				buf.append("buildNo: " + r.getBuildNo() + "\n");
				buf.append("success: " +  isSuccess + "\n");
				buf.append("consoleUrl: " + buildUrl + "/job/" + jobName
						+ "/" + r.getBuildNo() + "/console" + "\n");
			}
			return buf.toString();
		} else if ("status".equals(action)) {
			if (StringUtil.empty(cmd.getActionArg())) {
				List<CfgRunJob> builds = hudson.getStatus("");

				if (isJSON) {
					if(builds.size() == 0 ) {
						return "";
					}
					JSONArray list = toJSONArray(builds);
//					addBuildHistory(builds);
					return list.toString();
				}
				StringBuffer buf = new StringBuffer();

				for (CfgRunJob build : builds) {
					String timeStamp = "";
					if (!build.getTimeStamp().equals("")) {
						timeStamp = DateUtil.toStringDate(Long.valueOf(build
								.getTimeStamp()));
					}

					buf.append(build.getName() + "\t" + build.getBuildNo()
							+ "\t" + build.getStatus() + "\t" + timeStamp
							+ "\t" + build.getConsoleUrl() + "\n");
				}
//				addBuildHistory(builds);
				return buf.toString();
			}

			List<CfgRunJob> builds = hudson.getStatus(cmd.getActionArg());

			if (builds.size() == 0) {
				throw new OdenException("Invalid Job Name: "
						+ cmd.getActionArg());
			}
			CfgRunJob build = builds.get(0);

			String timeStamp = "";
			if (!build.getTimeStamp().equals("")) {
				timeStamp = DateUtil.toStringDate(Long.valueOf(build
						.getTimeStamp()));
			}
			if (isJSON) {
				JSONObject jo = new JSONObject();

				jo.put("jobName", build.getName());
				jo.put("buildNo", build.getBuildNo());
				jo.put("status", build.getStatus());
				jo.put("date", timeStamp);
				jo.put("consoleUrl", build.getConsoleUrl());

//				addBuildHistory(builds);

				return new JSONArray().put(jo).toString();
			}

			StringBuffer buf = new StringBuffer();

			buf.append("jobName: " + build.getName() + "\n");
			buf.append("buildNo: " + build.getBuildNo() + "\n");
			buf.append("status: " + build.getStatus() + "\n");
			buf.append("date: " + timeStamp + "\n");
			buf.append("consoleUrl: " + build.getConsoleUrl() + "\n");

//			addBuildHistory(builds);

			return buf.toString();
		} else if ("check".equals(action)) {
			// build.url 이 oden.ini에 존재하는지 check 또는 build server가 구동중인지 확인해서
			// true, false를 리턴
			boolean status = hudson.checkBuildServer();
			
			if (isJSON) {
				JSONObject jo = new JSONObject();
				jo.put("serverStatus", status);
				
				return new JSONArray().put(jo).toString();
			}

			StringBuffer buf = new StringBuffer();
			
			buf.append("serverStatus: " +status + "\n");
			
			return buf.toString();
			
		} else {
			throw new OdenException("Invalid Action: " + action);
		}
	}

	@SuppressWarnings("PMD")
	private JSONArray toJSONArray(List<CfgRunJob> builds) throws JSONException {
		JSONArray list = new JSONArray();

		for (CfgRunJob build : builds) {
			String timeStamp = "";
			if (!build.getTimeStamp().equals("")) {
				timeStamp = DateUtil.toStringDate(Long.valueOf(build
						.getTimeStamp()));
			}
			list.put(new JSONObject().put("jobName", build.getName())
					.put("buildNo", build.getBuildNo())
					.put("status", build.getStatus()).put("date", timeStamp)
					.put("consoleUrl", build.getConsoleUrl()));
		}

		return list;
	}

	@SuppressWarnings("PMD")
//	private void addBuildHistory(List<CfgRunJob> builds) throws Exception {
//		for (CfgRunJob build : builds) {
//			BrecordElement r;
//			try {
//				r = buildLogger.search(build.getName());
//			} catch (OdenException e) {
//				// 최초일 경우 insert
//				long tm = System.currentTimeMillis();
//
//				BrecordElement rr = new BrecordElement(String.valueOf(tm),
//						build.getName(), tm, build.getBuildNo(),
//						"S".equals(build.getStatus()) ? true : false);
//				buildLogger.record(rr);
//				throw e;
//			}
//			if (!StringUtil.empty(r.getBuildNo())) {
//				if (!r.getBuildNo().equals(build.getBuildNo())
//						&& !("B".equals(build.getStatus()) || "N".equals(build
//								.getStatus()))) {
//					// 다를 경우 update
//					long tm = System.currentTimeMillis();
//
//					BrecordElement rr = new BrecordElement(String.valueOf(tm),
//							build.getName(), tm, build.getBuildNo(),
//							"S".equals(build.getStatus()) ? true : false);
//					buildLogger.record(rr);
//				}
//			} else {
//				// 다를 경우 update
//				if (!("B".equals(build.getStatus()) || "N".equals(build
//						.getStatus()))) {
//					long tm = System.currentTimeMillis();
//
//					BrecordElement rr = new BrecordElement(String.valueOf(tm),
//							build.getName(), tm, build.getBuildNo(),
//							"S".equals(build.getStatus()) ? true : false);
//					buildLogger.record(rr);
//				}
//			}
//		}
//	}

	public String getFullUsage() {
		return "build info [ <job> ]" + "\nbuild run <job>"
				+ "\nbuild log <job>" + "\nbuild status [ <job> ]" + "\nbuild check";
	}
}
