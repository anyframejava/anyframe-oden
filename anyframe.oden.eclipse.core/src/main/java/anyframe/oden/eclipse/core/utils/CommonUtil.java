/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package anyframe.oden.eclipse.core.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Common Util, replace String Pattern info value etc.
 * 
 * @author 
 * @author HONG JungHwan
 * @version 1.0.0 RC2
 *
 */
public class CommonUtil {
	private static List<AgentInfo> agentInfos;

	public static String replaceIgnoreCase(String source, String pattern,
			String replace) {
		int sIndex = 0;
		int eIndex = 0;
		String sourceTemp = null;
		StringBuffer result = new StringBuffer();
		sourceTemp = source.toUpperCase();
		while ((eIndex = sourceTemp.indexOf(pattern.toUpperCase(), sIndex)) >= 0) {
			result.append(source.substring(sIndex, eIndex));
			result.append(replace);
			sIndex = eIndex + pattern.length();
		}
		result.append(source.substring(sIndex));
		return result.toString();
	}

	public static List<AgentInfo> getAgentInfos() {
		return agentInit();
	}
	private static List<AgentInfo> agentInit(){
		AgentInfo ex = null; 
		
		agentInfos = new ArrayList<AgentInfo>();
		// TODO : Preference Reading....
		ex = new AgentInfo("Local Test Server","70.7.53.119","9860");
		agentInfos.add(ex);
		ex = new AgentInfo("Development Server","10.46.147.212","9860");
		agentInfos.add(ex);
		ex = new AgentInfo("Staging Server","10.46.147.20","9860");
		agentInfos.add(ex);
		return agentInfos;
	}
	
}
