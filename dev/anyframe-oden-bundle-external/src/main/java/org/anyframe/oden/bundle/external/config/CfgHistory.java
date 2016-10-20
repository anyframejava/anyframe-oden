/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.anyframe.oden.bundle.external.config;

import java.io.Serializable;
import java.util.List;

import org.anyframe.oden.bundle.common.Utils;

/**
 * Domain class for history info.
 * 
 * @author Junghwan Hong
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
