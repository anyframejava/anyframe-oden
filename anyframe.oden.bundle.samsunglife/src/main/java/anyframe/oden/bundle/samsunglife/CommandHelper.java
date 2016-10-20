package anyframe.oden.bundle.samsunglife;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.ungoverned.osgi.service.shell.Command;

import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.command.Cmd;
import anyframe.oden.bundle.core.config.AgentElement;
import anyframe.oden.bundle.core.config.OdenConfigService;

public class CommandHelper {
	public static String toTaskPolicies(List<String> policies) {
		StringBuffer buf = new StringBuffer();
		for(String policy : policies){
			buf.append(policy + " ");
		}
		return buf.deleteCharAt(buf.length()-1).toString();
	}
	
	public static String toPolicyDest(OdenConfigService svc, SPFDeployFile df) throws OdenException {
		StringBuffer buf = new StringBuffer();
		for(SPFTargetGroup g : df.getTargetGroups()){
			buf.append(toPolicyDest(svc, g) + " ");
		}
		return buf.deleteCharAt(buf.length()-1).toString();
	}
	
	public static String toPolicyDest(OdenConfigService svc, SPFTargetGroup group) throws OdenException {
		StringBuffer buf = new StringBuffer();
		if(group.getTargets().size() == 0){		// ignore
			throw new OdenException("no agents are defined for " + group.getName());
		}else {
			for(SPFTarget t : group.getTargets()){
				buf.append(toPolicyDest(svc, t) + " ");	
			}
		}
		return buf.deleteCharAt(buf.length()-1).toString();
	}
	
	private static String toPolicyDest(OdenConfigService svc, SPFTarget t) throws OdenException{
		String loc = t.getPath();
		return toAgentName(svc, t.getHost()) + "/" + (loc.startsWith("/") ? loc.substring(1) : loc); 
	}
		
	public static String toAgentName(OdenConfigService svc, String host) throws OdenException {
		for(String name : svc.getAgentNames()){
			AgentElement agent = svc.getAgent(name);
			if(agent != null && agent.getHost().equals(host))
				return agent.getName();
		}
		throw new OdenException("Fail to get agent information: " + host);
	}
	
	public static List<AgentElement> allAgents(OdenConfigService svc) throws OdenException{
		List<AgentElement> list = new ArrayList<AgentElement>();
		for(String name : svc.getAgentNames()){
			AgentElement a = svc.getAgent(name);
			if(a == null)
				throw new OdenException("Fail to get agent information: " + name);
			list.add(a);
		}
		return list;
	}
	
	private static String toPolicyDest(AgentElement a){
		return a.getName(); 
	}
	
	public static String nextId(String id) {
		int sep = id.indexOf("$");
		if(sep != -1){
			try{
				String base = id.substring(0, sep);
				int next = Integer.valueOf(id.substring(sep+1)) + 1;
				return base + "$" + String.valueOf(next);
			}catch(Exception e){
			}
		}
		return id + "$" + "0";
	}

	public static String excuteCommand(Command cmd, String line) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		
		cmd.execute(line + " -json", new PrintStream(out), new PrintStream(err));
		if(err.size() > 0)	// error
			throw new Exception(err.toString());
		return out.toString();
	}
	
	public static String extractUserName(Cmd cmd) {
		String user = cmd.getOptionArg(new String[]{Cmd.USER_OPT});
		try{
			if(user.length() == 0)
				user = InetAddress.getLocalHost().getHostAddress();
		} catch(UnknownHostException e){
			user = "";
		}
		return user;
	}
}
