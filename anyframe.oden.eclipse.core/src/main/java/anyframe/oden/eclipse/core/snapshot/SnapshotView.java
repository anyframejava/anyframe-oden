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
package anyframe.oden.eclipse.core.snapshot;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.CommandNotFoundException;
import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenTrees.TreeParent;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.snapshot.actions.DeleteSnapshotAction;
import anyframe.oden.eclipse.core.snapshot.actions.DeleteSnapshotPlanAction;
import anyframe.oden.eclipse.core.snapshot.actions.NewSnapshotPlanAction;
import anyframe.oden.eclipse.core.snapshot.actions.RefreshAction;
import anyframe.oden.eclipse.core.snapshot.actions.RollbackSnapshotAction;
import anyframe.oden.eclipse.core.snapshot.actions.SaveSnapshotPlanAction;
import anyframe.oden.eclipse.core.snapshot.actions.TakeSnapshotPlanAction;
import anyframe.oden.eclipse.core.snapshot.dialogs.SnapshotNewPlanDialog;
import anyframe.oden.eclipse.core.utils.Cmd;
import anyframe.oden.eclipse.core.utils.CommonUtil;
import anyframe.oden.eclipse.core.utils.ImageUtil;
import anyframe.oden.eclipse.core.utils.VersionUtil;

/**
 * This class implements Snapshot view where users can manage snapshot plan and
 * files.
 * 
 * @author LEE Sujeong
 * @version 1.0.0 RC2
 */
