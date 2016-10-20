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