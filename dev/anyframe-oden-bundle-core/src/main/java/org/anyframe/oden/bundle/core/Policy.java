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
package org.anyframe.oden.bundle.core;
/**
 * 
 * This class contain policy's whole information.
 * 
 * @author joon1k
 *
 */
public class Policy {
	private String[] repoargs;
	
	private FileMap files;
	
	private boolean update;
	
	private String user;

	public Policy(String[] repoargs, FileMap files, boolean update, String user){
		this.repoargs = repoargs;
		this.files = files;
		this.update = update;
		this.user = user;
	}
	
	public String[] getRepoargs() {
		return repoargs;
	}

	public void setRepoargs(String[] repoargs) {
		this.repoargs = repoargs;
	}

	public FileMap getFiles() {
		return files;
	}

	public void setFiles(FileMap files) {
		this.files = files;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
}
