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
