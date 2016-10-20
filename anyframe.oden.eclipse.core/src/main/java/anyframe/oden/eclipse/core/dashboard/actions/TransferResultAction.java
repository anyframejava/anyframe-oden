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
package anyframe.oden.eclipse.core.dashboard.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.TableItem;
import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.dashboard.AbstractDashboardAction;
import anyframe.oden.eclipse.core.dashboard.Dashboard;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.DialogUtil;

/**
 * Transfer fetchlog to Spectrum. This class extends AbstractDashboardAction
 * class.
 * 
 * @author LEE Sujeong
 * @version 1.1.0
 * 
 */
public class TransferResultAction extends AbstractDashboardAction {

	TableItem[] id;
	private String url;
	protected static OdenBrokerService OdenBroker = new OdenBrokerImpl();

	public TransferResultAction(TableItem[] id, String url) {
		super(
				UIMessages.ODEN_DASHBOARD_Actions_TransferResultAction_Transfer,
				UIMessages.ODEN_DASHBOARD_Actions_TransferResultAction_TransferToolTip,
				UIMessages.ODEN_DASHBOARD_Actions_TransferResultAction_TransferIcon);
		this.id = new TableItem[id.length];
		this.id = id;
		this.url = url;
	}

	public void run() {

		String ids = ""; //$NON-NLS-1$
		String idList = ""; //$NON-NLS-1$

		for (int i = 0; i < id.length; i++) {
			idList += id[i].getText(0) + " "; //$NON-NLS-1$
		}

		String command = CommandMessages.ODEN_CLI_COMMAND_spectrum_fetchlog
				+ " " + CommandMessages.ODEN_CLI_OPTION_fetchloglistid + " "//$NON-NLS-1$ //$NON-NLS-2$
				+ idList + "-json";

		try {
			String result = OdenBroker.sendRequest(url, command);

			JSONArray array = new JSONArray(result);

			for (int i = 0; i < array.length(); i++) {
				JSONObject object = (JSONObject) array.get(i);

				String sORf = object.getString(id[i].getText(0));
				if (sORf
						.equals(UIMessages.ODEN_DASHBOARD_DashboardPage_DeployFailMark)) {
					ids += id[i].getText(0) + " "; //$NON-NLS-1$
				}
			}

			if (ids.length() > 0) {
				DialogUtil.openMessageDialog("Fail", //$NON-NLS-1$
						UIMessages.ODEN_DASHBOARD_DashboardPage_FetchlogFailIds
								+ ids, MessageDialog.INFORMATION);
			}
			new DashboardRefreshAction(Dashboard.getServer().getNickname())
					.refreshFilteredTable();

		} catch (OdenException odenException) {
			OdenActivator
					.error(
							UIMessages.ODEN_DASHBOARD_DashboardPage_Exception_SendFetchlog,
							odenException);
		} catch (Exception e) {
			OdenActivator
					.error(
							UIMessages.ODEN_DASHBOARD_DashboardPage_Exception_SendFetchlog,
							e);
		}
	}

}
