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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.alias.DeployNow;
import anyframe.oden.eclipse.core.alias.Repository;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Set Deploy Now Information(deployment target Server, location Var).
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC2
 *
 */
public class SetDeployNowDialog extends TitleAreaDialog {

	private Table table;
	
	private TableViewer viewer;
	
	private String[] attributeAgentArray;
	
	@SuppressWarnings("unused")
	private String[] originAttributeAgentArray;
	
	private String[] attributeLocationValArray;
	
	private final String MSG_AGENT_INFO = CommandMessages.ODEN_CLI_COMMAND_agent_info_json;
	
	private String shellurl;
	
	private String deployUrl;
	
	private Object obj;
	
	private HashMap<String, String> hm;
	
	private Label msg;
	
	// Strings and messages from message properties
	private String title = UIMessages.ODEN_EXPLORER_Dialogs_SetDeployNowDialog_Title;
	private String subtitle = UIMessages.ODEN_EXPLORER_Dialogs_SetDeployNowDialog_SubTitle;

	// Oden dialog image which appears on the upper right of the panel
	private ImageDescriptor odenImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_EXPLORER_Dialogs_OdenImageURL);
	
	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();
	
	private Repository repository;
	
	private DeployNow deploynow;
	
	private Set<DeployNow> deploynows;
	
	private TreeMap<String, DeployNow> deployNowMap;
	
	private Button alltodefault;
	
	private Button btnInsert;
	
	private Button btnDelete;
	
	private TreeMap<String,HashMap<String,String>> allAgentInfo ;
	
	public SetDeployNowDialog(Shell parentShell , Object obj , DeployNow deploynow) {
		super(parentShell);
		this.obj = obj;
		repository = OdenActivator.getDefault().getAliasManager().getRepositoryManager().getRepository(obj.toString());
		this.deploynow = deploynow;
	}
	
	public SetDeployNowDialog(Shell parentShell , Object obj , Set<DeployNow> deploynows) {
		super(parentShell);
		this.obj = obj;
		repository = OdenActivator.getDefault().getAliasManager().getRepositoryManager().getRepository(obj.toString());
		this.deploynows = deploynows;
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		validate();
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

	protected Control createDialogArea(Composite parent) {

		// Top level composite
		Composite parentComposite = (Composite) super.createDialogArea(parent);

		// Create a composite with standard margins and spacing
		Composite composite = new Composite(parentComposite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parentComposite.getFont());

		// Create select datagrid
		Composite compTable = new Composite(composite, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 10;
		layout.verticalSpacing = 4;
		
		compTable.setLayout(layout);
		
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 4;
		
		compTable.setLayoutData(data);
		
		alltodefault = new Button(compTable, SWT.CHECK);
		alltodefault.setText(UIMessages.ODEN_EXPLORER_Dialogs_SetDeployNowDialog_AllToDefaultLocation);
		alltodefault.setLayoutData(data);
		alltodefault.setSelection(repository.isAllToDefault());
		
			
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
		table.setHeaderVisible(false);
		table.setLinesVisible(true);
		
		data = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL);
		data.verticalSpan = 4;
		data.heightHint = 100;
		table.setLayoutData(data);
		
		TableColumn agentName = new TableColumn(table, 0);
		agentName.setAlignment(SWT.LEFT);
		agentName.setText("");
		agentName.setWidth(85);

		TableColumn locationVar = new TableColumn(table, 0);
		locationVar.setAlignment(SWT.LEFT);
		locationVar.setText("");
		locationVar.setWidth(100);
		
		TableColumn urlColumn = new TableColumn(table, 0);
		urlColumn.setAlignment(SWT.LEFT);
		urlColumn.setText("");
		urlColumn.setWidth(320);
		
		attachContentProvider(viewer);
		attachLabelProvider(viewer);
		attachCellEditors(viewer, table);
		
		final CellEditor[] cellEditors = viewer.getCellEditors();
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent arg) {
				try {
					ISelection selected = arg.getSelection();
					Object obj = ((StructuredSelection) selected).getFirstElement();
					if(obj == null)
						return; //avoid nullpointException
					String tempStr = ((SetDeployNowInputAttribute) obj).getAttributeAgent();
					
					getLocVar(tempStr);
					((ComboBoxCellEditor) cellEditors[1]).setItems(attributeLocationValArray);
				} catch (Exception odenException) {
					OdenActivator.error("Exception occured while add selection change.",odenException);
					odenException.printStackTrace();
				}
			}
		});
		
		((ComboBoxCellEditor) cellEditors[0]).addListener(new ICellEditorListener() {
			
			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
			}
			
			public void cancelEditor() {
			}
			
			public void applyEditorValue() {
				((ComboBoxCellEditor) cellEditors[1]).setItems(attributeLocationValArray);
			}
		});

		((ComboBoxCellEditor) cellEditors[1]).addListener(new ICellEditorListener() {
			
			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
			}
			
			public void cancelEditor() {
			}
			
			public void applyEditorValue() {
				try {
					String[] arr = ((ComboBoxCellEditor) cellEditors[1]).getItems();
					String temp = ((ComboBoxCellEditor) cellEditors[1]).getValue().toString();
					
					for(int i = 0 ; i < arr.length ; i ++)
						if(temp.equals(Integer.toString(i)))
							deployUrl = hm.get(arr[i]);
				} catch(Exception e) {
					System.out.println(e.getMessage());
				}
			}
		});
		
		Label label = new Label(compTable, SWT.NULL);
		label.setText("");
		
		btnInsert = new Button(compTable, SWT.NONE);
		GridData buttonData = new GridData();
		buttonData.widthHint = 60;
		btnInsert.setText("Add");
		btnInsert.setLayoutData(buttonData);
		btnInsert.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				SetDeployNowInputAttribute input = new SetDeployNowInputAttribute();
				input.setAttributeAgent(attributeAgentArray[0]);
				input.setAttributeLocationVal(attributeLocationValArray[0]);
				getLocVar(attributeAgentArray[0]);
				
				HashMap<String,String> agent = allAgentInfo.get(attributeAgentArray[0]);
				input.setAttributeDeployUrl(agent.get(attributeLocationValArray[0]));
				viewer.add(input);
				SetDeployNowInputAttribute.addElementToInputParamVect(input);
				SetDeployNowDialog.this.validate();
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
				int selectedIndex = table.getSelectionIndex();
				if (selectedIndex > -1) {
					table.remove(selectedIndex);
					SetDeployNowInputAttribute.getInputParamVect().remove(selectedIndex);
					SetDeployNowDialog.this.validate();
				} else
					msg.setText(" Please select the criteria you want to remove.");
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		});
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		label.setText("");
		
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 3;
		msg = new Label(parent, SWT.NONE);
		msg.setText("");
		msg.setLayoutData(data);
		
		getAllAgentInfo();
		
		alltodefaultEvent();
		
		if(! deploynows.isEmpty())
			loadingData();
