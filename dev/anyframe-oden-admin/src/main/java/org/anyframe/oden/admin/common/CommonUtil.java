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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * CommandUtil : parsing response data getOptionArgs : option name remove ,
 * split : split line
 * 
 * @author Junghwan Hong
 * 
 */
public class CommonUtil {
	public static String getOptionArgs(String full, String optionName) {
		// remove '-' from option name
		String _opt = optionName.startsWith("-") ? optionName.substring(1)
				: optionName;

		String[] options = full.split("^-| -");
		for (String option : options) {
			if (option.startsWith(_opt)) {
				int idx = _opt.length();
				if (option.length() > idx + 1
						&& Character.isWhitespace(option.charAt(idx))
						&& !Character.isWhitespace(option.charAt(idx + 1))) {
					return option.substring(idx + 1);
				} else {
					return "";
				}
			}
		}
		return null;
	}

	/**
	 * space로 토큰을 나눠 배열로 리턴함 ""로 묶여진 문자열은 스페이스와 무관하게 하나의 토큰으로 인식
	 * 
	 * @param line
	 * @return
	 */
	public static String[] split(String line) {
		List<String> args = new ArrayList<String>();

		boolean quote = false;
		StringBuffer arg = new StringBuffer();
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == '"') {
				quote = !quote;
				continue;
			} else if (c == ' ') {
				if (!quote) {
					args.add(arg.toString());
					arg.delete(0, arg.length());
					continue;
				}
			}
			arg.append(c);
		}
		if (arg.length() > 0) {
			args.add(arg.toString());
		}

		return args.toArray(new String[args.size()]);
	}

	/**
	 * , 로 구분된 값을 입력 받아 List로 리턴함
	 * 
	 * @param arg
	 * @return
	 */
	public static List<String> getRoleList(String role) {
		List<String> roleList = new ArrayList<String>();
		String[] roles = role.split(",");
		for (String r : roles) {
			roleList.add(r);
		}
		return roleList;
	}

	/**
	 * 오늘 날짜를 yyyymmdd 형태로 리턴함
	 * 
	 * @param
	 * @return
	 */
	public static String getCurrentDate() {

		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale
				.getDefault());

		return format.format(date);
	}

}
