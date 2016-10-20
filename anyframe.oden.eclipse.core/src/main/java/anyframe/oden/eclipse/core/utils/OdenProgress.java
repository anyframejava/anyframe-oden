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