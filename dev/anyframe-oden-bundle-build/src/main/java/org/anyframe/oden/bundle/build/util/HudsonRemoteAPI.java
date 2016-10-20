/*   
 * Copyright 2008-2013 the original author or authors.   
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
package org.anyframe.oden.bundle.build.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.anyframe.oden.bundle.build.config.CfgBuildJob;
import org.anyframe.oden.bundle.build.config.CfgPmdDetail;
import org.anyframe.oden.bundle.build.config.CfgPmdReturnVO;
import org.anyframe.oden.bundle.build.config.CfgRunJob;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * This is a JenkinsRemoteAPI class.
 * 
 * @author Junghwan Hong
 */
public class HudsonRemoteAPI {

	// private VelocityEngine velocity;

	private static String hudsonURL;

	private Element jobConfigElement;

	// public static final String ID = CodeGeneratorActivator.PLUGIN_ID;

	public HudsonRemoteAPI() {
	}

	public void setHudsonURL(String hudsonURL) {
		if (!hudsonURL.endsWith("/")) {
			hudsonURL += "/";
		}
		this.hudsonURL = hudsonURL;
	}

	@SuppressWarnings("unchecked")
	public List<String> getJobList() throws JDOMException, IOException {
		List<String> rtnList = new ArrayList<String>();

		URL url = new URL(hudsonURL + "api/xml");

		SAXBuilder builder = new SAXBuilder(false);
		Document dom = builder.build(url);

		List<Element> elements = dom.getRootElement().getChildren("job");

		for (Element element : elements) {
			rtnList.add(element.getChildText("name"));
		}

		return rtnList;
	}

	public List<String> getJobListWithAuth() throws Exception {
		List<String> rtnList = new ArrayList<String>();

		HttpClient client = new HttpClient();
		SAXBuilder builder = new SAXBuilder(false);
		Document dom = null;

		client.getParams().setAuthenticationPreemptive(true);
		Credentials defaultcreds = new UsernamePasswordCredentials("admin",
				"admin0");

		client.getState().setCredentials(
				new AuthScope("localhost", 9090, AuthScope.ANY_REALM),
				defaultcreds);

		// GetMethod get = new GetMethod("http://70.121.244.11:38080/" +
		// "api/xml");
		GetMethod get = new GetMethod("http://localhost:9090/" + "api/xml");
		get.setDoAuthentication(true);

		try {
			// execute the GET
			int status = client.executeMethod(get);

			if (status == 200) {
				dom = builder.build(get.getResponseBodyAsStream());
			}
		} finally {
			// release any connection resources used by the method
			get.releaseConnection();
		}

		if (dom != null) {
			List<Element> elements = dom.getRootElement().getChildren("job");

			for (Element element : elements) {
				rtnList.add(element.getChildText("name"));
			}
		}
		return rtnList;

	}

	public static List<Element> getJobDetail(String jobName)
			throws JDOMException, IOException {

		if (!jobName.endsWith("/")) {
			jobName += "/";
		}

		URL url = new URL(hudsonURL + "job/" + jobName + "api/xml");
		SAXBuilder builder = new SAXBuilder(false);

		Document dom = builder.build(url);
		List<Element> elements = dom.getRootElement().getChildren("lastBuild");

		return elements;
	}

	public List<CfgRunJob> getStatus(String jobName) throws Exception {
		List<String> jobs = new ArrayList<String>();
		if ("".equals(jobName)) {
			jobs = getJobList();
		} else {
			jobs.add(jobName);
		}

		List<CfgRunJob> rtnList = new ArrayList<CfgRunJob>();

		for (String job : jobs) {
			CfgRunJob runJob = new CfgRunJob();
			// job
			runJob.setName(job);
			
			job = URLEncoder.encode(job).replace("+", "%20");
			URL url = new URL(hudsonURL + "job/" + job + "/lastBuild/api/xml");

			SAXBuilder builder = new SAXBuilder(false);
			Document dom = null;
			try {
				dom = builder.build(url);
			} catch (IOException e) {
				// build job은 존재하나 구동 정보가 없을 경우
				runJob.setName(job);
				// 빌드 상태 None
				runJob.setStatus("N");
				runJob.setBuildNo("");
				runJob.setConsoleUrl("");
				runJob.setTimeStamp("");
				rtnList.add(runJob);
				continue;
			}
			// 빌드#
			runJob.setBuildNo(dom.getRootElement().getChildText("number"));
			// 구동여부(Success:S, Failure:F, Building: B)
			if ("true".equals(dom.getRootElement().getChildText("building"))) {
				runJob.setStatus("B");
			} else {
				if ("FAILURE".equals(dom.getRootElement()
						.getChildText("result"))) {
					runJob.setStatus("F");
				} else {
					runJob.setStatus("S");
				}
			}
			// consoleUrl
			runJob.setConsoleUrl(dom.getRootElement().getChildText("url")
					+ "console");
			// timeStamp
			runJob.setTimeStamp(dom.getRootElement().getChildText("timestamp"));
			rtnList.add(runJob);
		}
		return rtnList;

	}

