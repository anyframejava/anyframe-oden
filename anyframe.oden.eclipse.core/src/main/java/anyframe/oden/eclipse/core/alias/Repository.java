/*
 * Copyright 2009, 2010 SAMSUNG SDS Co., Ltd. All rights reserved.
 *
 * No part of this "source code" may be reproduced, stored in a retrieval
 * system, or transmitted, in any form or by any means, mechanical,
 * electronic, photocopying, recording, or otherwise, without prior written
 * permission of SAMSUNG SDS Co., Ltd., with the following exceptions:
 * Any person is hereby authorized to store "source code" on a single
 * computer for personal use only and to print copies of "source code"
 * for personal use provided that the "source code" contains SAMSUNG SDS's
 * copyright notice.
 *
 * No licenses, express or implied, are granted with respect to any of
 * the technology described in this "source code". SAMSUNG SDS retains all
 * intellectual property rights associated with the technology described
 * in this "source code".
 *
 */
package anyframe.oden.eclipse.core.alias;

import java.util.List;
import java.util.TreeMap;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import anyframe.oden.eclipse.core.OdenActivator;

/**
 * Represents a configured Build Repository profile.
 * This class extends Alias class and implements some "Build Repository specific cases.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 M3
 *
 */
public class Repository extends Alias {

	static final String REPOSITORIES = "repositories"; //$NON-NLS-1$
	static final String REPOSITORY = "repository"; //$NON-NLS-1$

	static final String SERVER_TO_USE = "server-to-use"; //$NON-NLS-1$
	static final String PROTOCOL = "protocol"; //$NON-NLS-1$
	static final String PATH = "path"; //$NON-NLS-1$

	static final String DEPLOY_NOW = "deploynow"; //$NON-NLS-1$
	static final String ALL_TO_DEFAULT = "all-to-default"; //$NON-NLS-1$

	private static int repositorySerialNo = 0;

	protected String serverToUse;
	protected String protocol;
	protected String path;

	private boolean allToDefault = true;
	private TreeMap<String, DeployNow> deployNowMap = new TreeMap<String, DeployNow>();

	/**
	 * Constructs a new Build Repository with a given nickname
	 * @param nickname
	 */
	public Repository(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * Constructs a new Build Repository with a unique nickname
	 */
	public Repository() {
		this("new-repository-" + (++repositorySerialNo)); //$NON-NLS-1$
	}

	/**
	 * Constructs a Build Repository from stored XML
	 * @param root
	 */
	@SuppressWarnings("unchecked")
	public Repository(Element root) {
		serverToUse = root.attributeValue(SERVER_TO_USE);
		String hasNoUserNameString = root.attributeValue(HAS_NO_USER_NAME);
		if (hasNoUserNameString != null)
			hasNoUserName = Boolean.parseBoolean(hasNoUserNameString);
		nickname = root.elementText(NICKNAME);
		url = root.elementText(URL);
		protocol = root.elementText(PROTOCOL);
		path = root.elementText(PATH);

		if (hasNoUserName) {
			setUser("anonymous"); //$NON-NLS-1$
			setPassword(""); //$NON-NLS-1$
		} else {
			user = root.elementText(USER);
			password = root.elementText(PASSWORD);
		}

		Element deployNowElements = root.element(DEPLOY_NOW);
		if (deployNowElements != null) {
			String allToDefaultString = deployNowElements.attributeValue(ALL_TO_DEFAULT);
			if (allToDefaultString != null)
				allToDefault = Boolean.parseBoolean(allToDefaultString);
			List<Element> list = deployNowElements.elements(DeployNow.DESTINATION);
			if (list != null)
				for (Element deployNowElement : list) {
					DeployNow deployNow = new DeployNow(deployNowElement);
					if (deployNow.getDestinedAgentName() != null && deployNow.getDestinedAgentName().trim().length() > 0)
						addDestination(deployNow);
				}
		}
	}

	/**
	 * Expresses this Build Repository in XML expression
	 * @return
	 */
	public Element expressRepositoryInXML() {
		DefaultElement root = new DefaultElement(REPOSITORY);
		root.addAttribute(SERVER_TO_USE, serverToUse);
		root.addAttribute(HAS_NO_USER_NAME, Boolean.toString(hasNoUserName));
		root.addElement(NICKNAME).setText(nickname);
		root.addElement(URL).setText(url);
		root.addElement(PROTOCOL).setText(protocol);
		root.addElement(PATH).setText(path);
		root.addElement(USER).setText(user);
		root.addElement(PASSWORD).setText(password);

		Element deployNowElement = root.addElement(DEPLOY_NOW);
		deployNowElement.addAttribute(ALL_TO_DEFAULT, Boolean.toString(allToDefault));
		
		for (DeployNow deployNow : deployNowMap.values())
			deployNowElement.add(deployNow.expressDeployNowInXML());

		return root;
	}
	
	/**
	 * Gets DeployNow Objects for Deploy-Now action
	 * @return
	 */
	public TreeMap<String, DeployNow> getDeployNowMap() {
		return deployNowMap;
	}
	
	/**
	 * Sets DeployNow Objects for Deploy-Now action
	 * @return
	 */
	public void setDeployNowMap(TreeMap<String, DeployNow> deployNowMap) {
		this.deployNowMap = deployNowMap;
	}

	/**
	 * Constructs a Build Repository as a duplicate of another with a different nickname
	 * @param duplicate
	 */
	public Repository(Repository duplicate) {
		this(duplicate.getNickname() + " - duplicated"); //$NON-NLS-1$
		setServerToUse(duplicate.getServerToUse());
		setHasNoUserName(duplicate.isHasNoUserName());
		setUrl(duplicate.getUrl());
		setProtocol(duplicate.getProtocol());
		setPath(duplicate.getPath());
		setUser(duplicate.getUser());
		setPassword(duplicate.getPassword());
	}

	/**
	 * Removes a Build Repository with a given nickname
	 */
	public void remove() {
		OdenActivator.getDefault().getAliasManager().getRepositoryManager().removeRepository(getNickname());
	}

	/**
	 * Gets the Server to use for this Build Repository
	 * @return the Server to use for this Build Repository
	 */
	public String getServerToUse() {
		return serverToUse;
	}

	/**
	 * Sets the Server to use for this Build Repository
	 * @param serverToUse
	 */
	public void setServerToUse(String serverToUse) {
		this.serverToUse = serverToUse;
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return
	 */
	public boolean isAllToDefault() {
		return allToDefault;
	}

	/**
	 * @param allToDefault
	 */
	public void setAllToDefault(boolean allToDefault) {
		this.allToDefault = allToDefault;
	}

	/**
	 * Adds a Deploy-Now destination
	 * @param deployNow
	 * @return
	 */
	public DeployNow addDestination(DeployNow deployNow) {
		if (deployNow.getDestinedAgentName() == null || deployNow.getDestinedAgentName().length() == 0)
			throw new IllegalArgumentException("Destined Agent name is invalid.");
		deployNowMap.put(deployNow.getDestinedAgentName(), deployNow);
		deployNow.setRepository(this);
		OdenActivator.getDefault().getAliasManager().getRepositoryManager().modelChanged();
		return deployNow;
	}

	/**
	 * Removes the Deploy-Now destination with a given destined Agent name
	 * @param deployNow
	 */
	public void removeDestination(DeployNow deployNow) {
		deployNow.setRepository(null);
		deployNowMap.remove(deployNow.getDestinedAgentName());
	}
	
}
