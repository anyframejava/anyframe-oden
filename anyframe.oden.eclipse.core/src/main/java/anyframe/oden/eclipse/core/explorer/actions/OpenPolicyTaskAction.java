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
package anyframe.oden.eclipse.core.explorer.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenMessages;
import anyframe.oden.eclipse.core.alias.Agent;
import anyframe.oden.eclipse.core.alias.AgentEditorInput;
import anyframe.oden.eclipse.core.explorer.AbstractExplorerViewAction;
import anyframe.oden.eclipse.core.utils.DialogUtil;

/**
 * Open task,policy in the Oden view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author HongJungHwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class OpenPolicyTaskAction extends AbstractExplorerViewAction {

	/**
	 * 
	 */
	public OpenPolicyTaskAction() {
		super(
				OdenMessages.ODEN_EXPLORER_Actions_LinkEditorAction_LinkEditor,
				OdenMessages.ODEN_EXPLORER_Actions_LinkEditorAction_LinkEditorToolTip,
				OdenMessages.ODEN_EXPLORER_Actions_LinkEditorAction_LinkEditorIcon);
	}

	/**
	 * 
	 */
	public void run() {

		Agent agent = getView().getSelectedAgent(false);
		if (agent != null) {

			IWorkbenchPage Page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			try {
				IEditorPart[] editors = Page.getEditors();
				if(editors.length < 1) {
					AgentEditorInput editorInput = new AgentEditorInput(agent);
					Page.openEditor(editorInput,"anyframe.oden.eclipse.core.editors.OdenEditor");
//					getView().refresh();
				} else {
					// exist open Editor(need to modify ASP)
					DialogUtil.openMessageDialog(OdenMessages.ODEN_CommonMessages_Title_Information,
					OdenMessages.ODEN_EXPLORER_Actions_OpenPolicyTaskAction_ExistEditor,
					MessageDialog.INFORMATION);
				}
			} catch (Exception odenException) {
				OdenActivator.error(
						OdenMessages.ODEN_EXPLORER_Actions_OpenPolicyTaskAction_Exeption,
						odenException);
			}
		}
	}

	public boolean isAvailable() {
		if (getView() == null)
			return false;
		return getView().getSelectedAgents(false).size() == 1;
	}

}
