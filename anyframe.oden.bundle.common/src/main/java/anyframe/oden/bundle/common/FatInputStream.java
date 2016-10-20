/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package anyframe.oden.bundle.common;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * InputStream having some more its file information. 
 * 
 * @author joon1k
 *
 */
public class FatInputStream extends BufferedInputStream{

	private String path;
	
	private boolean isDir;
	
	private long lastModified;

	public FatInputStream(InputStream in, String path, boolean isDir, long date) {
		super(in);
		this.path = path;
		this.isDir = isDir;
		this.lastModified = date;
	}

	public String getPath() {
		return path;
	}

	public boolean isDir() {
		return isDir;
	}

	public long getLastModified() {
		return lastModified;
	}

	public FileInfo getFileInfo(){
		return new FileInfo(path, isDir, lastModified);
	}
	
	
}
