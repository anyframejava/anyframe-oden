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
package anyframe.oden.eclipse.core.explorer.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Creates, changes, and duplicates an Server profile.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 M2
 *
 */
public class CreateServerDialog extends TitleAreaDialog {

	private static final int SIZING_TEXT_FIELD_WIDTH = 256;

	public enum Type {
		CREATE, CHANGE, COPY
	}

	// Strings and messages from message properties
	private String titleCreate = UIMessages.ODEN_EXPLORER_Dialogs_CreateServerDialog_AddTitle;
	private String subtitleCreate = UIMessages.ODEN_EXPLORER_Dialogs_CreateServerDialog_AddSubtitle;

	private String titleChange = UIMessages.ODEN_EXPLORER_Dialogs_CreateServerDialog_EditTitle;
	private String subtitleChange = UIMessages.ODEN_EXPLORER_Dialogs_CreateServerDialog_EditSubtitle;

	private String titleCopy = UIMessages.ODEN_EXPLORER_Dialogs_CreateServerDialog_DuplicateTitle;
	private String subtitleCopy = UIMessages.ODEN_EXPLORER_Dialogs_CreateServerDialog_DuplicateSubtitle;

	// Oden dialog image which appears on the upper right of the panel
	private ImageDescriptor odenImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_EXPLORER_Dialogs_OdenImageURL);

	private Type type;
	public Server server;

	// Define field names
	private String nicknameFieldName = UIMessages.ODEN_EXPLORER_Dialogs_NicknameFieldName;
	private String urlFieldName = UIMessages.ODEN_EXPLORER_Dialogs_ServerFieldName;
	private String noUsernameRequiredName = UIMessages.ODEN_EXPLORER_Dialogs_UserNameBooleanString;
	private String userFieldName = UIMessages.ODEN_EXPLORER_Dialogs_UserNameFieldName;
	private String passwordFieldName = UIMessages.ODEN_EXPLORER_Dialogs_PasswordFieldName;
	//	private String logTypeComboName = OdenMessages.ODEN_EXPLORER_Dialogs_CreateAgentDialog_LogTypeComboName;
	//	private String logLocationFieldName = OdenMessages.ODEN_EXPLORER_Dialogs_CreateAgentDialog_LogLocationFieldName;

	// Define field attributes
	private Text nicknameField;
	private Text urlField;
	private Button noUsernameRequired;
	private Text userField;
	private Text passwordField;

	//	private Combo logTypeCombo;
	//	private Text logLocationField;

	public CreateServerDialog(Shell parentShell, Type type, Server server) {
		super(parentShell);
		this.server = server;
		this.type = type;
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (type == Type.CREATE) {
			shell.setText(titleCreate);
		} else if (type == Type.CHANGE) {
			shell.setText(titleChange);
		} else if (type == Type.COPY) {
			shell.setText(titleCopy);
		}
	}

	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		validate();
	}

	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);

		if (type == Type.CREATE) {
			setTitle(titleCreate);
			setMessage(subtitleCreate);
		} else if (type == Type.CHANGE) {
			setTitle(titleChange);
			setMessage(subtitleChange);
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

		// Create profile name group
		Composite nameGroup = new Composite(composite, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 10;
		nameGroup.setLayout(layout);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		nameGroup.setLayoutData(data);

		// Nickname field
		Label nameLabel = new Label(nameGroup, SWT.WRAP);
		nameLabel.setText(nicknameFieldName);
		nicknameField = new Text(nameGroup, SWT.BORDER);
		if (type != Type.CREATE) {
			nicknameField.setText(server.getNickname());
			// If (type == Type.CREATE) then it will use sequential nickname such as "new-server-[server serial number]" internally.
			// However, it will not be displayed on this dialog and make users set a new nickname.
		}
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 2;
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		nicknameField.setLayoutData(data);
		nicknameField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent keyEvent) {
				CreateServerDialog.this.validate();
			};
			public void keyReleased(KeyEvent keyEvent) {
				CreateServerDialog.this.validate();
			};
		});

		// Server field
		Label serverLabel = new Label(nameGroup, SWT.WRAP);
		serverLabel.setText(urlFieldName);
		urlField = new Text(nameGroup, SWT.BORDER);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 2;
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		urlField.setLayoutData(data);
		urlField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				CreateServerDialog.this.validate();
			};
			public void keyReleased(KeyEvent arg0) {
				CreateServerDialog.this.validate();
			};
		});

		// a new label for matching indentation with user name and password fields
		new Label(nameGroup, SWT.NONE);

		// User name is not required check box
		noUsernameRequired = new Button(nameGroup, SWT.CHECK);
		noUsernameRequired.setText(noUsernameRequiredName);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 2;
		noUsernameRequired.setLayoutData(data);

		// User name
		Label userLabel = new Label(nameGroup, SWT.WRAP);
		userLabel.setText(userFieldName);
		userField = new Text(nameGroup, SWT.BORDER);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 2;
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		userField.setLayoutData(data);
		userField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent keyEvent) {
				CreateServerDialog.this.validate();
			};
			public void keyReleased(KeyEvent keyEvent) {
				CreateServerDialog.this.validate();
			};
		});

		// password field
		Label passwordLabel = new Label(nameGroup, SWT.WRAP);
		passwordLabel.setText(passwordFieldName);
		passwordField = new Text(nameGroup, SWT.BORDER);
		passwordField.setEchoChar('*');
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 2;
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		passwordField.setLayoutData(data);

		// check if "user name is required ..." check box is checked or not
		noUsernameRequired.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				boolean checked = noUsernameRequired.getSelection();
				userField.setEnabled(!checked);
				passwordField.setEnabled(!checked);
				if (checked) {
					userField.setText("anonymous"); // display "anonymous" as a user name when the check box is checked
					passwordField.setText("");
					CreateServerDialog.this.validate(); // validate the user field even if the check box is checked with "anonymous" text
				}
			}
		});
		if (server.isHasNoUserName()) {
			noUsernameRequired.setSelection(true);
			userField.setEnabled(false);
			passwordField.setEnabled(false);
		} else {
			noUsernameRequired.setSelection(false);
			userField.setEnabled(true);
			passwordField.setEnabled(true);		
		}

		// display the existing data for url, user name, and password in the case of CHANGE or COPY
		if (type != Type.CREATE) {
			if (server.getUrl() != null) {
				urlField.setText(server.getUrl());
			}
			if (server.getUser() != null) {
				userField.setText(server.getUser());
			}
			if (server.getPassword() != null) {
				passwordField.setText(server.getPassword());
			}
		}

		return parentComposite;
	}

	protected void okPressed() {
		server.setNickname(nicknameField.getText().trim());
		server.setUrl(urlField.getText().trim());
		if(noUsernameRequired.getSelection()) {
			server.setHasNoUserName(true);
		} else {
			server.setHasNoUserName(false);
			if (userField.getText().trim().length() > 0) {
				server.setUser(userField.getText().trim());
				server.setPassword(passwordField.getText().trim());
			}
		}

		if (type != Type.CHANGE) {
			OdenActivator.getDefault().getAliasManager().getServerManager().addServer(server);
		}

		server.setNickname(this.nicknameField.getText().trim());

		// Notify that there has been changes
		OdenActivator.getDefault().getAliasManager().getServerManager().modelChanged();

		// reload data for data consistency
		try {
			OdenActivator.getDefault().getAliasManager().save();
			OdenActivator.getDefault().getAliasManager().load();
		} catch (OdenException odenException) {
			OdenActivator.error("Exception occured while reloading Oden Server profiles.", odenException);
			odenException.printStackTrace();
		}

		close();
	}

	private void validate() {
		if((urlField.getText().trim().length() > 0)
				&& (nicknameField.getText().trim().length() > 0)
				&& (userField.getText().trim().length() > 0))
			setDialogComplete(true);
		else
			setDialogComplete(false);
	}

	private void setDialogComplete(boolean b) {
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (okButton != null)
			okButton.setEnabled(b);
	}

	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE);
	}

}
