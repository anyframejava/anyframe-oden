package anyframe.oden.eclipse.core.license.dialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenFiles;
import anyframe.oden.eclipse.core.license.dialog.LoginLicenseDialog.Way;

public class TrialInfoDialog extends TitleAreaDialog {

	public enum Type {
		INFO, WARNING
	}

	private Type type;
	private boolean existFile;

	private Image odenImage;
	private static final String IMAGES_PATH = "icons/";

	public static Image createImage(String imagePath) {
		final Bundle pluginBundle = Platform.getBundle(OdenActivator.PLUGIN_ID);
		final Path imageFilePath = new Path(TrialInfoDialog.IMAGES_PATH
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

		if (type == Type.INFO) {
			setTitle("Anyframe Oden Trial Information");
			setMessage("You can use for 30 days. Continue to use, register the license.");
		} else if (type == Type.WARNING) {
			setTitle("Anyframe Oden Trial is Expired.");
			setMessage("Trial is expired. Continue to use, register the license.");
		}

		setTitleImage(createImage("oden_chikuwa.png"));

		if (odenImage != null) {
			setTitleImage(odenImage);
		}
		return contents;

	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Anyframe Oden");
		newShell.setBounds(680, 400, 300, 300);
	}

	public TrialInfoDialog(Shell parentShell, Type type) {
		super(parentShell);
		this.type = type;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		composite.setLayout(layout);
		layout.marginHeight = 20;
		layout.marginWidth = 20;
		composite.setLayoutData(data);

		if (type == Type.INFO) {
			Label labelInfo = new Label(composite, SWT.NONE);
			labelInfo.setFont(new Font(composite.getDisplay(), "Arial", 13,
					SWT.BOLD));
			labelInfo.setText("Anyframe Oden Trial");
			existFile = true;
		} else if (type == Type.WARNING) {
			File file = new File(LicenseCheckAction.getPlatform()
					+ OdenFiles.TRIAL_FILE_FULL_NAME);
			if(file.exists()){
				Label labelInfo = new Label(composite, SWT.NONE);
				labelInfo.setFont(new Font(composite.getDisplay(), "Arial", 13,
						SWT.BOLD));
				labelInfo.setForeground(Display.getDefault().getSystemColor(
						SWT.COLOR_RED));
				labelInfo.setText("Anyframe Oden Trial is Expired");
				existFile = true;
			}else{
				Label labelInfo = new Label(composite, SWT.NONE);
				labelInfo.setFont(new Font(composite.getDisplay(), "Arial", 13,
						SWT.BOLD));
				labelInfo.setForeground(Display.getDefault().getSystemColor(
						SWT.COLOR_RED));
				labelInfo.setText("You don't use Trial version.");
				existFile = false;
			}
		} else {
		}

		emptyLine(composite, 1);
		
		if(existFile){
			Label label1 = new Label(composite, SWT.NONE);
			label1.setText("You can use this until : " + get30DayLater() + ".");
		}else{
			emptyLine(composite, 1);
		}

		emptyLine(composite, 1);

		Label label2 = new Label(composite, SWT.NONE);
		label2.setText("If you want to register the license,");

		Label label3 = new Label(composite, SWT.NONE);
		label3.setText("push the 'License' button, and register.");

		return composite;
	}

	private void emptyLine(Composite composite, int n) {
		for (int i = 0; i < n; i++) {
			Label labelEmptyLine = new Label(composite, SWT.NONE);
			labelEmptyLine.setText("");
		}

	}

	private String get30DayLater() {
		String currentAgo30Time = "";
		try {
			FileReader fr = new FileReader(new File(LicenseCheckAction.getPlatform()
					+ OdenFiles.TRIAL_FILE_FULL_NAME));
			BufferedReader inFile = new BufferedReader(fr);

			SimpleDateFormat regFormatter = new SimpleDateFormat("yyyyMMdd");

			String trialFromDate = new SimpleDateFormat("yyyyMMdd").format(Long
					.valueOf(inFile.readLine()));
			Date trialTime = regFormatter.parse(trialFromDate);

			inFile.close();
			fr.close();

			Calendar c = Calendar.getInstance();
			c.setTime(trialTime);
			c.add(c.DATE, 30); // 30days later
			DecimalFormat df = new DecimalFormat("00");

			currentAgo30Time = df.format(c.get(Calendar.YEAR)) + "."
					+ df.format((c.get(Calendar.MONTH) + 1)) + "."
					+ df.format(c.get(Calendar.DATE));

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return currentAgo30Time;
	}

	@Override
	protected void cancelPressed() { // License 버튼
		close();
		LoginLicenseDialog dialog = new LoginLicenseDialog(Display.getCurrent()
				.getActiveShell(), Way.ALREADY);
		dialog.open();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, "License", true);
		createButton(parent, IDialogConstants.OK_ID, "OK", true);
	}

	@Override
	protected boolean isResizable() {
		return false;
	}

}
