package org.anyframe.oden.admin.user.web;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

	@RequestMapping("/view.do")
	public String view(Model model, HttpSession session) throws Exception {
		return "user/list";
	}
	
}
