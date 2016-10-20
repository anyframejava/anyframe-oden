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
import anyframe.oden.eclipse.core.editors.PolicyDetails;
import anyframe.oden.eclipse.core.editors.PolicyPage;
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
public class NewPolicyAction extends AbstractEditorsAction {

	/**
	 * 
	 */
	public NewPolicyAction() {
		super(
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskSaveBtn,
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskSaveBtn,
				UIMessages.ODEN_EDITORS_TaskPage_TaskPageTitleImage);
	}

	/**
	 * 
	 */
	public void run(String title) {
		PolicyPage page = OdenEditor.getDefault(title).getPolicypage();
		page.getPolicyNameText().setEnabled(true);
		page.getRemovePolicy().setEnabled(false);
		page.chageMandaLabel();
		// initialize Text
		page.clearText();
		// input temporary field
		addTempTaskName(title);
		
	}
	
	private void addTempTaskName(String title) {
		PolicyPage page = OdenEditor.getDefault(title).getPolicypage();
		// create temp task 
		String tempPolicyName = UIMessages.ODEN_EDITORS_PolicyPage_TempPolicyName;
		String tempPolicyDesc = UIMessages.ODEN_EDITORS_PolicyPage_TempPolicyDesc;
		
		page.getPolicyNameText().setText(tempPolicyName);
		page.getDescriptionText().setText(tempPolicyDesc);
		PolicyDetails details = null;

		details = new PolicyDetails(tempPolicyName, tempPolicyDesc, null, null, null);
		page.getPolicyViewer().add(details);
		
		page.getPolicyViewer().getTable().select(page.getLastNum());
		page.getAddPolicy().setEnabled(false);
		page.getRemovePolicy().setEnabled(true);
	}
}
