package anyframe.oden.bundle.core;

import java.util.ArrayList;
import java.util.List;

import anyframe.oden.bundle.core.command.AgentLoc;

/**
 * This class store's file information which will be deployed.
 * 
 * @author joon1k
 *
 */
public class DeployFile {
	private String path;
	
	private List<AgentLoc> agents = new ArrayList<AgentLoc>();
	
	public String getPath() {
		return path;
	}

	public List<AgentLoc> getAgents() {
		return agents;
	}
	
	public void addAgent(AgentLoc agent){
		agents.add(agent);
	}
}
