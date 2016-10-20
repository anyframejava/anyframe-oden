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
package org.anyframe.oden.bundle.http;

import javax.servlet.http.HttpServlet;

import org.anyframe.oden.bundle.common.Logger;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

public class SecuredServlet extends HttpServlet {
	String name;

	private HttpContext httpContext;

	private SecurityHandler securityHandler;

	protected void setHttpService(HttpService hs) {
		try {
			hs.registerServlet("/" + name, this, null,
					httpContext = new ShellHttpContext(hs, securityHandler));
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	protected void setSecurityHandler(SecurityHandler handler) {
		this.securityHandler = handler;
		if (httpContext != null) // handler is already binded.
			((ShellHttpContext) httpContext).setSecurityHandler(handler);
	}

	protected void unsetSecurityHandler(SecurityHandler handler) {
		setSecurityHandler(null);
	}

	public SecuredServlet(String name) {
		this.name = name;
	}
}
