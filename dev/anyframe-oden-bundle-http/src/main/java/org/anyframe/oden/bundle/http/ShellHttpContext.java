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
package org.anyframe.oden.bundle.http;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

/**
 * 
 * To handle Security service, bind handler and service.s 
 * 
 * @author joon1k
 *
 */
public class ShellHttpContext implements HttpContext {

	private HttpContext base;
	
	private SecurityHandler securityHandler;
	
	public ShellHttpContext(HttpService httpService, SecurityHandler handler){
		this.base = httpService.createDefaultHttpContext();
		this.securityHandler = handler;
	}
	
	public void setSecurityHandler(SecurityHandler handler){
		this.securityHandler = handler;
	}
	
	public String getMimeType(String s) {
		return this.base.getMimeType(s);
	}

	public URL getResource(String s) {
		URL url = this.base.getResource(s);
        if (url == null && s.endsWith( "/" )) {
            return this.base.getResource(s.substring(0, s.length() - 1) );
        }
        return url;
	}

	public boolean handleSecurity(HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		if(securityHandler == null)
			return true;
		
		try{
			return securityHandler.handle(req, res);
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}

}
