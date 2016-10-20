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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import anyframe.oden.bundle.common.Utils;
import anyframe.oden.bundle.core.DeployFile.Mode;

/**
 * Utility class for DeployFile
 * 
 * @author joon1k
 *
 */
public class DeployFileUtil {
	
	/**
	 * add dfile to dfiles. if already exist, mix their mode.
	 * 
	 * @param dfiles
	 * @param dfile
	 */
	public static void updateDeployFiles(Set<DeployFile> dfiles, DeployFile dfile){
		dfiles.add(dfile);
	}
	
	/**
	 * group DeployFile by its repository property
	 */
	public static Map<Repository, Set<DeployFile>> groupByRepository(Set<DeployFile> dfiles){
		Map<Repository, Set<DeployFile>> rfiles = new HashMap<Repository, Set<DeployFile>>();
		for(DeployFile df : dfiles){
			Set<DeployFile> dfs = rfiles.get(df.getRepo());
			if(dfs == null)
				dfs = new HashSet<DeployFile>();
			dfs.add(df);
			rfiles.put(df.getRepo(), dfs);
		}
		return rfiles;
	}
	
	/**
	 * group DeployFile by its agent property
	 */
	public static Map<AgentLoc, Set<DeployFile>> groupByAgent(Set<DeployFile> files){
		Map<AgentLoc, Set<DeployFile>> afiles = new HashMap<AgentLoc, Set<DeployFile>>();
		for(DeployFile f : files){
			Set<DeployFile> fs = afiles.get(f.getAgent());
			if(fs == null)
				fs = new HashSet<DeployFile>();
			fs.add(f);
			afiles.put(f.getAgent(), fs);
		}
		return afiles;
	}
	
//	public static List<Set<DeployFile>> groupByPath(Set<DeployFile> files){
//		Map<String, Set<DeployFile>> filemap = new HashMap<String, Set<DeployFile>>();
//		for(DeployFile f : files){
//			Set<DeployFile> fs = filemap.get(f.getPath());
//			if(fs == null)
//				fs = new ArraySet<DeployFile>();
//			fs.add(f);
//			filemap.put(f.getPath(), fs);
//		}
//		
//		List<Set<DeployFile>> result = new ArrayList<Set<DeployFile>>();
//		for(String key : filemap.keySet()){
//			result.add(filemap.get(key));
//		}
//		return result;
//	}
	
	/**
	 * group DeployFile by its path property
	 */
	public static Map<String, Set<DeployFile>> groupByPath(Set<DeployFile> files){
		Map<String, Set<DeployFile>> filemap = new HashMap<String, Set<DeployFile>>();
		for(DeployFile f : files){
			Set<DeployFile> fs = filemap.get(f.getPath());
			if(fs == null)
				fs = new HashSet<DeployFile>();
			fs.add(f);
			filemap.put(f.getPath(), fs);
		}
		return filemap;
	}
	
	/**
	 * get the agent list which are referenced by set of DeployFiles
	 * 
	 * @param files
	 * @return
	 */
//	public static Set<AgentLoc> extractAgents(Set<DeployFile> files){
//		Set<AgentLoc> result = new ArraySet<AgentLoc>();
//		for(DeployFile f : files){
//			result.add(f.getAgent());
//		}
//		return result;
//	}

	/**
	 * convert DeployFile's Mode to String value
	 * 
	 * @param mode
	 * @return
	 */
	public static String modeToString(Mode mode){
		if(mode == Mode.ADD)
			return "A";
		if(mode == Mode.UPDATE)
			return "U";
		if(mode == Mode.DELETE)
			return "D";
		return "N";
	}
	
	public static DeployFile.Mode stringToMode(String s){
		if(s.equals("C") || s.equals("A"))
			return Mode.ADD;
		if(s.equals("D"))
			return Mode.DELETE;
		if(s.equals("U"))
			return Mode.UPDATE;
		return Mode.NA;
	}
	
	/**
	 * check the mode of DeployFiles and return higher order value.
	 * 
	 * @param m1
	 * @param m2
	 * @return
	 */
	public static Mode mergeMode(Mode m1, Mode m2){
		if(m1 == Mode.NA || m2 == Mode.NA)
			return Mode.NA;
		if(m1 == Mode.ADD || m2 == Mode.ADD)
			return Mode.ADD;
		if(m1 == Mode.UPDATE || m2 == Mode.UPDATE)
			return Mode.UPDATE;
		return Mode.DELETE;
	}

	/**
	 * extract DeployFiles which are already deployed but is not marked as success
	 * 
	 * @param dfs
	 * @return
	 */
	public static void filterToRedeploy(Set<DeployFile> dfs, 
			Set<DeployFile> ret) {
		Map<String, Set<DeployFile>> files = groupBySameSource(dfs);
		for(String key : files.keySet()){
			Set<DeployFile> fs = files.get(key);
			if(includeFail(fs)){
				for(DeployFile f : fs){
					f.setSuccess(false);	// init file to deploy
					f.setErrorLog("");
					ret.add(f);
				}
			}
		}
	}

	private static boolean includeFail(Set<DeployFile> fs) {
		for(DeployFile f : fs)
			if(!f.isSuccess())
				return true;
		return false;
	}

	/**
	 * group DeployFiles with having same repository & path
	 * 
	 * @param dfs
	 * @return
	 */
	public static Map<String, Set<DeployFile>> groupBySameSource(Set<DeployFile> dfs) {
		Map<String, Set<DeployFile>> result = new HashMap<String, Set<DeployFile>>();
		for(DeployFile df : dfs){
			String key = df.getRepo() + ":" + df.getPath();	// make unique key
			Set<DeployFile> fs = result.get(key);
			if(fs == null)
				fs = new HashSet<DeployFile>();
			fs.add(df);
			result.put(key, fs);
		}
		return result;
	}
	
	/**
	 * create DeployFile object which will be removed.
	 * 
	 * @param agent
	 * @param path
	 * @param sz
	 * @param date
	 * @return
	 */
	public static DeployFile beRemovedFile(AgentLoc agent, String path, long sz, long date){
		return new DeployFile(
				new Repository(new String[0]),
				path, agent, sz, date, Mode.DELETE
		);
	}
	
	/**
	 * create DeployFile which are not removed.
	 * 
	 * @param agent
	 * @param path
	 * @param e null available
	 * @return
	 */
	public static DeployFile notBeRemovedFile(AgentLoc agent, String path, Exception e){
		DeployFile f = new DeployFile(
				new Repository(new String[0]),
				path, agent, 0L, 0L, Mode.NA
		);
		if(e != null)
			f.setErrorLog(Utils.rootCause(e));
		return f;
	}
	
	/**
	 * create DeployFile which are not deployed.
	 * 
	 * @param repo
	 * @param path
	 * @param agent
	 * @param e
	 * @return
	 */
	public static DeployFile notBeDeployedFile(Repository repo, 
			String path, AgentLoc agent, Exception e){
		DeployFile f = new DeployFile(repo, path, agent, 0L, 0L, Mode.NA);
		if(e != null)
			f.setErrorLog(Utils.rootCause(e));
		return f;
	}
}
