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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.alias.Server;

/**
 * Common Util, replace String Pattern info value etc.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 RC2
 *
 */
public class CommonUtil {
	private String SHELL_URL ="";

	public String getSHELL_URL() {
		return SHELL_URL;
	}

	public void setSHELL_URL(String sHELLURL) {
		SHELL_URL = sHELLURL;
	}
	
	public void setSHELL_URL(final Combo serverCombo) {
		SHELL_URL = "http://" 
			+ OdenActivator.getDefault().getAliasManager()
			.getServerManager().getServer(
					serverCombo.getText()).getUrl()
					+ "/shell"; 
	}
	
	public CommonUtil() {}

	/**
	 * @return replace source(pattern) with replace
	 */
	public static String replaceIgnoreCase(String source, String pattern,
			String replace) {
		int sIndex = 0;
		int eIndex = 0;
		String sourceTemp = null;
		StringBuffer result = new StringBuffer();
		sourceTemp = source.toUpperCase();
		while ((eIndex = sourceTemp.indexOf(pattern.toUpperCase(), sIndex)) >= 0) {
			result.append(source.substring(sIndex, eIndex));
			result.append(replace);
			sIndex = eIndex + pattern.length();
		}
		result.append(source.substring(sIndex));
		return result.toString();
	}

	/**
	 * Oden Server Combobox event processing
	 */
	public void serverComboEvent(final Combo serverCombo) {
		serverCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				SHELL_URL = "http://" 
					+ OdenActivator.getDefault().getAliasManager()
					.getServerManager().getServer(
							serverCombo.getText()).getUrl()
							+ "/shell"; 
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				SHELL_URL = "http://" 
					+ OdenActivator.getDefault().getAliasManager()
					.getServerManager().getServer(
							serverCombo.getText()).getUrl()
							+ "/shell"; 
			}
		});
	}
	/**
	 * Oden Server Combobox Initializing
	 */
	public void initServerCombo(final Combo serverCombo) {
		Collection<Server> col = OdenActivator.getDefault().getAliasManager()
		.getServerManager().getServers();
		for (Server combo : col){
			serverCombo.add(combo.getNickname());
		}
		serverCombo.select(0);

		if(col.size() > 0){
			SHELL_URL = "http://" 
				+ OdenActivator.getDefault().getAliasManager()
				.getServerManager().getServer(
						serverCombo.getText()).getUrl()
						+ "/shell"; 
		} 
	}
	
	/*
	 * Oden Explorer view TreeObject TreeObject filename & Date return 
	 */
	public String[] getTreeObjectSplitElement ( String treeobject ) {
		String[] returnArr = new String[2];
		Pattern pattern = Pattern.compile("\\p{Punct}\\d{4}.\\d{1,2}.\\d{1,2} \\d{2}:\\d{2}:\\d{2}\\p{Punct}");
		Matcher matcher = pattern.matcher(treeobject);
		
		
		if(matcher.find()) {
			returnArr[0] = matcher.group();
			returnArr[1] = matcher.replaceAll("");
			
			return  returnArr;
		} else	
			return returnArr;
		
	}
	/**
	 * return today's date  
	 */
	public String getDateFormat(String argFormat) throws OdenException {
		if (argFormat == null || argFormat.length() == 0)
			throw new OdenException();
	
		String returnStr = null;
		DateFormat df = new SimpleDateFormat(argFormat);
		Calendar cal = Calendar.getInstance();
		returnStr = df.format(cal.getTime());
	
		return returnStr;
	}
	
	/**
	 * return the date 1week ago  
	 */
	public String getWeekDateFormat(String argFormat) {
		DateFormat df = new SimpleDateFormat(argFormat);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		
		return df.format(cal.getTime());
	}
	
	/**
	 * return the date 1week ago  
	 */
	public Date getWeekDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		
		return cal.getTime();
	}
	
	/**
	 * return  millisecond value
	 */
	public long getMilliseconds() {
		return Calendar.getInstance().getTimeInMillis();
	}
}
