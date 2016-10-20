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

import java.util.List;

import anyframe.oden.bundle.common.FatInputStream;
import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.OdenException;

/**
 * Oden Service to communicate with remote DeployerService. This sends 
 * DelegateService's request to DeployerService.
 * 
 * @author joon1k
 *
 */
public interface TransmitterService {
	/**
	 * 
	 * @param ip
	 * @param loc
	 * @param in
	 * @param updatejar if this is true and loc is jar, jar will be updated.
	 * @return
	 * @throws Exception
	 */
	public List<String> deploy(String ip, String loc, FatInputStream in, boolean update)
			throws OdenException;
	
	public FileInfo backup(String ip, String srcLoc, String repoLoc) 
			throws OdenException;

	public void removeSnapshot(String repoUri, String repoLoc, String snapshot) 
			throws OdenException;

	public List<String> restore(String repoUri, String repoLoc, String snapshot,
			String dest) throws OdenException;

	public long getDate(String destUri, String destRoot, String path)
			throws OdenException;
	
	public boolean available(String addr);
}
