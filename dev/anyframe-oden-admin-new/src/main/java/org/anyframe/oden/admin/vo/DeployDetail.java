package org.anyframe.oden.admin.vo;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class DeployDetail implements Serializable {

	// -----------------------------------------//
	// Oden Server Deploy Json Mapping info
	String path;
	String mode;
	List<String> targets;

	// -----------------------------------------//
	// additional info
	String jobName;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public List<String> getTargets() {
		return targets;
	}

	public void setTargets(List<String> targets) {
		this.targets = targets;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

}