public class SnapshotView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = OdenActivator.PLUGIN_ID
			+ ".snapshot.SnapshotView"; //$NON-NLS-1$

	protected static OdenBrokerService OdenBroker = new OdenBrokerImpl();

	private static Composite compositeWhole;
	private static Combo serverCombo;
	private static Composite compositeCombo;
	private HashMap<String, String> hm;
	private HashMap<String, String> locVariable;
	private Combo agentCombo;
	private Combo locationVar;
	private Text sourceLocPathText;

	protected static Composite compositeDetail;
	protected static String inputText;
	protected static PatternFilter patternFilter;
	protected static FilteredTree filter;

	public static TreeParent invisibleRoot;
	public static TreeViewer viewer;
	public static String SHELL_URL;
	public static String selectedName;
	public static String sourceLocation;
	public static String destAgent;
	public static String destination;
	public static String description;

	public String agentChosen;

	private static String[] SOURCE_OPT = { "source", "s" }; //$NON-NLS-1$ //$NON-NLS-2$
	private static String[] SIZE_OPT = { "size" }; //$NON-NLS-1$
	private static String[] USER_OPT = { "_user" }; //$NON-NLS-1$
	private static String[] DATE_OPT = { "date" }; //$NON-NLS-1$
	private static String[] DESC_OPT = { "desc" }; //$NON-NLS-1$

	class NameSorter extends ViewerSorter {
	}

	public void dispose() {
		selectedName = null;
		viewer = null;
		super.dispose();
	}

	public SnapshotView() {
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@SuppressWarnings("deprecation")
	public void createPartControl(Composite parent) {
		invisibleRoot = null;
		SHELL_URL = null;
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);

		new VersionUtil();
		String platformVer = VersionUtil.getPlatformVersion();
		String productVer = VersionUtil.getProductVersion();

		// FilteringTree
		patternFilter = new PatternFilter();

		if (platformVer.substring(0, 3).equals("3.5") //$NON-NLS-1$
				|| productVer.substring(0, 3).equals("3.5")) { //$NON-NLS-1$
			filter = new FilteredTree(composite, SWT.NONE | SWT.H_SCROLL
					| SWT.V_SCROLL | SWT.BORDER | SWT.MULTI, patternFilter,
					true);
		} else {
			filter = new FilteredTree(composite, SWT.NONE | SWT.H_SCROLL
					| SWT.V_SCROLL | SWT.BORDER | SWT.MULTI, patternFilter);
		}

		viewer = filter.getViewer();
		showSnapshotTreeView();

		// Right-Side Composite
		compositeWhole = new Composite(parent, SWT.NONE);
		compositeWhole.setLayout(layout);
		GridData data1 = new GridData(GridData.FILL_VERTICAL);
		compositeWhole.setLayoutData(data1);

		// Right-Top Composite(Server)
		GridLayout layoutLeft = new GridLayout(2, true);
		Composite compositeServer = new Composite(compositeWhole, SWT.NONE);
		compositeServer.setLayout(layoutLeft);
		GridData data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.horizontalSpan = 2;
		compositeServer.setLayoutData(data2);

		selectOdenServer(compositeServer);

		// separator for server and detail
		GridLayout layoutLine = new GridLayout();
		Composite compositeLine = new Composite(compositeWhole, SWT.NONE);
		compositeLine.setLayout(layoutLine);
		GridData dataLine = new GridData(GridData.FILL_HORIZONTAL);
		compositeLine.setLayoutData(dataLine);

		Label separator1 = new Label(compositeLine, SWT.SEPARATOR
				| SWT.HORIZONTAL);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		separator1.setLayoutData(data);

		// Right-Bottom Composite(Detail)
		compositeDetail = new Composite(compositeWhole, SWT.NONE);
		compositeDetail.setLayout(layoutLeft);
		GridData data3 = new GridData(GridData.FILL_BOTH);
		compositeDetail.setLayoutData(data3);

		addContextMenu();
		contributeToActionBars();

	}

	private void addContextMenu() {
		final SnapshotViewActionGroup actionGroup = new SnapshotViewActionGroup();
		MenuManager menuManager = new MenuManager(
				UIMessages.ODEN_SNAPSHOT_SnapshotView_ContextManager);
		menuManager.setRemoveAllWhenShown(true);
		Menu contextMenu = menuManager.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(contextMenu);

		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				actionGroup.fillContextMenu(manager);
			}
		});
	}

	private void showSnapshotTreeView() {

		getSnapshotTree();
		// setAgentInformation();

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {

				/*
				 * event.getSelection ex> [selectName]
				 */
				String selectedName = event.getSelection().toString()
						.substring(1,
								event.getSelection().toString().length() - 1);

				ArrayList<String> selected = new ArrayList<String>();
				int n = 0;
				while ((n = selectedName.indexOf(",")) != -1) { //$NON-NLS-1$
					// int n = selectedName.indexOf(",");
					selected.add(selectedName.substring(0, n));
					selectedName = selectedName.substring(n + 2);
				}

				new SnapshotViewContentProvider();
				ArrayList<String> snapshotPlanList = SnapshotViewContentProvider
						.getSnaphotPlanList(
								SHELL_URL,
								CommandMessages.ODEN_CLI_COMMAND_snapshot_planinfo
										+ " -json"); //$NON-NLS-1$

				for (int i = 0; i < snapshotPlanList.size(); i++) {
					String name = snapshotPlanList.get(i);
					if (selectedName.equalsIgnoreCase(name)) {
						displayPlan(compositeDetail, selectedName);
						break;
					}
				}

				JSONArray snapshotFileList = SnapshotViewContentProvider
						.getSnaphotFileList(
								SHELL_URL,
								CommandMessages.ODEN_CLI_COMMAND_snapshot_fileinfo
										+ " -json"); //$NON-NLS-1$

				/*
				 * when selectedName is file
				 */
				for (int i = 0; i < snapshotFileList.length(); i++) {

					try {
						JSONObject jo = snapshotFileList.getJSONObject(i);
						for (Iterator it = jo.keys(); it.hasNext();) {
							String fileName = (String) it.next();
							if (selectedName.equalsIgnoreCase(fileName)) {
								displayFile(compositeDetail, selectedName);
								break;
							}
						}

					} catch (JSONException e) {
						OdenActivator
								.error(
										UIMessages.ODEN_SNAPSHOT_SnapshotView_Exception_GetSnapshotDetailInfo,
										e);
					}
				}
				compositeWhole.layout(true);
			}
		});

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {

				Object obj = ((TreeSelection) event.getSelection())
						.getFirstElement();

				if (obj instanceof TreeParent) {
					if (viewer.getExpandedState(obj)) {
						viewer.collapseToLevel(obj, 1);
					} else {
						viewer.expandToLevel(obj, 2);
					}
				}
			}
		});

	}

	private void getSnapshotTree() {
		SnapshotViewContentProvider.invisibleRoot = invisibleRoot;
		new DrillDownAdapter(viewer);
		viewer.setContentProvider(new SnapshotViewContentProvider());
		viewer.setLabelProvider(new SnapshotViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
	}

	private void displayPlan(final Composite composite, String plan) {

		String planInfo = ""; //$NON-NLS-1$

		if (compositeDetail != null && !compositeDetail.isDisposed()) {
			compositeDetail.dispose();

			compositeDetail = new Composite(compositeWhole, SWT.NONE);
		}
		plan = plan.trim();

		String result = ""; //$NON-NLS-1$
		try {
			result = SnapshotViewContentProvider.getInfo(SHELL_URL,
					CommandMessages.ODEN_CLI_COMMAND_snapshot_planinfo
							+ " \"" + plan + "\" " + "-json"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} catch (OdenException e) {
			OdenActivator
					.error(
							UIMessages.ODEN_SNAPSHOT_SnapshotView_Exception_GetSnapshotPlanInfo,
							e);
		}

		try {
			JSONArray ja = new JSONArray(result);
			JSONObject jo = ja.getJSONObject(0);
			planInfo = jo.getString(plan);
		} catch (JSONException e) {
			OdenActivator
					.error(
							UIMessages.ODEN_SNAPSHOT_SnapshotView_Exception_ParseSnapshotPlanInfo,
							e);
		}

		Cmd cmd = new Cmd("\"" + plan + "\"" + " = " + planInfo); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		final String source = cmd.getOptionArg(SOURCE_OPT);
		String user = cmd.getOptionArg(USER_OPT);
		String date = cmd.getOptionArg(DATE_OPT);
		String[] desc = cmd.getOptionArgArray(DESC_OPT);

		String strDesc = "";//$NON-NLS-1$
		if (desc == null || desc.equals("")) { //$NON-NLS-1$
			StringTokenizer tokenizer = new StringTokenizer(date, "\n"); //$NON-NLS-1$
			date = tokenizer.nextToken();
		} else {
			if (desc.length == 0) {
			} else {
				strDesc = desc[0];
				for (int i = 1; i < desc.length; i++) {
					strDesc = strDesc + " " + desc[i]; //$NON-NLS-1$
				}
			}
			StringTokenizer tokenizer = new StringTokenizer(date, "\n"); //$NON-NLS-1$
			date = tokenizer.nextToken();
		}

		final HashMap<String, String> mapStrSrc = parsingSource(source);

		final String agentName = mapStrSrc
				.get(UIMessages.ODEN_SNAPSHOT_SnapshotView_SourceMapKeyAgent);
		String locVar = mapStrSrc
				.get(UIMessages.ODEN_SNAPSHOT_SnapshotView_SourceMapKeyLocVar);

		compositeDetail.setLayout(new GridLayout());
		GridData detailData = new GridData(GridData.FILL_HORIZONTAL);

		Group group = new Group(compositeDetail, SWT.SHADOW_ETCHED_IN);
		group.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_PlanGroupTitle);

		group.setLayout(new GridLayout(2, false));
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 1;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;

		Label label = new Label(group, SWT.SINGLE);
		label.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_LabelPlanName);

		Text planName = new Text(group, SWT.SINGLE | SWT.READ_ONLY);
		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		planName.setText(plan);
		planName.setLayoutData(gridData1);

		Label userIP = new Label(group, SWT.SINGLE);
		userIP.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_LabelUserInfo);

		final Text userInfo = new Text(group, SWT.SINGLE | SWT.READ_ONLY);
		GridData gridData5 = new GridData(GridData.FILL_HORIZONTAL);
		userInfo.setText(user);
		userInfo.setLayoutData(gridData5);

		Label enrollDate = new Label(group, SWT.SINGLE);
		enrollDate.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_LabelPlanDate);

		final Text dateInfo = new Text(group, SWT.SINGLE | SWT.READ_ONLY);
		GridData gridData6 = new GridData(GridData.FILL_HORIZONTAL);
		dateInfo.setText(date);
		dateInfo.setLayoutData(gridData6);

		Label agent = new Label(group, SWT.SINGLE);
		agent.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_LabelDestAgent);

		agentCombo = new Combo(group, SWT.SINGLE | SWT.BORDER | SWT.DROP_DOWN
				| SWT.LEFT | SWT.READ_ONLY);
		GridData gridData10 = new GridData(GridData.FILL_HORIZONTAL);
		setAgentVar();
		Object[] comboAgentItem = agentCombo.getItems();
		for (int i = 0; i < comboAgentItem.length; i++) {
			if (comboAgentItem[i].equals(agentName)) {
				agentCombo.select(i);
			}
		}
		agentCombo.setLayoutData(gridData10);

		agentCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setLocationVar();
				sourceLocPathText.setText(""); //$NON-NLS-1$
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				setLocationVar();
				sourceLocPathText.setText(""); //$NON-NLS-1$
			}
		});

		Label sourceLabel = new Label(group, SWT.SINGLE);
		sourceLabel
				.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_LabelSourceLocation);

		locationVar = new Combo(group, SWT.SINGLE | SWT.BORDER | SWT.DROP_DOWN
				| SWT.READ_ONLY);
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		setLocationVar();

		Object[] comboLocItem = locationVar.getItems();
		if (locVar.length() > 1) {
			for (int i = 0; i < comboLocItem.length; i++) {
				if (comboLocItem[i].equals(locVar.substring(1))) {
					locationVar.select(i);
				}
			}
		} else if (locVar.equals(CommandMessages.ODEN_CLI_OPTION_locvarsign)) {
			for (int i = 0; i < comboLocItem.length; i++) {
				if (comboLocItem[i]
						.equals(UIMessages.ODEN_SNAPSHOT_SnapshotView_DefaultLocComboText)) {
					locationVar.select(i);
				}
			}
		} else {
			for (int i = 0; i < comboLocItem.length; i++) {
				if (comboLocItem[i]
						.equals(UIMessages.ODEN_SNAPSHOT_SnapshotView_AbsolutePathComboText)) {
					locationVar.select(i);
				}
			}
		}
		locationVar.setLayoutData(gridData2);

		locationVar.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {

				if (!(locationVar.getText().equals(""))) { //$NON-NLS-1$
					if (locationVar
							.equals(UIMessages.ODEN_SNAPSHOT_SnapshotView_AbsolutePathComboText)) {
						sourceLocPathText.setText(""); //$NON-NLS-1$
					} else if (locationVar
							.equals(UIMessages.ODEN_SNAPSHOT_SnapshotView_DefaultLocComboText)) {
						sourceLocPathText.setText(""); //$NON-NLS-1$
					} else {
						sourceLocPathText.setText(""); //$NON-NLS-1$
					}
				} else {
					//					
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label hideLabel = new Label(group, SWT.SINGLE | SWT.RIGHT);
		hideLabel.setText(""); //$NON-NLS-1$

		sourceLocPathText = new Text(group, SWT.SINGLE | SWT.BORDER);
		GridData gridDataPath = new GridData(GridData.FILL_HORIZONTAL);
		setLocPathText(mapStrSrc);
		sourceLocPathText.setLayoutData(gridDataPath);

		Label labelDesc = new Label(group, SWT.SINGLE);
		labelDesc
				.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_LabelPlanDescription);

		final Text descText = new Text(group, SWT.SINGLE | SWT.BORDER);
		GridData gridData3 = new GridData(GridData.FILL_HORIZONTAL);
		descText.setText(strDesc);
		descText.setLayoutData(gridData3);

		group.setLayoutData(gridData);

		group.pack();

		Composite compositeButton = new Composite(compositeDetail, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		compositeButton.setLayout(layout);

		GridData buttonData = new GridData(GridData.FILL_HORIZONTAL);

		Button buttonSave = new Button(compositeButton, SWT.PUSH);
		buttonSave
				.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_ButtonPlanSave);
		buttonSave
				.setToolTipText(UIMessages.ODEN_SNAPSHOT_SnapshotView_ButtonTooltipPlanSave);
		buttonSave.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				destAgent = agentCombo.getText();
				sourceLocation = distinctionLoc(locationVar.getText())
						+ sourceLocPathText.getText();
				description = descText.getText();
				new SaveSnapshotPlanAction().run();
			}

			private String distinctionLoc(String text) {
				String result = ""; //$NON-NLS-1$
				if (text
						.equals(UIMessages.ODEN_SNAPSHOT_SnapshotView_DefaultLocComboText)) {
					result = CommandMessages.ODEN_CLI_OPTION_locvarsign;
				} else if (text
						.equals(UIMessages.ODEN_SNAPSHOT_SnapshotView_AbsolutePathComboText)) {
					result = ""; //$NON-NLS-1$
				} else {
					result = CommandMessages.ODEN_CLI_OPTION_locvarsign + text;
				}
				return result;
			}
		});

		Button buttonDelete = new Button(compositeButton, SWT.PUSH);
		buttonDelete
				.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_ButtonPlanDelete);
		buttonDelete
				.setToolTipText(UIMessages.ODEN_SNAPSHOT_SnapshotView_ButtonTooltipPlanDelete);
		buttonDelete.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				new DeleteSnapshotPlanAction().run();
			}
		});

		ImageDescriptor planImageDescriptor = ImageUtil
				.getImageDescriptor(UIMessages.ODEN_SNAPSHOT_SnapshotView_TakeSnapshotIcon);
		Image takeSnapshotImage = ImageUtil.getImage(planImageDescriptor);

		Button buttonTakeSnapshot = new Button(compositeButton, SWT.PUSH);
		buttonTakeSnapshot
				.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_ButtonTakeSnapshot);
		buttonTakeSnapshot
				.setToolTipText(UIMessages.ODEN_SNAPSHOT_SnapshotView_ButtonTooltipTakeSnapshot);
		buttonTakeSnapshot.setImage(takeSnapshotImage);
		GridData gridDataTake = new GridData(GridData.FILL_BOTH);
		gridDataTake.horizontalAlignment = GridData.END;
		buttonTakeSnapshot.setLayoutData(gridDataTake);
		buttonTakeSnapshot.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				new TakeSnapshotPlanAction().run();
			}
		});
		compositeButton.setLayoutData(buttonData);
		compositeDetail.setLayoutData(detailData);
	}

	private void setLocPathText(HashMap<String, String> mapStrSrc) {
		// String agent = mapStrSrc.get("agent-name");
		String locVar = mapStrSrc
				.get(UIMessages.ODEN_SNAPSHOT_SnapshotView_SourceMapKeyLocVar);
		String locVarPath = mapStrSrc
				.get(UIMessages.ODEN_SNAPSHOT_SnapshotView_SourceMapKeyLocVarAddedPath);

		if (locVar.length() > 0) {
			if (locVar.substring(0, 1).equals(
					CommandMessages.ODEN_CLI_OPTION_locvarsign)) {
				sourceLocPathText.setText(locVarPath);
			} else if (locVar.substring(0, 1).equals(
					CommandMessages.ODEN_CLI_OPTION_defaultlocsign)) {
				sourceLocPathText.setText(locVarPath);
			} else {

			}
		} else {
			sourceLocPathText.setText(locVarPath);
		}
	}

	private HashMap<String, String> parsingSource(String source) {
		HashMap<String, String> result = new HashMap<String, String>();

		int numAgent = source
				.indexOf(UIMessages.ODEN_SNAPSHOT_SnapshotView_AgentLocSeperateSign);
		String agent = source.substring(0, numAgent);
		result.put(UIMessages.ODEN_SNAPSHOT_SnapshotView_SourceMapKeyAgent,
				agent);
		String tempLoc = source.substring(numAgent + 1);

		String locVar = ""; //$NON-NLS-1$
		String location = ""; //$NON-NLS-1$

		if (tempLoc.indexOf(CommandMessages.ODEN_CLI_OPTION_locvarsign) == 0) { // location-variable
			int numLocVar = tempLoc.indexOf("/"); //$NON-NLS-1$
			if (numLocVar != -1) {
				locVar = tempLoc.substring(0, numLocVar); // $location-variable
				location = tempLoc.substring(numLocVar);
			} else {
				locVar = tempLoc;
				location = ""; //$NON-NLS-1$
			}
		} else if (tempLoc
				.indexOf(CommandMessages.ODEN_CLI_OPTION_defaultlocsign) == 0) { // default-location
			int numLocVar = tempLoc.indexOf("/"); //$NON-NLS-1$

			if (numLocVar != -1) {
				locVar = tempLoc.substring(0, numLocVar); // ~
				location = tempLoc.substring(numLocVar);
			} else {
				locVar = tempLoc;
				location = ""; //$NON-NLS-1$
			}
		} else { // absolute-path
			locVar = ""; //$NON-NLS-1$
			location = tempLoc;
		}
		result.put(UIMessages.ODEN_SNAPSHOT_SnapshotView_SourceMapKeyLocVar,
				locVar);
		result
				.put(
						UIMessages.ODEN_SNAPSHOT_SnapshotView_SourceMapKeyLocVarAddedPath,
						location);

		return result;
	}

	private void setAgentVar() {
		String result = ""; //$NON-NLS-1$
		String commnd = CommandMessages.ODEN_CLI_COMMAND_agent_info_json;
		hm = new HashMap<String, String>();

		try {
			result = OdenBroker.sendRequest(SHELL_URL, commnd);
			if (result != null) {
				JSONArray array = new JSONArray(result);
				if (array.length() > 0) {
					for (int i = 0; i < array.length(); i++) {
						String name = (String) ((JSONObject) array.get(i))
								.get("name"); //$NON-NLS-1$
						String urlRoot = (String) ((JSONObject) array.get(i))
								.get("loc"); //$NON-NLS-1$

						agentCombo.add(name);
						hm.put(name, urlRoot);
					}
					// agentCombo.select(0);
					// locationVar.removeAll();
					// setLocationVar();
				} else {
					OdenActivator
							.warning(CommonMessages.ODEN_CommonMessages_SetConfigXML);
				}
			} else {
				// no connection
				OdenActivator
						.warning(CommonMessages.ODEN_CommonMessages_UnableToConnectServer);
			}
		} catch (Exception odenException) {
			OdenActivator
					.error(
							UIMessages.ODEN_SNAPSHOT_SnapshotView_Exception_MsgAgentInfo,
							odenException);
			odenException.printStackTrace();
		}
		// HashMap aa =hm;
		// System.out.println();
	}

	private void setLocationVar() {
		String result = ""; //$NON-NLS-1$
		String commnd = CommandMessages.ODEN_CLI_COMMAND_agent_info_json;
		locVariable = new HashMap<String, String>();
		try {
			locationVar.removeAll();
			locationVar
					.add(UIMessages.ODEN_SNAPSHOT_SnapshotView_DefaultLocComboText);
			locationVar
					.add(UIMessages.ODEN_SNAPSHOT_SnapshotView_AbsolutePathComboText);
			result = OdenBroker.sendRequest(SHELL_URL, commnd);
			if (result != null) {
				JSONArray array = new JSONArray(result);
				if (array.length() > 0) {
					for (int i = 0; i < array.length(); i++) {
						String name = (String) ((JSONObject) array.get(i))
								.get("name"); //$NON-NLS-1$
						if (name.equals(agentCombo.getText())) {
							JSONObject locs = (JSONObject) ((JSONObject) array
									.get(i)).get("locs"); //$NON-NLS-1$
							Iterator it = locs.keys();
							while (it.hasNext()) {
								Object o = it.next();
								String locUri = locs.getString(o.toString());
								locationVar.add(o.toString());
								locVariable.put(o.toString(), locUri);
							}
							// root = (String) ((JSONObject)
							// array.get(i)).get("host");
						}
					}

					locationVar.redraw();
					// locationVar.select(0);
					// setagentUrl();
				}
			}
			// HashMap aa =locVariable;
			// System.out.println();
		} catch (Exception odenException) {
			OdenActivator
					.error(
							UIMessages.ODEN_SNAPSHOT_SnapshotView_Exception_MsgAgentInfo,
							odenException);
			odenException.printStackTrace();
		}
	}

	private void displayFile(Composite composite, final String string) {

		if (compositeDetail != null && !compositeDetail.isDisposed()) {
			compositeDetail.dispose();

			compositeDetail = new Composite(compositeWhole, SWT.NONE);
		}

		new SnapshotViewContentProvider();
		String fileInfo = ""; //$NON-NLS-1$
		try {
			fileInfo = SnapshotViewContentProvider.getInfo(SHELL_URL,
					CommandMessages.ODEN_CLI_COMMAND_snapshot_fileinfo
							+ " " + string + " -json"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (OdenException e) {
			OdenActivator
					.error(
							UIMessages.ODEN_SNAPSHOT_SnapshotView_Exception_GetSnapshotDetailInfo,
							e);
		}

		String info = ""; //$NON-NLS-1$
		try {
			JSONObject jo = new JSONArray(fileInfo).getJSONObject(0);
			info = jo.getString(string);
		} catch (JSONException e) {
			OdenActivator
					.error(
							UIMessages.ODEN_SNAPSHOT_SnapshotView_Exception_ParseSnapshotDetailInfo,
							e);
		}

		Cmd cmd = null;
		cmd = new Cmd(string + " = " + info); //$NON-NLS-1$
		String strFileSize = cmd.getOptionArg(SIZE_OPT);
		String strFileDate = cmd.getOptionArg(DATE_OPT);

		// comma setting
		Long longSize = Long.parseLong(strFileSize);
		String fileSizeComma = ""; //$NON-NLS-1$
		String resultWithUnit = ""; //$NON-NLS-1$

		if (longSize < 1024) {
			BigDecimal bd = new BigDecimal(longSize);
			BigDecimal fileSizeKB = bd.setScale(0, BigDecimal.ROUND_UP);

			DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
			df = new DecimalFormat("###,###,###"); //$NON-NLS-1$
			Long size = Long.parseLong(fileSizeKB.toString());
			fileSizeComma = df.format(size);
			resultWithUnit = fileSizeComma
					+ UIMessages.ODEN_SNAPSHOT_SnapshotView_FileSizeTailBytes;
		} else {
			double doubleSize = longSize / 1024;

			BigDecimal bd = new BigDecimal(doubleSize);
			BigDecimal fileSizeKB = bd.setScale(0, BigDecimal.ROUND_UP);

			DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
			df = new DecimalFormat("###,###,###"); //$NON-NLS-1$
			Long size = Long.parseLong(fileSizeKB.toString());
			fileSizeComma = df.format(size);
			resultWithUnit = fileSizeComma
					+ UIMessages.ODEN_SNAPSHOT_SnapshotView_FileSizeTailKB;
		}

		compositeDetail.setLayout(new GridLayout());
		GridData detailData = new GridData(GridData.FILL_HORIZONTAL);

		Group group = new Group(compositeDetail, SWT.SHADOW_ETCHED_IN);
		group.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_SnapshotGroupTitle);

		group.setLayout(new GridLayout(2, false));
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 1;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;

		Label label = new Label(group, SWT.SINGLE);
		label.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_LabelFileName);

		Text fileName = new Text(group, SWT.SINGLE | SWT.READ_ONLY);
		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		fileName.setText(string);
		fileName.setLayoutData(gridData1);

		Label source = new Label(group, SWT.SINGLE);
		source.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_LabelFileSize);

		Text fileSize = new Text(group, SWT.SINGLE | SWT.READ_ONLY);
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		fileSize.setText(resultWithUnit);
		fileSize.setLayoutData(gridData2);

		Label date = new Label(group, SWT.SINGLE);
		date.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_LabelFileDate);

		Text fileDate = new Text(group, SWT.SINGLE | SWT.READ_ONLY);
		GridData gridData3 = new GridData(GridData.FILL_HORIZONTAL);
		fileDate.setText(strFileDate);
		fileDate.setLayoutData(gridData3);

		group.setLayoutData(gridData);

		group.pack();

		Composite compositeButton = new Composite(compositeDetail, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		compositeButton.setLayout(layout);

		GridData buttonData = new GridData(GridData.FILL_HORIZONTAL);

		Button buttonDelete = new Button(compositeButton, SWT.PUSH);
		buttonDelete
				.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_ButtonSnapshotDelete);
		buttonDelete
				.setToolTipText(UIMessages.ODEN_SNAPSHOT_SnapshotView_ButtonTooltipSnapshotDelete);
		buttonDelete.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				new DeleteSnapshotAction().run();
			}
		});

		ImageDescriptor rollbackImageDescriptor = ImageUtil
				.getImageDescriptor(UIMessages.ODEN_SNAPSHOT_SnapshotView_RollbackIcon);
		Image rollbackImage = ImageUtil.getImage(rollbackImageDescriptor);

		Button buttonRollback = new Button(compositeButton, SWT.PUSH);
		buttonRollback
				.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_ButtonRollback);
		buttonRollback
				.setToolTipText(UIMessages.ODEN_SNAPSHOT_SnapshotView_ButtonTooltipRollback);
		buttonRollback.setImage(rollbackImage);
		GridData gridDataTake = new GridData(GridData.FILL_BOTH);
		gridDataTake.horizontalAlignment = GridData.END;
		buttonRollback.setLayoutData(gridDataTake);
		buttonRollback.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				new RollbackSnapshotAction().run();
			}
		});

		compositeButton.setLayoutData(buttonData);

		compositeDetail.setLayoutData(detailData);

	}

	private void selectOdenServer(Composite parent) {
		selectedName = null;
		compositeCombo = parent;
		GridLayout layout = new GridLayout();
		compositeCombo.setLayout(layout);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 1;
		compositeCombo.setLayoutData(gridData);

		GridLayout layoutSub = new GridLayout(3, false);
		compositeCombo.setLayout(layoutSub);

		GridData gridDataSub = new GridData(GridData.FILL_BOTH);
		gridDataSub.horizontalSpan = 1;

		Label label = new Label(compositeCombo, SWT.SINGLE);
		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		label.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_ServerSelect);
		label.setLayoutData(gridData1);

		serverCombo = new Combo(compositeCombo, SWT.READ_ONLY);
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		serverCombo.setLayoutData(gridData2);

		new CommonUtil().initServerCombo(serverCombo);

		if (selectedName == null) {
			// compositeDetail = new Composite(compositeWhole, SWT.NONE |
			// SWT.BORDER);
			listenCombo();
		}
		serverCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				listenCombo();
			}
		});
	}

	private static void listenCombo() {
		if (serverCombo.getItemCount() == 0) {

		} else {
			selectedName = OdenActivator.getDefault().getAliasManager()
					.getServerManager().getServer(serverCombo.getText())
					.getNickname();
			SnapshotNewPlanDialog.server = selectedName;
			Server server = OdenActivator.getDefault().getAliasManager()
					.getServerManager().getServer(selectedName);
			showServerList(server);
		}
	}

	/**
	 * Refresh server combo box.
	 */
	public static void refreshServerList() {
		serverCombo.removeAll();
		new CommonUtil().initServerCombo(serverCombo);
		listenCombo();
	}

	private static void showServerList(Server server) {
		SHELL_URL = CommonMessages.ODEN_CommonMessages_ProtocolString_HTTP
				+ server.getUrl()
				+ CommonMessages.ODEN_CommonMessages_ProtocolString_HTTPsuf;
		if (!checkConnect()) {
			SHELL_URL = null;
		} else {
		}
		invisibleRoot = null;
		refreshTree();
		// clearComposite();
	}

	/**
	 * Refresh Snapshot treeviewer.
	 */
	public static void refreshTree() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!viewer.getTree().isDisposed()) {
					invisibleRoot = null;
					viewer
							.setContentProvider(new SnapshotViewContentProvider());
				}
				viewer.refresh();
			}
		});
	}

	private static Boolean checkConnect() {
		String result = null;
		try {
			result = OdenBroker.sendRequest(SHELL_URL, "snapshot -json"); //$NON-NLS-1$
		} catch (CommandNotFoundException e) {
			OdenActivator.error("Anyframe Oden command not found.", e);
		} catch (OdenException e) {
			OdenActivator
					.error(
							UIMessages.ODEN_SNAPSHOT_SnapshotView_Exception_CheckConnection,
							e);
		}
		if (result == null) {
			return false;
		} else { // connect
			return true;
		}
	}

	/**
	 * To set Status Message in status line.
	 * 
	 * @param message
	 */
	public static void setStatusMessage(String message) {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		IWorkbenchPart part = page.getActivePart();
		IWorkbenchPartSite site = part.getSite();
		IViewSite vSite = (IViewSite) site;
		IActionBars actionBars = vSite.getActionBars();
		if (actionBars == null)
			return;
		IStatusLineManager statusLineManager = actionBars
				.getStatusLineManager();
		if (statusLineManager == null)
			return;
		statusLineManager.setMessage(message);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
		fillLocalPullDown(bars.getMenuManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(new NewSnapshotPlanAction());
		manager.add(new Separator());
		manager.add(new RefreshAction());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(new NewSnapshotPlanAction());
		manager.add(new Separator());
		manager.add(new RefreshAction());
	}

	/**
	 * To clean plan and file's detail information place.
	 */
	public static void clearComposite() {
		compositeDetail.dispose();
		compositeDetail = new Composite(compositeWhole, SWT.NONE);
		compositeDetail.setLayout(new GridLayout(2, true));
		compositeDetail.setLayoutData(new GridData());
	}

	private static Composite drawEmptyComposite(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, true));
		comp.setLayoutData(new GridData());
		return comp;
	}

	/**
	 * Return result what selected in this view.
	 * 
	 * @return Object array of result
	 */
	public Object[] getSelected() {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		if (selection == null)
			return null;
		Object[] result = selection.toArray();
		if (result.length == 0)
			return null;
		return result;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}