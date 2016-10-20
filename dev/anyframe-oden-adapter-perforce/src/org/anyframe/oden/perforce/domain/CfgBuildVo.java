package org.anyframe.oden.perforce.domain;

import java.util.List;

public class CfgBuildVo {
	
	private String userId;
	private String pwd;
	private String dbName;
	private String dbConnection;
	private String server;
	private String productName;
	private String projectName;
	private List<CfgBuildDetail> request;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getDbConnection() {
		return dbConnection;
	}
	public void setDbConnection(String dbConnection) {
		this.dbConnection = dbConnection;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public List<CfgBuildDetail> getRequest() {
		return request;
	}
	public void setRequest(List<CfgBuildDetail> request) {
		this.request = request;
	}	
}
