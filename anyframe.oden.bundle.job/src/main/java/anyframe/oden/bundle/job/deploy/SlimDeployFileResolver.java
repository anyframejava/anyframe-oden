package anyframe.oden.bundle.job.deploy;

import java.util.Collection;

import anyframe.oden.bundle.common.OdenException;

public interface SlimDeployFileResolver {
	Collection<SlimDeployFile> resolveDeployFiles() throws OdenException;
}
