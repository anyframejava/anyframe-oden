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

import java.util.Set;

import anyframe.oden.bundle.common.FatInputStream;
import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.AgentLoc;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.deploy.DeployerService;

/**
 * Oden Service to communicate with remote DeployerService. This sends 
 * DelegateService's request to DeployerService.
 * 
 * @author joon1k
 *
 */
public interface TransmitterService {
	
	public FileInfo backup(String ip, String srcLoc, String repoLoc) 
			throws OdenException;

	public void removeSnapshot(String repoUri, String repoLoc, String snapshot) 
			throws OdenException;

	public boolean removeDir(Set<DeployFile> fs, DeployFile toRemove) throws OdenException;
	
	public void restore(Set<DeployFile> fs, DeployFile toRestore) throws OdenException;

	public long getDate(String destUri, String destRoot, String path)
			throws OdenException;
	
	public boolean available(String addr);
	
	public boolean readyToDeploy(DeployerService deployer, DeployFile f);
	
	public DeployerService getDeployer(String addr);
	
	public boolean isNew(String destUri, String destRoot, FatInputStream srcin) throws OdenException;
	
	/**
	 * backup: dest to bak
	 * copy: src to dest
	 * 
	 * @param src
	 * @param file
	 * @param dest
	 * @param bak
	 * @return
	 * @throws OdenException
	 */
	public DeployFile backupCopy(AgentLoc src, String file, String dest, String bak) throws OdenException;
	
	/**
	 * backup: src to bak
	 * remove: src
	 * 
	 * @param src
	 * @param file
	 * @param bak
	 * @return
	 * @throws OdenException
	 */
	public DeployFile backupRemove(AgentLoc src, String file, String bak) throws OdenException;
	
	public boolean exist(String addr, String parent, String path);

	public boolean writable(String addr, String parent, String path);
}
