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
package anyframe.oden.eclipse.core.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * This class represents an <strong>Oden</strong> preference page,
 * under the <strong>&quot;Anyframe&quot;</strong> category,
 * that is contributed to the Preferences dialog.
 * <p>
 * This page is used to modify preferences only.
 * They are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0 M2
 *
 */
public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		//		setPreferenceStore(OdenActivator.getDefault().getPreferenceStore());
		noDefaultAndApplyButton(); //Suppresses creation of the standard Default and Apply buttons for this page.
		setDescription(UIMessages.ODEN_PREFERENCES_PreferencePage_Description_1 +
				UIMessages.ODEN_PREFERENCES_PreferencePage_Description_2 +
				UIMessages.ODEN_PREFERENCES_PreferencePage_Description_3 +
				UIMessages.ODEN_PREFERENCES_PreferencePage_Description_4 +
				UIMessages.ODEN_PREFERENCES_PreferencePage_Description_5 +
				UIMessages.ODEN_PREFERENCES_PreferencePage_Description_6);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}