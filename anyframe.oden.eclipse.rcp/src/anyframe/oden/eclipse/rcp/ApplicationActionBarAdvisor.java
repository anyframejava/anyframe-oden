package anyframe.oden.eclipse.rcp;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchWindow window;

	private IWorkbenchAction closeAction;
	private IWorkbenchAction closeAllAction;
	private IWorkbenchAction refreshAction;
	private IWorkbenchAction propertiesAction;
	private IWorkbenchAction exitAction;

	private IContributionItem _viewList;

	private IWorkbenchAction preferenceAction;

	private IWorkbenchAction aboutAction;
	private IWorkbenchAction showHelpAction;
	private IWorkbenchAction dynamicHelpAction;
	
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
		window = configurer.getWindowConfigurer().getWindow();
	}

	protected void makeActions(IWorkbenchWindow window) {
		register(ActionFactory.QUIT.create(window));
		register(ActionFactory.HELP_CONTENTS.create(window));

		closeAction = ActionFactory.CLOSE.create(window);
		register(closeAction);
		closeAllAction = ActionFactory.CLOSE_ALL.create(window);
		register(closeAllAction);
		refreshAction = ActionFactory.REFRESH.create(window);
		register(refreshAction);
		propertiesAction = ActionFactory.PROPERTIES.create(window);
		register(propertiesAction);
		exitAction = ActionFactory.QUIT.create(window);
		register(exitAction);

		_viewList = ContributionItemFactory.VIEWS_SHORTLIST.create(window);

		preferenceAction = ActionFactory.PREFERENCES.create(window);
		register(preferenceAction);

		aboutAction = ActionFactory.ABOUT.create(window);
		register(aboutAction);
		showHelpAction = ActionFactory.HELP_CONTENTS.create(window);
		register(showHelpAction);
		dynamicHelpAction = ActionFactory.DYNAMIC_HELP.create(window);
		register(dynamicHelpAction);

	}

	protected void fillMenuBar(IMenuManager menuBar) {

		// create menus
		MenuManager fileMenu = new MenuManager("&File", "file");
		// MenuManager editMenu = new MenuManager("&Edit", "edit");
		MenuManager viewMenu = new MenuManager("&View", "Oden.View");
		MenuManager windowMenu = new MenuManager("&Window", "window");
		MenuManager helpMenu = new MenuManager("&Help", "help");

		// create file menu
		fileMenu.add(closeAction);
		fileMenu.add(closeAllAction);
		fileMenu.add(new Separator());
		fileMenu.add(refreshAction);
		fileMenu.add(propertiesAction);
		fileMenu.add(new Separator());
		fileMenu.add(exitAction);
		menuBar.add(fileMenu);

		// menuBar.add(editMenu);

		// create view menu
		viewMenu.add(_viewList);
		menuBar.add(viewMenu);
		// viewMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		// windowMenu.add(item);
		windowMenu.add(preferenceAction);
		menuBar.add(windowMenu);

		// create help menu
		helpMenu.add(aboutAction);
		helpMenu.add(showHelpAction); // NEW
		helpMenu.add(dynamicHelpAction); // NEW
		menuBar.add(helpMenu);

	}

	private IWorkbenchWindow getWindow() {
		return window;
	}
    
}
