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
package anyframe.oden.eclipse.core.editors.dialogs;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenMessages;
import anyframe.oden.eclipse.core.brokers.OdenBroker;
import anyframe.oden.eclipse.core.editors.PolicyPage;
import anyframe.oden.eclipse.core.utils.CommonUtil;
import anyframe.oden.eclipse.core.utils.DialogUtil;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Select Deployment Target Agent. This class extends TitleAreaDialog class.
 * 
 * @author HONG Junghwan
 * @version 1.0.0 RC3
 * 
 */
public class SelectAgentsDialog extends TitleAreaDialog {

	// Strings and messages from message properties
	//	private String title = "Select the deployment target agents";
	//	private String subtitle = "Select the deployment target agents and location variables";

	// Oden dialog image which appears on the upper right of the panel
	private ImageDescriptor odenImageDescriptor = ImageUtil
	.getImageDescriptor(OdenMessages.ODEN_EXPLORER_Dialogs_OdenImageURL);
	private static final String MSG_AGENT_INFO = OdenMessages.ODEN_EDITORS_PolicyPage_MsgAgentInfo;
	private String shellurl;
	private HashMap<String, String> hm;
	private HashMap<String, String> locVariable;

	private Combo agentCombo;
	private Combo locationVar;

	private Label targetUrl;
	private Label targetStatement;

	public SelectAgentsDialog(Shell parentShell, String shellUrl)
	throws Exception {
		super(parentShell);
		shellurl = shellUrl;
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText(OdenMessages.ODEN_EXPLORER_Dialogs_SelectAgentsDialog_Title);

	}

	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		// validate();
	}

	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		setTitle(OdenMessages.ODEN_EXPLORER_Dialogs_SelectAgentsDialog_Title);
		setMessage(OdenMessages.ODEN_EXPLORER_Dialogs_SelectAgentsDialog_SubTitle);

		Image odenImage = ImageUtil.getImage(odenImageDescriptor);
		if (odenImage != null) {
			setTitleImage(odenImage);
		}
		contents.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent disposeEvent) {
				ImageUtil
				.disposeImage(OdenMessages.ODEN_EXPLORER_Dialogs_OdenImageURL);
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

		// TODO : select agent and location variable
		Label deploy = new Label(nameGroup, SWT.NONE);
		deploy.setText(OdenMessages.ODEN_EDITORS_PolicyPage_Man_DeployTarget);

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
		Label location = new Label(nameGroup, SWT.LEFT | SWT.WRAP);
		location.setText(OdenMessages.ODEN_EDITORS_PolicyPage_Man_LocationVar);

		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 2;
		data.widthHint = 256;
		locationVar = new Combo(nameGroup, SWT.SINGLE | SWT.BORDER
				| SWT.DROP_DOWN);
		locationVar.setLayoutData(data);

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
				| GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 2;

		targetUrl= new Label(nameGroup, SWT.WRAP);
		targetUrl.setText("");
		targetUrl.setLayoutData(data);

		// TODO : Grid End
		locationVar.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {

				if(!(locationVar.getText().equals("")))
					setagentUrl();
				else
					targetUrl.setText("");
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		agentCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setagentUrl();
				setLocationVar();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				setagentUrl();
				setLocationVar();
			}
		});

		return parentComposite;
	}

	protected void okPressed() {
		// input agent information
		PolicyPage page = new PolicyPage();
		if(!(agentCombo.getText().equals("")) && !(locationVar.getText().equals(""))){
			if(locationVar.getText().equals(OdenMessages.ODEN_EDITORS_PolicyPage_DialogBotAgent_ComboDefault)){
				locationVar.setText("");
			}
			if (page.checkAddDeploy(agentCombo.getText(), locationVar.getText())) {
				page.addDeploy(agentCombo.getText(), locationVar.getText());
				close();
			} else {
				DialogUtil.openMessageDialog(OdenMessages.ODEN_CommonMessages_Title_Warning,
						OdenMessages.ODEN_CommonMessages_NameAlreadyExists,
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
						String urlRoot = (String) ((JSONObject) array.get(i))
						.get("host")
						+ "/"
						+ (String) ((JSONObject) array.get(i)).get("loc");
						JSONObject locs = (JSONObject) ((JSONObject) array.get(i))
						.get("locs");
						agentCombo.add(name);
						hm.put(name, urlRoot);
					}
					agentCombo.select(0);
					locationVar.removeAll();
					setLocationVar();
				} else {
					OdenActivator.warning(OdenMessages.ODEN_CommonMessages_SetConfigXML);
				}
			} else {
				// no connection
				OdenActivator.warning(OdenMessages.ODEN_CommonMessages_UnableToConnectServer);
			}
		} catch (Exception odenException) {
			OdenActivator
			.error(
					OdenMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Exception_SearchDeployItem,
					odenException);
			odenException.printStackTrace();
		}
	}

	private void setLocationVar() {
		String result = "";
		String commnd = MSG_AGENT_INFO;
		locVariable = new HashMap<String, String>();
		try {
			locationVar.removeAll();
			locationVar.add(OdenMessages.ODEN_EDITORS_PolicyPage_DialogBotAgent_ComboDefault);
			result = OdenBroker.sendRequest(shellurl, commnd);
			if (result != null) {
				JSONArray array = new JSONArray(result);
				if(array.length() > 0){
					for (int i = 0; i < array.length(); i++) {
						String name = (String) ((JSONObject) array.get(i))
						.get("name");
						if (name.equals(agentCombo.getText())) {
							JSONObject locs = (JSONObject) ((JSONObject) array
									.get(i)).get("locs");
							Iterator it = locs.keys();
							while (it.hasNext()) {
								Object o = it.next();
								String locUri = CommonUtil.replaceIgnoreCase(locs.getString(o.toString()), (String) ((JSONObject) array.get(i)).get("loc"), "");
								locationVar.add(o.toString());
								locVariable.put(o.toString(), locUri);
							}
						}
					}
					locationVar.redraw();
					locationVar.select(0);
					setagentUrl();
				}
			}
		} catch (Exception odenException) {
			OdenActivator
			.error(
					OdenMessages.ODEN_EXPLORER_Dialogs_DeployItemDialog_Exception_SearchDeployItem,
					odenException);
			odenException.printStackTrace();
		}
	}

	private void setagentUrl() {
		String preStatement = OdenMessages.ODEN_EDITORS_PolicyPage_DialogBotAgent_Statement;
		targetStatement.setText(preStatement);
		if(locationVar.getText().equals(OdenMessages.ODEN_EDITORS_PolicyPage_DialogBotAgent_ComboDefault)){
			targetUrl.setText(hm.get(agentCombo.getText())) ;
		} else {
			targetUrl.setText(hm.get(agentCombo.getText()) + locVariable.get(locationVar.getText()));
		}
	}
}
