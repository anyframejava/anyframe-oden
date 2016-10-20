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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Collections of utility methods manipulating java.util.Date class.
 * 
 * @author joon1k
 *
 */
public class DateUtil {
	public final static String DATE_PATTERN = "yyyy.MM.dd HH:mm:ss";
	
	/**
	 * convert long date to String date with the format yyyy.MM.dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String toStringDate(long date) {
		return new SimpleDateFormat(DATE_PATTERN).format(new Date(date));
	}
	
	/**
	 * convert the string which is formated with yyy.MM.dd HH:mm:ss to long date.
	 * 
	 * @param s
	 * @return
	 * @throws ParseException
	 */
	public static long toLongDate(String s) throws ParseException {
		return new SimpleDateFormat(DATE_PATTERN).parse(s).getTime();
	}
	
}