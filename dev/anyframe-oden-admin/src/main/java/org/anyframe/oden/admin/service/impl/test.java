package org.anyframe.oden.admin.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class test {
	public final static String DATE_PATTERN = "yyyy.MM.dd HH:mm:ss";
	
	public static void main(String[] args) {
		
		Long dates = new Long((long) 1409011105957.0);
				String str = toStringDate(dates);
		System.out.println(str);
	}
	
	public static String toStringDate(long date) {
		return new SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
				.format(new Date(date));
	}

}
