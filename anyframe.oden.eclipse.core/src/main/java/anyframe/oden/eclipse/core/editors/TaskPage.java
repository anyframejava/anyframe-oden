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
package anyframe.oden.eclipse.core.editors;

import java.util.ArrayList;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.editors.actions.DeleteTaskAction;
import anyframe.oden.eclipse.core.editors.actions.NewTaskAction;
import anyframe.oden.eclipse.core.editors.actions.RunDeployTaskAction;
import anyframe.oden.eclipse.core.editors.actions.SaveTaskAction;
import anyframe.oden.eclipse.core.editors.actions.TaskRefreshAction;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.Cmd;
import anyframe.oden.eclipse.core.utils.DialogUtil;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Implement Oden TaskPage. This class implement
 * Page class.
 * 
 * @author HONG JungHwan
 * @version 1.0.0 RC2
 * 
 */
public class TaskPage implements Page {
	//TaskPage
	private static TaskPage instance;
	private final String HTTP_PROT = CommonMessages.ODEN_CommonMessages_ProtocolString_HTTP;

	private Label task;
	//	private Label run;
	private Label descrip;

	// text box
	private Text taskNameText;
	public Text getTaskNameText() {
		return taskNameText;
	}

	public void setTaskNameText(Text taskNameText) {
		this.taskNameText = taskNameText;
	}

	private Text descText;
	private Text filterText;
	private Text policyFilterText;
	
	// Table list
	private Table taskTable;
	private TableViewer taskViewer;

	private Table RunTable;
	private TableViewer runViewer;

	private FormToolkit toolkit;

	// Button
	private Button addTask;
	private Button removeTask;
	private Button saveTask;
	private Button runTask;
	private Button runTask_;

	private String shellUrl;
	private String policiesData;
	private int lastNum;
	private ArrayList<String> originTask;
	private boolean newTask;
	private ImageDescriptor _titleImageDescriptor = ImageUtil.getImageDescriptor("icons/form_banner.gif");
	private Image _TitleImage = ImageUtil.getImage(_titleImageDescriptor);

	TableViewerColumn taskName, desc, check, policy, description;

	private final String MSG_TASK_SHOW = CommandMessages.ODEN_CLI_COMMAND_task_info_json;
	private final String MSG_DETAIL_SHOW = CommandMessages.ODEN_CLI_COMMAND_task_info;
	private final String MSG_POLICY_SHOW = CommandMessages.ODEN_CLI_COMMAND_policy_info_json;

	private final int leftWidth = 120;
	private final int rightWidth = 150;
	//	private boolean taskNew;
	private Composite client;
	public Composite getClient() {
		return client;
	}

	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();
	//	private TaskPage taskInstance;
	private String serverNickname;

	ViewerFilter filter = new ViewerFilter() {
		public boolean select(Viewer viewer1, Object parentElement,
				Object element) {
			String text = ".*" + filterText.getText() + ".*"; //$NON-NLS-1$ //$NON-NLS-2$
			if (text == null || text.length() == 0 || filterText.getText().trim().equals("")) {
				return true;
			}
			String details = ((TaskDetails) element).getTaskName();
			if (details.matches(text)) {
				return true;
			}
			return false;
		}
	};
	
	ViewerFilter policyfilter = new ViewerFilter() {
		public boolean select(Viewer viewer1, Object parentElement,
				Object element) {
			String text = ".*" + policyFilterText.getText() + ".*"; //$NON-NLS-1$ //$NON-NLS-2$
			if (text == null || text.length() == 0 || policyFilterText.getText().trim().equals("")) {
				return true;
			}
			String details = ((TaskDetails) element).getPolicyName();
			if (details.matches(text)) {
				return true;
			}
			return false;
		}
	};

	public static TaskPage getInstance() {
		instance = new TaskPage();
		return instance;
	}

	public Composite getPage(final Composite parent) {
		// get Server object

		Server server = OdenEditor.server;
		serverNickname = server.getNickname();

		setShellUrl(HTTP_PROT + server.getUrl() + "/shell"); 

		toolkit = new FormToolkit(parent.getDisplay());

		final ScrolledForm form = toolkit.createScrolledForm(parent);

		// creation Head Section
		createHeadSection(form, toolkit);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		form.getBody().setLayout(layout);

		Composite whole = toolkit.createComposite(form.getBody());
		whole.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		whole.setLayoutData(gd);

		createLeftSection(form, toolkit, whole );
		createRightSection(form, toolkit , whole);

		loadInitData();

		// Event
		filterEvent();
		policyfilterEvent();
		
		tableEvent();
		temporaryEvent();
		setNewTask(false);

		return form;
	}

