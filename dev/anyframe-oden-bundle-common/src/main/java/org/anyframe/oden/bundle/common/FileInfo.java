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
package org.anyframe.oden.bundle.common;

import java.io.Serializable;

/**
 * Representing File. This is similar to java.io.File. But the path in this
 * class is not real. That path can be relative and can be other protocol's
 * path. If you want to save information like file which is not the real file,
 * you can use this. If you use java.io.File directly, some people may try to
 * access its contents. But because the file is not indicating the real
 * location, that try makes wrong result.
 * 
 * @author Junghwan Hong
 */
public class FileInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String path;

	private boolean isDir;

	private long lastModified;

	private long size;

	private String exception;

	public boolean success;

	private boolean update;

	private String date;

	public FileInfo(String path, boolean isDir, long lastModified) {
		this(FileUtil.normalize(path), isDir, lastModified, 0);
	}

	public FileInfo(String path, boolean isDir, long lastModified, long size) {
		this.path = FileUtil.normalize(path);
		this.isDir = isDir;
		this.lastModified = lastModified;
		this.size = size;
	}

	public FileInfo(String path, boolean isDir, String date, long size) {
		this.path = FileUtil.normalize(path);
		this.isDir = isDir;
		this.date = date;
		this.size = size;
	}

	public FileInfo(String path, boolean isDir, long lastModified, long size,
			String exception, boolean success, boolean update) {
		this.path = FileUtil.normalize(path);
		this.isDir = isDir;
		this.lastModified = lastModified;
		this.size = size;
		this.exception = exception;
		this.success = success;
		this.update = update;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = FileUtil.normalize(path);
	}

	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

	public long lastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public long size() {
		return this.size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getException() {
		return exception;
	}

	public boolean isSuccess() {
		return success;
	}

	public boolean isUpdate() {
		return update;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof FileInfo)) {
			return false;
		}
		FileInfo d = (FileInfo) o;
		return path.equals(d.path);
	}

	@Override
	public int hashCode() {
		return Utils.hashCode(path);
	}
}
