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
 * This is UserServiceImpl Class
 *  
 * @author Junghwan Hong
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
		return request(c.getProperty("userid"), c.getProperty("password"));
	}

	/**
	 * 
	 * @param param
	 * @throws Exception
	 */
	private boolean request(String userid, String password) throws Exception {
		return OdenBroker.checkUser("http://" + server + ":" + port + "/shell",
				userid, password);
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
		String groupId = ((HashMap) ((ArrayList) odenUserDao
				.findGroupByName(role)).get(0)).get("groupId") + "";
		odenUserDao.createGroupUser(groupId, id);
		for (int i = 0; i < jobs.length; i++) {
			if (jobs.length == 1 && jobs[0].equalsIgnoreCase("empty")) {
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
		String groupId = ((HashMap) ((ArrayList) odenUserDao
				.findGroupByName(role)).get(0)).get("groupId") + "";
		odenUserDao.updateGroupUser(groupId, id);
		odenUserDao.removeAuthorities(id);
		for (int i = 0; i < jobs.length; i++) {
			if (jobs.length == 1 && jobs[0].equalsIgnoreCase("empty")) {
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

	private void roleReloading() throws Exception {
		resourceReloadService.resourceReload("maps", "times");
	}

}