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
package anyframe.oden.eclipse.core.dashboard;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;

import anyframe.oden.eclipse.core.dashboard.actions.TransferResultAction;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Constructs an Action group for Dashboard of Anyframe Oden Eclipse plug-in.
 * 
 * @author LEE sujeong
 * @version 1.1.0
 * 
 */
public class DashboardActionGroup extends ActionGroup {

	private String selection = "";

	@Override
	public void fillContextMenu(IMenuManager menu) {
		IWorkbenchPage workbenchPage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IEditorPart[] editors = workbenchPage.getEditors();
		Dashboard dash = (Dashboard) editors[0];
		DashboardPage page = dash.getDashboardPage();

		this.selection = (page == null) ? "" : page.getTransferResult();

		TableItem[] item = page.getSelection();
		int select = 0;

		for (int i = 0; i < item.length; i++) {
			if (item[i].getText(5).equals(
					UIMessages.ODEN_DASHBOARD_DashboardPage_FetchlogNotyet)) {
				select++;
			} else {
				break;
			}
		}

		if (select == item.length) {
			addAction(menu, new TransferResultAction(item, page.getShellURL()));
		} else {

		}

	}

	private boolean addAction(IMenuManager menu, TransferResultAction action) {
		if (action.isAvailable()) {
			menu.add(action);
			action.setEnabled(true);
			return true;
		}
		return false;
	}
}
