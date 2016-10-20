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

import java.util.List;

import org.anyframe.oden.bundle.common.Utils;

/**
 * This is CfgJob Class
 * 
 * @author Junghwan Hong
 */
public class CfgJob {

	String id;
	String userId;
	List<CfgFileInfo> fileInfo;
	boolean sync;
	/**
	 * compress deploy option
	 */
	boolean compress;

	public CfgJob(String id, String userId, List<CfgFileInfo> fileInfo,
			boolean sync) {
		this.id = id;
		this.userId = userId;
		this.fileInfo = fileInfo;
		this.sync = sync;
	}

	public CfgJob(String id, String userId, List<CfgFileInfo> fileInfo,
			boolean sync, boolean compress) {
		this.id = id;
		this.userId = userId;
		this.fileInfo = fileInfo;
		this.sync = sync;
		this.compress = compress;
	}

	public String getId() {
		return id;
	}

	public String getUserId() {
		return userId;
	}

	public List<CfgFileInfo> getFileInfo() {
		return fileInfo;
	}

	public Boolean isSync() {
		return sync;
	}

	public boolean isCompress() {
		return compress;

	}

	public String getRepoLocation() {
		return fileInfo.size() == 0 ? "" : ((CfgFileInfo) fileInfo.get(0))
				.getExeDir();
	}

	@Override
	public int hashCode() {
		return Utils.hashCode(id, userId, fileInfo);
	}
}
