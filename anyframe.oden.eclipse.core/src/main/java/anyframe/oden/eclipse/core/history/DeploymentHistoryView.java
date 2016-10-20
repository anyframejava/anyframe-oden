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
package anyframe.oden.eclipse.core.history;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.history.actions.AdvancedSearchAction;
import anyframe.oden.eclipse.core.history.actions.HistoryRefreshAction;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.CommonUtil;

/**
 * This class implements Anyframe Oden Deployment History view.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 M3
 * 
 */
public class DeploymentHistoryView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = OdenActivator.PLUGIN_ID + ".history.DeploymentHistoryView";

	ArrayList<Object> historyList;

	private Composite rootComposite;
	
	private Text searchText;

	private Label historySearchView;

	private boolean canStartMarking = true;
	
	private boolean undoCheck = false; 
	
	private String findText;
	
	private boolean isTransaction;
	
	private TreeItem  roots;
	
	private Tree tree;
	
	String [] strings;
	
	TreeColumn column1, column2, column3, column4, column5, column6, column7, column8 , column9 , column10 , column11 , column12;
	
	Listener sortListener;


	private Label comboLabel;
	
	private Combo agentNameCombo;
	
	private Button transactionIdSearch;
	
	private Button itemNameSearch;
	
	private Button onlyFail;
	
	private HashMap<String,String> countMap;
	
	DeploymentHistoryViewDetails empDetails = new DeploymentHistoryViewDetails();

	private static final String MSG_HISTORY_SHOW = CommandMessages.ODEN_CLI_COMMAND_history_show
	+ " "; 
	private static final String HISTORY_JSON_OPT = CommandMessages.ODEN_CLI_OPTION_json;

	private final String OPT_NAME = CommandMessages.ODEN_CLI_OPTION_path;
	private final String OPT_HOST = CommandMessages.ODEN_CLI_OPTION_userip;
	private final String OPT_PATH = CommandMessages.ODEN_CLI_OPTION_path;
	private final String OPT_DATE = CommandMessages.ODEN_CLI_OPTION_date;
	private final String OPT_STATUS = CommandMessages.ODEN_CLI_OPTION_status;
	private final static String FILE_NAME_DATE_PATTERN = "yyyy.MM.dd aa hh:mm:ss";
	
	protected OdenBrokerService broker = new OdenBrokerImpl();
	
	private String itemCount;

	CommonUtil util = new CommonUtil();

	public CommonUtil getUtil() {
		return util;
	}
	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public DeploymentHistoryView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		try {
			if (rootComposite == null) {
				rootComposite = parent;
				rootComposite.setLayout(new GridLayout());
			}
			
			Composite buttonComposite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 7;
			buttonComposite.setLayout(layout);
			GridData buttonCompData = new GridData();
			buttonComposite.setLayoutData(buttonCompData);
			
			GridData gridData = new GridData(GridData.FILL_BOTH);
			gridData.heightHint = 15;
			gridData.widthHint = 150;
			
			searchText = new Text(buttonComposite, SWT.LEFT | SWT.BORDER);
			searchText.setText(""); 
			searchText.setLayoutData(gridData);

			searchText.addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent event) {

				}

				public void keyReleased(KeyEvent event) {
					if (event.character == SWT.CR) {
						tree.removeAll();
						searchPressed();
					}
				}
			});
			
			Label label = new Label(buttonComposite, SWT.NONE);
			label.setText(" "); 
			label.setText(" "); 
			
			transactionIdSearch = new Button(buttonComposite , SWT.RADIO);
			transactionIdSearch.setText(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Label_TxIdLabel);
			transactionIdSearch.setSelection(true);
			
			itemNameSearch = new Button(buttonComposite , SWT.RADIO);
			itemNameSearch.setText(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Label_ItemNameLabel);
			
			onlyFail = new Button(buttonComposite , SWT.CHECK);
			onlyFail.setText(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Label_OnlyFailLabel);
			onlyFail.setSelection(false);
			
			comboLabel = new Label(buttonComposite, SWT.NONE);
			comboLabel.setText(UIMessages.ODEN_HISTORY_DeploymentHistoryView_ServerSelect
					+ " "); 
			
			label.setText(""); 

			GridData gridDataSub = new GridData();
			gridDataSub.widthHint = 120;

			agentNameCombo = new Combo(buttonComposite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
			agentNameCombo.setLayoutData(gridDataSub);
			// Combo Event
			util.serverComboEvent(agentNameCombo);
			
			historySearchView = new Label(buttonComposite, 0);
			GridData labelLayoutData = new GridData(GridData.FILL_BOTH);
			labelLayoutData.horizontalSpan = 6;
			
			historySearchView.setLayoutData(labelLayoutData);
			historySearchView
			.setText(UIMessages.ODEN_HISTORY_DeploymentHistoryView_LabelSearchView);

			tree = new Tree(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER
					| SWT.H_SCROLL | SWT.V_SCROLL);
			
			GridData data = new GridData(GridData.FILL_BOTH);
			data.horizontalSpan = 6;
			
			tree.setHeaderVisible(true);
			tree.setLinesVisible(true);
			tree.setLayoutData(data);
			tree.setToolTipText("");
		
			// rcp packing -> column width 변경(1024*768에 맞춤), status 보이도록
			// 110(1)/130(2)/80(3)/40(4)/80(5)/300(6)/130(7)/80(8)/100(9)
			
			column1 = new TreeColumn(tree , SWT.NONE);
			column1.setText(UIMessages.ODEN_HISTORY_DeploymentHistoryView_LabelGridCol1);
			column1.setWidth(110);
		
			column2 = new TreeColumn(tree, SWT.None);
			column2.setText(UIMessages.ODEN_HISTORY_DeploymentHistoryView_LabelGridCol2);
			column2.setWidth(150);
			
			column3 = new TreeColumn(tree, SWT.None);
			column3.setText(UIMessages.ODEN_HISTORY_DeploymentHistoryView_LabelGridCol3);
			column3.setWidth(80);
			
			column4 = new TreeColumn(tree , SWT.None);
			column4.setText(UIMessages.ODEN_HISTORY_DeploymentHistoryView_LabelGridCol4);
			column4.setWidth(50);

			column5 = new TreeColumn(tree , SWT.None);
			column5.setText(UIMessages.ODEN_HISTORY_DeploymentHistoryView_LabelGridCol5);
			column5.setWidth(80);

			column6 = new TreeColumn(tree, SWT.None);
			column6.setText(UIMessages.ODEN_HISTORY_DeploymentHistoryView_LabelGridCol6);
			column6.setWidth(400);

			column7 = new TreeColumn(tree, SWT.NONE);
			column7.setText(UIMessages.ODEN_HISTORY_DeploymentHistoryView_LabelGridCol7);
			column7.setWidth(130);

			column8 = new TreeColumn(tree, SWT.NONE);
			column8.setText(UIMessages.ODEN_HISTORY_DeploymentHistoryView_LabelGridCol8);
			column8.setWidth(80);

			column9 = new TreeColumn(tree, SWT.NONE);
			column9.setText(UIMessages.ODEN_HISTORY_DeploymentHistoryView_LabelGridCol9);
			column9.setWidth(100);
			
			// the information of undo operation
			column10 = new TreeColumn(tree, SWT.NONE);
			column10.setText("");
			column10.setWidth(0);
			column10.setResizable(false);
			
			column11 = new TreeColumn(tree, SWT.NONE);
			column11.setText("");
			column11.setWidth(0);
			column11.setResizable(false);
			
			// the information of error log
			column12 = new TreeColumn(tree, SWT.NONE);
			column12.setText("");
			column12.setWidth(0);
			column12.setResizable(false);
			
			contributeToActionBars();
			parent.layout(true);
			// Initial Combo
			util.initServerCombo(agentNameCombo);
			// add context menu
			addContextMenu();
			// Tooltip Event
			tooltipEvent();
			
		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while create part control.", odenException);
			odenException.printStackTrace();
		}
	}
	
	/**
	 * Returns the tree items which are currently selected. NOTE this is package
	 * private and should remain that way. - the implementation of the
	 * DeploymentHistoryView is now hidden from the rest of the application (see the
	 * getSelectedXxxx() methods below for a structured API)
	 * @return
	 */
	final TreeItem[] getSelected() {
		
		TreeItem[] selection = (TreeItem[]) tree.getSelection();
		
		if (selection == null) {
			return null;
		}
		if (selection.length == 0) {
			return null;
		}
		return selection;
	}
	/*
	 * To select Root Tree
	 */
	public void rootSelected() {
		tree.deselectAll();
		TreeItem item = tree.getItem(0); 
		tree.setSelection(item);
		
		tree.select(item);
	}
	private void addContextMenu() {
		final DeploymentHistoryViewActionGroup actionGroup = new DeploymentHistoryViewActionGroup();
		MenuManager menuManager = new MenuManager("OdenHistoryContextMenu"); 
		menuManager.setRemoveAllWhenShown(true);
		Menu contextMenu = menuManager.createContextMenu(tree);
		tree.setMenu(contextMenu);

		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(final IMenuManager manager) {
				actionGroup.fillContextMenu(manager);
			}
		});
	}

	private void searchPressed() {
		canStartMarking = true;
		findText = searchText.getText();
		isTransaction = transactionIdSearch.getSelection() ? true : false ;  
		final boolean isFail = onlyFail.getSelection();
		final Job creatingMarkersJob = new Job("Creating Markers") { 

			protected IStatus run(IProgressMonitor monitor) {

				monitor.beginTask("Creating Markers", 1000); 
				monitor.subTask("Getting the Items"); 

				if (monitor.isCanceled()) {
					monitor.done();
					return Status.CANCEL_STATUS;
				}
				monitor.done();
				return Status.OK_STATUS;
			}

		};

		creatingMarkersJob.setSystem(false);
		creatingMarkersJob.setUser(false);

		Job gettingHistoryJob = new Job("Searching Histories") { 

			protected IStatus run(IProgressMonitor monitor) {

				monitor.beginTask("Searching the histories", 1000); 

				monitor.subTask("Getting the Histories"); 

				historyList = gettingHistories(monitor, findText , isTransaction , isFail);

				if (monitor.isCanceled()) {
					monitor.done();
					canStartMarking = false;
				}
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					public void run() {
						setTreeData();
						
						historySearchView.setText(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Found
								+ tree.getItemCount() + " "
								+ UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Transaction + " "
								+ itemCount + " "
								+ UIMessages.ODEN_HISTORY_DeploymentHistoryView_Items);

					}

				});

				monitor.done();
				return Status.OK_STATUS;
			}

		};

		gettingHistoryJob.addJobChangeListener(new JobChangeAdapter() {
			public void done(IJobChangeEvent arg0) {
				if (canStartMarking && historyList.size() != 0) {
					creatingMarkersJob.schedule();
				}

			}

		});
		gettingHistoryJob.setSystem(false);
		gettingHistoryJob.setUser(true);

		gettingHistoryJob.schedule();

	}
	/*
	 * setting TableTree Data
	 */
	public void setTreeData() {
		countMap = new HashMap<String,String>();
		for(Object obj : historyList) {
			if(obj instanceof DeploymentHistoryViewRoots) {
				setTreeRoot((DeploymentHistoryViewRoots) obj);
				countMap.put(((DeploymentHistoryViewRoots) obj).getDeployId(), ((DeploymentHistoryViewRoots) obj).getTotalQuery());
			} else {
				setTreeChild((DeploymentHistoryViewDetails)obj);
			}
		}
		getItemCount();
	}
	
	private void getItemCount() {
		int itemCount = 0;
		for(String count : countMap.values()) {
			itemCount = itemCount + Integer.valueOf(count);
		}
		this.itemCount = String.valueOf(itemCount);	
	}
	private void setTreeRoot(DeploymentHistoryViewRoots obj) {
		roots = new TreeItem(tree, 0 );
		
		roots.setText(0, obj.getDeployId());
		roots.setText(1, obj.getDeployDesc());
		roots.setText(6, obj.getDeployDate());
		roots.setText(7, obj.getDeployerIp());
		roots.setText(8, obj.getDeployStatus());
		if(obj.getDeployStatus().equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Fail)) {
			Color color = new Color(Display.getCurrent(), 255, 0, 0);
			roots.setForeground(8, color);
		}
		roots.setText(11, obj.getDeployLog());
	}
	
	private void setTreeChild(DeploymentHistoryViewDetails obj) {
		TreeItem subTree = new TreeItem(roots , SWT.NONE);
		
		subTree.setText(1, obj.getDeployItem());
		subTree.setText(2, obj.getDeployItemSize());
		subTree.setText(3, obj.getDeployItemMode());
		subTree.setText(4, obj.getDeployServer());
		subTree.setText(5, obj.getDeployPath());
		subTree.setText(6, obj.getDeployDate());
		subTree.setText(7, obj.getDeployerIp());
		subTree.setText(8, obj.getDeployStatus());
		
		if(obj.getDeployStatus().equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Fail)) {
			Color color = new Color(Display.getCurrent(), 255, 0, 0);
			subTree.setForeground(8, color);
		}
		// undo info
		subTree.setText(9, obj.getUndoAbsolutePath());
		subTree.setText(10, obj.getUndoFilePath());
		// errorlog info
		subTree.setText(11, obj.getDeployLog());
	}
	private void contributeToActionBars() {
		IActionBars actionBars = getViewSite().getActionBars();
		fillLocalToolBar(actionBars.getToolBarManager());
		fillLocalPullDown(actionBars.getMenuManager());
	}

	private void fillLocalToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(new AdvancedSearchAction());
		toolBarManager.add(new HistoryRefreshAction());
	}

	private void fillLocalPullDown(IMenuManager menuManager) {
		menuManager.add(new AdvancedSearchAction());
		menuManager.add(new HistoryRefreshAction());
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {

	}

	/**
	 * Collects all the log history from Server Log History
	 * 
	 * @return ArrayList List of items by Search condition.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Object> gettingHistories(IProgressMonitor monitor, String text , boolean isTransaction , boolean isFail) {
		ArrayList<Object> returnList = new ArrayList<Object>();
		String result = ""; 
		DeploymentHistoryViewDetails details = null;
		String commnd = ""; 
		this.undoCheck = true;
		String errLog = "";
		String log="";
		try {
			// seacrh method
			if (!(text.equals("")) && isTransaction) 
				commnd = MSG_HISTORY_SHOW + text + " ";
			else if (!(text.equals("")) && ! isTransaction)
				commnd = MSG_HISTORY_SHOW + OPT_PATH + " " + text + " ";
			else
				commnd = MSG_HISTORY_SHOW + " ";
			
			if(isFail)
				commnd =  commnd + CommandMessages.ODEN_CLI_OPTION_status + " " + HISTORY_JSON_OPT;
			else
				commnd =  commnd + HISTORY_JSON_OPT;
			
			result = broker.sendRequest(util.getSHELL_URL(), commnd);
			
			if(!(result == null) && ! result.equals("")) {
				JSONArray array = new JSONArray(result);
				if( ! (array.length() == 0)) {
					JSONObject obj = (JSONObject) array.get(0);
					
					JSONArray detailInfo = (JSONArray) obj.get("files");
					
					// transaction Info
					String deployerIp = obj.getString("user");
					String deployId = obj.getString("txid");
					
					String date = chgDateFormat(obj.getString(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Date));
					String deployStatus = obj.getString("success").equals("true") ? UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Success
							: UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Fail;
					String desc = obj.getString("desc");
					if(deployStatus.equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Fail)) 
						log = obj.has("log") ? obj.getString("log") : "";
					
					DeploymentHistoryViewRoots root = new DeploymentHistoryViewRoots(
							deployId, date, deployerIp, deployStatus, desc, log , Integer.toString(detailInfo.length())); 
					
					returnList.add(root);
					
					String deployItem = "";
					String deployPath = ""; 
					String deployDate = "";
					String deployerServer = "";
					String deployItemMode = "";
					String deployItemSize = "";
					String deployerServerPath = "";
					String absolutePath = "";
					String undoPath = "";
					
					for(int i = 0 ; i < detailInfo.length() ; i++) {
						JSONObject full = (JSONObject) detailInfo.get(i);
						for(Iterator<String> it = full.keys() ; it.hasNext();) {
							String keys = it.next();
							if(keys.equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Agent)) {
								JSONObject agents = new JSONObject(full.getString(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Agent));
								deployerServerPath = agents.getString("loc");
								deployerServer = agents.getString("name");
								
								// undo info-> absolute-path
								absolutePath = agents.getString("loc");
							} else if(keys.equals("mode")) {
								// new Add
								deployItemMode = full.getString("mode");
								if(deployItemMode.equals("A"))
									deployItemMode = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Add;
								else if(deployItemMode.equals("U"))
									deployItemMode = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Update;
								else if(deployItemMode.equals("D")) 
									deployItemMode = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Delete;
								else
									deployItemMode = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Nothing;
							} else if(keys.equals("path")) {
								String file = full.getString("path");
								// undo info-> file-path
								undoPath = file;
								if (file.indexOf("/") > 0) {
									// unix
									deployPath = deployerServerPath + "/" + file.substring(0, file.lastIndexOf("/"));
									deployPath = file.substring(0, file.lastIndexOf("/"));
									deployItem = file.substring(file.lastIndexOf("/") + 1);
								} else if (file.lastIndexOf(File.separator) > 0 ) {
									// window
									deployPath = deployerServerPath + File.separator + file.substring(0, file.lastIndexOf(File.separator));
									deployPath = File.separator + file.substring(0, file.lastIndexOf(File.separator));
									deployItem = file.substring(file.lastIndexOf(File.separator) + 1);
								} else {
									deployPath = deployerServerPath;
									deployItem = file;
								}
							} else if(keys.equals("size")) {
								// new Add
								deployItemSize = full.getString("size") + " " + "bytes";
							} else if(keys.equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Date)) {
								deployDate = chgDateFormat(full.getString(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Date)); 
							} else if(keys.equals("success")) {
								deployStatus = full.getString("success").equals(
										"true") ? UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Success
										: UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Fail; 
							} else if(keys.equals("errorlog")) {
								errLog = full.getString("errorlog");
							}
						}
						details = new DeploymentHistoryViewDetails(deployId,deployItem, deployItemSize , deployItemMode , 
								deployPath, deployDate, deployerIp,deployStatus, "100", deployerServer); 
						details.setUndoAbsolutePath(absolutePath);
						details.setUndoFilePath(undoPath);
						details.setDeployLog(errLog);
						
						returnList.add(details);
					}
				}	
			} else {
				OdenActivator.warning(CommonMessages.ODEN_CommonMessages_UnableToConnectServer);
			}
		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while getting history.", odenException);
			odenException.printStackTrace();
		}
		return returnList;
	}
	
	private String chgDateFormat(String input) {
		return new SimpleDateFormat(FILE_NAME_DATE_PATTERN).format(Long.valueOf(input));
	}
	@SuppressWarnings("unchecked")
	public ArrayList gettingHistories(IProgressMonitor monitor,
			ArrayList<String[]> inputList) {
		ArrayList returnList = new ArrayList();
		String result = ""; 
		DeploymentHistoryViewDetails details = null;
		String commnd = ""; 
		this.undoCheck = false;
		String errLog = "";
		String log="";
		int n = 0;
		
		try {
			if (!(inputList.size() == 0))
				for (String[] tmp : inputList) {
					n = n + 1;
					if (n == 1) {
						if (tmp[0].equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Name)) {
							commnd = MSG_HISTORY_SHOW + OPT_NAME + " " + tmp[2]; 
						} else if (tmp[0].equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_IP)) {
							commnd = MSG_HISTORY_SHOW + OPT_HOST + " " + tmp[2]; 
						} else if (tmp[0].equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_DeployDate)) {
							if (tmp[1].equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_Is)) {
								commnd = MSG_HISTORY_SHOW + OPT_DATE + " " + tmp[2]; 
							} else if (tmp[1].equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_After)) {
								commnd = MSG_HISTORY_SHOW + OPT_DATE + " " + tmp[2] + " " + "99999999";
							} else if (tmp[1].equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_Before)) {
								commnd = MSG_HISTORY_SHOW + OPT_DATE + " " + "11111111" + " " + tmp[2];  
							}

						} else if (tmp[0].equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Conditon_DeployStatus)) {
							if (tmp[1].equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_Failure)) {
								commnd = MSG_HISTORY_SHOW + OPT_STATUS ;
							} 
						} else if(tmp[0].equals("Agent")) {
							// agent
							commnd = MSG_HISTORY_SHOW + "-a" + " " + tmp[2]; 
						}
					} else {
						if (tmp[0].equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Name)) { 
							commnd = commnd + " " + OPT_NAME + " " + tmp[2]; 
						} else if (tmp[0].equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_IP)) { 
							commnd = commnd + " " + OPT_HOST + " " + tmp[2];
						} else if (tmp[0].equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_DeployDate)) { 
							if (tmp[1].equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_Is)) { 
								commnd = commnd + " " + OPT_DATE + " " + tmp[2]  
								                                             + " " + tmp[2]; 
							} else if (tmp[1].equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_After)) { 
								commnd = commnd + " " + OPT_DATE + " " + tmp[2] + " " + "99999999";  
							} else if (tmp[1].equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_Before)) { 
								commnd = commnd + " " + OPT_DATE + " " + "11111111" + " " + tmp[2]; 
							}
						} else if (tmp[0].equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Conditon_DeployStatus)) { 
							if (tmp[1].equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_Failure)) { 
								commnd = commnd + " " + OPT_STATUS ;   
							}
						} else if (tmp[0].equals("Agent")) {
							commnd = commnd + " " + "-a" + " " + tmp[2];
						}
					}
				}
			commnd = commnd + " " + HISTORY_JSON_OPT;
			
			
			result = broker.sendRequest(util.getSHELL_URL(), commnd);

			if(!(result == null)) {
				JSONArray array = new JSONArray(result);
				if( ! (array.length() == 0)) {
					for(int i = 0 ; i < array.length() ; i++) {
						JSONObject obj = (JSONObject) array.get(i);
						
						JSONArray detailInfo = (JSONArray) obj.get("files");
						// transaction Info
						String deployerIp = obj.getString("user");
						String deployId = obj.getString("txid");
						
						String date = chgDateFormat(obj.getString(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Date));
						String deployStatus = obj.getString("success").equals("true") ? UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Success
								: UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Fail;
						String desc = obj.getString("desc");
						
						if(deployStatus.equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Fail)) 
							log = obj.has("log") ? obj.getString("log") : "";
						
						DeploymentHistoryViewRoots root = new DeploymentHistoryViewRoots(
								deployId, date, deployerIp, deployStatus, desc, log , Integer.toString(detailInfo.length())); 
						
						returnList.add(root);
						
						String deployItem = "";
						String deployPath = ""; 
						String deployDate = "";
						String deployerServer = "";
						String deployItemMode = "";
						String deployItemSize = "";
						String deployerServerPath = "";
						String absolutePath = "";
						String undoPath = "";
						
						for(int j = 0 ; j < detailInfo.length() ; j++) {
							JSONObject full = (JSONObject) detailInfo.get(j);
							for(Iterator<String> it = full.keys() ; it.hasNext();) {
								String keys = it.next();
								if(keys.equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Agent)) {
									JSONObject agents = new JSONObject(full.getString(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Agent));
									deployerServerPath = agents.getString("addr") + "/" + agents.getString("loc");
									deployerServer = agents.getString("name");
									// undo info-> absolute-path
									absolutePath = agents.getString("loc");
								} else if(keys.equals("mode")) {
									// new Add
									deployItemMode = full.getString("mode");
									if(deployItemMode.equals("A"))
										deployItemMode = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Add;
									else if(deployItemMode.equals("U"))
										deployItemMode = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Update;
									else if(deployItemMode.equals("D"))
										deployItemMode = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Delete;
									else
										deployItemMode = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Nothing;
								} else if(keys.equals("path")) {
									String file = full.getString("path");
									// undo info-> file-path
									undoPath = file;
									if (file.indexOf("/") > 0) {
										// unix
										deployPath = deployerServerPath + file.substring(0, file.lastIndexOf("/"));
										deployItem = file.substring(file.lastIndexOf("/") + 1);
									} else if (file.lastIndexOf(File.separator) > 0 ) {
										// window
										deployPath = deployerServerPath + file.substring(0, file.lastIndexOf(File.separator));
										deployItem = file.substring(file.lastIndexOf(File.separator) + 1);
									} 
								} else if(keys.equals("size")) {
									// new Add
									deployItemSize = full.getString("size") + " " + "bytes"; 
								} else if(keys.equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Date)) {
									deployDate = chgDateFormat(full.getString(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Date)); 
								} else if(keys.equals("success")) {
									deployStatus = full.getString("success").equals(
											"true") ? UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Success
											: UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Fail; 
								} else if(keys.equals("errorlog")) {
									errLog = full.getString("errorlog");
								}
							}
							details = new DeploymentHistoryViewDetails(deployId,deployItem, deployItemSize , deployItemMode , 
									deployPath, deployDate, deployerIp, deployStatus, "100", deployerServer); 
							details.setUndoAbsolutePath(absolutePath);
							details.setUndoFilePath(undoPath);
							details.setDeployLog(errLog);
							returnList.add(details);
						}
					}
				}
			} else {
				OdenActivator.warning(CommonMessages.ODEN_CommonMessages_UnableToConnectServer);
			}
		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while getting history.", odenException);
			odenException.printStackTrace();
		}
		return returnList;
	}

	public Label getHistorySearchView() {
		return historySearchView;
	}
	
	public void refreshAgentCombo() {
		agentNameCombo.removeAll();
		util.initServerCombo(agentNameCombo);
		agentNameCombo.select(0);
	}

	/*
	 * setHistoryList
	 */
	public void setHistoryList(ArrayList<Object> historyList) {
		this.historyList = historyList;
	}
	
	private void tooltipEvent() {
		final Listener labelListener = new Listener() {
			public void handleEvent(Event event) {
				Label label = (Label) event.widget;
				Shell shell = label.getShell();
				switch (event.type) {
				case SWT.MouseDown:
					Event e = new Event();
					e.item = (TreeItem) label.getData("_TOOLTIP");

					tree.setSelection(new TreeItem[] { (TreeItem) e.item });
					tree.notifyListeners(SWT.Selection, e);

				case SWT.MouseExit:
					shell.dispose();
					break;
				}
			}
		};

		Listener treeListener = new Listener() {
			Shell tip = null;
			Label label = null;

			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.KeyDown: {
					if (tip == null) {
						break;
					}
					break;
				}
				case SWT.MouseHover: {
					TreeItem item = tree.getItem(new Point(event.x, event.y));

					if (item != null) {
						if (tip != null && !tip.isDisposed()) {
							tip.dispose();
						}
						String status = item.getText(8);
						String logdata = item.getText(11);
						
						if (status
								.equals(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Fail)
								&& item.getBounds(8).contains(event.x, event.y)) {

							if (!(logdata.trim().equals(""))) {
								tip = new Shell(tree.getShell(), SWT.ON_TOP
										| SWT.TOOL);
								FillLayout fillLayout = new FillLayout();
								fillLayout.marginHeight = 1;
								fillLayout.marginWidth = 1;
								tip.setLayout(fillLayout);

								TextViewer label = new TextViewer(tip, SWT.NONE);
								Document document = new Document();
								document.set(logdata);
								label.setDocument(document);
								label.setEditable(false);
								
								Point size = tip.computeSize(SWT.DEFAULT,
										SWT.DEFAULT);
								Point pt = tree.toDisplay(event.x, event.y);
								tip.setBounds(pt.x, pt.y + 26, size.x, size.y);

								tip.setVisible(true);
							}
						}
					} else {
						// tooltip dispose
						if (tip != null) {
							tip.dispose();
							tip = null;
							label = null;
						}
					}
				}
				}
			}
		};
		tree.setToolTipText("");
		tree.addListener(SWT.FocusOut, treeListener);
		tree.addListener(SWT.Dispose, treeListener);
		tree.addListener(SWT.KeyDown, treeListener);
		tree.addListener(SWT.MouseMove, treeListener);
		tree.addListener(SWT.MouseHover, treeListener);

	}

	/*
	 * get Item counts
	 */
	public String getCount() {
		return this.itemCount;
	}
	/*
	 * get Tree Object
	 */
	public Tree getTree() {
		return this.tree;
	}
	/*
	 * indicate whether undo or not 
	 */
	public boolean isUndoCheck() {
		return undoCheck;
	}

	public Combo getAgentNameCombo() {
		return agentNameCombo;
	}

	public Button getOnlyFail() {
		return onlyFail;
	}
}