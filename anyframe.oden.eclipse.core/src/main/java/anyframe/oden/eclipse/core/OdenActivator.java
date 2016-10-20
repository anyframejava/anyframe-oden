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
package anyframe.oden.eclipse.core;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import anyframe.oden.eclipse.core.alias.AliasManager;
import anyframe.oden.eclipse.core.explorer.ExplorerView;
import anyframe.oden.eclipse.core.history.DeploymentHistoryView;
import anyframe.oden.eclipse.core.jobmanager.JobManagerView;
import anyframe.oden.eclipse.core.jobmanager.dialogs.SetFileReqPathDialog;
import anyframe.oden.eclipse.core.snapshot.SnapshotView;

/**
 * The activator class controls the Anyframe Oden Eclipse plug-in life cycle.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 M3
 * 
 */
public class OdenActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "anyframe.oden.eclipse.core";
	public static final String HELP_PLUGIN_ID = "anyframe.common.eclipse.doc";

	// The shared instance
	private static OdenActivator plugin;

	private static final Log odenLogger = LogFactory
			.getLog(OdenActivator.class);

	// Cached views
	private ExplorerView explorerView;
	private SnapshotView snapshotView;
	private DeploymentHistoryView deploymentHistoryView;
	private JobManagerView jobmanagerView;
	private SetFileReqPathDialog setfilereqpathDialog;

	// AliasManager type for setInput() of "Oden Explorer view"
	private AliasManager aliasManager;

	/**
	 * The constructor
	 */
	public OdenActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		// There is no need to get preference store for this plugin - nothing is
		// there ;-)
		// PreferencePage.setCurrent(getPreferenceStore());

		try {
			getLog().addLogListener(new ILogListener() {
				public void logging(IStatus status, String plugin) {
					System.err.println(status.getMessage());
					Throwable throwable = status.getException();
					if (throwable != null) {
						System.err.println(throwable.getMessage());
						throwable.printStackTrace(System.err);
					}
				}
			});

			// Creates aliasManager and initialize it with stored ServerManager
			// and RepositoryManager in each XML file
			aliasManager = new AliasManager();
			aliasManager.load();

		} catch (Exception e) {
			error("Exception occured during start-up", e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {

		// Termination process, which stores working configuration of
		// ServerManager and RepositoryManager in each XML file
		aliasManager.save();

		// Set current plugin to null
		// plugin = null;

		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static OdenActivator getDefault() {
		return plugin;
	}

	/**
	 * Gets AliasManager which has ServerManager and RepositoryManager
	 * 
	 * @return aliasManager
	 */
	public AliasManager getAliasManager() {
		return aliasManager;
	}

	/**
	 * Gets Oden Explorer view
	 * 
	 * @return getExplorerView(true)
	 * @see anyframe.oden.eclipse.core.OdenActivator#getExplorerView(boolean
	 *      create)
	 */
	public ExplorerView getExplorerView() {
		return getExplorerView(true);
	}

	/**
	 * Gets Oden Snapshot view
	 * 
	 * @return getSnapshotView(true)
	 * @see anyframe.oden.eclipse.core.OdenActivator#getSnapshotView(boolean
	 *      create)
	 */
	public SnapshotView getSnapshotView() {
		return getSnapshotView(true);
	}

	/**
	 * Gets Oden Deployment History view
	 * 
	 * @return getDeploymentHistoryView(true)
	 * @see anyframe.oden.eclipse.core.OdenActivator#getDeploymentHistoryView(boolean
	 *      create)
	 */
	public DeploymentHistoryView getDeploymentHistoryView() {
		return getDeploymentHistoryView(true);
	}

	/**
	 * Gets Oden Deployment History view
	 * 
	 * @return getDeploymentHistoryView(true)
	 * @see anyframe.oden.eclipse.core.OdenActivator#getDeploymentHistoryView(boolean
	 *      create)
	 */
	public JobManagerView getJObManagerView() {
		return getJobManagerView(true);
	}

	/**
	 * Gets Oden Deployment History view
	 * 
	 * @return getDeploymentHistoryView(true)
	 * @see anyframe.oden.eclipse.core.OdenActivator#getDeploymentHistoryView(boolean
	 *      create)
	 */
	public SetFileReqPathDialog getSetFileReqPathDialog() {
		return getSetFileReqPathDialog(true);
	}

	/**
	 * Gets Oden Explorer view
	 * 
	 * @param create
	 * @return explorerView
	 */
	public ExplorerView getExplorerView(boolean create) {
		if (explorerView == null) {
			IWorkbenchPage page = getActivePage();
			if (page != null) {
				explorerView = (ExplorerView) page.findView(ExplorerView.class
						.getName());
				if (explorerView == null && create) {
					try {
						explorerView = (ExplorerView) page
								.showView(ExplorerView.class.getName());
					} catch (PartInitException partInitException) {
						partInitException.printStackTrace();
					}
				}
			}
		}

		return explorerView;
	}

	/**
	 * Gets Oden Snapshot view
	 * 
	 * @param create
	 * @return snapshotView
	 */
	public SnapshotView getSnapshotView(boolean create) {
		if (snapshotView == null) {
			IWorkbenchPage page = getActivePage();
			if (page != null) {
				snapshotView = (SnapshotView) page.findView(SnapshotView.class
						.getName());
				if (snapshotView == null && create) {
					try {
						snapshotView = (SnapshotView) page
								.showView(SnapshotView.class.getName());
					} catch (PartInitException partInitException) {
						partInitException.printStackTrace();
					}
				}
			}
		}

		return snapshotView;
	}

	/**
	 * Gets Oden DeploymentHistory view
	 * 
	 * @param create
	 * @return deploymentHistoryView
	 */
	public DeploymentHistoryView getDeploymentHistoryView(boolean create) {
		if (deploymentHistoryView == null) {
			IWorkbenchPage page = getActivePage();
			if (page != null) {
				deploymentHistoryView = (DeploymentHistoryView) page
						.findView(DeploymentHistoryView.class.getName());
				if (deploymentHistoryView == null && create) {
					try {
						deploymentHistoryView = (DeploymentHistoryView) page
								.showView(DeploymentHistoryView.class.getName());
					} catch (PartInitException partInitException) {
						partInitException.printStackTrace();
					}
				}
			}
		}

		return deploymentHistoryView;
	}

	/**
	 * Gets Oden JobManager view
	 * 
	 * @param create
	 * @return jobmanagerView
	 */
	public JobManagerView getJobManagerView(boolean create) {
		if (jobmanagerView == null) {
			IWorkbenchPage page = getActivePage();
			if (page != null) {
				jobmanagerView = (JobManagerView) page
						.findView(JobManagerView.class.getName());
				if (jobmanagerView == null && create) {
					try {
						jobmanagerView = (JobManagerView) page
								.showView(JobManagerView.class.getName());
					} catch (PartInitException partInitException) {
						partInitException.printStackTrace();
					}
				}
			}
		}

		return jobmanagerView;
	}

	/**
	 * Gets Oden SetFileReqPath dialog
	 * 
	 * @param create
	 * @return jobmanagerView
	 */
	public SetFileReqPathDialog getSetFileReqPathDialog(boolean create) {
		if (setfilereqpathDialog == null) {
			IWorkbenchPage page = getActivePage();
			if (page != null) {
				setfilereqpathDialog = (SetFileReqPathDialog) page
						.findView(SetFileReqPathDialog.class.getName());

				if (setfilereqpathDialog == null && create) {
					try {
						setfilereqpathDialog = (SetFileReqPathDialog) page
								.showView(SetFileReqPathDialog.class.getName());
					} catch (PartInitException partInitException) {
						partInitException.printStackTrace();
					}
				}
			}
		}

		return setfilereqpathDialog;
	}

	/**
	 * 
	 * @param explorerView
	 */
	public void setExplorerView(ExplorerView explorerView) {
		this.explorerView = explorerView;
	}

	/**
	 * 
	 * @param snapshotView
	 */
	public void setSnapshotView(SnapshotView snapshotView) {
		this.snapshotView = snapshotView;
	}

	/**
	 * 
	 * @param jobmanagerView
	 */
	public void setJobManagerView(JobManagerView jobmanagerView) {
		this.jobmanagerView = jobmanagerView;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Error handling
	 * 
	 * @param message
	 * @param throwable
	 */
	public static void error(String message, Throwable throwable) {
		getDefault().getLog().log(
				new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, String
						.valueOf(message), throwable));
		odenLogger.error(message, throwable);
	}

	/**
	 * Warning handling
	 * 
	 * @param message
	 * @param throwable
	 */
	public static void warning(String message) {
		getDefault().getLog()
				.log(
						new Status(IStatus.WARNING, PLUGIN_ID, String
								.valueOf(message)));
		odenLogger.warn(message);
	}

	/**
	 * @see anyframe.oden.eclipse.core.OdenActivator#getExplorerView()
	 * @see anyframe.oden.eclipse.core.explorer.ExplorerView#getSite()
	 * @return null if getExplorerView() is null, otherwise
	 *         explorerView.getSite()
	 */
	public IWorkbenchSite getSite() {
		if (getExplorerView() == null) {
			return null;
		}
		return explorerView.getSite();
	}

	private IWorkbenchPage getActivePage() {
		if (getWorkbench() != null
				&& getWorkbench().getActiveWorkbenchWindow() != null)
			return getWorkbench().getActiveWorkbenchWindow().getActivePage();
		return null;
	}

}
