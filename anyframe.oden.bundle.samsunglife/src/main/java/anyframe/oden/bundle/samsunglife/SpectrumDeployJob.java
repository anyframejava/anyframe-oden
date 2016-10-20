package anyframe.oden.bundle.samsunglife;

import org.osgi.framework.BundleContext;

import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.command.TaskDeployJob;
import anyframe.oden.bundle.core.job.DeployFileResolver;

/**
 * 
 * Job to deploy for spectrum using
 * anyframe.oden.bundle.core.command.TaskDeployJob2 class.
 * 
 * @author joon1k
 * 
 */
public class SpectrumDeployJob extends TaskDeployJob {
	public SpectrumDeployJob(BundleContext context, String user, String desc,
			DeployFileResolver resolver) throws OdenException {
		super(context, user, desc, resolver);
	}
}
