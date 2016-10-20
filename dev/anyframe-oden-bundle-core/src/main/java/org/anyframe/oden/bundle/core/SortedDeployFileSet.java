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
package org.anyframe.oden.bundle.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * This is SortedDeployFileSet class.
 * 
 * @author Junghwan Hong
 */
public class SortedDeployFileSet extends TreeSet<DeployFile> {
	private static final long serialVersionUID = 4055187636013787208L;

	public SortedDeployFileSet(Collection<DeployFile> s) {
		super(new DeployFileComparator());
		addAll(s);
	}
}

class DeployFileComparator implements Comparator<DeployFile>, Serializable {
	private static final long serialVersionUID = 7745117582884274026L;

	public int compare(DeployFile d1, DeployFile d2) {
		int ret = d1.getRepo().toString().compareTo(d2.getRepo().toString());
		if (ret != 0) {
			return ret;
		}

		ret = d1.getPath().compareTo(d2.getPath());
		if (ret != 0) {
			return ret;
		}

		return d1.getAgent().agentName().compareTo(d2.getAgent().agentName());
	}
}
