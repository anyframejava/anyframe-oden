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
import anyframe.oden.eclipse.core.utils.XMLUtil;

/**
 * Manages the list of Server objects, which have Server profile settings.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 M3
 *
 */
public class ServerManager implements ModelListener {

	private TreeMap<String, Server> servers = new TreeMap<String, Server>();
	private LinkedList<ModelListener> modelListners = new LinkedList<ModelListener>();

	/**
	 * Loads Servers from the user preference XML file
	 * @throws OdenException
	 */
	@SuppressWarnings("unchecked") // The expression of type List needs unchecked conversion to conform to List<Element>
	public void loadServers(String file) throws OdenException {
		servers.clear();

		Element root = XMLUtil.readRoot(new File(file));
		if (root != null) {
			List<Element> list = root.elements(Server.SERVER);
			if (list != null)
				for (Element elem : list)
					addServer(new Server(elem));
		}
	}

	/**
	 * Saves all the Servers to the user preference XML file
	 * @throws OdenException
	 */
	public void saveServers() throws OdenException {
		DefaultElement root = new DefaultElement(Server.SERVERS);
		for (Server server : servers.values())
			root.add(server.expressServerInXML());

		XMLUtil.save(root, new File(OdenFiles.USER_SERVER_FILE_NAME));
	}

	/**
	 * Adds an Server with a nickname as a key
	 * @param server
	 */
	public void addServer(Server server) {
		servers.put(server.getNickname(), server);
	}

	/**
	 * Gets an Server with a given nickname
	 * @param nickname
	 * @return
	 */
	public Server getServer(String nickname) {
		return servers.get(nickname);
	}

	/**
	 * Removes an Server with a given nickname
	 * @param nickname
	 */
	public void removeServer(String nickname) {
		Server server = servers.remove(nickname);
		if (server != null) {
			OdenActivator.getDefault().getAliasManager().getServerManager().modelChanged();
		}
	}

	/**
	 * Provides a list of all the Servers
	 * @return
	 */
	public Collection<Server> getServers() {
		return servers.values();
	}

	/**
	 * Returns "true" if the Server is in the list
	 * @param server
	 * @return
	 */
	public boolean contains(Server server) {
		return servers.values().contains(server);
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
