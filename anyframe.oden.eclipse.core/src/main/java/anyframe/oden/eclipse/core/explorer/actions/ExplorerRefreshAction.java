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

import anyframe.oden.eclipse.core.explorer.AbstractExplorerViewAction;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Refresh an Oden explorer view. This class extends
 * AbstractExplorerViewAction class.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class ExplorerRefreshAction extends AbstractExplorerViewAction {

	/**
	 * 
	 */
	public ExplorerRefreshAction() {
		super(
				UIMessages.ODEN_SNAPSHOT_Actions_RefreshAction_Refresh,
				UIMessages.ODEN_SNAPSHOT_Actions_RefreshAction_RefreshTooltip,
				UIMessages.ODEN_SNAPSHOT_Actions_RefreshAction_RefreshIcon);
	}

	/**
	 * run RefreshExplorerView
	 */
	public void run() {
		getView().refresh();
		
	}

}
