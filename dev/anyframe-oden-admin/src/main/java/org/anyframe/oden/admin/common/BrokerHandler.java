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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hsqldb.lib.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class provides some properties when using OdenBrokerService.
 * 
 * @author Junghwan Hong
 */
public class BrokerHandler implements InitializingBean {

	Log logger = LogFactory.getLog(this.getClass());

	private static OdenBrokerService odenBroker = new OdenBrokerImpl();

	private static String server;
	private static String port;

	protected static ApplicationContext context;

	/**
	 * initializing
	 */
	private void setup() {
		context = new ClassPathXmlApplicationContext("classpath:spring/context-property.xml");
		Map key = (Map) context.getBean("contextProperties");

		server = (String) key.get("oden.server");
		port = (String) key.get("oden.port");

		logger.debug("Broker Setup : " + server + ":" + port);
	}

	public String cmdConnect(String cmd) throws Exception {
		String url = "http://" + server + ":" + port + "/shell";
		logger.debug("Broker Connect : [" + url + "] " + cmd);

		String result = odenBroker.sendRequest(url, cmd);

		if (!StringUtil.isEmpty(result)) {
			JSONArray array = new JSONArray(result);

			for (int i = 0; i < array.length(); i++) {
				if (array.get(i) instanceof String) {
					return result;
				}
				JSONObject object = (JSONObject) array.get(i);
				if (object.has("KnownException")) {
					logger.debug("KnownException Occured. Return empty string.");
					return "";
				}
			}
		}

		return result;
	}

	public void afterPropertiesSet() throws Exception {
		setup();
	}

}
