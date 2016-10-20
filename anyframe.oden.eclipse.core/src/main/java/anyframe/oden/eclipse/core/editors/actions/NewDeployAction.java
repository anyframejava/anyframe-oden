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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenMessages;
import anyframe.oden.eclipse.core.brokers.OdenBroker;
import anyframe.oden.eclipse.core.brokers.ShellException;
import anyframe.oden.eclipse.core.editors.AbstractEditorsAction;
import anyframe.oden.eclipse.core.editors.PolicyPage;
import anyframe.oden.eclipse.core.editors.TaskDetails;
import anyframe.oden.eclipse.core.editors.TaskPage;
import anyframe.oden.eclipse.core.editors.dialogs.SelectAgentsDialog;
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
public class NewDeployAction extends AbstractEditorsAction {

	/**
	 * 
	 */
	
	public NewDeployAction() {
		super(
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskSaveBtn,
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskSaveBtn,
				OdenMessages.ODEN_EDITORS_TaskPage_TaskPageTitleImage);
	}

	/**
	 * 
	 */
	public void run() {
		SelectAgentsDialog dialog;
		PolicyPage page = new PolicyPage();
		try {
			dialog = new SelectAgentsDialog(Display.getCurrent()
					.getActiveShell(), page.getShellUrl());
			dialog.open();
		} catch (Exception odenException) {
			// TODO Auto-generated catch block
			OdenActivator.error(
					"Exception occured while open select Agent dialog.",
					odenException);
			odenException.printStackTrace();
		}
	}

}
