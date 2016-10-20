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
package anyframe.oden.eclipse.core.explorer.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

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
import anyframe.oden.eclipse.core.utils.JSONUtil;
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
	// Strings and messages from message properties
	private String title = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Title;
	private String subtitle = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_SubTitle;

	// Oden dialog image which appears on the upper right of the panel
	private ImageDescriptor odenImageDescriptor = ImageUtil
	.getImageDescriptor(UIMessages.ODEN_EXPLORER_Dialogs_OdenImageURL);

	public Server server;
	public Object tree;

	private ArrayList<String> relativepath;
	private String repoUrl;
	private String shellurl;

	Table table;
	TableViewer tableViewer;
	TableViewerColumn column1, column2, column3, column4;

	private String col1 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Repo;
	private String col2 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_DeployPath;
	private String col3 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_DeployItem;
	private String col4 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_DeployAgent;

	private ArrayList<DeployNowInfo> DeployItem;
	private Label itemCount;
	private final String tempPolicyName =  CommandMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_TempPolicyName + Calendar.getInstance().getTimeInMillis();
	private final String tempTaskName =  CommandMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_TempTaskName + + Calendar.getInstance().getTimeInMillis();

	private final String MSG_POLICY_ADD1 = CommandMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_PolicyAddCommand_Action
	+ " " + tempPolicyName;
	private final String MSG_POLICY_OPT = CommandMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_PolicyOpt;
	private final String MSG_POLICY_ADD2 = CommandMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_PolicyAddCommand_Include;
	private final String MSG_POLICY_ADD3 = CommandMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_PolicyAddCommand_Exclude;
	private final String MSG_POLICY_DELETE = CommandMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_PolicyDel + " " + tempPolicyName + " " + CommandMessages.ODEN_HISTORY_DeploymentHistoryView_History_Json_Opt;
	private final String MSG_TASK_ADD1 = CommandMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_TaskAdd1 
	+ tempTaskName + " -p ";
	private final String MSG_TASK_ADD2 = CommandMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_TaskAdd2 + " " + CommandMessages.ODEN_HISTORY_DeploymentHistoryView_History_Json_Opt;
	private final String MSG_TASK_DELETE = CommandMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_TaskDel + " " + tempTaskName + " " + CommandMessages.ODEN_HISTORY_DeploymentHistoryView_History_Json_Opt;
	private final String MSG_TASK_RUN = CommandMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_TaskRun + " " + tempTaskName  + " " + CommandMessages.ODEN_HISTORY_DeploymentHistoryView_History_Json_Opt;

	private Repository repo;
	private String protocol;
	private String[] hiddenFolder = CommandMessages.ODEN_EXPLORER_ExplorerView_HiddenFolder
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
			Object[] objects = obj;
			for (Object object : objects) {
				String fullpath = getFullpath((TreeObject) object);
				int firstIdx = fullpath.indexOf("/");
				int secondIdx = fullpath.indexOf("/", firstIdx + 1);
				String reponame = fullpath.substring(firstIdx + 1, secondIdx);
				repoUrl = OdenActivator.getDefault().getAliasManager()
				.getRepositoryManager().getRepository(reponame)
				.getUrl();
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
		// TODO 도움말 만든 후 아래 내용을 확인할 것
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
		tableViewer.setSorter(new TableViewerSorter());
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(data);

		column1 = new TableViewerColumn(tableViewer, SWT.None);
		column1.getColumn().setText(col1);
		column1.getColumn().setWidth(200);
		column1.setLabelProvider(new DeployRepoColumnLabelProvider());

		column2 = new TableViewerColumn(tableViewer, SWT.None);
		column2.getColumn().setText(col2);
		column2.getColumn().setWidth(150);
		column2.setLabelProvider(new DeployPathColumnLabelProvider());

		column3 = new TableViewerColumn(tableViewer, SWT.None);
		column3.getColumn().setText(col3);
		column3.getColumn().setWidth(200);
		column3.setLabelProvider(new DeployItemColumnLabelProvider());

		column4 = new TableViewerColumn(tableViewer, SWT.None);
		column4.getColumn().setText(col4);
		column4.getColumn().setWidth(100);
		column4.setLabelProvider(new DeployAgentsColumnLabelProvider());

		tableViewer.setInput(DeployItem);
		tableViewer.refresh();

		itemCount = new Label(composite, 0);
		itemCount
		.setText(tableViewer.getTable().getItemCount()
				+ " items."
				+ " "
				+ UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_ItemStatement);

		((TableViewerSorter) tableViewer.getSorter()).doSort(1);
		tableViewer.refresh();
		return parentComposite;
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
					if (include.indexOf("[20") > 0)
						include = '"' + include.substring(1, include.indexOf("[20")) + '"'  + " ";
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
				msgaddpoicy = msgaddpoicy + " " + CommandMessages.ODEN_HISTORY_DeploymentHistoryView_History_Json_Opt;
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

	private ArrayList<DeployNowInfo> returnList(String commnd) {
		ArrayList<DeployNowInfo> returnList = new ArrayList<DeployNowInfo>();

		try {
			String result = OdenBroker.sendRequest(shellurl, commnd);
			if(result != null){
				JSONArray array = new JSONArray(result);
				String path = "";
				String item = "";
				String agents_ = "";
				String repos = "";
				for (int i = 0; i < array.length(); i++) {
					for (Iterator<String> it = ((JSONObject) array.get(i)).keys(); it.hasNext();) {
						String key = it.next();
						JSONArray jsonArr = new JSONArray(key);

						repos = CommonUtil.replaceIgnoreCase(JSONUtil.toString(jsonArr), "\n", ",");
						String[] repoArr = repos.split(",");
						if(repoArr.length > 1) {
							// ftp
							repos = repoArr[0] + repoArr[1]; 
						} else {
							// file system
							repos = repoArr[0];
						}

						JSONObject files = (JSONObject) ((JSONObject) array.get(i))
						.get(key);
						for (Iterator<String> it2 = files.keys(); it2.hasNext();) {
							String file = it2.next();
							files.getJSONArray(file);

							agents_ = CommonUtil.replaceIgnoreCase(JSONUtil
									.toString(files.getJSONArray(file)), "\n", ",");
							if (agents_.lastIndexOf(",") > 0)
								agents_ = agents_.substring(0, agents_
										.lastIndexOf(","));

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
							DeployNowInfo DeployItem = new DeployNowInfo(repos, path, item,
									agents_);
							returnList.add(DeployItem);
						}

					}
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
					commnd = CommandMessages.ODEN_EDITORS_TaskPage_MsgTaskRun
					+ " " + '"' + taskName + '"' + " " + CommandMessages.ODEN_HISTORY_DeploymentHistoryView_History_Json_Opt;
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
		String commnd = CommandMessages.ODEN_EDITORS_PolicyPage_MsgAgentInfo;

		String result = OdenBroker.sendRequest(shellurl, commnd);
		if( result != null) {	
			JSONArray array = new JSONArray(result);
			if(array.length() > 0) {
				for (int i = 0; i < array.length(); i++) {
					String name = (String) ((JSONObject) array.get(i)).get("name");
					dest = dest + name + " ";
				}
			} else {
				OdenActivator.warning("You shoud add" +  '"' + "config.xml" + '"');
			}
		} else {
			OdenActivator.warning(CommonMessages.ODEN_CommonMessages_UnableToConnectServer);
		}
		return dest;
	}

	private void runCommand(String commnd) throws Exception {
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
		return super.close();
	}


}