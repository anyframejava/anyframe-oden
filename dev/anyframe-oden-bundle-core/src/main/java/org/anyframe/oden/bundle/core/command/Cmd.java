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
package org.anyframe.oden.bundle.core.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.common.OdenParseException;

/**
 * Structured command. Oden command line can be converted to this structure.
 * <br/>rule) <cmd> [<action> [<action-arg>]] -<option> [<option-arg[=value]> ...] [-<option> ...]
 * <br/>ex) policy add mypolicy $local -i **\*.js **\*.jar -x **\aa*.jar -u $all -desc "..."<br/>
 *  
 * @author Junghwan Hong
 */
public class Cmd {
	public final static String RUN_ACTION = "run";
	public final static String REMOVE_ACTION = "del";
	public final static String HELP_ACTION = "help";
	public final static String ADD_ACTION = "add";
	public final static String INFO_ACTION = "info";
	public final static String SHOW_ACTION = "show";

	public static final String[] JSON_OPT = { "json" };
	public static final String USER_OPT = "_user";
	public static final String UNDO_ACTION = "undo";

	private String name = "";
	private String action = "";
	private String actionArg = "";
	private LinkedList<String> actionArgs = new LinkedList<String>();
	
	private List<Opt> options = new ArrayList<Opt>();

	public Cmd(String line) throws OdenException {
		parse(line);
	}

//	public Cmd(String cmdName, String args) throws OdenException {
//		parse("\"" + cmdName + "\" " + args);
//  }

//	private void parse(String line) throws OdenException {
//		String[] args = CommandUtil.split(line);
//		if(args.length == 0 || isOption(args[0]))
//			throw new OdenParseException(line);
//	
//		int idx = 0;
//		name = args[idx++];
//		if(idx < args.length && !isOption(args[idx])){
//			action = args[idx++];
//			if(idx < args.length && !isOption(args[idx])){
//				actionArg = args[idx++];
//			}
//		}
//	
//		if(idx < args.length){
//			if(!isOption(args[idx]))
//				throw new OdenParseException(line);
//		
//			// collect the others
//			args[idx] = args[idx].substring(1); 	// remove '-'
//			StringBuffer others = new StringBuffer(args[idx++] + " ");
//			for(int i=idx; i<args.length; i++){
//				if(args[i].startsWith("-"))
//					others.append(args[i] + " ");
//				else
//					others.append("\"" + args[i] + "\" ");
//			}
//			
//			// get options
//			String[] sOpts = others.toString().split(" -");
//			for(String sOpt : sOpts) {
//				options.add(new Opt(sOpt));
//			}
//		}
//	}
	@SuppressWarnings("PMD")
	private void parse(String line) throws OdenException {
		String[] args = CommandUtil.split(line);
		if (args.length == 0 || isOption(args[0])) {
			throw new OdenParseException(line);
		}

		int idx = 0;
		name = args[idx++];
		if (idx < args.length && !isOption(args[idx])) {
			action = args[idx++];
			if ("runs".equals(action)) {
				for (int i = idx; i < args.length; i++) {
					if (!isOption(args[i])) {
						actionArgs.add(args[i]);
						idx++;
					} else {
						break;
					}
				}
			} else {
				if (idx < args.length && !isOption(args[idx])) {
					actionArg = args[idx++];
				}
			}
		}

		if (idx < args.length) {
			if (!isOption(args[idx])) {
				throw new OdenParseException(line);
			}

			// collect the others
			StringBuffer ops = new StringBuffer();
			for (int i = idx; i < args.length; i++) {
				if (args[i].startsWith("-")) {
					if (ops.length() != 0) {
						options.add(new Opt(ops.toString()));
						ops.delete(0, ops.length());
					}
					ops.append(args[i].substring(1) + " ");
				} else {
					ops.append("\"" + args[i] + "\" ");
				}
			}
			if (ops.length() != 0) {
				options.add(new Opt(ops.toString()));
			}
		}
	}

	protected boolean isOption(String s) {
		return s.matches("^-.*| -.*");
	}

	/**
	 * get the command's name
	 * 
	 * @return
	 */

	public String getName() {
		return name;
	}

	/**
	 * get the command's action name
	 * 
	 * @return
	 */
	public String getAction() {
		return action;
	}

	/**
	 * get command's action's argument
	 * 
	 * @return
	 */
	public String getActionArg() {
		return actionArg;
	}
	
	/**
	 * get command's action's arguments
	 * 
	 * @return
	 */
	public LinkedList<String> getActionArgs() {
		return actionArgs;
	}
	
	/**
	 * get all option objects
	 * 
	 * @return
	 */
	public List<Opt> getOptions() {
		return options;
	}

	/**
	 * get the specified option object
	 * 
	 * @param name
	 * @return
	 */
	public Opt getOption(String name) {
		for (Opt op : options) {
			if (name.equals(op.getName())) {
				return op;
			}
		}
		return null;
	}

	/**
	 * get the specified option object
	 * 
	 * @param names
	 * @return
	 */
	public Opt getOption(String[] names) {
		for (Opt op : options) {
			for (String name : names) {
				if (name.equals(op.getName())) {
					return op;
				}
			}
		}
		return null;
	}

	/**
	 * Return specified option's argument. If not, return "".
	 * 
	 * @param names
	 *            option names
	 * @return
	 */
	public String getOptionArg(String[] names) {
		String[] args = getOptionArgArray(names);
		if (args.length > 0) {
			return args[0];
		}
		return "";
	}

	/**
	 * get the specified option's arguments
	 * 
	 * @param names
	 * @return
	 */
	public List<String> getOptionArgList(String[] names) {
		Opt op = getOption(names);
		if (op != null) {
			return op.getArgList();
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * get the specified option's arguments
	 * 
	 * @param names
	 * @return
	 */
	public String[] getOptionArgArray(String[] names) {
		Opt op = getOption(names);
		if (op != null) {
			return op.getArgArray();
		}
		return new String[0];
	}

	/**
	 * remove specified option from this command
	 * 
	 * @param names
	 */
	public void removeOption(String[] names) {
		for (Opt op : options) {
			for (String name : names) {
				if (op.getName().equals(name)) {
					options.remove(op);
				}
			}
		}

	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(name);
		buf.append(action.length() > 0 ? " " + action : "");
		buf.append(actionArg.length() > 0 ? " \"" + actionArg + "\"" : " \""
				+ "\"");
		for (Opt option : options) {
			buf.append(" ");
			buf.append(option.toString());
		}
		return buf.toString();
	}

	/**
	 * Cmd의 옵션들을 문자열로 리턴 함. Cmd의 name, action, actionArgs무시.
	 * 
	 * @return toString() except Cmd name
	 */
	public String getOptionString() {
		StringBuffer buf = new StringBuffer();
		for (Opt option : options) {
			buf.append(" ");
			buf.append(option.toString());
		}
		return buf.toString().trim();
	}
}
