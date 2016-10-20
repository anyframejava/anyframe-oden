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

import org.eclipse.swt.widgets.Display;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.editors.AbstractEditorsAction;
import anyframe.oden.eclipse.core.editors.OdenEditor;
import anyframe.oden.eclipse.core.editors.PolicyPage;
import anyframe.oden.eclipse.core.editors.dialogs.SelectAgentsDialog;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Add a new server action in the Oden view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC2
 * 
 */
public class NewDeployAction extends AbstractEditorsAction {

	/**
	 * 
	 */
	
	public NewDeployAction() {
		super(
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskSaveBtn,
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskSaveBtn,
				UIMessages.ODEN_EDITORS_TaskPage_TaskPageTitleImage);
	}
	
	/**
	 * New DeployAction Run 
	 */
	public void run(String title) {
		SelectAgentsDialog dialog;
		PolicyPage page = OdenEditor.getDefault(title).getPolicypage();
		
		try {
			dialog = new SelectAgentsDialog(Display.getCurrent()
					.getActiveShell(), page.getShellUrl(),page);
			dialog.open();
		} catch (Exception odenException) {
			OdenActivator.error(
					"Exception occured while open select Server dialog.",
					odenException);
		}
	}

}
