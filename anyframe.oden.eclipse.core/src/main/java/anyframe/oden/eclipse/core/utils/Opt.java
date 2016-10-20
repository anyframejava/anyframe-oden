package anyframe.oden.eclipse.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
