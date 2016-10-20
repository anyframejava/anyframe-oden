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
package org.anyframe.oden.bundle.job.log;

import org.anyframe.oden.bundle.common.StringUtil;
import org.anyframe.oden.bundle.core.record.RecordElement2;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * This is RecordInfoBinding Class
 * 
 * @author Junghwan Hong
 */
public class RecordInfoBinding extends TupleBinding {
	String job;
	String user;
	boolean isFailOnly = false;

	public RecordInfoBinding() {
	}

	public RecordInfoBinding(String job, String user, boolean isFailOnly) {
		this.job = job;
		this.user = user;
		this.isFailOnly = isFailOnly;
	}

	@Override
	public Object entryToObject(TupleInput in) {
		if (in.available() == 0)
			return null;

		ShortenRecord ret = new ShortenRecord();
		ret.setId(in.readString());
		ret.setJob(in.readString());
		if (!StringUtil.empty(job) && !job.equals(ret.getJob()))
			return null;
		ret.setSuccess(in.readBoolean());
		if (isFailOnly && ret.isSuccess())
			return null;
		ret.setTotal(in.readInt());
		ret.setnSuccess(in.readInt());
		ret.setDate(in.readLong());
		ret.setLog(in.readString());
		ret.setUser(in.readString());
		if (!StringUtil.empty(user) && !user.equals(ret.getUser()))
			return null;

		// TODO path search
		return ret;
	}

	@Override
	public void objectToEntry(Object o, TupleOutput out) {
		RecordElement2 r = (RecordElement2) o;
		out.writeString(r.id()); // id is key
		out.writeString(r.desc()); // job
		out.writeBoolean(r.isSuccess());
		out.writeInt(r.getDeployFiles().size()); // total
		out.writeInt(r.getNSuccess());
		out.writeLong(r.getDate());
		out.writeString(r.log());
		out.writeString(r.getUser());
	}

}
