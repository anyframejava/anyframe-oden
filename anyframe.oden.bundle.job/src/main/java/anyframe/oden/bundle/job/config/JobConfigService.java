package anyframe.oden.bundle.job.config;

import java.util.List;

public interface JobConfigService {
	public void addJob(CfgJob job) throws Exception;
	
	public void removeJob(String name) throws Exception;
	
	public List<String> listJobs() throws Exception;
	
	public CfgJob getJob(String name) throws Exception;
}
