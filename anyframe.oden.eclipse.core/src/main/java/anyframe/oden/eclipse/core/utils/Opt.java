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
import java.util.List;

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
