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
package org.anyframe.oden.bundle.external;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.osgi.service.http.HttpService;

import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.external.deploy.ExtDeployerService;

import com.caucho.hessian.server.HessianServlet;

/**
 * Servlet to make availble to execute commands in the OSGi Shell.
 * 
 * @author junghwan.hong
 * 
 */
public class OdenExternalServlet extends HessianServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7131549884014328694L;

	public final static String NAME = "oden";

	protected void setHttpService(HttpService hs) {
		try {
			// HessianServlet uses currentThread's contextClassLoader to load
			// the
			// home-api and home-class instances.
			Thread.currentThread().setContextClassLoader(
					ExtDeployerImpl.class.getClassLoader());

			Hashtable<String, String> prop = new Hashtable<String, String>();
			prop.put("home-api", ExtDeployerService.class.getName());
			prop.put("home-class", ExtDeployerImpl.class.getName());

			hs
					.registerServlet("/" + OdenExternalServlet.NAME, this,
							prop, null);
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	@Override
	public void service(ServletRequest request, ServletResponse response)
			throws IOException, ServletException {
		Thread.currentThread().setContextClassLoader(
				ExtDeployerImpl.class.getClassLoader());
		super.service(request, response);
	}
}
