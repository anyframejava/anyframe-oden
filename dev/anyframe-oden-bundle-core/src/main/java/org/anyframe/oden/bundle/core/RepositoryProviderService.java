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
package org.anyframe.oden.bundle.core;

import java.util.List;

import org.anyframe.oden.bundle.common.FileInfo;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.core.repository.RepositoryService;

/**
 * interface class to provides convenient methods to provide repository's
 * information.
 * 
 * @author Junghwan Hong
 */
public interface RepositoryProviderService {
	/**
	 * get Repository Service which are matched with the repository arguments
	 * 
	 * @param repoArgs
	 * @return
	 */
	public RepositoryService getRepoServiceByURI(String[] repoArgs);

	public RepositoryAdaptor getRepositoryAdaptor(String[] args);

	/**
	 * get all loaded RepositoryService's protocols
	 * 
	 * @return
	 */
	public List<String> getRepositoryProtocols();

	/**
	 * get file information which are denoted by repository arguments
	 * 
	 * @param repoArgs
	 * @return
	 * @throws OdenException
	 */
	public List<FileInfo> getFilesFromRepo(String[] repoArgs)
			throws OdenException;

	/**
	 * get all loaded RepositoryService's usage
	 * 
	 * @return
	 */
	public List<String> getRepositoryUsages();

	/**
	 * check if there are any matched RepositoryService with the specified
	 * repository arguments.
	 * 
	 * @param repoArgs
	 * @return
	 */
	public boolean availableRepository(String[] repoArgs);

}
