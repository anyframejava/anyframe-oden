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
package org.anyframe.oden.bundle.job.log;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;

public class BDBJobComparator implements Comparator<byte[]>, Serializable {

	private static final long serialVersionUID = -4530227468319107512L;

	public int compare(byte[] b1, byte[] b2) {
		try{
			return new String(b1, "utf-8").compareTo(
					new String(b2, "utf-8"));
		}catch(UnsupportedEncodingException e){
			return 0;	// never be occured.
		}
	}

}
