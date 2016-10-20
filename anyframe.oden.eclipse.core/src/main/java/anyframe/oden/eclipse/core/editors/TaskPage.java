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
package anyframe.oden.eclipse.core.editors;

import java.util.ArrayList;

import org.eclipse.jface.action.IMenuManager;
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
import org.json.JSONException;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenMessages;
import anyframe.oden.eclipse.core.alias.Agent;
import anyframe.oden.eclipse.core.brokers.OdenBroker;
import anyframe.oden.eclipse.core.editors.actions.DeleteTaskAction;
import anyframe.oden.eclipse.core.editors.actions.NewTaskAction;
import anyframe.oden.eclipse.core.editors.actions.RunDeployTaskAction;
import anyframe.oden.eclipse.core.editors.actions.SaveTaskAction;
import anyframe.oden.eclipse.core.editors.actions.TaskRefreshAction;
import anyframe.oden.eclipse.core.utils.Cmd;
import anyframe.oden.eclipse.core.utils.DialogUtil;
import anyframe.oden.eclipse.core.utils.ImageUtil;

public class TaskPage implements Page {
	//TaskPage
	private static TaskPage instance;
	private static final String HTTP_PROT = OdenMessages.ODEN_CommonMessages_ProtocolString_HTTP;

	private static Label task;
	private Label run;
	private static Label descrip;

	// text box
	public static Text taskNameText;
	public static Text descText;
	private Text filterText;

	// Table list
	private Table taskTable;
	public static TableViewer taskViewer;

	private Table RunTable;
	public static TableViewer runViewer;

	// Local Variables
	private FormToolkit toolkit;

	// Button
	public static Button addTask;
	public static Button removeTask;
	public static Button saveTask;
	public static Button runTask;
	public static Button runTask_;

	public static String shellUrl;

	public static int lastNum;
	public static ArrayList<String> originTask;
	public static boolean newTask;
	private ImageDescriptor _titleImageDescriptor = ImageUtil
	.getImageDescriptor("icons/form_banner.gif");
	private Image _TitleImage = ImageUtil.getImage(_titleImageDescriptor);

	TableViewerColumn taskName, desc, check, policy, description;

	private static final String MSG_TASK_SHOW = OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskShow;
	private static final String MSG_DETAIL_SHOW = OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskInfo;
	private static final String MSG_POLICY_SHOW = OdenMessages.ODEN_EDITORS_TaskPage_MsgPolicyInfo;

	private static final int leftWidth = 120;
	private static final int rightWidth = 150;
	private boolean taskNew;
	private Composite client;
	ViewerFilter filter = new ViewerFilter() {
		public boolean select(Viewer viewer1, Object parentElement,
				Object element) {
			String text = ".*" + filterText.getText() + ".*"; //$NON-NLS-1$ //$NON-NLS-2$
			if (text == null || text.length() == 0) {
				return true;
			}
			String details = ((TaskDetails) element).getTaskName();
			if (details.matches(text)) {
				return true;
			}
			return false;
		}
	};

	public static TaskPage getInstance() {
		if (instance == null) {
			instance = new TaskPage();
		}

		return instance;
	}

	public Composite getPage(final Composite parent) {
		// get Agent object

		Agent agent = OdenEditor.agent;
		shellUrl = HTTP_PROT + agent.getUrl() + "/shell"; 

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

//		loadInitPolicyData();
		// Event
		filterEvent();
		tableEvent();
		temporaryEvent();
		newTask = false;

		return form;
	}

	private void createHeadSection(final ScrolledForm form, FormToolkit toolkit) {
		Image titleImage =
			new Image(form.getDisplay(), getClass().getResourceAsStream(
					OdenMessages.ODEN_EDITORS_TaskPage_TaskPageTitleImage));
		form.setText(OdenMessages.ODEN_EDITORS_TaskPage_TaskPageTitle);
		form.setBackgroundImage(_TitleImage);
		form.setImage(titleImage);
		form.setMessage( OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageDesc , SWT.NONE);

		fillLocalToolBar(form.getForm().getToolBarManager() , form);
		form.getForm().getToolBarManager().update(true);
		//		fillLocalPullDown(form.getForm().getMenuManager());
		toolkit.decorateFormHeading(form.getForm());
	}

