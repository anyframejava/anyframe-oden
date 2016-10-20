/*
 * Copyright 2009, 2010 SAMSUNG SDS Co., Ltd. All rights reserved.
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
package anyframe.oden.eclipse.core.history;

/**
 * Model of History Search Result Value(TreeRoot).
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC2
 *
 */
public class DeploymentHistoryViewRoots {

	private String deployId;

	private String deployDate;

	private String deployerIp;

	private String deployStatus;

	private String deployDesc;

	private String deployLog;
	
	private String totalQuery;

	public DeploymentHistoryViewRoots() {

	}

	public DeploymentHistoryViewRoots(String deployId, String deployDate,
			String deployerIp, String deployStatus, String deployDesc, String deployLog, String totalQuery) {
			
		this.deployId = deployId;
		this.deployDate = deployDate;

		this.deployerIp = deployerIp;
		this.deployStatus = deployStatus;
		this.deployDesc = deployDesc;
		this.deployLog = deployLog;
		
		this.totalQuery = totalQuery;
	}

	public String getDeployId() {
		return deployId;
	}

	public void setDeployId(String deployId) {
		this.deployId = deployId;
	}

	public String getDeployDate() {
		return deployDate;
	}

	public void setDeployDate(String deployDate) {
		this.deployDate = deployDate;
	}

	public String getDeployerIp() {
		return deployerIp;
	}

	public void setDeployerIp(String deployerIp) {
		this.deployerIp = deployerIp;
	}

	public String getDeployStatus() {
		return deployStatus;
	}

	public void setDeployStatus(String deployStatus) {
		this.deployStatus = deployStatus;
	}
	
	public String getDeployDesc() {
		return deployDesc;
	}

	public void setDeployDesc(String deployDesc) {
		this.deployDesc = deployDesc;
	}
	
	public String getTotalQuery() {
		return totalQuery;
	}

	public void setTotalQuery(String totalQuery) {
		this.totalQuery = totalQuery;
	}

	public String getDeployLog() {
		return deployLog;
	}
}
