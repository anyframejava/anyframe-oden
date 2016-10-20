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
package org.anyframe.oden.bundle.external.config;

import java.io.Serializable;

import org.anyframe.oden.bundle.common.Utils;

/**
 * This is CfgBuildReturnVO Class
 * 
 * @author Junghwan Hong
 */
@SuppressWarnings("PMD")
public class CfgBuildReturnVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String	buildName;
	
	String	buildNo;
	
	String	consoleUrl;
	
	public CfgBuildReturnVO(String buildName, String buildNo, String consoleUrl) {
		this.buildName = buildName;
		this.buildNo = buildNo;
		this.consoleUrl = consoleUrl;
	}

	public String getBuildName() {
		return buildName;
	}

	public String getBuildNo() {
		return buildNo;
	}

	public String getConsoleUrl() {
		return consoleUrl;
	}
	
	@Override
	public int hashCode() {
		return Utils.hashCode(buildName, buildNo, consoleUrl);
	}
}
