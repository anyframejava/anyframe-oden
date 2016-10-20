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
			String agentName = SnapshotView.selectedName;
			if(agentName!=null || "".equals(agentName)){ //$NON-NLS-1$
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
