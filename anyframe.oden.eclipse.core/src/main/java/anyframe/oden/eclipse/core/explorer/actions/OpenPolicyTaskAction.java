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

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.alias.ServerEditorInput;
import anyframe.oden.eclipse.core.explorer.AbstractExplorerViewAction;
import anyframe.oden.eclipse.core.messages.UIMessages;

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
				UIMessages.ODEN_EXPLORER_Actions_LinkEditorAction_LinkEditor,
				UIMessages.ODEN_EXPLORER_Actions_LinkEditorAction_LinkEditorToolTip,
				UIMessages.ODEN_EXPLORER_Actions_LinkEditorAction_LinkEditorIcon);
	}

	/**
	 * 
	 */
	public void run() {

		Server server = getView().getSelectedServer(false);
		if (server != null) {

			IWorkbenchPage Page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			try {
				ServerEditorInput editorInput = new ServerEditorInput(server);
				Page.openEditor(editorInput,"anyframe.oden.eclipse.core.editors.OdenEditor");
			} catch (Exception odenException) {
				OdenActivator.error(
						UIMessages.ODEN_EXPLORER_Actions_OpenPolicyTaskAction_Exception,
						odenException);
			}
		}
	}

	public boolean isAvailable() {
		if (getView() == null)
			return false;
		return getView().getSelectedServers(false).size() == 1;
	}

}
