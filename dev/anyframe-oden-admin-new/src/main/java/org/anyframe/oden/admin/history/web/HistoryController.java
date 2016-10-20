package org.anyframe.oden.admin.history.web;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.oden.admin.history.service.HistoryService;
import org.anyframe.oden.admin.vo.History;
import org.anyframe.oden.admin.vo.HistoryDetail;
import org.anyframe.oden.admin.vo.HistoryDetailWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("history")
public class HistoryController {

	@Inject
	@Named("historyService")
	HistoryService historyService;

	@RequestMapping("/listView.do")
	public String listView(Model model) throws Exception {
		return "history/list";
	}
	
	@RequestMapping("/detailView.do")
	public String detailView(@RequestParam("txId") String txId, Model model) throws Exception {
		model.addAttribute("txId", txId);
		return "history/detail";
	}

	@RequestMapping("/list.do")
	@ResponseBody
	public List<History> list(Model model) throws Exception {
		History history = new History();
		return historyService.getList(history);
	}

	@RequestMapping("/view.do")
	@ResponseBody
	public HistoryDetailWrapper view(@RequestParam("txId") String txId, Model mode) throws Exception {
		HistoryDetail detail = new HistoryDetail();
		detail.setTxId(txId);
		return historyService.getDetail(detail);
	}
	
}
