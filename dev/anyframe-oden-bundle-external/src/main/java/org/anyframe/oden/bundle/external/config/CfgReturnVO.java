package org.anyframe.oden.bundle.external.config;

import java.io.Serializable;

import org.anyframe.oden.bundle.common.Utils;

public class CfgReturnVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String ciId;

	String odenId;

	Boolean success;

	String count;

	public CfgReturnVO(String ciId, String odenId, Boolean success, String count) {
		this.ciId = ciId;
		this.odenId = odenId;
		this.success = success;
		this.count = count;
	}

	public String getCiId() {
		return ciId;
	}

	public String getOdenId() {
		return odenId;
	}

	public Boolean isSuccess() {
		return success;
	}

	public String getCount() {
		return count;
	}

	@Override
	public int hashCode() {
		return Utils.hashCode(ciId, odenId, success, count);
	}
}
