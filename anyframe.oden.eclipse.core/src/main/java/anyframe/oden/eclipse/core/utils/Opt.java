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
package anyframe.oden.eclipse.core.utils;

import java.util.ArrayList;
import java.util.List;

public class Opt {

	protected String name = "";
	protected List<String> args = new ArrayList<String>();

	/**
	 * 
	 * @param sOpt option name and its args. option name hasn't '-'.
	 */
	public Opt(String sOpt) {
		String[] sArgs = CommandUtil.split(sOpt);
		name = sArgs[0];
		for(int i=1; i < sArgs.length; i++)
			args.add(sArgs[i]);
	}

	public String getName() {
		return name;
	}

	public List<String> getArgList() {
		List<String> list = new ArrayList<String>();
		for(String s : args)
			list.add(s);
		return list;
	}

	public String[] getArgArray() { 
		return args.toArray(new String[args.size()]);
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("-"+name);
		for(String val0 : args){
			buf.append(" \"" + val0 + "\"");
		}
		return buf.toString();
	}
}
