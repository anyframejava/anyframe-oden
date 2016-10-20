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
package org.anyframe.oden.bundle.deploy;

import java.io.Serializable;

import org.anyframe.oden.bundle.common.Utils;

public class CfgReturnScript implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String stdOut;

	String runPath;

	int exitCode;

	public String getStdOut() {
		return stdOut;
	}

	public String getRunPath() {
		return runPath;
	}

	public int getExitCode() {
		return exitCode;
	}

	public CfgReturnScript(String stdOut, String runPath, int exitCode) {
		this.stdOut = stdOut;
		this.runPath = runPath;
		this.exitCode = exitCode;
	}

	@Override
	public int hashCode() {
		return Utils.hashCode(stdOut, runPath, exitCode);
	}
	
//	append string result
	public String getString() {
		return null;
	}
}
