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

import org.eclipse.swt.widgets.Display;

import anyframe.oden.eclipse.core.OdenMessages;
import anyframe.oden.eclipse.core.alias.Agent;
import anyframe.oden.eclipse.core.explorer.AbstractExplorerViewAction;
import anyframe.oden.eclipse.core.explorer.dialogs.CreateAgentDialog;

/**
 * Edit an existing agent action in the Oden view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class EditAgentAction extends AbstractExplorerViewAction {

	/**
	 * 
	 */
	public EditAgentAction() {
		super(
				OdenMessages.ODEN_EXPLORER_Actions_EditAgentAction_UpdateAgent,
				OdenMessages.ODEN_EXPLORER_Actions_EditAgentAction_UpdateAgentToolTip,
				OdenMessages.ODEN_EXPLORER_Actions_EditAgentAction_UpdateAgentIcon);
	}

	/**
	 * 
	 */
	public void run() {
		Agent agent = getView().getSelectedAgent(false);
		if (agent != null) {
			CreateAgentDialog dialog = new CreateAgentDialog(Display
					.getCurrent().getActiveShell(),
					CreateAgentDialog.Type.CHANGE, agent);
			dialog.open();
			if(dialog.getReturnCode() == 0)
				getView().refresh();
		}
	}

	/**
	 * 
	 */
	public boolean isAvailable() {
		if (getView() == null)
			return false;
		return getView().getSelectedAgents(false).size() == 1;
	}

}
