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
package anyframe.oden.eclipse.core.utils;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * This utility class implements common message dialogs for user interactive
 * actions.
 * 
 * @author KIM Changje
 * @author PARK Sooyeon
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class DialogUtil {

	protected DialogUtil() {
		// prevents calls from subclass
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param title
	 * @param message
	 * @param type
	 */
	public static void openMessageDialog(final String title,
			final String message, final int type) {
		if (type == MessageDialog.INFORMATION) {
			MessageDialog.openInformation(Display.getDefault().getActiveShell(), title, message);

		} else if (type == MessageDialog.ERROR) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), title, message);

		} else if (type == MessageDialog.WARNING) {
			MessageDialog.openWarning(Display.getDefault().getActiveShell(), title, message);

		}
	}

	/**
	 * 
	 * @param title
	 * @param message
	 * @return
	 */
	public static boolean confirmMessageDialog(final String title,
			final String message) {
		if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(), title, message)) {
			return true;

		}
		return false;

	}
}
