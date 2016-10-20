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
import java.util.Collection;

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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.alias.Repository;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.editors.actions.DeleteDeployAction;
import anyframe.oden.eclipse.core.editors.actions.DeletePolicyAction;
import anyframe.oden.eclipse.core.editors.actions.NewDeployAction;
import anyframe.oden.eclipse.core.editors.actions.NewPolicyAction;
import anyframe.oden.eclipse.core.editors.actions.PolicyRefreshAction;
import anyframe.oden.eclipse.core.editors.actions.SavePolicyAction;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.Cmd;
import anyframe.oden.eclipse.core.utils.DialogUtil;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Implement Oden PolicyPage. This class implement
 * Page class.
 * 
 * @author HONG JungHwan
 * @version 1.0.0 RC2
 * 
 */
public class PolicyPage implements Page {

	private static PolicyPage instance;

	private ImageDescriptor _titleImageDescriptor = ImageUtil
	.getImageDescriptor("icons/form_banner.gif");
	private Image _TitleImage = ImageUtil.getImage(_titleImageDescriptor);

	private FormToolkit toolkit;

	private Table policyTable;
	private Table DeployTable;
	private TableViewer policyViewer;
	private TableViewer deployViewer;

	TableViewerColumn policyName, desc, targetUri, targetRoot, locvar;
	// Command line
	private final String MSG_POLICY_SHOW = CommandMessages.ODEN_EDITORS_PolicyPage_MsgPolicyShow;
	private final String MSG_DETAIL_SHOW = CommandMessages.ODEN_EDITORS_PolicyPage_MsgDetailShow;
	private String noUsernameRequiredName = UIMessages.ODEN_EXPLORER_Dialogs_UserNameBooleanString;
	private String shellUrl;
	private Server server;

	private final String HTTP_PROT = "http://";

	private Button addPolicy;
	private Button removePolicy;
	private Button savePolicy;
	private Button addDeploy;
	private Button removeDeploy;
	private Button noUsernameRequired;
	private Button updateOptionRequired;

	// Label
	private Label policy;
	private Label repo;
	private Label repoPath;
	private Label include;
	private Label description;
	private Label userName;
	private Label userPassword;

	// Text Box
	private Text policyNameText;
	private StyledText includeText;
	private StyledText excludeText;
	//	private Text urlText;
	private Text filterText;
	private Text buildRepoUriText;
	private Text buildRepoRootText;
	private Text descriptionText;
	private Text userField;
	private Text passwordField;

	// Combo box
	private Combo repoNickname;
	private Combo repoKind;
	private ArrayList<String> originPolicy;

	private int lastNum;

	private final int leftWidth = 200;
	private final int rightWidth = 150;
	private Composite client;
	public Composite getClient() {
		return client;
	}

	//section
	private Section repo_section;
	private Section agent_section;
	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();
	private String serverName;

	ViewerFilter filter = new ViewerFilter() {
		public boolean select(Viewer viewer1, Object parentElement,
				Object element) {
			String text = ".*" + filterText.getText() + ".*";
			if (text == null || text.length() == 0) {
				return true;
			}
			String details = ((PolicyDetails) element).getPolicyName();
			if (details.matches(text)) {
				return true;
			}
			return false;
		}
	};

	public static PolicyPage getInstance() {
		instance = new PolicyPage();
		return instance;
	}

	public Composite getPage(final Composite parent) {
		//		get Server object
		server = OdenEditor.server;
		serverName = server.getNickname();

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

		createLeftSection(form, toolkit, whole);

		createRightSection(form, toolkit , whole);

		setShellUrl(HTTP_PROT + server.getUrl() + "/shell");

		loadInitData(getShellUrl());

		// Event
		filterEvent();
		comboEvent();
		tableEvent();
		temporaryEvent();
		client.setVisible(false);

		return form;
	}

	private void createLeftSection(final ScrolledForm form, FormToolkit toolkit , Composite parent) {
		Composite client_left = toolkit.createComposite(parent);
		client_left.setLayout(new GridLayout());

		GridData gd = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);

