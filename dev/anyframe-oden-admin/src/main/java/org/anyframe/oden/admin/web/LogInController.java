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
package org.anyframe.oden.admin.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.anyframe.oden.admin.exception.BrokerException;
import org.anyframe.oden.admin.service.Credential;
import org.anyframe.oden.admin.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import anyframe.iam.core.userdetails.jdbc.CustomUserDetailsHelper;

/**
 * controller class for create user.
 * 
 * @author Hong JungHwan
 */
@Controller
public class LogInController {

	@Resource
	private UserService userService = null;

	protected static ApplicationContext context;

	private Connection connection;

	private String userid;

	@RequestMapping("/login.do")
	public ModelAndView checkUser(HttpServletRequest request) throws Exception {
		ModelAndView mav = null;
		HttpSession session = request.getSession();
		userid = CustomUserDetailsHelper.getAuthenticatedUser().getUsername();
		
		Credential c = new Credential();
		c.setProperty("userid", "oden");
		c.setProperty("password", "oden0");

		try {
			boolean auth = userService.checkuser(c);
			if (auth) {
				mav = new ModelAndView("jsonLayout");
				session.setAttribute("userid", userid);
				session.setAttribute("userrole", getRole());
				
			}
		} catch (BrokerException e) {
			mav = new ModelAndView("login");
			mav.addObject("exception", e);

			return mav;
		}

		return mav;
	}

	private String getRole() throws Exception {
		String roles = "";
		if(! userid.equals("") || userid != null) {
			Class.forName("org.hsqldb.jdbcDriver");
			connection = DriverManager.getConnection(
					"jdbc:hsqldb:hsql://localhost/odendb", "sa", "");
			ResultSet rs = connection.prepareStatement(
					"SELECT ROLE_ID FROM AUTHORITIES WHERE SUBJECT_ID ='" + userid
							+ "'").executeQuery();
			while(rs.next()) {
				roles = roles + rs.getString(1)+ ",";
			}
		}
		return roles;
	}
}
