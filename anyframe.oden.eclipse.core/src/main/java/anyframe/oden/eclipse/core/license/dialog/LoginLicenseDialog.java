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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
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

public class LoginLicenseDialog extends TitleAreaDialog {

	// FIRST : Trial 버튼 활성화
	// ALREADY : Trial 버튼 비활성화(TrialInfoDialog 통해서 들어올때)
	public enum Way {
		FIRST, ALREADY
	}

	private Way way;

	private Text textLicense;
	private Text textOrg;
	private Text textName;
	private Text textID;

	private Image odenImage;

	private File file;

	private static final String IMAGES_PATH = "icons/"; //$NON-NLS-1$

	public LoginLicenseDialog(Shell parentShell, Way way) {
		super(parentShell);
		this.way = way;
	}

	public static Image createImage(String imagePath) {
		final Bundle pluginBundle = Platform.getBundle(OdenActivator.PLUGIN_ID);
		final Path imageFilePath = new Path(LoginLicenseDialog.IMAGES_PATH
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

	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);

		setTitle(UIMessages.ODEN_LICENSE_LoginLicenseDialog_LoginLicenseDialogTitle);
		setMessage(UIMessages.ODEN_LICENSE_LoginLicenseDialog_LoginLicenseDialogDecription);

		setTitleImage(createImage("oden_chikuwa.png"));

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

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell
				.setText(UIMessages.ODEN_LICENSE_LoginLicenseDialog_LoginLicenseTitle);

		Monitor mon = Display.getDefault().getMonitors()[0];
		Rectangle rect = mon.getBounds();
		int width = 450;
		int height = 330;
		newShell.setBounds(rect.width / 2 - width / 2, rect.height / 2 - height
				/ 2, width, height);
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				UIMessages.ODEN_LICENSE_LoginLicenseDialog_ButtonLicneseSave,
				true);
		createButton(parent, IDialogConstants.YES_ID,
				UIMessages.ODEN_LICENSE_LoginLicenseDialog_ButtonTrial, true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);

