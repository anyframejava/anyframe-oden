/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package anyframe.oden.bundle.core.txmitter;

import java.io.IOException;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import anyframe.oden.bundle.common.FatInputStream;
import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.FileUtil;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.Logger;
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
	private BundleContext context;
	
	private RemoteOSGiService remoteService;
	
	protected void activate(ComponentContext context){
		this.context = context.getBundleContext();
	}
	
	protected void setRemoteOSGiService(RemoteOSGiService remote){
		this.remoteService = remote;
	}
	
	protected void unsetRemoteOSGiService(RemoteOSGiService remote){
		this.remoteService = null;
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
	private DeployerService getDeployer(String addr) throws OdenException{
		try{
			if(remoteService != null){ 
				final RemoteServiceReference[] refs = 
					remoteService.getRemoteServiceReferences(
						new URI("r-osgi://" + addr), DeployerService.class.getName(), null);
		
				if(refs != null && refs.length > 0)
					return (DeployerService) remoteService.getRemoteService(refs[0]);
			}		
		}catch(Exception e){
			throw new OdenException(e);
		}
		return null;
	}
	
	public boolean available(String addr){
		try {
			if(getDeployer(addr) != null)
				return true;
		} catch (OdenException e) {
		}
		return false;
	}
	
	public List<String> deploy(String ip, String root, FatInputStream in, boolean update)
			throws OdenException{
		List<String> updatedfiles = null;
		
		DeployerService deployer = getDeployer(ip);
		Logger.log(LogService.LOG_DEBUG, "Deployer for deploy: " + deployer);
		try {
			String destpath = FileUtil.combinePath(root, in.getFileInfo().getPath()); 
			deployer.init(destpath, in.getFileInfo().lastModified(), update);
			byte[] buf = new byte[1024*8];
			int size = 0;
			while((size = in.read(buf)) != -1){
				deployer.write(buf, size);
			}
		} catch(Exception e){
			throw new OdenException(e);
		}finally {
			try {
				updatedfiles = deployer.close();
			} catch (IOException e) {
				throw new OdenException(e);
			}
		}	
		return updatedfiles;
	}

	/**
	 * get date for path on which ip's destRoot.
	 * @throws OdenException 
	 */
	public long getDate(String ip, String destRoot, String path) throws OdenException {
		long date = -1;
		
		DeployerService deployer = getDeployer(ip);
		try {
			date = deployer.getDate(destRoot, path);
		}finally {
			try {
				deployer.close();
			} catch (IOException e) {
				throw new OdenException(e);
			}
		}	
		return date;
	}

	
	/**
	 * @param ip
	 * @param srcLoc
	 * @param repoFile
	 * @return size of the archive
	 */
	public FileInfo backup(String ip, String srcLoc, String repoLoc) 
			throws OdenException {
		DeployerService deployer = getDeployer(ip);
		Logger.log(LogService.LOG_DEBUG, "Deployer for backup: " + deployer);
		try{
			return deployer.compress(srcLoc, repoLoc);
		}catch (Exception e){
			throw new OdenException(e);
		}
	}

	public void removeSnapshot(String ip, String repoLoc, String snapshot)
			throws OdenException {
		DeployerService deployer = getDeployer(ip);
		try {
			deployer.removeFile(repoLoc, snapshot);	
		} catch (Exception e) {
			throw new OdenException(e);
		}
	}

	/**
	 * @return relative paths of deployed files.
	 */
	public List<String> restore(String ip, String repoLoc, String snapshot,
			String dest) throws OdenException {
		List<String> restoredfiles = null;
		DeployerService deployer = getDeployer(ip);
		try {
			restoredfiles = deployer.extract(repoLoc, snapshot, dest);
		} catch(OdenException e) {
			throw e;
		} catch (Exception e) {
			throw new OdenException(e);
		}	
		return restoredfiles;
	}
	
}
