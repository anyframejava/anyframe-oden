package org.anyframe.oden.dimmension.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class BuildInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userId;

	private String password;

	private String dbName;

	private String dbConnection;

	private String server;

	private String requestId;

	private String productName;

	private String projectName;

	private String targetPath;

	private String srcRoot;
	
	private String resRoot;

	private String webRoot;
	
	private String srcAppd;
	
	private String webAppd;
	
	private String jarWebRoot;
	
	private String packageName;
	
	private String classesRoot;
	
	private String libsRoot;
	
	private String classesRoot2;
	
	private String libsRoot2;
	
	private String classesRoot3;
	
	private String libsRoot3;
	
	private String reference;
	
	private String encoding;

	public BuildInfo(String projectName, String requestId, String srcRoot, String resRoot,
			String webRoot, String srcAppd, String webAppd, String jarWebRoot, String packageName, String classesRoot, String libsRoot, String classesRoot2, String libsRoot2, String classesRoot3, String libsRoot3, String reference, String encoding) {
		this.projectName = projectName;
		this.requestId = requestId;
		this.srcRoot = srcRoot;
		this.resRoot = resRoot;		
		this.webRoot = webRoot;
		this.srcAppd = srcAppd;
		this.webAppd = webAppd;
		this.jarWebRoot = jarWebRoot;
		this.packageName = packageName;
		this.classesRoot = classesRoot;
		this.libsRoot = libsRoot;
		this.classesRoot2 = classesRoot2;
		this.libsRoot2 = libsRoot2;
		this.classesRoot3 = classesRoot3;
		this.libsRoot3 = libsRoot3;
		this.reference = reference;
		this.encoding = encoding;
	}

	public BuildInfo(String userId, String password, String dbName,
			String dbConnection, String server, String requestId,
			String productName, String projectName, String targetPath) {
		this.userId = userId;
		this.password = password;
		this.dbName = dbName;
		this.dbConnection = dbConnection;
		this.server = server;
		this.requestId = requestId;
		this.productName = productName;
		this.projectName = projectName;
		this.targetPath = targetPath;
	}

	public String getUserId() {
		return userId;
	}

	public String getPassword() {
		return password;
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

	public String getRequestId() {
		return requestId;
	}

	public String getProductName() {
		return productName;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getSrcRoot() {
		return srcRoot;
	}
	
	public String getResRoot() {
		return resRoot;
	}

	public String getWebRoot() {
		return webRoot;
	}

	public String getTargetPath() {
		return targetPath;
	}
	
	public String getSrcAppd() {
		return srcAppd;
	}

	public String getWebAppd() {
		return webAppd;
	}	
	
	public String getJarWebRoot() {
		return jarWebRoot;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public String getClassesRoot() {
		return classesRoot;
	}
	
	public String getLibsRoot() {
		return libsRoot;
	}
	
	public String getClassesRoot2() {
		return classesRoot2;
	}
	
	public String getLibsRoot2() {
		return libsRoot2;
	}
	
	public String getClassesRoot3() {
		return classesRoot3;
	}
	
	public String getLibsRoot3() {
		return libsRoot3;
	}
	
	public String getReference() {
		return reference;
	}
	
	public String getEncoding() {
		return encoding;
	}

	public List toBuildObject() throws Exception {
		List rtn = new ArrayList();

		if (!(requestId == null) && !("".equals(requestId))) {
			JSONArray array = new JSONArray(requestId);
			if (!(array.length() == 0)) {
				int recordSize = array.length();
				for (int i = 0; i < recordSize; i++) {
					JSONObject object = (JSONObject) array.get(i);
					CfgBuildDetail detail = new CfgBuildDetail(
							object.getString("requestId"),
							object.getString("buildId"));
							
					rtn.add(detail);
				}
			}
		}
		return rtn;
	}

}
