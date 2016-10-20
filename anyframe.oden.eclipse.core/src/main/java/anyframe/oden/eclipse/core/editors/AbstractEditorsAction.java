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
package anyframe.oden.eclipse.core.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.alias.AgentManager;
import anyframe.oden.eclipse.core.alias.RepositoryManager;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Abstract implementation for a context menu action in the Oden view.
 * When adding actions is required, extend this class.
 * 
 * @author Hong Junghwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 *
 */
public abstract class AbstractEditorsAction extends Action implements IViewActionDelegate {

	/**
	 * 
	 * @param actionId
	 * @param actionToolTipId
	 * @param actionIconId
	 */
	public AbstractEditorsAction(String actionId, String actionToolTipText, String actionIconId) {
		this(actionId, actionToolTipText, actionIconId, SWT.NONE);
	}

	/**
	 * 
	 * @param actionId
	 * @param actionToolTipText
	 * @param actionIconId
	 * @param style
	 */
	public AbstractEditorsAction(String actionId, String actionToolTipText, String actionIconId, int style) {
		super(actionId);
		setToolTipText(actionToolTipText);
		if (actionIconId != null) {
			ImageDescriptor image = ImageUtil.getImageDescriptor(actionIconId);
			setImageDescriptor(image);
			setHoverImageDescriptor(image);
		}
	}

	/**
	 * 
	 */
	public void init(IViewPart view) {

	}

	/**
	 * 
	 */
	public void run(IAction action) {
		run();
	}

	/**
	 * 
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		action.setEnabled(isAvailable());
	}

	/**
	 * 
	 * @return
	 */
	public boolean isAvailable() {
		return true;
	}
}
