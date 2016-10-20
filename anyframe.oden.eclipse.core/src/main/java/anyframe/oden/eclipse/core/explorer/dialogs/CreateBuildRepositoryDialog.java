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
import anyframe.oden.eclipse.core.OdenMessages;
import anyframe.oden.eclipse.core.alias.Agent;
import anyframe.oden.eclipse.core.alias.Repository;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Creates, changes, and duplicates an Build Repository profile.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 RC1
 *
 */
public class CreateBuildRepositoryDialog extends TitleAreaDialog {

	private static final int SIZING_TEXT_FIELD_WIDTH = 256;
	private static final int SIZING_PROTOCOL_COMBO_WIDTH = 20;
	private static final int SIZING_URL_FIELD_WIDTH = 140;

	public enum Type {
		CREATE, CHANGE, COPY
	}

	// Strings and messages from message properties
	private String titleCreate = OdenMessages.ODEN_EXPLORER_Dialogs_CreateBuildRepositoryDialog_AddTitle;
	private String subtitleCreate = OdenMessages.ODEN_EXPLORER_Dialogs_CreateBuildRepositoryDialog_AddSubtitle;

	private String titleChange = OdenMessages.ODEN_EXPLORER_Dialogs_CreateBuildRepositoryDialog_EditTitle;
	private String subtitleChange = OdenMessages.ODEN_EXPLORER_Dialogs_CreateBuildRepositoryDialog_EditSubtitle;

	private String titleCopy = OdenMessages.ODEN_EXPLORER_Dialogs_CreateBuildRepositoryDialog_DuplicateTitle;
	private String subtitleCopy = OdenMessages.ODEN_EXPLORER_Dialogs_CreateBuildRepositoryDialog_DuplicateSubtitle;

	// Oden dialog image which appears on the upper right of the panel
	private ImageDescriptor odenImageDescriptor = ImageUtil.getImageDescriptor(OdenMessages.ODEN_EXPLORER_Dialogs_OdenImageURL);

	private Type type;
	public Repository repository;

	// Define field names
	private String nicknameFieldName = OdenMessages.ODEN_EXPLORER_Dialogs_NicknameFieldName;
	private String urlFieldName = OdenMessages.ODEN_EXPLORER_Dialogs_ServerFieldName;
	private String noUsernameRequiredName = OdenMessages.ODEN_EXPLORER_Dialogs_UserNameBooleanString;
	private String userFieldName = OdenMessages.ODEN_EXPLORER_Dialogs_UserNameFieldName;
	private String passwordFieldName = OdenMessages.ODEN_EXPLORER_Dialogs_PasswordFieldName;
	private String agentComboName = OdenMessages.ODEN_EXPLORER_Dialogs_CreateBuildRepositoryDialog_AgentComboName;
	private String protocolComboName = OdenMessages.ODEN_EXPLORER_Dialogs_CreateBuildRepositoryDialog_ProtocolComboName;
	private String pathFieldName = OdenMessages.ODEN_EXPLORER_Dialogs_CreateBuildRepositoryDialog_PathFieldName;

	// Define field attributes
	private Text nicknameField;
	private Text urlField;
	private Button noUsernameRequired;
	private Text userField;
	private Text passwordField;

	private Combo agentCombo;
	private String agentChosen;
	private Combo protocolCombo;
	private String protocolChosen;
	private Text pathField;

