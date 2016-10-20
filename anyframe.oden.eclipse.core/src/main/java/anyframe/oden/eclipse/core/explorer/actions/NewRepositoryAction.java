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

import anyframe.oden.eclipse.core.alias.Repository;
import anyframe.oden.eclipse.core.explorer.AbstractExplorerViewAction;
import anyframe.oden.eclipse.core.explorer.dialogs.CreateBuildRepositoryDialog;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Add a new build repository action in the Oden view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class NewRepositoryAction extends AbstractExplorerViewAction {

	/**
	 * 
	 */
	public NewRepositoryAction() {
		super(
				UIMessages.ODEN_EXPLORER_Actions_NewRepositoryAction_CreateRepository,
				UIMessages.ODEN_EXPLORER_Actions_NewRepositoryAction_CreateRepositoryToolTip,
				UIMessages.ODEN_EXPLORER_Actions_NewRepositoryAction_CreateRepositoryIcon);
	}

	/**
	 * 
	 */
	public void run() {
		CreateBuildRepositoryDialog dialog = new CreateBuildRepositoryDialog(
				Display.getCurrent().getActiveShell(),
				CreateBuildRepositoryDialog.Type.CREATE, new Repository());
		dialog.open();
		if(dialog.getReturnCode() == 0)
			getView().refresh();
	}

}
