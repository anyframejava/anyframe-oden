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
package org.anyframe.oden.bundle.common;

import java.util.Collection;

/**
 * 
 * Check input string is empty.
 * 
 * @author joon1k
 *
 */
public class StringUtil {

	/**
	 * check if string is empty(null or empty string)
	 * @param s
	 * @return
	 */
	public static boolean empty(String s){
		return s == null || s.length() == 0;
	}
	
	public static String makeEmpty(String s){
		return s == null ? "" : s;
	}
	
	public static boolean equals(String s0, String s1){
		return (s0 == null && s1 == null) ||
			(s0 != null && s0.equals(s1));
	}
	
	public static String collectionToString(Collection c){
		StringBuffer buf = new StringBuffer();
		int i=0;
		for(Object o : c){
			buf.append(o.toString());
			if(i < c.size()-1)
				buf.append(", ");
			i++;
		}
		return buf.toString();
	}
}
