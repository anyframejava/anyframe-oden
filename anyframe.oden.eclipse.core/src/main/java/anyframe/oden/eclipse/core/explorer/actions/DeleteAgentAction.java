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

import java.util.Set;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenMessages;
import anyframe.oden.eclipse.core.alias.Agent;
import anyframe.oden.eclipse.core.explorer.AbstractExplorerViewAction;
import anyframe.oden.eclipse.core.utils.DialogUtil;

/**
 * Edit an existing agent action in the Oden view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class DeleteAgentAction extends AbstractExplorerViewAction {

	/**
	 * 
	 */
	private Agent agent;
	
	public DeleteAgentAction() {
		super(
				OdenMessages.ODEN_EXPLORER_Actions_DeleteAgentAction_RemoveAgent,
				OdenMessages.ODEN_EXPLORER_Actions_DeleteAgentAction_RemoveAgentToolTip,
				OdenMessages.ODEN_EXPLORER_Actions_DeleteAgentAction_RemoveAgentIcon);
	}

	/**
	 * run deleteAgentAction
	 */
	public void run() {
		agent = getView().getSelectedAgent(false);

		if (DialogUtil.confirmMessageDialog(
				OdenMessages.ODEN_CommonMessages_Title_ConfirmDelete,
				OdenMessages.ODEN_EXPLORER_Actions_DeleteAgentAction_ConfirmDelete_MessagePre +
				agent.getNickname() +
				OdenMessages.ODEN_CommonMessages_Confirm_MessageSuf)) {

			if (agent != null) {
				OdenActivator.getDefault().getAliasManager().getAgentManager().removeAgent(agent.getNickname());
			}

			// notify that there has been changes
			OdenActivator.getDefault().getAliasManager().getAgentManager().modelChanged();

			// reload data for data consistency
			try {
				OdenActivator.getDefault().getAliasManager().save();
				OdenActivator.getDefault().getAliasManager().load();
			} catch (OdenException odenException) {
				OdenActivator.error("Exception occured while reloading Oden Server profiles.", odenException);
				odenException.printStackTrace();
			}

			getView().refresh();
			closeDeleteAgentEditor();
		}
	}

	/**
	 * 
	 */
	public boolean isAvailable() {
		if (getView() == null) {
			return false;
		}

		Set<Agent> agents = getView().getSelectedAgents(false);

		if (agents.isEmpty()) {
			return false;
		}

		return true;
	}
	private void closeDeleteAgentEditor() {
		IWorkbenchPage Page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if(Page !=null) {
			IEditorReference[] editors = Page.getEditorReferences();
			for(IEditorReference editor : editors) {
				if(editor.getTitle().equals(agent.getNickname()))
					Page.closeEditor(editor.getEditor(false), false);
			}
		}
	}
}
