package org.anyframe.oden.bundle.external.config;

import java.util.List;

import org.anyframe.oden.bundle.common.Utils;

public class CfgJob {

	String id;
	String userId;
	List<CfgFileInfo> fileInfo;
	Boolean sync;

	public CfgJob(String id, String userId, List<CfgFileInfo> fileInfo,
			Boolean sync) {
		this.id = id;
		this.userId = userId;
		this.fileInfo = fileInfo;
		this.sync = sync;
	}

	public String getId() {
		return id;
	}

	public String getUserId() {
		return userId;
	}

	public List<CfgFileInfo> getFileInfo() {
		return fileInfo;
	}

	public Boolean isSync() {
		return sync;
	}

	@Override
	public int hashCode() {
		return Utils.hashCode(id, userId, fileInfo);
	}
}
