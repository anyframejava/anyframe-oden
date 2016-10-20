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
package org.anyframe.oden.bundle.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class has file list to be deployed and save the agent information to
 * which files are deployed.
 * 
 * @author Junghwan Hong
 */
public class FileMap extends HashMap<String, List<AgentFile>> {

	/**
	 * append a AgentFile to the list having same file path.
	 * 
	 * @param file
	 * @param agent
	 */
	@SuppressWarnings("PMD")
	public synchronized void append(String file, AgentFile agent) {
		List<AgentFile> agents = get(file);
		if (agents == null) {
			put(file, agents = new ArrayList<AgentFile>());
		}

		boolean contains = false;
		for (AgentFile a : agents) {
			if (agent.agent().equals(a.agent())) {
				contains = true;
				break;
			}
		}
		if (!contains) {
			agents.add(agent);
		}
	}

}
