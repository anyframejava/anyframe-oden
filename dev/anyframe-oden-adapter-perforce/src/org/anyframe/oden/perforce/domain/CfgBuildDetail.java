package org.anyframe.oden.perforce.domain;


import java.io.Serializable;

/**
 * This is CfgBuildDetail Class
 * 
 * @author Junghwan Hong
 */
@SuppressWarnings("PMD")
public class CfgBuildDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String requestId;

	String buildId;

	public CfgBuildDetail(String requestId, String buildId) {
		this.requestId = requestId;
		this.buildId = buildId;
	}

	public String getRequestId() {
		return requestId;
	}

	public String getBuildId() {
		return buildId;
	}

}
