/*
 * Copyright 2002-2014 the original author or authors.
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
package org.anyframe.oden.bundle.build.config;

import java.io.Serializable;

public class CfgPmdDetail implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String fileName;
	
	private int lineNumber;
	
	private String message;

	public CfgPmdDetail() {}

	public CfgPmdDetail(String fileName, int lineNumber, String message) {
		this.fileName = fileName;
		this.lineNumber = lineNumber;
		this.message = message;
	}
	
	public String getFileName() {
		return fileName;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getMessage() {
		return message;
	}

	
	
	
}
