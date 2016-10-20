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

import org.anyframe.oden.admin.vo.User;
import org.anyframe.query.QueryService;
import org.anyframe.query.dao.QueryServiceDaoSupport;
import org.springframework.stereotype.Repository;

/**
 * This is UserDao Class
 * 
 * @author Junghwan Hong
 */
@Repository("userDao")
public class UserDao extends QueryServiceDaoSupport {

	@Inject
	public void setQueryService(QueryService queryService) {
		super.setQueryService(queryService);
	}

	public int createUser(User user) throws Exception {
		return 1;
	}

	public User getUser(User user) throws Exception {
		return new User();
	}

	public List<User> getUserList(User user) throws Exception {
		return super.findList("findUserList", user);
	}

	public int updateUser(User user) throws Exception {
		return 1;
	}
	
	public int deleteUser(User user) throws Exception{
		return 1;
	}
}
