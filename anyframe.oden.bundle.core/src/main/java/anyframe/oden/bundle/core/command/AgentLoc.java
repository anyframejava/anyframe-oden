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
package anyframe.oden.bundle.core.command;

import java.io.FileNotFoundException;

import anyframe.oden.bundle.common.JSONizable;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.config.AgentElement;
import anyframe.oden.bundle.core.config.AgentLocation;
import anyframe.oden.bundle.core.config.OdenConfigService;

/**
 * This represents agent's location.
 * 
 * @author joon1k
 *
 */
public class AgentLoc implements JSONizable{
	private final static char SEPARATOR = '/';
	
	private String agentName;
	
	private String agentAddr;
	
	private String location;
	
	public AgentLoc(){
	}
	
	public AgentLoc(String args, OdenConfigService configsvc) throws OdenException {
		agentName = agentName(args);
		
		AgentElement agentinfo = null;
		try {
			agentinfo = configsvc.getAgent(agentName);
		} catch (FileNotFoundException e) {
			throw new OdenException(e);
		}
		if(agentinfo == null)
			throw new OdenException("Couldn't find a agent: " + agentName);
		
		agentAddr = agentinfo.getAddr();
		
		String locname = agentLocationName(args);
		AgentLocation loc = agentinfo.getLoc(locname);
		if(loc == null)
			throw new OdenException("Couldn't find a location variable: " + locname);
		location = loc.getValue();
	}

	private String agentName(String args) {
		int idx = args.lastIndexOf(SEPARATOR);
		if(idx == -1)
			return args;
		return args.substring(0, idx);
	}
	
	private String agentLocationName(String args){
		int idx = args.lastIndexOf(SEPARATOR);
		if(idx != -1 && args.length() > idx + 1)
			return args.substring(idx+1);
		return null;
	}

	public String agentName() {
		return agentName;
	}

	public String agentAddr() {
		return agentAddr;
	}

	public String location() {
		return location;
	}

	public Object jsonize() {
		return agentName();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AgentLoc){
			AgentLoc ra = (AgentLoc)obj;
			if(equals(agentAddr, ra.agentAddr()) && 
					equals(agentName, ra.agentName()) && 
					equals(location, ra.location()))
				return true;
		}
		return false;
	}
	
	private boolean equals(String s0, String s1){
		return (s0 == null && s1 == null) ||
			(s0 != null && s0.equals(s1));
	}
	
	
}

