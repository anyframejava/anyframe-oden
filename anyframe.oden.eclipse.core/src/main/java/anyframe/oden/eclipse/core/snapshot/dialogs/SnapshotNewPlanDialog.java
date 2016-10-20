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
package anyframe.oden.eclipse.core.snapshot.dialogs;

import java.util.ArrayList;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.snapshot.SnapshotView;
import anyframe.oden.eclipse.core.snapshot.SnapshotViewContentProvider;
import anyframe.oden.eclipse.core.utils.Cmd;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Create a new snapshot plan dialog.
 * 
 * @author LEE Sujeong
 * @version 1.0.0 RC2
 * 
 */
public class SnapshotNewPlanDialog extends TitleAreaDialog {

	public enum Type {
		ADD, COPY
	}

	private Type type;
	private String selection;
	private static Text textPlanName;
	private static Text textPlanDesc;
	private HashMap<String, String> hm;
	private HashMap<String, String> locVariable;
	private Combo agentCombo;
	private Combo locationVar;
	private Text sourceLocPathText;
	private Label total;
	private String strLocVar = "";
	private String strAbsPath = "";

	public static String server;

	protected static OdenBrokerService OdenBroker = new OdenBrokerImpl();

	private static String MSG_SNAPHOST_NEW_PLAN = ""; //$NON-NLS-1$
	private static String SHELL_URL;

	private static String[] SOURCE_OPT = { "source", "s" }; //$NON-NLS-1$ //$NON-NLS-2$
	private static String[] DESC_OPT = { "desc" }; //$NON-NLS-1$

	private static final String MSG_SNAPSHOT_INFO_PLAN = CommandMessages.ODEN_CLI_COMMAND_snapshot_planinfo;

	private String titleAdd = UIMessages.ODEN_SNAPSHOT_Dialogs_TextDlgMainInfo;
	private String subtitleAdd = UIMessages.ODEN_SNAPSHOT_Dialogs_TextDlgSubInfo;

	private String titleCopy = UIMessages.ODEN_SNAPSHOT_Dialogs_DuplicateTitle;
	private String subtitleCopy = UIMessages.ODEN_SNAPSHOT_Dialogs_DuplicateSubTitle;

	private ImageDescriptor odenImageDescriptor = ImageUtil
			.getImageDescriptor(UIMessages.ODEN_EXPLORER_Dialogs_OdenImageURL);

