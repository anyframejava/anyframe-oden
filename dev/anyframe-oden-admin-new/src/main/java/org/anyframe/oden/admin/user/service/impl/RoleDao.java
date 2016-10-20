package org.anyframe.oden.admin.user.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.anyframe.oden.admin.vo.User;
import org.anyframe.query.QueryService;
import org.anyframe.query.dao.QueryServiceDaoSupport;
import org.springframework.stereotype.Repository;

@Repository("roleDao")
public class RoleDao extends QueryServiceDaoSupport {

	@Inject
	public void setQueryService(QueryService queryService) {
		super.setQueryService(queryService);
	}
	
	// ROLES
	public int createUserRole(User user) throws Exception{
		return 1;
	}
	
	public String getUserRole(User user) throws Exception{
		return super.findByPk("findUserRole", user);
	}
	
	// GROUPS
	public int createUserGroup(User user) throws Exception{
		return 1;
	}
	
	// GROUPS_USERS
	public int createUserGroupRole(User user) throws Exception{
		return 1;
	}
	
	public String getUserGroup(User user) throws Exception{
		Map<String, String> result = super.findByPk("findUserGroup", user);
		return result.get("groupId");
	}
	
	// AUTHORITIES
	public int createUserAuthorities(User user) throws Exception{
		return 1;
	}
	
	public List<String> getUserAuthorities(User user) throws Exception{
		// job list assigned by user id
		List<Map<String, String>> result = super.findList("findAuthorities", user);
		List<String> list = new ArrayList<String>();
		for(Map<String, String> map : result){
			list.add(map.get("roleId"));
		}
		return list;
	}
	
	// SECURED_RESOURCES_ROLES
	public int createUserSecured(User user) throws Exception{
		return 1;
	}
}
