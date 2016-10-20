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
import anyframe.oden.eclipse.core.OdenMessages;
import anyframe.oden.eclipse.core.brokers.OdenBroker;
import anyframe.oden.eclipse.core.editors.AbstractEditorsAction;
import anyframe.oden.eclipse.core.editors.PolicyDetails;
import anyframe.oden.eclipse.core.editors.PolicyPage;
import anyframe.oden.eclipse.core.utils.DialogUtil;

/**
 * Add a new agent action in the Oden view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author HONG Junghwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class DeletePolicyAction extends AbstractEditorsAction {

	/**
	 * 
	 */
	private static final String MSG_POLICY_DELE = OdenMessages.ODEN_EDITORS_PolicyPage_MsgPolicyDele;
	
	public DeletePolicyAction() {
		super(
				OdenMessages.ODEN_EDITORS_PolicyPage_PolicyRemove_Btn,
				OdenMessages.ODEN_EDITORS_PolicyPage_PolicyRemove_Btn,
				OdenMessages.ODEN_EDITORS_TaskPage_TaskPageTitleImage);
	}

	/**
	 * 
	 */
	public void run() {
		PolicyDetails details = null;
		ISelection selection = PolicyPage.getPolicyViewer().getSelection();
		Object obj = ((IStructuredSelection) selection)
				.getFirstElement();
		details = (PolicyDetails) obj;
		removePolicy(details.getPolicyName());
	}
	
	private void removePolicy(String Policy) {

		String commnd = "";
		String result = "";
		PolicyPage page = new PolicyPage();
		commnd = MSG_POLICY_DELE + " " + '"' + Policy + '"' + " " + OdenMessages.ODEN_HISTORY_DeploymentHistoryView_History_Json_Opt;

		try {
			if(page.chkNewPolicy()) {
				// new policy
				page.removeTempcell();
			} else {
				// Delete Policy
				result = OdenBroker.sendRequest(PolicyPage.getShellUrl(), commnd);
//				
//				if (result.trim().equals(Policy + " is removed."))
					DialogUtil.openMessageDialog("Policy",
							OdenMessages.ODEN_CommonMessages_OperationSucceeded,
							MessageDialog.INFORMATION);
//				else
//					DialogUtil.openMessageDialog("Policy",
//							OdenMessages.ODEN_CommonMessages_OperationFailed,
//							MessageDialog.ERROR);
			}
			// Show Policy
			page.loadInitData(PolicyPage.getShellUrl());
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
//			DialogUtil.openMessageDialog("Policy",
//					OdenMessages.ODEN_CommonMessages_UnableToConnectServer,
//					MessageDialog.WARNING);
//			OdenActivator.error("Exception occured while http interface.",
//					odenException);
//			odenException.printStackTrace();
		} catch (Exception odenException) {
			OdenActivator.error(
					"Exception occured while removing policy.",
					odenException);
//			odenException.printStackTrace();
		}

	}
}