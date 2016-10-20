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
package org.anyframe.oden.admin.service.impl;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.oden.admin.common.OdenBrokerImpl;
import org.anyframe.oden.admin.common.OdenBrokerService;
import org.anyframe.oden.admin.common.OdenCommonDao;
import org.anyframe.oden.admin.common.OdenUserDao;
import org.anyframe.oden.admin.domain.Status;
import org.anyframe.oden.admin.domain.User;
import org.anyframe.oden.admin.service.Credential;
import org.anyframe.oden.admin.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import anyframe.common.Page;
import anyframe.iam.core.reload.IResourceReloadService;

/**
 * @version 1.0
 * @created 14-7-2010 ���� 10:13:42
 * @author HONG JungHwan
 */
@Service("userService")
@Transactional(rollbackFor = { Exception.class })
public class UserServiceImpl implements UserService {

	private static OdenBrokerService OdenBroker = new OdenBrokerImpl();

	@Value("#{contextProperties['oden.server'] ?: 'localhost'}")
	private String server;
	
	@Value("#{contextProperties['oden.port'] ?: '9860'}")
	private String port;
	
	private OdenCommonDao odenCommonDao = new OdenCommonDao<Status>();
	
	@Inject
    @Named("odenUserDao")
	private OdenUserDao odenUserDao;
	
	@Inject
	@Named("resourceReloadService")
	private IResourceReloadService resourceReloadService;
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

	public Page findList(String domain) throws Exception {
		return odenUserDao.getUserList();
	}
	
	public User findUser(String id) throws Exception {
		return odenUserDao.getUser(id);
	}

	public void createUser(String role, String id, String pw, String[] jobs)
			throws Exception {
		odenUserDao.createUser(id, pw);
		String groupId = ((HashMap)((ArrayList)odenUserDao.findGroupByName(role)).get(0)).get("groupId")+"";
		odenUserDao.createGroupUser(groupId, id);
		for(int i=0; i<jobs.length; i++){
			if(jobs.length == 1 && jobs[0].equalsIgnoreCase("empty")){
				break;
			}
			String jobName = jobs[i];
			odenUserDao.createAuthorities(jobName, id);
		}
		roleReloading();
	}

	public void updateUser(String role, String id, String pw, String[] jobs)
			throws Exception {
		odenUserDao.updateUser(id, pw);
		String groupId = ((HashMap)((ArrayList)odenUserDao.findGroupByName(role)).get(0)).get("groupId")+"";
		odenUserDao.updateGroupUser(groupId, id);
		odenUserDao.removeAuthorities(id);
		for(int i=0; i<jobs.length; i++){
			if(jobs.length == 1 && jobs[0].equalsIgnoreCase("empty")){
				break;
			}
			String jobName = jobs[i];
			odenUserDao.createAuthorities(jobName, id);
		}
		roleReloading();
	}

	public void removeUser(String id) throws Exception {
		odenUserDao.removeAuthorities(id);
		odenUserDao.removeGroupUser(id);
		odenUserDao.removeUser(id);
		roleReloading();
	}
	
	private void roleReloading() throws Exception{
		resourceReloadService.resourceReload("maps", "times");
	}

}