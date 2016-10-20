/*
 * Copyright 2010 SAMSUNG SDS Co., Ltd. All rights reserved.
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
package org.anyframe.oden.admin.common;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class provides some properties when using OdenBrokerService.   
 * 
 * @author Hong JungHwan
 *
 */
public class BrokerHandler implements InitializingBean {

	private static OdenBrokerService OdenBroker = new OdenBrokerImpl();
	
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
	}
	
	
	
	public static String cmdConnect(String cmd) throws Exception{
		//setup();
		
		String result = "";
		
		result = OdenBroker.sendRequest("http://" + server + ":" + port
				+ "/shell", cmd);
		
		return result;
	}



	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		setup();
	}
	
}
