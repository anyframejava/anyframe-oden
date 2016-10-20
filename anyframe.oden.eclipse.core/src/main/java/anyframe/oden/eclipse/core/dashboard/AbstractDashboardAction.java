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
package anyframe.oden.eclipse.core.dashboard;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Abstract implementation for a context menu action in the Dashboard.
 * When adding actions is required, extend this class.
 * 
 * @author LEE sujeong
 * @version 1.1.0
 *
 */
public class AbstractDashboardAction extends Action implements IViewActionDelegate {
	/**
	 * Constructor of AbstractDashboardAction class.
	 * @param actionId
	 * @param actionTooltipText
	 * @param actionIconId
	 */
	public AbstractDashboardAction(String actionId, String actionToolTipText, String actionIconId) {
		this(actionId, actionToolTipText, actionIconId, SWT.NONE);
	}
	
	/**
	 * Constructor of AbstractDashboardAction class.
	 * @param actionId
	 * @param actionTooltipText
	 * @param actionIconId
	 * @param style
	 */
	public AbstractDashboardAction(String actionId, String actionToolTipText, String actionIconId, int style) {
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
	
}
