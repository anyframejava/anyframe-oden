<%@ page language="java" errorPage="/common/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Welcome to Oden</title>
<script type="text/javascript">
function fncLogOut() {
	if(confirm('<anyframe:message code="top.confirm.logout"/>')) {
		document.logoutForm.target="_top";
	    document.logoutForm.action="<c:url value='/logout.do'/>";
	    document.logoutForm.submit();	
    }
}
</script>
<!-- for firefox topmargin compatibility -->
<style type="text/css">
<!--
*{
padding:0;
margin:0;
}
-->
</style>
<link rel="stylesheet" href="<c:url value='/css/left.css'/>" type="text/css">
</head>
<body>
<div id="frame_top"> 
	<form name="logoutForm"></form>
	<table style="width:100%; height:79px;" border="0" cellpadding="0" cellspacing="0" background="<c:url value='/images/bg_topframe.gif'/>">
		<tr>
			<td style="width:227px; height:49px;" align="left"><a href="<c:url value= '/login.do'/>" title="Go Home"><img src="<c:url value='/images/logo.gif'/>" /></a></td>
			<td align="right"><img src="<c:url value='/images/img_top.gif'/>" /></td>
		</tr>
		<tr> 
			<td height="30" colspan="2" align="right">
				<table width="245" border="0" cellpadding="0" cellspacing="0">
					<tr> 
						<td align="right" style="padding-right:8px">Welcome to <%= session.getAttribute("userid") %> ~ !!!</td>
						<td width="62" colspan="2" align="right" style="padding-right:18px">
						<a href="javascript:fncLogOut();"> 
						<img src="<c:url value='/images/btn_logout.gif'/>" alt="로그아웃" width="63" height="15" border="0" /></a></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</div>
</body>
</html>