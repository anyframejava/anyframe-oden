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
package anyframe.oden.bundle.core.record;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import anyframe.oden.bundle.core.command.CommandUtil;

/**
 * This represents each deploy log. policy의 agent 하나당 RecordElement하나가 생김
 * 
 * @author joon1k
 *
 */
public class RecordElement2 implements Serializable{
	private String host = "";
	private String agent = "";
	private String rootpath = "";
	private List<String> paths = new ArrayList<String>();
	private long date;
	
	public RecordElement2(long date, String record) {
		String[] records = CommandUtil.split(record);
		this.date = date;
		this.host = records.length > 0 ? records[0] : "";
		this.agent = records.length > 1 ? records[1] : "";
		this.rootpath = records.length > 2 ? records[2] : "";
		if(records.length > 3){
			for(String path : records[3].split(",")){
				paths.add(path);
			}
		}
	}
	
	public RecordElement2(String host, String agent, String rootpath, List<String> paths, long date) {
		this.host = host;
		this.agent = agent;
		this.rootpath = rootpath;
		this.date = date;
		this.paths = paths;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getRootpath() {
		return rootpath;
	}

	public void setRootpath(String rootpath) {
		this.rootpath = rootpath;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}
	
	public List<String> getPaths() {
		return paths;
	}
	
	/**
	 * recordElement의 데이터 String으로 리턴.
	 * date항목 제외. 각 항목 ""로 묶임. host, agent, path순서.
	 * @return "host" "agent" "path"
	 */
	public String getRecordAsString(){
		return "\"" + host + "\" " +
			"\"" + agent + "\" " +
			"\"" + rootpath + "\" " +
			"\"" + serialize(paths) + "\"";
	}
	
	/**
	 * make one string by combining list elements
	 * @param agentRootPath
	 * @param paths
	 * @return
	 */
	private String serialize(List<String> paths) {
		StringBuffer buf = new StringBuffer();
		for(int i=0; i<paths.size(); i++){
			buf.append(paths.get(i));
			if(i+1 < paths.size())
				buf.append(',');
		}
		return buf.toString();
	}

}
