package org.anyframe.oden.admin.vo;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class HistoryWrapper implements Serializable{

	private String total;
	private List<History> data;

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public List<History> getData() {
		return data;
	}

	public void setData(List<History> data) {
		this.data = data;
	}

}
