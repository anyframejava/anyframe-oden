package org.anyframe.oden.admin.vo;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class HistoryDetailWrapper implements Serializable {

	// -----------------------------------------//
	// Oden Server Log Wrapper Json Mapping info
	private String total;
	private List<HistoryDetail> data;

	// -----------------------------------------//
	// additional info for dynamic header
	private List<String> agents;

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public List<HistoryDetail> getData() {
		return data;
	}

	public void setData(List<HistoryDetail> data) {
		this.data = data;
	}

	public List<String> getAgents() {
		return agents;
	}

	public void setAgents(List<String> agents) {
		this.agents = agents;
	}

}
