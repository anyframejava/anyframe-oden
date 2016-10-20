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
package org.anyframe.oden.bundle.job.deploy;

import java.util.HashMap;
import java.util.Map;

import org.anyframe.oden.bundle.common.Assert;
import org.anyframe.oden.bundle.common.BundleUtil;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.core.config.OdenConfigService;
import org.anyframe.oden.bundle.core.txmitter.TransmitterService;
import org.anyframe.oden.bundle.deploy.DeployerService;
import org.osgi.framework.BundleContext;

/**
 * This class control connection pool for server and agents. 이 클래스를 사용함으로 써, 중간에
 * agent 와 연결이 실패하더라도 다음번에 연결을 시도하지 않음. connection timeout 시 시간이 너무 오래 걸리기 때문에
 * 매번 재 시도를 할 경우 배포가 너무 지연되게 됨.
 * 
 * @author Junghwan Hong
 */
public class SlimDeployManager {

	private Map<String, DeployerService> pool = new HashMap<String, DeployerService>();

	private Map<String, String> bakLocs = new HashMap<String, String>();

	private TransmitterService transmitter;

	private OdenConfigService cfg;

	private String id;

	private boolean undo;

	public SlimDeployManager(BundleContext ctx, String id, boolean undo)
			throws OdenException {
		this.undo = undo;
		this.transmitter = (TransmitterService) BundleUtil.getService(ctx,
				TransmitterService.class);
		this.cfg = (OdenConfigService) BundleUtil.getService(ctx,
				OdenConfigService.class);
		Assert.check(transmitter != null && cfg != null,
				"Fail to load service.");
		this.id = id;
	}

	/**
	 * disconnect to the DeployerService which are related specified DeployFile
	 * 
	 * @param f
	 * @throws Exception
	 */
	public void disconnect(String addr) throws Exception {
		transmitter.disconnect(addr);
	}

	/**
	 * get the DeployerService specified by addr
	 * 
	 * @param addr
	 * @return null if couldn't connect that DeployerService
	 */
	public DeployerService getDeployer(String addr) {
		if (addr == null) {
			return null;
		}

		if (!pool.containsKey(addr)) {
			DeployerService ds = transmitter.getDeployer(addr);
			// Althoght ds is null, it is saved. afterall, at first time it
			// couldn' t connect to the agent,
			// it doesn't try to connect to it again.
			pool.put(addr, ds);
			return ds;
		}
		return pool.get(addr);
	}
}
