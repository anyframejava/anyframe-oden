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

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.snapshot.AbstractSnapshotViewAction;
import anyframe.oden.eclipse.core.snapshot.SnapshotStatusProgress;
import anyframe.oden.eclipse.core.snapshot.SnapshotView;
import anyframe.oden.eclipse.core.utils.DialogUtil;

/**
 * Delete snapshot file in Snapshot view. This class extends
 * AbstractSnapshotViewAction class.
 * 
 * @author LEE Sujeong
 * @version 1.0.0 RC2
 * 
 */
public class DeleteSnapshotPlanAction extends AbstractSnapshotViewAction {

	/**
	 * Constructor of DeleteSnapshotAction forward actionId, actionTooltipText,
	 * actionIconId to AbstractSnapshotViewAction
	 */
	public DeleteSnapshotPlanAction() {
		super(
				UIMessages.ODEN_SNAPSHOT_Actions_DeleteSnpahsotPlanAction_DeletePlan,
				UIMessages.ODEN_SNAPSHOT_Actions_DeleteSnpahsotPlanAction_DeletePlanTooltip,
				UIMessages.ODEN_SNAPSHOT_Actions_DeleteSnpahsotPlanAction_DeletePlanIcon);
	}

	/**
	 * Run this Action with message box
	 */
	public void run() {

		Object selected = OdenActivator.getDefault().getSnapshotView()
				.getSelected();
		if (selected == null) {
			DialogUtil.openMessageDialog(
					UIMessages.ODEN_SNAPSHOT_Actions_MsgInfoAddPlan,
					UIMessages.ODEN_SNAPSHOT_Actions_SelectSnapshotPlanDelete,
					MessageDialog.INFORMATION);
		} else {
			String selection = OdenActivator.getDefault().getSnapshotView()
					.getSelected()[0].toString();

			if (DialogUtil
					.confirmMessageDialog(
							CommonMessages.ODEN_CommonMessages_Title_ConfirmDelete,
							UIMessages.ODEN_SNAPSHOT_Actions_MsgDlgReally
									+ selection
									+ CommonMessages.ODEN_CommonMessages_Confirm_MessageSuf)) {
				try {
					SnapshotStatusProgress
							.statusProgress(CommandMessages.ODEN_CLI_COMMAND_snapshot_plandel
									+ " " + "\"" + selection + UIMessages.ODEN_SNAPSHOT_TakeSnapshotPlanAction_1 //$NON-NLS-1$
									+ " -json"); //$NON-NLS-1$
				} catch (OdenException e) {
					OdenActivator
							.error(
									UIMessages.ODEN_SNAPSHOT_Actions_Exception_DeleteSnapshotPlan,
									e);
				}
				SnapshotView.clearComposite();
				SnapshotView.refreshTree();
			}
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
