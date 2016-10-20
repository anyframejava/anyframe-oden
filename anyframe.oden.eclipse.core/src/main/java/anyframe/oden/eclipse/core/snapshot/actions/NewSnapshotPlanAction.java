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
package anyframe.oden.eclipse.core.snapshot.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.snapshot.AbstractSnapshotViewAction;
import anyframe.oden.eclipse.core.snapshot.SnapshotView;
import anyframe.oden.eclipse.core.snapshot.dialogs.SnapshotNewPlanDialog;
import anyframe.oden.eclipse.core.snapshot.dialogs.SnapshotNewPlanDialog.Type;
import anyframe.oden.eclipse.core.utils.DialogUtil;

/**
 * Add a new Snapshot plan action in the Snapshot view. This class extends
 * AbstractSnapshotViewAction class.
 * 
 * @author LEE Sujeong
 * @version 1.0.0 RC2
 * 
 */
public class NewSnapshotPlanAction extends AbstractSnapshotViewAction {

	/**
	 * Constructor of NewSnapshotAction forward actionId, actionTooltipText,
	 * actionIconId to AbstractSnapshotViewAction
	 */
	public NewSnapshotPlanAction() {
		super(
				UIMessages.ODEN_SNAPSHOT_Actions_NewSnapshotPlanAction_NewSnapshotPlan,
				UIMessages.ODEN_SNAPSHOT_Actions_NewSnapshotPlanAction_NewSnapshotPlanTooltip,
				UIMessages.ODEN_SNAPSHOT_Actions_NewSnapshotPlanAction_NewSnapshotPlanIcon);
	}

	/**
	 * Run this Action with message box
	 */
	public void run() {
		if (SnapshotView.SHELL_URL == null) {
			DialogUtil.openMessageDialog(
					UIMessages.ODEN_SNAPSHOT_Actions_MsgInfoAddPlan,
					UIMessages.ODEN_SNAPSHOT_Actions_MsgAlertAddPlan,
					MessageDialog.WARNING);
		} else {
				SnapshotNewPlanDialog dialog = new SnapshotNewPlanDialog(
						Display.getCurrent().getActiveShell(), Type.ADD, ""); //$NON-NLS-1$
				dialog.open();
		}
	}

	/**
	 * Available check this method
	 */
	public boolean isAvailable() {
		if (getView() == null) {
			return false;
		}

		return true;
	}
}
