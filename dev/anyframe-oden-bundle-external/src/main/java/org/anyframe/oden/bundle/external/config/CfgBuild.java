/*
 * Copyright 2002-2014 the original author or authors.
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
package org.anyframe.oden.bundle.external.config;

import java.io.Serializable;
import java.util.List;

/**
 * This is CfgBuild Class
 * 
 * @author Junghwan Hong
 */
public class CfgBuild implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String address;

	String userId;

	String pwd;

	String dbName;

	String dbConnection;

	String server;

	String productName;

	String projectName;

	String buildName;

	List<CfgBuildDetail> request;

	String repoPath;
	
	String packageType;
	
	String packageName;

	public CfgBuild(String address, String userId, String pwd, String dbName,
			String dbConnection, String server, String productName,
			String projectName, String buildName, List<CfgBuildDetail> request,
			String repoPath, String packageType, String packageName) {
		this.address = address;
		this.userId = userId;
		this.pwd = pwd;
		this.dbName = dbName;
		this.dbConnection = dbConnection;
		this.server = server;
		this.productName = productName;
		this.projectName = projectName;
		this.buildName = buildName;
		this.request = request;
		this.repoPath = repoPath;
		this.packageType = packageType;
		this.packageName = packageName;
	}

	public String getAddress() {
		return address;
	}

	public String getUserId() {
		return userId;
	}

	public String getPwd() {
		return pwd;
	}

	public String getDbName() {
		return dbName;
	}

	public String getDbConnection() {
		return dbConnection;
	}

	public String getServer() {
		return server;
	}

	public String getProductName() {
		return productName;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getBuildName() {
		return buildName;
	}

	public List<CfgBuildDetail> getRequest() {
		return request;
	}

	public String getRepoPath() {
		return repoPath;
	}
	
	public String getPackageType() {
		return packageType;
	}
	
	public String getPackageName() {
		return packageName;
	}
}
