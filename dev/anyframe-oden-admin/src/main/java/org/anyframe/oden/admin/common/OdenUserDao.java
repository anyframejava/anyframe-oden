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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.anyframe.oden.admin.domain.User;
import org.anyframe.oden.admin.util.CommonUtil;
import org.anyframe.oden.admin.util.OdenConstants;
import org.anyframe.pagination.Page;
import org.anyframe.query.QueryService;
import org.springframework.stereotype.Repository;

/**
 * This is OdenUserDao Class
 * 
 * @author Junghwan Hong
 */
@Repository("odenUserDao")
public class OdenUserDao {

	@Inject
	QueryService queryService;

	public Page getUserList() throws Exception {
		Collection collection = queryService.find("findUsersList", new Object[] {});

		Collection result_col = new ArrayList();
		Iterator itr = collection.iterator();
		while (itr.hasNext()) {
			User vo = (User) itr.next();
			Object[] iVal = getVoArray(vo);

			// add role name
			List role_list = (List) queryService.find("findUserRole", getObjectArray(iVal));
			String role = ((Map) role_list.get(0)).get("groupName") + "";
			vo.setRole(role);

			// add assign job
			List<Map> job_list = (List) queryService.find("findUserJobList", getObjectArray(iVal));
			String assign_job = "";

			for (Map map : job_list) {
				if (map.get("roleId").equals("ROLE_ADMIN")) {
					assign_job = "All Jobs";
					continue;
				}
				assign_job = assign_job.concat(map.get("roleId") + ", ");
			}
			if (assign_job.length() > 0 && !"All Jobs".equals(assign_job)) {
				vo.setJob(assign_job.substring(0, assign_job.length() - 2));
			} else {
				vo.setJob(assign_job);
			}

			if (!(vo.getUserId().equalsIgnoreCase("oden"))) {
				String deleteAction = "<a href=\"javascript:deleteUser('" + vo.getUserId() + "');\">" + OdenConstants.IMG_TAG_DEL + "</a>";
				vo.setHidden(deleteAction);
			}
			result_col.add(vo);
		}

		return new Page(result_col, 1, result_col.size(), result_col.size(), result_col.size());
	}

	private Object[] getObjectArray(Object[] iVal) {
		return new Object[] { iVal };
	}

	private Object[] getVoArray(User vo) {
		return new Object[] { "vo", vo };
	}

	public User getUser(String id) throws Exception {
		User vo = new User();
		vo.setUserId(id);
		Object[] iVal = new Object[] { "vo", vo };

		User user = (User) ((List) queryService.find("findUsersByPk", new Object[] { iVal })).get(0);

		List role_list = (List) queryService.find("findUserRole", new Object[] { iVal });
		String role = ((Map) role_list.get(0)).get("groupName") + "";
		user.setRole(role);

		List<Map> job_list = (List) queryService.find("findUserJobList", new Object[] { iVal });
		String assign_job = "";

		for (Map map : job_list) {
			assign_job = assign_job.concat(map.get("roleId") + ", ");
		}
		if (assign_job.length() > 0) {
			user.setJob(assign_job.substring(0, assign_job.length() - 2));
		} else {
			user.setJob(assign_job);
		}

		return user;
	}

	public int createUser(String id, String pw) throws Exception {
		User vo = new User();
		vo.setUserId(id);
		vo.setPassword(pw);
		vo.setUserName(id);
		vo.setEnabled("Y");
		vo.setCreateDate(CommonUtil.getCurrentDate());
		vo.setModifyDate(CommonUtil.getCurrentDate());

		Object[] iVal = new Object[] { "vo", vo };

		return queryService.create("createUsers", new Object[] { iVal });
	}

	public Collection findGroupByName(String role) throws Exception {
		return queryService.find("findGroupByName", new Object[] { new Object[] { "groupName", role } });
	}

	public int createGroupUser(String groupId, String id) throws Exception {
		return queryService.create("createGroupUser", new Object[] { new Object[] { "groupId", groupId }, new Object[] { "userId", id },
				new Object[] { "createDate", CommonUtil.getCurrentDate() }, new Object[] { "modifyDate", CommonUtil.getCurrentDate() } });
	}

	public int createAuthorities(String jobName, String id) throws Exception {
		return queryService.create("createAuthorities",
				new Object[] { new Object[] { "roleId", jobName }, new Object[] { "subjectId", id }, new Object[] { "type", "U" },
						new Object[] { "createDate", CommonUtil.getCurrentDate() }, new Object[] { "modifyDate", CommonUtil.getCurrentDate() } });
	}

	public int updateUser(String id, String pw) throws Exception {
		User vo = new User();
		vo.setUserId(id);
		vo.setPassword(pw);
		vo.setUserName(id);
		vo.setEnabled("Y");
		vo.setCreateDate(CommonUtil.getCurrentDate());
		vo.setModifyDate(CommonUtil.getCurrentDate());

		Object[] iVal = new Object[] { "vo", vo };

		return queryService.create("updateUsers", new Object[] { iVal });
	}

	public int updateGroupUser(String groupId, String id) throws Exception {
		return queryService.create("updateGroupUser", new Object[] { new Object[] { "groupId", groupId }, new Object[] { "userId", id },
				new Object[] { "createDate", CommonUtil.getCurrentDate() }, new Object[] { "modifyDate", CommonUtil.getCurrentDate() } });
	}

	public int removeUser(String id) throws Exception {
		return queryService.create("removeUsers", new Object[] { new Object[] { "userId", id } });
	}

	public int removeGroupUser(String id) throws Exception {
		return queryService.create("removeGroupUser", new Object[] { new Object[] { "userId", id } });
	}

	public int removeAuthorities(String id) throws Exception {
		return queryService.create("removeAuthorities", new Object[] { new Object[] { "userId", id } });
	}
}
