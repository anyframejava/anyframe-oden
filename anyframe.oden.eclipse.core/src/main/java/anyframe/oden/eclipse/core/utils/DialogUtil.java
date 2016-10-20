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
