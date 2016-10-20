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
package org.anyframe.oden.admin.common;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class provides some properties when using OdenBrokerService.
 * 
 * @author Junghwan Hong
 */
public class BrokerHandler implements InitializingBean {

	private static OdenBrokerService odenBroker = new OdenBrokerImpl();

	private static String server;
	private static String port;

	protected static ApplicationContext context;

	/**
	 * initializing
	 */
	private void setup() {
		context = new ClassPathXmlApplicationContext(
				"classpath:spring/context-property.xml");
		Map key = (Map) context.getBean("contextProperties");

		server = (String) key.get("oden.server");
		port = (String) key.get("oden.port");
	}

	public static String cmdConnect(String cmd) throws Exception {
		// setup();

		String result = "";

		result = odenBroker.sendRequest("http://" + server + ":" + port
				+ "/shell", cmd);

		return result;
	}

	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		setup();
	}

}
