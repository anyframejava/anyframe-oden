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
 * Add a new agent action in the Oden view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class NewAgentAction extends AbstractExplorerViewAction {

	/**
	 * 
	 */
	public NewAgentAction() {
		super(
				OdenMessages.ODEN_EXPLORER_Actions_NewAgentAction_CreateAgent,
				OdenMessages.ODEN_EXPLORER_Actions_NewAgentAction_CreateAgentToolTip,
				OdenMessages.ODEN_EXPLORER_Actions_NewAgentAction_CreateAgentIcon);
	}

	/**
	 * 
	 */
	public void run() {
		CreateAgentDialog dialog = new CreateAgentDialog(Display.getCurrent()
				.getActiveShell(), CreateAgentDialog.Type.CREATE, new Agent());
		dialog.open();
		if(dialog.getReturnCode() == 0)
			getView().refresh();
	}

}
