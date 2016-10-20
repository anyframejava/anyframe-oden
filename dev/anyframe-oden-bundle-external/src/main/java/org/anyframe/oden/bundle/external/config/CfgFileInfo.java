package org.anyframe.oden.bundle.external.config;

import java.io.Serializable;
import java.util.List;

import org.anyframe.oden.bundle.common.Utils;

public class CfgFileInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String ciId;

	String exeDir;

	List<CfgTarget> targets;

	public CfgFileInfo(String ciId, String exeDir, List<CfgTarget> targets) {
		this.ciId = ciId;
		this.exeDir = exeDir;
		this.targets = targets;

	}

	public String getCiId() {
		return ciId;
	}

	public String getExeDir() {
		return exeDir;
	}

	public List<CfgTarget> getTargets() {
		return targets;
	}

	@Override
	public int hashCode() {
		return Utils.hashCode(ciId, exeDir, targets);
	}
}
