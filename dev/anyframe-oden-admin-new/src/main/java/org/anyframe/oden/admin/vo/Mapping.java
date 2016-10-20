package org.anyframe.oden.admin.vo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Mapping implements Serializable {

	String dir;
	String checkoutDir;

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getCheckoutDir() {
		return checkoutDir;
	}

	public void setCheckoutDir(String checkoutDir) {
		this.checkoutDir = checkoutDir;
	}

}
