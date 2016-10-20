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

import org.anyframe.oden.bundle.common.Utils;

public class CfgTarget {
	String name;
	String address;
	String path;

	public CfgTarget(String name, String address, String path) {
		this.name = name;
		this.address = address;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getPath() {
		return path;
	}

	@Override
	public int hashCode() {
		return Utils.hashCode(name, address, path);
	}
}
