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
package anyframe.oden.eclipse.core.utils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import anyframe.oden.eclipse.core.OdenException;

public abstract class OdenProgress extends Job {

	public OdenProgress(String name) {
		super(name);
	}

	private static IStatus status;

	protected IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
		try {
			executeMe();
		} catch (OdenException e) {
			setStatus(null);
			setStatus(new Status(IStatus.ERROR, "anyframe.oden.eclipse.core",
					IStatus.ERROR, "Error", null));
		}
		if (monitor.isCanceled())
			return new Status(IStatus.CANCEL, "anyframe.oden.eclipse.core",
					IStatus.CANCEL, "Cancel", null);
		monitor.done();
		return new Status(IStatus.OK, "anyframe.oden.eclipse.core",
				IStatus.OK, "Success", null);
	}

	abstract protected void executeMe() throws OdenException;

	public static IStatus getStatus() {
		return status;
	}

	public void setStatus(IStatus status) {
		this.status = status;
	}

}