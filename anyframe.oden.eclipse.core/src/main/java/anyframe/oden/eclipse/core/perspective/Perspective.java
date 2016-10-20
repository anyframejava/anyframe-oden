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
package anyframe.oden.eclipse.core.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import anyframe.oden.eclipse.core.explorer.ExplorerView;
import anyframe.oden.eclipse.core.history.DeploymentHistoryView;
import anyframe.oden.eclipse.core.jobmanager.JobManagerView;
import anyframe.oden.eclipse.core.snapshot.SnapshotView;

public class Perspective implements IPerspectiveFactory {

	public static final String ID = "anyframe.oden.eclipse.core.perspective.OdenPerspective";
	
	public void createInitialLayout(IPageLayout layout) {
		defineActions(layout);
		defineLayout(layout);
	}

	private void defineActions(IPageLayout layout) {
		layout.addShowViewShortcut(ExplorerView.ID);
		layout.addShowViewShortcut(SnapshotView.ID);
		layout.addShowViewShortcut(JobManagerView.ID);
		layout.addShowViewShortcut(DeploymentHistoryView.ID);
		//error log view
		layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");
	}

	private void defineLayout(IPageLayout layout) {

		layout.setEditorAreaVisible(true);
		String editorArea = layout.getEditorArea();

		IFolderLayout bottom = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.55f, editorArea);
		bottom.addView(SnapshotView.ID);
		bottom.addView(DeploymentHistoryView.ID);
		bottom.addView("org.eclipse.pde.runtime.LogView");
		
		IFolderLayout topLeft = layout.createFolder("topLeft",
				IPageLayout.LEFT, 0.25f, editorArea);
		topLeft.addView(ExplorerView.ID);

		IFolderLayout topRight = layout.createFolder("topRight",
				IPageLayout.RIGHT, 0.75f, editorArea);
		topRight.addView(JobManagerView.ID);

	}

}
