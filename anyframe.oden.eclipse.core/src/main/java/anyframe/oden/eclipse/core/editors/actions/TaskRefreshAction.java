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
package anyframe.oden.eclipse.core.editors.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import anyframe.oden.eclipse.core.editors.OdenEditor;
import anyframe.oden.eclipse.core.editors.TaskDetails;
import anyframe.oden.eclipse.core.editors.TaskPage;
import anyframe.oden.eclipse.core.history.AbstractDeploymentHistoryViewAction;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * The Implementation of RefreshAction,
 * for the Anyframe Oden Deployment History view.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 *
 */
public class TaskRefreshAction extends AbstractDeploymentHistoryViewAction {
	private String title;
	public TaskRefreshAction(String title) {
		super(
				UIMessages.ODEN_SNAPSHOT_Actions_RefreshAction_Refresh,
				UIMessages.ODEN_SNAPSHOT_Actions_RefreshAction_RefreshTooltip,
				UIMessages.ODEN_SNAPSHOT_Actions_RefreshAction_RefreshIcon);
		this.title = title;
		
	}

	public void run() {
		TaskPage page = OdenEditor.getDefault(title).getTaskpage();
		page.loadInitData();
		page.loadInitPolicyData();
		TaskDetails details = new TaskDetails();
		ISelection selection = page.getTaskViewer().getSelection();
		if(!(selection.isEmpty()) && ! page.chkNewTask()) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			details = (TaskDetails) obj;
			page.showTaskDetail(details.getTaskName());
		} else {
			page.clearText();
			page.getClient().setVisible(false);
		}
		page.getTaskViewer().refresh();
		page.getRunViewer().refresh();
		page.getAddTask().setEnabled(true);
	}

}
