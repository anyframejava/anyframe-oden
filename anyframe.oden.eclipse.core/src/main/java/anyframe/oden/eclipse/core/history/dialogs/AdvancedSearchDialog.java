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
package anyframe.oden.eclipse.core.history.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import anyframe.oden.eclipse.core.history.DeploymentHistoryView;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * AdvancedSearchDialog,
 * for the Anyframe Oden Advanced Deployment History.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC2
 *
 */
public class AdvancedSearchDialog extends Dialog {

	private Table table;

	private TableViewer viewer;

	private String[] attributeNameArray;

	private String[] attributeRelationArrayForName;

	private String[] attributeRelationArrayForIp;

	private String[] attributeRelationArrayForDate;
	
	private String[] attributeRelationArrayForAgent;
	
	private String[] attributeRelationArrayForStatus;
	
	private DeploymentHistoryView view;

	private Label msg;

	@SuppressWarnings("unchecked")
	private ArrayList advancedList;

	public AdvancedSearchDialog(Shell shell, DeploymentHistoryView view) {
		super(shell);
		this.view = view;
	}

	public AdvancedSearchDialog(Shell shell) {
		super(shell);

	}

	protected void configureShell(Shell newShell) {
		newShell.setText("Advanced Search");
		super.configureShell(newShell);
		newShell.setBounds(300, 200, 550, 320);
	}

