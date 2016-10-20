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

import org.apache.felix.shell.Command;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * This is ShutdownCommandImpl class.
 * 
 * @author Junghwan Hong
 */
public class ShutdownCommandImpl implements Command {
	private BundleContext m_context = null;

	public ShutdownCommandImpl(BundleContext context) {
		m_context = context;
	}

	public String getName() {
		return "shutdown";
	}

	public String getUsage() {
		return "shutdown";
	}

	public String getShortDescription() {
		return "shutdown framework.";
	}

	public void execute(String s, PrintStream out, PrintStream err) {
		// Get system bundle and use it to shutdown Felix.
		try {
			Bundle bundle = m_context.getBundle(0);
			bundle.stop();
		} catch (Exception ex) {
			err.println(ex.toString());
		}
	}
}