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

import anyframe.oden.eclipse.core.OdenMessages;
import anyframe.oden.eclipse.core.editors.AbstractEditorsAction;
import anyframe.oden.eclipse.core.editors.TaskDetails;
import anyframe.oden.eclipse.core.editors.TaskPage;

/**
 * Add a new agent action in the Oden view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author HONG Junghwan
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
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskAddBtn,
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskAddBtn,
				OdenMessages.ODEN_EDITORS_TaskPage_TaskPageTitleImage);
	}

	/**
	 * 
	 */
	public void run() {
		TaskPage page = new TaskPage();
		page.taskNameText.setEnabled(true);
		page.removeTask.setEnabled(false);
		page.chageMandaLabel();
		// initialize Text
		page.clearText();
		page.loadInitPolicyData();
		// input temporary field
		addTempTaskName();
		page.newTask = true;
	}
	
	private void addTempTaskName() {
		TaskPage page = new TaskPage();
		// create temp task 
		String tempTaskName = OdenMessages.ODEN_EDITORS_TaskPage_TempTaskName;
		String tempTaskDesc = OdenMessages.ODEN_EDITORS_TaskPage_TempTaskDesc;
		page.taskNameText.setText(tempTaskName);
		page.descText.setText(tempTaskDesc);
		TaskDetails details = null;

		details = new TaskDetails(tempTaskName, tempTaskDesc,  null, null);
		page.taskViewer.add(details);
		page.taskViewer.getTable().select(page.lastNum);
		page.addTask.setEnabled(false);
		page.removeTask.setEnabled(true);
	}
}
