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
package anyframe.oden.eclipse.core.explorer.actions;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.dashboard.DashboardEditorInput;
import anyframe.oden.eclipse.core.explorer.AbstractExplorerViewAction;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Open selected server's dashboard. This class extends AbstractExplorerViewAction class.
 * @author LEE Sujeong
 * 
 */
public class OpenDashboardAction extends AbstractExplorerViewAction {
	
	public OpenDashboardAction() {
		super(
				UIMessages.ODEN_EXPLORER_Actions_OpenDashboardAction_OpenDashboard,
				UIMessages.ODEN_EXPLORER_Actions_OpenDashboardAction_OpenDashboardToolTip,
				UIMessages.ODEN_EXPLORER_Actions_OpenDashboardAction_OpenDashboardIcon);
	}
	
	public void run() {

		Server server = getView().getSelectedServer(false);
		if (server != null) {

			IWorkbenchPage Page = PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getActivePage();
			try {
				DashboardEditorInput editorInput = new DashboardEditorInput(server);
				Page.openEditor(editorInput,
						UIMessages.ODEN_EXPLORER_Actions_OpenDashboardAction_DashboardEditorId);
			} catch (Exception odenException) {
				OdenActivator.error(
						UIMessages.ODEN_EXPLORER_Actions_OpenDashboardAction_Exception_OpenDashbaord,
						odenException);
			}
		}
	}
	
	public boolean isAvailable() {
		if (getView() == null)
			return false;
		return getView().getSelectedServers(false).size() == 1;
	}
}
