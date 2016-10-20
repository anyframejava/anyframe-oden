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

import org.eclipse.swt.widgets.Display;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.snapshot.AbstractSnapshotViewAction;
import anyframe.oden.eclipse.core.snapshot.dialogs.SnapshotNewPlanDialog;
import anyframe.oden.eclipse.core.snapshot.dialogs.SnapshotNewPlanDialog.Type;

/**
 * Add a new Snapshot plan with another plan's information.
 * This class extends AbstractSnapshotViewAction class.
 * 
 * @author LEE Sujeong
 * @version 1.0.0 RC2
 *
 */
public class DuplicateSnapshotPlanAction extends AbstractSnapshotViewAction {

	/**
	 * Constructor of DuplicateSnapshotPlanAction
	 * forward actionId, actionTooltipText, actionIconId to AbstractSnapshotViewAction
	 */
	public DuplicateSnapshotPlanAction() {
		super(UIMessages.ODEN_SNAPSHOT_Actions_DuplicateSnapshotPlanAction_DuplicatePlan, UIMessages.ODEN_SNAPSHOT_Actions_DuplicateSnapshotPlanAction_DuplicatePlanTooltip, UIMessages.ODEN_SNAPSHOT_Actions_DuplicateSnapshotPlanAction_DuplicatePlanIcon);
	}
	
	/**
	 * Run this Action with message box
	 */
	public void run(){
		String selection = OdenActivator.getDefault().getSnapshotView().getSelected()[0].toString();
		
		SnapshotNewPlanDialog dialog = new SnapshotNewPlanDialog(
				Display.getCurrent().getActiveShell(), Type.COPY, selection);
		dialog.open();
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
