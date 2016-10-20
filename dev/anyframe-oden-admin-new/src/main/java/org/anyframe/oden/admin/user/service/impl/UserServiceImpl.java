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
package org.anyframe.oden.admin.user.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.iam.core.reload.IResourceReloadService;
import org.anyframe.oden.admin.user.service.UserService;
import org.anyframe.oden.admin.util.OdenConstants;
import org.anyframe.oden.admin.vo.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is UserServiceImpl Class
 * 
 * @author Junghwan Hong
 */
@Service("userService")
@Transactional(rollbackFor = { Exception.class })
public class UserServiceImpl implements UserService {

	@Value("#{contextProperties['oden.server'] ?: 'localhost'}")
	private String server;

	@Value("#{contextProperties['oden.port'] ?: '9860'}")
	private String port;

	@Inject
	@Named("userDao")
	private UserDao userDao;

	@Inject
	@Named("roleDao")
	private RoleDao roleDao;

	@Inject
	@Named("resourceReloadService")
	private IResourceReloadService resourceReloadService;

	public List<User> getList(User user) throws Exception {
		List<User> userList = userDao.getUserList(user);

		for (User u : userList) {
			user.setRole(roleDao.getUserRole(u));

			List<String> jobList = this.getUserAssignedJobList(u);
			if (jobList.contains("ROLE_ADMIN")) {
				u.setJob("All Jobs");
			} else {
				u.setJob(StringUtils.join(jobList, ", "));
			}

			// TODO action 어떻게 할까?????
			if (!(u.getUserId().equalsIgnoreCase("oden"))) {
				String deleteAction = "<a href=\"javascript:deleteUser('" + u.getUserId() + "');\">" + OdenConstants.IMG_TAG_DEL + "</a>";
				u.setHidden(deleteAction);
			}
		}

		return userList;
	}

	public List<String> getUserAssignedJobList(User user) throws Exception {
		return roleDao.getUserAuthorities(user);
	}

	public String getUserGroup(User user) throws Exception {
		return roleDao.getUserGroup(user);
	}

}