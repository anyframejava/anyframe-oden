package org.anyframe.oden.admin.web;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;

public class JasperContextLoaderListener extends ContextLoaderListener {
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			Class.forName("org.apache.jasper.compiler.JspRuntimeContext");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