	private void createHeadSection(final ScrolledForm form, FormToolkit toolkit) {
		Image titleImage =
			new Image(form.getDisplay(), getClass().getResourceAsStream(
					UIMessages.ODEN_EDITORS_TaskPage_TaskPageTitleImage));
		form.setText(UIMessages.ODEN_EDITORS_TaskPage_TaskPageTitle);
		form.setBackgroundImage(_TitleImage);
		form.setImage(titleImage);
		form.setMessage( UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageDesc , SWT.NONE);

		fillLocalToolBar(form.getForm().getToolBarManager() , form);
		form.getForm().getToolBarManager().update(true);
		toolkit.decorateFormHeading(form.getForm());
	}

	private void fillLocalToolBar(IToolBarManager toolBarManager , ScrolledForm form) {
		toolBarManager.add(new TaskRefreshAction(serverNickname));
	}

	private void createLeftSection(final ScrolledForm form, FormToolkit toolkit , Composite parent) {
		Composite client_left = toolkit.createComposite(parent);
		client_left.setLayout(new GridLayout());

		GridData gd = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);

		client_left.setLayoutData(gd);
		createAllTaskSection(form, toolkit,
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageSubSection1 , client_left);
	}

	private void createRightSection(final ScrolledForm form, FormToolkit toolkit , Composite parent) {
		client = toolkit.createComposite(parent , SWT.NONE);
		client.setVisible(false);
		client.setLayout(new GridLayout());

		GridData gd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);

		client.setLayoutData(gd);

		createTaskDetailSection(form, toolkit,
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageSubSection2,
				client);
		createRunTaskSection(form, toolkit,
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageRunSubTitle,
				client);
	}

	private void createSpacer(FormToolkit toolkit, Composite parent, int span,
			int height) {
		Label spacer = toolkit.createLabel(parent, "");
		GridData gd = new GridData();
		gd.heightHint = height;
		gd.horizontalSpan = span;
		spacer.setLayoutData(gd);
	}

	@SuppressWarnings("deprecation")
	private void createAllTaskSection(final ScrolledForm form, FormToolkit toolkit,
			String title , Composite parent ) {
		createSpacer(toolkit, form.getBody(), 2, 4);

		Section section = toolkit.createSection(parent ,Section.DESCRIPTION | Section.TITLE_BAR);

		section.setActiveToggleColor(toolkit.getHyperlinkGroup().getActiveForeground());
		section.setToggleColor(toolkit.getColors().getColor(FormColors.SEPARATOR));

		Composite client = toolkit.createComposite(section, SWT.WRAP);

		createSpacer(toolkit, client , 2, 1);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		client.setLayout(layout);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		client.setLayoutData(gd);

		gd.widthHint = leftWidth;
		// type filter text
		filterText = new Text(client, SWT.LEFT | SWT.BORDER);
		gd.horizontalSpan = 2;

		filterText.setLayoutData(gd);
		filterText.setText("type filter text");

		createSpacer(toolkit, client , 2, 1);


		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.verticalSpan = 5;
		gridData.widthHint = 200;
		gridData.heightHint = 225;
		// Task Table
		taskTable = new Table(client, SWT.SINGLE | SWT.FULL_SELECTION
				| SWT.BORDER | SWT.V_SCROLL);
		setTaskViewer(new TableViewer(taskTable));
		getTaskViewer().setContentProvider(new PolicyContentProvider());

		taskTable.setHeaderVisible(true);
		taskTable.setLinesVisible(true);

		taskName = new TableViewerColumn(getTaskViewer(), SWT.None);
		taskName.getColumn().setText(
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskTableCol1);
		taskName.getColumn().setWidth(150);
		taskName.setLabelProvider(new TaskNameColumnLabelProvider());

		desc = new TableViewerColumn(getTaskViewer(), SWT.None);
		desc.getColumn().setText(
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskTableCol2);
		desc.getColumn().setWidth(250);
		desc.setLabelProvider(new TaskDescColumnLabelProvider());
		taskTable.setLayoutData(gridData);

		// task add Button
		setAddTask(toolkit.createButton(client,
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskAddBtn,
				SWT.PUSH));
		getAddTask().addListener(SWT.Selection, listener);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 120;
		getAddTask().setLayoutData(gd);

		// task remove Button
		setRemoveTask(toolkit.createButton(client,
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskRemoveBtn,
				SWT.PUSH));
		getRemoveTask().addListener(SWT.Selection, listener);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 120;
		getRemoveTask().setLayoutData(gd);

		new Label(client, SWT.NONE);
		new Label(client, SWT.NONE);

		// task run Button
		Image imageRun =
			new Image(client.getDisplay(), getClass().getResourceAsStream(
					UIMessages.ODEN_EDITORS_TaskPage_RunDeployImage));
		runTask = toolkit.createButton(client,
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskRunBtn,
				SWT.PUSH);
		runTask.setImage(imageRun);
		runTask.addListener(SWT.Selection, listener);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING );
		gd.widthHint = 120;
		runTask.setLayoutData(gd);

		section.setText(title);
		section
		.setDescription(UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageSubSection1Desc);
		section.setClient(client);
		section.setExpanded(true);

		gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);
	}

	private void createTaskDetailSection(final ScrolledForm form, FormToolkit toolkit,
			String title, Composite parent) {
		Section section = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR);

		Composite client = toolkit.createComposite(section, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 6;
		layout.marginHeight = 10;
		layout.marginWidth = 10;

		client.setLayout(layout);

		// task
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 15;

		gd.widthHint = rightWidth;

		client.setLayoutData(gd);

		// Task
		createSpacer(toolkit, client , 6, 1);
		task = new Label(client, SWT.LEFT);
		task.setText(UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskNameLabel_Man);
		task.setForeground(new Color(Display.getCurrent(), 65, 105, 225));

		gd.horizontalSpan = 5;
		taskNameText = new Text(client, SWT.LEFT | SWT.BORDER);

		taskNameText.setLayoutData(gd);
		client.setLayoutData(gd);

		createSpacer(toolkit, client, 6, 1);

		// Description
		descrip = new Label(client, SWT.LEFT);
		descrip.setText(UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskDescLabel_Opt);
		descrip.setForeground(new Color(Display.getCurrent(), 65, 105, 225));

		setDescText(new Text(client, SWT.LEFT | SWT.BORDER));
		getDescText().setLayoutData(gd);

		// END Layout
		// Section title & Description
		section.setText(title);
		section
		.setDescription(UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageSubSection2Desc);
		section.setClient(client);

		gd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		section.setLayoutData(gd);

	}

	private void createRunTaskSection(final ScrolledForm form, FormToolkit toolkit,
			String title, Composite parent) {
		Section section = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR);

		Composite client = toolkit.createComposite(section, SWT.WRAP);

		GridLayout layout = new GridLayout();
		layout.numColumns = 6;
		layout.marginHeight = 10;
		layout.marginWidth = 10;

		client.setLayout(layout);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 15;
		gd.widthHint = rightWidth;

		client.setLayoutData(gd);
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		policyFilterText = new Text(client, SWT.LEFT | SWT.BORDER);
		gd.horizontalSpan = 6;

		policyFilterText.setLayoutData(gd);
		policyFilterText.setText("type filter text");

		createSpacer(toolkit, client , 6, 1);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 6;
		gd.verticalSpan = 2;
		gd.heightHint = 110;

		RunTable = new Table(client, SWT.SINGLE | SWT.FULL_SELECTION
				| SWT.BORDER | SWT.V_SCROLL | SWT.CHECK);
		setRunViewer(new TableViewer(RunTable));
		getRunViewer().setContentProvider(new PolicyContentProvider());

		RunTable.setHeaderVisible(true);
		RunTable.setLinesVisible(true);

		policy = new TableViewerColumn(getRunViewer(), SWT.None);
		policy.getColumn().setText(
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPagePolicyCol1);
		policy.getColumn().setWidth(200);
		policy.setLabelProvider(new PolicyNameColumnLabelProvider());

		description = new TableViewerColumn(getRunViewer(), SWT.None);
		description.getColumn().setText(
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPagePolicyCol2);
		description.getColumn().setWidth(150);
		description.setLabelProvider(new PolicyDescColumnLabelProvider());
		RunTable.setLayoutData(gd);

		// Save Button
		createSpacer(toolkit, client, 6, 4);

		gd = new GridData();
		gd.widthHint = 120;
		saveTask = toolkit.createButton(client,
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskSaveBtn,
				SWT.PUSH);
		saveTask.addListener(SWT.Selection, listener);
		saveTask.setLayoutData(gd);

		new Label(client , SWT.NONE);

		Image imageRun =
			new Image(parent.getDisplay(), getClass().getResourceAsStream(
					UIMessages.ODEN_EDITORS_TaskPage_RunDeployImage));
		runTask_ = toolkit.createButton(client,
				UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskRunBtn,
				SWT.PUSH);
		runTask_.setImage(imageRun);
		runTask_.addListener(SWT.Selection, listener);
		runTask_.setLayoutData(gd);

		// END Layout
		// Section title & Description
		section.setText(title);
		section.setDescription(UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageSubTitle);
		section.setClient(client);
		section.setExpanded(true);

		gd = new GridData(GridData.FILL_BOTH
				| GridData.VERTICAL_ALIGN_BEGINNING);
		section.setLayoutData(gd);

	}

	private void chageLabel() {
		task.setText(UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskNameLabel_Opt);
	}
	
	/**
	 * change Mandatory label
	 */
	public void chageMandaLabel() {
		task.setText(UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskNameLabel_Man); 
		descrip.setText(UIMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskDescLabel_Opt); 
	}
	
	/**
	 * task data loading
	 */
	@SuppressWarnings("unchecked")
	public void loadInitData()  {
		String commnd = ""; 
		String result = ""; 
		TaskDetails details = null;
		ArrayList taskList = new ArrayList();
		originTask = new ArrayList<String>();
		commnd = MSG_TASK_SHOW;
		int index = 0;
		int selectIndex = 0;
		try {	
			result = OdenBroker.sendRequest(getShellUrl(), commnd);
			if (result != null ) {
				JSONArray array = new JSONArray(result);

				for (int i = 0; i < array.length(); i++) {
					Object o = ((JSONObject) array.get(i)).keys().next();
					if(!(o.toString().equals("KnownException"))){ // no data
						Cmd result_ = new Cmd("foo", "fooAction \""  
								+ o.toString()
								+ "\" " 
								+ (String) ((JSONObject) array.get(i)).get(
										o.toString()).toString());
						String name = o.toString();

						String desc = result_.getOptionArg(new String[] { "desc" }); 
						if(!(name.matches(CommandMessages.ODEN_CLI_COMMAND_task_tempname+".*"))){
							++index;
							details = new TaskDetails(name, desc, null, null);
							taskList.add(details);
							originTask.add(name);
							if(name.equals(taskNameText.getText())){
								selectIndex = index;
							}
						}
					}
				}
				getTaskViewer().setInput(taskList);
				setLastNum(getTaskViewer().getTable().getItemCount());
				getTaskViewer().getTable().select(selectIndex - 1);
			}
		} catch (OdenException odenException) {
		} catch (Exception e) {
			OdenActivator.error("Exception occured while loading Initial info.",e);
		}
	}
	
	/**
	 * the policy data of running task  
	 */
	@SuppressWarnings("unchecked")
	public void loadInitPolicyData() {
		String commnd = ""; 
		String result = ""; 
		TaskDetails details = null;
		ArrayList policyList = new ArrayList();
		commnd = MSG_POLICY_SHOW;
		try {
			result = OdenBroker.sendRequest(getShellUrl(), commnd);
			if (result != null) {
				JSONArray array = new JSONArray(result);

				for (int i = 0; i < array.length(); i++) {
					Object o = ((JSONObject) array.get(i)).keys().next();
					Cmd result_ = new Cmd("foo", "fooAction \""  
							+ o.toString()
							+ "\" " 
							+ (String) ((JSONObject) array.get(i)).get(
									o.toString()).toString());
					String name = o.toString();
					String desc = result_.getOptionArg(new String[] { "desc" }); 
					if(!(name.matches(CommandMessages.ODEN_CLI_COMMAND_policy_tempname+".*"))){
						details = new TaskDetails(null, null, name, desc);
						policyList.add(details);
					}
				}

				getRunViewer().setInput(policyList);
			}
		} catch (OdenException odenException) {
		} catch (Exception e) {
			OdenActivator.error("Exception occured while loading policy info.", e);
		}

	}

	private Listener listener = new Listener() {
		public void handleEvent(Event event) {
			TaskDetails details = null;
			ISelection selection = getTaskViewer().getSelection();
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			details = (TaskDetails) obj;
			if (event.widget == getRemoveTask()) {
				// validation check(remove)
				if (checkRemove(obj)) {
					if (DialogUtil.confirmMessageDialog(CommonMessages.ODEN_CommonMessages_Title_ConfirmDelete,
							UIMessages.ODEN_EDITORS_TaskPage_DialogMsg_ConfirmDeleteTask_MessagePre + details.getTaskName() + CommonMessages.ODEN_CommonMessages_Confirm_MessageSuf)) { 
						new DeleteTaskAction().run(serverNickname);
						client.setVisible(false);
					}
				} else {
					DialogUtil
					.openMessageDialog(
							CommonMessages.ODEN_CommonMessages_Title_Warning,
							CommonMessages.ODEN_CommonMessages_SelectItemFirst,  
							MessageDialog.WARNING);
				}
			} else if (event.widget == saveTask) {
				// validation check(save)
				if (checkSave())
					new SaveTaskAction().run(serverNickname);

			} else if (event.widget == getAddTask()) {
				// validation check(add)
				client.setVisible(true);
				new NewTaskAction().run(serverNickname);
			} else if (event.widget == runTask || event.widget == runTask_ ) {
				if (checkDeploy(obj)) {
					if(dupTaskCheck())
						new RunDeployTaskAction().run(serverNickname);
					else
						DialogUtil.openMessageDialog(CommonMessages.ODEN_CommonMessages_Title_Warning,UIMessages.ODEN_EDITORS_TaskPage_DialogMsg_FirstSave,
								MessageDialog.WARNING);
				} else {
					DialogUtil.openMessageDialog(CommonMessages.ODEN_CommonMessages_Title_Warning, CommonMessages.ODEN_CommonMessages_SelectItemFirst, 
							MessageDialog.WARNING);
				}
			}
		}
	};
	
	/**
	 * task detail data loading
	 */
	public void showTaskDetail(String taskName) {
		String commnd = ""; 
		String result = ""; 
		String desc = "";

		String policies = ""; 
		String[] policy = null;

		commnd = MSG_DETAIL_SHOW + " " + '"' + taskName + '"' + " " + "-json";   
		try {
			result = OdenBroker.sendRequest(getShellUrl(), commnd);
			JSONArray array = new JSONArray(result);

			for (int i = 0; i < array.length(); i++) {
				Object o = ((JSONObject) array.get(i)).keys().next();
				Cmd result_ = new Cmd("foo", "fooAction \""  
						+ o.toString()
						+ "\" " 
						+ (String) ((JSONObject) array.get(i))
						.get(o.toString()).toString());
				desc = result_.getOptionArg(new String[] { "desc" });
				policy = result_.getOptionArgArray(new String[] { "p", "policy" });  
			}

			for (int i = 0; i < policy.length; i++) {
				policies = policies + policy[i] + ",";
			}
			// clear text Filed
			clearText();
			taskNameText.setText(taskName);
			getDescText().setText(desc);
			policiesData = policies.substring(0, policies.lastIndexOf(",")); 
			checkRuntable(policiesData);

		} catch (OdenException e) {
		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while loading deailed task info.",odenException);
		}

	}

	private class TaskNameColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((TaskDetails) element).getTaskName();
		}
	}

	private class TaskDescColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((TaskDetails) element).getDescription();
		}
	}

	private class PolicyNameColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((TaskDetails) element).getPolicyName();
		}
	}

	private class PolicyDescColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((TaskDetails) element).getPolicyDesc();
		}
	}

	private Boolean checkSave() {
		if (taskNameText.getText().equals("")) {
			DialogUtil.openMessageDialog(
					CommonMessages.ODEN_CommonMessages_Title_Warning,
					UIMessages.ODEN_EDITORS_TaskPage_DialogMsg_InputTaskName, MessageDialog.INFORMATION);
			taskNameText.setFocus();
			return false;

		} else if (taskNameText.getEnabled() && dupTaskCheck()) {
			DialogUtil.openMessageDialog(CommonMessages.ODEN_CommonMessages_Title_Warning,
					CommonMessages.ODEN_CommonMessages_NameAlreadyExists,
					MessageDialog.WARNING);
			taskNameText.setFocus();
			return false;
		} else if (taskNameText.getText().equals(UIMessages.ODEN_EDITORS_TaskPage_TempTaskName)) {
			// check temporary taskName
			DialogUtil.openMessageDialog(CommonMessages.ODEN_CommonMessages_Title_Warning,
					CommonMessages.ODEN_CommonMessages_NameShouldBeSpecified + '"' + UIMessages.ODEN_EDITORS_TaskPage_TempTaskName + '"',
					MessageDialog.WARNING);
			taskNameText.setFocus();
			return false;
		}
		// check policy
		TableItem[] tia = getRunViewer().getTable().getItems();
		String policies = "";
		for (int i = 0; i < tia.length; i++) {
			if (tia[i].getChecked()) {
				policies = policies + tia[i].getText(0) + ",";
			}
		}
		if (policies.equals("")) {
			DialogUtil.openMessageDialog(
					CommonMessages.ODEN_CommonMessages_Title_Warning,
					"Select Policy", MessageDialog.INFORMATION);
			return false;
		}


		return true;
	}

	private Boolean checkRemove(Object obj) {
		if (obj != null)
			return true;
		else
			return false;
	}

	private Boolean checkDeploy(Object obj) {
		if (obj != null)
			return true;
		else
			return false;
	}

	/**
	 * initialize task detail data
	 */
	public void clearText() {
		taskNameText.setText("");
		getDescText().setText("");
		getRunViewer().getTable().clearAll();

	}

	private void checkRuntable(String ckPolicy) {
		loadInitPolicyData();
		TableItem[] tia = runViewer.getTable().getItems();
		for (int i = 0; i < tia.length; i++)
			if (chkMatch(tia[i].getText(0) , ckPolicy ))
				tia[i].setChecked(true);
	}

	private boolean chkMatch(String tableValue , String policyValue) {
		String[] values = policyValue.split(",");
		for(String value : values) 
			if(value.equals(tableValue))
				return true;
		return false;
	}
	
	private void filterEvent() {
		// filter Text Event
		filterText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {
				
			}

			public void keyReleased(KeyEvent ke) {
				if(!(filterText.getText().equals("type filter text"))){
					TaskPage.this.getTaskViewer().addFilter(TaskPage.this.filter);
					getTaskViewer().refresh();
				} 

			}
		});
		filterText.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent event) {

			}

			public void mouseDown(MouseEvent event) {
				if (filterText.getText().equals("type filter text"))
					filterText.setSelection(0, 16);

			}

			public void mouseDoubleClick(MouseEvent event) {

			}

		});
	}
	
	private void policyfilterEvent() {
		// filter Text Event
		policyFilterText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {
				
			}

			public void keyReleased(KeyEvent ke) {
				if(!(policyFilterText.getText().equals("type filter text"))){
					TaskPage.this.getRunViewer().addFilter(TaskPage.this.policyfilter);
					if(! chkNewTask())
						checkRuntable(policiesData);
					getRunViewer().refresh();
				} 

			}
		});
		policyFilterText.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent event) {

			}

			public void mouseDown(MouseEvent event) {
				if (policyFilterText.getText().equals("type filter text"))
					policyFilterText.setSelection(0, 16);

			}

			public void mouseDoubleClick(MouseEvent event) {

			}

		});
	}
	
	private void tableEvent() {
		// task table viewer event
		getTaskViewer().getTable().addMouseListener(new MouseAdapter() {

			public void mouseDown(MouseEvent e) {
				if (getTaskViewer().getTable().getItem(new Point(e.x, e.y)) != null) {
					if (chkNewTask()) {
						// add task
						clearText();
						TaskDetails details = null;
						ISelection selection = getTaskViewer().getSelection();
						Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
						details = (TaskDetails) obj;
						taskNameText.setText(details.getTaskName());
						getDescText().setText(details.getDescription());
						taskNameText.setEnabled(true);
						getAddTask().setEnabled(false);
						loadInitPolicyData();

					} else {
						taskNameText.setEnabled(false);
						getRemoveTask().setEnabled(true);
						// change Label
						chageLabel();
						TaskDetails details = new TaskDetails();
						ISelection selection = getTaskViewer().getSelection();
						Object obj = ((IStructuredSelection) selection).getFirstElement();
						details = (TaskDetails) obj;
						client.setVisible(true);
						showTaskDetail(details.getTaskName());
						if(chkNewTaskExist())
							getAddTask().setEnabled(false);
						else
							getAddTask().setEnabled(true);
					}
				} else {
					client.setVisible(false);
					taskViewer.getTable().clearAll();
					taskViewer.getTable().deselectAll();
					runViewer.getTable().deselectAll();
					loadInitData();
					addTask.setEnabled(true);
					taskViewer.refresh();
				}
			}
		});

	}
	/**
	 * Returns the new task exist or not
	 * @return true/false , When a new task exist , return true.
	 */
	public boolean chkNewTaskExist() {
		int count = getTaskViewer().getTable().getItemCount();
		int originCount = originTask.size();
		if(count == originCount)
			return false;

		return true;
	}
	/**
	 * Returns the selected task of new task or not
	 * @return true/false
	 */
	public boolean chkNewTask() {
		int selected= getTaskViewer().getTable().getSelectionIndex();
		if(selected != getLastNum())
			return false;
		return true;
	}

	private void temporaryEvent() {
		taskNameText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {
			}
			public void keyReleased(KeyEvent ke) {
				tempProcess();
			}
		});
		getDescText().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {
			}
			public void keyReleased(KeyEvent ke) {
				tempProcess();
			}
		});
	}

	private void tempProcess () {
		if(chkNewTask()) {
			removeTempcell();
			inputTempcell(getLastNum());
		} else {
			int selected= getTaskViewer().getTable().getSelectionIndex();
			removeTempcell();
			inputTempcell(selected);
			getTaskViewer().getTable().remove(selected + 1);
			getAddTask().setEnabled(true);
		}

	}
	/**
	 * Remove temporary cell
	 */
	public void removeTempcell() {
		TaskDetails details = null;
		ISelection selection = getTaskViewer().getSelection();

		details = (TaskDetails) ((IStructuredSelection) selection)
		.getFirstElement();
		if (details != null) {
			getTaskViewer().remove(details);
		}
		getTaskViewer().refresh();
	}
	private void inputTempcell( int num) {
		String tempTaskName = taskNameText.getText();
		String tempTaskDesc = getDescText().getText();

		TaskDetails details = null;
		details = new TaskDetails(tempTaskName, tempTaskDesc,  null, null);
		getTaskViewer().insert(details, num );
		getTaskViewer().getTable().select(num );

	}

	private Boolean dupTaskCheck() {
		String commnd = ""; 
		String result = ""; 
		commnd = MSG_TASK_SHOW;
		try {	
			result = OdenBroker.sendRequest(getShellUrl(), commnd);
			if (result != null ) {
				JSONArray array = new JSONArray(result);

				for (int i = 0; i < array.length(); i++) {
					Object o = ((JSONObject) array.get(i)).keys().next();
					if(!(o.toString().equals("KnownException"))){ // no data
						new Cmd("foo", "fooAction \""  
								+ o.toString()
								+ "\" " 
								+ (String) ((JSONObject) array.get(i)).get(
										o.toString()).toString());
						String name = o.toString();
						if(name.equals(taskNameText.getText())){
							return true;
						}
					}
				}
			}			

		} catch (OdenException e) {

		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while check policy duplication.",odenException);
		}
		return false;
	}
	/**
	 * Constructor UI component
	 */
	public void setRunViewer(TableViewer runViewer) {
		this.runViewer = runViewer;
	}

	public TableViewer getRunViewer() {
		return runViewer;
	}

	public void setDescText(Text descText) {
		this.descText = descText;
	}

	public Text getDescText() {
		return descText;
	}

	public void setShellUrl(String shellUrl) {
		this.shellUrl = shellUrl;
	}

	public String getShellUrl() {
		return shellUrl;
	}

	public void setAddTask(Button addTask) {
		this.addTask = addTask;
	}

	public Button getAddTask() {
		return addTask;
	}

	public void setNewTask(boolean newTask) {
		this.newTask = newTask;
	}

	public boolean isNewTask() {
		return newTask;
	}

	public void setRemoveTask(Button removeTask) {
		this.removeTask = removeTask;
	}

	public Button getRemoveTask() {
		return removeTask;
	}

	public void setTaskViewer(TableViewer taskViewer) {
		this.taskViewer = taskViewer;
	}

	public TableViewer getTaskViewer() {
		return taskViewer;
	}

	public void setLastNum(int lastNum) {
		this.lastNum = lastNum;
	}

	public int getLastNum() {
		return lastNum;
	}
}