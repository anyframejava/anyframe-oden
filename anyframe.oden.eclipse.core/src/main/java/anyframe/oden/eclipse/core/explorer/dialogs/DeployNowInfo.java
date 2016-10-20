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
package anyframe.oden.eclipse.core.explorer.dialogs;

public class DeployNowInfo {

	private String DeployPath;

	private String DeployAgent;

	private String DeployItem;

	private String DeployRepo;



	private String totalDeploy;

	public DeployNowInfo() {

	}

	public DeployNowInfo(String DeployRepo , String DeployPath ,String DeployItem,
			String DeployAgent) {
		this.DeployRepo = DeployRepo;
		this.DeployItem = DeployItem;
		this.DeployPath = DeployPath;
		this.DeployAgent = DeployAgent;
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

}
