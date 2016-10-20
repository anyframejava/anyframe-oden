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
package anyframe.oden.eclipse.core.editors.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.editors.AbstractEditorsAction;
import anyframe.oden.eclipse.core.editors.OdenEditor;
import anyframe.oden.eclipse.core.editors.PolicyDetails;
import anyframe.oden.eclipse.core.editors.PolicyPage;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.DialogUtil;

/**
 * Add a new server action in the Oden view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class DeletePolicyAction extends AbstractEditorsAction {

	/**
	 * 
	 */
	private static final String MSG_POLICY_DELE = CommandMessages.ODEN_EDITORS_PolicyPage_MsgPolicyDele;
	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();
	
	public DeletePolicyAction() {
		super(
				UIMessages.ODEN_EDITORS_PolicyPage_PolicyRemove_Btn,
				UIMessages.ODEN_EDITORS_PolicyPage_PolicyRemove_Btn,
				UIMessages.ODEN_EDITORS_TaskPage_TaskPageTitleImage);
	}

	/**
	 * 
	 */
	public void run(String title) {
		PolicyDetails details = null;
		PolicyPage page = OdenEditor.getDefault(title).getPolicypage();
		ISelection selection = page.getPolicyViewer().getSelection();
		Object obj = ((IStructuredSelection) selection)
				.getFirstElement();
		details = (PolicyDetails) obj;
		removePolicy(details.getPolicyName() , title);
	}
	
	private void removePolicy(String Policy , String title) {

		String commnd = "";
		
		PolicyPage page = OdenEditor.getDefault(title).getPolicypage();
		commnd = MSG_POLICY_DELE + " " + '"' + Policy + '"' + " " + CommandMessages.ODEN_HISTORY_DeploymentHistoryView_History_Json_Opt;

		try {
			if(page.chkNewPolicy()) {
				// new policy
				page.removeTempcell();
			} else {
				// Delete Policy
				OdenBroker.sendRequest(page.getShellUrl(), commnd);
				DialogUtil.openMessageDialog("Policy",
							CommonMessages.ODEN_CommonMessages_OperationSucceeded,
							MessageDialog.INFORMATION);

			}
			// Show Policy
			page.loadInitData(page.getShellUrl());
			// changeLabel
			page.chageMandaLabel();
			// Clear text
			page.clearText();

			page.getPolicyNameText().setEnabled(true);
			
			if(page.chkNewPolicyExist()){
				page.getAddPolicy().setEnabled(false);
			} else {
				page.getAddPolicy().setEnabled(true);
			}

		} catch (OdenException odenException) {
		} catch (Exception odenException) {
			OdenActivator.error(
					"Exception occured while removing policy.",
					odenException);
		}

	}
}