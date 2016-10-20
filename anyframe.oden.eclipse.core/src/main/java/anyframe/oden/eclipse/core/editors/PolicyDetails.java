/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

	private String PolicyName;

	private String Description;

	private String BuildRepo;

	private String IncludeItem;

	private String ExcludeItem;

	private String DeployUrl;

	private String DeployRoot;
	
	private String LocationVar;

	public PolicyDetails() {

	}

	public PolicyDetails(String PolicyName, String Description,
			String DeployUrl, String DeployRoot, String LocationVar) {
		this.PolicyName = PolicyName;
		this.Description = Description;
		this.DeployUrl = DeployUrl;
		this.DeployRoot = DeployRoot;
		this.LocationVar = LocationVar;
	}

	public String getPolicyName() {
		return PolicyName;
	}

	public void setPolicyName(String policyName) {
		this.PolicyName = policyName;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		this.Description = description;
	}

	public String getBuildRepo() {
		return BuildRepo;
	}

	public void setBuildRepo(String buildRepo) {
		this.BuildRepo = buildRepo;
	}

	public String getIncludeItem() {
		return IncludeItem;
	}

	public void setIncludeItem(String includeItem) {
		this.IncludeItem = includeItem;
	}

	public String getExcludeItem() {
		return ExcludeItem;
	}

	public void setExcludeItem(String excludeItem) {
		this.ExcludeItem = excludeItem;
	}

	public String getDeployUrl() {
		return DeployUrl;
	}

	public void setDeployUrl(String deployUrl) {
		this.DeployUrl = deployUrl;
	}

	public String getDeployRoot() {
		return DeployRoot;
	}

	public void setDeployRoot(String deployRoot) {
		this.DeployRoot = deployRoot;
	}

	public String getLocationVar() {
		return LocationVar;
	}

	public void setLocationVar(String locationVar) {
		LocationVar = locationVar;
	}

}
