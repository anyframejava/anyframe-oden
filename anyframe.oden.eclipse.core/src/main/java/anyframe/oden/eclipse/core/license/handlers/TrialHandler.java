package anyframe.oden.eclipse.core.license.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import anyframe.oden.eclipse.core.OdenFiles;
import anyframe.oden.eclipse.core.license.dialog.LicenseCheckAction;
import anyframe.oden.eclipse.core.license.dialog.TrialInfoDialog;
import anyframe.oden.eclipse.core.license.dialog.TrialInfoDialog.Type;

public class TrialHandler extends AbstractHandler {
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();

		File trialFile = new File(LicenseCheckAction.getPlatform()
				+ OdenFiles.TRIAL_FILE_FULL_NAME);

		TrialInfoDialog dialog;
		if (checkTrialFile(trialFile)) {
			dialog = new TrialInfoDialog(shell, Type.INFO);
			dialog.open();
		} else {
			dialog = new TrialInfoDialog(shell, Type.WARNING);
			dialog.open();
		}

		return dialog;
	}

	private boolean checkTrialFile(File trialFile) {// load
		boolean result = false;
		if (checkDemoDateValidation(trialFile)) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	public boolean checkDemoDateValidation(File trialFile) {

		boolean result = false;
		try {
			FileReader fr = new FileReader(trialFile);
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

			int currentAgo30Time = Integer.parseInt(df.format(c
					.get(Calendar.YEAR))
					+ ""
					+ df.format((c.get(Calendar.MONTH) + 1))
					+ ""
					+ df.format(c.get(Calendar.DATE)));

			int todayTime = Integer.parseInt(new SimpleDateFormat("yyyyMMdd")
					.format(System.currentTimeMillis()));

			if (currentAgo30Time >= todayTime) { // valid
				result = true;
			} else { // time over
				result = false;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return result;
	}
}
