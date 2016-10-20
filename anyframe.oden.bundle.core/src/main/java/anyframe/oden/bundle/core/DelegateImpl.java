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
package anyframe.oden.bundle.core;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import anyframe.oden.bundle.common.FatInputStream;
import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.PairValue;
import anyframe.oden.bundle.core.command.AgentLoc;
import anyframe.oden.bundle.core.record.DeployLogService;
import anyframe.oden.bundle.core.record.RecordElement;
import anyframe.oden.bundle.core.txmitter.TransmitterService;
import anyframe.oden.bundle.repository.RepositoryService;

/**
 * @see anyframe.oden.bundle.core.DelegateService
 * 
 * @author joon1k
 *
 */
public class DelegateImpl implements DelegateService{
	private List<RepositoryService> repoServices = new Vector<RepositoryService>();

	private TransmitterService txmitterService;

	private DeployLogService deploylog;
	
	private BundleContext context;
	
	private Object txmitterLatch = new Object();
	
	public DelegateImpl() {
	}

	protected void activate(ComponentContext context){
		this.context = context.getBundleContext();
	}
	
	protected void addRepositoryService(RepositoryService rs){
		repoServices.add(rs);
	}
	
	protected void removeRepositoryService(RepositoryService rs){
		repoServices.remove(rs);
	}
	
	protected void setTransmitterService(TransmitterService tx){
		this.txmitterService = tx;
	}
	
	protected void unsetTransmitterService(TransmitterService tx){
		this.txmitterService = null;
	}
	
	protected void setDeployLogService(DeployLogService recordsvc) {
		this.deploylog = recordsvc;
	}
	
	protected void unsetDeployLogService(DeployLogService recordsvc) {
		this.deploylog = null;
	}
	
	public List getPackageList(String id, String pwd, String user)
			throws Exception{
		return null;
	}
	
	public List getContents(String file, String id, String pwd, String user)
			throws Exception{
		return null;
	}	

	/**
	 * @return Map<repoargs, Map<filepath, agentlocs>. repoargs is list of repo arg array.
	 * agentlocs is list of the AgentLoc.
	 */
	public void preview(Map<List<String>, FileMap> repomap, 
			String[] repoargs, List<String> includes, List<String> excludes,
			boolean update, List<AgentLoc> agents) throws OdenException{
		RepositoryService repo = getRepoServiceByURI(repoargs);
		if(repo == null)
			throw new OdenException("Couldn't find a RepositoryService for " + 
					Arrays.toString(repoargs));
		
		List<String> repolist = toList(repoargs);
		FileMap filemap = repomap.get(repolist);
		if(filemap == null)
			repomap.put(repolist, filemap = new FileMap());		
		List<String> files = repo.resolveFileRegex(repoargs, includes, excludes);
		try{
			for(String file : files){
				FatInputStream in = null;
				try{									
					in = repo.resolve(repoargs, file);				
					if(in == null)
						continue;
					
					for(AgentLoc agent : agents) {										
						if(!availableAgent(agent))
							throw new OdenException("Couldn't connect to the agent: " + 
									agent.agentName() + "(" + agent.agentAddr() + ")");
						if(update && !isNew(agent.agentAddr(), agent.location(), in))
							continue;					
						filemap.append(file, agent);
					}
				}finally{
					try { if(in != null) in.close(); } catch (IOException e) { }
				}
			}
		}finally {
			repo.close(repoargs);
		}
	}
	
