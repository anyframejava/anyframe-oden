/*
 * Copyright 2010 SAMSUNG SDS Co., Ltd.
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
package anyframe.oden.ant;

/**
 * 
 * @author LEE Sujeong
 *
 */
public class Agent {

	String name = "";
	String path = "";
	String locvar = "";
	String fileimport = "";
	String groups = "";

	public Agent(){}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getLocvar() {
		return locvar;
	}

	public void setLocvar(String locvar) {
		this.locvar = locvar;
	}

	public String getImport() {
		return fileimport;
	}

	public void setImport(String fileimport) {
		this.fileimport = fileimport;
	}

	public String getGroups() {
		return groups;
	}

	public void setGroups(String groups) {
		this.groups = groups;
	}

}
