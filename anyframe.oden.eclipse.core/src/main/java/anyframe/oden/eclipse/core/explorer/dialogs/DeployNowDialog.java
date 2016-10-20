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
package anyframe.oden.eclipse.core.explorer.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TreeMap;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;
import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenTrees.TreeObject;
import anyframe.oden.eclipse.core.OdenTrees.TreeParent;
import anyframe.oden.eclipse.core.alias.DeployNow;
import anyframe.oden.eclipse.core.alias.Repository;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.editors.PolicyContentProvider;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.CommonUtil;
import anyframe.oden.eclipse.core.utils.ImageUtil;
import anyframe.oden.eclipse.core.utils.OdenProgress;

/**
 * Run Deploy(Preview of deployed item) in selected folder,files. This class extends TitleAreaDialog
 * class.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC2
 * 
 */
public class DeployNowDialog extends TitleAreaDialog {
	// CommonUtil
	CommonUtil util = new CommonUtil();
	
	// Strings and messages from message properties
	private String title = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Title;
	private String subtitle = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_SubTitle;

	// Oden dialog image which appears on the upper right of the panel
	private ImageDescriptor odenImageDescriptor = ImageUtil
	.getImageDescriptor(UIMessages.ODEN_EXPLORER_Dialogs_OdenImageURL);

	public Server server;
	public Object tree;

	private ArrayList<String> relativepath;
	@SuppressWarnings("unused")
	private String repoUrl;
	private String shellurl;
	
	private Repository repository;
	Table table;
	TableViewer tableViewer;
	TableViewerColumn column1, column2, column3, column4 , column5;

	private String col1 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Mode;
	private String col2 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Repo;
	private String col3 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_DeployPath;
	private String col4 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_DeployItem;
	private String col5 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_DeployAgent;

	private ArrayList<DeployNowInfo> DeployItem;
	private Label itemCount;
	private final String tempPolicyName =  CommandMessages.ODEN_CLI_COMMAND_policy_tempname + util.getMilliseconds();
	private final String tempTaskName =  CommandMessages.ODEN_CLI_COMMAND_task_tempname + util.getMilliseconds();

	private final String MSG_POLICY_ADD1 = CommandMessages.ODEN_CLI_COMMAND_policy_add
	+ " " + tempPolicyName;
	private final String MSG_POLICY_OPT = CommandMessages.ODEN_CLI_OPTION_repository + " ";
	private final String MSG_POLICY_ADD2 = CommandMessages.ODEN_CLI_OPTION_include + " ";
	private final String MSG_POLICY_ADD3 = CommandMessages.ODEN_CLI_OPTION_destination;
	private final String MSG_POLICY_DELETE = CommandMessages.ODEN_CLI_COMMAND_policy_delete + " " + tempPolicyName + " " + CommandMessages.ODEN_CLI_OPTION_json;
	private final String MSG_TASK_ADD1 = CommandMessages.ODEN_CLI_COMMAND_task_add + " "
	+ tempTaskName + " -p ";
	private final String MSG_TASK_ADD2 = CommandMessages.ODEN_CLI_OPTION_desc_deploynow + " " + CommandMessages.ODEN_CLI_OPTION_json;
	private final String MSG_TASK_DELETE = CommandMessages.ODEN_CLI_COMMAND_task_delete + " " + tempTaskName + " " + CommandMessages.ODEN_CLI_OPTION_json;
	private final String MSG_TASK_RUN = CommandMessages.ODEN_CLI_COMMAND_task_run + " " + tempTaskName  + " " + CommandMessages.ODEN_CLI_OPTION_json;

	private Repository repo;
	private String protocol;
	private String[] hiddenFolder = CommandMessages.ODEN_CLI_OPTION_hiddenfolder
	.split(",");
	private String exclude = this.returnExclude();
	private String taskName;
	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();

