/*
 * Copyright 2010 SAMSUNG SDS Co., Ltd.
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
package anyframe.oden.ant;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;

/**
 * 
 * @author LEE Sujeong
 *
 */
public class OdenAntUtil {

	public static ArrayList parseArg(String arg){
		ArrayList result = new ArrayList();
		
		StringTokenizer token = new StringTokenizer(arg, ",");
		while(token.hasMoreTokens()){
			result.add(token.nextToken().trim());
		}
		
		return result;
	}
	
	public static boolean isNull(String arg) {
		if (arg == null || arg.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	public static void buildFailMsg(String msg, Exception e) {
		if (e == null) {
			throw new BuildException(msg);
		} else {
			throw new BuildException(msg + " [" + e.getMessage() + "]");
		}
	}

}
