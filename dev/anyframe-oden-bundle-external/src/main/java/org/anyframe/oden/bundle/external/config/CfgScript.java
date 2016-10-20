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

public class CfgScript implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String command;

	List<String> params;

	List<CfgTarget> targets;

	public CfgScript(String command, List<String> params,
			List<CfgTarget> targets) {
		this.command = command;
		this.params = params;
		this.targets = targets;
	}

	public String getCommand() {
		String tmpCommand = "";
		for (String param : params) {
			tmpCommand += " " + param;
		}
		return command + tmpCommand;
	}

	public List<CfgTarget> getTargets() {
		return targets;
	}

	@Override
	public int hashCode() {
		return Utils.hashCode(command, targets);
	}
}
