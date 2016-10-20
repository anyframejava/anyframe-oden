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

/**
 * Domain class for history info.
 * 
 * @author junghwan.hong
 * 
 */
public class CfgHistoryDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String source;

	private String mode;

	private String errorlog;

	private String path;

	private String targets;

	private String success;

	public CfgHistoryDetail(String source, String path , String mode, String errorlog,
			String targets, String success) {
		this.source = source;
		this.path = path;
		this.mode = mode;
		this.errorlog = errorlog;
		this.targets = targets;
		this.success = success;
	}

	public String getSource() {
		return source;
	}

	public String getMode() {
		return mode;
	}

	public String getErrorlog() {
		return errorlog;
	}

	public String getPath() {
		return path;
	}

	public String getTargets() {
		return targets;
	}

	public String getSuccess() {
		return success;
	}

}
