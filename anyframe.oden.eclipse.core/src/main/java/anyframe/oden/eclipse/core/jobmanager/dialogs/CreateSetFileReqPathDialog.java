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

import java.util.Collection;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.alias.FileRequest;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Creates, changes a Set deploy by request profile.
 * 
 * @author HONG JungHwan
 * @version 1.1.0
 *
 */
public class CreateSetFileReqPathDialog extends TitleAreaDialog {

	private static final int SIZING_TEXT_FIELD_WIDTH = 256;
	private static final int SIZING_PROTOCOL_COMBO_WIDTH = 20;
	private static final int SIZING_URL_FIELD_WIDTH = 140;

	public enum Type {
		CREATE, CHANGE
	}

	// Strings and messages from message properties
	private String titleCreate = UIMessages.ODEN_JOBMANAGER_Dialogs_AddSetFileReqDialog_AddTitle;
	private String subtitleCreate = UIMessages.ODEN_JOBMANAGER_Dialogs_AddSetFileReqDialog_AddSubTitle;

	private String titleChange = UIMessages.ODEN_JOBMANAGER_Dialogs_AddSetFileReqDialog_EditTitle;
	private String subtitleChange = UIMessages.ODEN_JOBMANAGER_Dialogs_AddSetFileReqDialog_EditSubTitle;

