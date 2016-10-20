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

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import anyframe.oden.eclipse.core.OdenActivator;

/**
 * Represents a configured Oden Server (a.k.a Agent, Master Agent) profile.
 * This class extends Alias class and implements some "Agent" specific cases.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0 M3
 *
 */
public class Agent extends Alias {

	static final String AGENTS = "servers";
	static final String AGENT = "server";
	//	static final String LOGTYPE = "log-type";
	//	static final String LOGLOCATION = "log-location";

	private static int agentSerialNo = 0;

	//	private String logType;
	//	private String logLocation;

	/**
	 * Constructs a new Agent with a given nickname
	 * @param nickname
	 */
	public Agent(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * Constructs a new Agent with a unique nickname
	 */
	public Agent() {
		this("new-server-" + (++agentSerialNo));
	}

	/**
	 * Constructs an Agent from stored XML
	 * @param root
	 */
	public Agent(Element root) {
		String string = root.attributeValue(HAS_NO_USER_NAME);
		if (string != null)
			hasNoUserName = Boolean.parseBoolean(string);
		nickname = root.elementText(NICKNAME);
		url = root.elementText(URL);

		if (hasNoUserName) {
			setUser("anonymous");
			setPassword("");
		} else {
			user = root.elementText(USER);
			password = root.elementText(PASSWORD);
		}

		//		logType = root.elementText(LOGTYPE);
		//		logLocation = root.elementText(LOGLOCATION);
	}

	/**
	 * Expresses this Agent in XML expression
	 * @return
	 */
	public Element expressAgentInXML() {
		DefaultElement root = new DefaultElement(AGENT);
		root.addAttribute(HAS_NO_USER_NAME, Boolean.toString(hasNoUserName));
		root.addElement(NICKNAME).setText(nickname);
		root.addElement(URL).setText(url);
		root.addElement(USER).setText(user);
		root.addElement(PASSWORD).setText(password);
		//		root.addElement(LOGTYPE).setText(logType);
		//		root.addElement(LOGLOCATION).setText(logLocation);

		return root;
	}

	/**
	 * Constructs an Agent as a duplicate of another with a different nickname
	 * @param duplicate
	 */
	public Agent(Agent duplicate) {
		this(duplicate.getNickname() + " - duplicated");
		setHasNoUserName(duplicate.isHasNoUserName());
		setUrl(duplicate.getUrl());
		setUser(duplicate.getUser());
		setPassword(duplicate.getPassword());
		//		setLogType(duplicate.getLogType());
		//		setLogLocation(duplicate.getLogLocation());
	}

	/**
	 * Removes an Agent with a given nickname
	 */
	public void remove() {
		OdenActivator.getDefault().getAliasManager().getAgentManager().removeAgent(getNickname());
	}

	//	/**
	//	 * @return the logType
	//	 */
	//	public String getLogType() {
	//		return logType;
	//	}
	//	
	//	/**
	//	 * @param logType the logType to set
	//	 */
	//	public void setLogType(String logType) {
	//		this.logType = logType;
	//	}
	//	
	//	/**
	//	 * @return the logLocation
	//	 */
	//	public String getLogLocation() {
	//		return logLocation;
	//	}
	//	
	//	/**
	//	 * @param logLocation the logLocation to set
	//	 */
	//	public void setLogLocation(String logLocation) {
	//		this.logLocation = logLocation;
	//	}

}
