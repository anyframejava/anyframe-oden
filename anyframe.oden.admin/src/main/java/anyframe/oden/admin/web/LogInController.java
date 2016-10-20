/*
 * Copyright 2010 SAMSUNG SDS Co., Ltd. All rights reserved.
 *
 * No part of this "source code" may be reproduced, stored in a retrieval
 * system, or transmitted, in any form or by any means, mechanical,
 * electronic, photocopying, recording, or otherwise, without prior written
 * permission of SAMSUNG SDS Co., Ltd., with the following exceptions:
 * Any person is hereby authorized to store "source code" on a single
 * computer for personal use only and to print copies of "source code"
 * for personal use provided that the "source code" contains SAMSUNG SDS's
 * copyright notice.
 *
 * No licenses, express or implied, are granted with respect to any of
 * the technology described in this "source code". SAMSUNG SDS retains all
 * intellectual property rights associated with the technology described
 * in this "source code".
 *
 */
package anyframe.oden.admin.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import anyframe.oden.admin.exception.BrokerException;
import anyframe.oden.admin.service.Credential;
import anyframe.oden.admin.service.UserService;

/**
 * controller class for create user.
 * @author Hong JungHwan
 */
@Controller
public class LogInController {

    @Resource
    private UserService userService = null;

    @RequestMapping("/login.do")
    public ModelAndView checkUser(HttpServletRequest request ,@RequestParam("userid") String userid, @RequestParam("password") String password) throws Exception {
    	ModelAndView mav = null;
    	HttpSession session = request.getSession();
    	
    	Credential c = new Credential();
    	c.setProperty("userid", userid);
    	c.setProperty("password", password);
    	
    	try {
    		boolean auth = userService.checkuser(c);
    		if(auth) {
	    		mav = new ModelAndView("jsonLayout");
	    		session.setAttribute("userid", userid);
	    		session.setAttribute("password", password);
    		}
    	} catch(BrokerException e) {
    	    mav = new ModelAndView("login");
    		mav.addObject("exception" , e);
    		
    		return mav;
    	} 
    	
        return mav;
    }
}