	// Oden dialog image which appears on the upper right of the panel
	private ImageDescriptor odenImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_EXPLORER_Dialogs_OdenImageURL);

	private Type type;
	public FileRequest filerequest;
	
	
	// Define field names
	private String nicknameFieldName = UIMessages.ODEN_EXPLORER_Dialogs_NicknameFieldName;
	private String descriptionFieldName = UIMessages.ODEN_JOBMANAGER_Dialogs_AddSetFileReqDialog_Description;
	private String urlFieldName = UIMessages.ODEN_EXPLORER_Dialogs_ServerFieldName;
	private String noUsernameRequiredName = UIMessages.ODEN_EXPLORER_Dialogs_UserNameBooleanString;
	private String userFieldName = UIMessages.ODEN_EXPLORER_Dialogs_UserNameFieldName;
	private String passwordFieldName = UIMessages.ODEN_EXPLORER_Dialogs_PasswordFieldName;
	private String serverComboName = UIMessages.ODEN_EXPLORER_Dialogs_CreateBuildRepositoryDialog_ServerComboName;
	private String protocolComboName = UIMessages.ODEN_EXPLORER_Dialogs_CreateBuildRepositoryDialog_ProtocolComboName;
	private String pathFieldName = UIMessages.ODEN_EXPLORER_Dialogs_CreateBuildRepositoryDialog_PathFieldName;

	// Define field attributes
	private Text nicknameField;
	private Text descriptionField;
	private Text urlField;
	private Button noUsernameRequired;
	private Text userField;
	private Text passwordField;

	private Combo serverCombo;
	private String serverChosen;
	private Combo protocolCombo;
	private String protocolChosen;
	private Text pathField;
	
	private SetFileReqPathDialog dialog;
	
	public CreateSetFileReqPathDialog(Shell parentShell, Type type , FileRequest filerequest , SetFileReqPathDialog dialog) {
		super(parentShell);
		this.filerequest = filerequest;
		this.type = type;
		this.dialog = dialog;
	}

	protected void configureShell(Shell shell ) {
		super.configureShell(shell);
		if (type == Type.CREATE) {
			shell.setText(titleCreate);
		} else if (type == Type.CHANGE) {
			shell.setText(titleChange);
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
		layout.numColumns = 5;
		layout.marginWidth = 10;
		nameGroup.setLayout(layout);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		nameGroup.setLayoutData(data);

		// Nickname field
		Label nameLabel = new Label(nameGroup, SWT.WRAP);
		nameLabel.setText(nicknameFieldName);
		nicknameField = new Text(nameGroup, SWT.BORDER);
		if (type != Type.CREATE) {
			nicknameField.setText(filerequest.getNickname());
		}
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 4;
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		nicknameField.setLayoutData(data);
		nicknameField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent keyEvent) {
				CreateSetFileReqPathDialog.this.validate();
			};
			public void keyReleased(KeyEvent keyEvent) {
				CreateSetFileReqPathDialog.this.validate();
			};
		});

		// Description
		Label descriptionLabel = new Label(nameGroup, SWT.WRAP);
		descriptionLabel.setText(descriptionFieldName);
		descriptionField = new Text(nameGroup, SWT.BORDER);
		if (type != Type.CREATE) {
			descriptionField.setText(filerequest.getDesc());
		}
		descriptionField.setLayoutData(data);
		
		// Protocol combo
		Label protocolChoiceLabel = new Label(nameGroup, SWT.WRAP);
		protocolChoiceLabel.setText(protocolComboName);
		protocolCombo = new Combo(nameGroup, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 2;
		data.widthHint = SIZING_PROTOCOL_COMBO_WIDTH;
		protocolCombo.setLayoutData(data);
		populateProtocolCombo(protocolCombo);
		protocolCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				protocolChosen = protocolCombo.getText();
				CreateSetFileReqPathDialog.this.validate();
				if (protocolChosen.equals(CommonMessages.ODEN_ALIAS_RepositoryManager_ProtocolSet_FileSystem)) {
					urlField.setText("localhost");
					urlField.setEnabled(false);
					CreateSetFileReqPathDialog.this.validate();
				} else {
					urlField.setEnabled(true);
					urlField.setText("");
					CreateSetFileReqPathDialog.this.validate();
				}
			}
		});

		// Server field
		Label serverLabel = new Label(nameGroup, SWT.WRAP);
		serverLabel.setText(urlFieldName);
		urlField = new Text(nameGroup, SWT.BORDER);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);

		data.widthHint = SIZING_URL_FIELD_WIDTH;
		urlField.setLayoutData(data);
		urlField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				CreateSetFileReqPathDialog.this.validate();
			};
			public void keyReleased(KeyEvent arg0) {
				CreateSetFileReqPathDialog.this.validate();
			};
		});

		// Path field
		Label pathLabel = new Label(nameGroup, SWT.WRAP);
		pathLabel.setText(pathFieldName);
		pathField = new Text(nameGroup, SWT.BORDER);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 4;
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		pathField.setLayoutData(data);
		pathField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				CreateSetFileReqPathDialog.this.validate();
			};
			public void keyReleased(KeyEvent arg0) {
				CreateSetFileReqPathDialog.this.validate();
			};
		});

		// a new label for matching indentation with user name and password fields
		new Label(nameGroup, SWT.NONE);

		// User name is not required check box
		noUsernameRequired = new Button(nameGroup, SWT.CHECK);
		noUsernameRequired.setText(noUsernameRequiredName);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 4;
		noUsernameRequired.setLayoutData(data);

		// User name
		Label userLabel = new Label(nameGroup, SWT.WRAP);
		userLabel.setText(userFieldName);
		userField = new Text(nameGroup, SWT.BORDER);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 4;
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		userField.setLayoutData(data);
		userField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent keyEvent) {
				CreateSetFileReqPathDialog.this.validate();
			};
			public void keyReleased(KeyEvent keyEvent) {
				CreateSetFileReqPathDialog.this.validate();
			};
		});

		// password field
		Label passwordLabel = new Label(nameGroup, SWT.WRAP);
		passwordLabel.setText(passwordFieldName);
		passwordField = new Text(nameGroup, SWT.BORDER);
		passwordField.setEchoChar('*');
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 4;
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
					CreateSetFileReqPathDialog.this.validate(); // validate the user field even if the check box is checked with "anonymous" text
				}
			}
		});
		if (filerequest.isHasNoUserName()) {
			noUsernameRequired.setSelection(true);
			userField.setEnabled(false);
			passwordField.setEnabled(false);
		} else {
			noUsernameRequired.setSelection(false);
			userField.setEnabled(true);
			passwordField.setEnabled(true);		
		}

		// Create server-to-use group
		Composite serverChoiceGroup = new Composite(composite, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 10;
		serverChoiceGroup.setLayout(layout);
		GridData dataServerComboGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
		serverChoiceGroup.setLayoutData(dataServerComboGroup);

		// Server-To-Use combo
		Label serverChoiceLabel = new Label(serverChoiceGroup, SWT.WRAP);
		serverChoiceLabel.setText(serverComboName);
		serverCombo = new Combo(serverChoiceGroup, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		final GridData serverGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		serverGridData.widthHint = SIZING_TEXT_FIELD_WIDTH;
		serverCombo.setLayoutData(serverGridData);
		populateServerCombo(serverCombo);
		serverCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				serverChosen = OdenActivator.getDefault().getAliasManager().getServerManager().getServer(serverCombo.getText()).getNickname();
			}
		});

		// display the existing data for url, user name, password, and Server to use in the case of CHANGE or COPY
		if (type != Type.CREATE) {
			if (filerequest.getProtocol() != null) {
				protocolCombo.select(protocolCombo.indexOf(filerequest.getProtocol()));
				protocolChosen = filerequest.getProtocol();
			}
			if (filerequest.getUrl() != null) {
				// if the protocol is taken and it is "file system", then the address field would be disabled.
				if (protocolChosen.equals(CommonMessages.ODEN_ALIAS_RepositoryManager_ProtocolSet_FileSystem)) {
					urlField.setText("localhost");
					urlField.setEnabled(false);
				} else {
					urlField.setEnabled(true);
					urlField.setText(filerequest.getUrl());
				}
			}
			if (filerequest.getPath() != null) {
				pathField.setText(filerequest.getPath());
			}
			if (filerequest.getUser() != null) {
				userField.setText(filerequest.getUser());
			}
			if (filerequest.getPassword() != null) {
				passwordField.setText(filerequest.getPassword());
			}
			if (filerequest.getServerToUse() != null) {
				serverCombo.select(serverCombo.indexOf(filerequest.getServerToUse()));
				// if the pre-chosen Server does not exist, the choice will be removed (but it won't be saved until press "OK")
				if (serverCombo.indexOf(filerequest.getServerToUse()) != -1) {
					serverChosen = OdenActivator.getDefault().getAliasManager().getServerManager().getServer(serverCombo.getText()).getNickname();
				} else {
					serverChosen = null;
				}
			}
		}

		return parentComposite;
	}

	private void populateProtocolCombo(Combo protocolCombo) {
		String previous = protocolCombo.getText();
		if (previous != null) {
			previous = previous.trim();
			if (previous.length() == 0) {
				previous = null;
			}
		}
		String[] protocolSet = OdenActivator.getDefault().getAliasManager().getRepositoryManager().getProtocolSet();
		for (int i = 0; i < protocolSet.length; i++) {
			protocolCombo.add(protocolSet[i]);
		}
	}

	private void populateServerCombo(Combo serverCombo) {
		String previous = serverCombo.getText();
		if (previous != null) {
			previous = previous.trim();
			if (previous.length() == 0) {
				previous = null;
			}
		}
		Collection<Server> serverCollection = OdenActivator.getDefault().getAliasManager().getServerManager().getServers();
		for (Server combo : serverCollection) {
			serverCombo.add(combo.getNickname());
		}
	}

	protected void okPressed() {
		filerequest.setNickname(nicknameField.getText().trim());
		filerequest.setUrl(urlField.getText().trim());
		filerequest.setProtocol(protocolChosen);
		filerequest.setPath(pathField.getText().trim());
		filerequest.setDesc(descriptionField.getText());
		if(noUsernameRequired.getSelection()) {
			filerequest.setHasNoUserName(true);
		} else {
			filerequest.setHasNoUserName(false);
			if (userField.getText().trim().length() > 0) {
				filerequest.setUser(userField.getText().trim());
				filerequest.setPassword(passwordField.getText().trim());
			}
		}
		filerequest.setServerToUse(serverChosen);

		if (type != Type.CHANGE) {
			OdenActivator.getDefault().getAliasManager().getFileRequestManager().addFileRequest(filerequest);
		}

		filerequest.setNickname(this.nicknameField.getText().trim());

		// Notify that there has been changes
		OdenActivator.getDefault().getAliasManager().getFileRequestManager().modelChanged();
		// reload data for data consistency
		try {
			OdenActivator.getDefault().getAliasManager().save();
			OdenActivator.getDefault().getAliasManager().load();
		} catch (OdenException odenException) {
			OdenActivator.error("Exception occured while reloading File Request profiles.", odenException);
			odenException.printStackTrace();
		}
		
		dialog.initData();
		
		close();
	}

	private void validate() {
		if((urlField.getText().trim().length() > 0)
				&& (nicknameField.getText().trim().length() > 0)
				&& (userField.getText().trim().length() > 0)
				&& (protocolCombo.getText().trim().length() > 0)
				&& (pathField.getText().trim().length() > 0))
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
