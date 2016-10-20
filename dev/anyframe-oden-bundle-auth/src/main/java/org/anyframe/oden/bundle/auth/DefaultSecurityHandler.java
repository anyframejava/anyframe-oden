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
package org.anyframe.oden.bundle.auth;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpContext;

import org.anyframe.oden.bundle.common.BundleUtil;
import org.anyframe.oden.bundle.http.SecurityHandler;

/**
 * Http security handler to control the oden access
 * 
 * @author joon1k
 *
 */
public class DefaultSecurityHandler implements SecurityHandler {
	private static final String HEADER_WWW_AUTHENTICATE = "WWW-Authenticate";
	
	private static final String HEADER_AUTHORIZATION = "Authorization";
	
	private static final String REALM = "Anyframe OSGi Shell";
	
	protected void activate(ComponentContext context){
	}
	
	public boolean handle(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String authHeader = req.getHeader( HEADER_AUTHORIZATION );
		if (authHeader != null && authHeader.length() > 0) {
			String[] auth = authHeader.split(" ");
			if(auth.length == 2 && auth[0].equalsIgnoreCase("Basic")){
				Properties accs = loadAccounts();
				for(Object user : accs.keySet()){
					if(accs.get(user).equals(auth[1])){
						// this is spec
						req.setAttribute(HttpContext.AUTHENTICATION_TYPE, "");
						req.setAttribute(HttpContext.REMOTE_USER, user);
						return true;
					}
				}
			}
		}

		// if no auth header..
		res.setHeader( HEADER_WWW_AUTHENTICATE, "Basic realm=\"" + REALM + "\"" );
		try {
			res.sendError( HttpServletResponse.SC_UNAUTHORIZED );
		}catch (IOException ioe) {
			res.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
		}
		return false;
	}

	private Properties loadAccounts() throws IOException {
		InputStream in = null;
		try{
			File accFile = new File(BundleUtil.odenHome(), ACCOUNT_FILE);
			in = new BufferedInputStream(new FileInputStream(accFile));
			Properties prop = new Properties();
			prop.load(in);
			return prop;
		}finally{
			if(in != null) in.close();
		}		
	}

}
