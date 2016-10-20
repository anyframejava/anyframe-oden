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

import org.osgi.framework.BundleContext;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenFiles;
import anyframe.oden.eclipse.core.license.dialog.LicenseCheckAction;

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
	private FileRequestManager filerequestManager;

	// private String aliasType;

	// private AliasManager[] children = new AliasManager[0];
	// private AliasManager parent = null;

	/**
	 * Initializes list of Servers and Repositories , FileRequests.
	 * 
	 * @throws OdenException
	 * @see anyframe.oden.eclipse.core.OdenActivator#start(org.osgi.framework.BundleContext)
	 */
	public void load() throws OdenException {

		if (boolLicenseCorrect()) { // 라이센스 파일이 있거나 파일이 없어도 demo사용시
			settingExistXML();
		} else {// 라이센스도 없고 데모도 안쓸때, 데모 기간 만료시
			settingNoneExistXML();
		}
	}

	private void settingExistXML() throws OdenException {
		try {
			serverManager = new ServerManager();
			serverManager.loadServers(OdenFiles.USER_SERVER_FILE_NAME);

			repositoryManager = new RepositoryManager();
			repositoryManager
					.loadRepositories(OdenFiles.USER_REPOSITORY_FILE_NAME);

			filerequestManager = new FileRequestManager();
			filerequestManager
					.loadFileRequests(OdenFiles.USER_FILEREQUEST_FILE_NAME);

		} catch (OdenException odenException) {
			OdenActivator.error(
					"Exception occured during loading Servers or Repositories",
					odenException);
			throw odenException;
		}
	}

	public void settingNoneExistXML() throws OdenException {
		try {
			serverManager = new ServerManager();
			serverManager.loadServers("");

			repositoryManager = new RepositoryManager();
			repositoryManager.loadRepositories("");

			filerequestManager = new FileRequestManager();
			filerequestManager.loadFileRequests("");

		} catch (OdenException odenException) {
			OdenActivator.error(
					"Exception occured during loading Servers or Repositories",
					odenException);
			throw odenException;
		}
	}

	private boolean boolLicenseCorrect() {// load
		LicenseCheckAction action = new LicenseCheckAction();
		return action.confirmFile();
	}

	/**
	 * Saves modified list of Servers and Repositories , FileRequests when the
	 * plug-in closes It occurs usually when quits Eclipse
	 * 
	 * @throws OdenException
	 * @see anyframe.oden.eclipse.core.OdenActivator#stop(BundleContext context)
	 */
	public void save() throws OdenException {
		// if (boolLicenseCorrect()) {
		try {
			if (serverManager != null) {
				serverManager.saveServers();
			}
			if (repositoryManager != null) {
				repositoryManager.saveRepositories();
			}
			if (filerequestManager != null) {
				filerequestManager.saveFileRequests();
			}
		} catch (OdenException odenException) {
			OdenActivator.error(
					"Exception occured while saving Servers or Repositories",
					odenException);
			throw odenException;
		}
		// } else {
		// try {
		// if (serverManager != null) {
		// serverManager.saveServers();
		// }
		// if (repositoryManager != null) {
		// repositoryManager.saveRepositories();
		// }
		// if (filerequestManager != null) {
		// filerequestManager.saveFileRequests();
		// }
		// } catch (OdenException odenException) {
		// OdenActivator
		// .error(
		// "Exception occured while saving Servers or Repositories",
		// odenException);
		// throw odenException;
		// }
		//			
		// }
	}

	/**
	 * Gets an ServerManager
	 * 
	 * @return list of Servers
	 */
	public ServerManager getServerManager() {
		return serverManager;
	}

	// A setter for ServerManager is not required
	// public void setServerManager(ServerManager serverManager) {
	// this.serverManager = serverManager;
	// }

	/**
	 * Gets a RepositoryManager
	 * 
	 * @return list of Build Repositories
	 */
	public RepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	// A setter for RepositoryManager is not required
	// public void setRepositoryManager(RepositoryManager repositoryManager) {
	// this.repositoryManager = repositoryManager;
	// }

	/**
	 * Gets a RepositoryManager
	 * 
	 * @return list of Build Repositories
	 */
	public FileRequestManager getFileRequestManager() {
		return filerequestManager;
	}
}
