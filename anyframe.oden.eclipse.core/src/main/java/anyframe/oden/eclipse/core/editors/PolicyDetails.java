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
package anyframe.oden.eclipse.core.editors;

/**
 * The Model of Policy Data
 * 
 * @author HONG JungHwan
 * @version 1.0.0 RC2
 * 
 */
public class PolicyDetails {

	private String policyName;

	private String description;

	private String buildRepo;

	private String includeItem;

	private String excludeItem;

	private String deployUrl;

	private String deployRoot;
	
	private String locationVar;
	
	private String location;

	public PolicyDetails() {

	}

	public PolicyDetails(String policyName, String description,
			String deployUrl, String deployRoot, String locationVar , String location) {
		this.policyName = policyName;
		this.description = description;
		this.deployUrl = deployUrl;
		this.deployRoot = deployRoot;
		this.locationVar = locationVar;
		this.location = location;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBuildRepo() {
		return buildRepo;
	}

	public void setBuildRepo(String buildRepo) {
		this.buildRepo = buildRepo;
	}

	public String getIncludeItem() {
		return includeItem;
	}

	public void setIncludeItem(String includeItem) {
		this.includeItem = includeItem;
	}

	public String getExcludeItem() {
		return excludeItem;
	}

	public void setExcludeItem(String excludeItem) {
		this.excludeItem = excludeItem;
	}

	public String getDeployUrl() {
		return deployUrl;
	}

	public void setDeployUrl(String deployUrl) {
		this.deployUrl = deployUrl;
	}

	public String getDeployRoot() {
		return deployRoot;
	}

	public void setDeployRoot(String deployRoot) {
		this.deployRoot = deployRoot;
	}

	public String getLocationVar() {
		return locationVar;
	}

	public void setLocationVar(String locationVar) {
		this.locationVar = locationVar;
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
