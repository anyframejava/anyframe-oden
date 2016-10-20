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
package org.apache.felix.shell.impl;

import java.io.PrintStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.StringTokenizer;

import org.apache.felix.shell.Command;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * This is HeadersCommandImpl class.
 * 
 * @author Junghwan Hong
 */
public class HeadersCommandImpl implements Command {
	private BundleContext m_context = null;

	public HeadersCommandImpl(BundleContext context) {
		m_context = context;
	}

	public String getName() {
		return "headers";
	}

	public String getUsage() {
		return "headers [<id> ...]";
	}

	public String getShortDescription() {
		return "display bundle header properties.";
	}

	public void execute(String s, PrintStream out, PrintStream err) {
		StringTokenizer st = new StringTokenizer(s, " ");

		// Ignore the command name.
		st.nextToken();

		// Print the specified bundles or all if none are specified.
		if (st.hasMoreTokens()) {
			while (st.hasMoreTokens()) {
				String id = st.nextToken().trim();

				try {
					long l = Long.parseLong(id);
					Bundle bundle = m_context.getBundle(l);
					if (bundle != null) {
						printHeaders(out, bundle);
					} else {
						err.println("Bundle ID " + id + " is invalid.");
					}
				} catch (NumberFormatException ex) {
					err.println("Unable to parse id '" + id + "'.");
				} catch (Exception ex) {
					err.println(ex.toString());
				}
			}
		} else {
			Bundle[] bundles = m_context.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				printHeaders(out, bundles[i]);
			}
		}
	}

	private void printHeaders(PrintStream out, Bundle bundle) {
		String title = Util.getBundleName(bundle);
		out.println("\n" + title);
		out.println(Util.getUnderlineString(title));
		Dictionary dict = bundle.getHeaders();
		Enumeration keys = dict.keys();
		while (keys.hasMoreElements()) {
			Object k = (String) keys.nextElement();
			Object v = dict.get(k);
			out.println(k + " = " + Util.getValueString(v));
		}
	}
}