//		else
//			initData();
		
		return parentComposite;
	}

	private void alltodefaultEvent() {
		alltodefault.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				boolean checked = alltodefault.getSelection();
				if (checked) {
					btnInsert.setEnabled(false);
					btnDelete.setEnabled(false);
					table.setEnabled(false);
					setDialogComplete(true);
				} else {
					btnInsert.setEnabled(true);
					btnDelete.setEnabled(true);
					table.setEnabled(true);
				}
			}
		});
		
		if(repository.isAllToDefault()) {
			btnInsert.setEnabled(false);
			btnDelete.setEnabled(false);
			table.setEnabled(false);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadingData() {
		viewer.getTable().clearAll();
		SetDeployNowInputAttribute.clearInputParamVect();
		
		for(DeployNow deploynow : deploynows) {
			SetDeployNowInputAttribute attribute = new SetDeployNowInputAttribute();
			attribute.setAttributeAgent(deploynow.getDestinedAgentName());
			attribute.setAttributeLocationVal(deploynow.getDestinedLocation());
			HashMap<String,String> agent = allAgentInfo.get(deploynow.getDestinedAgentName());
			attribute.setAttributeDeployUrl(agent.get(deploynow.getDestinedLocation()));
			
			SetDeployNowInputAttribute.addElementToInputParamVect(attribute);
		}
		Iterator itr = SetDeployNowInputAttribute.getInputParamVect().iterator();
		while (itr.hasNext())
			viewer.add(itr.next());
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(title);
	}

	protected void okPressed() {
		try {
			setAgetntInfo();
		} catch(OdenException odenException) {
			OdenActivator.error("Exception occured while saving deploy now info.",odenException);
		}
		close();
	}
	
	protected void cancelPressed() {
		SetDeployNowInputAttribute.clearInputParamVect();
		super.cancelPressed();
	}
	
	/*
	 * validation check only one agent selection
	 */
	private void validate() {
		int index = 0;
		HashMap<String, String> dupCheck = new HashMap<String,String>();
		if (!(alltodefault.getSelection())) {
			Vector<SetDeployNowInputAttribute> factor = SetDeployNowInputAttribute.getInputParamVect();
			for(SetDeployNowInputAttribute attribute : factor) {
				dupCheck.put(attribute.getAttributeAgent(), attribute.getAttributeLocationVal());
				if(attribute != null) {
					index++;
				}
			}
			if(dupCheck.size() == index ) {
				setDialogComplete(true);
				msg.setText("");
			} else {
				if(! alltodefault.getSelection()) {
					setDialogComplete(false);
					msg.setText("     " + CommonMessages.ODEN_CommonMessages_NameAlreadyExists);
				}
			}
		}
	}
	
	private void setDialogComplete(boolean b) {
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (okButton != null)
			okButton.setEnabled(b);
	}

	private void setAgetntInfo() throws OdenException{
		Vector<SetDeployNowInputAttribute> inputParam = SetDeployNowInputAttribute.getInputParamVect();
		deployNowMap = new TreeMap<String, DeployNow>();
		try {
			repository.setAllToDefault(alltodefault.getSelection());
			if(inputParam.size() > 0 && ! alltodefault.getSelection()) {
				for(SetDeployNowInputAttribute attribute : inputParam) {
					deploynow = new DeployNow();
					deploynow.setRepository(repository);
					deploynow.setDestinedAgentName(attribute.getAttributeAgent());
					deploynow.setDestinedLocation(attribute.getAttributeLocationVal());
					
					deployNowMap.put(attribute.getAttributeAgent(), deploynow);
				}
				repository.setDeployNowMap(deployNowMap);
			}
			OdenActivator.getDefault().getAliasManager().getRepositoryManager().saveRepositories();
		} catch (OdenException odenexcption){
			throw odenexcption;
		}
	}
	
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE);
	}
	
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

	private void attachLabelProvider(TableViewer viewer) {
		viewer.setLabelProvider(new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch (columnIndex) {
				
				case 0:
					return ((SetDeployNowInputAttribute) element).getAttributeAgent();
				case 1:
					return ((SetDeployNowInputAttribute) element).getAttributeLocationVal();
				case 2:
					return ((SetDeployNowInputAttribute) element).getAttributeDeployUrl();

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

	private void attachCellEditors(final TableViewer viewerObj, Composite parent) {
		getAgents();
		getLocVar(attributeAgentArray[0]);
		
		viewerObj.setCellModifier(new ICellModifier() {
			
			public boolean canModify(Object element, String property) {
				return true;
			}
			
			public Object getValue(Object element, String property) {
				if(property.equals("attributeAgent")) {
					int i = Arrays.asList(attributeAgentArray).indexOf(
							((SetDeployNowInputAttribute) element)
									.getAttributeAgent());
					return i == -1 ? null : new Integer(i);
				} else if(property.equals("attributeLocationVal")){
					int i = Arrays.asList(attributeLocationValArray).indexOf(
							((SetDeployNowInputAttribute) element)
									.getAttributeLocationVal());
					if( i == -1) {
						getLocVar(((SetDeployNowInputAttribute) element).getAttributeAgent());
						i = Arrays.asList(attributeLocationValArray).indexOf(
								((SetDeployNowInputAttribute) element)
								.getAttributeLocationVal());
					}
					
					return i == -1 ? null : new Integer(i);
				} else if (property.equals("attributeDeployUrl")) {
					return ((SetDeployNowInputAttribute) element).getAttributeDeployUrl();
				} 
				return null;
			}
			
			public void modify(Object element, String property, Object value) {
				TableItem tableItem = (TableItem) element;
				SetDeployNowInputAttribute attribute = (SetDeployNowInputAttribute) tableItem.getData();
				
				if(property.equals("attributeAgent")){
					int i = ((Integer) value).intValue();
					
					attribute.setAttributeAgent(attributeAgentArray[i]);
					attribute.setAttributeLocationVal(attributeLocationValArray[0]);
					
					HashMap<String,String> agent = allAgentInfo.get(attributeAgentArray[i]);
					deployUrl = agent.get(attributeLocationValArray[0]);
					
					attribute.setAttributeDeployUrl(deployUrl);
					SetDeployNowDialog.this.validate();
				} else if(property.equals("attributeLocationVal")) {
					int i = ((Integer) value).intValue();
					if( i != -1 && attribute.getAttributeAgent() != null) {
						attribute.setAttributeLocationVal(attributeLocationValArray[i]);
						attribute.setAttributeDeployUrl(deployUrl);
					}
					
				} else if (property.equals("attributeDeployUrl")) {
					attribute.setAttributeDeployUrl(value.toString());
				}
				
				viewer.refresh(attribute);
			}
			
		});
		viewer.setCellEditors(new CellEditor[] {
				new ComboBoxCellEditor(parent, attributeAgentArray,SWT.READ_ONLY),
				new ComboBoxCellEditor(parent, attributeLocationValArray,SWT.READ_ONLY)});

			viewer.setColumnProperties(new String[] { "attributeAgent","attributeLocationVal","attributeDeployUrl"});
		}	
	
	private void getAgents() {
		String result = "";
		getShellurl();
		try {
			result = OdenBroker.sendRequest(shellurl, MSG_AGENT_INFO);
			if (result != null) {
				JSONArray array = new JSONArray(result);
				if(array.length() > 0) {
					attributeAgentArray = new String[array.length()];
					for (int i = 0; i < array.length(); i++) {
						String name = (String) ((JSONObject) array.get(i)).get("name");
						attributeAgentArray[i] = name;
					}
					originAttributeAgentArray = attributeAgentArray.clone();
					
				} else {
					OdenActivator.warning(CommonMessages.ODEN_CommonMessages_SetConfigXML);
				}
			} else {
				// no connection
				OdenActivator.warning(CommonMessages.ODEN_CommonMessages_UnableToConnectServer);
			}
		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while set Deploy Now Dialog.",odenException);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void getLocVar(String agentName) {
		String result = "";
		hm = new HashMap<String, String>();
		try {
			result = OdenBroker.sendRequest(shellurl, MSG_AGENT_INFO);
			if (result != null) {
				JSONArray array = new JSONArray(result);
				if(array.length() > 0) {
					
					for (int i = 0; i < array.length(); i++) {
						String name = (String) ((JSONObject) array.get(i)).get("name");
						if (name.equals(agentName)) {
							int j = 1;
							JSONObject locs = (JSONObject) ((JSONObject) array.get(i)).get("locs");
							Iterator it = locs.keys();
							attributeLocationValArray = new String[locs.length() + 1];
							attributeLocationValArray[0] = UIMessages.ODEN_EDITORS_PolicyPage_DialogAgent_ComboDefault;
							
							String root = (String) ((JSONObject) array.get(i)).get("host") + "/";
							String defaultLocation = (String) ((JSONObject) array.get(i)).get("loc");
							hm.put(UIMessages.ODEN_EDITORS_PolicyPage_DialogAgent_ComboDefault, root + defaultLocation);
							while (it.hasNext()) {
								Object o = it.next();
								attributeLocationValArray[j] = o.toString(); 
								j++;
								hm.put(o.toString(), root + locs.getString(o.toString()));
							}
							break;
						}
					}
					
				} else {
					OdenActivator.warning(CommonMessages.ODEN_CommonMessages_SetConfigXML);
				}
			} else {
				// no connection
				OdenActivator.warning(CommonMessages.ODEN_CommonMessages_UnableToConnectServer);
			}
		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while set Deploy Now Dialog.",odenException);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void getAllAgentInfo() {
		String result = "";
		getShellurl();
		allAgentInfo = new TreeMap<String, HashMap<String,String>>();
		try {
			result = OdenBroker.sendRequest(shellurl, MSG_AGENT_INFO);
			if (result != null) {
				JSONArray array = new JSONArray(result);
				if(array.length() > 0) {
					for (int i = 0; i < array.length(); i++) {
						HashMap<String,String> locVar = new HashMap<String,String>();
						String name = (String) ((JSONObject) array.get(i)).get("name");
						JSONObject locs = (JSONObject) ((JSONObject) array.get(i)).get("locs");
						Iterator it = locs.keys();
						String root = (String) ((JSONObject) array.get(i)).get("host") + "/";
						String defaultLocation = (String) ((JSONObject) array.get(i)).get("loc");
						locVar.put(UIMessages.ODEN_EDITORS_PolicyPage_DialogAgent_ComboDefault, root + defaultLocation);
						allAgentInfo.put(name, locVar);
						while (it.hasNext()) {
							Object o = it.next();
							locVar.put(o.toString(), root + locs.getString(o.toString()));
							allAgentInfo.put(name, locVar);
						}
					}
				} else {
					OdenActivator.warning(CommonMessages.ODEN_CommonMessages_SetConfigXML);
				}
			} else {
				// no connection
				OdenActivator.warning(CommonMessages.ODEN_CommonMessages_UnableToConnectServer);
			}
		} catch (Exception odenException) {
			OdenActivator.error("Exception occured while set Deploy Now Dialog.",odenException);
		}
	}
	private void getShellurl() {
		Repository repo = OdenActivator.getDefault().getAliasManager().getRepositoryManager().getRepository(obj.toString());
		String serverName = repo.getServerToUse();
		String serverUrl = OdenActivator.getDefault().getAliasManager().getServerManager().getServer(serverName).getUrl();
		shellurl = CommonMessages.ODEN_CommonMessages_ProtocolString_HTTP + serverUrl + CommonMessages.ODEN_CommonMessages_ProtocolString_HTTPsuf;
	}
	
}