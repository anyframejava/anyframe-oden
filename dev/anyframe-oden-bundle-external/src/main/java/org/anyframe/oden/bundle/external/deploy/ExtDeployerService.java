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
package org.anyframe.oden.bundle.external.deploy;

import java.util.List;
import java.util.Map;

import org.anyframe.oden.bundle.deploy.CfgReturnScript;
import org.anyframe.oden.bundle.external.config.CfgHistory;
import org.anyframe.oden.bundle.external.config.CfgJob;
import org.anyframe.oden.bundle.external.config.CfgReturnPreview;
import org.anyframe.oden.bundle.external.config.CfgReturnStatus;
import org.anyframe.oden.bundle.external.config.CfgReturnVO;
import org.anyframe.oden.bundle.external.config.CfgScript;
import org.anyframe.oden.bundle.external.config.CfgTarget;

/**
 * This class provides some methods to manipulate remote files. If you want to
 * handling remote files, register this service to the R-OSGi bundle. Your
 * service can access remote files using that registered service.
 * 
 * @author junghwan.hong
 * 
 */
public interface ExtDeployerService {

	/**
	 * External API method , which deploy
	 * 
	 * @param job
	 * @return
	 * @throws Exception
	 */
	public CfgReturnVO execute(CfgJob job) throws Exception;

	/**
	 * External API method , which deploy root directory in all files
	 * 
	 * @param job
	 * @return
	 * @throws Exception
	 */
	public CfgReturnVO executeRoot(CfgJob job) throws Exception;

	/**
	 * External API method , which deploy error files again
	 * 
	 * @param txid
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public CfgReturnVO reexecute(String txid, String user) throws Exception;

	/**
	 * External API method , which undo the deployment job
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public CfgReturnVO rollback(String id) throws Exception;

	/**
	 * External API method , which stop current deployment job
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String stop(String id) throws Exception;

	/**
	 * External API method , which show the deployment logs
	 * 
	 * @param id
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public CfgHistory log(String id, int pageIndex, int pageSize)
			throws Exception;

	/**
	 * External API method , which run command or script
	 * 
	 * @param script
	 * @return
	 * @throws Exception
	 */
	public Map<String, CfgReturnScript> run(CfgScript script) throws Exception;

	/**
	 * External API method , which check available agent or not
	 * 
	 * @param agents
	 * @return
	 * @throws Exception
	 */
	public Map<String, Boolean> checkAgent(List<CfgTarget> agents)
			throws Exception;

	/**
	 * External API method , which check deployment status
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<CfgReturnStatus> status(String id) throws Exception;

	/**
	 * External API method , which show preview of the deployment job
	 * 
	 * @param job
	 * @return
	 * @throws Exception
	 */
	public Map<String, List<CfgReturnPreview>> test(CfgJob job)
			throws Exception;
}
