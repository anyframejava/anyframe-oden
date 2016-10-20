package org.anyframe.oden.admin.build.service;

import org.anyframe.oden.admin.vo.Build;

public interface BuildService {

	public Build getBuild(String buildJobName) throws Exception;
}
