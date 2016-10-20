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

/**
 * That you use this Exception means you put some meaningful
 * messages to this. That messages should have detailed information
 * and can be shown to customers directly. 
 * 
 * @author joon1k
 *
 */
public class OdenException extends Exception {
	private static final long serialVersionUID = 6622800048854269793L;

	public OdenException(String msg) {
		super(msg);
	}

	public OdenException(Throwable cause) {
		super(cause);
	}
	
	public OdenException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
