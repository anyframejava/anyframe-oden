package org.anyframe.oden.admin.vo;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class Job implements Serializable {

	// -----------------------------------------//
	// Oden Server Job Json Mapping info
	private String name;
	private String group;
	private String build;

	private Source sources;
	private List<Target> targets;
	private List<Command> commands;

	// -----------------------------------------//
	// Grid additional info
	private History deployHistory;
	private Build buildHistory;
	private boolean enableBuildService;
	private boolean enableDeploy;
	private boolean enableCleanDeploy;
	private boolean enableCompare;
	private boolean enableRunScript;
	private boolean enableRollback;

	public Job() {
		enableBuildService = true;
		enableDeploy = true;
		enableCleanDeploy = true;
		enableCompare = true;
		enableRunScript = true;
		enableRollback = true;
		
		deployHistory = new History();
		buildHistory = new Build();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getBuild() {
		return build;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public Source getSources() {
		return sources;
	}

	public void setSources(Source sources) {
		this.sources = sources;
	}

	public List<Target> getTargets() {
		return targets;
	}

	public void setTargets(List<Target> targets) {
		this.targets = targets;
	}

	public List<Command> getCommands() {
		return commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}

	public boolean isEnableBuildService() {
		return enableBuildService;
	}

	public void setEnableBuildService(boolean enableBuildService) {
		this.enableBuildService = enableBuildService;
	}

	public boolean isEnableDeploy() {
		return enableDeploy;
	}

	public void setEnableDeploy(boolean enableDeploy) {
		this.enableDeploy = enableDeploy;
	}

	public boolean isEnableCleanDeploy() {
		return enableCleanDeploy;
	}

	public void setEnableCleanDeploy(boolean enableCleanDeploy) {
		this.enableCleanDeploy = enableCleanDeploy;
	}

	public boolean isEnableCompare() {
		return enableCompare;
	}

	public void setEnableCompare(boolean enableCompare) {
		this.enableCompare = enableCompare;
	}

	public boolean isEnableRunScript() {
		return enableRunScript;
	}

	public void setEnableRunScript(boolean enableRunScript) {
		this.enableRunScript = enableRunScript;
	}

	public boolean isEnableRollback() {
		return enableRollback;
	}

	public void setEnableRollback(boolean enableRollback) {
		this.enableRollback = enableRollback;
	}

	public History getDeployHistory() {
		return deployHistory;
	}

	public void setDeployHistory(History deployHistory) {
		this.deployHistory = deployHistory;
	}

	public Build getBuildHistory() {
		return buildHistory;
	}

	public void setBuildHistory(Build buildHistory) {
		this.buildHistory = buildHistory;
	}

}