	private void fillLocalPullDown(IMenuManager menuManager) {
		menuManager.add(new TaskRefreshAction());
	}

	private void fillLocalToolBar(IToolBarManager toolBarManager , ScrolledForm form) {
		toolBarManager.add(new TaskRefreshAction());
	}

	private void createLeftSection(final ScrolledForm form, FormToolkit toolkit , Composite parent) {
		Composite client_left = toolkit.createComposite(parent);
		client_left.setLayout(new GridLayout());

		GridData gd = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);

		client_left.setLayoutData(gd);
		createAllTaskSection(form, toolkit,
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageSubSection1 , client_left);
	}

	private void createRightSection(final ScrolledForm form, FormToolkit toolkit , Composite parent) {
		client = toolkit.createComposite(parent , SWT.NONE);
		client.setVisible(false);
		client.setLayout(new GridLayout());

		GridData gd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);

		client.setLayoutData(gd);

		createTaskDetailSection(form, toolkit,
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageSubSection2,
				client);
		createRunTaskSection(form, toolkit,
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageRunSubTitle,
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

	private void createAllTaskSection(final ScrolledForm form, FormToolkit toolkit,
			String title , Composite parent ) {
		createSpacer(toolkit, form.getBody(), 2, 4);

		Section section = toolkit.createSection(parent ,Section.DESCRIPTION | Section.TITLE_BAR);

		section.setActiveToggleColor(toolkit.getHyperlinkGroup().getActiveForeground());
		section.setToggleColor(toolkit.getColors().getColor(
				FormColors.SEPARATOR));

		Composite client = toolkit.createComposite(section, SWT.WRAP);

		createSpacer(toolkit, client , 2, 1);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		client.setLayout(layout);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		client.setLayoutData(gd);
		//		gd.heightHint = 10;
		gd.widthHint = leftWidth;
		// type filter text
		filterText = new Text(client, SWT.LEFT | SWT.BORDER);
		gd.horizontalSpan = 2;

		filterText.setLayoutData(gd);
		filterText.setText("type filter text");
		
		createSpacer(toolkit, client , 2, 1);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);

		gridData.verticalSpan = 5;
		gridData.widthHint = 200;
		// Task Table
		taskTable = new Table(client, SWT.SINGLE | SWT.FULL_SELECTION
				| SWT.BORDER | SWT.V_SCROLL);
		taskViewer = new TableViewer(taskTable);
		taskViewer.setContentProvider(new PolicyContentProvider());

		taskTable.setHeaderVisible(true);
		taskTable.setLinesVisible(true);

		taskName = new TableViewerColumn(taskViewer, SWT.None);
		taskName.getColumn().setText(
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskTableCol1);
		taskName.getColumn().setWidth(150);
		taskName.setLabelProvider(new TaskNameColumnLabelProvider());

		desc = new TableViewerColumn(taskViewer, SWT.None);
		desc.getColumn().setText(
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskTableCol2);
		desc.getColumn().setWidth(250);
		desc.setLabelProvider(new TaskDescColumnLabelProvider());
		taskTable.setLayoutData(gridData);
		
		// task add Button
		addTask = toolkit.createButton(client,
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskAddBtn,
				SWT.PUSH);
		addTask.addListener(SWT.Selection, listener);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 120;
		addTask.setLayoutData(gd);

		// task remove Button
		removeTask = toolkit.createButton(client,
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskRemoveBtn,
				SWT.PUSH);
		removeTask.addListener(SWT.Selection, listener);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 120;
		removeTask.setLayoutData(gd);

		new Label(client, SWT.NONE);
		new Label(client, SWT.NONE);

		// task run Button
		Image imageRun =
			new Image(client.getDisplay(), getClass().getResourceAsStream(
					OdenMessages.ODEN_EDITORS_TaskPage_RunDeployImage));
		runTask = toolkit.createButton(client,
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskRunBtn,
				SWT.PUSH);
		runTask.setImage(imageRun);
		runTask.addListener(SWT.Selection, listener);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING );
		gd.widthHint = 120;
		runTask.setLayoutData(gd);

		section.setText(title);
		section
		.setDescription(OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageSubSection1Desc);
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

		// TODO Layout
		// Task
		createSpacer(toolkit, client , 6, 1);
		task = new Label(client, SWT.LEFT);
		task.setText(OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskNameLabel_Man);
		task.setForeground(new Color(Display.getCurrent(), 65, 105, 225));

		gd.horizontalSpan = 5;
		taskNameText = new Text(client, SWT.LEFT | SWT.BORDER);

		taskNameText.setLayoutData(gd);
		client.setLayoutData(gd);

		createSpacer(toolkit, client, 6, 1);

		// Description
		descrip = new Label(client, SWT.LEFT);
		descrip.setText(OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskDescLabel_Opt);
		descrip.setForeground(new Color(Display.getCurrent(), 65, 105, 225));

		descText = new Text(client, SWT.LEFT | SWT.BORDER);
		descText.setLayoutData(gd);

		// END Layout
		// Section title & Description
		section.setText(title);
		section
		.setDescription(OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageSubSection2Desc);
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

		createSpacer(toolkit, client, 6, 1);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 5;
		gd.verticalSpan = 2;
		gd.heightHint = 200;

		RunTable = new Table(client, SWT.SINGLE | SWT.FULL_SELECTION
				| SWT.BORDER | SWT.V_SCROLL | SWT.CHECK);
		runViewer = new TableViewer(RunTable);
		runViewer.setContentProvider(new PolicyContentProvider());

		RunTable.setHeaderVisible(true);
		RunTable.setLinesVisible(true);

		policy = new TableViewerColumn(runViewer, SWT.None);
		policy.getColumn().setText(
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPagePolicyCol1);
		policy.getColumn().setWidth(200);
		policy.setLabelProvider(new PolicyNameColumnLabelProvider());

		description = new TableViewerColumn(runViewer, SWT.None);
		description.getColumn().setText(
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPagePolicyCol2);
		description.getColumn().setWidth(150);
		description.setLabelProvider(new PolicyDescColumnLabelProvider());
		RunTable.setLayoutData(gd);

		// Save Button
		createSpacer(toolkit, client, 6, 4);

		gd = new GridData();
		gd.widthHint = 120;
		saveTask = toolkit.createButton(client,
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskSaveBtn,
				SWT.PUSH);
		saveTask.addListener(SWT.Selection, listener);
		saveTask.setLayoutData(gd);

		new Label(client , SWT.NONE);

		Image imageRun =
			new Image(parent.getDisplay(), getClass().getResourceAsStream(
					OdenMessages.ODEN_EDITORS_TaskPage_RunDeployImage));
		runTask_ = toolkit.createButton(client,
				OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskRunBtn,
				SWT.PUSH);
		runTask_.setImage(imageRun);
		runTask_.addListener(SWT.Selection, listener);
		runTask_.setLayoutData(gd);

		// END Layout
		// Section title & Description
		section.setText(title);
		section.setDescription(OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageSubTitle);
		section.setClient(client);
		section.setExpanded(true);

		gd = new GridData(GridData.FILL_BOTH
				| GridData.VERTICAL_ALIGN_BEGINNING);
		section.setLayoutData(gd);



	}

	private void chageLabel() {
		// change Label
		task
		.setText(OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskNameLabel_Opt);
//		descrip
//		.setText(OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskDescLabel_Opt);
	}

	public static void chageMandaLabel() {
		// change Label
		task.setText(OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskNameLabel_Man); 
		descrip.setText(OdenMessages.ODEN_EDITORS_TaskPage_MsgTaskPageTaskDescLabel_Opt); 
	}

	public static void loadInitData()  {
		String commnd = ""; 
		String result = ""; 
		TaskDetails details = null;
		ArrayList taskList = new ArrayList();
		originTask = new ArrayList<String>();
		commnd = MSG_TASK_SHOW;
		int index = 0;
		int selectIndex = 0;
		try {	
			result = OdenBroker.sendRequest(shellUrl, commnd);
			if (result != null ) {
				JSONArray array = new JSONArray(result);

				for (int i = 0; i < array.length(); i++) {
					Object o = ((JSONObject) array.get(i)).keys().next();
					if(!(o.toString().equals("KnownException"))){ // no data
						Cmd result_ = new Cmd("foo", "fooAction \""  //$NON-NLS-2$
								+ o.toString()
								+ "\" " 
								+ (String) ((JSONObject) array.get(i)).get(
										o.toString()).toString());
						String name = o.toString();

						String desc = result_.getOptionArg(new String[] { "desc" }); 
						if(!(name.matches(OdenMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_TempTaskName+".*"))){
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
				taskViewer.setInput(taskList);
				lastNum = taskViewer.getTable().getItemCount() ;
				taskViewer.getTable().select(selectIndex - 1);
			}
		} catch (OdenException odenException) {
		} catch (Exception e) {
			OdenActivator.error("Exception occured while loading Initial info.",e);
		}
	}

	public static void loadInitPolicyData() {
		String commnd = ""; 
		String result = ""; 
		TaskDetails details = null;
		ArrayList policyList = new ArrayList();
		commnd = MSG_POLICY_SHOW;
		try {
			result = OdenBroker.sendRequest(shellUrl, commnd);
			if (result != null) {
				JSONArray array = new JSONArray(result);

				for (int i = 0; i < array.length(); i++) {
					Object o = ((JSONObject) array.get(i)).keys().next();
					Cmd result_ = new Cmd("foo", "fooAction \""  //$NON-NLS-2$
							+ o.toString()
							+ "\" " 
							+ (String) ((JSONObject) array.get(i)).get(
									o.toString()).toString());
					String name = o.toString();
					String desc = result_.getOptionArg(new String[] { "desc" }); 
					if(!(name.matches(OdenMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_TempPolicyName+".*"))){
						details = new TaskDetails(null, null, name, desc);
						policyList.add(details);
					}
				}

				runViewer.setInput(policyList);
			}
		} catch (OdenException odenException) {
//			OdenActivator.error("Exception occured while loading policy info.",odenException);
//			odenException.printStackTrace();
		} catch (Exception e) {
			OdenActivator.error("Exception occured while loading policy info.", e);
//			e.printStackTrace();
		}
		
	}

	private Listener listener = new Listener() {
		public void handleEvent(Event event) {
			TaskDetails details = null;
			ISelection selection = taskViewer.getSelection();
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			details = (TaskDetails) obj;
			if (event.widget == removeTask) {
				// validation check(remove)
				if (checkRemove(obj)) {
					if (DialogUtil.confirmMessageDialog(OdenMessages.ODEN_CommonMessages_Title_ConfirmDelete,
							OdenMessages.ODEN_EDITORS_TaskPage_DialogMsg_ConfirmDeleteTask_MessagePre + details.getTaskName() + OdenMessages.ODEN_CommonMessages_Confirm_MessageSuf)) { 
						new DeleteTaskAction().run();
						client.setVisible(false);
					}
				} else {
					DialogUtil
					.openMessageDialog(
							OdenMessages.ODEN_CommonMessages_Title_Warning,
							OdenMessages.ODEN_CommonMessages_SelectItemFirst,  //$NON-NLS-2$
							MessageDialog.WARNING);
				}
			} else if (event.widget == saveTask) {
				// validation check(save)
				if (checkSave())
					new SaveTaskAction().run();

			} else if (event.widget == addTask) {
				// validation check(add)
				client.setVisible(true);
				new NewTaskAction().run();
			} else if (event.widget == runTask || event.widget == runTask_ ) {
				if (checkDeploy(obj)) {
					if(dupTaskCheck())
						new RunDeployTaskAction().run();
					else
						DialogUtil.openMessageDialog(OdenMessages.ODEN_CommonMessages_Title_Warning,OdenMessages.ODEN_EDITORS_TaskPage_DialogMsg_FirstSave,
								MessageDialog.WARNING);
				} else {
					DialogUtil.openMessageDialog(OdenMessages.ODEN_CommonMessages_Title_Warning, OdenMessages.ODEN_CommonMessages_SelectItemFirst, 
							MessageDialog.WARNING);
				}
			}
		}
	};

	public void showTaskDetail(String taskName) {
		String commnd = ""; 
		String result = ""; 
		String desc = "";
		String[] descs = null;
		String policies = ""; 
		String[] policy = null;
		PolicyDetails details = new PolicyDetails();
		ArrayList deployList = new ArrayList();
		commnd = MSG_DETAIL_SHOW + " " + '"' + taskName + '"' + " " + "-json";   
		try {
			result = OdenBroker.sendRequest(shellUrl, commnd);
			JSONArray array = new JSONArray(result);

			for (int i = 0; i < array.length(); i++) {
				Object o = ((JSONObject) array.get(i)).keys().next();
				Cmd result_ = new Cmd("foo", "fooAction \""  
						+ o.toString()
						+ "\" " 
						+ (String) ((JSONObject) array.get(i))
						.get(o.toString()).toString());
				desc = result_.getOptionArg(new String[] { "desc" });
//				policies = result_.getOptionArg(new String[] { "p", "policy" });  
				policy = result_.getOptionArgArray(new String[] { "p", "policy" });  
			}

			for (int i = 0; i < policy.length; i++) {
				policies = policies + policy[i];
			}
			// clear text Filed
			clearText();
			taskNameText.setText(taskName);
			descText.setText(desc);
			checkRuntable(policies);

		} catch (OdenException e) {
//			OdenActivator.error("Exception occured while loading deailed task info.",e);
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

	// TODO : Validation
	private Boolean checkSave() {
		if (taskNameText.getText().equals("")) {
			DialogUtil.openMessageDialog(
					OdenMessages.ODEN_CommonMessages_Title_Warning,
					OdenMessages.ODEN_EDITORS_TaskPage_DialogMsg_InputTaskName, MessageDialog.INFORMATION);
			taskNameText.setFocus();
			return false;

		} else if (taskNameText.getEnabled() && dupTaskCheck()) {
			DialogUtil.openMessageDialog(OdenMessages.ODEN_CommonMessages_Title_Warning,
					OdenMessages.ODEN_CommonMessages_NameAlreadyExists,
					MessageDialog.WARNING);
			taskNameText.setFocus();
			return false;
		} else if (taskNameText.getText().equals(OdenMessages.ODEN_EDITORS_TaskPage_TempTaskName)) {
			// check temporary taskName
			DialogUtil.openMessageDialog(OdenMessages.ODEN_CommonMessages_Title_Warning,
					OdenMessages.ODEN_CommonMessages_NameShouldBeSpecified + '"' + OdenMessages.ODEN_EDITORS_TaskPage_TempTaskName + '"',
					MessageDialog.WARNING);
			taskNameText.setFocus();
			return false;
		}
		// check policy
		TableItem[] tia = runViewer.getTable().getItems();
		String policies = "";
		for (int i = 0; i < tia.length; i++) {
			if (tia[i].getChecked()) {
				policies = policies + tia[i].getText(0) + ",";
			}
		}
		if (policies.equals("")) {
			DialogUtil.openMessageDialog(
					OdenMessages.ODEN_CommonMessages_Title_Warning,
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

	// TODO : Parsing Result String
	public static String getOptionArgs(String full, String optionName) {
		// remove '-' from option name
		String _opt = optionName.startsWith("-") ? optionName.substring(1)
				: optionName;

		String[] options = full.split("^-| -");
		for (String option : options) {
			if (option.startsWith(_opt)) {
				int idx = _opt.length();
				if (option.length() > idx + 1
						&& Character.isWhitespace(option.charAt(idx))
						&& !Character.isWhitespace(option.charAt(idx + 1)))
					return option.substring(idx + 1);
				else
					return "";
			}
		}
		return null;
	}

	public static void clearText() {
		taskNameText.setText("");
		descText.setText("");
		runViewer.getTable().clearAll();

	}

	private void checkRuntable(String ckPolicy) {
		loadInitPolicyData();

		TableItem[] tia = runViewer.getTable().getItems();
		String policies = "";
		for (int i = 0; i < tia.length; i++) {
			if (ckPolicy.matches(".*" + tia[i].getText(0) + ".*")) { 
				tia[i].setChecked(true);
			}
		}
	}

	// TODO : Event List
	private void filterEvent() {
		// filter Text Event
		filterText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {

			}

			public void keyReleased(KeyEvent ke) {
				// filter.setSearchText(filterText.getText());
				TaskPage.this.taskViewer.addFilter(TaskPage.this.filter);
				taskViewer.refresh();

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

	private void tableEvent() {
		// task table viewer event
		taskViewer.getTable().addMouseListener(new MouseAdapter() {

			public void mouseDown(MouseEvent e) {
				if (taskViewer.getTable().getItem(new Point(e.x, e.y)) != null) {
					if (chkNewTask()) {
						// add task
						clearText();
						TaskDetails details = null;
						ISelection selection = taskViewer.getSelection();
						Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
						details = (TaskDetails) obj;
						taskNameText.setText(details.getTaskName());
						descText.setText(details.getDescription());
						taskNameText.setEnabled(true);
						addTask.setEnabled(false);
						loadInitPolicyData();

					} else {
						taskNameText.setEnabled(false);
						removeTask.setEnabled(true);
						// change Label
						chageLabel();
						TaskDetails details = new TaskDetails();
						ISelection selection = taskViewer.getSelection();
						Object obj = ((IStructuredSelection) selection).getFirstElement();
						details = (TaskDetails) obj;
						client.setVisible(true);
						showTaskDetail(details.getTaskName());
						if(chkNewTaskExist())
							addTask.setEnabled(false);
						else
							addTask.setEnabled(true);
					}
				}
			}
		});

	}
	public static boolean chkNewTaskExist() {
		int count = taskViewer.getTable().getItemCount();
		int originCount = originTask.size();
		if(count == originCount)
			return false;

		return true;
	}
	public static boolean chkNewTask() {
		int selected= taskViewer.getTable().getSelectionIndex();
		if(selected != lastNum)
			return false;
		return true;
	}

	private void temporaryEvent() {
		// taskName , Desc Event
		taskNameText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {
			}
			public void keyReleased(KeyEvent ke) {
				tempProcess();
			}
		});
		descText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {
			}
			public void keyReleased(KeyEvent ke) {
				tempProcess();
			}
		});
	}

	private void tempProcess () {
		ISelection selection = taskViewer.getSelection();

		if(chkNewTask()) {
			removeTempcell();
			inputTempcell(lastNum);
		} else {
			int selected= taskViewer.getTable().getSelectionIndex();
			removeTempcell();
			inputTempcell(selected);
			taskViewer.getTable().remove(selected + 1);
			addTask.setEnabled(true);
		}

	}
	public static void removeTempcell() {
		TaskDetails details = null;
		ISelection selection = taskViewer.getSelection();

		details = (TaskDetails) ((IStructuredSelection) selection)
		.getFirstElement();
		if (details != null) {
			taskViewer.remove(details);
		}
		taskViewer.refresh();
	}
	private void inputTempcell( int num) {
		String tempTaskName = taskNameText.getText();
		String tempTaskDesc = descText.getText();

		TaskDetails details = null;
		details = new TaskDetails(tempTaskName, tempTaskDesc,  null, null);
		taskViewer.insert(details, num );
		taskViewer.getTable().select(num );

	}

	private Boolean dupTaskCheck() {
		String commnd = ""; 
		String result = ""; 
		commnd = MSG_TASK_SHOW;
		try {	
			result = OdenBroker.sendRequest(shellUrl, commnd);
			if (result != null ) {
				JSONArray array = new JSONArray(result);

				for (int i = 0; i < array.length(); i++) {
					Object o = ((JSONObject) array.get(i)).keys().next();
					if(!(o.toString().equals("KnownException"))){ // no data
						Cmd result_ = new Cmd("foo", "fooAction \""  //$NON-NLS-2$
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


}