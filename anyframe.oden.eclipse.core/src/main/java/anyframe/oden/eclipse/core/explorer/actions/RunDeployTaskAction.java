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
package anyframe.oden.eclipse.core.explorer.actions;

import org.eclipse.swt.widgets.Display;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.explorer.AbstractExplorerViewAction;
import anyframe.oden.eclipse.core.explorer.dialogs.DeployNowDialog;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Run task action in the Oden view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author HongJungHwan
 * @version 1.0.0 RC1
 * 
 */
public class RunDeployTaskAction extends AbstractExplorerViewAction {

	/**
	 * 
	 */
	private Object[] obj;

	public RunDeployTaskAction(Object[] obj) {

		super(
				UIMessages.ODEN_EXPLORER_Actions_RunDeployAction_DeployNow,
				UIMessages.ODEN_EXPLORER_Actions_RunDeployAction_DeployNowToolTip,
				UIMessages.ODEN_EXPLORER_Actions_RunDeployAction_DeployNowIcon);

		this.obj = obj;
	}

	/**
	 * 
	 */
	public void run() {

		DeployNowDialog dialog;
		try {
			dialog = new DeployNowDialog(Display.getCurrent().getActiveShell(),
					obj , null , null);
			dialog.open();
		} catch (Exception odenException) {
			OdenActivator.error(
					"Exception occured while searching deploy items.",
					odenException);
			odenException.printStackTrace();
		}

	}
}
