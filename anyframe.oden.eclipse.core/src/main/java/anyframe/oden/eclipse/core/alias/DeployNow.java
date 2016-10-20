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
package anyframe.oden.eclipse.core.alias;

/**
 * Represents the combination of an Agent name and a location variable 
 * for Deploy Now action on the selected Build Repository item 
 * with zero-configuration at run-time; 
 * if never configured, the default-location of all available Agent 
 * will be used for Deploy Now action.
 * 
 * @author RHIE Jihwan
 * @author HONG Junghwan
 * @version 1.0.0
 *
 */
public class DeployNow {

	// XML tag strings for Deploy-Now destination
	static final String DESTINATION = "destination";
	static final String AGENT = "agent";
	static final String LOCATION = "location";

	// agent name and location variable name to use as Deploy-Now destination
	private String destinedAgent;
	private String destinedLocation;

	/**
	 * Constructor
	 */
	public DeployNow() {
		
	}

	public DeployNow(String destinedAgent, String destinedLocation) {
		super();
		this.destinedAgent = destinedAgent;
		this.destinedLocation = destinedLocation;
	}





	// 여기부터 홍선임이 작업했던  기존 것들
	//
	//	private String DeployPath;
	//
	//	private String DeployAgent;
	//
	//	private String DeployItem;
	//
	//	private String DeployRepo;
	//
	//
	//
	//	private String totalDeploy;
	//
	//	public DeployNow() {
	//
	//	}
	//
	//	public DeployNow(String DeployRepo , String DeployPath ,String DeployItem,
	//			String DeployAgent) {
	//		this.DeployRepo = DeployRepo;
	//		this.DeployItem = DeployItem;
	//		this.DeployPath = DeployPath;
	//		this.DeployAgent = DeployAgent;
	//	}
	//
	//	public String getDeployPath() {
	//		return DeployPath;
	//	}
	//
	//	public void setDeployPath(String deployPath) {
	//		DeployPath = deployPath;
	//	}
	//
	//	public String getDeployAgent() {
	//		return DeployAgent;
	//	}
	//
	//	public void setDeployAgent(String deployAgent) {
	//		DeployAgent = deployAgent;
	//	}
	//
	//	public String getDeployItem() {
	//		return DeployItem;
	//	}
	//
	//	public void setDeployItem(String deployItem) {
	//		DeployItem = deployItem;
	//	}
	//
	//	public String getDeployRepo() {
	//		return DeployRepo;
	//	}
	//
	//	public void setDeployRepo(String deployRepo) {
	//		DeployRepo = deployRepo;
	//	}
	//	public String getTotalDeploy() {
	//		return totalDeploy;
	//	}
	//
	//	public void setTotalDeploy(String totalDeploy) {
	//		this.totalDeploy = totalDeploy;
	//	}

}
