package org.anyframe.oden.admin.vo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Target implements Serializable {

	private String name;
	private String address;
	private String path;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
