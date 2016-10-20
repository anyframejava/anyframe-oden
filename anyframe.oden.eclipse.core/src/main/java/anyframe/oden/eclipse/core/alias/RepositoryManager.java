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
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.utils.XMLUtil;

/**
 * Manages the list of Build Repository objects, which have Build Repository profile settings.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 M3
 *
 */
public class RepositoryManager implements ModelListener {

	private TreeMap<String, Repository> repositories = new TreeMap<String, Repository>();
	private LinkedList<ModelListener> modelListners = new LinkedList<ModelListener>();
	private String[] protocolSet = {CommonMessages.ODEN_ALIAS_RepositoryManager_ProtocolSet_FileSystem, CommonMessages.ODEN_ALIAS_RepositoryManager_ProtocolSet_FTP};

	/**
	 * Loads Build Repositories from the user preference XML file
	 * @throws OdenException
	 */
	@SuppressWarnings("unchecked") // The expression of type List needs unchecked conversion to conform to List<Element>
	public void loadRepositories(String file) throws OdenException {
		repositories.clear();

		Element root = XMLUtil.readRoot(new File(file));
		if (root != null) {
			List<Element> list = root.elements(Repository.REPOSITORY);
			if (list != null)
				for (Element elem : list)
					addRepository(new Repository(elem));
		}
	}

	/**
	 * Saves all the Build Repositories to the user preference XML file
	 * @throws OdenException
	 */
	public void saveRepositories() throws OdenException {
		DefaultElement root = new DefaultElement(Repository.REPOSITORIES);
		for (Repository repository : repositories.values())
			root.add(repository.expressRepositoryInXML());

		XMLUtil.save(root, new File(OdenFiles.USER_REPOSITORY_FILE_NAME));
	}

	/**
	 * Adds a Build Repository with a nickname as a key
	 * @param repository
	 */
	public void addRepository(Repository repository) {
		repositories.put(repository.getNickname(), repository);
	}

	/**
	 * Gets a Build Repository with a given nickname
	 * @param nickname
	 * @return
	 */
	public Repository getRepository(String nickname) {
		return repositories.get(nickname);
	}
	
	/**
	 * Gets a Build Repository with a given nickname
	 * @param nickname
	 * @return
	 */
	public TreeMap<String, DeployNow> getAgentInfo(String nickname) {
		return repositories.get(nickname).getDeployNowMap();
	}

	/**
	 * Removes a Build Repository with a given nickname
	 * @param nickname
	 */
	public void removeRepository(String nickname) {
		Repository repository = repositories.remove(nickname);
		if (repository != null) {
			OdenActivator.getDefault().getAliasManager().getRepositoryManager().modelChanged();
		}
	}

	/**
	 * Provides a list of all the Build Repositories
	 * @return
	 */
	public Collection<Repository> getRepositories() {
		return repositories.values();
	}

	/**
	 * Returns "true" if the Build Repository is in the list
	 * @param repository
	 * @return
	 */
	public boolean contains(Repository repository) {
		return repositories.values().contains(repository);
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

	/**
	 * @return the protocolSet
	 */
	public String[] getProtocolSet() {
		return protocolSet;
	}

	/**
	 * @param protocolSet the protocolSet to set
	 */
	public void setProtocolSet(String[] protocolSet) {
		this.protocolSet = protocolSet;
	}

}
