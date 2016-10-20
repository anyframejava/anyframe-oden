package org.anyframe.oden.admin.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtil {

	public static String toStringDate(Long input) {
		return new SimpleDateFormat("yyyy.MM.dd aa hh:mm:ss", Locale.getDefault()).format(input);
	}
	
	public static String toStringDateLikeDeploy(Long input) {
		return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(input);
	}

}