	private List<String> toList(String[] a){
		List<String> l = new ArrayList<String>();
		for(String s : a)
			l.add(s);
		return l;
	}
	
//	public void deploy(String[] repoargs, FileMap files, 
//			boolean update, String user, PrintStream out) throws OdenException {
//		RepositoryService repo = getRepoServiceByURI(repoargs);
//		if(repo == null)
//			throw new OdenException("Couldn't find a RepositoryService for " + 
//					Arrays.toString(repoargs));
//		
//		List<RecordElement> records = new ArrayList<RecordElement>();		// for deploy log..
//		try{
//			for(String file : files.keySet()){
//				FatInputStream in = null;
//				try{					
//					in = repo.resolve(repoargs, file);
//					if(in == null)
//						continue;
//					
//					for(AgentLoc agent : files.get(file)){
//						// [deploy log] get record element for destUri
//						RecordElement record = null;
//						if(!in.isDir()){
//							record = getRecordElement(agent.agentAddr(), records);
//							if(record == null){
//								record = new RecordElement(user, agent.agentAddr(), agent.location(), 
//										new ArrayList<String>(), System.currentTimeMillis());
//								records.add(record);
//							}
//						}
//						
//						synchronized (txmitterLatch) {
//						List<String> updatedfiles = txmitterService.deploy(
//								agent.agentAddr(), agent.location(), in, update);
//						
//						// [deploy log]...
//						if(!in.isDir()){
//							if(updatedfiles != null && updatedfiles.size() > 0){
//								for(String updatedf : updatedfiles){
//									record.getPaths().add(in.getPath() + "/" + updatedf);
//									if(out != null) out.println("Updated: " + in.getPath() + "/" + updatedf);
//								}
//							} else{
//								record.getPaths().add(in.getPath());
//								if(out != null) out.println("Deployed: " + in.getPath());
//							}
//						}
//						}	// end of synchronized
//						
//					}
//				}finally{
//					try { if(in != null) in.close(); } catch (IOException e) { }
//				}
//			}
//
//		}catch(OdenException e) {
//			throw e;
//		}catch(Exception e){
//			throw new OdenException(e.getMessage(), e);
//		}finally {		
//			repo.close(repoargs);
//			for(RecordElement record : records)
//				deploylog.record(record);
//		}	
//	}
	
	private List<RecordElement> deploy(String[] repoargs, FileMap files, 
			boolean update, String user, long date, PrintStream out) throws OdenException {
		RepositoryService repo = getRepoServiceByURI(repoargs);
		if(repo == null)
			throw new OdenException("Couldn't find a RepositoryService for " + 
					Arrays.toString(repoargs));
		
		List<RecordElement> records = new ArrayList<RecordElement>();		// for deploy log..
		for(String file : files.keySet()){
			for(AgentLoc agent : files.get(file)){
				boolean success = true;
				FatInputStream in = null;
				try{
					in = repo.resolve(repoargs, file);
				}catch(Exception e){
					success = false;
					Logger.error(e);
				}
				
				List<String> updatedfiles = Collections.EMPTY_LIST;
				synchronized (txmitterLatch) {
					try{
						if(in == null)
							success = false;
						else
							updatedfiles = txmitterService.deploy(agent.agentAddr(), agent.location(), in, update);
					}catch(Exception e){
						success = false;
						Logger.error(e);
					}finally{
						if(in == null || !in.isDir())
							addDeployRecord(records, agent, user, date, file, updatedfiles, success, out);
					}
				}	
				try { if(in != null) in.close(); } catch (IOException e) { }
			}
		}	
		repo.close(repoargs);
		return records;
	}
	
	private void addDeployRecord(List<RecordElement> records, AgentLoc agent, String user, 
			long date, String path, List<String> updatedfiles, boolean success, PrintStream out){
		RecordElement record = null;
		record = getRecordElement(agent.agentAddr(), records);
		if(record == null){
			record = new RecordElement(user, agent.agentAddr(), agent.location(), 
					new ArrayList<PairValue<String, Boolean>>(), date, true);
			records.add(record);
		}
	
		if(updatedfiles != null && updatedfiles.size() > 0){
			for(String updatedf : updatedfiles){
				record.getPaths().add(new PairValue<String, Boolean>(path + "/" + updatedf, success));
				if(out != null) out.println("Updated: " + path + "/" + updatedf);
			}
		} else{
			record.getPaths().add(new PairValue<String, Boolean>(path, success));
			if(out != null) out.println("Deployed: " + path);
		}
		
		if(!success)
			record.setSucccess(false);
	}
	
	/**
	 * 
	 * @return transaction id
	 * @throws OdenException
	 */
	public String deployAll(Map<List<String>, FileMap> repomap, boolean update, 
			String user, PrintStream out) throws OdenException {
		boolean success = true;
		final long date = System.currentTimeMillis();

		List<RecordElement> records = new ArrayList<RecordElement>();
		try{
			for(List<String> repoargs : repomap.keySet()){
				List<RecordElement> l = deploy(repoargs.toArray(new String[repoargs.size()]), 
						repomap.get(repoargs), update, user, date, out);
				records.addAll(l);
			}
		} finally {
			deploylog.record(records);
		}
		if(!success)
			throw new OdenException("Fail to deploy. See log file.");
		return String.valueOf(date);			// This is the txid
	}
	
