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

import java.util.Set;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.explorer.AbstractExplorerViewAction;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.DialogUtil;

/**
 * Edit an existing server action in the Oden view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class DeleteServerAction extends AbstractExplorerViewAction {

	/**
	 * 
	 */
	private Server server;

	public DeleteServerAction() {
		super(
				UIMessages.ODEN_EXPLORER_Actions_DeleteServerAction_RemoveServer,
				UIMessages.ODEN_EXPLORER_Actions_DeleteServerAction_RemoveServerToolTip,
				UIMessages.ODEN_EXPLORER_Actions_DeleteServerAction_RemoveServerIcon);
	}

	/**
	 * run deleteServerAction
	 */
	public void run() {
		server = getView().getSelectedServer(false);

		if (DialogUtil.confirmMessageDialog(
				CommonMessages.ODEN_CommonMessages_Title_ConfirmDelete,
				UIMessages.ODEN_EXPLORER_Actions_DeleteServerAction_ConfirmDelete_MessagePre +
				server.getNickname() +
				CommonMessages.ODEN_CommonMessages_Confirm_MessageSuf)) {

			if (server != null) {
				OdenActivator.getDefault().getAliasManager().getServerManager().removeServer(server.getNickname());
			}

			// notify that there has been changes
			OdenActivator.getDefault().getAliasManager().getServerManager().modelChanged();

			// reload data for data consistency
			try {
				OdenActivator.getDefault().getAliasManager().save();
				OdenActivator.getDefault().getAliasManager().load();
			} catch (OdenException odenException) {
				OdenActivator.error("Exception occured while reloading Oden Server profiles.", odenException);
				odenException.printStackTrace();
			}

			getView().refresh();
			closePolicyTaskEditorOpenedViaDeletedServer();
		}
	}

	/**
	 * 
	 */
	public boolean isAvailable() {
		if (getView() == null) {
			return false;
		}

		Set<Server> servers = getView().getSelectedServers(false);

		if (servers.isEmpty()) {
			return false;
		}

		return true;
	}
	private void closePolicyTaskEditorOpenedViaDeletedServer() {
		IWorkbenchPage Page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if(Page !=null) {
			IEditorReference[] editors = Page.getEditorReferences();
			for(IEditorReference editor : editors) {
				if(editor.getTitle().equals(server.getNickname()))
					Page.closeEditor(editor.getEditor(false), false);
			}
		}
	}
}
