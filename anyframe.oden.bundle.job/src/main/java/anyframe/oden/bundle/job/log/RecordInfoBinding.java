package anyframe.oden.bundle.job.log;

import anyframe.oden.bundle.common.StringUtil;
import anyframe.oden.bundle.core.record.RecordElement2;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class RecordInfoBinding extends TupleBinding {
	String job;
	String user;
	boolean isFailOnly = false;
	
	public RecordInfoBinding(){
	}
	
	public RecordInfoBinding(String job, String user, boolean isFailOnly){
		this.job = job;
		this.user = user;
		this.isFailOnly = isFailOnly;
	}
	
	@Override
	public Object entryToObject(TupleInput in) {
		if(in.available() == 0) return null;
		
		ShortenRecord ret = new ShortenRecord();
		ret.setId(in.readString());
		ret.setJob(in.readString());
		if(!StringUtil.empty(job) && !job.equals(ret.getJob())) return null;
		ret.setSuccess(in.readBoolean());
		if(isFailOnly && ret.isSuccess()) return null;
		ret.setTotal(in.readInt());
		ret.setnSuccess(in.readInt());
		ret.setDate(in.readLong());
		ret.setLog(in.readString());
		ret.setUser(in.readString());
		if(!StringUtil.empty(user) && !user.equals(ret.getUser())) return null;
		
		// TODO path search
		return ret;
	}

	@Override
	public void objectToEntry(Object o, TupleOutput out) {
		RecordElement2 r = (RecordElement2)o;
		out.writeString(r.id());		// id is key
		out.writeString(r.desc());		// job
		out.writeBoolean(r.isSuccess());
		out.writeInt(r.getDeployFiles().size());	// total
		out.writeInt(r.getNSuccess());
		out.writeLong(r.getDate());
		out.writeString(r.log());
		out.writeString(r.getUser());
	}

}