		client_left.setLayoutData(gd);
		createAllPolicySection(form, toolkit,
				UIMessages.ODEN_EDITORS_PolicyPage_Section , client_left);
	}

	private void createSpacer(FormToolkit toolkit, Composite parent, int span,
			int height) {
		Label spacer = toolkit.createLabel(parent, "");
		GridData gd = new GridData();
		gd.heightHint = height;
		gd.horizontalSpan = span;
		spacer.setLayoutData(gd);
	}

	private void createHeadSection(final ScrolledForm form, FormToolkit toolkit) {
		Image titleImage =
			new Image(form.getDisplay(), getClass().getResourceAsStream(
					UIMessages.ODEN_EDITORS_PolicyPage_TitleImage));
		form.setText(UIMessages.ODEN_EDITORS_PolicyPage_Title);
		form.setBackgroundImage(_TitleImage);
		form.setImage(titleImage);
		form.setMessage(UIMessages.ODEN_EDITORS_PolicyPage_Desc , SWT.NONE);

		fillLocalToolBar(form.getForm().getToolBarManager());
		form.getForm().getToolBarManager().update(true);
		//		fillLocalPullDown(form.getForm().getMenuManager());
		toolkit.decorateFormHeading(form.getForm());

	}

	private void fillLocalToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(new PolicyRefreshAction(serverName));
	}

	private void createRightSection(final ScrolledForm form, FormToolkit toolkit , Composite parent) {

		client = toolkit.createComposite(parent , SWT.NONE);
		client.setLayout(new GridLayout());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);

		client.setLayoutData(gd);
		createDetailPolicySection(form, toolkit,UIMessages.ODEN_EDITORS_PolicyPage_PolicyLabel , client);

		createRepoSection(form, toolkit,UIMessages.ODEN_EDITORS_PolicyPage_RepositoryLabel , client);

		createDepolySection(form, toolkit,UIMessages.ODEN_EDITORS_PolicyPage_DeployLabel , client);

		createSaveButtonSection(form, toolkit , client);

	}

	private void createAllPolicySection(final ScrolledForm form, FormToolkit toolkit,
			String title , Composite parent ) {
		createSpacer(toolkit, form.getBody(), 2, 4);
		Section section = toolkit.createSection(parent,
				Section.DESCRIPTION | Section.TITLE_BAR);
		section.setActiveToggleColor(toolkit.getHyperlinkGroup()
				.getActiveForeground());
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

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

		gridData.verticalSpan = 2;
		gridData.widthHint = 250;
		gridData.heightHint = 200;
		// Policy Table
		policyTable = new Table(client, SWT.SINGLE | SWT.FULL_SELECTION
				| SWT.BORDER | SWT.V_SCROLL);
		setPolicyViewer(new TableViewer(policyTable));
		getPolicyViewer().setContentProvider(new PolicyContentProvider());

		policyTable.setHeaderVisible(true);
		policyTable.setLinesVisible(true);

		policyName = new TableViewerColumn(getPolicyViewer(), SWT.None);
		policyName.getColumn().setText(
				UIMessages.ODEN_EDITORS_PolicyPage_PolicyTable_col1);
		policyName.getColumn().setWidth(150);
		policyName.setLabelProvider(new PolicyNameColumnLabelProvider());

		desc = new TableViewerColumn(getPolicyViewer(), SWT.None);
		desc.getColumn().setText(
				UIMessages.ODEN_EDITORS_PolicyPage_PolicyTable_col2);
		desc.getColumn().setWidth(250);
		desc.setLabelProvider(new PolicyDescColumnLabelProvider());
		policyTable.setLayoutData(gridData);

		// policy add Button
		setAddPolicy(toolkit.createButton(client,
				UIMessages.ODEN_EDITORS_PolicyPage_PolicyAdd_Btn, SWT.PUSH));
		getAddPolicy().addListener(SWT.Selection, listener);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 90;
		getAddPolicy().setLayoutData(gd);

		// policy remove Button
		setRemovePolicy(toolkit.createButton(client,
				UIMessages.ODEN_EDITORS_PolicyPage_PolicyRemove_Btn, SWT.PUSH));
		getRemovePolicy().addListener(SWT.Selection, listener);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 90;
		getRemovePolicy().setLayoutData(gd);

		section.setText(title);
		section.setDescription(UIMessages.ODEN_EDITORS_PolicyPage_SectionDesc);
		section.setClient(client);
		section.setExpanded(true);

		gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);
	}

	private void createDetailPolicySection(final ScrolledForm form,
			FormToolkit toolkit, String title , Composite parent) {

		Section section = toolkit.createSection(parent,
				Section.DESCRIPTION | Section.TITLE_BAR);

		Composite client = toolkit.createComposite(section, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 6;
		layout.marginHeight = 10;
		layout.marginWidth = 10;

		client.setLayout(layout);

		// policy
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 15;
		gd.widthHint = rightWidth;

		client.setLayoutData(gd);

		createSpacer(toolkit, client , 6, 1);
		policy = new Label(client, SWT.LEFT);
		policy.setText(UIMessages.ODEN_EDITORS_PolicyPage_Man_PolicyName );
		policy.setForeground(new Color(Display.getCurrent(), 65, 105, 225));

		gd.horizontalSpan = 5;
		setPolicyNameText(new Text(client, SWT.LEFT | SWT.BORDER));

		getPolicyNameText().setLayoutData(gd);
		client.setLayoutData(gd);

		createSpacer(toolkit, client, 6, 1);

		// Description
		description = new Label(client, SWT.LEFT);
		description.setText(UIMessages.ODEN_EDITORS_PolicyPage_Opt_PolicyDesc);
		description
		.setForeground(new Color(Display.getCurrent(), 65, 105, 225));

		setDescriptionText(new Text(client, SWT.LEFT | SWT.BORDER));
		getDescriptionText().setLayoutData(gd);

		createSpacer(toolkit, client, 6, 1);
		// item include
		include = new Label(client, SWT.LEFT);
		include.setText(UIMessages.ODEN_EDITORS_PolicyPage_Man_ItemInclude + " ");
		include.setForeground(new Color(Display.getCurrent(), 65, 105, 225));

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 35;
		gd.horizontalSpan = 5;

		includeText = new StyledText(client, SWT.LEFT | SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		includeText.setLayoutData(gd);

		createSpacer(toolkit, client, 6, 1);

		// item exclude
		Label exclude = new Label(client, SWT.LEFT);
		exclude.setText(UIMessages.ODEN_EDITORS_PolicyPage_ItemExclude);
		exclude.setForeground(new Color(Display.getCurrent(), 65, 105, 225));

		setExcludeText(new StyledText(client, SWT.LEFT | SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL));
		getExcludeText().setLayoutData(gd);

		new Label(client,SWT.NONE);
		setUpdateOptionRequired(new Button(client, SWT.CHECK));
		getUpdateOptionRequired().setText(UIMessages.ODEN_EDITORS_PolicyPage_Update_Option);
		getUpdateOptionRequired().setLayoutData(gd);


		// Section title & Description
		section.setText(title);
		section
		.setDescription(UIMessages.ODEN_EDITORS_PolicyPage_PolicyLabelDesc);
		section.setClient(client);

		gd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		section.setLayoutData(gd);

	}

	private void createRepoSection(final ScrolledForm form, FormToolkit toolkit,
			String title , Composite parent) {
		repo_section = toolkit.createSection(parent, Section.TWISTIE
				| Section.DESCRIPTION | Section.TITLE_BAR);
		repo_section.setActiveToggleColor(toolkit.getHyperlinkGroup()
				.getActiveForeground());
		repo_section.setToggleColor(toolkit.getColors().getColor(
				FormColors.SEPARATOR));
		Composite client = toolkit.createComposite(repo_section, SWT.WRAP);

		GridLayout layout = new GridLayout();
		layout.numColumns = 6;
		layout.marginHeight = 10;
		layout.marginWidth = 10;

		client.setLayout(layout);

		// Build Repository
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 10;
		gd.widthHint = rightWidth;

		client.setLayoutData(gd);
		createSpacer(toolkit, client , 6, 1);
		repo = new Label(client, SWT.LEFT);
		repo.setText(UIMessages.ODEN_EDITORS_PolicyPage_Man_BuildRepo );

		repo.setForeground(new Color(Display.getCurrent(), 65, 105, 225));

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;

		repoNickname = new Combo(client,  SWT.BORDER | SWT.DROP_DOWN | SWT.LEFT );

		repoNickname.setLayoutData(gd);

		gd = new GridData();
		gd.horizontalSpan = 3;
		Label label = new Label(client, SWT.NONE);
		label.setText("");

		// Build Repository new line
		createSpacer(toolkit, client, 6, 1);
		gd = new GridData();
		label = new Label(client, SWT.NONE);
		label.setText("");
		label.setLayoutData(gd);

		gd = new GridData();
		gd.horizontalSpan = 1;
		setRepoKind(new Combo(client, SWT.SINGLE | SWT.BORDER | SWT.DROP_DOWN | SWT.LEFT));
		getRepoKind().setLayoutData(gd);
		getRepoKind().setEnabled(false);
		getRepoKind().setToolTipText(UIMessages.ODEN_EDITORS_PolicyPage_BuildRepositoryProtocol_Tooltip);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 15;
		gd.horizontalSpan = 4;
		setBuildRepoUriText(new Text(client, SWT.SINGLE | SWT.BORDER | SWT.LEFT));
		getBuildRepoUriText().setLayoutData(gd);
		getBuildRepoUriText().setEnabled(false);
		getBuildRepoUriText().setToolTipText(UIMessages.ODEN_EDITORS_PolicyPage_BuildRepositoryUrl_Tooltip);

		String[] protocolSet = OdenActivator.getDefault().getAliasManager()
		.getRepositoryManager().getProtocolSet();
		for (String protocol : protocolSet) {
			getRepoKind().add(protocol);
		}

		createSpacer(toolkit, client, 6, 1);
		// Build Repository Path
		gd = new GridData();
		repoPath = new Label(client, SWT.LEFT | SWT.WRAP);
		repoPath.setText(UIMessages.ODEN_EDITORS_PolicyPage_Man_BuildRoot);
		repoPath.setForeground(new Color(Display.getCurrent(), 65, 105, 225));
		repoPath.setEnabled(false);
		repoPath.setLayoutData(gd);

		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 5;
		gd.heightHint = 20;
		setBuildRepoRootText(new Text(client, SWT.SINGLE | SWT.BORDER));
		getBuildRepoRootText().setLayoutData(gd);
		getBuildRepoRootText().setEnabled(false);

		createSpacer(toolkit, client, 6, 1);

		new Label(client, SWT.NONE);

		// noUserName Required
		setNoUsernameRequired(new Button(client, SWT.CHECK));
		getNoUsernameRequired().setText(noUsernameRequiredName);
		getNoUsernameRequired().setLayoutData(gd);
		getNoUsernameRequired().setEnabled(false);
		createSpacer(toolkit, client, 6, 1);
		gd = new GridData();
		// User name
		userName = new Label(client, SWT.LEFT);
		userName.setText(UIMessages.ODEN_EXPLORER_Dialogs_UserNameFieldName
				+ " : ");
		userName.setForeground(new Color(Display.getCurrent(), 65, 105, 225));
		userName.setEnabled(false);
		userName.setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		setUserField(new Text(client, SWT.SINGLE | SWT.BORDER));
		getUserField().setLayoutData(gd);
		getUserField().setEnabled(false);
		// password field
		gd = new GridData();
		gd.horizontalSpan = 1;
		userPassword = new Label(client, SWT.CENTER);
		userPassword
		.setText("    " + UIMessages.ODEN_EXPLORER_Dialogs_PasswordFieldName
				+ " : ");
		userPassword
		.setForeground(new Color(Display.getCurrent(), 65, 105, 225));
		userPassword.setEnabled(false);
		userPassword.setLayoutData(gd);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		setPasswordField(new Text(client, SWT.SINGLE | SWT.BORDER));
		getPasswordField().setLayoutData(gd);
		getPasswordField().setEchoChar('*');
		getPasswordField().setEnabled(false);

		client.setLayoutData(gd);

		repo_section.setText(title);
		repo_section
		.setDescription(UIMessages.ODEN_EDITORS_PolicyPage_RepositoryLabelDesc);
		repo_section.setClient(client);
		//		section.setExpanded(true);
		repo_section.setExpanded(false);
		repo_section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {

			}
		});
		gd = new GridData(GridData.FILL_BOTH
				| GridData.VERTICAL_ALIGN_BEGINNING);
		repo_section.setLayoutData(gd);

		// check if "user name is required ..." check box is checked or not
		getNoUsernameRequired().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				boolean checked = getNoUsernameRequired().getSelection();
				getUserField().setEnabled(!checked);
				getPasswordField().setEnabled(!checked);
				if (checked) {
					getUserField().setText("anonymous");
					getPasswordField().setText("");
				}
			}
		});
	}

	private void createDepolySection(final ScrolledForm form, FormToolkit toolkit,
			String title , Composite parent) {
		agent_section = toolkit.createSection(parent, Section.TWISTIE
				| Section.DESCRIPTION | Section.TITLE_BAR);
		agent_section.setActiveToggleColor(toolkit.getHyperlinkGroup()
				.getActiveForeground());
		agent_section.setToggleColor(toolkit.getColors().getColor(
				FormColors.SEPARATOR));

		Composite client = toolkit.createComposite(agent_section, SWT.WRAP);

		GridLayout layout = new GridLayout();
		layout.numColumns = 6;
		layout.marginHeight = 10;
		layout.marginWidth = 10;

		client.setLayout(layout);

		// Build Repository
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 15;
		gd.widthHint = rightWidth;

		client.setLayoutData(gd);

		createSpacer(toolkit, client, 6, 1);

		// deploy table
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 5;
		gd.verticalSpan = 2;
		gd.heightHint = 80;
		DeployTable = new Table(client, SWT.SINGLE | SWT.FULL_SELECTION
				| SWT.BORDER | SWT.V_SCROLL);
		setDeployViewer(new TableViewer(DeployTable));
		getDeployViewer().setContentProvider(new PolicyContentProvider());

		DeployTable.setHeaderVisible(true);
		DeployTable.setLinesVisible(true);

		targetUri = new TableViewerColumn(getDeployViewer(), SWT.None);
		targetUri.getColumn().setText(
				UIMessages.ODEN_EDITORS_PolicyPage_DeployTable_col1);
		targetUri.getColumn().setWidth(150);
		targetUri.setLabelProvider(new DeployUriColumnLabelProvider());

		locvar = new TableViewerColumn(getDeployViewer(), SWT.None);
		locvar.getColumn().setText(
				UIMessages.ODEN_EDITORS_PolicyPage_DeployTable_col2);
		locvar.getColumn().setWidth(150);
		locvar.setLabelProvider(new LocVarColumnLabelProvider());
		DeployTable.setLayoutData(gd);

		// add & remove Deploy
		addDeploy = toolkit.createButton(client, "Add...", SWT.PUSH);
		addDeploy.addListener(SWT.Selection, listener);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 90;
		addDeploy.setLayoutData(gd);

		removeDeploy = toolkit.createButton(client, "Remove", SWT.PUSH);
		removeDeploy.addListener(SWT.Selection, listener);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 90;
		removeDeploy.setLayoutData(gd);

		agent_section.setText(title);
		agent_section
		.setDescription(UIMessages.ODEN_EDITORS_PolicyPage_DeployLabelDesc);
		agent_section.setClient(client);
		agent_section.setExpanded(false);
		agent_section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {

			}
		});
		gd = new GridData(GridData.FILL_BOTH);
		agent_section.setLayoutData(gd);

	}

	private void createSaveButtonSection(final ScrolledForm form, FormToolkit toolkit , Composite parent) {
		Composite client = toolkit.createComposite(parent, SWT.TOP);

		GridLayout layout = new GridLayout();
		layout.numColumns = 6;

		client.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 15;
		gd.widthHint = rightWidth;

		gd = new GridData();
		gd.widthHint = 120; 
		savePolicy = toolkit.createButton(client,
				UIMessages.ODEN_EDITORS_PolicyPage_PolicySave_Btn, SWT.PUSH);
		savePolicy.addListener(SWT.Selection, listener);
		savePolicy.setLayoutData(gd);

	}
	// table Label Provider
	private class PolicyNameColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((PolicyDetails) element).getPolicyName();
		}
	}

	private class PolicyDescColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((PolicyDetails) element).getDescription();
		}
	}

	private class DeployUriColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((PolicyDetails) element).getDeployUrl();
		}
	}

	private class LocVarColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((PolicyDetails) element).getLocationVar();
		}
	}
	
	/**
	 * policy data loading
	 */
	public void loadInitData(String url) {
		String commnd = "";
		String result = "";
		PolicyDetails details = null;
		ArrayList policyList = new ArrayList();
		int index = 0;
		int selectIndex = 0;
		originPolicy = new ArrayList<String>();
		commnd = MSG_POLICY_SHOW;
		try {
			result = OdenBroker.sendRequest(url, commnd);
			if(result != null){
				JSONArray array = new JSONArray(result);

				for (int i = 0; i < array.length(); i++) {
					Object o = ((JSONObject) array.get(i)).keys().next();
					if(!(o.toString().equals("KnownException"))){ // no data
						Cmd result_ = new Cmd("foo", "fooAction \""
								+ o.toString()
								+ "\" "
								+ (String) ((JSONObject) array.get(i))
								.get(o.toString()).toString());
						String name = o.toString();
						String desc = result_.getOptionArg(new String[] { "desc" });
						if(!(name.matches(CommandMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_TempPolicyName+".*"))){
							++index;
							details = new PolicyDetails(name, desc, null, null, null);
							policyList.add(details);
							originPolicy.add(name);
							if(name.equals(policyNameText.getText())){
								selectIndex = index;
							}
						}
					}
				}
				getPolicyViewer().setInput(policyList);
				setLastNum(getPolicyViewer().getTable().getItemCount());
				policyViewer.getTable().select(selectIndex - 1);
			}
		} catch (OdenException e) {

		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while loading policy data.",odenException);
		} finally {
			getRepo();
		}
	}
	/**
	 * policy detail data loading
	 */
	public void showPolicyDetail(String policyName) {
		String commnd = "";
		String result = "";
		PolicyDetails details = null;
		ArrayList deployList = new ArrayList();

		commnd = MSG_DETAIL_SHOW + " " + '"' + policyName + '"' + " " + "-json";
		try {
			result = OdenBroker.sendRequest(getShellUrl(), commnd);
			JSONArray array = new JSONArray(result);
			Object o = ((JSONObject) array.get(0)).keys().next();
			Cmd result_ = new Cmd("foo", "fooAction \""
					+ o.toString()
					+ "\" "
					+ (String) ((JSONObject) array.get(0)).get(o.toString())
					.toString());

			String[] repo = result_.getOptionArgArray(new String[] { "r",
			"repo" });

			String[] dest = result_.getOptionArgArray(new String[] { "d",
			"dest" });

			String desc = result_.getOptionArg(new String[] { "desc" });
			String[] include = result_.getOptionArgArray(new String[] { "i", "include" });
			String[] exclude = result_.getOptionArgArray(new String[] { "e", "exclude" });

			Boolean update = result.indexOf("-u") > 0 ? true : false;

			// clear text Filed
			clearText();

			getPolicyNameText().setText(policyName);
			getDescriptionText().setText(desc);
			includeText.setText(changeIncludeValue(include));
			excludeText.setText(changeExcludeValue(exclude));

			String protocol = repo[0].substring(0, repo[0].lastIndexOf("://"));

			if (protocol.equals("file")) {
				// fileserver
				getRepoKind().select(0);
				getBuildRepoUriText().setText(server.getUrl());
				getBuildRepoRootText().setText(repo[0].substring(repo[0]
				                                                      .lastIndexOf("://") + 3));
				noUsernameRequired.setEnabled(false);
				getBuildRepoUriText().setEnabled(false);
			} else {
				// ftp protocol
				getRepoKind().select(1);
				getBuildRepoUriText().setText(repo[0].substring(repo[0]
				                                                     .lastIndexOf("://") + 3));
				getBuildRepoRootText().setText(repo[1]);
			}
			if( repo.length == 3 ) {
				getUserField().setText(repo[2]);
				getUserField().setEnabled(true);
				getPasswordField().setEnabled(true);
			} else if ( repo.length == 4 ) {
				getUserField().setText(repo[2]);
				getPasswordField().setText(repo[3]);
				if(repo[2].equals("anonymous")){
					getNoUsernameRequired().setSelection(true);
					getUserField().setEnabled(false);
					getPasswordField().setEnabled(false);
				} else {
					getUserField().setEnabled(true);
					getPasswordField().setEnabled(true);
				}
			} else {
				// anonymous
				getNoUsernameRequired().setSelection(true);
				getUserField().setText("anonymous");
				getUserField().setEnabled(false);
				getPasswordField().setEnabled(false);
			}
			// update option
			if(update)
				getUpdateOptionRequired().setSelection(true);
			for (int i = 0; i < dest.length; i++) {
				String d = dest[i];
				if(d.indexOf('/') > 0)
					details = new PolicyDetails(null, null, d.substring(0, d
							.indexOf('/')), null, d
							.substring(d.indexOf('/') + 1));
				else
					details = new PolicyDetails(null, null, d, null, null);
				deployList.add(details);
			}

			getDeployViewer().setInput(deployList);
			addPolicy.setEnabled(true);
		} catch (OdenException e) {

		} catch (Exception odenException) {
			OdenActivator.error(
					"Exception occured while loading policy detail info.",
					odenException);
		}

	}

	private String changeIncludeValue(String[] inputArr) {
		String returnVal="";
		if(inputArr.length > 0) {
			for(String include : inputArr)
				returnVal = returnVal + include + ";";
		}
		if(returnVal.indexOf(";") > 0) {
			return returnVal.substring(0, returnVal.lastIndexOf(";"));
		} else {
			return returnVal;
		}
	}
	private String changeExcludeValue(String[] inputArr) {
		ArrayList<String> exepts = arrToChange(CommandMessages.ODEN_EXPLORER_ExplorerView_HiddenFolder.split(","));
		String returnVal="";
		if(inputArr.length > 0) {
			for(String exclude : inputArr)
				if(checkExcept(exclude, exepts))
					returnVal = returnVal + exclude + ";";
		} else {
			returnVal = "";
		}
		if(returnVal.indexOf(";") > 0) {
			return returnVal.substring(0, returnVal.lastIndexOf(";"));
		} else {
			return returnVal;
		}
	}
	private boolean checkExcept(String exclude , ArrayList<String> exepts) {
		for(String except : exepts)
			if(except.equals(exclude))
				return false;
		return true;
	}
	
	private ArrayList<String> arrToChange(String[] inputArr) {
		ArrayList<String> returnList = new ArrayList<String>();
		for(String input : inputArr){
			String tempStr = "**/" + input + "/**" ;
			returnList.add(tempStr);
		}
		return returnList;
	}
	
	/**
	 * initialize policy detail data
	 */
	public void clearText() {

		includeText.setText("");
		getExcludeText().setText("");
		getBuildRepoUriText().setText("");
		getBuildRepoRootText().setText("");
		getPolicyNameText().setText("");
		getDescriptionText().setText("");
		repoNickname.deselectAll();
		getRepoKind().deselectAll();
		getUpdateOptionRequired().setSelection(false);
		getDeployViewer().getTable().clearAll();
		PolicyDetails details = null;
		getDeployViewer().setInput(details);

		getNoUsernameRequired().setSelection(false);
		getUserField().setText("");
		getPasswordField().setText("");
		getUserField().setEnabled(true);
		getPasswordField().setEnabled(true);
		getRepoKind().setEnabled(true);
		repoPath.setEnabled(true);
		getBuildRepoUriText().setEnabled(true);
		getBuildRepoRootText() .setEnabled(true);
		getNoUsernameRequired().setEnabled(true);
		getUserField().setEnabled(true);
		userName.setEnabled(true);
		userPassword.setEnabled(true);
		getPasswordField().setEnabled(true);

	}

	private Listener listener = new Listener() {
		public void handleEvent(Event event) {
			if (event.widget == getRemovePolicy()) {
				// validation check(remove)
				PolicyDetails details = null;
				ISelection selection = getPolicyViewer().getSelection();
				Object obj = ((IStructuredSelection) selection)
				.getFirstElement();
				details = (PolicyDetails) obj;

				if (checkRemove(obj)) {
					if (DialogUtil.confirmMessageDialog(CommonMessages.ODEN_CommonMessages_Title_ConfirmDelete,
							UIMessages.ODEN_EDITORS_PolicyPage_DialogMsg_ConfirmDeletePolicy_MessagePre + details.getPolicyName() + CommonMessages.ODEN_CommonMessages_Confirm_MessageSuf )) {
						new DeletePolicyAction().run(serverName);
						client.setVisible(false);
					}
				} else {
					DialogUtil.openMessageDialog(CommonMessages.ODEN_CommonMessages_Title_Warning,
							CommonMessages.ODEN_CommonMessages_SelectItemFirst,
							MessageDialog.WARNING);
				}
			} else if (event.widget == savePolicy) {
				// validation check(save)
				if (checkSave()){
					new SavePolicyAction().run(serverName);
				}
			} else if (event.widget == getAddPolicy()) {
				// validation check(add)
				client.setVisible(true);
				new NewPolicyAction().run(serverName);
			} else if (event.widget == addDeploy) {
				new NewDeployAction().run(serverName);

			} else if (event.widget == removeDeploy) {
				ISelection selection = getDeployViewer().getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				PolicyDetails detail = (PolicyDetails) obj;

				if(checkRemoveDeploy()){
					if (DialogUtil.confirmMessageDialog(CommonMessages.ODEN_CommonMessages_Title_ConfirmDelete,
							UIMessages.ODEN_EDITORS_PolicyPage_DialogMsg_ConfirmDeleteDeploy_MessagePre + detail.getDeployUrl() + CommonMessages.ODEN_CommonMessages_Confirm_MessageSuf)) {
						new DeleteDeployAction().run(serverName);
					}
				} else {
					DialogUtil.openMessageDialog(CommonMessages.ODEN_CommonMessages_Title_Warning,
							"Select Deployment Target",
							MessageDialog.WARNING);
				}
			}
		}
	};

	private void chageLabel() {
		// change Label
		policy.setText(UIMessages.ODEN_EDITORS_PolicyPage_Opt_PolicyName);
		repo.setText(UIMessages.ODEN_EDITORS_PolicyPage_Opt_BuildRepo);
	}
	
	/**
	 * change Mandatory label
	 */
	public void chageMandaLabel() {
		// change Label
		policy.setText(UIMessages.ODEN_EDITORS_PolicyPage_Man_PolicyName);
		description.setText(UIMessages.ODEN_EDITORS_PolicyPage_Opt_PolicyDesc);
		repo.setText(UIMessages.ODEN_EDITORS_PolicyPage_Man_BuildRepo);
		include.setText(UIMessages.ODEN_EDITORS_PolicyPage_Man_ItemInclude);
	}
	
	/**
	 * Add deploy grid
	 */
	public void addDeploy(String agent , String location) {
		// Deploy Text reset
		PolicyDetails details = null;

		details = new PolicyDetails(null, null, agent, null, location);
		getDeployViewer().add(details);
	}

	private Boolean checkSave() {
		if (getPolicyNameText().getText().equals("")) {
			DialogUtil.openMessageDialog(CommonMessages.ODEN_CommonMessages_Title_Warning,
					UIMessages.ODEN_EDITORS_PolicyPage_DialogMsg_InputValue + " " 
					+ UIMessages.ODEN_EDITORS_PolicyPage_Opt_PolicyName,
					MessageDialog.INFORMATION);
			getPolicyNameText().setFocus();
			return false;
		} else if (getPolicyNameText().getText().equals(UIMessages.ODEN_EDITORS_PolicyPage_TempPolicyName)) {
			// check temporary policyName
			DialogUtil.openMessageDialog(CommonMessages.ODEN_CommonMessages_Title_Warning,
					CommonMessages.ODEN_CommonMessages_NameShouldBeSpecified + '"' + UIMessages.ODEN_EDITORS_PolicyPage_TempPolicyName + '"',
					MessageDialog.WARNING);
			getPolicyNameText().setFocus();
			return false;
		} else if (includeText.getText().equals("")) {
			DialogUtil.openMessageDialog(CommonMessages.ODEN_CommonMessages_Title_Warning,
					UIMessages.ODEN_EDITORS_PolicyPage_DialogMsg_InputValue  + " "
					+ UIMessages.ODEN_EDITORS_PolicyPage_Opt_ItemInclude,
					MessageDialog.INFORMATION);
			includeText.setFocus();
			return false;
		} else if (getRepoKind().getText().equals("")){
			DialogUtil.openMessageDialog(CommonMessages.ODEN_CommonMessages_Title_Warning,
					UIMessages.ODEN_EDITORS_PolicyPage_DialogMsg_InputValue + " " 
					+ UIMessages.ODEN_EDITORS_PolicyPage_Opt_BuildRepo,
					MessageDialog.INFORMATION);
			repo_section.setExpanded(true);
			getRepoKind().setFocus();
			return false;
		} else if (getBuildRepoUriText().getText().equals("")){
			DialogUtil.openMessageDialog(CommonMessages.ODEN_CommonMessages_Title_Warning,
					UIMessages.ODEN_EDITORS_PolicyPage_DialogMsg_InputValue + " " 
					+ UIMessages.ODEN_EDITORS_PolicyPage_Opt_BuildRepo,
					MessageDialog.INFORMATION);
			repo_section.setExpanded(true);
			getBuildRepoUriText().setFocus();
			return false;		
		} else if (getBuildRepoRootText().getText().equals("")){
			DialogUtil.openMessageDialog(CommonMessages.ODEN_CommonMessages_Title_Warning,
					UIMessages.ODEN_EDITORS_PolicyPage_DialogMsg_InputValue + " " 
					+ UIMessages.ODEN_EDITORS_PolicyPage_Opt_BuildRoot,
					MessageDialog.INFORMATION);
			repo_section.setExpanded(true);
			getBuildRepoRootText().setFocus();
			return false;
		} else if (getDeployViewer().getTable().getItemCount() == 0){
			DialogUtil.openMessageDialog(CommonMessages.ODEN_CommonMessages_Title_Warning,
					UIMessages.ODEN_EDITORS_PolicyPage_DialogMsg_InputValue + " " 
					+ UIMessages.ODEN_HISTORY_DeploymentHistoryView_LabelGridCol2,
					MessageDialog.INFORMATION);
			agent_section.setExpanded(true);
			return false;		
		} else if (getPolicyNameText().getEnabled()  && dupPolicyNameCheck() ) {
			DialogUtil.openMessageDialog(CommonMessages.ODEN_CommonMessages_Title_Warning,
					CommonMessages.ODEN_CommonMessages_NameAlreadyExists,
					MessageDialog.WARNING);
			getPolicyNameText().setFocus();
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
	
	/**
	 * check when add agent information
	 * @return duplicate server name and location variable
	 */
	public Boolean checkAddDeploy(String addAgent, String addLocation) {
		PolicyDetails details = new PolicyDetails();
		int count = getDeployViewer().getTable().getItemCount();
		if(count != 0){
			for (int i = 0; i < getDeployViewer().getTable().getItemCount(); i++) {
				TableItem item = getDeployViewer().getTable().getItem(i);
				details = (PolicyDetails) item.getData();
				String agent = details.getDeployUrl();
				String location = details.getLocationVar();
				if(addAgent.equals(agent)&&addLocation.equals(location)){
					return false;
				}
			}
		}	
		return true; 
	}

	private Boolean checkRemoveDeploy() {
		ISelection selection = getDeployViewer().getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();

		if(obj != null)
			return true;
		return false; 
	}

	private void comboEvent() {
		repoNickname.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setCombo();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				setCombo();
			}
		});
		getRepoKind().addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setUserAccount();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				setUserAccount();
			}
		});

	}
	private void setUserAccount() {
		if(getRepoKind().getText().equals(CommonMessages.ODEN_ALIAS_RepositoryManager_ProtocolSet_FileSystem)) {
			// when file protocol use
			getNoUsernameRequired().setEnabled(false);
			getUserField().setEnabled(false);
			userName.setEnabled(false);
			userPassword.setEnabled(false);
			getPasswordField().setEnabled(false);
			getNoUsernameRequired().setSelection(true);
			getBuildRepoUriText().setEnabled(false);
			getBuildRepoUriText().setText(server.getUrl());
		} else {
			// when ftp protocol use
			getNoUsernameRequired().setEnabled(true);
			getNoUsernameRequired().setSelection(false);
			getUserField().setEnabled(true);
			userName.setEnabled(true);
			userPassword.setEnabled(true);
			getPasswordField().setEnabled(true);
			getBuildRepoUriText().setEnabled(true);
			getBuildRepoUriText().setText("");
		}
	}
	private void setCombo() {
		Repository repo = OdenActivator.getDefault().getAliasManager()
		.getRepositoryManager().getRepository(repoNickname.getText());
		if(repoNickname.getText().equals("User Input")) {
			getRepoKind().setEnabled(true);
			repoPath.setEnabled(true);
			getBuildRepoUriText().setEnabled(true);
			getBuildRepoRootText() .setEnabled(true);
			getNoUsernameRequired().setEnabled(true);
			getNoUsernameRequired().setSelection(false);
			getUserField().setEnabled(true);
			userName.setEnabled(true);
			userPassword.setEnabled(true);
			getPasswordField().setEnabled(true);

			getRepoKind().deselectAll();

			getBuildRepoUriText().setText("");
			getBuildRepoRootText().setText("");

			getUserField().setText("");
			getPasswordField().setText("");

		} else {
			buildRepoInfoDisable();
			if (repo.isHasNoUserName()) {
				getNoUsernameRequired().setSelection(true);
				getUserField().setText("");
				getPasswordField().setText("");
				getUserField().setEnabled(false);
				getUserField().setText("anonymous");
				getPasswordField().setEnabled(false);
			} else {
				getNoUsernameRequired().setSelection(false);
				getUserField().setText(repo.getUser());
				getPasswordField().setText(repo.getPassword());
				//				getUserField().setText("");
			}
			if (repo
					.getProtocol()
					.equals(
							CommonMessages.ODEN_ALIAS_RepositoryManager_ProtocolSet_FileSystem)) {
				getBuildRepoUriText().setText(repo.getUrl());
				getBuildRepoRootText().setText(repo.getPath());
				getRepoKind().select(0);
			} else {
				getBuildRepoUriText().setText(repo.getUrl());
				getBuildRepoRootText().setText(repo.getPath());
				getRepoKind().select(1);
			}
		}

	}

	private void buildRepoInfoDisable() {
		getRepoKind().setEnabled(false);
		repoPath.setEnabled(false);
		getBuildRepoUriText().setEnabled(false);
		getBuildRepoRootText() .setEnabled(false);
		getNoUsernameRequired().setEnabled(false);
		getUserField().setEnabled(false);
		userName.setEnabled(false);
		userPassword.setEnabled(false);
		getPasswordField().setEnabled(false);
	}
	private Boolean dupPolicyNameCheck() {
		String commnd = "";
		String result = "";
		commnd = MSG_POLICY_SHOW;
		try {
			result = OdenBroker.sendRequest(shellUrl, commnd);
			if(result != null){
				JSONArray array = new JSONArray(result);

				for (int i = 0; i < array.length(); i++) {
					Object o = ((JSONObject) array.get(i)).keys().next();
					if(!(o.toString().equals("KnownException"))){ // no data
						Cmd result_ = new Cmd("foo", "fooAction \""
								+ o.toString()
								+ "\" "
								+ (String) ((JSONObject) array.get(i))
								.get(o.toString()).toString());
						String name = o.toString();
						if(name.equals(policyNameText.getText())){
							return true;
						}

					}
				}
			}
		} catch (OdenException e) {

		} catch (Exception odenException) {
			OdenActivator.error(
					"Exception occured while check policy duplication.",
					odenException);
		}
		return false;
	}

	// Events
	private void filterEvent() {
		filterText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {

			}

			public void keyReleased(KeyEvent ke) {
				// filter.setSearchText(filterText.getText());
				if(!(filterText.getText().equals("type filter text")) && !(filterText.getText().equals(""))){
					PolicyPage.this.getPolicyViewer().addFilter(PolicyPage.this.filter);
					getPolicyViewer().refresh();
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

	private void tableEvent() {
		getPolicyViewer().getTable().addMouseListener(new MouseAdapter() {

			public void mouseDown(MouseEvent e) {

				if (getPolicyViewer().getTable().getItem(new Point(e.x, e.y)) != null) {
					if ( chkNewPolicy()) {
						// add Policy
						clearText();
						PolicyDetails details = null;
						ISelection selection = getPolicyViewer().getSelection();
						Object obj = ((IStructuredSelection) selection).getFirstElement();
						details = (PolicyDetails) obj;
						getPolicyNameText().setText(details.getPolicyName());
						getDescriptionText().setText(details.getDescription());
						getPolicyNameText().setEnabled(true);
						getAddPolicy().setEnabled(false);
					} else {
						client.setVisible(true);
						getPolicyNameText().setEnabled(false);
						PolicyDetails details = null;
						ISelection selection = getPolicyViewer().getSelection();
						Object obj = ((IStructuredSelection) selection).getFirstElement();
						details = (PolicyDetails) obj;
						chageLabel();
						showPolicyDetail(details.getPolicyName());
						getRemovePolicy().setEnabled(true);
						if(chkNewPolicyExist())
							getAddPolicy().setEnabled(false);
						else
							getAddPolicy().setEnabled(true);
					}
				} else {
					client.setVisible(false);
					policyViewer.getTable().clearAll();
					policyViewer.getTable().deselectAll();
					loadInitData(shellUrl);
					addPolicy.setEnabled(true);
					policyViewer.refresh();
				}

			}
		});
	}
	/**
	 * Set information of server name at repoCombo
	 */
	public void getRepo(){
		String serverNickname = server.getNickname();
		repoNickname.removeAll();
		Collection<Repository> repos = OdenActivator.getDefault()
		.getAliasManager().getRepositoryManager().getRepositories();
		for(Repository repo : repos){
			if(serverNickname.equals(repo.getServerToUse())){
				repoNickname.add(repo.getNickname());
			}
		}
		repoNickname.add("User Input");
	}

	private void temporaryEvent() {
		getPolicyNameText().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {
			}
			public void keyReleased(KeyEvent ke) {
				tempProcess();
			}
		});
		getDescriptionText().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {
			}
			public void keyReleased(KeyEvent ke) {
				tempProcess();
			}
		});
	}

	private void tempProcess () {
		if(chkNewPolicy()) {
			removeTempcell();
			inputTempcell(getLastNum());
		} else {
			int selected= getPolicyViewer().getTable().getSelectionIndex();
			removeTempcell();
			inputTempcell(selected);
			getPolicyViewer().getTable().remove(selected + 1);
			getAddPolicy().setEnabled(true);
		}

	}
	/**
	 * Remove temporary cell
	 */
	public void removeTempcell() {
		PolicyDetails details = null;
		ISelection selection = getPolicyViewer().getSelection();

		details = (PolicyDetails) ((IStructuredSelection) selection)
		.getFirstElement();
		if (details != null) {
			getPolicyViewer().remove(details);
		}
		getPolicyViewer().refresh();
	}
	private void inputTempcell( int num) {
		String tempPolicyName = getPolicyNameText().getText();
		String tempPolicyDesc = getDescriptionText().getText();

		PolicyDetails details = null;
		details = new PolicyDetails(tempPolicyName, tempPolicyDesc, null, null, null);
		getPolicyViewer().insert(details, num );
		getPolicyViewer().getTable().select(num );
	}
	/**
	 * Returns the new policy exist or not exist
	 * @return true/false , When a new Policy exist , return true.
	 */
	public boolean chkNewPolicyExist() {
		int count = getPolicyViewer().getTable().getItemCount();
		int originCount = originPolicy.size();
		if(count == originCount)
			return false;

		return true;
	}
	/**
	 * Returns the selected policy of new policy or not
	 * @return true/false
	 */
	public boolean chkNewPolicy() {
		int selected= getPolicyViewer().getTable().getSelectionIndex();
		if(selected != getLastNum())
			return false;
		return true;
	}
	/**
	 * Constructor UI component
	 */
	public void setExcludeText(StyledText excludeText) {
		this.excludeText = excludeText;
	}

	public StyledText getExcludeText() {
		return excludeText;
	}

	public void setIncludeText(StyledText includeText) {
		this.includeText = includeText;
	}

	public StyledText getIncludeText() {
		return includeText;
	}

	public void setRepoKind(Combo repoKind) {
		this.repoKind = repoKind;
	}

	public Combo getRepoKind() {
		return repoKind;
	}

	public void setDeployViewer(TableViewer deployViewer) {
		this.deployViewer = deployViewer;
	}

	public TableViewer getDeployViewer() {
		return deployViewer;
	}

	public void setNoUsernameRequired(Button noUsernameRequired) {
		this.noUsernameRequired = noUsernameRequired;
	}

	public Button getNoUsernameRequired() {
		return noUsernameRequired;
	}

	public void setPolicyNameText(Text policyNameText) {
		this.policyNameText = policyNameText;
	}

	public Text getPolicyNameText() {
		return policyNameText;
	}

	public void setBuildRepoUriText(Text buildRepoUriText) {
		this.buildRepoUriText = buildRepoUriText;
	}

	public Text getBuildRepoUriText() {
		return buildRepoUriText;
	}

	public void setBuildRepoRootText(Text buildRepoRootText) {
		this.buildRepoRootText = buildRepoRootText;
	}

	public Text getBuildRepoRootText() {
		return buildRepoRootText;
	}

	public void setUserField(Text userField) {
		this.userField = userField;
	}

	public Text getUserField() {
		return userField;
	}

	public void setPasswordField(Text passwordField) {
		this.passwordField = passwordField;
	}

	public Text getPasswordField() {
		return passwordField;
	}

	public void setDescriptionText(Text descriptionText) {
		this.descriptionText = descriptionText;
	}

	public Text getDescriptionText() {
		return descriptionText;
	}

	public  void setUpdateOptionRequired(Button updateOptionRequired) {
		this.updateOptionRequired = updateOptionRequired;
	}

	public  Button getUpdateOptionRequired() {
		return updateOptionRequired;
	}

	public void setShellUrl(String shellUrl) {
		this.shellUrl = shellUrl;
	}

	public String getShellUrl() {
		return shellUrl;
	}

	public void setPolicyViewer(TableViewer policyViewer) {
		this.policyViewer = policyViewer;
	}

	public TableViewer getPolicyViewer() {
		return policyViewer;
	}

	public void setAddPolicy(Button addPolicy) {
		this.addPolicy = addPolicy;
	}

	public Button getAddPolicy() {
		return addPolicy;
	}

	public void setRemovePolicy(Button removePolicy) {
		this.removePolicy = removePolicy;
	}

	public Button getRemovePolicy() {
		return removePolicy;
	}

	public void setLastNum(int lastNum) {
		this.lastNum = lastNum;
	}

	public int getLastNum() {
		return lastNum;
	}

}