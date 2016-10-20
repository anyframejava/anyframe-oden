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

import java.text.SimpleDateFormat;
import java.util.Date;

import anyframe.oden.eclipse.core.dashboard.AbstractDashboardAction;
import anyframe.oden.eclipse.core.dashboard.Dashboard;
import anyframe.oden.eclipse.core.dashboard.DashboardPage;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.CommonUtil;

/**
 * Refresh Dashboard. This class extends AbstractDashboardAction class.
 * 
 * @author LEE Sujeong
 * @version 1.1.0
 * 
 */
public class DashboardRefreshAction extends AbstractDashboardAction {

	private String server;
	
	CommonUtil util = new CommonUtil();
	
	public DashboardRefreshAction(String serverNickName) {
		super(
				UIMessages.ODEN_DASHBOARD_Actions_DashboardRefreshAction_Refresh,
				UIMessages.ODEN_DASHBOARD_Actions_DashboardRefreshAction_RefreshToolTip,
				UIMessages.ODEN_DASHBOARD_Actions_DashboardRefreshAction_RefreshIcon);
		this.server = serverNickName;
	}

	public void run() {
		String weekAgo = util.getWeekDateFormat(UIMessages.ODEN_DASHBOARD_DashboardPage_DateFormat);
		
		SimpleDateFormat df = new SimpleDateFormat(
				UIMessages.ODEN_DASHBOARD_DashboardPage_DateFormat);
		String test = df.format(new Date());

		DashboardPage page = Dashboard.getDefault(
				server + UIMessages.ODEN_DASHBOARD_DashboardPage_DashboardSuf)
				.getDashboardPage();
//		page.dateFrom.setText(weekAgo);
//		page.dateTo.setText(test);
		page.loadInitData();
		page.fillResultData();
		page.getDashboardViewer().refresh();
		page.getResultViewer().refresh();

	}

	public void refreshFilteredTable() {

		DashboardPage page = Dashboard.getDefault(
				server + UIMessages.ODEN_DASHBOARD_DashboardPage_DashboardSuf)
				.getDashboardPage();
		page.loadInitData();
		page.fillResultData();
		page.getDashboardViewer().refresh();
		page.getResultViewer().refresh();

	}

}
