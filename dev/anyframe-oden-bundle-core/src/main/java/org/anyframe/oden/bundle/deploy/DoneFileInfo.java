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
package org.anyframe.oden.bundle.deploy;

import org.anyframe.oden.bundle.common.FileInfo;

/**
 * File informaton about well-done job.
 * 
 * @author Junghwan Hong
 */
public class DoneFileInfo extends FileInfo {

	private static final long serialVersionUID = -6767136485115297651L;

	private boolean isUpdated = false;

	private boolean success = false;

	public DoneFileInfo(String path, boolean isDir, long lastModified, long size) {
		this(path, isDir, lastModified, size, false, false);
	}

	public DoneFileInfo(String path, boolean isDir, long lastModified,
			long size, boolean isUpdated, boolean success) {
		super(path, isDir, lastModified, size);
		this.isUpdated = isUpdated;
		this.success = success;
	}

	public void setSuccess(boolean s) {
		this.success = s;
	}

	public boolean success() {
		return this.success;
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdated = isUpdate;
	}

	public boolean isUpdate() {
		return this.isUpdated;
	}
}
