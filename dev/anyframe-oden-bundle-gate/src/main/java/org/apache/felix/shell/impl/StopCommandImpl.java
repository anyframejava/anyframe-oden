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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.felix.shell.Command;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * This is StopCommandImpl class.
 * 
 * @author Junghwan Hong
 */
public class StopCommandImpl implements Command {
	private static final String TRANSIENT_SWITCH = "-t";

	private BundleContext m_context = null;

	public StopCommandImpl(BundleContext context) {
		m_context = context;
	}

	public String getName() {
		return "stop";
	}

	public String getUsage() {
		return "stop [-t] <id> [<id> ...]";
	}

	public String getShortDescription() {
		return "stop bundle(s).";
	}

	@SuppressWarnings("PMD")
	public void execute(String s, PrintStream out, PrintStream err) {
		StringTokenizer st = new StringTokenizer(s, " ");

		// Ignore the command name.
		st.nextToken();

		// Put the remaining tokens into a list.
		List tokens = new ArrayList();
		for (int i = 0; st.hasMoreTokens(); i++) {
			tokens.add(st.nextToken());
		}

		// Default switch value.
		boolean isTransient = false;

		// Check for "transient" switch.
		if (tokens.contains(TRANSIENT_SWITCH)) {
			// Remove the switch and set boolean flag.
			tokens.remove(TRANSIENT_SWITCH);
			isTransient = true;
		}

		// There should be at least one bundle id.
		if (tokens.size() >= 1) {
			while (tokens.size() > 0) {
				String id = ((String) tokens.remove(0)).trim();

				try {
					long l = Long.parseLong(id);
					Bundle bundle = m_context.getBundle(l);
					if (bundle != null) {
						bundle.stop(isTransient ? Bundle.STOP_TRANSIENT : 0);
					} else {
						err.println("Bundle ID " + id + " is invalid.");
					}
				} catch (NumberFormatException ex) {
					err.println("Unable to parse id '" + id + "'.");
				} catch (BundleException ex) {
					if (ex.getNestedException() != null) {
						err.println(ex.getNestedException().toString());
					} else {
						err.println(ex.toString());
					}
				} catch (Exception ex) {
					err.println(ex.toString());
				}
			}
		} else {
			err.println("Incorrect number of arguments");
		}
	}
}