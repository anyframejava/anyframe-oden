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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.deploy.DeployerFactory;
import org.anyframe.oden.bundle.deploy.DeployerService;

/**
 * @see anyframe.oden.bundle.core.txmitter.TransmitterService
 * 
 * @author joon1k
 *
 */
public class TransmitterImpl implements TransmitterService {
	private BundleContext context;
	
	protected void activate(ComponentContext context){
		this.context = context.getBundleContext();
	}
	
	private List<DeployerFactory> deployfactories = new ArrayList<DeployerFactory>();
	
	protected void addDeployerFactory(DeployerFactory df){
		deployfactories.add(df);
	}
	
	protected void removeDeployerFactory(DeployerFactory df){
		deployfactories.remove(df);
	}
	
	/**
	 * get agent which is located in the ip. 
	 * must be released by releaseDeployer(ip)
	 * <pre>getDeployer("localhost:9871")</pre>
	 * @param addr ip:port on which agent is located
	 * @return
	 * @throws OdenException 
	 * @throws Exception 
	 */
	public DeployerService getDeployer(String addr){
		// check if connection is available really
		DeployerService ds = null;
		try{
			ds = _getDeployer(addr);
			if(ds == null || !ds.alive())
				throw new IOException("Fail to access: " + addr);
		}catch(Exception e){
			// try one more
			try{
				ds = _getDeployer(addr);
				if(ds == null || !ds.alive())
					throw new IOException("Fail to access: " + addr);
			}catch(Exception e2){
				Logger.debug("Fail to access: " + addr);
				return null;
			}
		}
		return ds;
	}
		
	private DeployerService _getDeployer(String addr) throws Exception{
		final int idx = addr.indexOf("://");
		final String protocol = addr.substring(0, idx < 0 ? 0 : idx+3);
		for(DeployerFactory factory : deployfactories){
			if(factory.getProtocol().equals(protocol))
				return factory.newInstance(addr);
		}
		return null;
	}
	
	public void disconnect(String addr) throws Exception{
	}
}
