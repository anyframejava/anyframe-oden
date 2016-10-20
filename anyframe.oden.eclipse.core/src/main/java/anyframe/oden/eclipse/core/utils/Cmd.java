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
package anyframe.oden.eclipse.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * rule) <name> [<action> [<action-arg>]] -<option> [<option-arg[=value]> ...] [-<option> ...]
 * ex) policy add mypolicy $local -i **\*.js **\*.jar -x **\aa*.jar -u $all -desc "..."<br/>
 * @author joon1k
 *
 */
public class Cmd {
	public final static String RUN_ACTION = "run";
	public final static String REMOVE_ACTION = "del";
	public final static String HELP_ACTION = "help";
	public final static String ADD_ACTION = "add";
	public final static String INFO_ACTION = "info";
	public final static String SHOW_ACTION = "show";
	public static final String[] JSON_OPT = {"json"};
	public static final String USER_OPT = "_user";

	private String name = "";
	private String action = "";
	private String actionArg = "";
	private List<Opt> options = new ArrayList<Opt>();

	public Cmd(String line)  {
		parse(line);
	}

	public Cmd(String cmdName, String args)  {
		parse(cmdName + " " + args);
	}

	private void parse(String line) {
		String[] args = CommandUtil.split(line);
		if(args.length == 0 || isOption(args[0]))
			System.out.println("Syntax Error command");

		int idx = 0;
		name = args[idx++];
		if(idx < args.length && !isOption(args[idx])){
			action = args[idx++];
			if(idx < args.length && !isOption(args[idx])){
				actionArg = args[idx++];
			}
		}

		if(idx < args.length){
			if(!isOption(args[idx]))
				System.out.println("Syntax Error command");

			// collect the others
			StringBuffer ops = new StringBuffer();
			for(int i=idx; i<args.length; i++){
				if(args[i].startsWith("-")){
					if(ops.length() != 0){
						options.add(new Opt(ops.toString()));
						ops.delete(0, ops.length());
					}
					ops.append(args[i].substring(1) + " ");
				} else
					ops.append("\"" + args[i] + "\" ");
			}
			if(ops.length() != 0)
				options.add(new Opt(ops.toString()));
		}
	}

	protected boolean isOption(String s) {
		return s.matches("^-.*| -.*");
	}

	public String getName() {
		return name;
	}
	public String getAction() {
		return action;
	}
	public String getActionArg() {
		return actionArg;
	}
	public List<Opt> getOptions() {
		return options;
	}

	public Opt getOption(String name){
		for(Opt op : options){
			if(name.equals(op.getName()))
				return op;
		}
		return null;
	}

	public Opt getOption(String[] names){
		for(Opt op : options){
			for(String name : names)
				if(name.equals(op.getName()))
					return op;
		}
		return null;
	}

	/**
	 * Return specified option's argument. If not, return "".
	 * 
	 * @param names option names
	 * @return
	 */
	public String getOptionArg(String[] names) {
		String[] args = getOptionArgArray(names);
		if(args.length > 0)
			return args[0];
		return "";
	}

	public List<String> getOptionArgList(String[] names) {
		Opt op = getOption(names);
		if(op != null) {
			return op.getArgList();
		}
		return Collections.EMPTY_LIST;
	}

	public String[] getOptionArgArray(String[] names) {
		Opt op = getOption(names);
		if(op != null) {
			return op.getArgArray();
		}
		return new String[0];
	}

	public void removeOption(String[] names) {
		for(Opt op : options){
			for(String name : names){
				if(op.getName().equals(name)){
					options.remove(op);
				}
			}
		}

	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(name);
		buf.append(action.length() > 0 ? " " + action : "");
		buf.append(actionArg.length() > 0 ? " \"" + actionArg + "\"" : " \"" + "\"");
		for(Opt option : options)
			buf.append(" " + option.toString());
		return buf.toString();
	}

	/**
	 * Cmd의 옵션들을 문자열로 리턴 함.
	 * Cmd의 name, action, actionArgs무시.
	 * @return toString() except Cmd name
	 */
	public String getOptionString() {
		StringBuffer buf = new StringBuffer();
		for(Opt option : options)
			buf.append(" " + option.toString());
		return buf.toString().trim();
	}

}


