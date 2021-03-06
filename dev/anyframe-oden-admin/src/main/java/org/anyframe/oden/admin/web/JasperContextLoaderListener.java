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
package org.anyframe.oden.admin.web;

import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ContextLoaderListener;

/**
 * This is JasperContextLoaderListener Class
 * 
 * @author Junghwan Hong
 */
public class JasperContextLoaderListener extends ContextLoaderListener {
	@Override
	public void contextInitialized(ServletContextEvent event) {
		Log logger = LogFactory.getLog(JasperContextLoaderListener.class);
		try {
			Class.forName("org.apache.jasper.compiler.JspRuntimeContext");
		} catch (ClassNotFoundException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e.getMessage());
			}
		}
	}
}
