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
	private static final String MSG_TASK_SAVE = CommandMessages.ODEN_CLI_COMMAND_task_add;
	private static final String POLI_OPT = CommandMessages.ODEN_CLI_OPTION_policy;
	private static final String DESC_OPT = CommandMessages.ODEN_CLI_OPTION_desc;
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