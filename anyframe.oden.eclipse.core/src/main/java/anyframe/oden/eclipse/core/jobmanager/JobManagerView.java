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
package anyframe.oden.eclipse.core.jobmanager;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenTrees.TreeObject;
import anyframe.oden.eclipse.core.OdenTrees.TreeParent;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.history.DeploymentHistoryView;
import anyframe.oden.eclipse.core.jobmanager.actions.JobManagerRefreshAction;
import anyframe.oden.eclipse.core.jobmanager.actions.DeployByFileReqAction;
import anyframe.oden.eclipse.core.jobmanager.actions.DeployByTaskAction;
import anyframe.oden.eclipse.core.jobmanager.actions.SetFileReqPathAction;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.CommonUtil;

/**
 * This class implements Anyframe Oden Job Manager view.
 * 
 * @author HONG JungHwan
 * @version 1.1.0
 * 
 */
public class JobManagerView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = OdenActivator.PLUGIN_ID + ".jobmanager.JobManagerView";

	ArrayList<Object> historyList;

	private TreeViewer treeViewer;

	private JobManagerViewContentProvider jobcontentprovider;

	private boolean isExpand = false;
	
	private String shellUrl;

	private Combo serverCombo;
	
	private String txId;
	
	private boolean isFail;
	
	private ArrayList<String> finishedList;
	
	CommonUtil util = new CommonUtil();

	protected OdenBrokerService broker = new OdenBrokerImpl();
	
	public CommonUtil getUtil() {
		return util;
	}

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public JobManagerView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		try {
			// create outline
			
			Composite composite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();

			composite.setLayout(layout);
			
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			
			serverCombo = new Combo(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.LEFT | SWT.READ_ONLY);
			
			serverCombo.setLayoutData(gridData);
			
			// Combo Event
			util.serverComboEvent(serverCombo);
			
			// Initial Combo
			util.initServerCombo(serverCombo);
			
			shellUrl = util.getSHELL_URL();
			
			GridData gridData1 = new GridData( GridData.FILL_BOTH);
			
			treeViewer = new TreeViewer(composite, SWT.V_SCROLL | SWT.H_SCROLL);
			treeViewer.getTree().setLayoutData(gridData1);
			
			// create action bar
			contributeToActionBars();
			
			// add context menu
			addContextMenu();
			
			// add content and label provider
			jobcontentprovider = new JobManagerViewContentProvider();
			jobcontentprovider.setShellURL(util.getSHELL_URL());
			
			treeViewer.setContentProvider(jobcontentprovider);
			treeViewer.setLabelProvider(new JobManagerViewLabelProvider());

			// set input session
			treeViewer.setInput(OdenActivator.getDefault().getAliasManager());

			treeViewer.addDoubleClickListener(new IDoubleClickListener() {
				public void doubleClick(final DoubleClickEvent event) {
					JobManagerView view = OdenActivator.getDefault()
							.getJObManagerView();
					Object[] selections = (view == null) ? null : view
							.getSelected();
					// avoiding nullpointexception for using Mac
					Object element = selections[0] != null ? selections[0] : null; 

					if(element != null) {
						String elementName = ((TreeObject) element).getName();
						TreeParent parent = ((TreeObject) element).getParent();
						String parentName = parent.getName();
	
						// items that have more than one child
						if (element instanceof TreeParent) {
							isExpand = treeViewer
									.getExpandedState((TreeParent) element);
							if (parent.getParent() != null && !isExpand) {
								if (parent.getParent().getName().equals("")
										&& parentName.equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_FinishedJob)) {
									elementName = elementName.substring(0,elementName.indexOf("("));
									addJobDoneChild((TreeParent) element,elementName);
									treeViewer.expandToLevel(element, 1);
								} 
							} else if(elementName.equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_CurrentJob) ||
									  elementName.equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_FinishedJob)) {
								treeViewer.expandToLevel(element, 1);
							}
							if (isExpand) {
								// when double click expanded tree cell
								treeViewer.collapseToLevel((TreeParent) element, 1);
							}
						} else if ( element instanceof TreeObject &&
						            parent.getParent().getName().equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_FinishedJob)) {
								
							txId = elementName.substring(2, elementName.indexOf("["));
							
							// open history show
							okPressed(getHistoryView());
						}
					}
				}
			});

			// TODO 도움말 완성 후 다시 체크할 것
			
			serverCombo.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
						shellUrl = util.getSHELL_URL();
						refresh();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
						shellUrl = util.getSHELL_URL();
						refresh();
				}
			});
			
			PlatformUI.getWorkbench().getHelpSystem().setHelp(
					treeViewer.getControl(),
					OdenActivator.HELP_PLUGIN_ID + ".oden.odenexplorerview");

			parent.layout();

		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while create part control.",
					odenException);
			odenException.printStackTrace();
		}
	}
	
	private DeploymentHistoryView getHistoryView() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		DeploymentHistoryView view = null;
		if(page != null) {
			view = (DeploymentHistoryView) page.findView(DeploymentHistoryView.class.getName());
			if(view == null) {
				try {
					view = (DeploymentHistoryView)page.showView(DeploymentHistoryView.class.getName());
				} catch (PartInitException partInitException) {
					partInitException.printStackTrace();
				}
			} else {
				// already open history view
				page.activate(view);
			}
		}
		return view;
	}
	/**
	 * Returns the objects which are currently selected. NOTE this is package
	 * private and should remain that way. - the implementation of the
	 * ExplorerView is now hidden from the rest of the application (see the
	 * getSelectedXxxx() methods below for a structured API)
	 * 
	 * @return
	 */
	public Object[] getSelected() {
		IStructuredSelection selection = (IStructuredSelection) treeViewer
				.getSelection();
		if (selection == null) {
			return null;
		}
		Object[] result = selection.toArray();
		if (result.length == 0) {
			return null;
		}
		return result;
	}

	private void addContextMenu() {
		final JobManagerViewActionGroup actionGroup = new JobManagerViewActionGroup();
		MenuManager menuManager = new MenuManager("OdenJobManagerContextMenu");
		menuManager.setRemoveAllWhenShown(true);
		Menu contextMenu = menuManager.createContextMenu(treeViewer.getTree());
		treeViewer.getTree().setMenu(contextMenu);

		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(final IMenuManager manager) {
				actionGroup.fillContextMenu(manager , shellUrl);
			}
		});
	}

	private void contributeToActionBars() {
		IActionBars actionBars = getViewSite().getActionBars();
		fillLocalToolBar(actionBars.getToolBarManager());
		fillLocalPullDown(actionBars.getMenuManager());
	}

	private void fillLocalToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(new DeployByTaskAction(shellUrl));
		toolBarManager.add(new DeployByFileReqAction());
		toolBarManager.add(new Separator());
		toolBarManager.add(new JobManagerRefreshAction());
	}

	private void fillLocalPullDown(IMenuManager menuManager) {
		menuManager.add(new DeployByTaskAction(shellUrl));
		menuManager.add(new DeployByFileReqAction());
		menuManager.add(new Separator());
		menuManager.add(new SetFileReqPathAction());
		menuManager.add(new Separator());
		menuManager.add(new JobManagerRefreshAction());
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {

	}

	private void addJobDoneChild(final TreeParent parent, String elementName) {
		ArrayList<String> result = jobcontentprovider.addJobDoneChild(elementName);

		if(result.size() > 0) {
			for(int i = result.size() - 1 ; i >= 0; i-=1) {
				parent.addChild(new TreeObject(result.get(i)));
			}
		}
	}
	
	public void addTaskListChild(ArrayList<String> tasklist , TreeParent parent) {

		for(String elementName : tasklist)
			if(!dupExistAddTask(parent , elementName))
				parent.addChild(new TreeObject(elementName));

		
		treeViewer.expandToLevel(parent, 1);
		treeViewer.refresh(parent);
	}

	private boolean dupExistAddTask(TreeParent parent , String inputvalue) {
		for(TreeObject treeobject : parent.getChildren())
			if(treeobject.getName().equals(inputvalue))
				return true;
		return false;
	}
	
	public String getShellUrl() {
		return shellUrl;
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}
	
	protected void okPressed(final DeploymentHistoryView historyview) {
		if(historyview != null) {
			historyview.getTree().removeAll();
			historyview.refreshAgentCombo();
			historyview.getAgentNameCombo().setText(serverCombo.getText());
			isFail = historyview.getOnlyFail().getSelection();
			
			try {
				historyview.getUtil().setSHELL_URL("http://" + OdenActivator.getDefault().getAliasManager()
					   .getServerManager().getServer(serverCombo.getText()).getUrl() + "/shell");
				
			} catch (Exception odenException) {
				// TODO: handle exception
//				OdenActivator.error("Exception occured while showing deployment history.",odenException);
			}
		}
		
		final Job creatingMarkersJob = new Job("Creating Markers...") {

			protected IStatus run(IProgressMonitor monitor) {

				monitor.beginTask("Creating Markers...", 1000);
				monitor.subTask("Getting Queries...");

				monitor.done();
				return Status.OK_STATUS;
			}
		};

		creatingMarkersJob.setSystem(false);
		creatingMarkersJob.setUser(false);

		Job gettingQueryIdsJob = new Job("Searching Queries...") {
			@SuppressWarnings("unchecked")
			protected IStatus run(IProgressMonitor monitor) {
				
				monitor.beginTask("Searching Queries...", 1000);
				monitor.subTask("Getting Queries...");
				
				final ArrayList arrayList = historyview.gettingHistories(monitor, txId , true, isFail);
				
				
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					public void run() {
						
						historyview.setHistoryList(arrayList);
						historyview.setTreeData();

						historyview.getHistorySearchView().setText(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Found
								+ historyview.getTree().getItemCount() + " "
								+ "transactions" + " "
								+ historyview.getCount() + " "
								+ UIMessages.ODEN_HISTORY_DeploymentHistoryView_Items);

					}	
				});
				monitor.done();
				return Status.OK_STATUS;
			}
		};

		gettingQueryIdsJob.setSystem(false);
		gettingQueryIdsJob.setUser(true);

		gettingQueryIdsJob.schedule();
	}
	
	protected void openView() {
		
		IWorkbenchPage Page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			
			OdenActivator.getDefault().setJobManagerView((JobManagerView) Page.showView(OdenActivator.PLUGIN_ID + ".history.DeploymentHistoryView"));
			Page.activate(Page.showView(OdenActivator.PLUGIN_ID + ".history.DeploymentHistoryView"));
		} catch (Exception odenException) {
			OdenActivator.error(
					"Exception occured while showing deployment history.",
					odenException);
		}
	}
	
	/**
	 * Refreshes the tree
	 */
	public final void refresh() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (! treeViewer.getTree().isDisposed()) {
					jobcontentprovider = new JobManagerViewContentProvider();
					jobcontentprovider.setShellURL(shellUrl);
					treeViewer.setContentProvider(jobcontentprovider);
				}
				treeViewer.expandToLevel(2);
			}
		});
	}
	/*
	 * server combo refresh
	 */
	public void refreshServerCombo() {
		String selectedText = "";
		
		if(serverCombo.getText() != null && ! serverCombo.getText().equals(""))
			selectedText = serverCombo.getText();
		else {
			serverCombo.select(0);
			selectedText = serverCombo.getText();
		}
		serverCombo.removeAll();

		util.initServerCombo(serverCombo);
		
		String [] items = serverCombo.getItems();
		
		for(int i= 0 ; i < items.length ; i++) {
			if(selectedText.equals(serverCombo.getItem(i))) {
				serverCombo.select(i);
				util.setSHELL_URL(serverCombo);
				shellUrl = util.getSHELL_URL();
				return;
			}
		}
		shellUrl = util.getSHELL_URL();
	}
	
	public ArrayList<String> getFinishedList() {
		return finishedList;
	}

	public void setFinishedList(ArrayList<String> finishedList) {
		this.finishedList = finishedList;
	}

	public Combo getServerCombo() {
		return serverCombo;
	}
}