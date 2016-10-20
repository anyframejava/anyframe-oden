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
package anyframe.oden.eclipse.core.jobmanager.actions;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.history.AbstractDeploymentHistoryViewAction;
import anyframe.oden.eclipse.core.jobmanager.JobManagerView;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * The Implementation of JobManagerRefreshAction,
 * for the Anyframe Oden Job Manager view.
 * 
 * @author HONG JungHwan
 * @version 1.1.0
 *
 */

public class JobManagerRefreshAction extends AbstractDeploymentHistoryViewAction {

	/**
	 * Job Manager Refresh Action constructor
	 */
	public JobManagerRefreshAction() {
		super(
				UIMessages.ODEN_JOBMANAGER_Actions_JobManagerRefreshAction_JobManagerRefresh,
				UIMessages.ODEN_JOBMANAGER_Actions_JobManagerRefreshAction_JobManagerRefreshToolTip,
				UIMessages.ODEN_JOBMANAGER_Actions_JobManagerRefreshAction_JobManagerRefreshIcon);
	}
	/**
	 * Job Manager Refresh Action Run
	 */
	public void run() {
		JobManagerView view = OdenActivator.getDefault().getJObManagerView();
		view.refreshServerCombo();
		view.refresh();
	}
}
