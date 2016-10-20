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
package anyframe.oden.eclipse.core.explorer.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Create a compare success dialog.
 * 
 * @author LEE Sujeong
 * @version 1.1.0
 * 
 */
public class CompareSuccessDialog extends Dialog {

	public CompareSuccessDialog(Shell shell) {
		super(shell);
	}

	protected void configureShell(Shell newShell) {
		newShell.setText("Agent Compare Complete");
		super.configureShell(newShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite sub = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 15;
		gridLayout.marginWidth = 35;
		sub.setLayout(gridLayout);

		Label label = new Label(sub, SWT.NONE);
		label.setText("All of the investigated items are identical.");

		return super.createDialogArea(parent);
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "OK", true);
	}
}
