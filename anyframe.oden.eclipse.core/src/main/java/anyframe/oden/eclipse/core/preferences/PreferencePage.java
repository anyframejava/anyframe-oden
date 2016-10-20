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
package anyframe.oden.eclipse.core.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import anyframe.oden.eclipse.core.OdenMessages;

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
		setDescription(OdenMessages.ODEN_PREFERENCES_PreferencePage_Description_1 +
				OdenMessages.ODEN_PREFERENCES_PreferencePage_Description_2 +
				OdenMessages.ODEN_PREFERENCES_PreferencePage_Description_3 +
				OdenMessages.ODEN_PREFERENCES_PreferencePage_Description_4 +
				OdenMessages.ODEN_PREFERENCES_PreferencePage_Description_5 +
				OdenMessages.ODEN_PREFERENCES_PreferencePage_Description_6);
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