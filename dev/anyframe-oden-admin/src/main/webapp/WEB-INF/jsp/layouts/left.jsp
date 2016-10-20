<%@ page language="java" errorPage="/common/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/common/taglibs.jsp"%>
<%
	String roles = (String) session.getAttribute("userrole");
%>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Welcome to Oden</title>
<link rel="stylesheet" href="<c:url value='/css/admin.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/css/left.css'/>" type="text/css">
<style type="text/css">
<!--
*{
padding:0;
margin:0;
}
-->
</style>
</head>
<body>
<div id="left">
	<table style="height:100%;" border="0" cellpadding="0" cellspacing="0" >
		<tr>
			<td valign="top">
				<table width="65" border="0" cellpadding="0" cellspacing="0" bgcolor="#FFFFFF">
					<tr>
						<td valign="top" bgcolor="#eeeeee" class="depth01">
						   <a href="javascript:fn_addTab('03job', 'Job', '', '&amp;initdataService=groupService.findGroupAndUngroup()&amp;initdataResult=groupUngroups', 'ALL');"></a>
						</td>
					</tr>
					<tr>
						<td valign="top" bgcolor="#eeeeee" class="depth02">
						   <a href="javascript:fn_addTab('04history', 'History','','','&amp;initdataService=historyService.findJob(role)&amp;initdataResult=jobs&amp;role=<%= session.getAttribute("userrole") %>');"></a>
						</td>
					</tr>
					<tr>
						<td valign="top" bgcolor="#eeeeee" class="depth03">
						   <a href="javascript:fn_addTab('06status', 'Status');"></a>
						</td>
					</tr>
					<iam:access hasPermission="${iam:getPermissionMask(\"CREATE\")}" viewName="addUser">
						<tr>
							<td valign="top" bgcolor="#eeeeee" class="depth04">
							   <a href="javascript:fn_addTab('05log', 'Log','','&amp;initdataService=logService.findList(cmd)&amp;initdataResult=logs');"></a>
							</td>
						</tr>
						<tr>
							<td valign="top" bgcolor="#eeeeee" class="depth05">
							   <a href="javascript:fn_addTab('07user', 'User');"></a>
							</td>
						</tr>
					</iam:access>
				</table>
			</td>
		</tr>
	</table>
</div>
</body>
</html>