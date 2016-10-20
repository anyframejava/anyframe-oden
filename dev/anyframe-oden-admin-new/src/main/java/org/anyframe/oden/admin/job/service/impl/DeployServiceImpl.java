package org.anyframe.oden.admin.job.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.oden.admin.broker.BrokerHandler;
import org.anyframe.oden.admin.job.service.DeployService;
import org.anyframe.oden.admin.util.JsonUtil;
import org.anyframe.oden.admin.vo.DeployDetail;
import org.anyframe.oden.admin.vo.DeployDetailWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service("deployService")
public class DeployServiceImpl implements DeployService {

	@Inject
	@Named("brokerHandler")
	BrokerHandler broker;

	public List<DeployDetail> test(DeployDetail detail) throws Exception {
		List<String> optionList = new ArrayList<String>();
		optionList.add("-i");
		DeployDetailWrapper wrapper = JsonUtil.jsonToGeneric(broker.connect("deploy test", detail.getJobName(), StringUtils.join(optionList, " ")),
				DeployDetailWrapper.class);

		return wrapper.getData();
	}

}
