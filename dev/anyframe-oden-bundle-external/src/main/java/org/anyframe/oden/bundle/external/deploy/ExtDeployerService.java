/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.anyframe.oden.bundle.external.deploy;

import org.anyframe.oden.bundle.external.config.CfgHistory;
import org.anyframe.oden.bundle.external.config.CfgJob;
import org.anyframe.oden.bundle.external.config.CfgReturnVO;

/**
 * This class provides some methods to manipulate remote files. If you want to
 * handling remote files, register this service to the R-OSGi bundle. Your
 * service can access remote files using that registered service.
 * 
 * @author junghwan.hong
 * 
 */
public interface ExtDeployerService {

	public CfgReturnVO execute(CfgJob job) throws Exception;

	public CfgReturnVO rollback(String id) throws Exception;

	public String stop(String id) throws Exception;
	
	public CfgHistory log(String id, int pageIndex , int pageSize) throws Exception;
}
