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
package anyframe.oden.eclipse.core.history;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenMessages;
import anyframe.oden.eclipse.core.brokers.OdenBroker;
import anyframe.oden.eclipse.core.history.actions.AdvancedSearchAction;
import anyframe.oden.eclipse.core.history.actions.HistoryRefreshAction;

/**
 * This class implements Anyframe Oden Deployment History view.
 * 
 * @author HONG Junghwan
 * @version 1.0.0
 * @since 1.0.0 M3
 * 
 */
public class DeploymentHistoryView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "anyframe.oden.eclipse.core.history.DeploymentHistoryView"; 

	// private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	// TODO - Add private
	ArrayList historyList = null;

	private Composite rootComposite;
	private Composite tableComposite;
	private Composite buttonComposite;
	private boolean constructTable = true;
	private Text searchText;

	// private Button caseSensitive;
	Button caseSensitive;

	private Label historySearchView;
	private TreeViewer viewer;

	private boolean canStartMarking = true;
	private Action advHistorySearchAction;
	private Action historyRefreshAction;

	private String findText;
	Table table;
	TableViewer tableViewer;
	TableViewerColumn column1, column2, column3, column4, column5, column6;

	Listener sortListener;
	// private ListenerList selectionChangedListeners;
	private StyledText displayQueryTextArea;
	private String SHELL_URL;
	// Agent comobo
	private Label comboLabel;
	private static Combo agentNameCombo;

	DeploymentHistoryViewDetails empDetails = new DeploymentHistoryViewDetails();

	private static final String MSG_HISTORY_SHOW = OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Msg_History_Show
	+ " "; 
	private static final String HISTORY_JSON_OPT = OdenMessages.ODEN_HISTORY_DeploymentHistoryView_History_Json_Opt;

	private static final String OPT_NAME = OdenMessages.ODEN_HISTORY_DeploymentHistoryView_History_Opt_Name;
	private static final String OPT_HOST = OdenMessages.ODEN_HISTORY_DeploymentHistoryView_History_Opt_Host;
	private static final String OPT_AGENT = OdenMessages.ODEN_HISTORY_DeploymentHistoryView_History_Opt_Agent;
	private static final String OPT_PATH = OdenMessages.ODEN_HISTORY_DeploymentHistoryView_History_Opt_Path;
	private static final String OPT_DATE = OdenMessages.ODEN_HISTORY_DeploymentHistoryView_History_Opt_Date;
	private static final String OPT_STATUS = OdenMessages.ODEN_HISTORY_DeploymentHistoryView_History_Opt_Status;

	ViewerFilter filter = new ViewerFilter() {
		public boolean select(Viewer viewer1, Object parentElement,
				Object element) {

			String text = searchText.getText();

			String searchString = ""; 

			int length = text.length();
			if (((DeploymentHistoryViewDetails) element).getDeployItem().length() > length)
				searchString = ((DeploymentHistoryViewDetails) element).getDeployItem().substring(
						0, length);
			String wildCard = DeploymentHistoryView.this.searchText.getText();
			String UpperWildCard = wildCard.toUpperCase();
			String queString = ((DeploymentHistoryViewDetails) element).getDeployItem()
			+ ((DeploymentHistoryViewDetails) element).getDeployPath();

			if (!caseSensitive.getSelection()) {

				queString = queString.toUpperCase();

				return (length == 0 || (queString.contains(UpperWildCard)) || (text
						.equalsIgnoreCase(searchString)));

			} else {

				wildCard = DeploymentHistoryView.this.searchText.getText();

				return (length == 0 || (queString.contains(wildCard)) || (text
						.equals(searchString)));
			}
		}
	};

	// The below inner class is moved to "DeploymentHistoryViewContentProvider"
	//	/*
	//	 * The content provider class is responsible for providing objects to the
	//	 * view. It can wrap existing objects in adapters or simply return objects
	//	 * as-is. These objects may be sensitive to the current input of the view,
	//	 * or ignore it and always show the same content (like Task List, for
	//	 * example).
	//	 */
	//
	//	class ViewContentProvider implements IStructuredContentProvider {
	//		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	//		}
	//
	//		public void dispose() {
	//		}
	//
	//		public Object[] getElements(Object parent) {
	//			return new String[] { "One", "Two", "Three" };
	//		}
	//	}

	// The below inner class is moved to "DeploymentHistoryViewLabelProvider"
	//	class ViewLabelProvider extends LabelProvider implements
	//			ITableLabelProvider {
	//		public String getColumnText(Object obj, int index) {
	//			return getText(obj);
	//		}
	//
	//		public Image getColumnImage(Object obj, int index) {
	//			return getImage(obj);
	//		}
	//
	//		public Image getImage(Object obj) {
	//			return PlatformUI.getWorkbench().getSharedImages().getImage(
	//					ISharedImages.IMG_OBJ_ELEMENT);
	//		}
	//	}

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	// below constructor is moved to "AdvancedSearchAction"
	//	final AdvancedSearchDialog advSearchDialog = new AdvancedSearchDialog(
	//			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
	//			this);

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

			if (constructTable) {

				Composite buttonComposite = new Composite(parent, SWT.NONE);
				GridLayout layout = new GridLayout();
				layout.numColumns = 6;
				buttonComposite.setLayout(layout);
				GridData buttonCompData = new GridData(GridData.FILL_BOTH);
				buttonComposite.setLayoutData(buttonCompData);
				GridData gridData = new GridData();
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
							// add search button click
							searchPressed();
						}

					}

				});

				caseSensitive = new Button(buttonComposite, SWT.CHECK);

				caseSensitive
				.setText(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_LabelCaseSensitive);

				caseSensitive.setSelection(false);

				Label label = new Label(buttonComposite, SWT.NONE);
				label.setText(" "); 
				label.setText(" "); 

				comboLabel = new Label(buttonComposite, SWT.NONE);
				comboLabel.setText(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_AgentSelect
						+ " "); 
				label.setText(" "); 

				GridData gridDataSub = new GridData();
				gridDataSub.widthHint = 120;

				agentNameCombo = new Combo(buttonComposite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
				agentNameCombo.setLayoutData(gridDataSub);
				// Combo Event
				OdenBroker.AgentcomboEvent(agentNameCombo);
				historySearchView = new Label(buttonComposite, 0);
				GridData labelLayoutData = new GridData();
				labelLayoutData.horizontalSpan = 6;
				historySearchView.setLayoutData(labelLayoutData);
				historySearchView
				.setText(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_LabelSearchView);

				GridData data = new GridData(GridData.FILL_BOTH);
				data.horizontalSpan = 6;
				table = new Table(buttonComposite, SWT.SINGLE
						| SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL);
				tableViewer = new TableViewer(table);
				table.setHeaderVisible(true);
				table.setLinesVisible(true);
				table.setLayoutData(data);

				table.addListener(SWT.MeasureItem, new Listener() {
					public void handleEvent(Event event) {
						TableItem item = (TableItem) event.item;
						String text = item.getText(event.index);
						Point size = event.gc.textExtent(text);
						event.width = size.x;
						event.height = Math.max(25, 25);
					}
				});

				ColumnViewerToolTipSupport.enableFor(tableViewer,
						ToolTip.NO_RECREATE);

				column1 = new TableViewerColumn(tableViewer, SWT.None);
				column1.getColumn().setText(
						OdenMessages.ODEN_HISTORY_DeploymentHistoryView_LabelGridCol1);
				column1.getColumn().setWidth(100);
				column1.setLabelProvider(new DeployItemColumnLabelProvider());

				column2 = new TableViewerColumn(tableViewer, SWT.None);
				column2.getColumn().setText(
						OdenMessages.ODEN_HISTORY_DeploymentHistoryView_LabelGridCol2);
				column2.getColumn().setWidth(200);
				column2.setLabelProvider(new DeployServerColumnLabelProvider());

				column3 = new TableViewerColumn(tableViewer, SWT.None);
				column3.getColumn().setText(
						OdenMessages.ODEN_HISTORY_DeploymentHistoryView_LabelGridCol3);
				column3.getColumn().setWidth(300);
				column3.setLabelProvider(new DeployPathColumnLabelProvider());

				column4 = new TableViewerColumn(tableViewer, SWT.NONE);
				column4.getColumn().setText(
						OdenMessages.ODEN_HISTORY_DeploymentHistoryView_LabelGridCol4);
				column4.getColumn().setWidth(120);
				column4.setLabelProvider(new DeployDateColumnLabelProvider());

				column5 = new TableViewerColumn(tableViewer, SWT.NONE);
				column5.getColumn().setText(
						OdenMessages.ODEN_HISTORY_DeploymentHistoryView_LabelGridCol5);
				column5.getColumn().setWidth(100);
				column5.setLabelProvider(new DeployerIpColumnLabelProvider());

				column6 = new TableViewerColumn(tableViewer, SWT.NONE);
				column6.getColumn().setText(
						OdenMessages.ODEN_HISTORY_DeploymentHistoryView_LabelGridCol6);
				column6.getColumn().setWidth(200);
				column6.setLabelProvider(new DeployStatusColumnLabelProvider());

				tableViewer.setContentProvider(new DeploymentHistoryViewContentProvider());

			} else {
				Composite tableComposite = new Composite(parent, SWT.NONE);
				GridLayout layout = new GridLayout();

				tableComposite.setLayout(layout);
				GridData tableData = new GridData(GridData.FILL_BOTH);
				tableData.horizontalSpan = 1;
				tableComposite.setLayoutData(tableData);

				SashForm sashForm = new SashForm(tableComposite, SWT.HORIZONTAL);
				sashForm.setLayout(layout);
				sashForm.setLayoutData(tableData);

				GridData viewerData = new GridData();
				viewerData.horizontalSpan = 1;

				viewer = new TreeViewer(sashForm, SWT.MULTI | SWT.H_SCROLL
						| SWT.V_SCROLL | SWT.BORDER);

				viewer.setContentProvider(new DeploymentHistoryViewContentProvider());
				viewer.setLabelProvider(new DeploymentHistoryViewLabelProvider());
				viewer.setSorter(new ViewerSorter());
				viewer.getTree().setLayoutData(viewerData);

				displayQueryTextArea = new StyledText(sashForm, SWT.BORDER
						| SWT.V_SCROLL | SWT.H_SCROLL);
				GridData displayTextData = new GridData();
				displayTextData.horizontalSpan = 2;
				displayQueryTextArea.setLayoutData(displayTextData);
				displayQueryTextArea.setText(""); 

			}

			makeActions();
			contributeToActionBars();
			parent.layout(true);
			// Initial Combo
			OdenBroker.InitAgentCombo(agentNameCombo);
		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while create part control.", odenException);
			odenException.printStackTrace();
		}
	}

	private void searchPressed() {
		canStartMarking = true;
		if (!caseSensitive.getSelection())
			findText = searchText.getText();
		else
			findText = searchText.getText().toLowerCase();
		DeploymentHistoryView.this.tableViewer
		.addFilter(DeploymentHistoryView.this.filter);
		tableViewer.refresh();
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

			String text = searchText.getText();

			protected IStatus run(IProgressMonitor monitor) {
				// TODO: Progress Bar

				monitor.beginTask("Searching the histories", 1000); 

				monitor.subTask("Getting the Histories"); 

				historyList = gettingHistories(monitor, findText);

				// TODO : Add Result History View
				if (monitor.isCanceled()) {
					monitor.done();
					canStartMarking = false;
				}
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					public void run() {
						tableViewer.setInput(historyList);

						historySearchView.setText(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Found
								+ tableViewer.getTable().getItemCount()
								+ OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Items);

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

	private void fillContextMenu(IMenuManager manager) {
		manager.add(advHistorySearchAction);
		manager.add(historyRefreshAction);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void makeActions() {
		// below action is moved to "AdvancedSearchAction"
		//		advHistorySearchAction = new Action() {
		//			public void run() {
		//				advSearchDialog.open();
		//			}
		//		};
		//		advHistorySearchAction.setToolTipText("Advanced Search");
		//		advHistorySearchAction.setId("DEPLOY_SEARCH_ID");
		//		advHistorySearchAction.setImageDescriptor(ImageDescriptor
		//				.createFromFile(DeploymentHistoryView.class,
		//						OdenMessages.VIEWS_ODEN_HistoryView_AdvanceSearchImage));

		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
				.getFirstElement();
				showMessage("Double-click detected on " + obj.toString()); 
			}
		};
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Message_Title, message);
	}


	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {

	}

	/**
	 * Collects all the log history from Server Log History
	 * 
	 * @return ArrayList List of all the Query Ids as Location Objects.
	 */
	public ArrayList gettingHistories(IProgressMonitor monitor, String text) {
		ArrayList returnList = new ArrayList();
		String result = ""; 
		DeploymentHistoryViewDetails details = null;
		String commnd = ""; 
		String seperator = ""; 

		try {
			// seacrh method
			if (!(text.equals(""))) 
				commnd = MSG_HISTORY_SHOW + OPT_PATH + " " + text 
				+ " " + HISTORY_JSON_OPT;
			else
				commnd = MSG_HISTORY_SHOW + HISTORY_JSON_OPT;
			result = OdenBroker.sendRequest(OdenBroker.SHELL_URL, commnd);
			if( result != null) {
				JSONArray array = new JSONArray(result);
				for (int i = 0; i < array.length(); i++) {
					if(!(array.getString(i).equals("{}"))){
						String DeployId = ""; 
						JSONArray paths = ((JSONObject) array.get(i)).getJSONArray(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Path);

						for(int j=0; j< paths.length() ; j++){
							String DeployItem = paths.get(j).toString();

							String DeployPath = (String) ((JSONObject) array.get(i))
							.get(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Root);
							String DeployDate = (String) ((JSONObject) array.get(i))
							.get(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Date);
							String DeployerIp = (String) ((JSONObject) array.get(i))
							.get(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Host);
							String DeployStatus = OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Success;
							String DeployerServer = (String) ((JSONObject) array.get(i))
							.get(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Agent);
							details = new DeploymentHistoryViewDetails(DeployId,
									DeployItem, DeployPath, DeployDate, DeployerIp,
									DeployStatus, "100", DeployerServer); 
							returnList.add(details);
						}
					}
				}
			} else {
				// no connection
				OdenActivator.warning(OdenMessages.ODEN_CommonMessages_UnableToConnectServer);
			}
		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while getting history.", odenException);
			odenException.printStackTrace();
		}
		return returnList;
	}

	public ArrayList gettingHistories(IProgressMonitor monitor,
			ArrayList<String[]> inputList) {
		ArrayList returnList = new ArrayList();
		String result = ""; 
		DeploymentHistoryViewDetails details = null;
		String commnd = ""; 
		String seperator = ""; 
		int n = 0;
		try {
			if (!(inputList.size() == 0))
				for (String[] tmp : inputList) {
					n = n + 1;
					if (n == 1) {
						if (tmp[0].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Name)) {
							commnd = MSG_HISTORY_SHOW + OPT_NAME + " " + tmp[2]; 
						} else if (tmp[0].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_IP)) {
							commnd = MSG_HISTORY_SHOW + OPT_HOST + " " + tmp[2]; 
						} else if (tmp[0].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_DeployDate)) {
							if (tmp[1].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_Is)) {
								commnd = MSG_HISTORY_SHOW + OPT_DATE + " " 
								+ tmp[2] + " " + tmp[2]; 
							} else if (tmp[1].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_After)) {
								commnd = MSG_HISTORY_SHOW + OPT_DATE + " " + tmp[2];
							} else if (tmp[1].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_Before)) {
								commnd = MSG_HISTORY_SHOW + OPT_DATE + " " 
								+ "11111111" + " " + tmp[2];  
							}

						} else if (tmp[0].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Conditon_DeployStatus)) {
							if (tmp[1].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Condtion_Success)) {
								commnd = MSG_HISTORY_SHOW + OPT_STATUS + " " 
								+ OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_S;
							} else if (tmp[1].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_Failure)) {
								commnd = MSG_HISTORY_SHOW + OPT_STATUS + " " 
								+ OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_F;
							} else if (tmp[1].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_NA)) {
								commnd = MSG_HISTORY_SHOW + OPT_STATUS + " " 
								+ OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_N;
							}
						}
					} else {
						if (tmp[0].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Name)) { 
							commnd = commnd + " " + OPT_NAME + " " + tmp[2]; 
						} else if (tmp[0].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_IP)) { 
							commnd = commnd + " " + OPT_AGENT + " " + tmp[2];
						} else if (tmp[0].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_DeployDate)) { 
							if (tmp[1].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_Is)) { 
								commnd = commnd + " " + OPT_DATE + " " + tmp[2]  
								                                             + " " + tmp[2]; 
							} else if (tmp[1].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_After)) { 
								commnd = commnd + " " + OPT_DATE + " " + tmp[2];  
							} else if (tmp[1].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_Before)) { 
								commnd = commnd + " -date 11111111 " + tmp[2]; 
							}
						} else if (tmp[0].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Conditon_DeployStatus)) { 
							if (tmp[1].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Condtion_Success)) { 
								commnd = commnd + " " + OPT_STATUS + " " + OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_S; 
							} else if (tmp[1].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_Failure)) { 
								commnd = commnd + " " + OPT_STATUS + " " + OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_F;   
							} else if (tmp[1].equals(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_NA)) { 
								commnd = commnd + " " + OPT_STATUS + " " + OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Condition_N;   
							}
						}
					}
				}
			commnd = commnd + " " + HISTORY_JSON_OPT;

			result = OdenBroker.sendRequest(OdenBroker.SHELL_URL, commnd);

			if (result != null) {
				JSONArray array = new JSONArray(result);

				for (int i = 0; i < array.length(); i++) {

					String DeployId = ""; 

					JSONArray paths = ((JSONObject) array.get(i)).getJSONArray(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Path);

					for(int j=0; j< paths.length() ; j++){
						String DeployItem = paths.get(j).toString();

						String DeployPath = (String) ((JSONObject) array.get(i))
						.get(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Root);
						String DeployDate = (String) ((JSONObject) array.get(i))
						.get(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Date);
						String DeployerIp = (String) ((JSONObject) array.get(i))
						.get(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Host);
						String DeployStatus = OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Success;
						String DeployerServer = (String) ((JSONObject) array.get(i))
						.get(OdenMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Agent);
						details = new DeploymentHistoryViewDetails(DeployId,
								DeployItem, DeployPath, DeployDate, DeployerIp,
								DeployStatus, "100", DeployerServer); 
						returnList.add(details);
					}
				}

			} else {
				// no connection
				OdenActivator.warning(OdenMessages.ODEN_CommonMessages_UnableToConnectServer);
			}
		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while getting history.", odenException);
			odenException.printStackTrace();
		}
		return returnList;
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}

	public Label getHistorySearchView() {
		return historySearchView;
	}

	public class DeployItemColumnLabelProvider extends ColumnLabelProvider {
		public String getText(Object element) {
			return ((DeploymentHistoryViewDetails) element).getDeployItem();
		}
	}

	public class DeployServerColumnLabelProvider extends ColumnLabelProvider {
		public String getText(Object element) {
			return ((DeploymentHistoryViewDetails) element).getDeployServer();
		}
	}

	public class DeployPathColumnLabelProvider extends ColumnLabelProvider {
		public String getText(Object element) {
			return ((DeploymentHistoryViewDetails) element).getDeployPath();
		}
	}

	public class DeployDateColumnLabelProvider extends ColumnLabelProvider {
		public String getText(Object element) {
			return ((DeploymentHistoryViewDetails) element).getDeployDate();
		}
	}

	public class DeployerIpColumnLabelProvider extends ColumnLabelProvider {
		public String getText(Object element) {
			return ((DeploymentHistoryViewDetails) element).getDeployerIp();
		}
	}

	public class DeployStatusColumnLabelProvider extends ColumnLabelProvider {
		public String getText(Object element) {
			return ((DeploymentHistoryViewDetails) element).getDeployStatus();
		}
	}

	public static void refreshAgentCombo() {
		agentNameCombo.removeAll();
		OdenBroker.InitAgentCombo(agentNameCombo);
		agentNameCombo.select(0);
	}


}