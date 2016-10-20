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
package anyframe.oden.eclipse.core.snapshot;

import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.messages.UIMessages;
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
				UIMessages.ODEN_SNAPSHOT_SnapshotView_StatusMsg) {

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
//								DialogUtil.openMessageDialog("Finished",
//										"Finish the job.",
//										MessageDialog.INFORMATION);
//								SnapshotView.setStatusMessage(msg);
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
