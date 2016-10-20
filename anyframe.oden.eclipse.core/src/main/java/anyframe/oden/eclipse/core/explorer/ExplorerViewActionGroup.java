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
package anyframe.oden.eclipse.core.explorer;

import java.awt.PageAttributes.OriginType;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenMessages;
import anyframe.oden.eclipse.core.OdenTrees.TreeObject;
import anyframe.oden.eclipse.core.OdenTrees.TreeParent;
import anyframe.oden.eclipse.core.alias.Agent;
import anyframe.oden.eclipse.core.alias.Repository;
import anyframe.oden.eclipse.core.explorer.actions.DeleteAgentAction;
import anyframe.oden.eclipse.core.explorer.actions.DeleteRepositoryAction;
import anyframe.oden.eclipse.core.explorer.actions.DuplicateAgentAction;
import anyframe.oden.eclipse.core.explorer.actions.DuplicateRepositoryAction;
import anyframe.oden.eclipse.core.explorer.actions.EditAgentAction;
import anyframe.oden.eclipse.core.explorer.actions.EditRepositoryAction;
import anyframe.oden.eclipse.core.explorer.actions.NewAgentAction;
import anyframe.oden.eclipse.core.explorer.actions.NewRepositoryAction;
import anyframe.oden.eclipse.core.explorer.actions.OpenPolicyTaskAction;
import anyframe.oden.eclipse.core.explorer.actions.RunDeployTaskAction;

/**
 * Constructs an Action group for Oden Explorer view of
 * Anyframe Oden Eclipse plug-in.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 *
 */
public class ExplorerViewActionGroup extends ActionGroup {

	/**
	 * Fills the context menu
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(IMenuManager)
	 */
	@SuppressWarnings("deprecation")
	public void fillContextMenu(IMenuManager contextmenu) {
		ExplorerView view = OdenActivator.getDefault().getExplorerView();
		Object[] selection = (view == null) ? null : view.getSelected();
		boolean chk = false;

		IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		IWorkbenchPage page = win.getActivePage();
		IEditorPart[] editors = page.getEditors();

		if(editors != null){
			for(IEditorPart editor: editors){
				if (selection != null) {
					if(((TreeObject) selection[0]).getName().equals(editor.getTitle())){
						chk = true;
					}
				} else {
					// If nothing is selected, show default context menu
					addAction(contextmenu, new NewAgentAction());
					addAction(contextmenu, new NewRepositoryAction());
					return;
				}
			}
		}

		TreeParent parent;
		String parentNm;
		if (selection != null) {
			parent = ((TreeObject) selection[0]).getParent();
			parentNm = parent.getName();
		} else {
			// If nothing is selected, show default context menu
			addAction(contextmenu, new NewAgentAction());
			addAction(contextmenu, new NewRepositoryAction());
			return;
		}

		// If multi folder and file selected , show deploy now menu
		if (selection == null || selection.length != 1) {
			if(chekckSameRepo(selection))
				if (!(parentNm.equals(OdenMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoriesRootLabel)
						|| ((TreeObject) selection[0]).getName().equals(OdenMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoriesRootLabel))) {
					addAction(contextmenu, new RunDeployTaskAction(selection));
				}
			return;

		}



		if (parentNm.equals(OdenMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_AgentsRootLabel)
				|| ((TreeObject) selection[0]).getName().equals(OdenMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_AgentsRootLabel)) {

			if (OdenActivator.getDefault().getAliasManager().getAgentManager().getAgent(selection[0].toString()) instanceof Agent) {
				addAction(contextmenu, new EditAgentAction());
				addAction(contextmenu, new DuplicateAgentAction());
				addAction(contextmenu, new DeleteAgentAction());
				if(!chk){
					// add seperator
					contextmenu.add(new Separator());
					// add Open Poliy/Task
					addAction(contextmenu, new OpenPolicyTaskAction());
				}
			} else {
				// Root
				addAction(contextmenu, new NewAgentAction());
			}
		} else if (parentNm.equals(OdenMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoriesRootLabel)
				|| ((TreeObject) selection[0]).getName().equals(OdenMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoriesRootLabel)) {
			if (OdenActivator.getDefault().getAliasManager().getRepositoryManager().getRepository(selection[0].toString()) instanceof Repository) {
				addAction(contextmenu, new EditRepositoryAction());
				addAction(contextmenu, new DuplicateRepositoryAction());
				addAction(contextmenu, new DeleteRepositoryAction());
			} else {
				// Root
				addAction(contextmenu, new NewRepositoryAction());
			} 
		} else {
			// Repository directory or file add Deploy Now
			addAction(contextmenu, new RunDeployTaskAction(selection));

		}
	}

	private boolean addAction(IMenuManager menu, AbstractExplorerViewAction action) {
		if (action.isAvailable()) {
			menu.add(action);
			action.setEnabled(true);
			return true;
		}
		return false;
	}

	private boolean chekckSameRepo(Object[] selections){
		String compare = "";
		String orgin = "";
		int i = 0;
		for(Object selection : selections){
			i = i + 1;
			if( i== 1){
				String temp = fullpath(selection);
				String[] tempArr = temp.split("/");
				orgin = tempArr[1];
			} else {
				String temp = fullpath(selection);
				String[] tempArr = temp.split("/");
				compare = tempArr[1];
				if( !(orgin.equals(compare))){
					return false;
				}
			}
		}	
		return true;
	}
	private String fullpath(Object selection) {
		TreeObject obj = ((TreeObject) selection);
		StringBuffer full = new StringBuffer(obj.getName());
		TreeParent parent = obj.getParent();
		String fullpath = "";
		while (parent != null) {
			full.insert(0, parent.getName() + "/"); 
			parent = parent.getParent();
		}
		if (full.toString().substring(0, 1).equals("/")) {
			return full.toString().substring(1);
		} else {
			return full.toString();
		}
	}
}
