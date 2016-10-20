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

import org.anyframe.oden.admin.common.OdenCommonDao;
import org.anyframe.oden.admin.domain.Status;
import org.anyframe.oden.admin.service.StatusService;
import org.springframework.stereotype.Service;

import anyframe.common.Page;

/**
 * @version 1.0
 * @created 14-7-2010 ���� 10:13:35
 * @author HONG JungHwan
 */
@Service("statusService")
public class StatusServiceImpl implements StatusService {

	private OdenCommonDao<Status> odenCommonDao = new OdenCommonDao<Status>();

	/**
	 * Method for getting job list in progess.
	 * 
	 * @param domain
	 * @throws Exception
	 */
	public Page findList(String domain) throws Exception {
		return odenCommonDao.getList("status", "info");
	}

	/**
	 * Method for stopping job in progess.
	 * 
	 * @param param
	 * @throws Exception
	 */
	public String stop(String param) throws Exception {
		return odenCommonDao.getResultString("status", "stop", param);
	}

}