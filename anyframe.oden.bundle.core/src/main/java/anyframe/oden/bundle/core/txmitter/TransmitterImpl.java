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

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import anyframe.common.bundle.log.Logger;
import anyframe.oden.bundle.common.ArraySet;
import anyframe.oden.bundle.common.FatInputStream;
import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.AgentLoc;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.Repository;
import anyframe.oden.bundle.core.DeployFile.Mode;
import anyframe.oden.bundle.deploy.DeployerService;
import anyframe.oden.bundle.deploy.DoneFileInfo;
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
		try{
			if(remoteService != null){
				URI uri = new URI("r-osgi://" + addr);
				final RemoteServiceReference[] refs = 
					remoteService.getRemoteServiceReferences(uri, DeployerService.class.getName(), null);
		
				if(refs != null && refs.length > 0)
					return (DeployerService) remoteService.getRemoteService(refs[0]);
			}		
		}catch(RuntimeException e){
			// ignore
		}
		return null;
	}
	
	public boolean readyToDeploy(DeployerService deployer, DeployFile f) {
		if(deployer == null)
			return false;
			
		try {		
			String parent = f.getAgent().location();
			String child = f.getPath();
			f.setMode(deployer.exist(parent, child) ? Mode.UPDATE : Mode.ADD);
			deployer.init(f.getAgent().location(), f.getPath(), f.getDate(), f.mode() == Mode.UPDATE);
		}catch(Exception e){
			Logger.error(e);
			try { deployer.close(null, null); } catch (IOException e1) { }
			return false;
		} 
		return true;
	}
			
	public boolean available(String addr){
		return getDeployer(addr) != null;
	}

	/**
	 * @return get date of the file(destUri/destRoot/srcin.getPath()). true if there's no file in the destUri
	 */
	public boolean isNew(String destUri, String destRoot, FatInputStream srcin){
		if(getDate(destUri, destRoot, srcin.getPath()) >= srcin.getLastModified())
			return false;
		return true;
	}
	
	/**
	 * get date for path on which addr's destRoot.
	 * -1 if errors have been occured.
	 */
	public long getDate(String addr, String destRoot, String path) {
		DeployerService deployer = getDeployer(addr);
		if(deployer != null)
			return deployer.getDate(destRoot, path);
		return -1;
	}

	public boolean exist(String addr, String parent, String path) {
		DeployerService deployer = getDeployer(addr);
		if(deployer != null)
			return deployer.exist(parent, path);
		return false;
	}
	
	public boolean writable(String addr, String parent, String path) {
		DeployerService deployer = getDeployer(addr);
		if(deployer != null)
			return deployer.writable(parent, path);
		return false;
	}
	
	/**
	 * @param ip
	 * @param srcLoc
	 * @param repoFile
	 * @return size of the archive
	 */
	public FileInfo backup(String ip, String srcLoc, String repoLoc) 
			throws OdenException {
		FileInfo info = null;
		
		DeployerService deployer = getDeployer(ip);
		Logger.log(LogService.LOG_DEBUG, "Deployer for backup: " + deployer);
		
		info = deployer.compress(srcLoc, repoLoc);
		if(info == null)
			throw new OdenException("Fail to compress file: " + repoLoc);
		return info;
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

	public boolean removeDir(Set<DeployFile> fs, DeployFile toRemove) throws OdenException {
		boolean success = true;
		AgentLoc loc = toRemove.getAgent();
		DeployerService deployer = getDeployer(loc.agentAddr());
		List<DoneFileInfo> results = deployer.backupNRemoveDir(loc.location(), toRemove.backupLocation());
		for(DoneFileInfo f : results){
			success = success & f.success();
			fs.add(new DeployFile(
					new Repository(new String[0]), 
					f.getPath(), loc, toRemove.backupLocation(), f.size(), f.lastModified(), 
					DeployFile.Mode.DELETE, f.success()));
		}
		return success;
	}
	
	/**
	 * @return relative paths of deployed files.
	 */
	public void restore(Set<DeployFile> fs, DeployFile toRestore) throws OdenException{
		String[] repo = toRestore.getRepo().args();	// snapshot
		DeployerService deployer = getDeployer(repo[0]);
		List<DoneFileInfo> results = deployer.extract(
				repo[1],		// snapshot root 
				toRestore.getPath(),		// snapshot path 
				toRestore.getAgent().location());		// destination
		if(results.size() == 0)
			throw new OdenException("Fail to restore: " + toRestore.getPath());
		
		for(DoneFileInfo d : results){ 
			boolean contains = false;
			for(DeployFile r : fs){		// update mode
				if( r.getAgent().equals(toRestore.getAgent()) 
						&& r.getPath().equals(d.getPath())){
					r.setRepo(toRestore.getRepo());
					r.setSize(d.size());
					r.setDate(d.lastModified());
					r.setMode(Mode.UPDATE);
					r.setSuccess(r.isSuccess() && d.success());
					contains = true;
					break;
				}
			}
			
			if(!contains)		// add mode
				((ArraySet)fs).addForce(new DeployFile(
						toRestore.getRepo(), 
						d.getPath(), 
						toRestore.getAgent(), 
						toRestore.backupLocation(),
						d.size(), 
						d.lastModified(), 
						DeployFile.Mode.ADD, 
						d.success()) );
			
		}
	}

	/**
	 * backup src/file to bak/file and copy src/file to dest/file. 
	 * if bak is null(add mode), backup will not be occured.
	 */
	public DeployFile backupCopy(AgentLoc src, String file, String dest, String bak) throws OdenException{
		DeployerService deployer = getDeployer(src.agentAddr());
		DoneFileInfo f= deployer.backupNCopy(src.location(), file, dest, bak);
		return new DeployFile(
				new Repository(src),
				file, 
				new AgentLoc(src.agentName(), src.agentAddr(), dest), "",
				f.size(), f.lastModified(), bak == null ? Mode.ADD : Mode.UPDATE, f.success());
	}
	
	public DeployFile backupRemove(AgentLoc src, String file, String bak) throws OdenException{
		DeployerService deployer = getDeployer(src.agentAddr());
		DoneFileInfo f= deployer.backupNRemove(src.location(), file, bak);
		return new DeployFile(
				new Repository(new String[0]), file, src, "",
				f.size(), f.lastModified(), Mode.DELETE, f.success());
	}
}
