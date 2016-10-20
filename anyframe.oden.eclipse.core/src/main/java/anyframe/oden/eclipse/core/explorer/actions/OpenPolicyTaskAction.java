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
import anyframe.oden.eclipse.core.alias.ServerEditorInput;
import anyframe.oden.eclipse.core.explorer.AbstractExplorerViewAction;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Open task,policy in the Oden view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author HongJungHwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class OpenPolicyTaskAction extends AbstractExplorerViewAction {

	/**
	 * 
	 */
	public OpenPolicyTaskAction() {
		super(
				UIMessages.ODEN_EXPLORER_Actions_LinkEditorAction_LinkEditor,
				UIMessages.ODEN_EXPLORER_Actions_LinkEditorAction_LinkEditorToolTip,
				UIMessages.ODEN_EXPLORER_Actions_LinkEditorAction_LinkEditorIcon);
	}

	/**
	 * 
	 */
	public void run() {

		Server server = getView().getSelectedServer(false);
		if (server != null) {

			IWorkbenchPage Page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			try {
				ServerEditorInput editorInput = new ServerEditorInput(server);
				Page.openEditor(editorInput,"anyframe.oden.eclipse.core.editors.OdenEditor");
			} catch (Exception odenException) {
				OdenActivator.error(
						UIMessages.ODEN_EXPLORER_Actions_OpenPolicyTaskAction_Exception,
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
