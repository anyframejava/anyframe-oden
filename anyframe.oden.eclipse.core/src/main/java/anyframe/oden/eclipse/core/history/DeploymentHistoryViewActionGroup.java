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
package anyframe.oden.eclipse.core.history;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.actions.ActionGroup;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.history.actions.ReDeployHistoryAction;
import anyframe.oden.eclipse.core.history.actions.UndoHistoryAction;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Constructs an Action group for Oden DeploymentHistory view of
 * Anyframe Oden Eclipse plug-in.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 *
 */
public class DeploymentHistoryViewActionGroup extends ActionGroup {

	/**
	 * Fills the context menu
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(IMenuManager)
	 */
	private TreeItem[] selection;
	
	public void fillContextMenu(IMenuManager contextmenu) {
		DeploymentHistoryView view = OdenActivator.getDefault().getDeploymentHistoryView();
		this.selection = (view == null) ? null : view.getSelected();

		if(view.isUndoCheck()) {
			if (this.selection != null && this.selection.length == 1) {
				// If nothing is selected, show default context menu
				addAction(contextmenu, new UndoHistoryAction(selection));
			} else if (this.selection != null && this.selection.length > 1 && this.checkRootInclude()) {
				// exclude root item -> selection items
				addAction(contextmenu, new UndoHistoryAction(selection));
			} else {
				// include root item -> root tree 1 selection
				view.rootSelected();
				this.selection = view.getSelected();
				addAction(contextmenu, new UndoHistoryAction(selection));
			}
		}
		
		if(! this.checkRootInclude() && selection[0].getText(8).equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Fail)){
			addAction(contextmenu, new ReDeployHistoryAction(selection));
		}
		return;
		
	}

	private boolean addAction(IMenuManager menu, AbstractDeploymentHistoryViewAction action) {
		if (action.isAvailable()) {
			menu.add(action);
			action.setEnabled(true);
			return true;
		}
		return false;
	}
	
	private boolean checkRootInclude() {
		for(TreeItem treeitem : this.selection) {
			if(! treeitem.getText(0).equals("")) {
				return false;
			}
		}
		return true;
	}
}	
