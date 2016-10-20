package org.anyframe.oden.admin.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.inject.Inject;

import org.anyframe.oden.admin.domain.User;
import org.springframework.stereotype.Repository;

import anyframe.common.Page;
import anyframe.core.query.IQueryService;

@Repository("odenUserDao")
public class OdenUserDao{

	@Inject
	IQueryService queryService;
//	public void setQueryService(IQueryService queryService) {
//		super.setQueryService(queryService);
//	}
	
	public Page getUserList() throws Exception{
		Collection collection = queryService.find("findUsersList", new Object[]{});

		Collection result_col = new ArrayList();
		Iterator itr = collection.iterator();
		while(itr.hasNext()){
			User vo = (User)itr.next();
			Object[] iVal = new Object[]{"vo", vo};
			
			//add role name
			ArrayList role_list = (ArrayList)queryService.find("findUserRole", new Object[]{iVal});
			String role = ((HashMap)role_list.get(0)).get("groupName")+"";
			vo.setRole(role);
			
			//add assign job
			ArrayList<HashMap> job_list = (ArrayList)queryService.find("findUserJobList", new Object[]{iVal});
			String assign_job = "";
			
			for(HashMap map : job_list){
				if(map.get("roleId").equals("ROLE_ADMIN")) {
					assign_job = "All Jobs";
					continue;
				}
				assign_job += map.get("roleId") + ", ";
			}
			if(assign_job.length() > 0 && ! assign_job.equals("All Jobs")){
				vo.setJob(assign_job.substring(0, assign_job.length()-2));
			}else{
				vo.setJob(assign_job);
			}

			if(vo.getUserId().equalsIgnoreCase("oden")){
			}else{
				String imgDel = "<img src='images/ico_del.gif' alt='Delete' title='Delete' style='vertical-align:middle;'/>";
				
				String deleteAction = "<a href=\"javascript:deleteUser('" + vo.getUserId() + "');\">" + imgDel + "</a>";
				vo.setHidden(deleteAction);
			}
			result_col.add(vo);
		}
		
		return new Page(result_col, 1, result_col.size(), result_col.size(), result_col.size());
	}

	public User getUser(String id) throws Exception {
		User vo = new User();
		vo.setUserId(id);
		Object[] iVal = new Object[]{"vo", vo};
		
		User user = (User) ((ArrayList)queryService.find("findUsersByPk", new Object[]{iVal})).get(0);
		
		ArrayList role_list = (ArrayList)queryService.find("findUserRole", new Object[]{iVal});
		String role = ((HashMap)role_list.get(0)).get("groupName")+"";
		user.setRole(role);
		
		ArrayList<HashMap> job_list = (ArrayList)queryService.find("findUserJobList", new Object[]{iVal});
		String assign_job = "";
		
		for(HashMap map : job_list){
			assign_job += map.get("roleId") + ", ";
		}
		if(assign_job.length() > 0){
			user.setJob(assign_job.substring(0, assign_job.length()-2));
		}else{
			user.setJob(assign_job);
		}
		
		return user;
	}

	public int createUser(String id, String pw) throws Exception{
		User vo = new User();
		vo.setUserId(id);
		vo.setPassword(pw);
		vo.setUserName(id);
		vo.setEnabled("Y");
		vo.setCreateDate(CommonUtil.getCurrentDate());
		vo.setModifyDate(CommonUtil.getCurrentDate());
		
		Object[] iVal = new Object[]{"vo", vo};
		
		return queryService.create("createUsers", new Object[]{iVal});
	}

	public Collection findGroupByName(String role) throws Exception{
		return queryService.find("findGroupByName", new Object[] {
				new Object[] { "groupName", role } });
	}
	
	public int createGroupUser(String groupId, String id) throws Exception{
		return queryService.create("createGroupUser", new Object[] {
				new Object[] { "groupId", groupId },
				new Object[] { "userId", id },
				new Object[] { "createDate", CommonUtil.getCurrentDate() },
				new Object[] { "modifyDate", CommonUtil.getCurrentDate() }});
	}

	public int createAuthorities(String jobName, String id) throws Exception{
		return queryService.create("createAuthorities", new Object[] {
				new Object[] { "roleId", jobName },
				new Object[] { "subjectId", id },
				new Object[] { "type", "U" },
				new Object[] { "createDate", CommonUtil.getCurrentDate() },
				new Object[] { "modifyDate", CommonUtil.getCurrentDate() }});
	}

	public int updateUser(String id, String pw) throws Exception{
		User vo = new User();
		vo.setUserId(id);
		vo.setPassword(pw);
		vo.setUserName(id);
		vo.setEnabled("Y");
		vo.setCreateDate(CommonUtil.getCurrentDate());
		vo.setModifyDate(CommonUtil.getCurrentDate());
		
		Object[] iVal = new Object[]{"vo", vo};
		
		return queryService.create("updateUsers", new Object[]{iVal});
	}

	public int updateGroupUser(String groupId, String id) throws Exception{
		return queryService.create("updateGroupUser", new Object[] {
				new Object[] { "groupId", groupId },
				new Object[] { "userId", id },
				new Object[] { "createDate", CommonUtil.getCurrentDate() },
				new Object[] { "modifyDate", CommonUtil.getCurrentDate() }});
	}

	public int removeUser(String id) throws Exception{
		return queryService.create("removeUsers", new Object[] {
				new Object[] { "userId", id }});
	}

	public int removeGroupUser(String id) throws Exception{
		return queryService.create("removeGroupUser", new Object[] {
				new Object[] { "userId", id }});
	}
	
	public int removeAuthorities(String id) throws Exception{
		return queryService.create("removeAuthorities", new Object[] {
				new Object[] { "userId", id }});
	}
}
