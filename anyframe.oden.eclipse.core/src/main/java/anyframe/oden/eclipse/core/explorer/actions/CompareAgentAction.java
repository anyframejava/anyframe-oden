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
package anyframe.oden.eclipse.core.explorer.actions;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenTrees.TreeObject;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.explorer.AbstractExplorerViewAction;
import anyframe.oden.eclipse.core.explorer.dialogs.CompareResultReportDialog;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.DialogUtil;

/**
 * Compare selected agents. This class extends AbstractExplorerViewAction class.
 * 
 * @author LEE Sujeong
 * @version 1.1.0
 * 
 */
public class CompareAgentAction extends AbstractExplorerViewAction {

	private Object[] obj;
	private String url;

	private ArrayList<JSONObject> compareAgents = new ArrayList<JSONObject>();

	private static OdenBrokerService OdenBroker = new OdenBrokerImpl();

	public CompareAgentAction(Object[] obj) {
		super(
				UIMessages.ODEN_EXPLORER_Actions_CompareAgentAction_CompareAgent,
				UIMessages.ODEN_EXPLORER_Actions_CompareAgentAction_CompareAgentToolTip,
				UIMessages.ODEN_EXPLORER_Actions_CompareAgentAction_CompareAgentIcon);

		this.obj = obj;
	}

	public void run() {

		String strServer = ((TreeObject) obj[0]).getParent().getName();
		Server server = OdenActivator.getDefault().getAliasManager()
				.getServerManager().getServer(strServer);

		url = CommonMessages.ODEN_CommonMessages_ProtocolString_HTTP
				+ server.getUrl()
				+ CommonMessages.ODEN_CommonMessages_ProtocolString_HTTPsuf;

		String result = ""; //$NON-NLS-1$

		String msgCompareAgent = CommandMessages.ODEN_CLI_COMMAND_spectrum_compare
				+ " " + CommandMessages.ODEN_CLI_OPTION_targetagent + " ";
		for (int i = 0; i < obj.length; i++) {
			String tempObj = obj[i] + ""; //$NON-NLS-1$
			String justAgent = tempObj.substring(1);
			String[] justAgentToken = justAgent.split(" ");

			String agentNameTemp = "\"";
			for (int n = 0; n < justAgentToken.length-1; n++) {
				agentNameTemp += justAgentToken[n] + " ";
			}
			agentNameTemp = agentNameTemp.trim() + "\"";
			msgCompareAgent+=agentNameTemp+" ";
		}

		try {
			msgCompareAgent = msgCompareAgent.trim();
			result = OdenBroker.sendRequest(url, msgCompareAgent + " " //$NON-NLS-1$
					+ CommandMessages.ODEN_CLI_OPTION_json);
			JSONArray ja = new JSONArray(result);

			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				compareAgents.add(jo);
			}

			notifyResult();
		} catch (OdenException e) {
			OdenActivator
					.error(
							UIMessages.ODEN_EXPLORER_Actions_CompareAgentAction_Exception_CompareAgent,
							e);
		} catch (Exception e) {

		}
	}

	private void notifyResult() {

		boolean success = false;
		if (compareAgents.size() == 0) {
			DialogUtil
					.openMessageDialog(
							UIMessages.ODEN_EXPLORER_Actions_CompareAgentAction_WarningTitle,
							UIMessages.ODEN_EXPLORER_Actions_CompareAgentAction_WarningDefaultLocationCheck,
							MessageDialog.INFORMATION);
		} else {
			try {
				for (int i = 0; i < compareAgents.size(); i++) {
					String match = compareAgents.get(i).getString("match"); //$NON-NLS-1$
					if (match.equals("true")) { //$NON-NLS-1$
						success = true;
					} else {
						success = false;
						break;
					}
				}
			} catch (Exception e) {
				OdenActivator
						.error(
								UIMessages.ODEN_EXPLORER_Actions_CompareAgentAction_Exception_CompareAgent,
								e);
			} finally {
				if (success) {
					DialogUtil
							.openMessageDialog(
									UIMessages.ODEN_EXPLORER_Actions_CompareAgentAction_WarningTitle,
									UIMessages.ODEN_EXPLORER_Actions_CompareAgentAction_CompareAgentSuccessMsg,
									MessageDialog.INFORMATION);
					// CompareSuccessDialog dialog = new
					// CompareSuccessDialog(Display.getCurrent().getActiveShell());
					// dialog.open();
				} else {
					CompareResultReportDialog dialog = new CompareResultReportDialog(
							Display.getCurrent().getActiveShell(), obj,
							compareAgents);
					dialog.open();
				}
			}
		}

	}

}
