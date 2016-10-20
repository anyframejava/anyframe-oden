/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.anyframe.oden.admin.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.anyframe.iam.core.userdetails.jdbc.CustomUserDetailsHelper;
import org.anyframe.oden.admin.exception.BrokerException;
import org.anyframe.oden.admin.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * controller class for create user.
 * 
 * @author Junghwan Hong
 */
@Controller
public class LogInController {

	@Resource
	private UserService userService;

	protected static ApplicationContext context;

	private Connection connection;

	private String userid;

	@Value("#{contextProperties['url'] ?: }")
	String url;

	@RequestMapping("/login.do")
	public ModelAndView checkUser(HttpServletRequest request) throws Exception {
		ModelAndView mav = null;
		HttpSession session = request.getSession();
		userid = CustomUserDetailsHelper.getAuthenticatedUser().getUsername();
		
//		Credential c = new Credential();
		Properties c = new Properties();
		c.setProperty("userid", "oden");
		c.setProperty("password", "oden0");

		try {
			boolean auth = userService.checkuser(c);
			if (auth) {
				mav = new ModelAndView("jsonLayout");
				session.setAttribute("userid", userid);
				session.setAttribute("userrole", getRole());
				session.setAttribute("groupid", getGroupId());
			}
		} catch (BrokerException e) {
			mav = new ModelAndView("login");
			mav.addObject("exception", e);

			return mav;
		}

		return mav;
	}

	private String getRole() throws Exception{
		String roles = "";
		ResultSet rs = null;
		if (!"".equals(userid) || userid != null) {
			try {
				Class.forName("org.hsqldb.jdbcDriver");
				connection = DriverManager.getConnection(url, "sa", "");
				if (connection != null) {
					rs = connection.prepareStatement(
							"SELECT ROLE_ID FROM AUTHORITIES WHERE SUBJECT_ID ='"
									+ userid + "'").executeQuery();
					while (rs.next()) {
						roles = roles.concat(rs.getString(1) + ",");
					}
				}
			} catch (Exception e) {
				throw e;
			} finally {
				rs.close();
				connection.close();
			}
		}
		return roles;
	}
	
	private String getGroupId() throws Exception{
		String groupId = "";
		ResultSet rs = null;
		if (!"".equals(userid) || userid != null) {
			try {
				Class.forName("org.hsqldb.jdbcDriver");
				connection = DriverManager.getConnection(url, "sa", "");
				if (connection != null) {
					rs = connection.prepareStatement(
							"SELECT GROUP_ID FROM GROUPS_USERS WHERE USER_ID ='"
									+ userid + "'").executeQuery();
					while (rs.next()) {
						groupId = rs.getString("GROUP_ID");
					}
				}
			} catch (Exception e) {
				throw e;
			} finally {
				rs.close();
				connection.close();
			}
		}
		return groupId;
	}
}
