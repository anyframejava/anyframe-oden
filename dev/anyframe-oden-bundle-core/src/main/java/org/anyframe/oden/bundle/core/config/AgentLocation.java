/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.anyframe.oden.bundle.core.config;

/**
 * This represents agent's location in the config.xml
 * 
 * @author Junghwan Hong
 */
class AgentLocation {
	private AgentElement agent;

	private String name;

	private String value;

	public AgentLocation(AgentElement agent, String name, String value) {
		this.agent = agent;
		this.name = name;
		this.value = value;
	}

	public void setAgent(AgentElement agent) {
		this.agent = agent;
	}

	public AgentElement getAgent() {
		return agent;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getAgentName() {
		if (agent != null) {
			return agent.getName();
		}
		return null;
	}

	public String getAgentAddr() {
		if (agent != null) {
			return agent.getAddr();
		}
		return null;
	}
}
