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
package anyframe.oden.bundle.core.record;

import java.util.List;

import anyframe.oden.bundle.common.OdenException;

/**
 * Oden Service to log deploy information. Whenever deploy some files to Deployerervice,
 * that information is logged by this service.
 * 
 * @author joon1k
 *
 */
public interface DeployLogService2 {
	public void record(RecordElement2 r) throws OdenException;
	
	public List<RecordElement2> search(String txid, String host, String agent, String path, 
			String startdate, String enddate, boolean failonly) throws OdenException;
	
	public List<String> recordedDateList();
}
