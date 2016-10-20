/*
 * Copyright 2002-2014 the original author or authors.
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
package org.anyframe.oden.bundle.build.log;

import org.anyframe.oden.bundle.build.config.BrecordElement;
import org.anyframe.oden.bundle.common.StringUtil;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * This is BRecordInfoBinding Class
 * 
 * @author Junghwan Hong
 */
public class BRecordElementBinding extends TupleBinding {

	private String jobName;

	public BRecordElementBinding() {
	}
	
	public BRecordElementBinding(String jobName) {
		this.jobName = jobName;
	}
	
	@Override
	public Object entryToObject(TupleInput in) {
		if (in.available() == 0) {
			return null;
		}

		BrecordElement ret = new BrecordElement();
		ret.setId(in.readString());
		ret.setJobName(in.readString());
		
		if (!StringUtil.empty(jobName) && !jobName.equals(ret.getJobName())) {
			return null;
		}
		
		ret.setDate(in.readLong());
		ret.setBuildNo(in.readString());
		ret.setSuccess(in.readBoolean());
		
		// TODO path search
		return ret;
	}

	@Override
	public void objectToEntry(Object o, TupleOutput out) {
		BrecordElement r = (BrecordElement) o;
		
		out.writeString(r.getId()); // id is key
		out.writeString(r.getJobName()); // job name
		out.writeLong(r.getDate()); // Date
		out.writeString(r.getBuildNo()); // Build No
		out.writeBoolean(r.isSuccess()); // success
	}

}
