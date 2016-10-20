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
							.statusProgress(CommandMessages.ODEN_SNAPSHOT_Actions_MsgDelPlan
									+ "\"" + selection + UIMessages.ODEN_SNAPSHOT_TakeSnapshotPlanAction_1 //$NON-NLS-1$
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
