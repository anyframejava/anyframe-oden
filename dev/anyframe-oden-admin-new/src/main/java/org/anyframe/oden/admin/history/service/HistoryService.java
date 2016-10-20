package org.anyframe.oden.admin.history.service;

import java.util.List;

import org.anyframe.oden.admin.vo.History;
import org.anyframe.oden.admin.vo.HistoryDetail;
import org.anyframe.oden.admin.vo.HistoryDetailWrapper;

public interface HistoryService {

	List<History> getList(History history) throws Exception;

	HistoryDetailWrapper getDetail(HistoryDetail log) throws Exception;

}
