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

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenFiles;
import anyframe.oden.eclipse.core.utils.XMLUtil;

/**
 * Manages the list of Agent objects, which have Agent profile settings.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 M3
 *
 */
public class AgentManager implements ModelListener {

	private TreeMap<String, Agent> agents = new TreeMap<String, Agent>();
	private LinkedList<ModelListener> modelListners = new LinkedList<ModelListener>();

	/**
	 * Loads Agents from the user preference XML file
	 * @throws OdenException
	 */
	@SuppressWarnings("unchecked") // The expression of type List needs unchecked conversion to conform to List<Element>
	public void loadAgents() throws OdenException {
		agents.clear();

		Element root = XMLUtil.readRoot(new File(OdenFiles.USER_AGENT_FILE_NAME));
		if (root != null) {
			List<Element> list = root.elements(Agent.AGENT);
			if (list != null)
				for (Element elem : list)
					addAgent(new Agent(elem));
		}
	}

	/**
	 * Saves all the Agents to the user preference XML file
	 * @throws OdenException
	 */
	public void saveAgents() throws OdenException {
		DefaultElement root = new DefaultElement(Agent.AGENTS);
		for (Agent agent : agents.values())
			root.add(agent.expressAgentInXML());

		XMLUtil.save(root, new File(OdenFiles.USER_AGENT_FILE_NAME));
	}

	/**
	 * Adds an Agent with a nickname as a key
	 * @param agent
	 */
	public void addAgent(Agent agent) {
		agents.put(agent.getNickname(), agent);
	}

	/**
	 * Gets an Agent with a given nickname
	 * @param nickname
	 * @return
	 */
	public Agent getAgent(String nickname) {
		return agents.get(nickname);
	}

	/**
	 * Removes an Agent with a given nickname
	 * @param nickname
	 */
	public void removeAgent(String nickname) {
		Agent agent = agents.remove(nickname);
		if (agent != null) {
			OdenActivator.getDefault().getAliasManager().getAgentManager().modelChanged();
		}
	}

	/**
	 * Provides a list of all the Agents
	 * @return
	 */
	public Collection<Agent> getAgents() {
		return agents.values();
	}

	/**
	 * Returns "true" if the Agent is in the list
	 * @param agent
	 * @return
	 */
	public boolean contains(Agent agent) {
		return agents.values().contains(agent);
	}

	/**
	 * Adds a listener
	 * @param listener
	 */
	public void addListener(ModelListener listener) {
		modelListners.add(listener);
	}

	/**
	 * Removes a listener
	 * @param listener
	 */
	public void removeListener(ModelListener listener) {
		modelListners.remove(listener);
	}

	/**
	 * Called to notify that the list has changed
	 */
	public void modelChanged() {
		for (ModelListener listener : modelListners)
			listener.modelChanged();
	}
}
