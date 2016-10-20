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
package org.anyframe.oden.admin.broker;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.oden.admin.broker.service.OdenBroker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hsqldb.lib.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * This class provides some properties when using OdenBrokerService.
 * 
 * @author Junghwan Hong
 */
@Service("brokerHandler")
public class BrokerHandler {

	Log logger = LogFactory.getLog(this.getClass());

	@Inject
	@Named("odenBroker")
	private OdenBroker odenBroker;

	@Value("#{contextProperties['oden.server'] ?: 'localhost'}")
	private String server;

	@Value("#{contextProperties['oden.port'] ?: '9860'}")
	private String port;

	public String connect(String cmd, String jobName) throws Exception{
		return connect(cmd, jobName, "");
	}
	
	public String connect(String cmd, String jobName, String option) throws Exception{
		return connect(cmd + " \"" + jobName + "\"" + " " + option);
	}
	
	public String connect(String cmd) throws Exception {
		String url = "http://" + server + ":" + port + "/shell";

		if(!cmd.trim().endsWith("-json")){
			cmd += " -json";
		}
		
		String result = odenBroker.sendRequest(url, cmd);

		logger.debug("Broker Connect : [" + url + "] " + cmd);
		logger.debug("Server Result : " + result);

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

	public boolean healthCheck() throws Exception {
		return odenBroker.healthCheck("http://" + server + ":" + port + "/shell");
	}

}
