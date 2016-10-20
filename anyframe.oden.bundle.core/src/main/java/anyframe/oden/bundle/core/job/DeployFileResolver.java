package anyframe.oden.bundle.core.job;

import java.util.Collection;

import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.DeployFile;

public interface DeployFileResolver {
	Collection<DeployFile> resolveDeployFiles() throws OdenException;
}
