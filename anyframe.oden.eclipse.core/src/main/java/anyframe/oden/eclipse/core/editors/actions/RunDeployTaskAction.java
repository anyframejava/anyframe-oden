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
import org.eclipse.swt.widgets.Display;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenMessages;
import anyframe.oden.eclipse.core.editors.AbstractEditorsAction;
import anyframe.oden.eclipse.core.editors.TaskDetails;
import anyframe.oden.eclipse.core.editors.TaskPage;
import anyframe.oden.eclipse.core.explorer.dialogs.DeployNowDialog;

/**
 * Add a new agent action in the Oden view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author HONG Junghwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class RunDeployTaskAction extends AbstractEditorsAction {

	/**
	 * 
	 */
	public RunDeployTaskAction() {
		super(
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskRunBtn,
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskRunBtn,
				OdenMessages.ODEN_EDITORS_TaskPage_RunDeployImage);
	}

	/**
	 * 
	 */
	public void run() {
		TaskPage page = new TaskPage();
		DeployNowDialog dialog;
		TaskDetails details = null;
		ISelection selection = page.taskViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		details = (TaskDetails) obj;
		try {
			dialog = new DeployNowDialog(Display.getCurrent().getActiveShell(),
					null , details.getTaskName() , page.shellUrl);
			dialog.open();
		} catch (Exception odenException) {
			OdenActivator.error(
					"Exception occured while searching deploy items.",
					odenException);
			odenException.printStackTrace();
		}
	}

}
