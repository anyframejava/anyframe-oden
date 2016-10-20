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
package anyframe.oden.eclipse.core.jobmanager.dialogs;

/**
 * The DataSet of Deploy by task,
 * for the Anyframe Oden Job Manager view. 
 * 
 * @author HONG JungHwan
 * @version 1.1.0
 * 
 */
public class DeployByTaskInfo {

	private String deployPath;

	private String deployAgent;

	private String deployItem;

	private String deployRepo;

	private String mode;

	private String totalDeploy;

	public DeployByTaskInfo() {

	}

	public DeployByTaskInfo(String deployRepo , String deployPath ,String deployItem,
			String deployAgent , String mode) {
		this.deployRepo = deployRepo;
		this.deployItem = deployItem;
		this.deployPath = deployPath;
		this.deployAgent = deployAgent;
		this.mode = mode;
	}

	public String getDeployPath() {
		return deployPath;
	}

	public void setDeployPath(String deployPath) {
		this.deployPath = deployPath;
	}

	public String getDeployAgent() {
		return deployAgent;
	}

	public void setDeployAgent(String deployAgent) {
		this.deployAgent = deployAgent;
	}

	public String getDeployItem() {
		return deployItem;
	}

	public void setDeployItem(String deployItem) {
		this.deployItem = deployItem;
	}

	public String getDeployRepo() {
		return deployRepo;
	}

	public void setDeployRepo(String deployRepo) {
		this.deployRepo = deployRepo;
	}
	public String getTotalDeploy() {
		return totalDeploy;
	}

	public void setTotalDeploy(String totalDeploy) {
		this.totalDeploy = totalDeploy;
	}
	
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
}
