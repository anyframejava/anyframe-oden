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
import anyframe.oden.eclipse.core.utils.DialogUtil;

/**
 * Take a snapshot plan in Snapshot view. This class extends
 * AbstractSnapshotViewAction class.
 * 
 * @author LEE Sujeong
 * @version 1.0.0 RC2
 * 
 */
public class TakeSnapshotPlanAction extends AbstractSnapshotViewAction {

	/**
	 * Constructor of TakeSnapshotPlanAction forward actionId,
	 * actionTooltipText, actionIconId to AbstractSnapshotViewAction
	 */
	public TakeSnapshotPlanAction() {
		super(
				UIMessages.ODEN_SNAPSHOT_Actions_TakeSnapshotPlanAction_TakeSnapshot,
				UIMessages.ODEN_SNAPSHOT_Actions_TakeSnapshotPlanAction_TakeSnapshotTooltip,
				UIMessages.ODEN_SNAPSHOT_Actions_TakeSnapshotPlanAction_TakeSnapshotIcon);
	}

	/**
	 * Run this Action
	 */
	public void run() {
		Object selected = OdenActivator.getDefault().getSnapshotView()
		.getSelected();
		if(selected==null){
			DialogUtil.openMessageDialog(UIMessages.ODEN_SNAPSHOT_Actions_MsgInfoAddPlan,
					UIMessages.ODEN_SNAPSHOT_Actions_SelectSnapshotPlanTake,
					MessageDialog.INFORMATION);
		}else{
			String selection = OdenActivator.getDefault().getSnapshotView()
			.getSelected()[0].toString();
			
			if (DialogUtil.confirmMessageDialog(
					UIMessages.ODEN_SNAPSHOT_Actions_MsgDlgSnapshot,
					UIMessages.ODEN_SNAPSHOT_Actions_MsgDlgSnapshotConfirm
					+ selection
					+ CommonMessages.ODEN_CommonMessages_Confirm_MessageSuf)) {
				try {
					SnapshotStatusProgress
					.statusProgress(CommandMessages.ODEN_SNAPSHOT_Actions_MsgRun
							+ "\"" + selection + UIMessages.ODEN_SNAPSHOT_TakeSnapshotPlanAction_1 + " -json"); //$NON-NLS-1$ //$NON-NLS-2$
				} catch (OdenException e) {
					OdenActivator.error(UIMessages.ODEN_SNAPSHOT_Actions_Exception_TakeSnapshot, e);
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
