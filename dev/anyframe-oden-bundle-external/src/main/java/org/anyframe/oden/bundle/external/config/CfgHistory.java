/*
 * Copyright 2010 SAMSUNG SDS Co., Ltd. All rights reserved.
 *
 * No part of this "source code" may be reproduced, stored in a retrieval
 * system, or transmitted, in any form or by any means, mechanical,
 * electronic, photocopying, recording, or otherwise, without prior written
 * permission of SAMSUNG SDS Co., Ltd., with the following exceptions:
 * Any person is hereby authorized to store "source code" on a single
 * computer for personal use only and to print copies of "source code"
 * for personal use provided that the "source code" contains SAMSUNG SDS's
 * copyright notice.
 *
 * No licenses, express or implied, are granted with respect to any of
 * the technology described in this "source code". SAMSUNG SDS retains all
 * intellectual property rights associated with the technology described
 * in this "source code".
 *
 */
package org.anyframe.oden.bundle.external.config;

import java.io.Serializable;
import java.util.List;

import org.anyframe.oden.bundle.common.Utils;

/**
 * Domain class for history info.
 * 
 * @author junghwan.hong
 * 
 */
public class CfgHistory implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String txid;

	private String user;

	private String job;

	private String total;

	private String date;

	private String success;

	private List<CfgHistoryDetail> data;

	public CfgHistory(String txid, String user, String job, String total,
			List<CfgHistoryDetail> data, String date, String success) {
		this.txid = txid;
		this.user = user;
		this.total = total;
		this.data = data;
		this.date = date;
		this.success = success;
	}

	public String getTxid() {
		return txid;
	}

	public String getUser() {
		return user;
	}

	public String getJob() {
		return job;
	}

	public List<CfgHistoryDetail> getData() {
		return data;
	}

	public String getTotal() {
		return total;
	}

	public String getDate() {
		return date;
	}

	public String getSuccess() {
		return success;
	}

	@Override
	public int hashCode() {
		return Utils.hashCode(txid, user, job, total, data);
	}
}