	/**
	 * Constructor of SnapshotNewPlanDialog
	 * 
	 * @param parent
	 * @param type
	 * @param selection
	 */
	public SnapshotNewPlanDialog(Shell parent, Type type, String selection) {
		super(parent);
		this.type = type;
		this.selection = selection;
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (type == Type.ADD) {
			shell.setText(UIMessages.ODEN_SNAPSHOT_Dialogs_TextDlgTitle);
		} else if (type == Type.COPY) {
			shell.setText(UIMessages.ODEN_SNAPSHOT_Dialogs_Duplicate);
		}
	}

	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		validate();
	}

	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);

		if (type == Type.ADD) {
			setTitle(titleAdd);
			setMessage(subtitleAdd);
		} else if (type == Type.COPY) {
			setTitle(titleCopy);
			setMessage(subtitleCopy);
		}

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

		return contents;
	}

	protected Control createDialogArea(Composite parent) {

		String source = null, desc = null;
		Composite parentComposite = (Composite) super.createDialogArea(parent);

		Composite composite = new Composite(parentComposite, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		GridLayout layout = new GridLayout(3, false);
		composite.setLayoutData(data);
		composite.setLayout(layout);
		layout.marginHeight = 20;
		layout.marginWidth = 25;
//		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
//		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
//		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
//		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);

		Label labelPlanName = new Label(composite, SWT.NULL);
		labelPlanName.setText(UIMessages.ODEN_SNAPSHOT_Dialogs_LabelPlanName);

		textPlanName = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 2;
		gridData1.heightHint = 15;
		if (type != Type.ADD) {
			textPlanName.setText(selection + "-duplicated"); //$NON-NLS-1$

			String url = "http://" //$NON-NLS-1$
					+ OdenActivator.getDefault().getAliasManager()
							.getServerManager().getServer(server).getUrl()
					+ "/shell"; //$NON-NLS-1$

			String result = ""; //$NON-NLS-1$
			try {
				result = SnapshotViewContentProvider.getInfo(url,
						CommandMessages.ODEN_CLI_COMMAND_snapshot_planinfo
								+ " " + "\"" + selection + "\"" + " -json"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			} catch (OdenException e) {
				OdenActivator
						.error(
								UIMessages.ODEN_SNAPSHOT_Actions_Exception_GetSnapshotPlanInfo,
								e);
			}

			String tempResult = ""; //$NON-NLS-1$

			try {
				JSONArray ja = new JSONArray(result);
				JSONObject jo = ja.getJSONObject(0);
				tempResult = jo.getString(selection);
			} catch (JSONException e) {
				OdenActivator
						.error(
								UIMessages.ODEN_SNAPSHOT_Actions_Exception_ParseSnapshotPlanInfo,
								e);
			}

			Cmd cmd = null;
			cmd = new Cmd("\"" + selection + "\"" + " = " + tempResult); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			source = cmd.getOptionArg(SOURCE_OPT);
			desc = cmd.getOptionArg(DESC_OPT);

		}
		textPlanName.setLayoutData(gridData1);
		textPlanName.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent keyEvent) {
				SnapshotNewPlanDialog.this.validate();
			};

			public void keyReleased(KeyEvent keyEvent) {
				SnapshotNewPlanDialog.this.validate();
			};
		});

		Label labelPlanDesc = new Label(composite, SWT.NULL);
		labelPlanDesc
		.setText(UIMessages.ODEN_SNAPSHOT_Dialogs_LabelPlanDescription);
		
		textPlanDesc = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridData5 = new GridData(GridData.FILL_HORIZONTAL);
		gridData5.horizontalSpan = 2;
		if (type != Type.ADD) {
			textPlanDesc.setText(desc);
		}
		textPlanDesc.setLayoutData(gridData5);
		
		Label labelDestAgent = new Label(composite, SWT.NULL);
		labelDestAgent
				.setText(UIMessages.ODEN_SNAPSHOT_Dialogs_LabelDestinationAgent);

		agentCombo = new Combo(composite, SWT.SINGLE | SWT.BORDER
				| SWT.DROP_DOWN | SWT.LEFT | SWT.READ_ONLY);
		GridData gridData10 = new GridData(GridData.FILL_HORIZONTAL);
		gridData10.horizontalSpan = 2;
		setAgentVar();

		agentCombo.setLayoutData(gridData10);

		agentCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setLocationVar();
				sourceLocPathText.setText(""); //$NON-NLS-1$
				SnapshotNewPlanDialog.this.validate();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				setLocationVar();
				sourceLocPathText.setText(""); //$NON-NLS-1$
				SnapshotNewPlanDialog.this.validate();
			}
		});

		Label labelPlanSource = new Label(composite, SWT.NULL);
		labelPlanSource
				.setText(UIMessages.ODEN_SNAPSHOT_Dialogs_LabelPlanSource);

		locationVar = new Combo(composite, SWT.SINGLE | SWT.BORDER
				| SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		gridData2.horizontalSpan = 2;
		setLocationVar();
		locationVar.setLayoutData(gridData2);

		locationVar.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setTotalInfoLabel();
				SnapshotNewPlanDialog.this.validate();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				setTotalInfoLabel();
				SnapshotNewPlanDialog.this.validate();
			}
		});

		Label hideLabel = new Label(composite, SWT.SINGLE | SWT.RIGHT);
		hideLabel.setText(""); //$NON-NLS-1$

		sourceLocPathText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridDataPath = new GridData(GridData.FILL_HORIZONTAL);
		gridDataPath.horizontalSpan = 2;
		sourceLocPathText.setLayoutData(gridDataPath);
		sourceLocPathText.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				strAbsPath = sourceLocPathText.getText();
				SnapshotNewPlanDialog.this.validate();
				total.setText(strLocVar+strAbsPath);
			}
			public void keyPressed(KeyEvent e) {
				strAbsPath = sourceLocPathText.getText();
				SnapshotNewPlanDialog.this.validate();
				total.setText(strLocVar+strAbsPath);
			}
		});

		Label hideLabel2 = new Label(composite, SWT.SINGLE | SWT.RIGHT);
		hideLabel2.setText(""); //$NON-NLS-1$

		total = new Label(composite, SWT.WRAP | SWT.READ_ONLY);
		GridData gridDataTotal = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL | GridData.FILL_BOTH);
		gridDataTotal.horizontalSpan = 2;
		gridDataTotal.verticalSpan = 5;
		total.setText("");
		setTotalInfoLabel();
		total.setLayoutData(gridDataTotal);


		return composite;
	}

	protected void setTotalInfoLabel() {
		if (locationVar.getText()
				.equals(UIMessages.ODEN_SNAPSHOT_SnapshotView_AbsolutePathComboText)) {
			sourceLocPathText.setText(""); //$NON-NLS-1$
			strLocVar = "";
			strAbsPath = "";
		} else if (locationVar.getText()
				.equals(UIMessages.ODEN_SNAPSHOT_SnapshotView_DefaultLocComboText)) {
			sourceLocPathText.setText(""); //$NON-NLS-1$
			strLocVar = hm.get(agentCombo.getText());
			strAbsPath = "";
		} else {
			sourceLocPathText.setText(""); //$NON-NLS-1$
			strLocVar = locVariable.get(locationVar.getText());
			strAbsPath = "";
		}
		total.setText(strLocVar+strAbsPath);
	}

	private void setAgentVar() {
		String result = ""; //$NON-NLS-1$
		String commnd = CommandMessages.ODEN_CLI_COMMAND_agent_info_json;
		hm = new HashMap<String, String>();

		try {
			result = OdenBroker.sendRequest(SnapshotView.SHELL_URL, commnd);
			if (result != null) {
				JSONArray array = new JSONArray(result);
				if (array.length() > 0) {
					for (int i = 0; i < array.length(); i++) {
						String name = (String) ((JSONObject) array.get(i))
								.get("name"); //$NON-NLS-1$
						String urlRoot = (String) ((JSONObject) array.get(i))
								.get("loc"); //$NON-NLS-1$

						agentCombo.add(name);
						hm.put(name, urlRoot);
					}
					agentCombo.select(0);
				} else {
					OdenActivator
							.warning(CommonMessages.ODEN_CommonMessages_SetConfigXML);
				}
			} else {
				// no connection
				OdenActivator
						.warning(CommonMessages.ODEN_CommonMessages_UnableToConnectServer);
			}
		} catch (Exception odenException) {
			OdenActivator
					.error(
							UIMessages.ODEN_SNAPSHOT_SnapshotView_Exception_MsgAgentInfo,
							odenException);
			odenException.printStackTrace();
		}
	}

	private void setLocationVar() {
		String result = ""; //$NON-NLS-1$
		String commnd = CommandMessages.ODEN_CLI_COMMAND_agent_info_json;
		locVariable = new HashMap<String, String>();
		try {
			locationVar.removeAll();
			locationVar
					.add(UIMessages.ODEN_SNAPSHOT_SnapshotView_DefaultLocComboText);
			locationVar
					.add(UIMessages.ODEN_SNAPSHOT_SnapshotView_AbsolutePathComboText);
			result = OdenBroker.sendRequest(SnapshotView.SHELL_URL, commnd);
			if (result != null) {
				JSONArray array = new JSONArray(result);
				if (array.length() > 0) {
					for (int i = 0; i < array.length(); i++) {
						String name = (String) ((JSONObject) array.get(i))
								.get("name"); //$NON-NLS-1$
						if (name.equals(agentCombo.getText())) {
							JSONObject locs = (JSONObject) ((JSONObject) array
									.get(i)).get("locs"); //$NON-NLS-1$
							Iterator it = locs.keys();
							while (it.hasNext()) {
								Object o = it.next();
								String locUri = locs.getString(o.toString());
								locationVar.add(o.toString());
								locVariable.put(o.toString(), locUri);
							}
						}
					}
					locationVar.redraw();
					locationVar.select(0);
				}
			}
		} catch (Exception odenException) {
			OdenActivator
					.error(
							UIMessages.ODEN_SNAPSHOT_SnapshotView_Exception_MsgAgentInfo,
							odenException);
			odenException.printStackTrace();
		}
	}

	protected void okPressed() {
		addSnapshotplan();
		close();
	}

	private void addSnapshotplan() {

		SHELL_URL = "http://" //$NON-NLS-1$
				+ OdenActivator.getDefault().getAliasManager()
						.getServerManager().getServer(server).getUrl()
				+ "/shell"; //$NON-NLS-1$
		if (!checkValidation()) {

			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					Boolean result = MessageDialog.openConfirm(Display
							.getDefault().getActiveShell(),
							UIMessages.ODEN_SNAPSHOT_Dialogs_MsgValidation,
							UIMessages.ODEN_SNAPSHOT_Dialogs_MsgValidationInfo);
					if (result == true) {
						addSnapshot();
					}
				}
			});
		} else {
			addSnapshot();
		}
		SnapshotView.refreshTree();
	}

	private Boolean checkValidation() {
		Boolean result = null;
		ArrayList<String> snapshotPlanList = SnapshotViewContentProvider
				.getSnaphotPlanList(SHELL_URL, MSG_SNAPSHOT_INFO_PLAN
						+ " -json"); //$NON-NLS-1$
		if (snapshotPlanList.size() == 0) {
			result = true;
		} else {
			for (int i = 0; i < snapshotPlanList.size(); i++) {
				Object plan = snapshotPlanList.get(i);

				if (plan.equals(textPlanName.getText())) {
					result = false;
					break;
				} else {
					result = true;
				}
			}
		}
		return result;
	}

	private void addSnapshot() {

		String sourceLocation = distinctionLoc(locationVar.getText())
				+ sourceLocPathText.getText();

		if (sourceLocation.equals("") || sourceLocation==null) { // default-location
			MSG_SNAPHOST_NEW_PLAN = CommandMessages.ODEN_CLI_COMMAND_snapshot_add
					+ " "
					+ "\"" //$NON-NLS-1$
					+ textPlanName.getText().trim()
					+ "\"" + " " + CommandMessages.ODEN_CLI_OPTION_plansource + " " + agentCombo.getText() //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-6$
					+ " "
					+ CommandMessages.ODEN_CLI_OPTION_desc
					+ " " + "\"" + textPlanDesc.getText() + "\"" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					+ " -json"; //$NON-NLS-1$
		} else {
			MSG_SNAPHOST_NEW_PLAN = CommandMessages.ODEN_CLI_COMMAND_snapshot_add
					+ " "
					+ "\"" //$NON-NLS-1$
					+ textPlanName.getText().trim()
					+ "\"" + " " + CommandMessages.ODEN_CLI_OPTION_plansource + " " + agentCombo.getText() //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-6$
					+ ":"
					+ sourceLocation
					+ " " + CommandMessages.ODEN_CLI_OPTION_desc + " " + "\"" + textPlanDesc.getText() + "\"" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					+ " -json"; //$NON-NLS-1$
		}

		new SnapshotViewContentProvider();
		try {
			String aa = MSG_SNAPHOST_NEW_PLAN;
			SnapshotViewContentProvider.doOdenBroker(SHELL_URL,
					MSG_SNAPHOST_NEW_PLAN);
		} catch (OdenException e) {
			OdenActivator
					.error(
							UIMessages.ODEN_SNAPSHOT_Actions_Exception_SaveSnapshotPlanInfo,
							e);
		}
	}

	private String distinctionLoc(String text) {
		String result = ""; //$NON-NLS-1$
		if (text
				.equals(UIMessages.ODEN_SNAPSHOT_SnapshotView_DefaultLocComboText)) {
			result = CommandMessages.ODEN_CLI_OPTION_locvarsign;
		} else if (text
				.equals(UIMessages.ODEN_SNAPSHOT_SnapshotView_AbsolutePathComboText)) {
			result = ""; //$NON-NLS-1$
		} else {
			result = CommandMessages.ODEN_CLI_OPTION_locvarsign
					+ text;
		}
		return result;
	}

	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE);
	}

	private void validate() {
		if ((textPlanName.getText().trim().length() > 0)){
			if(locationVar.getText().equals(UIMessages.ODEN_SNAPSHOT_SnapshotView_AbsolutePathComboText)){
				if(sourceLocPathText.getText().trim().length() > 0){
					setDialogComplete(true);
				}else{
					setDialogComplete(false);
				}
			}else{
				setDialogComplete(true);
			}
		}else{
			setDialogComplete(false);
		}
	}

	private void setDialogComplete(boolean b) {
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (okButton != null)
			okButton.setEnabled(b);
	}

}