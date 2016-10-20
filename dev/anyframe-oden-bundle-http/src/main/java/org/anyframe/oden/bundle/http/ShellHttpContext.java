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

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

/**
 * To handle Security service, bind handler and service.s
 * 
 * @author Junghwan Hong
 */
public class ShellHttpContext implements HttpContext {

	private HttpContext base;

	private SecurityHandler securityHandler;

	public ShellHttpContext(HttpService httpService, SecurityHandler handler) {
		this.base = httpService.createDefaultHttpContext();
		this.securityHandler = handler;
	}

	public void setSecurityHandler(SecurityHandler handler) {
		this.securityHandler = handler;
	}

	public String getMimeType(String s) {
		return this.base.getMimeType(s);
	}

	public URL getResource(String s) {
		URL url = this.base.getResource(s);
		if (url == null && s.endsWith("/")) {
			return this.base.getResource(s.substring(0, s.length() - 1));
		}
		return url;
	}

	public boolean handleSecurity(HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		if (securityHandler == null) {
			return true;
		}

		try {
			return securityHandler.handle(req, res);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
