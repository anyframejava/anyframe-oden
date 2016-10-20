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
package anyframe.oden.eclipse.core.explorer.dialogs;

public class DeployNowInfo {

	private String DeployPath;

	private String DeployAgent;

	private String DeployItem;

	private String DeployRepo;

	private String mode;

	private String totalDeploy;

	public DeployNowInfo() {

	}

	public DeployNowInfo(String DeployRepo , String DeployPath ,String DeployItem,
			String DeployAgent , String mode) {
		this.DeployRepo = DeployRepo;
		this.DeployItem = DeployItem;
		this.DeployPath = DeployPath;
		this.DeployAgent = DeployAgent;
		this.mode = mode;
	}

	public String getDeployPath() {
		return DeployPath;
	}

	public void setDeployPath(String deployPath) {
		DeployPath = deployPath;
	}

	public String getDeployAgent() {
		return DeployAgent;
	}

	public void setDeployAgent(String deployAgent) {
		DeployAgent = deployAgent;
	}

	public String getDeployItem() {
		return DeployItem;
	}

	public void setDeployItem(String deployItem) {
		DeployItem = deployItem;
	}

	public String getDeployRepo() {
		return DeployRepo;
	}

	public void setDeployRepo(String deployRepo) {
		DeployRepo = deployRepo;
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
