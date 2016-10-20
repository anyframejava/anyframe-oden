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
package anyframe.oden.eclipse.core.jobmanager.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;
import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.editors.PolicyContentProvider;
import anyframe.oden.eclipse.core.jobmanager.JobManagerView;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.Cmd;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Run Deploy(Preview of deployed item) in selected folder,files. This class
 * extends TitleAreaDialog class.
 * 
 * @author HONG JungHwan
 * @version 1.1.0
 * 
 */
public class DeployByTaskDialog extends TitleAreaDialog {
	
	private String title = "Deploy by Task";
	private String subtitle = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_SubTitle;
	
	// Oden dialog image which appears on the upper right of the panel
	private ImageDescriptor odenImageDescriptor = ImageUtil
			.getImageDescriptor(UIMessages.ODEN_EXPLORER_Dialogs_OdenImageURL);

	public Server server;
	public Object tree;

	private String shellurl;

	private Combo taskCombo;
	
	Table table;
	TableViewer tableViewer;
	TableViewerColumn column1, column2, column3, column4, column5;

	private String col1 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Mode;
	private String col2 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Repo;
	private String col3 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_DeployPath;
	private String col4 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_DeployItem;
	private String col5 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_DeployAgent;

	private ArrayList<DeployByTaskInfo> deployItem;
	private ArrayList<combolist> addlist;
	
	private Label itemCount;
	
	private String taskname;
	
	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();
	
	private Button preView;
	
	public DeployByTaskDialog(Shell parentShell, String shellurl)
			throws Exception {
		super(parentShell);

		this.shellurl = shellurl;

	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText(title);

	}

	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
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
		layout.numColumns = 5;
		
		GridData data = new GridData(GridData.FILL_BOTH);

		data.horizontalSpan = 3;
		taskCombo = new Combo(composite, SWT.SINGLE | SWT.BORDER
				| SWT.DROP_DOWN | SWT.LEFT);
		taskCombo.setLayoutData(data);
		
		data = new GridData();
		data.horizontalSpan = 2;
		preView = new Button(composite , SWT.NONE);
		preView.setText("Preview");
		preView.setLayoutData(data);
		preView.addListener(SWT.Selection, listener);
		
		data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 150;
		data.horizontalSpan = 5;
		
