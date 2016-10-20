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
package org.anyframe.oden.bundle.job.config;

import org.anyframe.oden.bundle.core.AgentLoc;
import org.anyframe.oden.bundle.core.Repository;

public class CfgUtil {
	public static String[] toRepoArg(CfgSource s){
		return new String[]{"file://" + s.getPath()};
	}
	
	public static Repository toRepository(CfgSource s){
		return new Repository(new String[]{"file://" + s.getPath()});
	}
	
	public static AgentLoc toAgentLoc(CfgTarget t){
		return new AgentLoc(t.getName(), t.getAddress(), t.getPath());
	}
}
