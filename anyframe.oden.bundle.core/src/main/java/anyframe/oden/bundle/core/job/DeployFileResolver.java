package anyframe.oden.bundle.core.job;

import java.util.Set;

import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.DeployFile;

public interface DeployFileResolver {
	Set<DeployFile> resolveDeployFiles() throws OdenException;
}