		Button trialButton = getButton(IDialogConstants.YES_ID);
		if (way == Way.FIRST) {
			trialButton.setEnabled(true);
		} else if (way == Way.ALREADY) {
			trialButton.setEnabled(false);
		}

	}

	protected Control createDialogArea(Composite parent) {

		Composite parentComposite = (Composite) super.createDialogArea(parent);

		Composite composite = new Composite(parentComposite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		composite.setLayout(layout);
		layout.marginHeight = 20;
		layout.marginWidth = 20;
		composite.setLayoutData(data);

		Label label = new Label(composite, SWT.NONE);
		label
				.setText(UIMessages.ODEN_LICENSE_LoginLicenseDialog_LabelEnterLicense);
		label.setFont(new Font(parent.getDisplay(), "Arial", 13, SWT.BOLD)); //$NON-NLS-1$
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		label.setLayoutData(gridData);

		Label labelEmptyLine = new Label(composite, SWT.NONE);
		labelEmptyLine.setText(""); //$NON-NLS-1$
		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		gridData1.horizontalSpan = 3;
		labelEmptyLine.setLayoutData(gridData1);

		Label labelOrg = new Label(composite, SWT.NONE);
		labelOrg.setText(UIMessages.ODEN_LICENSE_LoginLicenseDialog_ColumnOrg);

		textOrg = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridDataTextOrg = new GridData(GridData.FILL_HORIZONTAL);
		gridDataTextOrg.horizontalSpan = 2;
		textOrg.setLayoutData(gridDataTextOrg);

		Label labelName = new Label(composite, SWT.NONE);
		labelName
				.setText(UIMessages.ODEN_LICENSE_LoginLicenseDialog_ColumnName);

		textName = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridDataTextName = new GridData(GridData.FILL_HORIZONTAL);
		gridDataTextName.horizontalSpan = 2;
		textName.setLayoutData(gridDataTextName);

		Label labelID = new Label(composite, SWT.NONE);
		labelID.setText(UIMessages.ODEN_LICENSE_LoginLicenseDialog_ColumnID);

		textID = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridDataTextID = new GridData(GridData.FILL_HORIZONTAL);
		gridDataTextID.horizontalSpan = 2;
		textID.setLayoutData(gridDataTextID);

		Label labelLicense = new Label(composite, SWT.NONE);
		labelLicense
				.setText(UIMessages.ODEN_LICENSE_LoginLicenseDialog_ColumnLicense);

		textLicense = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridDataText = new GridData(GridData.FILL_HORIZONTAL);
		gridDataText.horizontalSpan = 2;
		textLicense.setLayoutData(gridDataText);

		checkLicense();

		return composite;
	}

	private void checkLicense() {
		if (confirmExistFile()) {
			String[] result = readLicenseFile();
			String inFileOrg = result[0];
			String inFileName = result[1];
			String inFileID = result[2];
			String inFileLicense = result[3];

			DecodingID decodingID = new DecodingID();
			boolean boolID = decodingID.checkIdAvailable(inFileID,
					inFileLicense);

			DecodingLicense decodingLicense = new DecodingLicense();
			boolean boolLicense = decodingLicense.checkLicenseAvailable(
					inFileOrg, inFileName, inFileLicense);

			if (boolID && boolLicense) {
				close();
			} else {
			}
		} else {

		}

	}

	public boolean confirmExistFile() {
		file = new File(
				LicenseCheckAction.getPlatform()
						+ OdenFiles.LICENSE_FILE_FULL_NAME); 
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void buttonPressed(int buttonId) { // select trial version
		if (buttonId == IDialogConstants.YES_ID) {
			selectButtonTrial();
		} else if (buttonId == IDialogConstants.OK_ID) {
			selectButtonOK();
		} else if (buttonId == IDialogConstants.CANCEL_ID) {
			close();
		} else {
		}
	}

	private void selectButtonTrial() {
		try {
			File f = new File(LicenseCheckAction.getPlatform()
					+ OdenFiles.LICENSE_TRIAL_FILE_CONF);
			f.mkdirs();

			PrintStream out = new PrintStream(new FileOutputStream(f.getPath()
					+ "/" + OdenFiles.TRIAL_FILE_NAME));
			long today = System.currentTimeMillis();
			out.println(today);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		close();
	}

	private void selectButtonOK() {
		String organization = textOrg.getText();
		String name = textName.getText();
		String id = textID.getText();
		String license = textLicense.getText();

		DecodingID decodingID = new DecodingID();
		boolean boolID = decodingID.checkIdAvailable(id, license);

		DecodingLicense dl = new DecodingLicense();
		boolean b = dl.checkLicenseAvailable(organization, name, license);

		if (boolID && b) {
			try {
				File f = new File(LicenseCheckAction.getPlatform()
						+ OdenFiles.LICENSE_TRIAL_FILE_CONF); 
				f.mkdirs();

				PrintStream out = new PrintStream(new FileOutputStream(f
						.getPath()
						+ "/" + OdenFiles.LICENSE_FILE_NAME));
				out.println(textOrg.getText());
				out.println(textName.getText());
				out.println(textID.getText());
				out.println(textLicense.getText());
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			close();
		} else {
			MessageDialog
					.openError(
							Display.getDefault().getActiveShell(),
							UIMessages.ODEN_LICENSE_LoginLicenseDialog_MsgAuthenticationFail,
							UIMessages.ODEN_LICENSE_LoginLicenseDialog_MsgAuthenticationFailDesc);
			textOrg.setText(""); //$NON-NLS-1$
			textName.setText(""); //$NON-NLS-1$
			textID.setText(""); //$NON-NLS-1$
			textLicense.setText(""); //$NON-NLS-1$
			textOrg.setFocus();
		}
	}

	@Override
	protected boolean isResizable() {
		return false;
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
