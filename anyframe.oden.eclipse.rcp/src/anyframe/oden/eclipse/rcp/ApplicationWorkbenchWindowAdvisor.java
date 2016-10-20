package anyframe.oden.eclipse.rcp;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    public void postWindowCreate() {

		super.postWindowCreate();

		IWorkbenchWindowConfigurer windowConfigurer = getWindowConfigurer();
		windowConfigurer.setShowCoolBar(false);

		IMenuManager menuBar = windowConfigurer.getActionBarConfigurer()
				.getMenuManager();

		// clean file menu
		// hideMenuItem(menuBar, IWorkbenchActionConstants.M_FILE,
		// "converstLineDelimitersTo");
		// hideMenuItem(menuBar, IWorkbenchActionConstants.M_FILE,
		// "org.eclipse.ui.edit.text.openExternalFile");

		IContributionItem[] mItems, mSubItems;
		IMenuManager mm = getWindowConfigurer().getActionBarConfigurer()
				.getMenuManager();
		mItems = mm.getItems();
		for (int i = 0; i < mItems.length; i++) {
			if (mItems[i] instanceof MenuManager) {
				mSubItems = ((MenuManager) mItems[i]).getItems();
				for (int j = 0; j < mSubItems.length; j++) {
					if (mItems[i].getId().equals("file"))
						((MenuManager) mItems[i])
								.remove("org.eclipse.ui.openLocalFile");
					else if (mItems[i].getId().equals("help")) {
						((MenuManager) mItems[i]).remove("group.updates");
						((MenuManager) mItems[i])
								.remove("org.eclipse.update.ui.updateMenu");
						((MenuManager) mItems[i])
								.remove("org.eclipse.ui.actions.showKeyAssistHandler");
					}
				}
			}
		}

		// refresh menubar
		menuBar.updateAll(true);

	}
    
    public void preWindowOpen() {
    	IWorkbenchWindowConfigurer configurer = getWindowConfigurer();

		configurer.setInitialSize(new Point(1024, 768));
		configurer.setShowMenuBar(true);
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(true);
		configurer.setTitle("Anyframe Oden RCP");
		configurer.setShowPerspectiveBar(false);
    }
    
    private void hideMenuItem(IMenuManager menuBar, String menupath, String id) {

		IMenuManager menu = menuBar.findMenuUsingPath(menupath);
		if (menu == null) {
			return;
		}
		IContributionItem item = menu.findUsingPath(id);
		if (item != null) {
			item.setVisible(false);
		}
	}
}
