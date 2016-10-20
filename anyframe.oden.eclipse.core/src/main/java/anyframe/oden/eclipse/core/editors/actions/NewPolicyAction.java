/*
 * Copyright 2009, 2010 SAMSUNG SDS Co., Ltd. All rights reserved.
 *
 * No part of this "source code" may be reproduced, stored in a retrieval
 * system, or transmitted, in any form or by any means, mechanical,
 * electronic, photocopying, recording, or otherwise, without prior written
 * permission of SAMSUNG SDS Co., Ltd., with the following exceptions:
 * Any person is hereby authorized to store "source code" on a single
 * computer for personal use only and to print copies of "source code"
 * for personal use provided that the "source code" contains SAMSUNG SDS's
 * copyright notice.
 *
 * No licenses, express or implied, are granted with respect to any of
 * the technology described in this "source code". SAMSUNG SDS retains all
 * intellectual property rights associated with the technology described
 * in this "source code".
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

		details = new PolicyDetails(tempPolicyName, tempPolicyDesc, null, null, null, null);
		page.getPolicyViewer().add(details);
		
		page.getPolicyViewer().getTable().select(page.getLastNum());
		page.getAddPolicy().setEnabled(false);
		page.getRemovePolicy().setEnabled(true);
	}
}
