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

import java.util.ArrayList;

import org.eclipse.swt.widgets.TreeItem;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.history.AbstractDeploymentHistoryViewAction;
import anyframe.oden.eclipse.core.history.DeploymentHistoryView;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * The Implementation of CancelHistoryAction,
 * for the Anyframe Oden Deployment History view.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC2
 *
 */

public class UndoHistoryAction extends AbstractDeploymentHistoryViewAction {
	
	private TreeItem[] selection;
	
	private ArrayList<UndoInfo> undolist;
	
	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();
	
	private String commnd;
	/**
	 * cancel History Action constructor
	 */
	public UndoHistoryAction(TreeItem[] selection) {
		super(
				UIMessages.ODEN_HISTORY_Actions_UndoAction_Undo,
				UIMessages.ODEN_HISTORY_Actions_UndoAction_UndoToolTip,
				UIMessages.ODEN_HISTORY_Actions_UndoAction_UndoIcon);
		this.selection = selection;
	}
	/**
	 * cancel History Action Run
	 */
	public void run() {
		UndoInfo info = null;
		undolist = new ArrayList<UndoInfo>();
		
		for(TreeItem treeitem : selection) {
			if(treeitem.getText(0).equals("")) {
				// childeren
				String txid = treeitem.getParent().getTopItem().getText(0);
				info = new UndoInfo("chilld" , txid, treeitem.getText(4), treeitem.getText(10), treeitem.getText(9));
			} else {
				// root
				info = new UndoInfo("root" , treeitem.getText(0), null, null, null);
			}
			undolist.add(info);
		}
		this.makeCommand();
		
	}
	
	/*
	 * make command which cancel deploy, rollback....
	 */
	private void makeCommand() {
		for(UndoInfo list : undolist) {
			commnd = "";
			if(list.getInfo().equals("root")) {
				commnd = CommandMessages.ODEN_CLI_COMMAND_history_undo + " "
						+ list.getTxid() + " "
						+ CommandMessages.ODEN_CLI_OPTION_json;
			} else {
				commnd = CommandMessages.ODEN_CLI_COMMAND_history_undo + " "
						+ list.getTxid();
				commnd = commnd + " " + CommandMessages.ODEN_CLI_OPTION_path
						+ " " + list.getAgent() + ":";
				commnd = commnd + list.getAbsolutepath();
				commnd = commnd + " " + list.getFilepath() + " "
						+ CommandMessages.ODEN_CLI_OPTION_json;
			}
			this.runAction();
		}
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
			OdenActivator.error("Exception occured while undoing deploy.", odenException);
			odenException.printStackTrace();
		}
	}
	/*
	 * Object UndoInfo , must rename
	 */
	private class UndoInfo {
		private String info;
		
		private String txid;
		
		private String agent;
		
		private String absolutepath;
		
		public String getAbsolutepath() {
			return absolutepath;
		}
		
		private String filepath;
		
		public String getTxid() {
			return txid;
		}
		
		public String getAgent() {
			return agent;
		}
		
		public String getFilepath() {
			return filepath;
		}

		public String getInfo() {
			return info;
		}
		
		public UndoInfo(String info , String txid , String agent , String filepath , String absolutepath){
			this.info = info;
			this.txid = txid;
			this.agent = agent;
			this.filepath = filepath;
			this.absolutepath = absolutepath;
		}
	}
}
