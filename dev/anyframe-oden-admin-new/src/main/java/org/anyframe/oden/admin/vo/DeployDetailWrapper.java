package org.anyframe.oden.admin.vo;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class DeployDetailWrapper implements Serializable {

	// -----------------------------------------//
	// Oden Server Deploy Wrapper Json Mapping info
	private String total;
	private List<DeployDetail> data;

	// -----------------------------------------//
	// additional info

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public List<DeployDetail> getData() {
		return data;
	}

	public void setData(List<DeployDetail> data) {
		this.data = data;
	}

}
