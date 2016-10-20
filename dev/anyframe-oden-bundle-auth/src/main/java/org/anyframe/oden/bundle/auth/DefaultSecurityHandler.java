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
