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
import org.eclipse.swt.widgets.Display;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenMessages;
import anyframe.oden.eclipse.core.brokers.OdenBroker;
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

	public static String agent;

	/**
	 * Constructor of DeleteSnapshotAction forward actionId, actionTooltipText,
	 * actionIconId to AbstractSnapshotViewAction
	 */
	public NewSnapshotPlanAction() {
		super(
				OdenMessages.ODEN_SNAPSHOT_Actions_NewSnapshotPlanAction_NewSnapshotPlan,
				OdenMessages.ODEN_SNAPSHOT_Actions_NewSnapshotPlanAction_NewSnapshotPlanTooltip,
				OdenMessages.ODEN_SNAPSHOT_Actions_NewSnapshotPlanAction_NewSnapshotPlanIcon);
	}

	/**
	 * Run this Action with message box
	 */
	public void run() {
		new SnapshotView();
		if (agent == null) {

			DialogUtil.openMessageDialog(
					OdenMessages.ODEN_SNAPSHOT_Actions_MsgInfoAddPlan,
					OdenMessages.ODEN_SNAPSHOT_Actions_MsgAlertAddPlan,
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
