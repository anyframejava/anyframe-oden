/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package anyframe.oden.eclipse.core.utils;

import java.util.Collection;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;

import anyframe.oden.eclipse.core.OdenActivator;
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

}
