package org.anyframe.oden.dimmension;

import org.anyframe.oden.dimmension.domain.BuildInfo;
import org.apache.tools.ant.Task;

public class DimensionAdapter extends Task {
	private String userId;
	private String password;
	private String dbName;
	private String dbConnection;
	private String server;
	private String requestId;
	private String productName;
	private String projectName;
	private String targetPath;
	

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public void setDbConnection(String dbConnection) {
		this.dbConnection = dbConnection;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	
	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public void execute() {
		try {
			// 0. transfer vo object
			System.out.println("Request: " + requestId);
			BuildInfo build = new BuildInfo(userId, password, dbName, dbConnection,
					server, convertJson(requestId), productName, projectName, targetPath);
			System.out.println("Request: " + build.getRequestId());
			
			// 1. checkout task
			CheckOut checkout = new CheckOut();
		
			checkout.downLoad(build);
		} catch (Exception e) {
			getProject().fireBuildFinished(e);
			System.exit(-1);
		}
	}

	private String convertJson(String request) {
		String trans = request;

		trans = trans.replace("[{", "[{\"");
		trans = trans.replace(":", "\":\"");
		trans = trans.replace(",", "\",\"");
		trans = trans.replace("}", "\"}");
		
		trans = trans.replace("\"{", "{\"");
		trans = trans.replace("}\"", "}");
		
		return trans;
	}
}
