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
package org.anyframe.oden.bundle.core.job;

import java.util.Collection;
import java.util.Collections;

import org.anyframe.oden.bundle.common.Assert;
import org.anyframe.oden.bundle.common.BundleUtil;
import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.core.DeployFile;
import org.anyframe.oden.bundle.core.command.DeployerManager;
import org.anyframe.oden.bundle.core.record.DeployLogService;
import org.anyframe.oden.bundle.core.record.RecordElement2;
import org.anyframe.oden.bundle.core.txmitter.TransmitterService;
import org.osgi.framework.BundleContext;

/**
 * Job to write log & make cancel available.
 * 
 * @author Junghwan Hong
 */
public abstract class DeployJob extends Job {
	protected TransmitterService txmitterService;

	protected DeployLogService deploylog;

	protected DeployerManager deployerManager;

	protected Collection<DeployFile> deployFiles = Collections.EMPTY_SET;

	protected String user;

	protected String bakLoc;

	protected String errorMessage;

	private DeployFileResolver resolver;

	public DeployJob(BundleContext context, String user, String desc,
			DeployFileResolver resolver) throws OdenException {
		super(context, desc);
		this.resolver = resolver;
		txmitterService = (TransmitterService) BundleUtil.getService(context,
				TransmitterService.class);
		deploylog = (DeployLogService) BundleUtil.getService(context,
				DeployLogService.class);
		Assert.check(txmitterService != null && deploylog != null,
				"Fail to load service.");

		String undo = context.getProperty("deploy.undo");
		deployerManager = new DeployerManager(context, id,
				(undo != null && undo.equals("true")));
		this.user = user;
	}

	@Override
	void start() {
		status = RUNNING;
		currentWork = "resolving deploy files...";
		try {
			deployFiles = resolver.resolveDeployFiles();
		} catch (OdenException e) {
			errorMessage = e.getMessage();
			return;
		}

		totalWorks = deployFiles.size() + additionalWorks;
		finishedWorks = 1; // 1 is kind of addtional work
		run();
		finishedWorks = totalWorks - 1; // 1 is kind of additional work
	}

	@Override
	public int todoWorks() {
		return totalWorks - additionalWorks;
	}

	@SuppressWarnings("PMD")
	protected void done() {
		try {
			RecordElement2 r = new RecordElement2(id, deployFiles, user,
					System.currentTimeMillis(), desc);
			if (errorMessage != null) {
				r.setLog(errorMessage);
				r.setSucccess(false);
			} else if (deployFiles.isEmpty()) {
				r.setSucccess(true);
			}
			deploylog.record(r);

		} catch (OdenException e) {
			Logger.error(e);
		}
		deployFiles.clear();
		deployFiles = null;
	}

}
