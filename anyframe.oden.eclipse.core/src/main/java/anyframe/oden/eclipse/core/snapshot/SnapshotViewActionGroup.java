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
package anyframe.oden.eclipse.core.snapshot;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionGroup;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenTrees.TreeObject;
import anyframe.oden.eclipse.core.OdenTrees.TreeParent;
import anyframe.oden.eclipse.core.snapshot.actions.DeleteSnapshotAction;
import anyframe.oden.eclipse.core.snapshot.actions.DeleteSnapshotPlanAction;
import anyframe.oden.eclipse.core.snapshot.actions.DuplicateSnapshotPlanAction;
import anyframe.oden.eclipse.core.snapshot.actions.NewSnapshotPlanAction;
import anyframe.oden.eclipse.core.snapshot.actions.RollbackSnapshotAction;
import anyframe.oden.eclipse.core.snapshot.actions.TakeSnapshotPlanAction;

/**
 * Constructs an Action group for Snapshot view of
 * Anyframe Oden Eclipse plug-in.
 * 
 * @author LEE Sujeong
 * @version 1.0.0 RC2
 */
public class SnapshotViewActionGroup extends ActionGroup{

	/**
	 * Fills the context menu
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(IMenuManager)
	 */
	public void fillContextMenu(IMenuManager contextmenu) {
		SnapshotView snapshotView = OdenActivator.getDefault().getSnapshotView();
		Object[] selection = (snapshotView == null) ? null : snapshotView.getSelected();

		if (selection == null || selection.length != 1) {
			String serverNickname = SnapshotView.selectedName;
			if(serverNickname!=null || "".equals(serverNickname)){ //$NON-NLS-1$
				addAction(contextmenu, new NewSnapshotPlanAction());
			}
			return;
		}

		TreeParent parent = ((TreeObject) selection[0]).getParent();

		if(parent.getName()==null || "".equals(parent.getName())){//plan //$NON-NLS-1$
			addAction(contextmenu, new NewSnapshotPlanAction());
			addAction(contextmenu, new DuplicateSnapshotPlanAction());
			addAction(contextmenu, new DeleteSnapshotPlanAction());
			contextmenu.add(new Separator());
			addAction(contextmenu, new TakeSnapshotPlanAction());
		}else{//file
			addAction(contextmenu, new DeleteSnapshotAction());
			contextmenu.add(new Separator());
			addAction(contextmenu, new RollbackSnapshotAction());
		}

		//		clearComposite();
	}

	private boolean addAction(IMenuManager menu, AbstractSnapshotViewAction action) {
		if (action.isAvailable()) {
			menu.add(action);
			action.setEnabled(true);
			return true;
		}
		return false;
	}
}
