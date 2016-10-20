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
