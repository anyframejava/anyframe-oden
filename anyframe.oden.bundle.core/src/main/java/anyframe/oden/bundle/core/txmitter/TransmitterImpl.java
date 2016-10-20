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
package anyframe.oden.bundle.core.txmitter;

import anyframe.oden.bundle.common.Logger;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.deploy.DeployerService;
import ch.ethz.iks.r_osgi.RemoteOSGiService;
import ch.ethz.iks.r_osgi.RemoteServiceReference;
import ch.ethz.iks.r_osgi.URI;

/**
 * @see anyframe.oden.bundle.core.txmitter.TransmitterService
 * 
 * @author joon1k
 *
 */
public class TransmitterImpl implements TransmitterService {
	private RemoteOSGiService remoteService;
	
	protected void setRemoteOSGiService(RemoteOSGiService remote){
		this.remoteService = remote;
	}
	
	public TransmitterImpl() {
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
		URI uri = new URI("r-osgi://" + addr);
		
		// check if connection is available really
		DeployerService ds = null;
		try{
			ds = getDeployer(uri);
			if(ds == null) return null;
			ds.alive();
		}catch(Exception e){
			// try one more
			try{
				ds = getDeployer(uri);
				if(ds == null) return null;
				ds.alive();
			}catch(Exception e2){
				Logger.error(e2);
				return null;
			}
		}
		return ds;
	}
	
//	private DeployerService getDeployer(URI uri) throws Exception{
//		return new DeployerImpl();
//	}
	
	private DeployerService getDeployer(URI uri) throws Exception{
		if(remoteService != null){
			final RemoteServiceReference[] refs = 
				remoteService.getRemoteServiceReferences(uri, DeployerService.class.getName(), null);
	
			if(refs != null && refs.length > 0)
				return (DeployerService) remoteService.getRemoteService(refs[0]);
		}
		return null;
	}
	
	public void disconnect(String addr) throws Exception{
		if(remoteService != null)
			remoteService.disconnect(new URI("r-osgi://" + addr));
	}
}
