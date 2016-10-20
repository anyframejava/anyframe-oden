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
package anyframe.oden.eclipse.core.snapshot.dialogs;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
import anyframe.oden.eclipse.core.OdenMessages;
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
	private static Text textPlanTarget;
	private static Text textDestAgent;
	private static Text textPlanDest;
	private static Text textPlanDesc;

	public static String agent;

	private static String MSG_SNAPHOST_NEW_PLAN = ""; //$NON-NLS-1$
	private static String SHELL_URL;

	private static String[] DEST_OPT = { "dest", "d" }; //$NON-NLS-1$ //$NON-NLS-2$
	private static String[] TARGET_OPT = { "source", "s" }; //$NON-NLS-1$ //$NON-NLS-2$
	private static String[] DESC_OPT = { "desc" }; //$NON-NLS-1$

	private static final String MSG_SNAPSHOT_INFO_PLAN = OdenMessages.ODEN_SNAPSHOT_SnapshotView_MsgInfoPlan;

	private String titleAdd = OdenMessages.ODEN_SNAPSHOT_Dialogs_TextDlgMainInfo;
	private String subtitleAdd = OdenMessages.ODEN_SNAPSHOT_Dialogs_TextDlgSubInfo;

	private String titleCopy = OdenMessages.ODEN_SNAPSHOT_Dialogs_DuplicateTitle;
	private String subtitleCopy = OdenMessages.ODEN_SNAPSHOT_Dialogs_DuplicateSubTitle;

	private ImageDescriptor odenImageDescriptor = ImageUtil
			.getImageDescriptor(OdenMessages.ODEN_EXPLORER_Dialogs_OdenImageURL);

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
			shell.setText(OdenMessages.ODEN_SNAPSHOT_Dialogs_TextDlgTitle);
		} else if (type == Type.COPY) {
			shell.setText(OdenMessages.ODEN_SNAPSHOT_Dialogs_Duplicate);
		}
	}

	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
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
						.disposeImage(OdenMessages.ODEN_EXPLORER_Dialogs_OdenImageURL);
			}
		});

		return contents;
	}

	protected Control createDialogArea(Composite parent) {

		String tar = null, dest = null, desc = null;
		Composite parentComposite = (Composite) super.createDialogArea(parent);

		Composite composite = new Composite(parentComposite, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		composite.setLayoutData(data);
		composite.setLayout(new GridLayout(3, true));

		Label labelPlanName = new Label(composite, SWT.NULL);
		labelPlanName.setText(OdenMessages.ODEN_SNAPSHOT_Dialogs_LabelPlanName);

		textPlanName = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 2;
		if (type != Type.ADD) {
			textPlanName.setText(selection + "-duplicated"); //$NON-NLS-1$

			String url = "http://" //$NON-NLS-1$
					+ OdenActivator.getDefault().getAliasManager()
							.getAgentManager().getAgent(agent).getUrl()
					+ "/shell"; //$NON-NLS-1$

			String result = ""; //$NON-NLS-1$
			try {
				result = SnapshotViewContentProvider.getInfo(url,
						OdenMessages.ODEN_SNAPSHOT_SnapshotView_MsgInfoPlan
								+ " " + "\"" + selection + "\"" + " -json"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			} catch (OdenException e) {
				OdenActivator
						.error(
								OdenMessages.ODEN_SNAPSHOT_Actions_Exception_GetSnapshotPlanInfo,
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
								OdenMessages.ODEN_SNAPSHOT_Actions_Exception_ParseSnapshotPlanInfo,
								e);
			}

			Cmd cmd = null;
			cmd = new Cmd("\"" + selection + "\"" + " = " + tempResult); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			tar = cmd.getOptionArg(TARGET_OPT);
			dest = cmd.getOptionArg(DEST_OPT);
			desc = cmd.getOptionArg(DESC_OPT);

		}
		textPlanName.setLayoutData(gridData1);

		Label labelDestAgent = new Label(composite, SWT.NULL);
		labelDestAgent
				.setText(OdenMessages.ODEN_SNAPSHOT_Dialogs_LabelDestinationAgent);

		textDestAgent = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridData3 = new GridData(GridData.FILL_HORIZONTAL);
		gridData3.horizontalSpan = 2;
		if (type != Type.ADD) {
			int temp = dest.indexOf("/"); //ex> dest = agent1/location1  //$NON-NLS-1$
			String strAgent = dest.substring(0, temp);
			textDestAgent.setText(strAgent);
		}
		textDestAgent.setLayoutData(gridData3);

		Label labelPlanTarget = new Label(composite, SWT.NULL);
		labelPlanTarget
				.setText(OdenMessages.ODEN_SNAPSHOT_Dialogs_LabelPlanTarget);

		textPlanTarget = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		gridData2.horizontalSpan = 2;
		if (type != Type.ADD) {
			tar = tar.substring(1);
			textPlanTarget.setText(tar);
		}
		textPlanTarget.setLayoutData(gridData2);

		Label labelPlanDest = new Label(composite, SWT.NULL);
		labelPlanDest
				.setText(OdenMessages.ODEN_SNAPSHOT_Dialogs_LabelPlanDestination);

		textPlanDest = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridData4 = new GridData(GridData.FILL_HORIZONTAL);
		gridData4.horizontalSpan = 2;
		if (type != Type.ADD) {
			int temp = dest.indexOf("/"); //ex> dest = agent1/location1  //$NON-NLS-1$
			String strDest = dest.substring(temp + 1);
			textPlanDest.setText(strDest);
		}
		textPlanDest.setLayoutData(gridData4);

		Label labelPlanDesc = new Label(composite, SWT.NULL);
		labelPlanDesc
				.setText(OdenMessages.ODEN_SNAPSHOT_Dialogs_LabelPlanDescription);

		textPlanDesc = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridData5 = new GridData(GridData.FILL_HORIZONTAL);
		gridData5.horizontalSpan = 2;
		if (type != Type.ADD) {
			textPlanDesc.setText(desc);
		}
		textPlanDesc.setLayoutData(gridData5);

		return composite;
	}

	protected void okPressed() {
		addSnapshotplan();
		close();
	}

	private void addSnapshotplan() {

		SHELL_URL = "http://" //$NON-NLS-1$
				+ OdenActivator.getDefault().getAliasManager()
						.getAgentManager().getAgent(agent).getUrl() + "/shell"; //$NON-NLS-1$
		if (!checkValidation()) {
			
			Display.getDefault().syncExec(new Runnable(){
				public void run() {
					Boolean result = MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
							OdenMessages.ODEN_SNAPSHOT_Dialogs_MsgValidation, 
							OdenMessages.ODEN_SNAPSHOT_Dialogs_MsgValidationInfo);
					if(result==true){
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
						+ " -json");
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
		MSG_SNAPHOST_NEW_PLAN = OdenMessages.ODEN_SNAPSHOT_Dialogs_NewPlanCmdHead
				+ "\"" + textPlanName.getText().trim() //$NON-NLS-1$
				+ "\"" + " " + OdenMessages.ODEN_SNAPSHOT_Dialogs_NewPlanCmdSource + " /" //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-6$
				+ textPlanTarget.getText()
				+ " " + OdenMessages.ODEN_SNAPSHOT_Dialogs_NewPlanCmdDest + " " + textDestAgent.getText() + "/" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ textPlanDest.getText()
				+ " " + OdenMessages.ODEN_SNAPSHOT_Dialogs_NewPlanCmdDesc + " " + textPlanDesc.getText() //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ " -json"; //$NON-NLS-1$
		new SnapshotViewContentProvider();
		try {
			SnapshotViewContentProvider.doOdenBroker(SHELL_URL,
					MSG_SNAPHOST_NEW_PLAN);
		} catch (OdenException e) {
			OdenActivator
					.error(
							OdenMessages.ODEN_SNAPSHOT_Actions_Exception_SaveSnapshotPlanInfo,
							e);
		}
	}

	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE);
	}

}