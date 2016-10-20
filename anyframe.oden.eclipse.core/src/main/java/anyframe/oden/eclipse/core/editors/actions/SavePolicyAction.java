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
import org.eclipse.swt.widgets.TableItem;

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
 * Save policy action in the Oden editor view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class SavePolicyAction extends AbstractEditorsAction {

	/**
	 * 
	 */
	private static final String MSG_POLICY_SAVE = CommandMessages.ODEN_EDITORS_PolicyPage_MsgPolicySave;
	private static final String FTP_PROT = "ftp://";
	private static final String FILE_PROT = "file://";
	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();
	
	public SavePolicyAction() {
		super(
				UIMessages.ODEN_EDITORS_PolicyPage_PolicySave_Btn,
				UIMessages.ODEN_EDITORS_PolicyPage_PolicySave_Btn,
				UIMessages.ODEN_EDITORS_TaskPage_TaskPageTitleImage);
	}

	/**
	 * 
	 */
	public void run(String title) {
		PolicyPage page = OdenEditor.getDefault(title).getPolicypage();
		String commnd = "";
		PolicyDetails details = null;

		String repoType = page.getRepoKind().getText().equals(
				CommonMessages.ODEN_ALIAS_RepositoryManager_ProtocolSet_FileSystem) ? "fs"
						: "ftp";

		String deployUrl = "";
		String dCommand = "";
		String locationVal = "";
		String updateOpt = "-u";
		int size = page.getDeployViewer().getTable().getItemCount();
		
		for (int i = 0; i < size; i++) {
			TableItem item = page.getDeployViewer().getTable().getItem(i);
			details = (PolicyDetails) item.getData();
			deployUrl = details.getDeployUrl();

			locationVal = details.getLocationVar();
			if(!(locationVal == null))
				dCommand = dCommand + deployUrl + "/"+ locationVal + " ";
			else
				dCommand = dCommand + deployUrl + " ";
		}

		if (repoType
				.equals(UIMessages.ODEN_EDITORS_PolicyPage_Repo_Kind1))
			commnd = MSG_POLICY_SAVE + " " + '"' + page.getPolicyNameText().getText() + '"' + " "
			+ "-r " + FTP_PROT + page.getBuildRepoUriText().getText() + " "
			+ page.getBuildRepoRootText().getText() + " "
			+ '"' + page.getUserField().getText() + '"' + " " + '"' + page.getPasswordField().getText() + '"' 
			+ " " + "-d" + " " + dCommand ;
		else
			commnd = MSG_POLICY_SAVE + " " + '"' + page.getPolicyNameText().getText()
			+ '"' + " " + "-r " + FILE_PROT
			+ page.getBuildRepoRootText().getText() + " " + "-d" + " " + dCommand;

		if (!(page.getDescriptionText().getText().equals(""))) {
			commnd = commnd + "-desc" + " " + '"' + page.getDescriptionText().getText() + '"';
		}

		if (!(page.getIncludeText().getText().equals(""))) {
			String include = " " + "-i" + " " + changeCludeValue(page.getIncludeText().getText());
			commnd = commnd + include;
		}
		if (!(page.getExcludeText().getText().equals(""))) {
			String exclude = " " + "-e" + " " + changeCludeValue(page.getExcludeText().getText());
			commnd = commnd + exclude;
			// default exclude (.svn, CVS)
			String[] hiddenFolder = CommandMessages.ODEN_EXPLORER_ExplorerView_HiddenFolder.split(",");
			for(String val : hiddenFolder) {
				commnd = commnd + " " + '"' + "**/" + val + "/**" + '"' ; 
			}
		} else {
			// default exclude (.svn, CVS)
			String[] hiddenFolder = CommandMessages.ODEN_EXPLORER_ExplorerView_HiddenFolder.split(",");
			commnd = commnd + " " + "-e" ;
			for(String val : hiddenFolder) {
				commnd = commnd  + " " + '"' + "**/" + val + "/**" + '"'; 
			}
		}
		// update option check
		if(page.getUpdateOptionRequired().getSelection()){
			commnd = commnd + " " + updateOpt + " " + CommandMessages.ODEN_HISTORY_DeploymentHistoryView_History_Json_Opt;
		} else {
			commnd = commnd + " " + CommandMessages.ODEN_HISTORY_DeploymentHistoryView_History_Json_Opt;
		}
		try {
			OdenBroker.sendRequest(page.getShellUrl(), commnd);
			DialogUtil.openMessageDialog("Policy",
					CommonMessages.ODEN_CommonMessages_OperationSucceeded,
					MessageDialog.INFORMATION);
			page.loadInitData(page.getShellUrl());
			page.showPolicyDetail(page.getPolicyNameText().getText());
			page.getPolicyNameText().setEnabled(false);
			
		} catch (OdenException e) {
		} catch (Exception odenException) {
			OdenActivator.error(
					"Exception occured while saving policy info.",
					odenException);
		}

	}
	
	private String changeCludeValue(String inputVal) {
		String[] cludeArr = inputVal.split(";");
		if(cludeArr.length == 0) {
			return inputVal;
		} else {
			String returnVal = "";
			for(String val : cludeArr) {
				returnVal = returnVal + '"' + val.trim() + '"' + " ";
			}
			return returnVal.substring(0,returnVal.lastIndexOf(" "));
		}
	}
}