		table = new Table(composite, SWT.SINGLE | SWT.FULL_SELECTION
				| SWT.BORDER | SWT.V_SCROLL);
		tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new PolicyContentProvider());
		tableViewer.setSorter(new DeployByTaskTableViewerSorter());
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

		tableEvent();
		
		itemCount = new Label(composite, 0);
		itemCount
		.setText(tableViewer.getTable().getItemCount()
				+ " items."
				+ " "
				+ UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_ItemStatement);
		
		tableViewer.refresh();
		
		
		getTaskList();
		
		return parentComposite;
	}
	
	private Listener listener = new Listener() {
		public void handleEvent(Event event) {
			if (event.widget == preView) {
				try {
					deployItem = searchDeployItem(shellurl);
					tableViewer.setInput(deployItem);
					
					itemCount
					.setText(tableViewer.getTable().getItemCount()
							+ " items."
							+ " "
							+ UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_ItemStatement);
					
					((DeployByTaskTableViewerSorter) tableViewer.getSorter()).setColumn(3);

					tableViewer.refresh();
					
				} catch (Exception odenException) {
					OdenActivator.error(UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Exception_SearchDeployItem,odenException);
					odenException.printStackTrace();
				}
			}
		}
	};
	
	/*
	 * column sorting event
	 */
	private void tableEvent() {
		column1.getColumn().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				((DeployByTaskTableViewerSorter) tableViewer.getSorter())
						.setColumn(1);
				int dir = tableViewer.getTable().getSortDirection();
				if (tableViewer.getTable().getSortColumn() == column1
						.getColumn()) {
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
				((DeployByTaskTableViewerSorter) tableViewer.getSorter())
						.setColumn(2);
				int dir = tableViewer.getTable().getSortDirection();
				if (tableViewer.getTable().getSortColumn() == column2
						.getColumn()) {
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
				((DeployByTaskTableViewerSorter) tableViewer.getSorter())
						.setColumn(3);
				int dir = tableViewer.getTable().getSortDirection();
				if (tableViewer.getTable().getSortColumn() == column3
						.getColumn()) {
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
				((DeployByTaskTableViewerSorter) tableViewer.getSorter())
						.setColumn(4);
				int dir = tableViewer.getTable().getSortDirection();
				if (tableViewer.getTable().getSortColumn() == column4
						.getColumn()) {
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
				((DeployByTaskTableViewerSorter) tableViewer.getSorter())
						.setColumn(5);
				int dir = tableViewer.getTable().getSortDirection();
				if (tableViewer.getTable().getSortColumn() == column5
						.getColumn()) {
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		// Run Deploy
		try {
			deploy();
			
			JobManagerView jobmanagerview = OdenActivator.getDefault().getJObManagerView();
			jobmanagerview.refresh();
			
		} catch (OdenException odenException) {
			
			OdenActivator
					.error(
							UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Exception_DeployItem,
							odenException);
			odenException.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
	 */
	protected void cancelPressed() {
		super.cancelPressed();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#setShellStyle(int)
	 */
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE);
	}


	// table label provider
	public class DeployPathColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((DeployByTaskInfo) element).getDeployPath();
		}
	}

	public class DeployItemColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((DeployByTaskInfo) element).getDeployItem();
		}
	}

	public class DeployModeColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((DeployByTaskInfo) element).getMode();
		}
	}

	public class DeployRepoColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((DeployByTaskInfo) element).getDeployRepo();
		}
	}

	public class DeployAgentsColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((DeployByTaskInfo) element).getDeployAgent();
		}
	}

	private ArrayList<DeployByTaskInfo> searchDeployItem(String url)
			throws Exception {
		ArrayList<DeployByTaskInfo> returnList = new ArrayList<DeployByTaskInfo>();
		
		for(combolist list : addlist)
			if(list.getCombolist().equals(taskCombo.getText()))
				taskname = list.getName();
		
		String commnd = CommandMessages.ODEN_CLI_COMMAND_task_test + " " + '"'
				+ taskname + '"' + " "
				+ CommandMessages.ODEN_CLI_OPTION_json;
		
		returnList = returnList(commnd);

		return returnList;
	}

	private void deploy() throws OdenException {
		try {
			// task run
			String runcommnd;
			
			for(combolist list : addlist)
				if(list.getCombolist().equals(taskCombo.getText()))
					taskname = list.getName();
			
			runcommnd = CommandMessages.ODEN_CLI_COMMAND_task_run + " " + '"'
						+ taskname + '"' + " "
						+ CommandMessages.ODEN_CLI_OPTION_json;
			
			
			try {
				runCommand(runcommnd);
				
			} catch (Exception odenException) {
				OdenActivator
						.error(
								UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Exception_RunTask,
								odenException);
			}
			super.close();
		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while deploying items.",
					odenException);
		}
	}

	@SuppressWarnings("unchecked")
	private ArrayList<DeployByTaskInfo> returnList(String commnd) {
		ArrayList<DeployByTaskInfo> returnList = new ArrayList<DeployByTaskInfo>();

		try {
			String result = OdenBroker.sendRequest(shellurl, commnd);
			if (result != null) {
				JSONArray array = new JSONArray(result);
				String path = "";
				String item = "";
				String agent = "";
				String repo = "";
				String mode = "";
				for (int i = 0; i < array.length(); i++) {
					JSONObject full = (JSONObject) array.get(i);
					for (Iterator<String> it = full.keys(); it.hasNext();) {
						String keys = it.next();
						if (keys.equals("agent")) {
							JSONObject agents = new JSONObject(full
									.getString("agent"));
							agent = agents.getString("name");
						} else if (keys.equals("repo")) {
							JSONArray repos = new JSONArray(full
									.getString("repo"));
							repo = repos.getString(0);
						} else if (keys.equals("mode")) {
							mode = full.getString("mode");
							if (mode.equals("A"))
								mode = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Add;
							else if (mode.equals("U"))
								mode = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Update;
							else if (mode.equals("D"))
								mode = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Delete;
						} else if (keys.equals("path")) {
							String file = full.getString("path");
							if (file.indexOf("/") > 0) {
								// unix
								path = file.substring(0, file.lastIndexOf("/"));
								item = file
										.substring(file.lastIndexOf("/") + 1);
							} else if (file.lastIndexOf(File.separator) > 0) {
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
					DeployByTaskInfo DeployItem = new DeployByTaskInfo(repo, path,
							item, agent, mode);
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

	private String runCommand(String commnd) throws OdenException {

		try {
			return OdenBroker.sendRequest(shellurl, commnd);
		} catch (OdenException e) {
		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while running deploy item.",
					odenException);
		}
		return null;
		
	}

	/**
	 * Closes the dialog box Override so we can delete temporary policy and task
	 */
	public boolean close() {
		return super.close();
	}

	private void getTaskList() {
		
		String commnd = ""; 
		String result = ""; 
		addlist = new ArrayList<combolist>();
		
		commnd = CommandMessages.ODEN_CLI_COMMAND_task_info_json;
		
		try {	
			result = OdenBroker.sendRequest(this.shellurl, commnd);
			if (result != null && !(result.equals(""))) {
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
						combolist list = new combolist();
						list.setName(name);
						list.setDesc(desc);
						list.setCombolist(name + " " + "(" + desc + ")");
						
						if(!(name.matches(CommandMessages.ODEN_CLI_COMMAND_task_tempname+".*"))){
							taskCombo.add(list.getCombolist());
							addlist.add(list);
						}
					}
				}
				taskCombo.select(0);
				taskCombo.setData(addlist);
			}
		} catch (OdenException odenException) {
		} catch (Exception e) {
			OdenActivator.error("Exception occured while loading Initial info.",e);
		}
	}
	/*
	 *	the task information of combo list   
	 */
	class combolist {
		String name;
		String desc;
		String combolist;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		public String getCombolist() {
			return combolist;
		}
		public void setCombolist(String combolist) {
			this.combolist = combolist;
		}
	}
}