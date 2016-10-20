package anyframe.oden.eclipse.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class CommandUtil {
	public static String getOptionArgs(String full, String optionName){
		// remove '-' from option name
		String _opt = optionName.startsWith("-") ? 
				optionName.substring(1) : optionName;
				
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
	
}
	