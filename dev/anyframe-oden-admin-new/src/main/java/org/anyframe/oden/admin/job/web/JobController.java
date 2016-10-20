package org.anyframe.oden.admin.job.web;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.oden.admin.job.service.DeployService;
import org.anyframe.oden.admin.job.service.JobService;
import org.anyframe.oden.admin.vo.DeployDetail;
import org.anyframe.oden.admin.vo.Job;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("job")
public class JobController {

	@Inject
	@Named("jobService")
	JobService jobService;

	@Inject
	@Named("deployService")
	DeployService deployService;

	@RequestMapping("/listView.do")
	public String listView(Model model) throws Exception {
		return "job/list";
	}

	@RequestMapping("/deployView.do")
	public String deployView(@RequestParam("job") String job, Model model) throws Exception {
		model.addAttribute("job", job);
		return "job/deploy";
	}

	@RequestMapping("/list.do")
	@ResponseBody
	public List<Job> list(Model model) throws Exception {
		return jobService.getList();
	}

	@RequestMapping("/deployList.do")
	@ResponseBody
	public List<DeployDetail> deployList(@RequestParam("job") String job, Model model) throws Exception {
		DeployDetail detail = new DeployDetail();
		detail.setJobName(job);
		return deployService.test(detail);
	}
}
