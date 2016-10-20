/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package anyframe.oden.eclipse.core.snapshot.actions;

import org.eclipse.jface.viewers.ViewerSorter;

import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.snapshot.AbstractSnapshotViewAction;
import anyframe.oden.eclipse.core.snapshot.SnapshotView;

/**
 * Refresh Snapshot view's tree, detail information, server combo'x data.
 * This class extends AbstractSnapshotViewAction class.
 * 
 * @author LEE Sujeong
 * @version 1.0.0 RC2
 *
 */
public class RefreshAction  extends AbstractSnapshotViewAction {

	/**
	 * Constructor of RefreshAction
	 * forward actionId, actionTooltipText, actionIconId to AbstractSnapshotViewAction
	 */
	public RefreshAction() {
		super(UIMessages.ODEN_SNAPSHOT_Actions_RefreshAction_Refresh, UIMessages.ODEN_SNAPSHOT_Actions_RefreshAction_RefreshTooltip, UIMessages.ODEN_SNAPSHOT_Actions_RefreshAction_RefreshIcon);
	}
	
	/**
	 * Run this Action
	 */
	public void run(){
		new SnapshotView();
		SnapshotView.setStatusMessage(UIMessages.ODEN_SNAPSHOT_Actions_MsgRefreshTree);
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
