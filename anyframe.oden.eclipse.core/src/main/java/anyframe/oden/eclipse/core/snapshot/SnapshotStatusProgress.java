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
package anyframe.oden.eclipse.core.snapshot;

import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenMessages;
import anyframe.oden.eclipse.core.utils.DialogUtil;
import anyframe.oden.eclipse.core.utils.OdenProgress;

/**
 * For using OdenProgress class, override executeMe method and get a result
 * message.
 * 
 * @author LEE Sujeong
 * @version 1.0.0 RC2
 * 
 */
public class SnapshotStatusProgress {

	/**
	 * Do job with progress bar and put message.
	 * 
	 * @param msg
	 * @throws OdenException
	 */
	public static void statusProgress(final String msg) throws OdenException {

		OdenProgress jobProgress = new OdenProgress(
				OdenMessages.ODEN_SNAPSHOT_SnapshotView_StatusMsg) {

			@Override
			protected void executeMe() throws OdenException {
				new SnapshotViewContentProvider();
				try {
					SnapshotViewContentProvider.doOdenBroker(
							SnapshotView.SHELL_URL, msg);
				} catch (OdenException e) {
					throw new OdenException(e);
				}
			}
		};

		jobProgress.setUser(true);
		jobProgress.schedule();

		jobProgress.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				String resultTemp = event.getResult().toString();
				StringTokenizer tokenizer = new StringTokenizer(resultTemp, ":"); //$NON-NLS-1$
				final String msg = tokenizer.nextToken();

				IStatus status = OdenProgress.getStatus();
				if (status == null) {
					if (msg.equals("Status OK")) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								DialogUtil.openMessageDialog("Finished",
										"Finish the job.",
										MessageDialog.INFORMATION);
								SnapshotView.setStatusMessage(msg);
							}
						});
						SnapshotView.refreshTree();
					}
				} else {
				}
			}

		});

	}
}
