package org.anyframe.oden.bundle.job.config;

import org.json.JSONException;
import org.json.JSONObject;

import org.anyframe.oden.bundle.common.Utils;

public class CfgTarget {
	String name;
	String address;
	String path;
	
	public CfgTarget(String name, String address, String path) {
		this.name = name;
		this.address = address;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getPath() {
		return path;
	}

	public JSONObject toJSON() throws JSONException{
		JSONObject o = new JSONObject();
		o.put("name", name);
		o.put("address", address);
		o.put("dir", path);
		return o;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CfgTarget){
			CfgTarget ct = (CfgTarget)obj;
			if(equals(name, ct.getName()) && 
					equals(address, ct.getAddress()) && 
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
		return Utils.hashCode(name, address, path);
	}
}