	public DeployNowDialog(Shell parentShell, Object[] obj, String task,
			String url) throws Exception {
		super(parentShell);
		this.tree = obj;
		relativepath = new ArrayList<String>();
		DeployItem = new ArrayList<DeployNowInfo>();
		if (obj != null) {
			// Deploy now(Selected Tree)
			Object[] selections = obj;
			for (Object selection : selections) {
				String fullpath = getFullpath((TreeObject) selection);
				int firstIdx = fullpath.indexOf("/");
				int secondIdx = fullpath.indexOf("/", firstIdx + 1);
				String reponame = fullpath.substring(firstIdx + 1, secondIdx);
				repository = OdenActivator.getDefault().getAliasManager().getRepositoryManager().getRepository(reponame); 
				this.repoUrl = repository.getUrl();
				String serverToUse = OdenActivator.getDefault().getAliasManager()
				.getRepositoryManager().getRepository(reponame)
				.getServerToUse();
				String serverURL = OdenActivator.getDefault().getAliasManager()
				.getServerManager().getServer(serverToUse).getUrl();
				shellurl = CommonMessages.ODEN_CommonMessages_ProtocolString_HTTP
				+ serverURL + CommonMessages.ODEN_CommonMessages_ProtocolString_HTTPsuf;
				repo = OdenActivator.getDefault().getAliasManager()
				.getRepositoryManager().getRepository(reponame);
				protocol = repo.getProtocol()
				.equals(CommonMessages.ODEN_ALIAS_RepositoryManager_ProtocolSet_FileSystem) ? CommonMessages.ODEN_CommonMessages_ProtocolString_File
						: CommonMessages.ODEN_CommonMessages_ProtocolString_FTP;
				if (repo.getProtocol()
						.equals(CommonMessages.ODEN_ALIAS_RepositoryManager_ProtocolSet_FTP)) {
					// ftp
					String path = repo.getPath() + "/"
					+ CommonUtil.replaceIgnoreCase(fullpath,
							UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoriesRootLabel
							+ "/" + reponame + "/", "");
					relativepath.add(path);
				} else {
					// file system
					String path = protocol + repo.getPath() + "/"
					+ CommonUtil.replaceIgnoreCase(fullpath,
							UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_BuildRepositoriesRootLabel
							+ "/" + reponame + "/", "");

					relativepath.add(path);
				}
			}
			// get deploy item info
			try {
				DeployItem = searchDeployItem(shellurl);

			} catch (OdenException odenException) {
				OdenActivator
				.error(
						UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Exception_SearchDeployItem,
						odenException);
				odenException.printStackTrace();
			}



		} else {
			// Task Run
			taskName = task;
			shellurl = url;
			DeployItem = searchDeployItem(shellurl, task);
		}
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText(title);

	}

	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		// validate();
	}

	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		setTitle(title);
		setMessage(subtitle);

		Image odenImage = ImageUtil.getImage(odenImageDescriptor);
		if (odenImage != null) {
			setTitleImage(odenImage);
		}
		contents.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent disposeEvent) {
				ImageUtil
				.disposeImage(UIMessages.ODEN_EXPLORER_Dialogs_OdenImageURL);
			}
		});
		// TODO �룄���留� 留뚮뱺 �썑 �븘�옒 �궡�슜�쓣 �솗�씤�븷 寃�
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent,
				OdenActivator.HELP_PLUGIN_ID + ".oden.odenexplorerview");

		return contents;
	}

	protected Control createDialogArea(Composite parent) {

		// Top level composite
		Composite parentComposite = (Composite) super.createDialogArea(parent);

		// Create a composite with standard margins and spacing
		Composite composite = new Composite(parentComposite, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parentComposite.getFont());
		layout.marginHeight = 15;

		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 150;

		table = new Table(composite, SWT.SINGLE | SWT.FULL_SELECTION
				| SWT.BORDER | SWT.V_SCROLL);
		tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new PolicyContentProvider());
		tableViewer.setSorter(new DeployNowTableViewerSorter());
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(data);

		column1 = new TableViewerColumn(tableViewer, SWT.None);
		column1.getColumn().setText(col1);
		column1.getColumn().setWidth(50);
		column1.setLabelProvider(new DeployModeColumnLabelProvider());
		
		column2 = new TableViewerColumn(tableViewer, SWT.None);
		column2.getColumn().setText(col2);
		column2.getColumn().setWidth(200);
		column2.setLabelProvider(new DeployRepoColumnLabelProvider());

		column3 = new TableViewerColumn(tableViewer, SWT.None);
		column3.getColumn().setText(col3);
		column3.getColumn().setWidth(150);
		column3.setLabelProvider(new DeployPathColumnLabelProvider());

		column4 = new TableViewerColumn(tableViewer, SWT.None);
		column4.getColumn().setText(col4);
		column4.getColumn().setWidth(200);
		column4.setLabelProvider(new DeployItemColumnLabelProvider());

		column5 = new TableViewerColumn(tableViewer, SWT.None);
		column5.getColumn().setText(col5);
		column5.getColumn().setWidth(100);
		column5.setLabelProvider(new DeployAgentsColumnLabelProvider());

		tableViewer.setInput(DeployItem);
		tableViewer.refresh();

		itemCount = new Label(composite, 0);
		itemCount
		.setText(tableViewer.getTable().getItemCount()
				+ " items."
				+ " "
				+ UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_ItemStatement);

		((DeployNowTableViewerSorter) tableViewer.getSorter()).setColumn(3);
		
		tableViewer.refresh();
		tableEvent();
		return parentComposite;
	}
	/*
	 * column sorting event
	 */
	private void tableEvent() {
		column1.getColumn().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				((DeployNowTableViewerSorter) tableViewer.getSorter()).setColumn(1);
				int dir = tableViewer.getTable().getSortDirection();
				if (tableViewer.getTable().getSortColumn() == column1.getColumn()) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {

					dir = SWT.DOWN;
				}
				tableViewer.getTable().setSortDirection(dir);
				tableViewer.getTable().setSortColumn(column1.getColumn());
				tableViewer.refresh();
			}

		});
		column2.getColumn().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				((DeployNowTableViewerSorter) tableViewer.getSorter()).setColumn(2);
				int dir = tableViewer.getTable().getSortDirection();
				if (tableViewer.getTable().getSortColumn() == column2.getColumn()) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {

					dir = SWT.DOWN;
				}
				tableViewer.getTable().setSortDirection(dir);
				tableViewer.getTable().setSortColumn(column2.getColumn());
				tableViewer.refresh();
			}

		});
		column3.getColumn().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				((DeployNowTableViewerSorter) tableViewer.getSorter()).setColumn(3);
				int dir = tableViewer.getTable().getSortDirection();
				if (tableViewer.getTable().getSortColumn() == column3.getColumn()) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {

					dir = SWT.DOWN;
				}
				tableViewer.getTable().setSortDirection(dir);
				tableViewer.getTable().setSortColumn(column3.getColumn());
				tableViewer.refresh();
			}

		});
		column4.getColumn().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				((DeployNowTableViewerSorter) tableViewer.getSorter()).setColumn(4);
				int dir = tableViewer.getTable().getSortDirection();
				if (tableViewer.getTable().getSortColumn() == column4.getColumn()) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {

					dir = SWT.DOWN;
				}
				tableViewer.getTable().setSortDirection(dir);
				tableViewer.getTable().setSortColumn(column4.getColumn());
				tableViewer.refresh();
			}

		});
		column5.getColumn().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				((DeployNowTableViewerSorter) tableViewer.getSorter()).setColumn(5);
				int dir = tableViewer.getTable().getSortDirection();
				if (tableViewer.getTable().getSortColumn() == column5.getColumn()) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {

					dir = SWT.DOWN;
				}
				tableViewer.getTable().setSortDirection(dir);
				tableViewer.getTable().setSortColumn(column5.getColumn());
				tableViewer.refresh();
			}

		});


	}
	protected void okPressed() {
		// Deploy Now
		try {
			deploynow();
		} catch (OdenException odenException) {
			OdenActivator
			.error(
					UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Exception_DeployItem,
					odenException);
			odenException.printStackTrace();
		}
	}
	protected void cancelPressed() {
		//		deleteTemp();
		super.cancelPressed();
	}
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE);
	}

	private String getFullpath(TreeObject obj) {
		StringBuffer full = new StringBuffer(obj.getName());
		TreeParent parent = obj.getParent();
		while (parent != null) {
			full.insert(0, parent.getName() + "/");
			parent = parent.getParent();
		}
		if (full.toString().substring(0, 1).equals("/"))
			return full.toString().substring(1);
		else
			return full.toString();

	}

	// table label provider
	public class DeployPathColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((DeployNowInfo) element).getDeployPath();
		}
	}

	public class DeployItemColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((DeployNowInfo) element).getDeployItem();
		}
	}
	
	public class DeployModeColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((DeployNowInfo) element).getMode();
		}
	}
	
	public class DeployRepoColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((DeployNowInfo) element).getDeployRepo();
		}
	}

	public class DeployAgentsColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((DeployNowInfo) element).getDeployAgent();
		}
	}

	private ArrayList<DeployNowInfo> searchDeployItem(String url) throws Exception {
		ArrayList<DeployNowInfo> returnList = new ArrayList<DeployNowInfo>();
		String[] includeArr = new String[2];
		String commnd = "";
		String includes = "";

		try {
			// temporary policy & task make
			String repo_root = null;
			String msgaddpoicy = null;
			if (protocol.equals("file://")) {
				repo_root = protocol + repo.getPath();
			} else {
				repo_root = repo.getPath();
			}
			String agents = "";
			// 0. get agents
			try {
				agents = getAgents();
			} catch (OdenException odenException) {

			} catch (Exception odenException) {
				OdenActivator
				.error(
						UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Exception_GetAgent,
						odenException);
			}
			if(!(agents.equals(""))) {
				// 1. add temp policy
				for(String include : relativepath){
					include = CommonUtil.replaceIgnoreCase(include, repo_root, "");
					includeArr = util.getTreeObjectSplitElement(include);
					if (includeArr[0] != null)
						include = '"' + includeArr[1].substring(1) + '"'  + " ";
					else
						include = '"' + include.substring(1) + "/*" + '"' + " " + '"' + include.substring(1)
						+ "/**" + '"' + " ";
					includes = includes + include + " ";
				}
				includes = includes.substring(0, includes.lastIndexOf(" "));
				if (protocol
						.equals(CommonMessages.ODEN_CommonMessages_ProtocolString_File)) {
					msgaddpoicy = MSG_POLICY_ADD1 + MSG_POLICY_OPT + repo_root
					+ MSG_POLICY_ADD2 + includes + "-e" + " "
					+ exclude + MSG_POLICY_ADD3 + " " + agents
					+ "-desc deploynow" ;
				} else {
					msgaddpoicy = MSG_POLICY_ADD1
					+ MSG_POLICY_OPT
					+ CommonMessages.ODEN_CommonMessages_ProtocolString_FTP
					+ repo.getUrl() + " " + repo_root + " "
					+ repo.getUser() + " " + repo.getPassword()
					+ MSG_POLICY_ADD2 + includes + "-e" + " "
					+ exclude + MSG_POLICY_ADD3 + " " + agents
					+ "-desc deploynow";
				}
				msgaddpoicy = msgaddpoicy + " " + CommandMessages.ODEN_CLI_OPTION_json;
				try {
					runCommand(msgaddpoicy);
				} catch (OdenException odenException) {

				} catch (Exception odenException) {
					OdenActivator
					.error(
							UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Exception_SavePolicy,
							odenException);
				}

				// 2. add temp task
				String msgaddtask = MSG_TASK_ADD1
				+ tempPolicyName
				+ " " + MSG_TASK_ADD2;

				try {
					runCommand(msgaddtask);
				} catch (OdenException odenException) {
				} catch (Exception odenException) {
					OdenActivator
					.error(
							UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Exception_SaveTask,
							odenException);
				}

				commnd = "task test " + tempTaskName + " -json";

				returnList = returnList(commnd);
			}	
		} catch (Exception odenException) {

			OdenActivator.error(
					"Exception occured while searching deploy items.",
					odenException);
			odenException.printStackTrace();
		}
		return returnList;
	}

	private ArrayList<DeployNowInfo> searchDeployItem(String url, String taskname)
	throws Exception {
		ArrayList<DeployNowInfo> returnList = new ArrayList<DeployNowInfo>();
		String commnd = "";
		try {
			// seacrh method
			commnd = "task test" + " " + '"' + taskname + '"' + " " + "-json";
			returnList = returnList(commnd);
		} catch (Exception odenException) {

			OdenActivator.error(
					"Exception occured while searching deploy items.",
					odenException);
		}
		return returnList;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<DeployNowInfo> returnList(String commnd) {
		ArrayList<DeployNowInfo> returnList = new ArrayList<DeployNowInfo>();

		try {
			String result = OdenBroker.sendRequest(shellurl, commnd);
			if(result != null){
				JSONArray array = new JSONArray(result);
				String path = "";
				String item = "";
				String agent = "";
				String repo = "";
				String mode = "";
				for (int i = 0; i < array.length(); i++) {
					JSONObject full = (JSONObject) array.get(i);
					for(Iterator<String> it = full.keys() ; it.hasNext();) {
						String keys = it.next();
						if(keys.equals("agent")) {
							JSONObject agents = new JSONObject(full.getString("agent"));
							agent = agents.getString("name");
						} else if(keys.equals("repo")) {
							JSONArray repos = new JSONArray(full.getString("repo"));
							if(repos.length() > 0)
								repo = repos.getString(0);
						} else if(keys.equals("mode")) {
							mode = full.getString("mode");
							if(mode.equals("A"))
								mode = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Add;
							else if(mode.equals("U"))
								mode = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Update;
							else if(mode.equals("D"))
								mode = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Delete;
						} else if(keys.equals("path")) {
							String file = full.getString("path");
							if (file.indexOf("/") > 0) {
								// unix
								path = file.substring(0, file.lastIndexOf("/"));
								item = file.substring(file.lastIndexOf("/") + 1);
							} else if (file.lastIndexOf(File.separator) > 0 ) {
								// window
								path = file.substring(0, file
										.lastIndexOf(File.separator));
								item = file.substring(file
										.lastIndexOf(File.separator) + 1);

							} else {
								// root directory
								path = "";
								item = file;
							}	
						}
					}
					DeployNowInfo DeployItem = new DeployNowInfo(repo, path, item,agent,mode);
					returnList.add(DeployItem);
				}
			}
		} catch (OdenException odenException) {
		} catch (Exception odenException) {

			OdenActivator.error(
					"Exception occured while searching deploy items.",
					odenException);
		}

		return returnList;
	}

	private void deploynow() throws OdenException {
		try {
			statusProgress(MSG_TASK_RUN);
			super.close();
		} catch (OdenException odenException) {

		} catch ( Exception odenException) {
			OdenActivator.error("Exception occured while deploying items.",odenException);
		}
	}

	private void statusProgress(final String msg) throws OdenException {

		final OdenProgress jobProgress = new OdenProgress("in progress") {
			@Override
			protected void executeMe() {
				String commnd;
				// 1. task run
				if (taskName != null) {
					commnd = CommandMessages.ODEN_CLI_COMMAND_task_run
					+ " " + '"' + taskName + '"' + " " + CommandMessages.ODEN_CLI_OPTION_json;
				} else {
					commnd = msg;
				}
				try {
					runCommand(commnd);
				} catch (Exception odenException) {
					OdenActivator
					.error(
							UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Exception_RunTask,
							odenException);
				} finally{
					deleteTemp();
				}

			}
		};

		jobProgress.setUser(true);
		jobProgress.schedule();

		jobProgress.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				
			}
		});

	}

	private String getAgents() throws Exception {
		String dest = "";
		if(repository.isAllToDefault()) {
			// All to the default location
			String commnd = CommandMessages.ODEN_CLI_COMMAND_agent_info_json;
	
			String result = OdenBroker.sendRequest(shellurl, commnd);
			if( result != null) {	
				JSONArray array = new JSONArray(result);
				if(array.length() > 0) {
					for (int i = 0; i < array.length(); i++) {
						String name = (String) ((JSONObject) array.get(i)).get("name");
						dest =  dest + '"' + name + ":~" + '"' +  " ";
					}
				} else {
					OdenActivator.warning("You shoud add" +  '"' + "config.xml" + '"');
				}
			} else {
				OdenActivator.warning(CommonMessages.ODEN_CommonMessages_UnableToConnectServer);
			}
		} else {
			 TreeMap<String, DeployNow> deployMap = repository.getDeployNowMap();
			 String locationVar = "";
			 for(DeployNow deploynow : deployMap.values()) {
				 if(deploynow.getDestinedLocation().equals(UIMessages.ODEN_EDITORS_PolicyPage_DialogAgent_ComboDefault))
					 locationVar = "~";
				 else 
					 locationVar = "$" + deploynow.getDestinedLocation();
				 dest = dest + '"' + deploynow.getDestinedAgentName() + ":"+ locationVar + '"' + " ";
			 }
		}
		return dest;
	}

	private void runCommand(String commnd) throws Exception {
		@SuppressWarnings("unused")
		String result = OdenBroker.sendRequest(shellurl, commnd);
	}

	private void deleteTemp() {
		if (taskName == null) {
			// 2. delete policy
			String msgdelpolicy = MSG_POLICY_DELETE;
			try {
				runCommand(msgdelpolicy);
			} catch (OdenException odenExeption) {

			} catch (Exception odenException) {
				OdenActivator
				.error(
						UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Exception_DeletePolicy,
						odenException);
			}

			// 3. delete task
			String msgdeltask = MSG_TASK_DELETE;
			try {
				runCommand(msgdeltask);
			} catch (OdenException odenExeption) {
			} catch (Exception odenException) {
				OdenActivator
				.error(
						UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Exception_DeleteTask,
						odenException);
			}
		}

	}

	private String returnExclude() {
		String exclude = "";
		for (String folder : hiddenFolder) {
			exclude = exclude + "**/" + folder + "/** ";
		}
		return exclude;
	}
	/**
	 * Closes the dialog box Override so we can delete temporary policy and task
	 */
	public boolean close() {
		deleteTemp();
		util = null;
		return super.close();
	}


}