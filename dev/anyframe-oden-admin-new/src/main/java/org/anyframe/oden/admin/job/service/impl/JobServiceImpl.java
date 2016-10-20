package org.anyframe.oden.admin.job.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.oden.admin.broker.BrokerHandler;
import org.anyframe.oden.admin.build.service.BuildService;
import org.anyframe.oden.admin.history.service.HistoryService;
import org.anyframe.oden.admin.job.service.JobService;
import org.anyframe.oden.admin.util.JsonUtil;
import org.anyframe.oden.admin.vo.History;
import org.anyframe.oden.admin.vo.Job;
import org.anyframe.util.StringUtil;
import org.springframework.stereotype.Service;

@Service("jobService")
public class JobServiceImpl implements JobService {

	@Inject
	@Named("brokerHandler")
	BrokerHandler broker;
	
	@Inject
	@Named("historyService")
	HistoryService historyService;
	
	@Inject
	@Named("buildService")
	BuildService buildService;

	public List<Job> getList() throws Exception {
		List<Job> jobList = new ArrayList<Job>();

		List<String> jobNameList = Arrays.asList(JsonUtil.jsonToGeneric(broker.connect("job info"), String[].class));

		for (String jobName : jobNameList) {
			Job job = JsonUtil.jsonToGeneric(broker.connect("job info", jobName), Job.class);
			
			if (jobName.indexOf("\\") != -1) {
				//job name update
				jobName = jobName.replaceAll("\\\\", "/");
				job.setName(jobName);
			}
			
			
			// latest deploy history 
			History history = new History();
			history.setJob(jobName);

			List<History> historyList = historyService.getList(history);
			if(historyList != null && historyList.size() > 0){
				history = historyList.get(0); // latest
				job.setDeployHistory(history);
			}
			
			// latest build history
			if(!StringUtil.isEmpty(job.getBuild())){
				job.setBuildHistory(buildService.getBuild(job.getBuild()));
			}
				
			jobList.add(job);
		}

		return jobList;
	}
}