	public static int getStatusWithArg(String address, String buildName,
			String buildNo) throws Exception {
		buildName = URLEncoder.encode(buildName).replace("+", "%20");
		URL url = new URL(address + "/job/" + buildName + "/" + buildNo
				+ "/api/xml");

		SAXBuilder builder = new SAXBuilder(false);

		Document dom = null;
		try {
			dom = builder.build(url);
		} catch (IOException e) {
			// build job은 존재하나 구동 정보가 없을 경우
			return 0;
		}

		// build no 일치 여부(미구동)
		if (!buildNo.equals(dom.getRootElement().getChildText("number"))) {
			return 0;
		}
		// 구동여부(Success:1, Failure:-1, Building: 0)
		if ("true".equals(dom.getRootElement().getChildText("building"))) {
			return 0;
		} else {
			if ("FAILURE".equals(dom.getRootElement().getChildText("result"))) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	public CfgBuildJob getJob(String jobName) throws Exception {
		CfgBuildJob job = new CfgBuildJob();

		Element jobConfigElement = this.getJobConfigXml(jobName);
		// name
		job.setName(jobName);
		// type (나중에 정리할것 필요없을듯)
		if (jobName.endsWith("build")) {
			job.setType("build");
		} else {
			job.setType("report");
		}

		// scm type
		switch (this.getScmTypeMapping(jobConfigElement)) {
		case 0:
			job.setScm("subversion");
			break;
		case 1:
			job.setScm("cvs");
			break;
		default:
			job.setScm("none");
			break;
		}
		// workspace/ scm url/schedule/otherproject
		job.setWorkspace(this.getCustomWorkspace(jobConfigElement));
		job.setScmurl(this.getScmURL(jobConfigElement));
		job.setSchedule(this.getSchedule(jobConfigElement));
		job.setOtherproject(this.getChildProject(jobConfigElement));

		return job;
	}

	public Element getJobConfigXml(String jobName) throws JDOMException,
			IOException {

		URL url = new URL(hudsonURL
				+ "anyframe/api?service=getJobConfig&jobName=" + jobName);
		SAXBuilder builder = new SAXBuilder(false);

		return builder.build(url).getRootElement();
	}

	public CfgRunJob executeBuild(String jobName) throws Exception {
		CfgRunJob runJob = new CfgRunJob();
		runJob.setName(jobName);
		
		jobName = URLEncoder.encode(jobName).replace("+", "%20");
		String uri = hudsonURL + "job/" + jobName + "/build";

//		GetMethod method = new GetMethod(uri.replaceAll(" ", "%20"));
		GetMethod method = new GetMethod(uri);
		method.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=UTF-8");
		
		try {
			HttpClient httpClient = new HttpClient();
			int statusCode = httpClient.executeMethod(method);

			// if (statusCode == HttpStatus.SC_OK || statusCode == 201) {
			// throw new Exception(method.getResponseBodyAsString());
			// }

		} catch (Exception e) {
			throw e;

		} finally {
			method.releaseConnection();
		}
		// get last build(number, url)
		List<Element> elements = getJobDetail(jobName);
		
		if (elements.size() == 0) {
			runJob.setBuildNo("1");
			runJob.setConsoleUrl(hudsonURL + "job/"
					+ jobName + "/" + "1" + "/console");
			return runJob;
		}

		for (Element element : elements) {
			String buildNo = String.valueOf(Integer.parseInt(element
					.getChildText("number")) + 1);
			runJob.setBuildNo(buildNo);
			runJob.setConsoleUrl(hudsonURL + "job/" + jobName + "/" + buildNo
					+ "/console");
		}

		return runJob;
	}

	public static CfgRunJob executeBuildWithArg(String address, String userId,
			String pwd, String dbName, String dbConnection, String server,
			String productName, String projectName, String buildName,
			String request, String repoPath, boolean isPmd) throws Exception {

		CfgRunJob runJob = new CfgRunJob();
		runJob.setName(buildName);
		
		// String url = URLEncoder.encode(address + "/job/" + buildName
		// + "/buildWithParameters");
		buildName = URLEncoder.encode(buildName).replace("+", "%20");
		PostMethod method = new PostMethod(address + "/job/"
				+ buildName + "/buildWithParameters");
		method.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=UTF-8");
		method.addParameter("USER.ID", userId);
		method.addParameter("PWD", pwd);
		method.addParameter("DM.DB.NAME", dbName);
		method.addParameter("DB.CONNECTION", dbConnection);
		method.addParameter("DM.SERVER", server);
		method.addParameter("PRODUCT.NAME", productName);
		method.addParameter("DM.PROJECT.NAME", projectName);
		method.addParameter("REQUEST", request);
		method.addParameter("REPOSITORY.PATH", repoPath);
		
		if (isPmd) {
			method.addParameter("TARGET", "pmd.run");
		}
		hudsonURL = address + "/";

		String buildNo = getLastBuildNo(buildName);

		 try {
			HttpClient httpClient = new HttpClient();
			int statusCode = httpClient.executeMethod(method);

			// if (statusCode == HttpStatus.SC_OK || statusCode == 201) {
			// throw new Exception(method.getResponseBodyAsString());
			// }

		} catch (Exception e) {
			throw e;

		} finally {
			method.releaseConnection();
		}

		// fixed build no setting
		buildNo = getBuildNo(buildNo, buildName);
		runJob.setBuildNo(buildNo);

		runJob.setConsoleUrl(hudsonURL + "job/" + buildName + "/" + buildNo
				+ "/console");

		return runJob;

	}

	private static String getLastBuildNo(String buildName) throws Exception {
		List<Element> elements = getJobDetail(buildName);
		// 처음 빌드시 1
		String buildNo = "1";

		for (Element element : elements) {
			buildNo = String.valueOf(Integer.parseInt(element
					.getChildText("number")) + 1);
			if (!buildNo.equals("")) {
				return buildNo;
			}
		}

		return buildNo;
	}

	private static String getBuildNo(String buildNo, String buildName)
			throws Exception {
		URL url = new URL(hudsonURL + "job/" + buildName + "/lastBuild/api/xml");

		SAXBuilder builder = new SAXBuilder(false);

		Document dom = null;

		for (int i = 0; i < 100; i++) {
			try {
				dom = builder.build(url);
			} catch (IOException e) {
				// 처음 구동
				continue;
			}
			// build no 일치 여부확인, 일치화면 buildNo Return
			if (buildNo.equals(dom.getRootElement().getChildText("number"))) {
				return buildNo;
			}
			Thread.sleep(1000L);
		}
		return buildNo;
	}

	public static CfgRunJob rollbackBuildWithArg(String address,
			String buildName, String build) throws Exception {

		CfgRunJob runJob = new CfgRunJob();
		runJob.setName(buildName);
		
		buildName = URLEncoder.encode(buildName).replace("+", "%20");
		
		PostMethod method = new PostMethod(address + "/job/" + buildName
				+ "/buildWithParameters");

		method.addParameter("TARGET", "rollback.run");
		method.addParameter("REQUEST", build);
		try {
			HttpClient httpClient = new HttpClient();
			int statusCode = httpClient.executeMethod(method);

		} catch (Exception e) {
			throw e;

		} finally {
			method.releaseConnection();
		}
		hudsonURL = address + "/";

		// get last build(number, url)
		List<Element> elements = getJobDetail(buildName);
		
		for (Element element : elements) {
			String buildNo = String.valueOf(Integer.parseInt(element
					.getChildText("number")) + 1);

			runJob.setBuildNo(buildNo);
			runJob.setConsoleUrl(hudsonURL + "job/" + buildName + "/" + buildNo
					+ "/console");
		}

		return runJob;

	}

	public static CfgPmdReturnVO returnPmd(String address, String buildName,
			String buildNo) throws Exception {
		buildName = URLEncoder.encode(buildName).replace("+", "%20");
		
		if (!buildName.endsWith("/")) {
			buildName += "/";
		}

		URL url = new URL(address + "/job/" + buildName + buildNo
				+ "/pmdResult/api/xml?depth=1");
		SAXBuilder builder = new SAXBuilder(false);

		Document dom = builder.build(url);
		List<Element> elements = dom.getRootElement().getChildren("warning");

		List<CfgPmdDetail> highDetail = new ArrayList<CfgPmdDetail>();
		List<CfgPmdDetail> normalDetail = new ArrayList<CfgPmdDetail>();
		List<CfgPmdDetail> lowDetail = new ArrayList<CfgPmdDetail>();

		for (Element element : elements) {

			String priority = element.getChildText("priority");

			String fileName = element.getChildText("fileName");
			fileName = fileName.substring(fileName.lastIndexOf("/"));

			String message = element.getChildText("message");

			int lineNumber = Integer.parseInt(element
					.getChildText("primaryLineNumber"));

			CfgPmdDetail deail = new CfgPmdDetail(fileName, lineNumber, message);

			if ("HIGH".equals(priority)) {
				highDetail.add(deail);
			} else if ("NORMAL".equals(priority)) {
				normalDetail.add(deail);
			} else if ("LOW".equals(priority)) {
				lowDetail.add(deail);
			}

		}

		CfgPmdReturnVO rtnVo = new CfgPmdReturnVO(highDetail.size(),
				normalDetail.size(), lowDetail.size(), highDetail,
				normalDetail, lowDetail);

		return rtnVo;
	}

	public String getCustomWorkspace(Element jobConfig) {
		if (jobConfig.getChildText("customWorkspace") == null) {
			return "";
		}
		return jobConfig.getChildText("customWorkspace");
	}

	public int getScmTypeMapping(Element jobConfig) {
		try {
			String scmClass = jobConfig.getChild("scm").getAttribute("class")
					.getValue();
			if ("hudson.scm.SubversionSCM".equals(scmClass)) {
				return 0;
			} else if ("hudson.scm.CVSSCM".equals(scmClass)) {
				return 1;
			}

		} catch (Exception e) {
			// PluginLoggerUtil.error(ID,
			// Message.view_exception_failtogetscmtype, e);
		}
		return 2;
	}

	public String getScmURL(Element jobConfig) {
		try {
			String ret = null;
			int type = getScmTypeMapping(jobConfig);
			if (type == 0) {
				ret = jobConfig.getChild("scm").getChild("locations")
						.getChild("hudson.scm.SubversionSCM_-ModuleLocation")
						.getChildText("remote");

			} else if (type == 1) {
				ret = jobConfig.getChild("scm").getChildText("cvsroot");
			}
			if (ret == null) {
				ret = "";
			}
			return ret;

		} catch (Exception e) {
			// PluginLoggerUtil.error(ID,
			// Message.view_exception_failtogetscmurl, e);
		}

		return "";
	}

	@SuppressWarnings("unchecked")
	public String getSchedule(Element jobConfig) {
		try {
			List triggers = jobConfig.getChild("triggers").getChildren();
			if (triggers.size() > 0) {
				Element elem = (Element) triggers.get(0);
				if (elem.getChildText("spec") == null) {
					return "";
				}
				return elem.getChildText("spec");
			}

		} catch (Exception e) {
			// PluginLoggerUtil.error(ID,
			// Message.view_exception_failtogetschedule, e);
		}
		return "";
	}

	public String getChildProject(Element jobConfig) {
		try {
			if (jobConfig.getChild("publishers").getChild(
					"hudson.tasks.BuildTrigger") == null) {
				return "";
			}
			return jobConfig.getChild("publishers")
					.getChild("hudson.tasks.BuildTrigger")
					.getChildText("childProjects");

		} catch (Exception e) {
			// PluginLoggerUtil.error(ID,
			// Message.view_exception_failtogetchildproject, e);
		}

		return "";
	}

}
