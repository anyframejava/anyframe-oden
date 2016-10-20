/*
 * Copyright 2009, 2010 SAMSUNG SDS Co., Ltd. All rights reserved.
 *
 * No part of this "source code" may be reproduced, stored in a retrieval
 * system, or transmitted, in any form or by any means, mechanical,
 * electronic, photocopying, recording, or otherwise, without prior written
 * permission of SAMSUNG SDS Co., Ltd., with the following exceptions:
 * Any person is hereby authorized to store "source code" on a single
 * computer for personal use only and to print copies of "source code"
 * for personal use provided that the "source code" contains SAMSUNG SDS's
 * copyright notice.
 *
 * No licenses, express or implied, are granted with respect to any of
 * the technology described in this "source code". SAMSUNG SDS retains all
 * intellectual property rights associated with the technology described
 * in this "source code".
 *
 */
package anyframe.oden.admin.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * CommandUtil : parsing response data
 * getOptionArgs : option name remove , split : split line
 * 
 * @author HONG JungHwan
 * @version 1.0.0 RC2
 * 
 */
public class CommonUtil {
	public static String getOptionArgs(String full, String optionName){
		// remove '-' from option name
		String _opt = optionName.startsWith("-") ? optionName.substring(1) : optionName;

		String[] options = full.split("^-| -");
		for(String option: options){
			if(option.startsWith(_opt)){
				int idx = _opt.length();
				if(option.length() > idx + 1 &&
						Character.isWhitespace(option.charAt(idx)) &&
						!Character.isWhitespace(option.charAt(idx+1)) )
					return option.substring(idx + 1);
				else
					return "";
			}
		}
		return null;
	}
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
	
	/**
	 * , 로 구분된 값을 입력 받아 List로 리턴함
	 * @param arg
	 * @return
	 */
	public static ArrayList<String> getRoleList(String role) {
		ArrayList<String> roleList = new ArrayList<String>();
		String[] roles = role.split(",");
		for(String r : roles) 
			roleList.add(r);
		return roleList;
	}
	
	/**
	 * 오늘 날짜를 yyyymmdd 형태로 리턴함
	 * @param 
	 * @return 
	 */
	public static String getCurrentDate() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		
		return format.format(date);
	}

}
