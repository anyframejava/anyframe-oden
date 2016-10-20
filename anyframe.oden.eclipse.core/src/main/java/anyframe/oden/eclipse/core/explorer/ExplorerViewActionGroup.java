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
package anyframe.oden.eclipse.core.explorer;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenTrees.TreeObject;
import anyframe.oden.eclipse.core.OdenTrees.TreeParent;
import anyframe.oden.eclipse.core.alias.Repository;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.explorer.actions.CompareAgentAction;
import anyframe.oden.eclipse.core.explorer.actions.DeleteRepositoryAction;
import anyframe.oden.eclipse.core.explorer.actions.DeleteServerAction;
import anyframe.oden.eclipse.core.explorer.actions.DuplicateRepositoryAction;
import anyframe.oden.eclipse.core.explorer.actions.DuplicateServerAction;
import anyframe.oden.eclipse.core.explorer.actions.EditRepositoryAction;
import anyframe.oden.eclipse.core.explorer.actions.EditServerAction;
import anyframe.oden.eclipse.core.explorer.actions.NewRepositoryAction;
import anyframe.oden.eclipse.core.explorer.actions.NewServerAction;
import anyframe.oden.eclipse.core.explorer.actions.OpenDashboardAction;
import anyframe.oden.eclipse.core.explorer.actions.OpenPolicyTaskAction;
import anyframe.oden.eclipse.core.explorer.actions.RunDeployTaskAction;
import anyframe.oden.eclipse.core.explorer.actions.SetDeployNowAction;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Constructs an Action group for Oden Explorer view of Anyframe Oden Eclipse
 * plug-in.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class ExplorerViewActionGroup extends ActionGroup {

	/**
	 * Fills the context menu
	 * 
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(IMenuManager)
	 */
	@SuppressWarnings("deprecation")
	public void fillContextMenu(IMenuManager contextmenu) {
		ExplorerView view = OdenActivator.getDefault().getExplorerView();
		Object[] selection = (view == null) ? null : view.getSelected();
		boolean chk = false;
		boolean chkDash = false;

		IWorkbenchWindow win = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();

		IWorkbenchPage page = win.getActivePage();
		IEditorPart[] editors = page.getEditors();

		if (editors != null) {
			for (IEditorPart editor : editors) {
				if (selection != null) {
					String dashName = ((TreeObject) selection[0]).getName()
							+ UIMessages.ODEN_DASHBOARD_DashboardPage_DashboardSuf;
					if (((TreeObject) selection[0]).getName().equals(
							editor.getTitle())) {
						chk = true;
					}
					if (dashName.equals(editor.getTitle())) {
						chkDash = true;
					}
				} else {
					// If nothing is selected, show default context menu
					addAction(contextmenu, new NewServerAction());
					addAction(contextmenu, new NewRepositoryAction());
					return;
				}
			}
		}

		TreeParent parent;
		String parentName;

		if (selection != null) {
			parent = ((TreeObject) selection[0]).getParent();
			parentName = parent.getName();
		} else {
			// If nothing is selected, show default context menu
			addAction(contextmenu, new NewServerAction());
			addAction(contextmenu, new NewRepositoryAction());
			return;
		}

		// If multiple folders or files are selected , show deploy now context
		// menu
		if (selection == null || selection.length != 1) {
			if (chekckSameRepo(selection))
				if (!(parentName
						.equals(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoriesRootLabel) || ((TreeObject) selection[0])
						.getName()
						.equals(
								UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoriesRootLabel))) {
					if (((TreeObject) selection[0])
							.getParent()
							.getParent()
							.getName()
							.equals(
									UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoriesRootLabel)) {
						addAction(contextmenu,
								new SetDeployNowAction(selection));
					} else if (((TreeObject) selection[0])
							.getParent()
							.getParent()
							.getName()
							.equals(
									UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_ServersRootLabel)) {
						addAction(contextmenu,
								new CompareAgentAction(selection));
					} else {
					}
				}
			return;
		}

		if (parentName
				.equals(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_ServersRootLabel)
				|| ((TreeObject) selection[0])
						.getName()
						.equals(
								UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_ServersRootLabel)) {

			if (OdenActivator.getDefault().getAliasManager().getServerManager()
					.getServer(selection[0].toString()) instanceof Server) {
				addAction(contextmenu, new EditServerAction());
				addAction(contextmenu, new DuplicateServerAction());
				addAction(contextmenu, new DeleteServerAction());
				if ((!chk) || (!chkDash)) {
					// add seperator
					contextmenu.add(new Separator());
					if (!chk) {
						// add Open Poliy/Task
						addAction(contextmenu, new OpenPolicyTaskAction());
					}
					if (!chkDash) {
						// add Open Dashboard
						addAction(contextmenu, new OpenDashboardAction());
					}
				} else {
				}
			} else {
				// Root
				addAction(contextmenu, new NewServerAction());
			}
		} else if (parentName
				.equals(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoriesRootLabel)
				|| ((TreeObject) selection[0])
						.getName()
						.equals(
								UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoriesRootLabel)) {
			if (OdenActivator.getDefault().getAliasManager()
					.getRepositoryManager().getRepository(
							selection[0].toString()) instanceof Repository) {
				addAction(contextmenu, new EditRepositoryAction());
				addAction(contextmenu, new DuplicateRepositoryAction());
				addAction(contextmenu, new DeleteRepositoryAction());
				// add set Deploy Now Information
				contextmenu.add(new Separator());
				addAction(contextmenu, new SetDeployNowAction(selection[0]));

			} else {
				// Root
				addAction(contextmenu, new NewRepositoryAction());
			}
		} else {
			if (parent.getParent() != null) {
				if (parent.getParent().getParent() != null) {
					if (parent.getParent().getParent().getName().equals("")
							&& parent
									.getParent()
									.getName()
									.equals(
											UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_ServersRootLabel)) {
					} else {
						// Repository directory or file add Deploy Now
						addAction(contextmenu, new RunDeployTaskAction(
								selection));
					}
				} else {
					// Repository directory or file add Deploy Now
					addAction(contextmenu, new RunDeployTaskAction(selection));
				}
			} else {
				// Repository directory or file add Deploy Now
				addAction(contextmenu, new RunDeployTaskAction(selection));
			}
		}
	}

	private boolean addAction(IMenuManager menu,
			AbstractExplorerViewAction action) {
		if (action.isAvailable()) {
			menu.add(action);
			action.setEnabled(true);
			return true;
		}
		return false;
	}

	private boolean chekckSameRepo(Object[] selections) {
		String compare = "";
		String orgin = "";
		int i = 0;
		for (Object selection : selections) {
			i = i + 1;
			if (i == 1) {
				String temp = fullpath(selection);
				String[] tempArr = temp.split("/");
				orgin = tempArr[1];
			} else {
				String temp = fullpath(selection);
				String[] tempArr = temp.split("/");
				compare = tempArr[1];
				if (!(orgin.equals(compare))) {
					return false;
				}
			}
		}
		return true;
	}

	private String fullpath(Object selection) {
		TreeObject obj = ((TreeObject) selection);
		StringBuffer full = new StringBuffer(obj.getName());
		TreeParent parent = obj.getParent();

		while (parent != null) {
			full.insert(0, parent.getName() + "/");
			parent = parent.getParent();
		}
		if (full.toString().substring(0, 1).equals("/")) {
			return full.toString().substring(1);
		} else {
			return full.toString();
		}
	}

}
