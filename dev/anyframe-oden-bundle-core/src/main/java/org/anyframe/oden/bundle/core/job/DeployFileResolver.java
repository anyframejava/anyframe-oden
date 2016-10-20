package org.anyframe.oden.bundle.core.job;

import java.util.Collection;

import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.core.DeployFile;

public interface DeployFileResolver {
	Collection<DeployFile> resolveDeployFiles() throws OdenException;
}