	@SuppressWarnings("unchecked")
	protected Control createDialogArea(final Composite parent) {

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		parent.setLayout(gridLayout);

		GridData grpData = new GridData(GridData.FILL_BOTH);
		final Group grpMain = new Group(parent, SWT.V_SCROLL);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		grpMain.setLayout(gridLayout);
		grpMain.setLayoutData(grpData);

		GridData gd = new GridData(GridData.FILL_BOTH);
		parent.setLayoutData(gd);

		Label lblFollowingTrue = new Label(grpMain, SWT.BOLD);
		GridData layoutData = new GridData(GridData.BEGINNING);
		lblFollowingTrue.setLayoutData(layoutData);
		lblFollowingTrue.setText("All of the following are true.");

		Composite compTable = new Composite(grpMain, SWT.NONE);
		GridLayout glCompTable = new GridLayout();
		glCompTable.numColumns = 2;
		glCompTable.verticalSpacing = 4;

		compTable.setLayout(glCompTable);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;

		compTable.setLayoutData(gridData);

		table = new Table(compTable, SWT.MULTI | SWT.FULL_SELECTION
				| SWT.V_SCROLL | SWT.BORDER);

		table.addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				TableItem item = (TableItem) event.item;
				String text = item.getText(event.index);
				Point size = event.gc.textExtent(text);
				event.width = size.x + 2 * 6;
				event.height = Math.max(event.height, size.y + 6);

			}
		});

		viewer = new TableViewer(table);
		table.setHeaderVisible(false);
		table.setLinesVisible(true);

		gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL);
		gridData.verticalSpan = 6;
		table.setLayoutData(gridData);

		TableColumn tableColumn = new TableColumn(table, 0);
		tableColumn.setAlignment(0x1000000);
		tableColumn.setText("");

		tableColumn = new TableColumn(table, 0);
		tableColumn.setAlignment(0x1000000);
		tableColumn.setText("");

		tableColumn = new TableColumn(table, 0);
		tableColumn.setAlignment(0x1000000);
		tableColumn.setText("");

		for (int i = 0; i < 3; i++) {
			TableColumn tc = table.getColumn(i);
			tc.setWidth(105);
		}
		attachContentProvider(viewer);
		attachLabelProvider(viewer);
		attachCellEditors(viewer, table);

		if (AdvancedSearchInputAttribute.getInputParamVect().size() < 1) {
			AdvancedSearchInputAttribute input = new AdvancedSearchInputAttribute();
			input.setAttributeName("Item Name");
			input.setAttributeRelation("is");
			input.setAttributeValue("");
			viewer.add(input);
			AdvancedSearchInputAttribute.addElementToInputParamVect(input);
		} else {
			Iterator itr = AdvancedSearchInputAttribute.getInputParamVect()
					.iterator();
			while (itr.hasNext())
				viewer.add(itr.next());
		}
		final CellEditor[] cellEditors = viewer.getCellEditors();

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent arg0) {
				ISelection selected = arg0.getSelection();
				Object obj = ((StructuredSelection) selected).getFirstElement();
				if (obj == null)
					return; // Avoiding NullPointerException on Mac/Eclipse

				String tempStr = ((AdvancedSearchInputAttribute) obj)
						.getAttributeName();
				
				if (tempStr.equals("Item Name")) {
					((ComboBoxCellEditor) cellEditors[1])
							.setItems(attributeRelationArrayForName);
				} else if (tempStr.equals("IP")) {
					((ComboBoxCellEditor) cellEditors[1])
							.setItems(attributeRelationArrayForIp);
				} else if (tempStr.equals("Deployed Date")) {
					((ComboBoxCellEditor) cellEditors[1])
							.setItems(attributeRelationArrayForDate);
				} else if (tempStr.equals("Agent")) {
					((ComboBoxCellEditor) cellEditors[1])
					.setItems(attributeRelationArrayForAgent);
				} else if (tempStr.equals("Deployment Status")) {
					((ComboBoxCellEditor) cellEditors[1])
					.setItems(attributeRelationArrayForStatus);
					((TextCellEditor) cellEditors[2]).deactivate();
					((TextCellEditor) cellEditors[2]).setValue("");
				}
			}
		});

		((ComboBoxCellEditor) cellEditors[0])
				.addListener(new ICellEditorListener() {
					public void editorValueChanged(boolean arg0, boolean arg1) {
					}

					public void applyEditorValue() {
						String tempStr = (String) Arrays
								.asList(attributeNameArray)
								.get(
										((Integer) ((ComboBoxCellEditor) cellEditors[0])
												.getValue()).intValue());
						if (tempStr.equals("Item Name")) {
							((ComboBoxCellEditor) cellEditors[1])
									.setItems(attributeRelationArrayForName);
						} else if (tempStr.equals("IP")) {
							((ComboBoxCellEditor) cellEditors[1])
									.setItems(attributeRelationArrayForIp);
						} else if (tempStr.equals("Deployed Date")) {
							((ComboBoxCellEditor) cellEditors[1])
									.setItems(attributeRelationArrayForDate);
						} else if (tempStr.equals("Agent")) {
							((ComboBoxCellEditor) cellEditors[1])
							.setItems(attributeRelationArrayForAgent);
						} else if (tempStr.equals("Deployment Status")) {
							((ComboBoxCellEditor) cellEditors[1]).setItems(attributeRelationArrayForStatus);
							((TextCellEditor) cellEditors[2]).deactivate();
							((TextCellEditor) cellEditors[2]).setValue("");
						}
					}

					public void cancelEditor() {

					}
				});
		Label label = new Label(compTable, SWT.NULL);
		label.setText("");
		Button btnInsert = new Button(compTable, SWT.NONE);
		GridData buttonData = new GridData();
		buttonData.widthHint = 60;
		btnInsert.setText("Add");
		btnInsert.setLayoutData(buttonData);
		btnInsert.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				AdvancedSearchInputAttribute input = new AdvancedSearchInputAttribute();
				input.setAttributeName("Item Name");
				input.setAttributeRelation("is");
				input.setAttributeValue("");
				viewer.add(input);
				AdvancedSearchInputAttribute.addElementToInputParamVect(input);
				msg.setText("");
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		});

		Button btnDelete = new Button(compTable, SWT.NONE);
		btnDelete.setText("Remove");
		btnDelete.setLayoutData(buttonData);
		btnDelete.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				int selectedIndex = table.getSelectionIndex();
				if (selectedIndex > -1) {
					table.remove(selectedIndex);
					AdvancedSearchInputAttribute.getInputParamVect().remove(selectedIndex);
				} else
					msg.setText(" Please select the criteria you want to remove.");
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		});
		msg = new Label(parent, SWT.NONE);
		msg.setText("");

		((TextCellEditor) cellEditors[2])
				.addListener(new ICellEditorListener() {
					public void applyEditorValue() {
						if (!((TextCellEditor) cellEditors[2]).getValue()
								.toString().equals("")) {
							msg.setText("");
						} else {
							msg.setText(" ");
						}
					}

					public void cancelEditor() {

					}

					public void editorValueChanged(boolean arg0, boolean arg1) {

					}
				});

		return super.createDialogArea(parent);
	}

	/**
	 * Content provider for Input Parameter table viewer
	 * 
	 * @param viewer
	 *            TableViewer object
	 */
	private void attachContentProvider(TableViewer viewer) {
		viewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				return (Object[]) inputElement;
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
		});
	}

	/**
	 * Label provider for Input Parameter table viewer
	 * 
	 * @param viewer
	 *            TableViewer object
	 */
	private void attachLabelProvider(TableViewer viewer) {
		viewer.setLabelProvider(new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {

				switch (columnIndex) {
				case 0:
					return ((AdvancedSearchInputAttribute) element)
							.getAttributeName();
				case 1:
					return ((AdvancedSearchInputAttribute) element)
							.getAttributeRelation();
				case 2:
					return ((AdvancedSearchInputAttribute) element)
							.getAttributeValue();

				default:
					return "";
				}
			}

			public void addListener(ILabelProviderListener listener) {
			}

			public void dispose() {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {

			}
		});
	}

	/**
	 * This method is called when the user modifes a cell in the tableViewer
	 * 
	 * @param viewer
	 *            Input Parameter table viewer
	 * @param parent
	 *            The composite object
	 */
	private void attachCellEditors(final TableViewer viewerObj, Composite parent) {
		attributeNameArray = new String[] { "Item Name", "IP", "Deployed Date" , "Agent" , "Deployment Status"};
		attributeRelationArrayForName = new String[] { "is" };
		attributeRelationArrayForIp = new String[] { "is" };
		attributeRelationArrayForDate = new String[] { "is", "before", "after" };
		attributeRelationArrayForAgent = new String[] { "is" };
		attributeRelationArrayForStatus = new String[] { "Failure" };
		
		viewerObj.setCellModifier(new ICellModifier() {

			public boolean canModify(Object element, String property) {
				return true;
			}

			public Object getValue(Object element, String property) {
				if (property.equals("attributeName")) {
					int i = Arrays.asList(attributeNameArray).indexOf(
							((AdvancedSearchInputAttribute) element)
									.getAttributeName());
					return i == -1 ? null : new Integer(i);
				} else if (property.equals("attributeRelation")) {
					int i = Arrays.asList(attributeRelationArrayForName)
							.indexOf(
									((AdvancedSearchInputAttribute) element)
											.getAttributeRelation());
					if (i == -1)
						i = Arrays.asList(attributeRelationArrayForDate)
								.indexOf(
										((AdvancedSearchInputAttribute) element)
												.getAttributeRelation());
					if (i == -1)
						i = Arrays.asList(attributeRelationArrayForIp).indexOf(
								((AdvancedSearchInputAttribute) element)
										.getAttributeRelation());
					if (i == -1)
						i = Arrays.asList(attributeRelationArrayForAgent).indexOf(
								((AdvancedSearchInputAttribute) element)
										.getAttributeRelation());
					if (i == -1)
						i = Arrays.asList(attributeRelationArrayForStatus).indexOf(
								((AdvancedSearchInputAttribute) element)
										.getAttributeRelation());
					return i == -1 ? null : new Integer(i);
				} else if (property.equals("attributeValue"))
					return ((AdvancedSearchInputAttribute) element)
							.getAttributeValue();
				else
					return null;
			}

			public void modify(Object element, String property, Object value) {
				TableItem tableItem = (TableItem) element;

				AdvancedSearchInputAttribute advSearchInputAttribute = (AdvancedSearchInputAttribute) tableItem
						.getData();
				if (property.equals("attributeName")) {
					int i = ((Integer) value).intValue();
					if ((advSearchInputAttribute.getAttributeName().equals(
							"Item Name")
							|| advSearchInputAttribute.getAttributeName()
									.equals("IP") || advSearchInputAttribute
							.getAttributeName().equals("Deployment Status"))
							&& (attributeNameArray[i].equals("Deployed Date")))
						advSearchInputAttribute
								.setAttributeRelation(attributeRelationArrayForDate[0]);
					else if ((advSearchInputAttribute.getAttributeName()
							.equals("Deployed Date")
							|| advSearchInputAttribute.getAttributeName()
									.equals("Deployment Status") || advSearchInputAttribute
							.getAttributeName().equals("IP"))
							&& (attributeNameArray[i].equals("Item Name") || attributeNameArray[i]
									.equals("Deployed Date")))
						advSearchInputAttribute
								.setAttributeRelation(attributeRelationArrayForName[0]);
					else if ((advSearchInputAttribute.getAttributeName()
							.equals("Deployed Date")
							|| advSearchInputAttribute.getAttributeName()
									.equals("Item Name") || advSearchInputAttribute
							.getAttributeName().equals("Deployment Status"))
							&& (attributeNameArray[i].equals("IP")))
						advSearchInputAttribute
								.setAttributeRelation(attributeRelationArrayForIp[0]);
					advSearchInputAttribute
							.setAttributeName(attributeNameArray[i]);

				} else if (property.equals("attributeRelation")) {
					int i = ((Integer) value).intValue();

					if (i != -1
							&& (advSearchInputAttribute.getAttributeName()
									.equals("Item Name"))) {
						advSearchInputAttribute
								.setAttributeRelation(attributeRelationArrayForName[i]);
					} else if (i != -1
							&& advSearchInputAttribute.getAttributeName()
									.equals("IP")) {
						advSearchInputAttribute
								.setAttributeRelation(attributeRelationArrayForIp[i]);
					} else if (i != -1
							&& advSearchInputAttribute.getAttributeName()
									.equals("Deployed Date")) {
						advSearchInputAttribute
								.setAttributeRelation(attributeRelationArrayForDate[i]);
					} else if (i != -1
							&& advSearchInputAttribute.getAttributeName()
							.equals("Agent")) {
						advSearchInputAttribute
							.setAttributeRelation(attributeRelationArrayForAgent[i]);
					} else if (i != -1
							&& advSearchInputAttribute.getAttributeName()
							.equals("Deployment Status")) {
						advSearchInputAttribute
							.setAttributeRelation(attributeRelationArrayForStatus[i]);
						
					}
				} else if (property.equals("attributeValue")) {
					advSearchInputAttribute.setAttributeValue(value.toString());

				} else {
				} // do nothing
				viewer.refresh(advSearchInputAttribute);
			}
		});

		viewer.setCellEditors(new CellEditor[] {
				new ComboBoxCellEditor(parent, attributeNameArray,
						SWT.READ_ONLY),
				new ComboBoxCellEditor(parent, attributeRelationArrayForName,
						SWT.READ_ONLY), new TextCellEditor(parent) });

		viewer.setColumnProperties(new String[] { "attributeName",
				"attributeRelation", "attributeValue" });
	}

	protected void cancelPressed() {
		AdvancedSearchInputAttribute.clearInputParamVect();
		super.cancelPressed();
	}

	protected void okPressed() {
		view.getTree().removeAll();
		
		final Job creatingMarkersJob = new Job("Creating Markers...") {

			protected IStatus run(IProgressMonitor monitor) {

				monitor.beginTask("Creating Markers...", 1000);
				monitor.subTask("Getting Queries...");

				monitor.done();
				return Status.OK_STATUS;
			}
		};

		creatingMarkersJob.setSystem(false);
		creatingMarkersJob.setUser(false);

		Job gettingQueryIdsJob = new Job("Searching Queries...") {
			@SuppressWarnings("unchecked")
			protected IStatus run(IProgressMonitor monitor) {

				monitor.beginTask("Searching Queries...", 1000);
				monitor.subTask("Getting Queries...");
				ArrayList<String[]> List = new ArrayList<String[]>();
				Vector searchCriteria = AdvancedSearchInputAttribute
						.getInputParamVect();
				for (int i = 0; i < searchCriteria.size(); i++) {
					AdvancedSearchInputAttribute advSrchInput = (AdvancedSearchInputAttribute) searchCriteria
							.get(i);

					String name = advSrchInput.getAttributeName();
					String relation = advSrchInput.getAttributeRelation();
					String value = advSrchInput.getAttributeValue();
					String[] inputArr = { name, relation, value };

					List.add(inputArr);
				}

				final ArrayList arrayList = view.gettingHistories(monitor, List);

				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					public void run() {
						view.setHistoryList(arrayList);
						view.setTreeData();

						view.getHistorySearchView().setText(UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Found
								+ view.getTree().getItemCount() + " "
								+ "transactions" + " "
								+ view.getCount() + " "
								+ UIMessages.ODEN_HISTORY_DeploymentHistoryView_Items);

					}
				});
				monitor.done();
				return Status.OK_STATUS;
			}
		};

		gettingQueryIdsJob.setSystem(false);
		gettingQueryIdsJob.setUser(true);

		gettingQueryIdsJob.addJobChangeListener(new JobChangeAdapter() {
			public void done(IJobChangeEvent arg0) {
				if (advancedList != null && advancedList.size() != 0) {
					creatingMarkersJob.schedule();
				}
			}
		});
		gettingQueryIdsJob.schedule();
		super.okPressed();
	}

	protected void createButtonsForButtonBar(Composite parent) {
		// create Search and Cancel buttons
		createButton(parent, IDialogConstants.OK_ID, "Search", true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}
}
