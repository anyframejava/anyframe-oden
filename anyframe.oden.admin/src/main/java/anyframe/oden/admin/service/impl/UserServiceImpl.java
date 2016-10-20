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
package anyframe.oden.admin.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import anyframe.oden.admin.common.OdenBrokerImpl;
import anyframe.oden.admin.common.OdenBrokerService;
import anyframe.oden.admin.service.Credential;
import anyframe.oden.admin.service.UserService;

/**
 * @version 1.0
 * @created 14-7-2010 ���� 10:13:42
 * @author HONG JungHwan
 */
@Service("userService")
public class UserServiceImpl implements UserService {

	private static OdenBrokerService OdenBroker = new OdenBrokerImpl();

	@Value("#{contextProperties['oden.server'] ?: 'localhost'}")
	private String server;
	
	@Value("#{contextProperties['oden.port'] ?: '9860'}")
	private String port;
	
	/**
	 * 
	 * @param password
	 * @param userid
	 * @throws Exception 
	 */
	public boolean checkuser(Credential c) throws Exception {
		return request(c.getProperty("userid"),c.getProperty("password"));
	}

	/**
	 * 
	 * @param param
	 * @throws Exception 
	 */
	private boolean request(String userid, String password) throws Exception {
		return OdenBroker.checkUser("http://" + server + ":" + port+ "/shell" , userid, password);
	}

}