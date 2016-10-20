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
package anyframe.oden.eclipse.core;

/**
 * Generic exception class for Anyframe Oden Eclipse plug-in.
 * 
 * @author RHIE Jihwan
 * @version 1.0.0 RC1
 *
 */
@SuppressWarnings("serial")
public class OdenException extends Exception {
	
	/**
	 * @param
	 */
	public OdenException() {
		super();
	}
	
	/**
	 * @param string
	 * @param throwable
	 */
	public OdenException(String string, Throwable throwable) {
		super(string, throwable);
	}
	
	/**
	 * @param string
	 */
	public OdenException(String string) {
		super(string);
	}
	
	/**
	 * @param throwable
	 */
	public OdenException(Throwable throwable) {
		super(throwable);
	}

}
