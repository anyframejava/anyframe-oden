/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package anyframe.oden.bundle.core.command;

import java.util.HashMap;
import java.util.Map;

import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.txmitter.TransmitterService;
import anyframe.oden.bundle.deploy.DeployerService;

public class DeployerManager{
	
	private Map<String, DeployerService> pool = new HashMap<String, DeployerService>();
	
	private TransmitterService transmitter;
	
	public DeployerManager(TransmitterService transmitter){
		this.transmitter = transmitter;
	}
	
	public DeployerService getDeployer(DeployFile f){
		return getDeployer(f.getAgent().agentAddr());
	}
	
	/**
	 * 
	 * @param addr
	 * @return null if couldn't connect that DeployerService
	 */
	public DeployerService getDeployer(String addr){
		DeployerService ds = null;
		
		if(!pool.containsKey(addr)){
			ds = transmitter.getDeployer(addr);
			pool.put(addr, ds);
			return ds;
		}
		return pool.get(addr);
	}
}
