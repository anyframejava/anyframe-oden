package org.anyframe.oden.bundle.external.config;

import org.anyframe.oden.bundle.core.AgentLoc;
import org.anyframe.oden.bundle.core.Repository;

public class CfgUtil {

	public static String[] toRepoArg(String s) {
		return new String[] { "file://" + s };
	}

	public static Repository toRepository(CfgFileInfo s) {
		return new Repository(new String[] { "file://" + s.getExeDir() });
	}

	public static AgentLoc toAgentLoc(CfgTarget t) {
		return new AgentLoc(t.getName(), t.getAddress() + ":9872", t.getPath());
	}
}
