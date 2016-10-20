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
package anyframe.oden.bundle.core.config;

import java.util.List;

import anyframe.oden.bundle.common.OdenException;

/**
 * Oden Service to handling Oden's configuration file: config.xml
 * 
 * @author joon1k
 *
 */
public interface OdenConfigService {
	/**
	 * add agent to the oden config file
	 */
	public void addAgent(AgentElement agent) throws OdenException;
	
	/**
	 * remove the agent from the config file 
	 */
	public void removeAgent(String name) throws OdenException;
	
	/**
	 * get agent information by name
	 */
	public AgentElement getAgent(String name);
	
	/**
	 * get all agent's names
	 */
	public List<String> getAgentNames() throws OdenException;
	
	/**
	 * get backup location for the agent
	 */
	public String getBackupLocation(String agentName) throws OdenException;
	
}
