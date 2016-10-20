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
import anyframe.oden.eclipse.core.editors.TaskDetails;
import anyframe.oden.eclipse.core.editors.TaskPage;
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
public class DeleteTaskAction extends AbstractEditorsAction {

	/**
	 * 
	 */
	private static final String MSG_TASK_DELE = OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskDel;

	public DeleteTaskAction() {
		super(
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskRemoveBtn,
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskRemoveBtn,
				OdenMessages.ODEN_EDITORS_TaskPage_TaskPageTitleImage);
	}

	/**
	 * 
	 */
	public void run() {
		TaskPage page = new TaskPage();
		TaskDetails details = null;
		ISelection selection = page.taskViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		details = (TaskDetails) obj;
		removeTask(details.getTaskName());
	}

	private void removeTask(String Task) {
		TaskPage page = new TaskPage();
		String commnd = ""; 
		String result = ""; 

		commnd = MSG_TASK_DELE + " " + '"' + Task + '"' + " " + OdenMessages.ODEN_HISTORY_DeploymentHistoryView_History_Json_Opt; 

		try {
			// Delete Task
			if(page.chkNewTask() && page.newTask) {
				// new Task
				page.removeTempcell();

			} else {
				result = OdenBroker.sendRequest(page.shellUrl, commnd);
				DialogUtil.openMessageDialog(OdenMessages.ODEN_CommonMessages_Title_Warning,
							"Success Delete Task.", 
							MessageDialog.INFORMATION);
				

			}
			// Show Task
			page.loadInitData();
			// changeLabel
			page.chageMandaLabel();
			// Clear text
			page.clearText();

			// policy Data
			page.loadInitPolicyData();

			page.taskNameText.setEnabled(true);
			if(page.chkNewTaskExist()){
				page.addTask.setEnabled(false);
			} else {
				page.addTask.setEnabled(true);
			}

		} catch (OdenException odenException) {
//			DialogUtil
//			.openMessageDialog(
//					OdenMessages.ODEN_CommonMessages_Title_Warning,
//					OdenMessages.ODEN_CommonMessages_UnableToConnectServer,  
//					MessageDialog.WARNING);
//			OdenActivator.error("Exception occured while http interface.",
//					odenException);
//			odenException.printStackTrace();
		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while removing task.",odenException);
//			odenException.printStackTrace();
		}
	}


}