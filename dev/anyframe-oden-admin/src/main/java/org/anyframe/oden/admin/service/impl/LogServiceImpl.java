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
import org.anyframe.oden.admin.domain.Log;
import org.anyframe.oden.admin.service.LogService;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @created 14-7-2010 ���� 10:13:34
 * @author HONG JungHwan
 */
@Service("logService")
public class LogServiceImpl implements LogService {
	
	private OdenCommonDao<Log> odenCommonDao = new OdenCommonDao<Log>();
	
	/**
	 * Method for getting log with date.
	 * 
	 * @param param
	 */
	public Log findList(String param) throws Exception {
		return odenCommonDao.findLog("log", "error", param);
	}
}