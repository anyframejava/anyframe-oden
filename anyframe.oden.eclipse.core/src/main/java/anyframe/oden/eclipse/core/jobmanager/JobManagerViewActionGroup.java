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
package anyframe.oden.eclipse.core.jobmanager;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionGroup;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenTrees.TreeObject;
import anyframe.oden.eclipse.core.OdenTrees.TreeParent;
import anyframe.oden.eclipse.core.jobmanager.actions.CancelCurrentJobAction;
import anyframe.oden.eclipse.core.jobmanager.actions.DeployByFileReqAction;
import anyframe.oden.eclipse.core.jobmanager.actions.DeployByTaskAction;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Constructs an Action group for Oden JobManager view of
 * Anyframe Oden Eclipse plug-in.
 * 
 * @author HONG JungHwan
 * @version 1.1.0
 *
 */
public class JobManagerViewActionGroup extends ActionGroup {

	/**
	 * Fills the context menu
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(IMenuManager)
	 */
	
	public void fillContextMenu(IMenuManager contextmenu , String shellUrl) {
		JobManagerView view = OdenActivator.getDefault().getJObManagerView();
		Object[] selection = (view == null) ? null : view.getSelected();
		
		// TODO : define contextMenu using addAction
		
		TreeParent parent;
		String parentName;
		
		if (selection != null) {
			parent = ((TreeObject) selection[0]).getParent();
			parentName = parent.getName();
		} else {
			// If nothing is selected, show default context menu(open File Request and Task List)
			addAction(contextmenu, new DeployByFileReqAction());
			addAction(contextmenu, new DeployByTaskAction(shellUrl));
			return;
		}
		// tree 1 level context menu
		if (parentName.equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_CurrentJob)) {
			addAction(contextmenu, new CancelCurrentJobAction());
		}
	}
	
	private boolean addAction(IMenuManager menu, AbstractJobManagerViewAction action) {
		if (action.isAvailable()) {
			menu.add(action);
			action.setEnabled(true);
			return true;
		}
		return false;
	}
}	
