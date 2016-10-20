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
package anyframe.oden.bundle.core.command;

import java.util.ArrayList;
import java.util.List;


/**
 * Collections of utility methods helping traversing Oden commands.
 * 
 * @author joon1k
 *
 */
public class CommandUtil {
	
	/**
	 * space로 토큰을 나눠 배열로 리턴함
	 * ""로 묶여진 문자열은 스페이스와 무관하게 하나의 토큰으로 인식
	 * @param line
	 * @return
	 */
	public static String[] split(String line) {
		List<String> args = new ArrayList<String>();
		
		boolean quote = false;
		StringBuffer arg = new StringBuffer();
		for(int i=0; i<line.length(); i++){
			char c = line.charAt(i);
			if(c == '"'){
				quote = !quote;
				continue;
			}else if(c == ' '){
				if(!quote){ 
					args.add(arg.toString());
					arg.delete(0, arg.length());
					continue;
				}
			}
			arg.append(c);
		}
		if(arg.length() > 0)
			args.add(arg.toString());
		
		return args.toArray(new String[args.size()] );
	}
}
