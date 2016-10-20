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
package anyframe.oden.bundle.core;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.command.AgentLoc;

/**
 * The kind of Oden Dispatcher. Oden commands are access the Oden Services
 * via this.
 * 
 * @author joon1k
 *
 */
public interface DelegateService {
	/**
	 * @deprecated
	 */
	public List getPackageList(String id, String pwd, String user) throws Exception;

	/**
	 * @deprecated
	 */
	public List getContents(String file, String id, String pwd, String user) throws Exception;


	/**
	 * Fetches files to deploy.
	 * 
	 * @param repoArgs arguments which are required to access repository.
	 * @param includes 
	 * @param excludes
	 * @param update
	 * @param agentAddr
	 * @param agentPath
	 * @return
	 * @throws OdenException
	 */
	public void preview(Map<List<String>, FileMap> repomap, 
			String[] repoArgs, List<String> includes, List<String> excludes,
			boolean update, List<AgentLoc> agents) throws OdenException;


	/** 
	 * @deprecated
	 */
	public void deploy(String policyName, String[] repoArgs, List<String> includes, List<String> excludes,
			boolean update, List<AgentLoc> agents, String user, PrintStream out) throws OdenException;
	
	/**
	 * Fetches files from repository(repoargs) and Deploy files to the apporopriate agent 
	 * which can be retrieved from files object. By calling preview function, you can get the 
	 * files object.
	 * 
	 * @param repoargs
	 * @param files
	 * @param update
	 * @param user
	 * @param out
	 * @throws OdenException
	 */
	public void deploy(String[] repoargs, FileMap files, 
			boolean update, String user, PrintStream out) throws OdenException;

	/**
	 * Get available repository protocols
	 * @return
	 * @throws Exception
	 */
	public List<String> getRepositoryProtocols();

	public void removeSnapshot(String agentUri, String agentPath, String snapshot, String user)
			throws OdenException;

	public FileInfo snapshot(String targetLoc, String agentUri, String agentPath,
			String user) throws OdenException;

	public void rollback(String agentUri, String agentPath, String snapshot,
			String dest, String user) throws OdenException;
	
	/**
	 * @deprecated
	 */
	public String showlog(String ip, String fromDate, String toDate,
			String filename, String status, String user) throws Exception;
	
	/**
	 * @deprecated
	 */
	public void updatefile(String filepath, String filename, String user) throws Exception;
	
	public List<FileInfo> getFilesFromRepo(String[] repoArgs) throws OdenException;
	
	public List<String> getRepositoryUsages();

	public boolean availableAgent(AgentLoc agent);
	
	public boolean availableRepository(String[] repoArgs);
}
