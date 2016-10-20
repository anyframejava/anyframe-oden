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
package anyframe.oden.eclipse.core.snapshot;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Abstract implementation for a context menu action in the Snapshot view.
 * When adding actions is required, extend this class.
 * 
 * @author LEE Sujeong
 * @version 1.0.0 RC2
 *
 */
public class AbstractSnapshotViewAction extends Action implements IViewActionDelegate {

	/**
	 * Constructor of AbstractSnapshotViewAction class.
	 * @param actionId
	 * @param actionTooltipText
	 * @param actionIconId
	 */
	public AbstractSnapshotViewAction(String actionId, String actionToolTipText, String actionIconId) {
		this(actionId, actionToolTipText, actionIconId, SWT.NONE);
	}
	
	/**
	 * Constructor of AbstractSnapshotViewAction class.
	 * @param actionId
	 * @param actionTooltipText
	 * @param actionIconId
	 * @param style
	 */
	public AbstractSnapshotViewAction(String actionId, String actionToolTipText, String actionIconId, int style) {
		super(actionId);
		setToolTipText(actionToolTipText);
		if (actionIconId != null) {
			ImageDescriptor image = ImageUtil.getImageDescriptor(actionIconId);
			setImageDescriptor(image);
			setHoverImageDescriptor(image);
		}
	}
	
	public void init(IViewPart arg0) {
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		action.setEnabled(true);
	}

	public boolean isAvailable() {
		return true;
	}
	
	protected SnapshotView getView() {
		return OdenActivator.getDefault().getSnapshotView();
	}
}
