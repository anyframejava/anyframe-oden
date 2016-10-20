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
package anyframe.oden.eclipse.core.editors.dialogs;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.editors.PolicyPage;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.DialogUtil;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Select Deployment Target Server. This class extends TitleAreaDialog class.
 * 
 * @author HONG JungHwan
 * @version 1.0.0 RC3
 * 
 */
public class SelectAgentsDialog extends TitleAreaDialog {

	private ImageDescriptor odenImageDescriptor = ImageUtil
	.getImageDescriptor(UIMessages.ODEN_EXPLORER_Dialogs_OdenImageURL);
	private static final String MSG_AGENT_INFO = CommandMessages.ODEN_CLI_COMMAND_agent_info_json;
	private String shellurl;
	private HashMap<String, String> hm;
	private HashMap<String, String> locVariable;

	private Combo agentCombo;
	private Combo locationVar;
	private Text addlocation;
	
	private Label targetUrl;
	private Label targetStatement;
	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();
	private PolicyPage page;
	private String root;
	public SelectAgentsDialog(Shell parentShell, String shellUrl , PolicyPage page)
	throws Exception {
		super(parentShell);
		shellurl = shellUrl;
		this.page = page;
		
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText(UIMessages.ODEN_EXPLORER_Dialogs_SelectAgentsDialog_Title);

	}

	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		// validate();
	}

	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		setTitle(UIMessages.ODEN_EXPLORER_Dialogs_SelectAgentsDialog_Title);
		setMessage(UIMessages.ODEN_EXPLORER_Dialogs_SelectAgentsDialog_SubTitle);

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

		// set combo data
		setInformation();

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

		// Create profile name group
		Composite nameGroup = new Composite(composite, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 10;
		nameGroup.setLayout(layout);

		GridData data = new GridData(GridData.FILL_BOTH);
		nameGroup.setLayoutData(data);

		// TODO : select server and location variable
		Label deploy = new Label(nameGroup, SWT.NONE);
		deploy.setText(UIMessages.ODEN_EDITORS_PolicyPage_Man_DeployTarget);

		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 2;
		data.widthHint = 256;
		agentCombo = new Combo(nameGroup, SWT.SINGLE | SWT.BORDER
				| SWT.DROP_DOWN | SWT.LEFT);
		agentCombo.setLayoutData(data);

		data = new GridData();
		data.heightHint = 15;

		createSpacer( nameGroup , 3, 1);
		Label labellocvar = new Label(nameGroup, SWT.LEFT | SWT.WRAP);
		labellocvar.setText(UIMessages.ODEN_EDITORS_PolicyPage_Man_LocationVar);

		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 2;
		data.widthHint = 256;
		locationVar = new Combo(nameGroup, SWT.SINGLE | SWT.BORDER
				| SWT.DROP_DOWN);
		locationVar.setLayoutData(data);

		createSpacer( nameGroup , 3, 3);

		Label labellocation = new Label(nameGroup, SWT.LEFT | SWT.WRAP);
		labellocation.setText("Location");

		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 2;
		data.widthHint = 256;
		addlocation = new Text(nameGroup, SWT.SINGLE | SWT.BORDER);
		addlocation.setLayoutData(data);
		
		createSpacer( nameGroup , 3, 3);
		
		Label dummy = new Label(nameGroup , SWT.WRAP);
		dummy.setText("");

		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 2;

		targetStatement= new Label(nameGroup, SWT.WRAP);
		targetStatement.setText("");
		targetStatement.setLayoutData(data);
		//		createSpacer( nameGroup , 3, 1);

		dummy = new Label(nameGroup , SWT.WRAP);
		dummy.setText("");

		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL | GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		data.verticalSpan = 5;
		
		targetUrl= new Label(nameGroup, SWT.WRAP);
		targetUrl.setText("");
		targetUrl.setLayoutData(data);

		// TODO : Grid End
		locationVar.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {

				if(!(locationVar.getText().equals(""))){
					setagentUrl();
				} else {
					targetUrl.setText("");
				}
				SelectAgentsDialog.this.validate();
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		agentCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setLocationVar();
				setagentUrl();
				
				
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				setLocationVar();
				setagentUrl();
			}
		});
		
		addlocation.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {

			}

			public void keyReleased(KeyEvent ke) {
//				setLocationVar();
				setagentUrl();
				SelectAgentsDialog.this.validate();
			}
		});

		return parentComposite;
	}

	protected void okPressed() {
		// input server information
		if(!(agentCombo.getText().equals("")) && !(locationVar.getText().equals(""))){
//			if(locationVar.getText().equals(UIMessages.ODEN_EDITORS_PolicyPage_DialogAgent_ComboDefault)){
//				locationVar.setText("");
//			}
			if (page.checkAddDeploy(agentCombo.getText(), locationVar.getText())) {
				page.addDeploy(agentCombo.getText(), locationVar.getText() , addlocation.getText());
				close();
			} else {
				DialogUtil.openMessageDialog(CommonMessages.ODEN_CommonMessages_Title_Warning,
						CommonMessages.ODEN_CommonMessages_NameAlreadyExists,
						MessageDialog.INFORMATION);
				agentCombo.select(0);
				agentCombo.setFocus();
			}
		}
	}

	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE);
	}

	private void createSpacer(Composite parent, int span, int height) {
		Label spacer = new Label(parent, SWT.NONE);
		GridData gd = new GridData();
		gd.heightHint = height;
		gd.horizontalSpan = span;
		spacer.setLayoutData(gd);
	}

	@SuppressWarnings("unchecked")
	private void setInformation() {
		String result = "";
		String commnd = MSG_AGENT_INFO;
		hm = new HashMap();
		try {
			result = OdenBroker.sendRequest(shellurl, commnd);
			if (result != null) {
				JSONArray array = new JSONArray(result);
				if(array.length() > 0) {
					for (int i = 0; i < array.length(); i++) {
						String name = (String) ((JSONObject) array.get(i))
						.get("name");
						String urlRoot = (String) ((JSONObject) array.get(i)).get("loc");
						
						agentCombo.add(name);
						hm.put(name, urlRoot);
					}
					agentCombo.select(0);
					locationVar.removeAll();
					setLocationVar();
				} else {
					OdenActivator.warning(CommonMessages.ODEN_CommonMessages_SetConfigXML);
				}
			} else {
				// no connection
				OdenActivator.warning(CommonMessages.ODEN_CommonMessages_UnableToConnectServer);
			}
		} catch (Exception odenException) {
			OdenActivator
			.error(
					UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Exception_SearchDeployItem,
					odenException);
			odenException.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void setLocationVar() {
		String result = "";
		String commnd = MSG_AGENT_INFO;
		locVariable = new HashMap<String, String>();
		try {
			locationVar.removeAll();
			locationVar.add(UIMessages.ODEN_EDITORS_PolicyPage_DialogAgent_ComboDefault);
			
			locVariable.put(UIMessages.ODEN_EDITORS_PolicyPage_DialogAgent_ComboAbsolutePath, "");
			
			result = OdenBroker.sendRequest(shellurl, commnd);
			if (result != null) {
				JSONArray array = new JSONArray(result);
				if(array.length() > 0){
					for (int i = 0; i < array.length(); i++) {
						String name = (String) ((JSONObject) array.get(i)).get("name");
						if (name.equals(agentCombo.getText())) {
							JSONObject locs = (JSONObject) ((JSONObject) array.get(i)).get("locs");
							Iterator it = locs.keys();
							while (it.hasNext()) {
								Object o = it.next();
								String locUri = locs.getString(o.toString());
								locationVar.add(o.toString());
								locVariable.put(o.toString(), locUri);
							}
							root = (String) ((JSONObject) array.get(i)).get("host");
						}
					}
					locationVar.add(UIMessages.ODEN_EDITORS_PolicyPage_DialogAgent_ComboAbsolutePath);
					locationVar.redraw();
					locationVar.select(0);
					setagentUrl();
				}
			}
		} catch (Exception odenException) {
			OdenActivator
			.error(
					UIMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Exception_SearchDeployItem,
					odenException);
			odenException.printStackTrace();
		}
	}

	private void setagentUrl() {
		String preStatement = UIMessages.ODEN_EDITORS_PolicyPage_DialogAgent_Statement;
		String url;
		
		if(locationVar.getText().equals(UIMessages.ODEN_EDITORS_PolicyPage_DialogAgent_ComboDefault)){
			url = addlocation.getText().equals("") ? hm.get(agentCombo.getText()) : hm.get(agentCombo.getText()) + "/" + addlocation.getText();
		} else {
			url = addlocation.getText().equals("") ? locVariable.get(locationVar.getText()) : locVariable.get(locationVar.getText()) + "/" + addlocation.getText();
		}
		targetStatement.setText(preStatement);
		targetUrl.setText(url + " on " + root);
	}
	
	/*
	 * validation check mandatory input value 
	 */
	private void validate() {
		if(locationVar.getText().equals(UIMessages.ODEN_EDITORS_PolicyPage_DialogAgent_ComboAbsolutePath)){
			if(addlocation.getText().equals("")) {
				this.setDialogComplete(false);
			} else {
				this.setDialogComplete(true);
			}
		} else {
			this.setDialogComplete(true);
		}
	}
	private void setDialogComplete(boolean b) {
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (okButton != null)
			okButton.setEnabled(b);
	}

}
