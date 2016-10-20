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
package anyframe.oden.eclipse.core.history.actions;

import org.eclipse.ui.PlatformUI;

import anyframe.oden.eclipse.core.history.AbstractDeploymentHistoryViewAction;
import anyframe.oden.eclipse.core.history.dialogs.AdvancedSearchDialog;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * The Implementation of Advanced Search,
 * for the Anyframe Oden Deployment History view.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 *
 */
public class AdvancedSearchAction extends AbstractDeploymentHistoryViewAction {

	public AdvancedSearchAction() {
		super(UIMessages.ODEN_HISTORY_Actions_AdvancedSearchAction_AdvancedSearch, UIMessages.ODEN_HISTORY_Actions_AdvancedSearchAction_AdvancedSearchToolTip, UIMessages.ODEN_HISTORY_Actions_AdvancedSearchAction_AdvancedSearchIcon);
	}

	public void run() {
		AdvancedSearchDialog dialog = new AdvancedSearchDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), getView());
		dialog.open();
	}

}
