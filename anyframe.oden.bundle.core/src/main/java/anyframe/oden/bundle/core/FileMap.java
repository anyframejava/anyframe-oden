package anyframe.oden.bundle.core;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import anyframe.oden.bundle.core.command.AgentLoc;

/**
 * This class has file list to be deployed and save the agent information
 * to which files are deployed.
 * 
 * @author joon1k
 *
 */
public class FileMap extends TreeMap<String, List<AgentLoc>>{
	public void append(String file, AgentLoc agent){
		List<AgentLoc> agents = get(file);
		if(agents == null)
			put(file, agents = new ArrayList<AgentLoc>());
		if(!agents.contains(agent))
			agents.add(agent);
	}
	
}
