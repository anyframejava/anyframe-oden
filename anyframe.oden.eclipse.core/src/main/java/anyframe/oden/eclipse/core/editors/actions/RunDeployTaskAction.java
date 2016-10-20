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
import org.eclipse.swt.widgets.Display;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.editors.AbstractEditorsAction;
import anyframe.oden.eclipse.core.editors.OdenEditor;
import anyframe.oden.eclipse.core.editors.TaskDetails;
import anyframe.oden.eclipse.core.editors.TaskPage;
import anyframe.oden.eclipse.core.explorer.dialogs.DeployNowDialog;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Add a new server action in the Oden view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class RunDeployTaskAction extends AbstractEditorsAction {

	/**
	 * 
	 */
	public RunDeployTaskAction() {
		super(
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskRunBtn,
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskRunBtn,
				UIMessages.ODEN_EDITORS_TaskPage_RunDeployImage);
	}

	/**
	 * 
	 */
	public void run(String title) {
		TaskPage page = OdenEditor.getDefault(title).getTaskpage();
		DeployNowDialog dialog;
		TaskDetails details = null;
		ISelection selection = page.getTaskViewer().getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		details = (TaskDetails) obj;
		try {
			dialog = new DeployNowDialog(Display.getCurrent().getActiveShell(),
					null , details.getTaskName() , page.getShellUrl());
			dialog.open();
		} catch (Exception odenException) {
			OdenActivator.error(
					"Exception occured while searching deploy items.",
					odenException);
			odenException.printStackTrace();
		}
	}

}
