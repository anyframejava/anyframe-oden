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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import anyframe.oden.bundle.common.ArraySet;
import anyframe.oden.bundle.core.DeployFile.Mode;

public class DeployFileUtil {
	
	/**
	 * add dfile to dfiles. if already exist, mix their mode.
	 * 
	 * @param dfiles
	 * @param dfile
	 */
	public static void updateDeployFiles(Set<DeployFile> dfiles, DeployFile dfile){
		for(DeployFile df : dfiles){
			if(dfile.equals(df)){		// already existed
				df.setMode(mergeMode(df.mode(), dfile.mode())); 
				return;
			}
		}
		dfiles.add(dfile);		// not existed
	}
	
//	public static Map<Repository, Map<AgentLoc, Set<DeployFile>>> toRepositoryFiles(Set<DeployFile> files){
//		Map<Repository, Map<AgentLoc, Set<DeployFile>>> result = 
//				new HashMap<Repository, Map<AgentLoc, Set<DeployFile>>>();
//		Map<Repository, Set<DeployFile>> rmap = groupByRepository(files);
//		for(Repository r : rmap.keySet()){
//			result.put(r, groupByAgent(rmap.get(r)));
//		}
//		return result;
//	}
	
	public static Map<Repository, Set<DeployFile>> groupByRepository(Set<DeployFile> dfiles){
		Map<Repository, Set<DeployFile>> rfiles = new HashMap<Repository, Set<DeployFile>>();
		for(DeployFile df : dfiles){
			if(df.mode() == Mode.NA){	// Mode.NA if target f is readonly
				df.setMode(Mode.UPDATE);
			}else {
				Set<DeployFile> dfs = rfiles.get(df.getRepo());
				if(dfs == null)
					dfs = new ArraySet<DeployFile>();
				dfs.add(df);
				rfiles.put(df.getRepo(), dfs);
		
			}
		}
		return rfiles;
	}
	
	public static Map<AgentLoc, Set<DeployFile>> groupByAgent(Set<DeployFile> files){
		Map<AgentLoc, Set<DeployFile>> afiles = new HashMap<AgentLoc, Set<DeployFile>>();
		for(DeployFile f : files){
			Set<DeployFile> fs = afiles.get(f.getAgent());
			if(fs == null)
				fs = new ArraySet<DeployFile>();
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
	
	public static Map<String, Set<DeployFile>> groupByPath(Set<DeployFile> files){
		Map<String, Set<DeployFile>> filemap = new HashMap<String, Set<DeployFile>>();
		for(DeployFile f : files){
			Set<DeployFile> fs = filemap.get(f.getPath());
			if(fs == null)
				fs = new ArraySet<DeployFile>();
			fs.add(f);
			filemap.put(f.getPath(), fs);
		}
		return filemap;
	}
	
	public static Set<AgentLoc> extractAgents(Set<DeployFile> files){
		Set<AgentLoc> result = new ArraySet<AgentLoc>();
		for(DeployFile f : files){
			result.add(f.getAgent());
		}
		return result;
	}

	
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
		if(s.equals("A"))
			return Mode.ADD;
		if(s.equals("D"))
			return Mode.DELETE;
		if(s.equals("U"))
			return Mode.UPDATE;
		return Mode.NA;
	}
	
	public static Mode mergeMode(Mode m1, Mode m2){
		if(m1 == Mode.ADD || m2 == Mode.ADD)
			return Mode.ADD;
		if(m1 == Mode.UPDATE || m2 == Mode.UPDATE)
			return Mode.UPDATE;
		return Mode.DELETE;
	}
}
