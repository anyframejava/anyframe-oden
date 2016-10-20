package org.anyframe.oden.admin.history.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.oden.admin.broker.BrokerHandler;
import org.anyframe.oden.admin.history.service.HistoryService;
import org.anyframe.oden.admin.util.JsonUtil;
import org.anyframe.oden.admin.vo.CompareTarget;
import org.anyframe.oden.admin.vo.History;
import org.anyframe.oden.admin.vo.HistoryDetail;
import org.anyframe.oden.admin.vo.HistoryDetailWrapper;
import org.anyframe.oden.admin.vo.HistoryWrapper;
import org.anyframe.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service("historyService")
public class HistoryServiceImpl implements HistoryService {

	@Inject
	@Named("brokerHandler")
	BrokerHandler broker;

	public List<History> getList(History history) throws Exception {
		HistoryWrapper historyWrapper = null;
		if (StringUtils.isEmpty(history.getJob())) {
			historyWrapper = JsonUtil.jsonToGeneric(broker.connect("log search"), HistoryWrapper.class);
		} else {
			historyWrapper = JsonUtil.jsonToGeneric(broker.connect("log search -job", history.getJob()), HistoryWrapper.class);
		}
		return historyWrapper.getData();
	}

	public HistoryDetailWrapper getDetail(HistoryDetail log) throws Exception {
		String cmd = "log show";

		// 1. [essential] txid
		cmd += " " + log.getTxId();

		// 2. [optional] mode (A, U, D)
		if (!StringUtil.isEmpty(log.getMode())) {
			if ("A".equalsIgnoreCase(log.getMode())) {
				cmd += " -mode A";
			} else if ("U".equalsIgnoreCase(log.getMode())) {
				cmd += " -mode U";
			} else if ("D".equalsIgnoreCase(log.getMode())) {
				cmd += " -mode D";
			}
		}

		// 3. [optional] path
		if (!StringUtil.isEmpty(log.getPath())) {
			cmd += " -path \"" + log.getPath() + "\"";
		}

		// 4. [optional] failonly
		if (!StringUtil.isEmpty(log.getSuccess()) && "F".equalsIgnoreCase(log.getSuccess())) {
			cmd += " -failonly";
		}

		HistoryDetailWrapper historyDetailWrapper = JsonUtil.jsonToGeneric(broker.connect(cmd), HistoryDetailWrapper.class);
		Set<String> agentSet = new HashSet<String>();

		HistoryDetailWrapper wrapper = new HistoryDetailWrapper();
		wrapper.setData(rawLogToProductLog(historyDetailWrapper.getData(), agentSet));
		wrapper.setAgents(new ArrayList<String>(agentSet));
		return wrapper;
	}

	/**
	 * <pre>
	 * rawData) 여러 agent에 deploy할 경우, agent 하나별로 HistoryDetail 생성되어 있음 (file개수 X agent개수)
	 * proudctData ) 하나의 HistoryDetail에 여러 agent에 배포한 이력 쌓음 (file개수)
	 * </pre>
	 * 
	 * @param rawData
	 * @param agentSet
	 * @return
	 * @throws Exception
	 */
	private List<HistoryDetail> rawLogToProductLog(List<HistoryDetail> rawData, Set<String> agentSet) throws Exception {

		Map<String, HistoryDetail> pathMap = new HashMap<String, HistoryDetail>();
		for (HistoryDetail raw : rawData) {
			String path = raw.getPath();
			String agent = raw.getTargets().get(0);
			agentSet.add(agent);

			HistoryDetail newLog = pathMap.get(path);
			if (newLog == null) {
				newLog = new HistoryDetail();
				newLog.setTargetList(new ArrayList<CompareTarget>());

				newLog.setPath(path);
			}

			CompareTarget target = new CompareTarget();
			target.setName(agent);
			target.setStatus(raw.getSuccess());
			target.setErrorLog(raw.getErrorlog());
			target.setMode(raw.getMode());
			
			newLog.getTargetList().add(target);
			
			pathMap.put(path, newLog);
			
		}
		
		// order by path
		TreeMap<String,HistoryDetail> tm = new TreeMap<String,HistoryDetail>(pathMap);
		List<HistoryDetail> productData = new ArrayList<HistoryDetail>(tm.values());
		return productData;
	}

}