	public CreateBuildRepositoryDialog(Shell parentShell, Type type, Repository repository) {
		super(parentShell);
		this.repository = repository;
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
				ImageUtil.disposeImage(OdenMessages.ODEN_EXPLORER_Dialogs_OdenImageURL);
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
			nicknameField.setText(repository.getNickname());
			// If (type == Type.CREATE) then it will use sequential nickname such as "new-repository-[repository serial number]" internally.
			// However, it will not be displayed on this dialog and make users set a new nickname.
		}
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 4;
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		nicknameField.setLayoutData(data);
		nicknameField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent keyEvent) {
				CreateBuildRepositoryDialog.this.validate();
			};
			public void keyReleased(KeyEvent keyEvent) {
				CreateBuildRepositoryDialog.this.validate();
			};
		});

		// Server field
		Label serverLabel = new Label(nameGroup, SWT.WRAP);
		serverLabel.setText(urlFieldName);
		urlField = new Text(nameGroup, SWT.BORDER);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 2;
		data.widthHint = SIZING_URL_FIELD_WIDTH;
		urlField.setLayoutData(data);
		urlField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				CreateBuildRepositoryDialog.this.validate();
			};
			public void keyReleased(KeyEvent arg0) {
				CreateBuildRepositoryDialog.this.validate();
			};
		});

		// Protocol combo
		Label protocolChoiceLabel = new Label(nameGroup, SWT.WRAP);
		protocolChoiceLabel.setText(protocolComboName);
		protocolCombo = new Combo(nameGroup, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		//		data.horizontalSpan = 2;
		data.widthHint = SIZING_PROTOCOL_COMBO_WIDTH;
		protocolCombo.setLayoutData(data);
		populateProtocolCombo(protocolCombo);
		protocolCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				protocolChosen = protocolCombo.getText();
				CreateBuildRepositoryDialog.this.validate();
				if (protocolChosen.equals(OdenMessages.ODEN_ALIAS_RepositoryManager_ProtocolSet_FileSystem)) {
					urlField.setText("localhost");
					urlField.setEnabled(false);
					CreateBuildRepositoryDialog.this.validate();
				} else {
					urlField.setEnabled(true);
					urlField.setText("");
					CreateBuildRepositoryDialog.this.validate();
				}
			}
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
				CreateBuildRepositoryDialog.this.validate();
			};
			public void keyReleased(KeyEvent arg0) {
				CreateBuildRepositoryDialog.this.validate();
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
				CreateBuildRepositoryDialog.this.validate();
			};
			public void keyReleased(KeyEvent keyEvent) {
				CreateBuildRepositoryDialog.this.validate();
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
					CreateBuildRepositoryDialog.this.validate(); // validate the user field even if the check box is checked with "anonymous" text
				}
			}
		});
		if (repository.isHasNoUserName()) {
			noUsernameRequired.setSelection(true);
			userField.setEnabled(false);
			passwordField.setEnabled(false);
		} else {
			noUsernameRequired.setSelection(false);
			userField.setEnabled(true);
			passwordField.setEnabled(true);		
		}

		// Create agent-to-use group
		Composite agentChoiceGroup = new Composite(composite, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 10;
		agentChoiceGroup.setLayout(layout);
		GridData dataAgentComboGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
		agentChoiceGroup.setLayoutData(dataAgentComboGroup);

		// Agent-To-Use combo
		Label agentChoiceLabel = new Label(agentChoiceGroup, SWT.WRAP);
		agentChoiceLabel.setText(agentComboName);
		agentCombo = new Combo(agentChoiceGroup, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		final GridData agentGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		agentGridData.widthHint = SIZING_TEXT_FIELD_WIDTH;
		agentCombo.setLayoutData(agentGridData);
		populateAgentCombo(agentCombo);
		agentCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				agentChosen = OdenActivator.getDefault().getAliasManager().getAgentManager().getAgent(agentCombo.getText()).getNickname();
			}
		});

		// display the existing data for url, user name, password, and Agent to use in the case of CHANGE or COPY
		if (type != Type.CREATE) {
			if (repository.getUrl() != null) {
				urlField.setText(repository.getUrl());
			}
			if (repository.getProtocol() != null) {
				protocolCombo.select(protocolCombo.indexOf(repository.getProtocol()));
				protocolChosen = repository.getProtocol();
			}
			if (repository.getPath() != null) {
				pathField.setText(repository.getPath());
			}
			if (repository.getUser() != null) {
				userField.setText(repository.getUser());
			}
			if (repository.getPassword() != null) {
				passwordField.setText(repository.getPassword());
			}
			if (repository.getAgentToUse() != null) {
				agentCombo.select(agentCombo.indexOf(repository.getAgentToUse()));
				// if the pre-chosen Agent does not exist, the choice will be removed (but it won't be saved until press "OK")
				if (agentCombo.indexOf(repository.getAgentToUse()) != -1) {
					agentChosen = OdenActivator.getDefault().getAliasManager().getAgentManager().getAgent(agentCombo.getText()).getNickname();
				} else {
					agentChosen = null;
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
		//		protocolCombo.select(0);
	}

	private void populateAgentCombo(Combo agentCombo) {
		String previous = agentCombo.getText();
		if (previous != null) {
			previous = previous.trim();
			if (previous.length() == 0) {
				previous = null;
			}
		}
		Collection<Agent> agentCollection = OdenActivator.getDefault().getAliasManager().getAgentManager().getAgents();
		for (Agent combo : agentCollection) {
			agentCombo.add(combo.getNickname());
		}
		//		agentCombo.select(0);
	}

	protected void okPressed() {
		repository.setNickname(nicknameField.getText().trim());
		repository.setUrl(urlField.getText().trim());
		repository.setProtocol(protocolChosen);
		repository.setPath(pathField.getText().trim());
		if(noUsernameRequired.getSelection()) {
			repository.setHasNoUserName(true);
		} else {
			repository.setHasNoUserName(false);
			if (userField.getText().trim().length() > 0) {
				repository.setUser(userField.getText().trim());
				repository.setPassword(passwordField.getText().trim());
			}
		}
		repository.setAgentToUse(agentChosen);

		if (type != Type.CHANGE) {
			OdenActivator.getDefault().getAliasManager().getRepositoryManager().addRepository(repository);
		}

		repository.setNickname(this.nicknameField.getText().trim());

		// Notify that there has been changes
		OdenActivator.getDefault().getAliasManager().getRepositoryManager().modelChanged();

		// reload data for data consistency
		try {
			OdenActivator.getDefault().getAliasManager().save();
			OdenActivator.getDefault().getAliasManager().load();
		} catch (OdenException odenException) {
			OdenActivator.error("Exception occured while reloading Build Repository profiles.", odenException);
			odenException.printStackTrace();
		}

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
