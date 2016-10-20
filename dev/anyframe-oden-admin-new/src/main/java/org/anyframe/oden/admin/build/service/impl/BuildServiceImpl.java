package org.anyframe.oden.admin.build.service.impl;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.oden.admin.broker.BrokerHandler;
import org.anyframe.oden.admin.build.service.BuildService;
import org.anyframe.oden.admin.util.JsonUtil;
import org.anyframe.oden.admin.vo.Build;
import org.anyframe.util.StringUtil;
import org.springframework.stereotype.Service;

@Service("buildService")
public class BuildServiceImpl implements BuildService {

	@Inject
	@Named("brokerHandler")
	BrokerHandler broker;

	public Build getBuild(String buildJobName) throws Exception {
		Build build = JsonUtil.jsonToGeneric(broker.connect("build log", buildJobName), Build.class);
		
		if(StringUtil.isEmpty(build.getBuildNo())){
			build = null;
		}
		return build;
	}

}
