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

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.snapshot.AbstractSnapshotViewAction;
import anyframe.oden.eclipse.core.snapshot.SnapshotStatusProgress;
import anyframe.oden.eclipse.core.snapshot.SnapshotView;
import anyframe.oden.eclipse.core.snapshot.SnapshotViewContentProvider;
import anyframe.oden.eclipse.core.utils.DialogUtil;

/**
 * Delete snapshot plan in Snapshot view. This class extends
 * AbstractSnapshotViewAction class.
 * 
 * @author LEE Sujeong
 * @version 1.0.0 RC2
 * 
 */
public class DeleteSnapshotAction extends AbstractSnapshotViewAction {

	/**
	 * Constructor of DeleteSnapshotAction forward actionId, actionTooltipText,
	 * actionIconId to AbstractSnapshotViewAction
	 */
	public DeleteSnapshotAction() {
		super(
				UIMessages.ODEN_SNAPSHOT_Actions_DeleteSnapshotAction_DeleteSnapshot,
				UIMessages.ODEN_SNAPSHOT_Actions_DeleteSnapshotAction_DeleteSnapshotTooltip,
				UIMessages.ODEN_SNAPSHOT_Actions_DeleteSnapshotAction_DeleteSnapshotIcon);
	}

	/**
	 * Run this Action with message box
	 */
	public void run() {
		Object[] selectedName = OdenActivator.getDefault().getSnapshotView()
				.getSelected();
		if (selectedName == null) {
			DialogUtil.openMessageDialog(
					UIMessages.ODEN_SNAPSHOT_Actions_MsgInfoAddPlan,
					UIMessages.ODEN_SNAPSHOT_Actions_SelectSnapshotDelete,
					MessageDialog.INFORMATION);
		} else {

			if (selectedName.length == 1) {// 1개
				String selection = OdenActivator.getDefault().getSnapshotView()
						.getSelected()[0].toString();

				if (DialogUtil
						.confirmMessageDialog(
								CommonMessages.ODEN_CommonMessages_Title_ConfirmDelete,
								UIMessages.ODEN_SNAPSHOT_Actions_MsgDlgDelSnapshot
										+ selection
										+ CommonMessages.ODEN_CommonMessages_Confirm_MessageSuf)) {

					try {
						SnapshotStatusProgress
								.statusProgress(CommandMessages.ODEN_CLI_COMMAND_snapshot_filedel
										+ " " + selection + " -json"); //$NON-NLS-1$
					} catch (OdenException e) {
						OdenActivator
								.error(
										UIMessages.ODEN_SNAPSHOT_Actions_Exception_DeleteSnapshot,
										e);
					}

					SnapshotView.clearComposite();
					SnapshotView.refreshTree();
				}
			} else {// 2개 이상
				ArrayList<String> selected = new ArrayList<String>();
				for (int i = 0; i < selectedName.length; i++) {
					selected.add(selectedName[i].toString());
				}
				if (checkAllFile(selected)) {// all file
					if (DialogUtil
							.confirmMessageDialog(
									CommonMessages.ODEN_CommonMessages_Title_ConfirmDelete,
									UIMessages.ODEN_SNAPSHOT_Actions_MsgDlgDelSnapshot
											+ selected.toString().substring(
													1,
													selected.toString()
															.length() - 1)
											+ CommonMessages.ODEN_CommonMessages_Confirm_MessageSuf)) {

						try {

							for (int i = 0; i < selectedName.length; i++) {
								SnapshotStatusProgress
										.statusProgress(CommandMessages.ODEN_CLI_COMMAND_snapshot_filedel
												+ " " + selected.get(i) + " -json"); //$NON-NLS-1$
							}

						} catch (OdenException e) {
							OdenActivator
									.error(
											UIMessages.ODEN_SNAPSHOT_Actions_Exception_DeleteSnapshot,
											e);
						}

						SnapshotView.clearComposite();
						SnapshotView.refreshTree();
					}
				} else {// not all file
					DialogUtil.openMessageDialog("Warning", "Selected items are not Snapshot Files. So Can't delete selected items.", MessageDialog.WARNING);
				}
			}

		}

	}

	private boolean checkAllFile(ArrayList<String> selected) {

		boolean result = false;

		JSONArray snapshotFileList = SnapshotViewContentProvider
				.getSnaphotFileList(SnapshotView.SHELL_URL,
						CommandMessages.ODEN_CLI_COMMAND_snapshot_fileinfo
								+ " -json"); //$NON-NLS-1$

		for (int n = 0; n < selected.size(); n++) {

			for (int i = 0; i < snapshotFileList.length(); i++) {

				try {
					JSONObject jo = snapshotFileList.getJSONObject(i);
					for (Iterator it = jo.keys(); it.hasNext();) {
						String fileName = (String) it.next();
						if (selected.get(n).equalsIgnoreCase(fileName)) {
							result = true;
							break;
						} else {
							result = false;
						}
					}
					if (result == true) {
						break;
					}
				} catch (JSONException e) {
					OdenActivator
							.error(
									UIMessages.ODEN_SNAPSHOT_SnapshotView_Exception_GetSnapshotDetailInfo,
									e);
				}
			}
			if (result == false) {
				return result;
			}
		}

		return result;
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
