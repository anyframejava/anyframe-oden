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
	
	static final String DEPLOY_NOW = "deploynow";
	static final String WITH_ZERO_CONFIG = "with-zero-config";

	private static int repositorySerialNo = 0;

	private String serverToUse;
	private String protocol;
	private String path;

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
	public Repository(Element root) {
		serverToUse = root.attributeValue(SERVER_TO_USE);
		String string = root.attributeValue(HAS_NO_USER_NAME);
		if (string != null)
			hasNoUserName = Boolean.parseBoolean(string);
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

		return root;
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

}
