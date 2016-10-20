package anyframe.oden.bundle.job.config;

import anyframe.oden.bundle.core.AgentLoc;
import anyframe.oden.bundle.core.Repository;

public class CfgUtil {
	public static String[] toRepoArg(CfgSource s){
		return new String[]{"file://" + s.getPath()};
	}
	
	public static Repository toRepository(CfgSource s){
		return new Repository(new String[]{"file://" + s.getPath()});
	}
	
	public static AgentLoc toAgentLoc(CfgTarget t){
		return new AgentLoc(t.getName(), t.getAddress(), t.getPath());
	}
}
