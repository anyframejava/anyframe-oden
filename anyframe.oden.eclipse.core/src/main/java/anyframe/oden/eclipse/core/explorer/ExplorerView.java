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
package anyframe.oden.eclipse.core.explorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenTrees.TreeObject;
import anyframe.oden.eclipse.core.OdenTrees.TreeParent;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.alias.ModelListener;
import anyframe.oden.eclipse.core.alias.Repository;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.explorer.actions.ExplorerRefreshAction;
import anyframe.oden.eclipse.core.explorer.actions.NewServerAction;
import anyframe.oden.eclipse.core.explorer.actions.NewRepositoryAction;
import anyframe.oden.eclipse.core.explorer.dialogs.CreateBuildRepositoryDialog;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.CommonUtil;
import anyframe.oden.eclipse.core.utils.DialogUtil;

/**
 * This class implements Oden Explorer view where users can manage profiles for
 * servers and repositories. Also, they can trigger Task/Policy editor within it.
 * 
 * @author RHIE Jihwan
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 * 
 */
public class ExplorerView extends ViewPart implements ModelListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = OdenActivator.PLUGIN_ID + ".explorer.ExplorerView";

	private static final HashSet<Server> EMPTY_SERVERS = new HashSet<Server>();
	private static final HashSet<Repository> EMPTY_REPOSITORIES = new HashSet<Repository>();

	private static final String MSG_REPOSITORY_SHOW = CommandMessages.ODEN_EXPLORER_ExplorerView_Msg_RepoShow; 

	// Tree viewer for servers and repositories
	private TreeViewer treeViewer;

	private Clipboard clipboard;

	private String shellurl;

	private Repository repo;

	private String servername;

	private String[] hiddenFolder = CommandMessages.ODEN_EXPLORER_ExplorerView_HiddenFolder.split(",");

	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();

	/**
	 * The constructor
	 */
	public ExplorerView() {
		super();
		OdenActivator.getDefault().setExplorerView(this);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public final void createPartControl(final Composite parent) {

		OdenActivator.getDefault().getAliasManager().getServerManager().addListener(this);
		OdenActivator.getDefault().getAliasManager().getRepositoryManager().addListener(this);

		// create outline
		treeViewer = new TreeViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		getSite().setSelectionProvider(treeViewer);

		// create action bar
		contributeToActionBars();

		// add context menu
		addContextMenu();

		// use hash lookup to improve performance
		treeViewer.setUseHashlookup(true);

		// add content and label provider
		treeViewer.setContentProvider(new ExplorerViewContentProvider());
		treeViewer.setLabelProvider(new ExplorerViewLabelProvider());

		// set input session
		treeViewer.setInput(OdenActivator.getDefault().getAliasManager());

		treeViewer.expandToLevel(OdenActivator.getDefault().getAliasManager(), 1);

		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent event) {
				ExplorerView view = OdenActivator.getDefault().getExplorerView();
				Object[] selection = (view == null) ? null : view.getSelected();
				if (!(selection[0].toString().indexOf("[20") > 0)) { 
					TreeParent obj = ((TreeParent) selection[0]);
					if (!(treeViewer.getExpandedState(obj))) {
						String path = getFullpath(obj);
						String[] paths = path.split("/");
						if(!(paths[0].equals(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_ServersRootLabel)) &&
								paths.length > 1) {
							repo = OdenActivator.getDefault().getAliasManager().getRepositoryManager().getRepository(paths[1]);
							servername = repo.getServerToUse();
						}
						if (obj.getParent().toString().equals(
								UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoriesRootLabel)) {
							if(paths.length > 1) {
								repo = OdenActivator.getDefault().getAliasManager().getRepositoryManager().getRepository(paths[1]);
								servername = repo.getServerToUse();
							}
							if(chkExistServer()) {
								// not mapping server: opens editing Dialog
								if (DialogUtil.confirmMessageDialog(UIMessages.ODEN_EXPLORER_ExplorerView_Msg_Title, UIMessages.ODEN_EXPLORER_ExplorerView_Confirm_SelectServer)) {
									CreateBuildRepositoryDialog dialog =
										new CreateBuildRepositoryDialog(Display.getCurrent().getActiveShell(),CreateBuildRepositoryDialog.Type.CHANGE, repo);
									dialog.open();
								}
							} else {
								String serverUrl = OdenActivator.getDefault().getAliasManager().getServerManager().getServer(servername).getUrl();
								shellurl = CommonMessages.ODEN_CommonMessages_ProtocolString_HTTP + serverUrl + CommonMessages.ODEN_CommonMessages_ProtocolString_HTTPsuf;
								//								removeList(obj);
								try {
									if(obj.getChildren().length == 0)
										getChildList(obj);
								} catch (OdenException odenException) {

								} catch (Exception odenException) {
									OdenActivator.error(UIMessages.ODEN_EXPLORER_ExplorerView_Msg_ExceptionTree, odenException);
								}

								treeViewer.expandToLevel(obj, 1);
								treeViewer.refresh();
							}
						} else if (!(obj.getParent().toString().equals(""))
								&& !(obj.getParent().toString().equals(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_ServersRootLabel))) {
							//							removeList(obj);
							if(paths.length > 1) {
								String serverUrl = OdenActivator.getDefault().getAliasManager().getServerManager().getServer(servername).getUrl();
								repo = OdenActivator.getDefault().getAliasManager().getRepositoryManager().getRepository(paths[1]);
								shellurl = CommonMessages.ODEN_CommonMessages_ProtocolString_HTTP + serverUrl + CommonMessages.ODEN_CommonMessages_ProtocolString_HTTPsuf;
							}
							try {
								if(obj.getChildren().length == 0)
									getChildList(obj);
							} catch (OdenException odenException) {
							} catch (Exception odenException) {
								OdenActivator.error(UIMessages.ODEN_EXPLORER_ExplorerView_Msg_ExceptionTree, odenException);
							}

							treeViewer.expandToLevel(obj, 1);
							treeViewer.refresh();

						} else if ((obj.getParent().toString().equals(""))) { 
							treeViewer.expandToLevel(obj, 1);
						}
					} else {
						treeViewer.collapseToLevel(obj, 1);
						treeViewer.refresh();
					}
				}
			}
		});

		// Tree expansion event
		treeViewer.addTreeListener(new ITreeViewerListener() {
			public void treeCollapsed(final TreeExpansionEvent event) {
				Object obj = ((TreeParent) event.getElement());
				treeViewer.collapseToLevel(obj, 1);
				treeViewer.refresh();
			}

			public void treeExpanded(final TreeExpansionEvent event) {
			}
		});

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				refreshToolbar();
			}
		});

		treeViewer.expandToLevel(2);

		// TODO 도움말 완성 후 다시 체크할 것
		PlatformUI.getWorkbench().getHelpSystem().setHelp(treeViewer.getControl(), OdenActivator.HELP_PLUGIN_ID + ".oden.odenexplorerview"); 

		parent.layout();

	}

	private boolean chkExistServer() {
		Collection<Server> servers = OdenActivator.getDefault().getAliasManager().getServerManager().getServers();
		if(servername == null)
			return true;
		else
			for(Server server : servers){
				if(servername.equals(server.getNickname())){
					return false;
				}
			}
		return true;
	}
	private void contributeToActionBars() {
		IActionBars actionBars = getViewSite().getActionBars();
		fillLocalToolBar(actionBars.getToolBarManager());
		fillLocalPullDown(actionBars.getMenuManager());
	}

	private void fillLocalToolBar(final IToolBarManager toolBarManager) {
		toolBarManager.add(new NewServerAction());
		toolBarManager.add(new NewRepositoryAction());
		toolBarManager.add(new Separator());
		toolBarManager.add(new ExplorerRefreshAction());
	}

	private void fillLocalPullDown(final IMenuManager menuManager) {
		menuManager.add(new NewServerAction());
		menuManager.add(new NewRepositoryAction());
		menuManager.add(new Separator());
		menuManager.add(new ExplorerRefreshAction());
	}

	private void addContextMenu() {
		final ExplorerViewActionGroup actionGroup = new ExplorerViewActionGroup();
		MenuManager menuManager = new MenuManager("OdenExplorerContextMenu"); 
		menuManager.setRemoveAllWhenShown(true);
		Menu contextMenu = menuManager.createContextMenu(treeViewer.getTree());
		treeViewer.getTree().setMenu(contextMenu);

		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(final IMenuManager manager) {
				actionGroup.fillContextMenu(manager);
			}
		});
	}

	/**
	 * Implements anyframe.oden.eclipse.core.alias.ModelListener.modelChanged()
	 */
	public final void modelChanged() {
		getSite().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (!treeViewer.getTree().isDisposed()) {
					treeViewer.refresh();
					refreshToolbar();
				}
			}
		});
	}

	/**
	 * Overrides org.eclipse.ui.part.WorkbenchPart.dispose()
	 */
	public final void dispose() {
		if (clipboard != null) {
			clipboard.dispose();
			clipboard = null;
		}
		OdenActivator.getDefault().getAliasManager().getServerManager().removeListener(this);
		OdenActivator.getDefault().getAliasManager().getRepositoryManager().removeListener(this);
		super.dispose();
	}

	/**
	 * Gets tree viewer
	 * @return Tree Viewer object
	 */
	public final TreeViewer getTreeViewer() {
		return treeViewer;
	}

	/**
	 * Refreshes the tree
	 */
	public final void refresh() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!treeViewer.getTree().isDisposed()) {
					treeViewer.setContentProvider(new ExplorerViewContentProvider());
				}
				treeViewer.refresh();
				treeViewer.expandToLevel(2);

			}
		});
	}

	private void refreshToolbar() {
		IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
		IContributionItem[] items = toolbar.getItems();
		for (IContributionItem item : items) {
			if (item instanceof ActionContributionItem) {
				ActionContributionItem contrib = (ActionContributionItem) item;
				IAction contribAction = contrib.getAction();
				if (contribAction instanceof AbstractExplorerViewAction) {
					AbstractExplorerViewAction action = (AbstractExplorerViewAction) contribAction;
					action.setEnabled(action.isAvailable());
				}
			}
		}
	}

	/**
	 * Returns the objects which are currently selected. NOTE this is package
	 * private and should remain that way. - the implementation of the
	 * ExplorerView is now hidden from the rest of the application (see the
	 * getSelectedXxxx() methods below for a structured API)
	 * @return
	 */
	final Object[] getSelected() {
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		if (selection == null) {
			return null;
		}
		Object[] result = selection.toArray();
		if (result.length == 0) {
			return null;
		}
		return result;
	}

	/**
	 * Returns a list of the selected Servers. If recurse is true then the result
	 * will include any servers associated with other objects;
	 * @param recurse
	 * @return Set of Servers, never returns null
	 */
	public final Set<Server> getSelectedServers(final boolean recurse) {
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		if (selection == null) {
			return EMPTY_SERVERS;
		}

		LinkedHashSet<Server> result = new LinkedHashSet<Server>();
		Iterator<?> iter = selection.iterator();
		while (iter.hasNext()) {
			Server obj = OdenActivator.getDefault().getAliasManager().getServerManager().getServer(iter.next().toString());
			if (obj instanceof Server){
				result.add((Server) obj);
			}
		}

		return result;
	}

	/**
	 * Returns the first available selected Server; if recurse is true, then
	 * indirectly selected servers are included
	 * @param recurse
	 * @return
	 */
	public Server getSelectedServer(boolean recurse) {
		return (Server) getFirstOf(getSelectedServers(recurse));
	}

	/**
	 * Returns a list of the selected Build Repositories. If recurse is true
	 * then the result will include any servers associated with other objects.
	 * @param recurse
	 * @return Set of Build Repositories, never returns null
	 */
	public Set<Repository> getSelectedRepositories(boolean recurse) {
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		if (selection == null)
			return EMPTY_REPOSITORIES;

		LinkedHashSet<Repository> result = new LinkedHashSet<Repository>();
		Iterator<?> iter = selection.iterator();
		while (iter.hasNext()) {
			Repository obj = OdenActivator.getDefault().getAliasManager().getRepositoryManager().getRepository(iter.next().toString());
			if (obj instanceof Repository)
				result.add((Repository) obj);
		}

		return result;
	}

	/**
	 * Returns the first available selected Build Repository; if recurse is
	 * true, then indirectly selected servers are included
	 * @param recurse
	 * @return
	 */
	public Repository getSelectedRepository(boolean recurse) {
		return (Repository) getFirstOf(getSelectedRepositories(recurse));
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {

	}

	/**
	 * Helper method which returns the first element of a set, or null if the
	 * set is empty (or if the set is null)
	 * @param set
	 *            the set to look into (may be null)
	 * @return
	 */
	private <T> T getFirstOf(final Set<T> set) {
		if (set == null) {
			return null;
		}
		Iterator<T> iter = set.iterator();
		if (iter.hasNext()) {
			return iter.next();
		}
		return null;
	}

	/**
	 * @return the clipboard
	 */
	public final Clipboard getClipboard() {
		if (clipboard == null) {
			clipboard = new Clipboard(getSite().getShell().getDisplay());
		}
		return clipboard;
	}

	/**
	 * @param clipboard
	 *            the clipboard to set
	 */
	public final void setClipboard(final Clipboard clipboard) {
		this.clipboard = clipboard;
	}
	
	private void getChildList(final TreeParent parent) throws OdenException {
		String repopath = ""; 
		String command = ""; 
		if (repo.getProtocol().equals(CommonMessages.ODEN_ALIAS_RepositoryManager_ProtocolSet_FileSystem)) {
			// when the protocol is Filesystem
			repopath = CommonMessages.ODEN_CommonMessages_ProtocolString_File + repo.getPath();
		} else {
			// when the protocol is FTP
			repopath = repo.getPath();
		}

		String url = CommonUtil.replaceIgnoreCase(getFullpath((TreeParent) parent),
				UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoriesRootLabel + "/" + repo.getNickname(), repopath); 

		if (repo.getProtocol().equals(
				CommonMessages.ODEN_ALIAS_RepositoryManager_ProtocolSet_FileSystem)) {
			// when the protocol is Filesystem
			command = MSG_REPOSITORY_SHOW + '"' + url + '"'; 
		} else {
			// when the protocol is FTP
			command = MSG_REPOSITORY_SHOW + CommonMessages.ODEN_CommonMessages_ProtocolString_FTP + repo.getUrl() + " " + '"' + url + '"' + " " + repo.getUser() + " " + repo.getPassword();  
		}
		ArrayList<String> FileList = getRepoShowList(shellurl, command);

		for (String file : FileList) {
			if (file.indexOf(UIMessages.ODEN_EXPLORER_ExplorerView_Index_Directory) == 0) {
				// Directory
				TreeParent sub = new TreeParent(CommonUtil.replaceIgnoreCase(file, UIMessages.ODEN_EXPLORER_ExplorerView_Index_Directory, ""));  
				parent.addChild(sub);
			} else {
				// File
				parent.addChild(new TreeObject(CommonUtil.replaceIgnoreCase(file, UIMessages.ODEN_EXPLORER_ExplorerView_Index_File, ""))); 
			}
		}
	}

	private String getFullpath(final TreeObject obj) {
		StringBuffer full = new StringBuffer(obj.getName());
		TreeParent parent = obj.getParent();
		while (parent != null) {
			full.insert(0, parent.getName() + "/"); 
			parent = parent.getParent();
		}
		if (full.toString().substring(0, 1).equals("/")) {
			return full.toString().substring(1);
		} else {
			return full.toString();
		}

	}

	private ArrayList<String> getRepoShowList(final String shellUrl,
			final String msgRepoList) {
		ArrayList<String> repoList = new ArrayList<String>();
		String result = ""; 
		String child = ""; 
		try {
			result = OdenBroker.sendRequest(shellUrl, msgRepoList);
			if(result != null){
				JSONArray array = new JSONArray(result);

				for (int i = 0; i < array.length(); i++) {
					String type = (String) ((JSONObject) array.get(i)).get(UIMessages.ODEN_EXPLORER_ExplorerView_Index_Type);
					String name = (String) ((JSONObject) array.get(i)).get(UIMessages.ODEN_EXPLORER_ExplorerView_Index_Name);
					if(this.checkFolderVisible(name)){
						if (name.lastIndexOf(File.separator) > 0) {
							// when the separator follows Microsoft Windows or equivalent operating system conventions
							name = name.substring(name.lastIndexOf(File.separator) + 1);
						} else if (name.lastIndexOf("/") > 0) { 
							// when the separator follows UNIX or equivalent operating system conventions
							name = name.substring(name.lastIndexOf("/") + 1); 
						}

						String date = (String) ((JSONObject) array.get(i)).get(UIMessages.ODEN_EXPLORER_ExplorerView_Index_Date);
						child = type + name + (type.equals(UIMessages.ODEN_EXPLORER_ExplorerView_Index_File) ? "[" + date + "]" : "");   
						repoList.add(child);
					}
				}
			}
		} catch (OdenException odenException) {
		} catch (Exception odenException) {
			OdenActivator.error(UIMessages.ODEN_EXPLORER_ExplorerView_Msg_ExceptionRepoList, odenException);
			odenException.printStackTrace();
		}

		return repoList;
	}

	private boolean checkFolderVisible(final String name) {
		for(String folder : hiddenFolder){
			if(name.indexOf(folder) > 0) {
				return false;
			}
		}	
		return true;
	}
}
