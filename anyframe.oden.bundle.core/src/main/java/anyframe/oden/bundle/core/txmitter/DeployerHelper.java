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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import anyframe.oden.bundle.common.ArraySet;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.AgentLoc;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.Repository;
import anyframe.oden.bundle.core.DeployFile.Mode;
import anyframe.oden.bundle.deploy.DeployerService;
import anyframe.oden.bundle.deploy.DoneFileInfo;

/**
 * Helper class to access the DeployerService
 * 
 * @author joon1k
 *
 */
public class DeployerHelper {
	public static void readyToDeploy(DeployerService ds, DeployFile f) throws Exception{
		try {		
			String parent = f.getAgent().location();
			String child = f.getPath();
			f.setMode(ds.exist(parent, child) ? Mode.UPDATE : Mode.ADD);
			try { ds.close(null, null); } catch (Exception ee){}
			ds.init(f.getAgent().location(), f.getPath(), f.getDate());
		}catch(Exception e){
			try { ds.close(null, null); } catch (Exception e1) {}
			throw e;
		} 
	}
	
	public static boolean isNewFile(DeployerService ds,
			long filedate, String parent, String path) throws Exception {
		return ds.getDate(parent, path) < filedate;
	}
	
	public static boolean removeDir(DeployerService ds, String txid, Set<DeployFile> fs, 
			AgentLoc loc, String bakLoc) throws OdenException {
		boolean success = true;
		List<DoneFileInfo> results = Collections.EMPTY_LIST;
		try {
			results = ds.backupNRemoveDir(txid, loc.location(), bakLoc);
		} catch (Exception e) {
			throw new OdenException(e);
		}
		for(DoneFileInfo f : results){
			success = success & f.success();
			fs.add(new DeployFile(
					new Repository(new String[0]), 
					f.getPath(), loc, f.size(), f.lastModified(), 
					DeployFile.Mode.DELETE, f.success()));
		}
		return success;
	}
	
	public static void restore(DeployerService ds, String txid, Set<DeployFile> fs, 
			DeployFile snapshot) throws OdenException{
		String[] repo = snapshot.getRepo().args();	// snapshot
		List<DoneFileInfo> results = Collections.EMPTY_LIST;
		try {
			results = ds.extract(
					txid,
					repo[1],		// snapshot root 
					snapshot.getPath(),		// snapshot path 
					snapshot.getAgent().location());
		} catch (Exception e) {
			throw new OdenException(e);
		}

		for(DoneFileInfo d : results){ 
			boolean contains = false;
			for(DeployFile r : fs){		// update mode
				if( r.getAgent().equals(snapshot.getAgent()) 
						&& r.getPath().equals(d.getPath())){
					r.setRepo(snapshot.getRepo());
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
						snapshot.getRepo(), 
						d.getPath(), 
						snapshot.getAgent(), 
						d.size(), 
						d.lastModified(), 
						DeployFile.Mode.ADD, 
						d.success()) );
			
		}
	}
}
