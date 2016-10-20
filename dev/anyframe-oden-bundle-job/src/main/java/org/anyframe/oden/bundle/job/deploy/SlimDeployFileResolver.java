package org.anyframe.oden.bundle.job.deploy;

import java.util.Collection;

import org.anyframe.oden.bundle.common.OdenException;

public interface SlimDeployFileResolver {
	Collection<SlimDeployFile> resolveDeployFiles() throws OdenException;
}
