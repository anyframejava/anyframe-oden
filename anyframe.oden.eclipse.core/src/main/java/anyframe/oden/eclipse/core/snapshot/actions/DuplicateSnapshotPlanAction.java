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
