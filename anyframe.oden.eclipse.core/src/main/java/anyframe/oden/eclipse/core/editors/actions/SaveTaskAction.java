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
public class SaveTaskAction extends AbstractEditorsAction {

	/**
	 * 
	 */
	private static final String MSG_TASK_SAVE = CommandMessages.ODEN_EDITORS_TaskPage_MsgTaskAdd;
	private static final String POLI_OPT = CommandMessages.ODEN_EDITORS_TaskPage_MsgTaskPolicyOpt;
	private static final String DESC_OPT = CommandMessages.ODEN_EDITORS_TaskPage_MsgTaskDescOpt;
	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();
	
	public SaveTaskAction() {
		super(
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskSaveBtn,
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskSaveBtn,
				UIMessages.ODEN_EDITORS_TaskPage_TaskPageTitleImage);
	}

	/**
	 * 
	 */
	public void run(String title) {
		TaskPage page = OdenEditor.getDefault(title).getTaskpage();

		String commnd = ""; 
		String policies = "";
		
		// check policy
		TableItem[] tia = page.getRunViewer().getTable().getItems();
		for (int i = 0; i < tia.length; i++) {
			if (tia[i].getChecked()) {
				policies = policies + '"' + tia[i].getText(0) + '"' + " "; 
			}
		}
		commnd = MSG_TASK_SAVE
		+ " " + '"' + page.getTaskNameText().getText() + '"' + " " + POLI_OPT 
		+ " " + policies.trim() + " -json";
		if(!(page.getDescText().getText().equals("")))
			commnd = commnd +  " " + DESC_OPT + " " + '"' + page.getDescText().getText() + '"';

		try {
			OdenBroker.sendRequest(page.getShellUrl(), commnd);
 
			DialogUtil.openMessageDialog("Task", CommonMessages.ODEN_CommonMessages_OperationSucceeded,  
					MessageDialog.INFORMATION);
			page.showTaskDetail(page.getTaskNameText().getText());
			page.loadInitData();
			page.getTaskNameText().setEnabled(false);
			page.getAddTask().setEnabled(true);

			page.setNewTask(false);
			page.getAddTask().setEnabled(true);
			page.showTaskDetail(page.getTaskNameText().getText());
			
		} catch (OdenException odenException) {
		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while saving task info.",odenException);
		}
	}

}