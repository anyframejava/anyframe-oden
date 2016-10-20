/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package anyframe.oden.eclipse.core.snapshot.actions;

import java.util.StringTokenizer;

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
 * Rollback snapshot file to destination. This class extends
 * AbstractSnapshotViewAction class.
 * 
 * @author LEE Sujeong
 * @version 1.0.0 RC2
 * 
 */
public class RollbackSnapshotAction extends AbstractSnapshotViewAction {

	/**
	 * Constructor of RollbackSnapshotAction forward actionId,
	 * actionTooltipText, actionIconId to AbstractSnapshotViewAction
	 */
	public RollbackSnapshotAction() {
		super(
				UIMessages.ODEN_SNAPSHOT_Actions_RollbackSnapshotAction_Rollback,
				UIMessages.ODEN_SNAPSHOT_Actions_RollbackSnapshotAction_RollbackTooltip,
				UIMessages.ODEN_SNAPSHOT_Actions_RollbackSnapshotAction_RollbackIcon);
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
					UIMessages.ODEN_SNAPSHOT_Actions_SelectSnapshotRollback,
					MessageDialog.INFORMATION);
		} else {

			String selection = OdenActivator.getDefault().getSnapshotView()
					.getSelected()[0].toString();

			if (DialogUtil
					.confirmMessageDialog(
							UIMessages.ODEN_SNAPSHOT_SnapshotView_MsgDlgRollback,
							UIMessages.ODEN_SNAPSHOT_Actions_MsgDlgRollbackConfirm
									+ selection
									+ CommonMessages.ODEN_CommonMessages_Confirm_MessageSuf)) {
				SnapshotView.setStatusMessage(""); //$NON-NLS-1$

				StringTokenizer token = new StringTokenizer(selection, " "); //$NON-NLS-1$
				String fileName = token.nextToken();

				SnapshotView.setStatusMessage(""); //$NON-NLS-1$

				try {
					SnapshotStatusProgress
							.statusProgress(CommandMessages.ODEN_SNAPSHOT_Actions_MsgRollback
									+ fileName + " -json"); //$NON-NLS-1$

				} catch (OdenException e) {
					OdenActivator
							.error(
									UIMessages.ODEN_SNAPSHOT_Actions_Exception_RollbackSnapshot,
									e);
				}

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
