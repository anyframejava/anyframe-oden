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
package anyframe.oden.eclipse.core.history;

/**
 * Model of History Search Result Value.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC2
 *
 */
public class DeploymentHistoryViewDetails {

	private String DeployId;

	private String DeployItem;

	private String DeployServer;

	private String DeployPath;

	private String DeployDate;

	private String DeployerIp;

	private String DeployStatus;

	private String totalQuery;

	public DeploymentHistoryViewDetails() {

	}

	public DeploymentHistoryViewDetails(String DeployId, String DeployItem, String DeployPath,
			String DeployDate, String DeployerIp, String DeployStatus,
			String totalQuery, String deployServer) {

		this.DeployId = DeployId;
		this.DeployItem = DeployItem;
		this.DeployPath = DeployPath;
		this.DeployDate = DeployDate;

		this.DeployerIp = DeployerIp;
		this.DeployStatus = DeployStatus;
		this.totalQuery = totalQuery;
		this.totalQuery = totalQuery;
		this.DeployServer = deployServer;
	}

	public String getDeployId() {
		return DeployId;
	}

	public void setDeployId(String deployId) {
		this.DeployId = deployId;
	}

	public String getDeployItem() {
		return DeployItem;
	}

	public void setDeployItem(String deployItem) {
		this.DeployItem = deployItem;
	}

	public String getDeployPath() {
		return DeployPath;
	}

	public void setDeployPath(String deployPath) {
		this.DeployPath = deployPath;
	}

	public String getDeployDate() {
		return DeployDate;
	}

	public void setDeployDate(String deployDate) {
		this.DeployDate = deployDate;
	}

	public String getDeployerIp() {
		return DeployerIp;
	}

	public void setDeployerIp(String deployerIp) {
		this.DeployerIp = deployerIp;
	}

	public String getDeployStatus() {
		return DeployStatus;
	}

	public void setDeployStatus(String deployStatus) {
		this.DeployStatus = deployStatus;
	}

	public String getTotalQuery() {
		return totalQuery;
	}

	public void setTotalQuery(String totalQuery) {
		this.totalQuery = totalQuery;
	}

	public String getDeployServer() {
		return DeployServer;
	}

	public void setDeployServer(String deployServer) {
		DeployServer = deployServer;
	}
}
