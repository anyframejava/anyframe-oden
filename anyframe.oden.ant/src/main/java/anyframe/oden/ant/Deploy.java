/*
 * Copyright 2010 SAMSUNG SDS Co., Ltd.
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
package anyframe.oden.ant;

import java.util.Vector;

/**
 * 
 * @author LEE Sujeong
 *
 */
public class Deploy {
	
	String updateonly = "";
	String repository = "";
	String includes = "";
	String excludes = "";
	
	Vector agents = new Vector();

	public String getUpdateonly() {
		return updateonly;
	}

	public void setUpdateonly(String updateonly) {
		this.updateonly = updateonly;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public String getIncludes() {
		return includes;
	}

	public void setIncludes(String includes) {
		this.includes = includes;
	}

	public String getExcludes() {
		return excludes;
	}

	public void setExcludes(String excludes) {
		this.excludes = excludes;
	}

	public Vector getAgents() {
		return agents;
	}

	public void setAgents(Vector agents) {
		this.agents = agents;
	}

	public Agent createAgent() {
		Agent agent = new Agent();
		agents.add(agent);
		return agent;
	}
	
	
}
