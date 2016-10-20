package org.anyframe.oden.bundle.job.config;

import org.json.JSONException;
import org.json.JSONObject;

import org.anyframe.oden.bundle.common.Utils;

public class CfgCommand {
	String name;
	String command;
	String path;
	
	public CfgCommand(String name, String command, String path) {
		this.name = name;
		this.command = command;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public String getCommand() {
		return command;
	}

	public String getPath() {
		return path;
	}

	public JSONObject toJSON() throws JSONException{
		JSONObject o = new JSONObject();
		o.put("name", name);
		o.put("command", command);
		o.put("dir", path);
		return o;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CfgCommand){
			CfgCommand ct = (CfgCommand)obj;
			if(equals(name, ct.getName()) && 
					equals(command, ct.getCommand()) && 
					equals(path, ct.getPath()))
				return true;
		}
		return false;
	}
	
	private boolean equals(String s0, String s1){
		return (s0 == null && s1 == null) ||
			(s0 != null && s0.equals(s1));
	}
	
	@Override
	public int hashCode() {
		return Utils.hashCode(name, command, path);
	}
}
