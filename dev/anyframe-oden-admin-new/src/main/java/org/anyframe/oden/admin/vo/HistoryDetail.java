package org.anyframe.oden.admin.vo;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class HistoryDetail implements Serializable {

	// -----------------------------------------//
	// Oden Server Log Json Mapping info
	private String path;
	private String success;
	private List<String> targets; // agent name list
	private String errorlog;
	private String mode;

	// -----------------------------------------//
	// additional info
	private String txId;
	private List<CompareTarget> targetList;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public List<String> getTargets() {
		return targets;
	}

	public void setTargets(List<String> targets) {
		this.targets = targets;
	}

	public String getErrorlog() {
		return errorlog;
	}

	public void setErrorlog(String errorlog) {
		this.errorlog = errorlog;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public List<CompareTarget> getTargetList() {
		return targetList;
	}

	public void setTargetList(List<CompareTarget> targetList) {
		this.targetList = targetList;
	}

}