	/**
	 * policy 단위로 파일 배포. policy는 하나의 repo, 여러개의 agent올 수 있음.
	 * 
	 * @return 배포된 파일 목록들. agent하나당 목록 하나 
	 */
	public List<RecordElement> deploy(Policy policy, long date, PrintStream out) throws OdenException {
		return deploy(policy.getRepoargs(), policy.getFiles(), policy.isUpdate(), policy.getUser(), date, out);
	}
	
	public String deployAll(List<Policy> policies, PrintStream out) throws OdenException {
		boolean success = true;
		final long date = System.currentTimeMillis();
		List<RecordElement> records = new ArrayList<RecordElement>(); 
		try{
			for(Policy policy : policies){
				for(RecordElement record : deploy(policy, date, out)){
					records.add(record);
					if(!record.isSuccess()) success = false;
				}
			}
		} finally {
			deploylog.record(records);
		}
		if(!success)
			throw new OdenException("Fail to deploy. See log file.");
		return String.valueOf(date);			// This is the txid
	}
	
	private RecordElement getRecordElement(String agent, List<RecordElement> records){
		for(RecordElement record : records) {
			if(record.getAgent().equals(agent))
				return record;
		}
		return null;
	}
	
	private boolean isNew(String destUri, String destRoot, FatInputStream srcin) throws OdenException {
	synchronized (txmitterLatch) {
		if(txmitterService.getDate(destUri, destRoot, srcin.getPath()) >= srcin.getLastModified())
			return false;
		return true;
	}
	}
	
	protected RepositoryService getRepoServiceByURI(String[] repoArgs){
		for(RepositoryService repoService : repoServices){
			if(repoService.matchedURI(repoArgs))
				return repoService;
		}
		return null;
	}
	
	public boolean availableRepository(String[] repoArgs){
		return getRepoServiceByURI(repoArgs) != null;
	}
		
	public List<String> getRepositoryProtocols() {
		List<String> types = new ArrayList<String>();
		for(RepositoryService repoService : repoServices){
			types.add(repoService.getProtocol());
		}
		return types;
	}

	public FileInfo snapshot(String targetLoc, String agentUri, String agentPath,
			String user) throws OdenException {
	synchronized (txmitterLatch) {
		return txmitterService.backup(agentUri, targetLoc, agentPath);
	}
	}

	public void removeSnapshot(String agentUri, String agentPath, String snapshot, String user)
			throws OdenException {
	synchronized (txmitterLatch) {
		txmitterService.removeSnapshot(agentUri, agentPath, snapshot);
	}
	}

	public String rollback(String agentUri, String agentPath, String snapshot,
			String dest, String user) throws OdenException {
	synchronized (txmitterLatch) {
		List<PairValue<String, Boolean>> restoredfiles = txmitterService.restore(agentUri, agentPath, snapshot, dest);
		long date = System.currentTimeMillis();
		deploylog.record(user, agentUri, agentPath, restoredfiles, date, false);
		return String.valueOf(date);
	}
	}
	
	public String showlog(String ip, String fromDate, String toDate,
			String filename, String status, String user) throws Exception {
		return null;
	}

	public void updatefile(String filepath, String filename, String user) throws Exception {
	}

	public List<FileInfo> getFilesFromRepo(String[] repoargs) throws OdenException {
		RepositoryService repo = getRepoServiceByURI(repoargs);
		if(repo == null) {
			throw new OdenException("Couldn't find a RepositoryService for " + Arrays.toString(repoargs));
		}
		return repo.getFileList(repoargs);
	}

	public List<String> getRepositoryUsages() {
		List<String> usages = new ArrayList<String>();
		for(RepositoryService rsvc : repoServices){
			usages.add(rsvc.getUsage());
		}
		return usages;
	}

	public boolean availableAgent(AgentLoc agent){
		return txmitterService.available(agent.agentAddr());
	}
}
