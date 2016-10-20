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
package org.anyframe.oden.bundle.hessiansvr;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.osgi.service.http.HttpService;

import org.anyframe.oden.bundle.common.Logger;
import org.anyframe.oden.bundle.deploy.DeployerService;

import com.caucho.hessian.server.HessianServlet;

/**
 * Servlet to make availble to execute commands in the OSGi Shell.
 * 
 * @author joon1k
 *
 */
public class OdenHessianServlet extends HessianServlet{
	private static final long serialVersionUID = 7131549884014328694L;
	
	public final static String NAME = "deploy";
	
	protected void setHttpService(HttpService hs){
		try {
			// HessianServlet uses currentThread's contextClassLoader to load the
			// home-api and home-class instances.
			Thread.currentThread().setContextClassLoader(DeployerImpl.class.getClassLoader());
			
			Hashtable<String, String> prop  = new Hashtable<String, String>();
			prop.put("home-api", DeployerService.class.getName());
			prop.put("home-class", DeployerImpl.class.getName());
			
			hs.registerServlet(
					"/" + OdenHessianServlet.NAME, 
					this, 
					prop, 
					null);
		} catch (Exception e) {
			Logger.error(e);
		}
	}
	
	@Override
	public void service(ServletRequest request, ServletResponse response)
			throws IOException, ServletException {
		Thread.currentThread().setContextClassLoader(DeployerImpl.class.getClassLoader());
		super.service(request, response);
	}
}
