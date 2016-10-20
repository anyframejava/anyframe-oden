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

import anyframe.oden.eclipse.core.editors.AbstractEditorsAction;
import anyframe.oden.eclipse.core.editors.OdenEditor;
import anyframe.oden.eclipse.core.editors.TaskDetails;
import anyframe.oden.eclipse.core.editors.TaskPage;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Add a new server action in the Oden view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class NewTaskAction extends AbstractEditorsAction {

	/**
	 * 
	 */
	
	
	
	public NewTaskAction() {
		super(
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskAddBtn,
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskAddBtn,
				UIMessages.ODEN_EDITORS_TaskPage_TaskPageTitleImage);
	}

	/**
	 * 
	 */
	public void run(String title) {
		TaskPage page = OdenEditor.getDefault(title).getTaskpage();
		
		page.getTaskNameText().setEnabled(true);
		page.getRemoveTask().setEnabled(false);
		page.chageMandaLabel();
		// initialize Text
		page.clearText();
		page.loadInitPolicyData();
		// input temporary field
		addTempTaskName(title);
		page.setNewTask(true);
	}
	
	private void addTempTaskName(String title) {
		TaskPage page = OdenEditor.getDefault(title).getTaskpage();
		String tempTaskName = UIMessages.ODEN_EDITORS_TaskPage_TempTaskName;
		String tempTaskDesc = UIMessages.ODEN_EDITORS_TaskPage_TempTaskDesc;
		page.getTaskNameText().setText(tempTaskName);
		page.getDescText().setText(tempTaskDesc);
		TaskDetails details = null;

		details = new TaskDetails(tempTaskName, tempTaskDesc,  null, null);
		page.getTaskViewer().add(details);
		page.getTaskViewer().getTable().select(page.getLastNum());
		page.getAddTask().setEnabled(false);
		page.getRemoveTask().setEnabled(true);
	}
}
