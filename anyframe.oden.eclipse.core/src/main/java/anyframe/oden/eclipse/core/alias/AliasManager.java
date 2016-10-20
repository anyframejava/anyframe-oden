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

import org.osgi.framework.BundleContext;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;

/**
 * Manages the list of Servers and Repositories.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 *
 */
public class AliasManager {

	private ServerManager serverManager;
	private RepositoryManager repositoryManager;
	//	private String aliasType;

	//	private AliasManager[] children = new AliasManager[0];
	//	private AliasManager parent = null;

	/**
	 * Initializes list of Servers and Repositories.
	 * @throws OdenException
	 * @see anyframe.oden.eclipse.core.OdenActivator#start(org.osgi.framework.BundleContext)
	 */
	public void load() throws OdenException {
		try {
			serverManager = new ServerManager();
			serverManager.loadServers();

			repositoryManager = new RepositoryManager();
			repositoryManager.loadRepositories();
		} catch (OdenException odenException) {
			OdenActivator.error("Exception occured during loading Servers or Repositories", odenException);
			throw odenException;
		}
	}

	/**
	 * Saves modified list of Servers and Repositories when the plug-in closes
	 * It occurs usually when quits Eclipse 
	 * @throws OdenException
	 * @see anyframe.oden.eclipse.core.OdenActivator#stop(BundleContext context)
	 */
	public void save() throws OdenException {
		try {
			if (serverManager != null) {
				serverManager.saveServers();
			}
			if (repositoryManager != null) {
				repositoryManager.saveRepositories();
			}		
		} catch (OdenException odenException) {
			OdenActivator.error("Exception occured while saving Servers or Repositories", odenException);
			throw odenException;
		}
	}

	/**
	 * Gets an ServerManager
	 * @return list of Servers
	 */
	public ServerManager getServerManager() {
		return serverManager;
	}

	// A setter for ServerManager is not required
	//	public void setServerManager(ServerManager serverManager) {
	//		this.serverManager = serverManager;
	//	}

	/**
	 * Gets a RepositoryManager
	 * @return list of Build Repositories
	 */
	public RepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	// A setter for RepositoryManager is not required
	//	public void setRepositoryManager(RepositoryManager repositoryManager) {
	//		this.repositoryManager = repositoryManager;
	//	}

}
