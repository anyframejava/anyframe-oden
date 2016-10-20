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
package org.anyframe.oden.admin.common;

import java.util.ArrayList;
import java.util.List;

import org.anyframe.oden.admin.util.CommonUtil;

/**
 * This is Opt Class
 * 
 * @author Junghwan Hong
 */
public class Opt {

	protected String name = "";

	protected List<String> args = new ArrayList<String>();

	/**
	 * Opt Default Constructor
	 */
	public Opt() {
		super();
	}

	/**
	 * 
	 * @param sOpt
	 *            option name and its args. option name hasn't '-'.
	 */
	public Opt(String sOpt) {
		String[] sArgs = CommonUtil.split(sOpt);
		name = sArgs[0];
		for (int i = 1; i < sArgs.length; i++) {
			args.add(sArgs[i]);
		}
	}
	
	public void makeOpt(String sOpt) {
		String[] sArgs = CommonUtil.split(sOpt);
		name = sArgs[0];
		for (int i = 1; i < sArgs.length; i++) {
			args.add(sArgs[i]);
		}
	}
	
	public String getName() {
		return name;
	}

	public List<String> getArgList() {
		List<String> list = new ArrayList<String>();
		for (String s : args) {
			list.add(s);
		}
		return list;
	}

	public String[] getArgArray() {
		return args.toArray(new String[args.size()]);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}

	public void clear() {
		name = "";
		args.clear();
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("-");
		buf.append(name);
		for (String val0 : args) {
			buf.append(" \"");
			buf.append(val0);
			buf.append("\"");
		}
		return buf.toString();
	}
}
