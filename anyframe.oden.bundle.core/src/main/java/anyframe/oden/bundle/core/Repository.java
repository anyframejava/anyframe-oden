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
package anyframe.oden.bundle.core;

import java.io.Serializable;
import java.util.Arrays;

import org.json.JSONArray;

import anyframe.oden.bundle.common.JSONizable;
/**
 * 
 * This class contain Repository's whole information.
 * 
 * @author joon1k
 *
 */
public class Repository implements JSONizable, Serializable{
	private static final long serialVersionUID = 4973729525028146045L;

	private String[] args = new String[0];
	
	public Repository(String[] args){
		this.args = args;
	}
	
	public Repository(AgentLoc agent){
		this(agent.agentAddr(), agent.location());
	}
	
	public Repository(String addr, String loc){
		this(new String[]{addr, loc});
	}
	
	public String[] args(){
		return args;
	}
	
	public void setArgs(String[] args){
		this.args = args;
	}
	
	@Override
	public String toString(){
		return Arrays.toString(args);
	}
	
	@Override
	public boolean equals(Object o) {
		return toString().equals(o.toString());
	}

	public Object jsonize() {
		return new JSONArray(Arrays.asList(args));
	}
}
