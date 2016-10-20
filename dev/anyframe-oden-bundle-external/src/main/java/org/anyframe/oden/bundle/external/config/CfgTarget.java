package org.anyframe.oden.bundle.external.config;

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

	@Override
	public int hashCode() {
		return Utils.hashCode(name, address, path);
	}
}
