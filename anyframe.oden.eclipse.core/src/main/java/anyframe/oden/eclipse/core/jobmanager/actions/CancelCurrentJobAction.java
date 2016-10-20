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
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.jobmanager.AbstractJobManagerViewAction;
import anyframe.oden.eclipse.core.jobmanager.JobManagerView;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * The Implementation of CancelCurrentJobAction,
 * for the Anyframe Oden Job Manager view.
 * 
 * @author HONG JungHwan
 * @version 1.1.0
 *
 */

public class CancelCurrentJobAction extends AbstractJobManagerViewAction{

	private String shellUrl;
	
	private Object element;
	
	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();
	
	/**
	 * 
	 */
	public CancelCurrentJobAction() {
		super(
				UIMessages.ODEN_JOBMANAGER_Actions_JobManagerCancelAction_JobManagerCancel,
				UIMessages.ODEN_JOBMANAGER_Actions_JobManagerCancelAction_JobManagerCancelToolTip,
				UIMessages.ODEN_JOBMANAGER_Actions_JobManagerCancelAction_JobManagerCancelIcon);
	}
	/**
	 * 
	 */
	public void run() {
		JobManagerView view = OdenActivator.getDefault().getJObManagerView();
		this.shellUrl = view.getShellUrl();
		
		Object[] selections = view.getSelected();
		element = selections[0];
		
		cancelJob();
		view.refresh();
	}
	
	private void cancelJob() {
		String txId = element.toString().substring(element.toString().indexOf(":") + 1, element.toString().indexOf("-"));
		String commnd = CommandMessages.ODEN_CLI_COMMAND_status_stop + " " + txId;
		
		try {
			OdenBroker.sendRequest(shellUrl, commnd);
		} catch (OdenException e) {
		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while cancel current job.",odenException);
		}
	}
}
