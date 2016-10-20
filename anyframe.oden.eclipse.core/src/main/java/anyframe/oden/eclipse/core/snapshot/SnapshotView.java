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
package anyframe.oden.eclipse.core.snapshot;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenTrees.TreeParent;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.messages.CommandMessages;
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
	public static final String ID = OdenActivator.PLUGIN_ID + ".snapshot.SnapshotView";

	protected static OdenBrokerService OdenBroker = new OdenBrokerImpl();

	private static Composite compositeWhole;
	private static Combo serverCombo;
	private static Composite compositeCombo;

	protected static Composite compositeDetail;
	protected static String inputText;
	protected static PatternFilter patternFilter;
	protected static FilteredTree filter;

	public static TreeParent invisibleRoot;
	public static TreeViewer viewer;
	public static String SHELL_URL;
	public static String selectedName;
	public static String targetLocation;
	public static String destAgent;
	public static String destination;
	public static String description;

	public String agentChosen;

	private static String[] DEST_OPT = { "dest", "d" }; //$NON-NLS-1$ //$NON-NLS-2$
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
					| SWT.V_SCROLL | SWT.BORDER, patternFilter, true);
		} else {
			filter = new FilteredTree(composite, SWT.NONE | SWT.H_SCROLL
					| SWT.V_SCROLL | SWT.BORDER, patternFilter);
		}

		viewer = filter.getViewer();
		showSnapshotTreeView();

		// Right-Side Composite
		compositeWhole = new Composite(parent, SWT.NONE);
		compositeWhole.setLayout(layout);
		GridData data1 = new GridData(GridData.FILL_VERTICAL);
		compositeWhole.setLayoutData(data1);

		// Right-Top Composite(Server)
		GridLayout layoutLeft = new GridLayout();
		Composite compositeServer = new Composite(compositeWhole, SWT.NONE);
		compositeServer.setLayout(layoutLeft);
		GridData data2 = new GridData(GridData.FILL_HORIZONTAL);
		compositeServer.setLayoutData(data2);

		selectOdenServer(compositeServer);

		// separator for server and detail
		GridLayout layoutLine = new GridLayout();
		Composite compositeLine = new Composite(compositeWhole, SWT.NONE);
		compositeLine.setLayout(layoutLine);
		GridData dataLine = new GridData(GridData.FILL_HORIZONTAL);
		compositeLine.setLayoutData(dataLine);

		Label separator1 = new Label(compositeLine,  SWT.SEPARATOR
				|SWT.HORIZONTAL);
		separator1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Right-Bottom Composite(Detail)
		compositeDetail = new Composite(compositeWhole, SWT.NONE);
		compositeDetail.setLayout(layoutLeft);
		GridData data3 = new GridData(GridData.FILL_BOTH);
		compositeDetail.setLayoutData(data3);

		Composite compositeTemp = new Composite(compositeWhole, SWT.NONE);
		compositeTemp.setLayout(layoutLeft);
		GridData data4 = new GridData(GridData.FILL_HORIZONTAL);
		compositeTemp.setLayoutData(data4);

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

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				/*
				 * event.getSelection ex> [selectName]
				 */
				String selectedName = event.getSelection().toString()
				.substring(1,
						event.getSelection().toString().length() - 1);

				new SnapshotViewContentProvider();
				ArrayList<String> snapshotPlanList = SnapshotViewContentProvider
				.getSnaphotPlanList(
						SHELL_URL,
						CommandMessages.ODEN_SNAPSHOT_SnapshotView_MsgInfoPlan
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
						CommandMessages.ODEN_SNAPSHOT_SnapshotView_MsgInfoFile
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
					CommandMessages.ODEN_SNAPSHOT_SnapshotView_MsgInfoPlan
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

		String source = cmd.getOptionArg(SOURCE_OPT);
		String dest = cmd.getOptionArg(DEST_OPT);
		String user = cmd.getOptionArg(USER_OPT);
		String date = cmd.getOptionArg(DATE_OPT);
		String[] desc = cmd.getOptionArgArray(DESC_OPT);

		StringTokenizer token = new StringTokenizer(dest, "\n"); //$NON-NLS-1$
		dest = token.nextToken();
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
		enrollDate
		.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_LabelPlanDate);

		final Text dateInfo = new Text(group, SWT.SINGLE | SWT.READ_ONLY);
		GridData gridData6 = new GridData(GridData.FILL_HORIZONTAL);
		dateInfo.setText(date);
		dateInfo.setLayoutData(gridData6);

		Label agent = new Label(group, SWT.SINGLE);
		agent.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_LabelDestAgent);

		final Text agentText = new Text(group, SWT.SINGLE | SWT.BORDER);
		GridData gridData10 = new GridData(GridData.FILL_HORIZONTAL);
		int temp = dest.indexOf("/"); //ex> dest = agent1/location1 //$NON-NLS-1$
		String strAgent = dest.substring(0, temp);
		agentText.setText(strAgent);
		agentText.setLayoutData(gridData10);

		Label sourceLabel = new Label(group, SWT.SINGLE);
		sourceLabel
		.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_LabelSourceLocation);

		final Text sourceLocText = new Text(group, SWT.SINGLE | SWT.BORDER);
		GridData gridData4 = new GridData(GridData.FILL_HORIZONTAL);
		if (source.length() == 1 || source == null) {
			source = ""; //$NON-NLS-1$
		} else {
			source = source.substring(1);
		}
		sourceLocText.setText(source);
		sourceLocText.setLayoutData(gridData4);

		Label labelDest = new Label(group, SWT.SINGLE);
		labelDest
		.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_LabelDestination);

		final Text destText = new Text(group, SWT.SINGLE | SWT.BORDER);
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		String strDest = dest.substring(temp + 1);
		destText.setText(strDest);
		destText.setLayoutData(gridData2);

		Label labelDesc = new Label(group, SWT.SINGLE);
		labelDesc
		.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_LabelPlanDescription);

		// final Text descText = new Text(group, SWT.MULTI | SWT.V_SCROLL |
		// SWT.BORDER);
		final Text descText = new Text(group, SWT.SINGLE | SWT.BORDER);
		GridData gridData3 = new GridData(GridData.FILL_HORIZONTAL);
		// int lineHeight = descText.getLineHeight();
		// gridData3.verticalSpan = 2;
		// gridData3.heightHint = lineHeight*3; //textbox 3line fix
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
				targetLocation = sourceLocText.getText();
				destAgent = agentText.getText();
				destination = destText.getText();
				description = descText.getText();
				new SaveSnapshotPlanAction().run();
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

	private void displayFile(Composite composite, final String string) {

		if (compositeDetail != null && !compositeDetail.isDisposed()) {
			compositeDetail.dispose();

			compositeDetail = new Composite(compositeWhole, SWT.NONE);
		}

		new SnapshotViewContentProvider();
		String fileInfo = ""; //$NON-NLS-1$
		try {
			fileInfo = SnapshotViewContentProvider.getInfo(SHELL_URL,
					CommandMessages.ODEN_SNAPSHOT_SnapshotView_MsgInfoFile
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
		double doubleSize = longSize / 1024;

		BigDecimal bd = new BigDecimal(doubleSize);
		BigDecimal fileSizeKB = bd.setScale(0, BigDecimal.ROUND_UP);

		DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
		df = new DecimalFormat("###,###,###"); //$NON-NLS-1$
		Long size = Long.parseLong(fileSizeKB.toString());
		String fileSizeComma = df.format(size);

		compositeDetail.setLayout(new GridLayout());
		GridData detailData = new GridData(GridData.FILL_HORIZONTAL);

		Group group = new Group(compositeDetail, SWT.SHADOW_ETCHED_IN);
		group
		.setText(UIMessages.ODEN_SNAPSHOT_SnapshotView_SnapshotGroupTitle);

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
		fileSize.setText(fileSizeComma
				+ UIMessages.ODEN_SNAPSHOT_SnapshotView_FileSizeTail);
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
			compositeDetail = new Composite(compositeWhole, SWT.NONE);
			listenCombo();
		}
		serverCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				listenCombo();
			}
		});
	}

	private static void listenCombo() {
		selectedName = OdenActivator.getDefault().getAliasManager().getServerManager().getServer(serverCombo.getText()).getNickname();
		SnapshotNewPlanDialog.server = selectedName;
		Server server = OdenActivator.getDefault().getAliasManager().getServerManager().getServer(selectedName);
		showServerList(server);
	}

	/**
	 * Refresh server combo box.
	 */
	public static void refreshServerList() {
		serverCombo.removeAll();
		new CommonUtil().initServerCombo(serverCombo);
	}

	private static void showServerList(Server server) {
		SHELL_URL = "http://" + server.getUrl() + "/shell"; //$NON-NLS-1$ //$NON-NLS-2$
		if (!checkConnect()) {
			SHELL_URL = null;
		}else{}
		invisibleRoot = null;
		refreshTree();
		clearComposite();
	}

	/**
	 * Refresh Snapshot treeviewer.
	 */
	//	public static void refreshTree() {
	//		Display display = viewer.getTree().getDisplay();
	//		display.asyncExec(new Runnable() {
	//			public void run() {
	//				viewer.setContentProvider(new SnapshotViewContentProvider());
	//				viewer.setLabelProvider(new SnapshotViewLabelProvider());
	//				viewer.refresh();
	//			}
	//		});
	//	}

	public static void refreshTree() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!viewer.getTree().isDisposed()) {
					invisibleRoot = null;
					viewer.setContentProvider(new SnapshotViewContentProvider());
				}
				viewer.refresh();
			}
		});
	}

	private static Boolean checkConnect() {
		String result = null;
		try {
			result = OdenBroker.sendRequest(SHELL_URL, "snapshot -json"); //$NON-NLS-1$
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
		compositeDetail = drawEmptyComposite(compositeWhole);
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