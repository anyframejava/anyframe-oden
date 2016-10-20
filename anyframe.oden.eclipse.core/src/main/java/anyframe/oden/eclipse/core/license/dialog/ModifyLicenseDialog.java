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
package anyframe.oden.eclipse.core.license.dialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenFiles;
import anyframe.oden.eclipse.core.license.DecodingID;
import anyframe.oden.eclipse.core.license.DecodingLicense;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * 
 * Dialog to modify license already exist. If you want to change license, use
 * this dialog.
 * 
 * @version 1.1.0
 * @author LEE sujeong
 * 
 */
public class ModifyLicenseDialog extends TitleAreaDialog {

	private Text textLicense;
	private Text textOrg;
	private Text textName;
	private Text textID;

	private Label checkResult;
	private Image odenImage;

	private File file;

	private static final String IMAGES_PATH = "icons/"; //$NON-NLS-1$

	public static Image createImage(String imagePath) {
		final Bundle pluginBundle = Platform.getBundle(OdenActivator.PLUGIN_ID);
		final Path imageFilePath = new Path(ModifyLicenseDialog.IMAGES_PATH
				+ imagePath);
		final URL imageFileUrl = Platform.find(pluginBundle, imageFilePath);

		Image image = null;
		InputStream imageFileStream = null;

		try {
			imageFileStream = imageFileUrl.openStream();
			image = new Image(null, imageFileStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (imageFileStream != null) {
				try {
					imageFileStream.close();
				} catch (IOException e) {
				}
			}
		}
		return image;
	}

	public ModifyLicenseDialog(Shell parentShell) {
		super(parentShell);
	}

	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);

		setTitle(UIMessages.ODEN_LICENSE_LoginLicenseDialog_LoginLicenseDialogTitle);
		setMessage(UIMessages.ODEN_LICENSE_ModyfyLicense_ModifyLicenseDialogDesc);

		setTitleImage(createImage("oden_chikuwa.png")); //$NON-NLS-1$

