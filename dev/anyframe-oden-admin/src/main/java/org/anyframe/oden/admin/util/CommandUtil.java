package org.anyframe.oden.admin.util;

public class CommandUtil {

	private final static String SUFFIX_JSON = "-json";
	private final static String SPACE = " ";
	
	public static String getBasicCommand(String cmd, String opt){
		return cmd + SPACE + opt + SPACE + SUFFIX_JSON;
	}
	
	public static String getBasicCommand(String cmd, String opt, String param) {
		return cmd + SPACE + opt + SPACE + param + SPACE + SUFFIX_JSON;
	}
	
	public static String getCommandRemove(String cmd, String name){
		return cmd + SPACE + "del" + SPACE + name + SPACE + SUFFIX_JSON;
	}
	
	public static String getCommandDate(String cmd, String opt, String param){
		return cmd + SPACE + opt + SPACE + "-date" + SPACE + param + SPACE + SUFFIX_JSON;
	}
}
