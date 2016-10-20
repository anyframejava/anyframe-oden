package anyframe.oden.bundle.job.log;

import java.util.Collection;
import java.util.Set;

import anyframe.oden.bundle.common.StringUtil;
import anyframe.oden.bundle.core.AgentLoc;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.DeployFileUtil;
import anyframe.oden.bundle.core.Repository;
import anyframe.oden.bundle.core.DeployFile.Mode;
import anyframe.oden.bundle.job.deploy.SlimDeployFile;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class DeployFileListBinding extends TupleBinding {
	Mode mode = Mode.NA;
	boolean isFailOnly = false;
	String path = null;
	
	public DeployFileListBinding(){
	}
	
	public DeployFileListBinding(String path, Mode mode, boolean isFailOnly){
		this.path = path;
		this.mode = mode;
		this.isFailOnly = isFailOnly;
	}
	
	@Override
	public Object entryToObject(TupleInput in) {
		Set<DeployFile> ret = new java.util.HashSet<DeployFile>();
		if(in.available() == 0)
			return ret;
		
		int sz = in.readInt();
		for(int i=0; i<sz; i++){
			Repository repo = new Repository(new String[]{in.readString()});
			String _path = in.readString();
			AgentLoc agent = new AgentLoc(in.readString(), "", "");
			String log = in.readString();
			Mode mode = DeployFileUtil.stringToMode(in.readString());
			boolean isSuccess = in.readBoolean();
			
			if(this.mode != Mode.NA && this.mode != mode)
				continue;
			
			if(isFailOnly && isSuccess)
				continue;
			
			if(!StringUtil.empty(this.path) && !_path.endsWith(this.path))
				continue;
			
			DeployFile df = new DeployFile(repo, _path, agent, 
					0L, 0L, mode, isSuccess);
			if(!isSuccess && log != null) df.setErrorLog(log);
			ret.add(df);
		}
		return ret;
	}

	@Override
	public void objectToEntry(Object o, TupleOutput out) {
//		if(o instanceof Collection<DeployFile>){
			Collection<DeployFile> dfs = (Collection<DeployFile>)o;
			out.writeInt(dfs.size());
			for(DeployFile df : dfs){
				out.writeString(df.getRepo().args()[0]);	// arg[0] is meaningful only
				out.writeString(df.getPath());
				out.writeString(df.getAgent().agentName());
				out.writeString(df.errorLog());
				out.writeString(DeployFileUtil.modeToString(df.mode()));
				out.writeBoolean(df.isSuccess());
			}
//		}else if(o instanceof SlimDeployFile){
//			Collection<SlimDeployFile> dfs = (Collection<SlimDeployFile>)o;
//			out.writeInt(dfs.size());
//			for(SlimDeployFile df : dfs){
//				out.writeString("");	// arg[0] is meaningful only
//				out.writeString(df.getPath());
//				out.writeString(df.getTarget());
//				out.writeString(df.getError());
//				out.writeString(DeployFileUtil.modeToString(df.getMode()));
//				out.writeBoolean(df.isSuccess());
//			}
//		}
	}
}
