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

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.oden.admin.common.OdenCommonDao;
import org.anyframe.oden.admin.domain.Log;
import org.anyframe.oden.admin.service.LogService;
import org.springframework.stereotype.Service;

/**
 * This is LogServiceImpl Class
 * 
 * @author Junghwan Hong
 */
@Service("logService")
public class LogServiceImpl implements LogService {

	@Inject
	@Named("odenCommonDao")
	OdenCommonDao<Log> odenCommonDao;

	/**
	 * Method for getting log with date.
	 * 
	 * @param param
	 */
	public Log findList(String param) throws Exception {
		return odenCommonDao.findLog("log", "error", param);
	}
}