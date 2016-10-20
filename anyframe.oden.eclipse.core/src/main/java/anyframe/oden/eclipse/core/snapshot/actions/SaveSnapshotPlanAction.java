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
import anyframe.oden.eclipse.core.snapshot.SnapshotView;
import anyframe.oden.eclipse.core.snapshot.SnapshotViewContentProvider;
import anyframe.oden.eclipse.core.utils.DialogUtil;

/**
 * Edit an existing plan action in the Snapshot view. This class extends
 * AbstractSnapshotViewAction class.
 * 
 * @author LEE Sujeong
 * @version 1.0.0 RC2
 * 
 */
public class SaveSnapshotPlanAction extends AbstractSnapshotViewAction {

	/**
	 * Constructor of SaveSnapshotPlanAction forward actionId,
	 * actionTooltipText, actionIconId to AbstractSnapshotViewAction
	 */
	public SaveSnapshotPlanAction() {
		super(
				UIMessages.ODEN_SNAPSHOT_Actions_SaveSnapshotPlanAction_NewPlan,
				UIMessages.ODEN_SNAPSHOT_Actions_SaveSnapshotPlanAction_NewPlanTooltip,
				UIMessages.ODEN_SNAPSHOT_Actions_SaveSnapshotPlanAction_NewPlanIcon);
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
					UIMessages.ODEN_SNAPSHOT_Actions_SelectSnapshotPlanSave,
					MessageDialog.INFORMATION);
		} else {

			String selection = OdenActivator.getDefault().getSnapshotView()
					.getSelected()[0].toString();
			String source = SnapshotView.sourceLocation;
			String destAgent = SnapshotView.destAgent;
			String desc = SnapshotView.description;

			String msg = "";
			if(source.equals("") || source==null){ //default-location
				msg = CommandMessages.ODEN_CLI_COMMAND_snapshot_add + " " 
				+ "\"" //$NON-NLS-1$
				+ selection
				+ "\"" + " " + CommandMessages.ODEN_CLI_OPTION_plansource + " " + destAgent //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-6$
				+ " "
				+ CommandMessages.ODEN_CLI_OPTION_desc + " " + "\"" + desc + "\"" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ " -json"; //$NON-NLS-1$
			}else{ //non default-location
				msg = CommandMessages.ODEN_CLI_COMMAND_snapshot_add + " " 
				+ "\"" //$NON-NLS-1$
				+ selection
				+ "\"" + " " + CommandMessages.ODEN_CLI_OPTION_plansource + " " + destAgent //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-6$
				+ ":"
				+ source
				+ " " + CommandMessages.ODEN_CLI_OPTION_desc + " " + "\"" + desc + "\"" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ " -json"; //$NON-NLS-1$
			}
			

			if (DialogUtil.confirmMessageDialog(
					CommonMessages.ODEN_CommonMessages_Title_ConfirmSave,
					UIMessages.ODEN_SNAPSHOT_Actions_MsgDlgAlertSavePlan)) {
				try {
					SnapshotViewContentProvider.doOdenBroker(
							SnapshotView.SHELL_URL, msg);
				} catch (OdenException e) {
					OdenActivator
							.error(
									UIMessages.ODEN_SNAPSHOT_Actions_Exception_SaveSnapshotPlan,
									e);
					SnapshotView.clearComposite();
				}

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
