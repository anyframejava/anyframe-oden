package anyframe.oden.eclipse.core.license.dialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Shell;

import anyframe.oden.eclipse.core.OdenFiles;
import anyframe.oden.eclipse.core.license.DecodingID;
import anyframe.oden.eclipse.core.license.DecodingLicense;
import anyframe.oden.eclipse.core.license.dialog.LoginLicenseDialog.Way;
import anyframe.oden.eclipse.core.license.dialog.TrialInfoDialog.Type;
import anyframe.oden.eclipse.core.license.handlers.TrialHandler;

public class LicenseCheckAction {

	String line = ""; //$NON-NLS-1$
	File file;

	public boolean confirmExistFile() {
		file = new File(getPlatform() + OdenFiles.LICENSE_FILE_FULL_NAME);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean confirmFile() {

		boolean result = false;

		File licenseFile = new File(getPlatform()
				+ OdenFiles.LICENSE_FILE_FULL_NAME);
		File trialFile = new File(getPlatform()
				+ OdenFiles.TRIAL_FILE_FULL_NAME);

		if (licenseFile.exists()) {
			if (checkLicenseFile(licenseFile)) {
				result = true;
			} else {
				result = false;
			}
		} else {
			if (trialFile.exists()) {
				if (checkTrialFile(trialFile)) {
					result = true;
				} else {
					// login dialog 띄우지 않기 위해(수정요)
					// new AliasManager().settingNoneExistXML();
					result = false;
				}
			} else {//라이센스도 없고 데모도 안쓸때
				LoginLicenseDialog dialog = new LoginLicenseDialog(new Shell(),
						Way.FIRST);
				dialog.open();
				result = false;
			}
		}
		return result;
	}

	private boolean checkLicenseFile(File licenseFile) {
		String[] result = readLicenseFile(licenseFile);
		String inFileOrg = result[0];
		String inFileName = result[1];
		String inFileID = result[2];
		String inFileLicense = result[3];

		DecodingID decodingID = new DecodingID();
		boolean boolID = decodingID.checkIdAvailable(inFileID, inFileLicense);

		DecodingLicense decodingLicense = new DecodingLicense();
		boolean boolLicense = decodingLicense.checkLicenseAvailable(inFileOrg,
				inFileName, inFileLicense);

		if (boolID && boolLicense) {
			// 맞는 라이센스
			return true;
		} else {
			// 라이센스는 있는데, 틀림
			LoginLicenseDialog license = new LoginLicenseDialog(new Shell(),
					Way.ALREADY);
			license.open();

			// dialog 띄워서 라이센스 틀렸다고 안내, 라이센스 입력 및 데모버튼
			return true;
		}
	}

	public String[] readLicenseFile(File licenseFile) {
		String[] result = new String[4];
		try {
			FileReader fr = new FileReader(licenseFile);
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

	private boolean checkTrialFile(File trialFile) {// load
		boolean result = false;

		if (new TrialHandler().checkDemoDateValidation(trialFile)) {
			result = true;
		} else {
			TrialInfoDialog dialog = new TrialInfoDialog(new Shell(),
					Type.WARNING);
			dialog.open();
			result = false;
		}
		return result;
	}

	public void readLicenseFile() {
		try {
			FileReader fr = new FileReader(file);
			BufferedReader inFile = new BufferedReader(fr);
			line = inFile.readLine();
			inFile.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		checkLicense(line);
	}

	public String returnLicense() {
		return line;
	}

	private void checkLicense(String license) {
		DecodingLicense dl = new DecodingLicense();
	}

	public static String getPlatform() {
		String result = ""; //$NON-NLS-1$
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			result = Platform.getInstallLocation().getURL().getPath()
					.substring(1)
					+ "/"; //$NON-NLS-1$
		} else {
			result = Platform.getInstallLocation().getURL().getPath();
		}
		return result;
	}
}
