package org.anyframe.oden.admin.job.service;

import java.util.List;

import org.anyframe.oden.admin.vo.Job;

public interface JobService {

	List<Job> getList() throws Exception;
}
