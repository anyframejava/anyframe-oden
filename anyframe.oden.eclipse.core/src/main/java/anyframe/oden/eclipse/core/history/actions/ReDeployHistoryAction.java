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
package anyframe.oden.eclipse.core.history.actions;

import org.eclipse.swt.widgets.TreeItem;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.history.AbstractDeploymentHistoryViewAction;
import anyframe.oden.eclipse.core.history.DeploymentHistoryView;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * The Implementation of ReDeployHistoryAction,
 * for the Anyframe Oden Deployment History view.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC2
 *
 */

public class ReDeployHistoryAction extends AbstractDeploymentHistoryViewAction {
	
	private TreeItem[] selection;
	
	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();
	
	private String commnd;
	
	private String txId;
	
	/**
	 * Re Deploy Action constructor
	 */
	public ReDeployHistoryAction(TreeItem[] selection) {
		super(
				UIMessages.ODEN_HISTORY_Actions_ReDeployAction_ReDeploy,
				UIMessages.ODEN_HISTORY_Actions_ReDeployAction_ReDeployToolTip,
				UIMessages.ODEN_HISTORY_Actions_ReDeployAction_ReDeployIcon);
		
		this.selection = selection;
	}
	/**
	 * Re Deploy Action Run
	 */
	public void run() {
		txId = selection[0].getText();
		this.makeCommand();
	}
	
	/*
	 * make command which ReDeploy....
	 */
	private void makeCommand() {
		
		commnd = CommandMessages.ODEN_CLI_COMMAND_history_redeploy + " " + txId + " " + CommandMessages.ODEN_CLI_OPTION_json;
		this.runAction();
	}
	
	/*
	 * interface eclipse plug in with Server
	 * Using OdenBroker
	 */
	@SuppressWarnings("unused")
	private void runAction() {
		DeploymentHistoryView view = OdenActivator.getDefault().getDeploymentHistoryView();
		try {
			String result = OdenBroker.sendRequest(view.getUtil().getSHELL_URL(), commnd);
		} catch (Exception odenException) {
			// TODO: handle exception
			OdenActivator.error("Exception occured while re deploy.", odenException);
			odenException.printStackTrace();
		}
	}
}
