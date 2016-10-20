package anyframe.oden.eclipse.rcp;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.ActionSetRegistry;
import org.eclipse.ui.internal.registry.IActionSetDescriptor;

@SuppressWarnings("restriction")
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		return "anyframe.oden.eclipse.core.perspective.OdenPerspective";
	}

	@Override
	public String getMainPreferencePageId() {
		return "anyframe.oden.eclipse.core.preferences.Preference";
	}

	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);

		PlatformUI.getPreferenceStore().setValue(
				IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS,
				false);

		configurer.setSaveAndRestore(true);
		// For standalone app, remove the stuff we don't use
		ActionSetRegistry reg = WorkbenchPlugin.getDefault()
				.getActionSetRegistry();

		IActionSetDescriptor[] actionSets = reg.getActionSets();
		String[] removeActionSets = new String[] {
		// "org.eclipse.search.searchActionSet",
				"org.eclipse.ui.cheatsheets.actionSet",
				// "org.eclipse.ui.actionSet.keyBindings",
				// "org.eclipse.ui.edit.text.actionSet.navigation",
				// "org.eclipse.ui.edit.text.actionSet.annotationNavigation",
				"org.eclipse.ui.edit.text.actionSet.convertLineDelimitersTo",
				// "org.eclipse.ui.edit.text.actionSet.openExternalFile",
				"org.eclipse.ui.externaltools.ExternalToolsSet",
				// "org.eclipse.ui.WorkingSetActionSet",
				"org.eclipse.update.ui.softwareUpdates" };

		for (int i = 0; i < actionSets.length; i++) {
			boolean found = false;
			for (int j = 0; j < removeActionSets.length; j++) {
				if (removeActionSets[j].equals(actionSets[i].getId()))
					found = true;
			}

			if (!found)
				continue;
			IExtension ext = actionSets[i].getConfigurationElement()
					.getDeclaringExtension();
			reg.removeExtension(ext, new Object[] { actionSets[i] });
		}

	}
}
