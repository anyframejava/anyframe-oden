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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.snapshot.SnapshotView;
import anyframe.oden.eclipse.core.snapshot.SnapshotViewContentProvider;
import anyframe.oden.eclipse.core.utils.Cmd;
import anyframe.oden.eclipse.core.utils.DialogUtil;
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
	private static Text textPlanSource;
	private static Text textDestAgent;
	private static Text textPlanDest;
	private static Text textPlanDesc;

	public static String server;

	private static String MSG_SNAPHOST_NEW_PLAN = ""; //$NON-NLS-1$
	private static String SHELL_URL;

	private static String[] DEST_OPT = { "dest", "d" }; //$NON-NLS-1$ //$NON-NLS-2$
	private static String[] SOURCE_OPT = { "source", "s" }; //$NON-NLS-1$ //$NON-NLS-2$
	private static String[] DESC_OPT = { "desc" }; //$NON-NLS-1$

	private static final String MSG_SNAPSHOT_INFO_PLAN = CommandMessages.ODEN_SNAPSHOT_SnapshotView_MsgInfoPlan;

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

		String source = null, dest = null, desc = null;
		Composite parentComposite = (Composite) super.createDialogArea(parent);

		Composite composite = new Composite(parentComposite, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		composite.setLayoutData(data);
		composite.setLayout(new GridLayout(3, true));

		Label labelPlanName = new Label(composite, SWT.NULL);
		labelPlanName.setText(UIMessages.ODEN_SNAPSHOT_Dialogs_LabelPlanName);

		textPlanName = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 2;
		if (type != Type.ADD) {
			textPlanName.setText(selection + "-duplicated"); //$NON-NLS-1$

			String url = "http://" //$NON-NLS-1$
				+ OdenActivator.getDefault().getAliasManager()
				.getServerManager().getServer(server).getUrl()
				+ "/shell"; //$NON-NLS-1$

			String result = ""; //$NON-NLS-1$
			try {
				result = SnapshotViewContentProvider.getInfo(url,
						CommandMessages.ODEN_SNAPSHOT_SnapshotView_MsgInfoPlan
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
			dest = cmd.getOptionArg(DEST_OPT);
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

		Label labelDestAgent = new Label(composite, SWT.NULL);
		labelDestAgent
		.setText(UIMessages.ODEN_SNAPSHOT_Dialogs_LabelDestinationAgent);

		textDestAgent = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridData3 = new GridData(GridData.FILL_HORIZONTAL);
		gridData3.horizontalSpan = 2;
		if (type != Type.ADD) {
			int temp = dest.indexOf("/"); //ex> dest = agent1/location1  //$NON-NLS-1$
			String strAgent = dest.substring(0, temp);
			textDestAgent.setText(strAgent);
		}
		textDestAgent.setLayoutData(gridData3);
		textDestAgent.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent keyEvent) {
				SnapshotNewPlanDialog.this.validate();
			};

			public void keyReleased(KeyEvent keyEvent) {
				SnapshotNewPlanDialog.this.validate();
			};
		});

		Label labelPlanSource = new Label(composite, SWT.NULL);
		labelPlanSource
		.setText(UIMessages.ODEN_SNAPSHOT_Dialogs_LabelPlanSource);

		textPlanSource = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		gridData2.horizontalSpan = 2;
		if (type != Type.ADD) {
			source = source.substring(1);
			textPlanSource.setText(source);
		}
		textPlanSource.setLayoutData(gridData2);

		Label labelPlanDest = new Label(composite, SWT.NULL);
		labelPlanDest
		.setText(UIMessages.ODEN_SNAPSHOT_Dialogs_LabelPlanDestination);

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
		.setText(UIMessages.ODEN_SNAPSHOT_Dialogs_LabelPlanDescription);

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
			.getServerManager().getServer(server).getUrl() + "/shell"; //$NON-NLS-1$
		if (!checkValidation()) {

			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					Boolean result = MessageDialog
					.openConfirm(
							Display.getDefault().getActiveShell(),
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

		if (textPlanSource.getText().equalsIgnoreCase(textPlanDest.getText())) {
			DialogUtil.openMessageDialog(
					UIMessages.ODEN_SNAPSHOT_Actions_MsgInfoAddPlan,
					UIMessages.ODEN_SNAPSHOT_Actions_MsgInfoSameDestSource,
					MessageDialog.INFORMATION);
		} else {
			MSG_SNAPHOST_NEW_PLAN = CommandMessages.ODEN_SNAPSHOT_Dialogs_NewPlanCmdHead
			+ "\"" + textPlanName.getText().trim() //$NON-NLS-1$
			+ "\"" + " " + CommandMessages.ODEN_SNAPSHOT_Dialogs_NewPlanCmdSource + " /" //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-6$
			+ textPlanSource.getText()
			+ " " + CommandMessages.ODEN_SNAPSHOT_Dialogs_NewPlanCmdDest + " " + textDestAgent.getText() + "/" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			+ textPlanDest.getText()
			+ " " + CommandMessages.ODEN_SNAPSHOT_Dialogs_NewPlanCmdDesc + " " + "\"" + textPlanDesc.getText() + "\"" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			+ " -json"; //$NON-NLS-1$
			new SnapshotViewContentProvider();
			try {
				SnapshotViewContentProvider.doOdenBroker(SHELL_URL,
						MSG_SNAPHOST_NEW_PLAN);
			} catch (OdenException e) {
				OdenActivator
				.error(
						UIMessages.ODEN_SNAPSHOT_Actions_Exception_SaveSnapshotPlanInfo,
						e);
			}
		}
	}

	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE);
	}

	private void validate() {
		if ((textPlanName.getText().trim().length() > 0)
				&& (textDestAgent.getText().trim().length() > 0))
			setDialogComplete(true);
		else
			setDialogComplete(false);
	}

	private void setDialogComplete(boolean b) {
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (okButton != null)
			okButton.setEnabled(b);
	}

}