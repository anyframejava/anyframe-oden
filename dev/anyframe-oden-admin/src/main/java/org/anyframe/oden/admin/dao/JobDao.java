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
package org.anyframe.oden.admin.dao;

import javax.inject.Inject;

import org.anyframe.oden.admin.common.CommonUtil;
import org.springframework.stereotype.Repository;

import anyframe.core.query.IQueryService;

@Repository("jobDao")
public class JobDao {
	@Inject
	private IQueryService queryService;

	/**
	 * @param jobname
	 * @return
	 * @throws Exception
	 */
	public int insertRoles(String jobname) throws Exception {
		return queryService.create("insertRoles", new Object[] {
				new Object[] { "roleid", jobname },
				new Object[] { "rolename", jobname },
				new Object[] { "desc", jobname },
				new Object[] { "createdate", CommonUtil.getCurrentDate() },
				new Object[] { "modifydate", CommonUtil.getCurrentDate() }, });
	}

	/**
	 * @param jobname
	 * @return
	 * @throws Exception
	 */
	public int removeRoles(String jobname) throws Exception {
		return queryService.remove("removeRoles", new Object[] { new Object[] {
				"jobname", jobname } });
	}
	
	/**
	 * @param jobname
	 * @return
	 * @throws Exception
	 */
	public int removeAuthorities(String jobname) throws Exception {
		return queryService.remove("removeAuthority", new Object[] { new Object[] {
				"jobname", jobname } });
	}
	
	/**
	 * @param jobname
	 * @return
	 * @throws Exception
	 */
	public int insertSecuredResRoles(String id, String jobname)
			throws Exception {
		return queryService.create("insertSecuredResRoles", new Object[] {
				new Object[] { "resourceid", id },
				new Object[] { "roleid", jobname },
				new Object[] { "createdate", CommonUtil.getCurrentDate() },
				new Object[] { "modifydate", CommonUtil.getCurrentDate() } });
	}

	/**
	 * @param jobname
	 * @return
	 * @throws Exception
	 */
	public int removeSecuredResRoles(String jobname) throws Exception {
		return queryService.remove("removeSecuredResRoles",
				new Object[] { new Object[] { "jobname", jobname } });
	}
}
