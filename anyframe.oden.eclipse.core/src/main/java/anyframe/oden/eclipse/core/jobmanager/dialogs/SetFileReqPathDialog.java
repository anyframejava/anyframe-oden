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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.alias.FileRequest;
import anyframe.oden.eclipse.core.editors.PolicyContentProvider;
import anyframe.oden.eclipse.core.jobmanager.dialogs.CreateSetFileReqPathDialog.Type;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Set the file path of File Request(Ex. spectrum interface). 
 * This class extends TitleAreaDialog class.
 * 
 * @author HONG JungHwan
 * @version 1.1.0
 * 
 */


public class SetFileReqPathDialog extends TitleAreaDialog {

	private Table table;
	
	private TableViewer viewer;
	
	private Button btnInsert;
	
	private Button btnEdit;
	
	private Button btnDelete;

	// Strings and messages from message properties
	private String title = UIMessages.ODEN_JOBMANAGER_Dialogs_SetFileReqDialog_Title;
	private String subtitle = UIMessages.ODEN_JOBMANAGER_Dialogs_SetFileReqDialog_SubTitle;
	
	// Oden dialog image which appears on the upper right of the panel
	private ImageDescriptor odenImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_EXPLORER_Dialogs_OdenImageURL);
	
	private ArrayList<FileRequest> filerequest;
	
	private SetFileReqPathDialog setfilereqdialog = this;
	
	public SetFileReqPathDialog(Shell parentShell) {
		super(parentShell);
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(title);
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
				ImageUtil.disposeImage(UIMessages.ODEN_EXPLORER_Dialogs_OdenImageURL);
			}
		});
		// TODO 도움말 만든 후 아래 내용을 확인할 것
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, OdenActivator.HELP_PLUGIN_ID + ".oden.odenexplorerview");

		return contents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		// Top level composite
		
		Composite parentComposite = (Composite) super.createDialogArea(parent);

		Composite composite = new Composite(parentComposite, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Create profile name group
		Composite compTable = new Composite(composite, SWT.NONE);
		
		layout.numColumns = 2;
		layout.marginWidth = 10;
		layout.verticalSpacing = 4;
		
		compTable.setLayout(layout);
		
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 4;
		
		compTable.setLayoutData(data);

		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 4;
		
		table = new Table(compTable, SWT.MULTI | SWT.FULL_SELECTION| SWT.V_SCROLL | SWT.BORDER);
		
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
		viewer.setContentProvider(new PolicyContentProvider());
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		data = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL);
		data.verticalSpan = 4;
		data.heightHint = 100;
		table.setLayoutData(data);
		
		TableViewerColumn nickNameColumn = new TableViewerColumn(viewer, SWT.NONE);
		
		nickNameColumn.getColumn().setAlignment(SWT.LEFT);
		nickNameColumn.getColumn().setText("NickName");
		nickNameColumn.getColumn().setWidth(85);
		nickNameColumn.setLabelProvider(new nicknameColumnLabelProvider());

		TableViewerColumn descColumn = new TableViewerColumn(viewer, SWT.NONE);
		
		descColumn.getColumn().setAlignment(SWT.LEFT);
		descColumn.getColumn().setText("Description");
		descColumn.getColumn().setWidth(100);
		descColumn.setLabelProvider(new descColumnLabelProvider());
		
		TableViewerColumn pathColumn = new TableViewerColumn(viewer, SWT.NONE);
		
		pathColumn.getColumn().setAlignment(SWT.LEFT);
		pathColumn.getColumn().setText("Path");
		pathColumn.getColumn().setWidth(320);
		pathColumn.setLabelProvider(new pathColumnLabelProvider());
		
		TableViewerColumn serverColumn = new TableViewerColumn(viewer, SWT.NONE);
		
		serverColumn.getColumn().setAlignment(SWT.LEFT);
		serverColumn.getColumn().setText("Server To Use");
		serverColumn.getColumn().setWidth(100);
		serverColumn.setLabelProvider(new serverColumnLabelProvider());
		
		Label label = new Label(compTable, SWT.NULL);
		label.setText("");
		
		btnInsert = new Button(compTable, SWT.NONE);
		GridData buttonData = new GridData();
		
		buttonData.widthHint = 60;
		btnInsert.setText("Add...");
		btnInsert.setLayoutData(buttonData);
		btnInsert.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				CreateSetFileReqPathDialog dialog; 
				dialog = new CreateSetFileReqPathDialog(
							Display.getCurrent().getActiveShell(), Type.CREATE,
							new FileRequest(),setfilereqdialog );
								
				dialog.open();
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		});

		label.setText("");
		
		btnEdit = new Button(compTable, SWT.NONE);
		btnEdit.setText("Edit...");
		btnEdit.setLayoutData(buttonData);
		btnEdit.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				ISelection selection = viewer.getSelection();
				CreateSetFileReqPathDialog dialog = null;
				
				FileRequest filerequest = (FileRequest) ((IStructuredSelection) selection).getFirstElement();
				if(filerequest != null) {  
					dialog = new CreateSetFileReqPathDialog(
							Display.getCurrent().getActiveShell(), Type.CHANGE,
							filerequest ,setfilereqdialog );
					dialog.open();
				}
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		});

		label.setText("");
		
		btnDelete = new Button(compTable, SWT.NONE);
		btnDelete.setText("Remove");
		btnDelete.setLayoutData(buttonData);
		btnDelete.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				ISelection selection = viewer.getSelection();
				FileRequest filerequest = (FileRequest) ((IStructuredSelection) selection).getFirstElement();
				
				if (filerequest != null) {
					OdenActivator.getDefault().getAliasManager().getFileRequestManager().removeFileRequest(filerequest.getNickname());
				}

				// notify that there has been changes
				OdenActivator.getDefault().getAliasManager().getFileRequestManager().modelChanged();

				// reload data for data consistency
				try {
					OdenActivator.getDefault().getAliasManager().save();
					OdenActivator.getDefault().getAliasManager().load();
				} catch (OdenException odenException) {
					OdenActivator.error("Exception occured while reloading Build Repository profiles.", odenException);
					odenException.printStackTrace();
				}
				initData();
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		});
		initData();
		
		return parentComposite;
	}
	// table label provider
	
	public class nicknameColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((FileRequest) element).getNickname();
		}
	}
	
	public class descColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((FileRequest) element).getDesc();
		}
	}
	
	public class pathColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((FileRequest) element).getPath();
		}
	}
	
	public class serverColumnLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {

			return ((FileRequest) element).getServerToUse();
		}
	}
	
	/*
	 * Loading File Request *.xml file
	 */
	public void initData() {
		Collection<FileRequest> filerequsts =  OdenActivator.getDefault().getAliasManager().getFileRequestManager().getFileRequests();
		this.filerequest = new ArrayList<FileRequest>();
		for(FileRequest filerequest : filerequsts)
			this.filerequest.add(filerequest);
		
		viewer.setInput(this.filerequest);
		viewer.refresh();
	}
}
