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

import org.eclipse.swt.widgets.Display;

import anyframe.oden.eclipse.core.alias.DeployNow;
import anyframe.oden.eclipse.core.explorer.AbstractExplorerViewAction;
import anyframe.oden.eclipse.core.explorer.dialogs.SetDeployNowDialog;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Set Deploy Now action in the Oden view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author HongJungHwan
 * @version 1.0.0 RC1
 * 
 */
public class SetDeployNowAction extends AbstractExplorerViewAction {

	/**
	 * 
	 */
	private Object obj;
	public Object getObj() {
		return obj;
	}

	public SetDeployNowAction(Object obj) {
		super(
				UIMessages.ODEN_EXPLORER_Actions_SetDeployNowAction_SetDeployNow,
				UIMessages.ODEN_EXPLORER_Actions_SetDeployNowAction_SetDeployNowToolTip,
				UIMessages.ODEN_EXPLORER_Actions_SetDeployNowAction_SetDeployNowIcon);
		this.obj = obj;
	}

	/**
	 * 
	 */
	public void run() {
		Set<DeployNow> deploynows  = getView().getSelectedDeploynow(true);
		
		SetDeployNowDialog dialog ;
		if(deploynows != null) {
			dialog = new SetDeployNowDialog(Display.getCurrent().getActiveShell() , this.getObj() , deploynows);
		} else {
			dialog = new SetDeployNowDialog(Display.getCurrent().getActiveShell() , this.getObj() , new DeployNow());
		}
			
		dialog.open();
		if(dialog.getReturnCode() == 0)
			getView().refresh();
	}
}
