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
import anyframe.oden.eclipse.core.editors.TaskDetails;
import anyframe.oden.eclipse.core.editors.TaskPage;
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
public class DeleteTaskAction extends AbstractEditorsAction {

	/**
	 * 
	 */
	private static final String MSG_TASK_DELE = CommandMessages.ODEN_EDITORS_TaskPage_MsgTaskDel;
	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();
	
	public DeleteTaskAction() {
		super(
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskRemoveBtn,
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskRemoveBtn,
				UIMessages.ODEN_EDITORS_TaskPage_TaskPageTitleImage);
	}

	/**
	 * 
	 */
	public void run(String title) {
		TaskPage page = OdenEditor.getDefault(title).getTaskpage();
		
		TaskDetails details = null;
		ISelection selection = page.getTaskViewer().getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		details = (TaskDetails) obj;
		removeTask(details.getTaskName(), title);
	}

	private void removeTask(String Task , String title) {
		TaskPage page = OdenEditor.getDefault(title).getTaskpage();
		String commnd = ""; 
		
		commnd = MSG_TASK_DELE + " " + '"' + Task + '"' + " " + CommandMessages.ODEN_HISTORY_DeploymentHistoryView_History_Json_Opt; 

		try {
			// Delete Task
			if(page.chkNewTask() && page.isNewTask()) {
				// new Task
				page.removeTempcell();

			} else {
				OdenBroker.sendRequest(page.getShellUrl(), commnd);
				DialogUtil.openMessageDialog(CommonMessages.ODEN_CommonMessages_Title_Warning,
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

			page.getTaskNameText().setEnabled(true);
			if(page.chkNewTaskExist()){
				page.getAddTask().setEnabled(false);
			} else {
				page.getAddTask().setEnabled(true);
			}

		} catch (OdenException odenException) {
		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while removing task.",odenException);
		}
	}


}