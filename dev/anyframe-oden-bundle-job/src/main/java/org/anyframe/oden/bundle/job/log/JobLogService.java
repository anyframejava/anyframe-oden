/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.anyframe.oden.bundle.job.log;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.core.DeployFile;
import org.anyframe.oden.bundle.core.DeployFile.Mode;
import org.anyframe.oden.bundle.core.record.RecordElement2;
import org.anyframe.oden.bundle.job.deploy.SlimDeployFile;

/**
 * This is JobLogService Interface
 * 
 * @author Junghwan Hong
 */
public interface JobLogService {
	public Set<DeployFile> show(String id, String path, Mode mode,
			boolean isFailOnly) throws OdenException;

	public ShortenRecord search(String id) throws OdenException;

	public List<ShortenRecord> search(String job, String user, String path,
			boolean isFailOnly) throws OdenException;

	public LogError getErrorLog(String date) throws OdenException;

	public void record(RecordElement2 record) throws OdenException;

	public void record(String id, String user, long time, String desc,
			int nSuccess, String error, Collection<SlimDeployFile> fs)
			throws OdenException;
}
