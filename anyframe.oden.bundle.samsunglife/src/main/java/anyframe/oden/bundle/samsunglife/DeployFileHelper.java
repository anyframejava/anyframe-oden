package anyframe.oden.bundle.samsunglife;

import java.util.ArrayList;
import java.util.List;

import anyframe.oden.bundle.common.FileUtil;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.AgentLoc;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.Repository;
import anyframe.oden.bundle.core.config.AgentElement;
import anyframe.oden.bundle.core.config.OdenConfigService;
/**
 * 
 * Helper class to handling deploy file.
 * 
 * @author joon1k
 *
 */
public class DeployFileHelper {
	public static List<DeployFile> toDeployFiles(SPFDeployFile df, OdenConfigService svc) throws OdenException{
		Repository repo = new Repository(new String[]{"file://" + df.getReqParentPath()});
		String path = FileUtil.combinePath(df.getReqFilePath(), df.getReqFileName());

		List<DeployFile> dfs = new ArrayList<DeployFile>();
		for(SPFTargetGroup tg : df.getTargetGroups()){
			for(SPFTarget t : tg.getTargets()){
				AgentElement agentEle = getAgent(svc, t.getHost());
				AgentLoc agent = new AgentLoc(
						agentEle.getName(), 
						agentEle.getAddr(), 
						t.getPath());
				dfs.add(new DeployFile(
						repo, 
						path, 
						agent, 
						0L, 0L, 
						new SpectrumComment(df.getApplyId(), df.getRscId(), tg.getName(), t.getHost(), tg.getOperGb()).toString(),
						anyframe.oden.bundle.core.DeployFileUtil.stringToMode(df.getReqGb()) ) );
			}
		}
		return dfs;
	}
	
	public static AgentElement getAgent(OdenConfigService svc, String host) throws OdenException {
		for(String name : svc.getAgentNames()){
			AgentElement agent = svc.getAgent(name);
			if(agent != null && agent.getHost().equals(host))
				return agent;
		}
		throw new OdenException("Fail to get agent information: " + host);
	}
	

}
