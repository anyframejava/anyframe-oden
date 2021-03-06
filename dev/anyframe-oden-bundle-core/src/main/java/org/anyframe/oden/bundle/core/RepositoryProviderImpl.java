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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.anyframe.oden.bundle.common.FileInfo;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.core.repository.RepositoryService;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

/**
 * This is RepositoryProviderImpl class.
 * 
 * @author Junghwan Hong
 * @see anyframe.oden.bundle.core.RepositoryProviderService
 */
public class RepositoryProviderImpl implements RepositoryProviderService {
	private List<RepositoryService> repoServices = new Vector<RepositoryService>();

	private BundleContext context;

	@SuppressWarnings("PMD")
	public RepositoryProviderImpl() {
	}

	protected void activate(ComponentContext context) {
		this.context = context.getBundleContext();
	}

	protected void addRepositoryService(RepositoryService rs) {
		repoServices.add(rs);
	}

	protected void removeRepositoryService(RepositoryService rs) {
		repoServices.remove(rs);
	}

	public List getPackageList(String id, String pwd, String user)
			throws Exception {
		return null;
	}

	public List getContents(String file, String id, String pwd, String user)
			throws Exception {
		return null;
	}

	public RepositoryService getRepoServiceByURI(String[] repoArgs) {
		for (RepositoryService repoService : repoServices) {
			if (repoService.matchedURI(repoArgs)) {
				return repoService;
			}
		}
		return null;
	}

	public RepositoryAdaptor getRepositoryAdaptor(String[] args) {
		RepositoryService svc = getRepoServiceByURI(args);
		if (svc == null) {
			return null;
		}
		return new RepositoryAdaptor(svc, args);
	}

	public boolean availableRepository(String[] repoArgs) {
		return getRepoServiceByURI(repoArgs) != null;
	}

	public List<String> getRepositoryProtocols() {
		List<String> types = new ArrayList<String>();
		for (RepositoryService repoService : repoServices) {
			types.add(repoService.getProtocol());
		}
		return types;
	}

	public List<FileInfo> getFilesFromRepo(String[] repoargs)
			throws OdenException {
		RepositoryService repo = getRepoServiceByURI(repoargs);
		if (repo == null) {
			throw new OdenException("Couldn't find a RepositoryService for "
					+ Arrays.toString(repoargs));
		}
		return repo.getFileList(repoargs);
	}

	public List<String> getRepositoryUsages() {
		List<String> usages = new ArrayList<String>();
		for (RepositoryService rs : repoServices) {
			usages.add(rs.getUsage());
		}
		return usages;
	}

}
