package anyframe.oden.bundle.core.config;

/**
 * This represents agent's location in the config.xml
 * 
 * @author joon1k
 *
 */
public class AgentLocation {
	private AgentElement agent;
	
	private String name;
	
	private String value;

	public AgentLocation(AgentElement agent, String name, String value){
		this.agent = agent;
		this.name = name;
		this.value = value;
	}
	
	public void setAgent(AgentElement agent){
		this.agent = agent;
	}
	
	public AgentElement getAgent() {
		return agent;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
	public String getAgentName(){
		if(agent != null)
			return agent.getName();
		return null;
	}
	
	public String getAgentAddr(){
		if(agent != null)
			return agent.getAddr();
		return null;
	}
}
