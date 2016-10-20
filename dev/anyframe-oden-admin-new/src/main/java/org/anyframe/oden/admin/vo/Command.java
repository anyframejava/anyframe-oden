package org.anyframe.oden.admin.vo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Command implements Serializable {
	
	private String name;
	private String command;
	private String path;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
