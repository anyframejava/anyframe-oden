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
package org.anyframe.oden.bundle.core.txmitter;

import org.anyframe.oden.bundle.deploy.DeployerService;

/**
 * Oden Service to communicate with remote DeployerService. This sends
 * DelegateService's request to DeployerService.
 * 
 * @author Junghwan Hong
 */
public interface TransmitterService {
	/**
	 * get the DeployerService from addr
	 * 
	 * @param addr
	 * @return
	 */
	public DeployerService getDeployer(String addr);

	/**
	 * disconnect to the DeployerService
	 * 
	 * @param addr
	 * @throws Exception
	 */
	public void disconnect(String addr) throws Exception;
}
