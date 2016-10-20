/*
 * Copyright 2002-2014 the original author or authors.
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
package org.anyframe.oden.bundle.build.config;

import java.io.Serializable;
import java.util.List;

public class CfgPmdReturnVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int highCnt;

	private int normalCnt;

	private int lowCnt;

	List<CfgPmdDetail> highDetail;

	List<CfgPmdDetail> normalDetail;

	List<CfgPmdDetail> lowDetail;
	
	public CfgPmdReturnVO(int highCnt, int normalCnt, int lowCnt,
			List<CfgPmdDetail> highDetail, List<CfgPmdDetail> normalDetail,
			List<CfgPmdDetail> lowDetail) {
		this.highCnt = highCnt;
		this.normalCnt = normalCnt;
		this.lowCnt = lowCnt;
		this.highDetail = highDetail;
		this.normalDetail = normalDetail;
		this.lowDetail = lowDetail;
	}

	public int getHighCnt() {
		return highCnt;
	}

	public int getNormalCnt() {
		return normalCnt;
	}

	public int getLowCnt() {
		return lowCnt;
	}

	public List<CfgPmdDetail> getHighDetail() {
		return highDetail;
	}

	public List<CfgPmdDetail> getNormalDetail() {
		return normalDetail;
	}

	public List<CfgPmdDetail> getLowDetail() {
		return lowDetail;
	}

}
