package org.anyframe.oden.bundle.job.log;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.core.DeployFile;
import org.anyframe.oden.bundle.core.DeployFile.Mode;
import org.anyframe.oden.bundle.core.record.RecordElement2;
import org.anyframe.oden.bundle.job.deploy.SlimDeployFile;

public interface JobLogService {
	public Set<DeployFile> show(String id, String path, Mode mode, 
			boolean isFailOnly) throws OdenException;
	
	public ShortenRecord search(String id) throws OdenException;
	
	public List<ShortenRecord> search(String job, String user, String path, 
			boolean isFailOnly) throws OdenException;
	
	public LogError getErrorLog(String date) throws OdenException;
	
	public void record(RecordElement2 record) throws OdenException; 
	
	public void record(String id, String user, long time, String desc, 
			int nSuccess, String error, 
			Collection<SlimDeployFile> fs) throws OdenException;
}
