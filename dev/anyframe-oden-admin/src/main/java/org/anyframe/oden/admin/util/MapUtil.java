package org.anyframe.oden.admin.util;

import java.util.Iterator;
import java.util.Map;

public class MapUtil {

	public static String getKeyFromMapByValue(Map<String, String> map, String value) {
		Iterator<String> itr = map.keySet().iterator();
		String result = "";
		while (itr.hasNext()) {
			result = itr.next();
			if (map.get(result).equalsIgnoreCase(value)) {
				break;
			} else {
				result = "";
			}
		}

		return result;
	}
}
