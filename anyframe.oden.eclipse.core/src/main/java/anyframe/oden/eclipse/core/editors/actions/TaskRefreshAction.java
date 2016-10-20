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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import anyframe.oden.eclipse.core.editors.OdenEditor;
import anyframe.oden.eclipse.core.editors.TaskDetails;
import anyframe.oden.eclipse.core.editors.TaskPage;
import anyframe.oden.eclipse.core.history.AbstractDeploymentHistoryViewAction;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * The Implementation of RefreshAction,
 * for the Anyframe Oden Deployment History view.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 *
 */
public class TaskRefreshAction extends AbstractDeploymentHistoryViewAction {
	private String title;
	public TaskRefreshAction(String title) {
		super(
				UIMessages.ODEN_SNAPSHOT_Actions_RefreshAction_Refresh,
				UIMessages.ODEN_SNAPSHOT_Actions_RefreshAction_RefreshTooltip,
				UIMessages.ODEN_SNAPSHOT_Actions_RefreshAction_RefreshIcon);
		this.title = title;
		
	}

	public void run() {
		TaskPage page = OdenEditor.getDefault(title).getTaskpage();
		page.loadInitData();
		page.loadInitPolicyData();
		TaskDetails details = new TaskDetails();
		ISelection selection = page.getTaskViewer().getSelection();
		if(!(selection.isEmpty()) && ! page.chkNewTask()) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			details = (TaskDetails) obj;
			page.showTaskDetail(details.getTaskName());
		} else {
			page.clearText();
			page.getClient().setVisible(false);
		}
		page.getTaskViewer().refresh();
		page.getRunViewer().refresh();
		page.getAddTask().setEnabled(true);
	}

}
