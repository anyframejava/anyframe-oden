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
package anyframe.oden.eclipse.core.snapshot.actions;

import org.eclipse.jface.viewers.ViewerSorter;

import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.snapshot.AbstractSnapshotViewAction;
import anyframe.oden.eclipse.core.snapshot.SnapshotView;

/**
 * Refresh Snapshot view's tree, detail information, server combo'x data. This
 * class extends AbstractSnapshotViewAction class.
 * 
 * @author LEE Sujeong
 * @version 1.0.0 RC2
 * 
 */
public class RefreshAction extends AbstractSnapshotViewAction {

	/**
	 * Constructor of RefreshAction forward actionId, actionTooltipText,
	 * actionIconId to AbstractSnapshotViewAction
	 */
	public RefreshAction() {
		super(UIMessages.ODEN_SNAPSHOT_Actions_RefreshAction_Refresh,
				UIMessages.ODEN_SNAPSHOT_Actions_RefreshAction_RefreshTooltip,
				UIMessages.ODEN_SNAPSHOT_Actions_RefreshAction_RefreshIcon);
	}

	/**
	 * Run this Action
	 */
	public void run() {
		new SnapshotView();
		SnapshotView
				.setStatusMessage(UIMessages.ODEN_SNAPSHOT_Actions_MsgRefreshTree);
		SnapshotView.clearComposite();
		SnapshotView.refreshServerList();
		SnapshotView.invisibleRoot = null;
		SnapshotView.refreshTree();
	}

	/**
	 * Available check this method
	 */
	public boolean isAvailable() {
		if (getView() == null) {
			return false;
		}

		return true;
	}

	class NameSorter extends ViewerSorter {
	}
}
