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

import org.eclipse.swt.widgets.Display;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.explorer.AbstractExplorerViewAction;
import anyframe.oden.eclipse.core.explorer.dialogs.DeployNowDialog;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Run task action in the Oden view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author HongJungHwan
 * @version 1.0.0 RC1
 * 
 */
public class RunDeployTaskAction extends AbstractExplorerViewAction {

	/**
	 * 
	 */
	private Object[] obj;

	public RunDeployTaskAction(Object[] obj) {

		super(
				UIMessages.ODEN_EXPLORER_Actions_RunDeployAction_DeployNow,
				UIMessages.ODEN_EXPLORER_Actions_RunDeployAction_DeployNowToolTip,
				UIMessages.ODEN_EXPLORER_Actions_RunDeployAction_DeployNowIcon);

		this.obj = obj;
	}

	/**
	 * 
	 */
	public void run() {

		DeployNowDialog dialog;
		try {
			dialog = new DeployNowDialog(Display.getCurrent().getActiveShell(),
					obj , null , null);
			dialog.open();
		} catch (Exception odenException) {
			OdenActivator.error(
					"Exception occured while searching deploy items.",
					odenException);
			odenException.printStackTrace();
		}

	}
}