		if (odenImage != null) {
			setTitleImage(odenImage);
		}
		return contents;
	}

	public boolean close() {
		if (odenImage != null)
			odenImage.dispose();
		return super.close();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell
				.setText(UIMessages.ODEN_LICENSE_LoginLicenseDialog_LoginLicenseTitle);

		Monitor mon = Display.getDefault().getMonitors()[1];
		Rectangle rect = mon.getBounds();
		int width = 450;
		int height = 330;
		newShell.setBounds(rect.width / 2 - width / 2, rect.height / 2 - height
				/ 2, width, height);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				UIMessages.ODEN_LICENSE_LoginLicenseDialog_ButtonLicneseSave,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		setDialogComplete(false);

		ModifyLicenseDialog.this.validate();
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite parentComposite = (Composite) super.createDialogArea(parent);

		Composite composite = new Composite(parentComposite, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		GridLayout layout = new GridLayout(4, true);
		composite.setLayoutData(data);
		composite.setLayout(layout);
		layout.marginHeight = 20;
		layout.marginWidth = 20;

		Label label = new Label(composite, SWT.NONE);
		label
				.setText(UIMessages.ODEN_LICENSE_LoginLicenseDialog_LabelEnterLicense);
		label.setFont(new Font(parent.getDisplay(), "Arial", 13, SWT.BOLD)); //$NON-NLS-1$
		GridData gridDataTitle = new GridData(GridData.FILL_HORIZONTAL);
		gridDataTitle.horizontalSpan = 4;
		label.setLayoutData(gridDataTitle);

		Label labelEmptyLine = new Label(composite, SWT.NONE);
		labelEmptyLine.setText(""); //$NON-NLS-1$
		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 4;
		labelEmptyLine.setLayoutData(gridData1);

		Label labelOrg = new Label(composite, SWT.NONE);
		labelOrg.setText(UIMessages.ODEN_LICENSE_LoginLicenseDialog_ColumnOrg);

		textOrg = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridDataTextOrg = new GridData(GridData.FILL_HORIZONTAL);
		gridDataTextOrg.horizontalSpan = 3;
		textOrg.setLayoutData(gridDataTextOrg);
		textOrg.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				ModifyLicenseDialog.this.validate();
			}

			public void keyPressed(KeyEvent e) {
				ModifyLicenseDialog.this.validate();
			}
		});

		Label labelName = new Label(composite, SWT.NONE);
		labelName
				.setText(UIMessages.ODEN_LICENSE_LoginLicenseDialog_ColumnName);

		textName = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridDataTextName = new GridData(GridData.FILL_HORIZONTAL);
		gridDataTextName.horizontalSpan = 3;
		textName.setLayoutData(gridDataTextName);
		textName.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				ModifyLicenseDialog.this.validate();
			}

			public void keyPressed(KeyEvent e) {
				ModifyLicenseDialog.this.validate();
			}
		});

		Label labelID = new Label(composite, SWT.NONE);
		labelID.setText(UIMessages.ODEN_LICENSE_LoginLicenseDialog_ColumnID);

		textID = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridDataTextID = new GridData(GridData.FILL_HORIZONTAL);
		gridDataTextID.horizontalSpan = 3;
		textID.setLayoutData(gridDataTextID);
		textID.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				ModifyLicenseDialog.this.validate();
			}

			public void keyPressed(KeyEvent e) {
				ModifyLicenseDialog.this.validate();
			}
		});

		Label labelLicense = new Label(composite, SWT.NONE);
		labelLicense
				.setText(UIMessages.ODEN_LICENSE_LoginLicenseDialog_ColumnLicense);

		textLicense = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		LicenseCheckAction action = new LicenseCheckAction();
		if (action.confirmExistFile()) {// file ����
			action.readLicenseFile();
			textLicense.setText(action.returnLicense());
		} else {// file ����
			textLicense.setText(""); //$NON-NLS-1$
		}
		textLicense.setLayoutData(gridData);
		textLicense.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				ModifyLicenseDialog.this.validate();
			}

			public void keyPressed(KeyEvent e) {
				ModifyLicenseDialog.this.validate();
			}
		});

		checkResult = new Label(composite, SWT.NONE);
		GridData gridDataCheck = new GridData(GridData.FILL_HORIZONTAL);
		gridDataCheck.horizontalSpan = 4;
		checkResult.setLayoutData(gridDataCheck);

		String inFileOrg = ""; //$NON-NLS-1$
		String inFileName = ""; //$NON-NLS-1$
		String inFileID = ""; //$NON-NLS-1$
		String inFileLicense = ""; //$NON-NLS-1$

		if (confirmExistFile()) {
			String[] result = readLicenseFile();
			inFileOrg = result[0];
			inFileName = result[1];
			inFileID = result[2];
			inFileLicense = result[3];
		} else {
			checkResult
					.setText(UIMessages.ODEN_LICENSE_ModyfyLicense_MsgLicenseNotExsit);
		}

		textOrg.setText(inFileOrg);
		textName.setText(inFileName);
		textID.setText(inFileID);
		textLicense.setText(inFileLicense);

		return composite;
	}

	@Override
	protected void okPressed() {
		// �������� ����
		File f = new File(LicenseCheckAction.getPlatform()
				+ OdenFiles.LICENSE_FILE_FULL_NAME);
		f.delete();

		try {
			File file = new File(LicenseCheckAction.getPlatform()
					+ OdenFiles.LICENSE_TRIAL_FILE_CONF);
			file.mkdirs();

			PrintStream out = new PrintStream(new FileOutputStream(file
					.getPath()
					+ "/" + OdenFiles.LICENSE_FILE_NAME)); //$NON-NLS-1$
			out.println(textOrg.getText());
			out.println(textName.getText());
			out.println(textID.getText());
			out.println(textLicense.getText());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		close();
	}

	@Override
	protected void cancelPressed() {
		close();
	}

	private void setDialogComplete(boolean b) {
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (okButton != null)
			okButton.setEnabled(b);
	}

	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE);
	}

	private void validate() {
		if (textOrg.getText().trim().length() > 0
				&& textName.getText().trim().length() > 0) {
			if (textID.getText().trim().length() == 5) {
				if (textLicense.getText().trim().length() == 23) {
					DecodingID decodingID = new DecodingID();
					boolean boolID = decodingID.checkIdAvailable(textID
							.getText().trim(), textLicense.getText().trim());

					DecodingLicense decodingLicense = new DecodingLicense();
					boolean boolLicense = decodingLicense
							.checkLicenseAvailable(textOrg.getText().trim(),
									textName.getText().trim(), textLicense
											.getText().trim());
					if (boolID && boolLicense) {
						setDialogComplete(true);
						checkResult
								.setText(UIMessages.ODEN_LICENSE_ModyfyLicense_MsgLicenseAvailable);
					} else {
						setDialogComplete(false);
						checkResult
								.setText(UIMessages.ODEN_LICENSE_ModyfyLicense_MsgLicenseNotAvailable);
					}
				} else {
					setDialogComplete(false);
					checkResult
							.setText(UIMessages.ODEN_LICENSE_ModyfyLicense_MsgLicenseNotAvailable);
				}
			} else {
				setDialogComplete(false);
				checkResult
						.setText(UIMessages.ODEN_LICENSE_ModyfyLicense_MsgLicenseNotAvailable);
			}
		} else {
			setDialogComplete(false);
			checkResult
					.setText(UIMessages.ODEN_LICENSE_ModyfyLicense_MsgLicenseNotAvailable);
		}
	}

	public boolean confirmExistFile() {
		file = new File(LicenseCheckAction.getPlatform()
				+ OdenFiles.LICENSE_FILE_FULL_NAME);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public String[] readLicenseFile() {
		String[] result = new String[4];
		try {
			FileReader fr = new FileReader(file);
			BufferedReader inFile = new BufferedReader(fr);
			String org = inFile.readLine();
			String name = inFile.readLine();
			String id = inFile.readLine();
			String license = inFile.readLine();
			inFile.close();
			fr.close();

			result[0] = org;
			result[1] = name;
			result[2] = id;
			result[3] = license;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

}
