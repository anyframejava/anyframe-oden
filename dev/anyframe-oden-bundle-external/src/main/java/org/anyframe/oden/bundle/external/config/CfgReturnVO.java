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
import java.util.Map;

import org.anyframe.oden.bundle.common.Utils;

/**
 * This is CfgReturnVO Class
 * 
 * @author Junghwan Hong
 */
public class CfgReturnVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String ciId;

	String odenId;

	Boolean success;

	String count;

	String sCount;

	Map<String, List<CfgReturnErr>> errLog;

	public CfgReturnVO(String ciId, String odenId, Boolean success,
			String count, String sCount, Map<String, List<CfgReturnErr>> errLog) {
		this.ciId = ciId;
		this.odenId = odenId;
		this.success = success;
		this.count = count;
		this.sCount = sCount;
		this.errLog = errLog;
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

	public Map<String, List<CfgReturnErr>> getErrLog() {
		return errLog;
	}

	public String getsCount() {
		return sCount;
	}

	@Override
	public int hashCode() {
		return Utils.hashCode(ciId, odenId, success, count, sCount, errLog);
	}
}
