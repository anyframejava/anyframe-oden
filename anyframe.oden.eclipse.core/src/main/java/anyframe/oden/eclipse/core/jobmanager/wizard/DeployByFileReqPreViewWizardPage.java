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
package anyframe.oden.eclipse.core.jobmanager.wizard;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.editors.PolicyContentProvider;
import anyframe.oden.eclipse.core.jobmanager.dialogs.DeployByTaskInfo;
import anyframe.oden.eclipse.core.jobmanager.dialogs.DeployByTaskTableViewerSorter;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Deploy by File Request preview (Ex. spectrum interface). This class extends Wizard
 * class.
 * 
 * @author HONG JungHwan
 * @version 1.1.0
 * 
 */


public class DeployByFileReqPreViewWizardPage extends WizardPage {
	
	Table table;
	TableViewer tableViewer;
	TableViewerColumn column1, column2, column3, column4, column5;
	
	private String col1 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Mode;
	private String col2 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Repo;
	private String col3 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_DeployPath;
	private String col4 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_DeployItem;
	private String col5 = UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_DeployAgent;
	
	private String testcommand;
	
	@SuppressWarnings("unused")
	private String runcommand;
	
	private String shellUrl;
	
	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();
	
	private ArrayList<DeployByTaskInfo> deployItem;
	
	private Label itemCount;
	
	private Label confirmMessage;
	
	// for using count Map
	private TreeMap<String,String> itemCountMap;
	
	DeployByFileReqPreViewWizardPage() {
		super("getPreViewPage");
		
		setTitle(UIMessages.ODEN_JOBMANAGER_Wizards_RepositoryPreView_Title);
		setDescription(UIMessages.ODEN_JOBMANAGER_Wizards_RepositoryPreView_Description);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		
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
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		itemCount = new Label(composite, 0);
		
		itemCount
		.setText(0
				+ " "
				+ UIMessages.ODEN_JOBMANAGER_Wizards_RepositoryPreView_DeployedItem
				+ " "
				+ UIMessages.ODEN_JOBMANAGER_Wizards_RepositoryPreView_TotalItemCount
				+ " " 
				+ 0);

		
		itemCount.setLayoutData(data);
		
		confirmMessage = new Label(composite,0);
		confirmMessage.setText(UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_ItemStatement);
		
		tableViewer.refresh();
		
		setControl(composite);
	}
	
	/*
	 * setter command, url using next page.
	 */
	public void setTestCommand(String testcommand) {
		this.testcommand = testcommand;
	}

	public void setShellUrl(String shellUrl) {
		this.shellUrl = shellUrl;
	}

	public void setRuncommand(String runcommand) {
		this.runcommand = runcommand;
	}
	
	/*
	 * get preview page using next page.
	 */
	public void getPreview() {
		deployItem = searchDeployItem();
		tableViewer.setInput(deployItem);

		itemCount
		.setText(itemCountMap.size()
				+ " "
				+ UIMessages.ODEN_JOBMANAGER_Wizards_RepositoryPreView_DeployedItem
				+ " "
				+ UIMessages.ODEN_JOBMANAGER_Wizards_RepositoryPreView_TotalItemCount
				+ " " 
				+ tableViewer.getTable().getItemCount());
		
	}
	
	
	@SuppressWarnings("unchecked")
	private ArrayList<DeployByTaskInfo> searchDeployItem() {
		ArrayList<DeployByTaskInfo> returnList = new ArrayList<DeployByTaskInfo>();
		itemCountMap = new TreeMap<String, String>();
		
		try {
			String result = OdenBroker.sendRequest(shellUrl, testcommand);
			
			if (result != null) {
				JSONArray array = new JSONArray(result);
				String path = "";
				String item = "";
				String agent = "";
				String repo = "";
				String mode = "";
				String file = "";
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
							file = full.getString("path");
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
					if(! repo.equals("")) {
						DeployByTaskInfo DeployItem = new DeployByTaskInfo(repo, path,
								item, agent, mode);
						returnList.add(DeployItem);
						itemCountMap.put(file, item);
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
	 *  table label provider
	 */
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
	
	/*
	 * flag of finish or not 
	 */
	public boolean isComplete() {
        return tableViewer.getTable().getItems().length > 0 ? true : false;
    }
}
