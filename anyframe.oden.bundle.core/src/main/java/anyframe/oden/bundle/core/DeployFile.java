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
package anyframe.oden.bundle.core;

import java.util.ArrayList;
import java.util.List;

import anyframe.oden.bundle.core.command.AgentLoc;

/**
 * This class store's file information which will be deployed.
 * 
 * @author joon1k
 *
 */
public class DeployFile {
	private String path;
	
	private List<AgentLoc> agents = new ArrayList<AgentLoc>();
	
	public String getPath() {
		return path;
	}

	public List<AgentLoc> getAgents() {
		return agents;
	}
	
	public void addAgent(AgentLoc agent){
		agents.add(agent);
	}
}
