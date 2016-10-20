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
public class SaveTaskAction extends AbstractEditorsAction {

	/**
	 * 
	 */
	private static final String MSG_TASK_SAVE = OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskAdd;
	private static final String POLI_OPT = OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPolicyOpt;
	private static final String DESC_OPT = OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskDescOpt;

	public SaveTaskAction() {
		super(
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskSaveBtn,
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskSaveBtn,
				OdenMessages.ODEN_EDITORS_TaskPage_TaskPageTitleImage);
	}

	/**
	 * 
	 */
	public void run() {
		String commnd = ""; 
		String result = ""; 
		TaskDetails details = null;
		String policies = "";
		TaskPage page = new TaskPage();
		// check policy
		TableItem[] tia = page.runViewer.getTable().getItems();
		for (int i = 0; i < tia.length; i++) {
			if (tia[i].getChecked()) {
				policies = policies + '"' + tia[i].getText(0) + '"' + " "; 
			}
		}
		commnd = MSG_TASK_SAVE
		+ " " + '"' + page.taskNameText.getText() + '"' + " " + POLI_OPT 
		+ " " + policies.trim() + " -json";
		if(!(page.descText.getText().equals("")))
			commnd = commnd +  " " + DESC_OPT + " " + '"' + page.descText.getText() + '"';


		try {
			result = OdenBroker.sendRequest(page.shellUrl, commnd);
 
			DialogUtil.openMessageDialog("Task", OdenMessages.ODEN_CommonMessages_OperationSucceeded,  
					MessageDialog.INFORMATION);
			page.showTaskDetail(page.taskNameText.getText());
			page.loadInitData();
			page.taskNameText.setEnabled(false);
			page.addTask.setEnabled(true);

			page.newTask = false;
			page.addTask.setEnabled(true);
			page.showTaskDetail(page.taskNameText.getText());
			
		} catch (OdenException odenException) {
//			OdenActivator.warning(OdenMessages.ODEN_CommonMessages_UnableToConnectServer);
		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while saving task info.",odenException);
		}
	}

}