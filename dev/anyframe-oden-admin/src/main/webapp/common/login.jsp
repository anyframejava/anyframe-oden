<%@ page contentType="text/html; charset=euc-kr" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.anyframejava.org/tags" prefix="anyframe" %>
<%@ page import="java.lang.String" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Welcome to Oden</title>
<meta http-equiv="content-type" content="text/html; charset=euc-kr">
<link rel="stylesheet" href="<c:url value='/css/admin.css'/>" type="text/css">

<style type="text/css">
<!--
body {
	background-color: #FFFFFF;
	margin-left: 0px;
	margin-top: 100px;
	margin-right: 0px;
	margin-bottom: 0px;
}

button {text-indent: -9999px; font-size: 0px; cursor: pointer; border:none;}
-->
</style>
<script language="javascript" src="<c:url value='/js/CommonScript.js'/>"></script>
<script language="JavaScript">
function fncLogin() {
	if(FormValidation(document.loginForm) != false) {
		document.loginForm.flag.value="L";
	    document.loginForm.action="<c:url value='/j_spring_security_check'/>";
	    document.loginForm.submit();
    }
}
</script>

</head>

<body>

<div id="login_wrap">
	<div id="login_content">
	<form id="loginForm" name="loginForm" method="post" action="<c:url value='/j_spring_security_check'/>">
	<fieldset>
	<legend>login id,pw</legend>
	<img src="<c:url value='/images/login.jpg'/>" alt="oden login" />
		<div class="login_info">
				<p class="uid"><label class="hidden" for="id">아이디</label><input type='text' name='j_username' <c:if test="${not empty param.login_error  || not empty exception}">value='<c:out value="${SPRING_SECURITY_LAST_USERNAME}"/>'</c:if> class='ct_input_g' tabindex="1" style="width:100px; height:15px; vertical-align:middle;" maxLength='50'><input type="hidden" name="flag"></p>
				<p class="upw"><label class="hidden" for="enpw">비밀번호 </label><input type="password" name="j_password" class="ct_input_g" tabindex="2" style="width:100px; height:15px; vertical-align:middle;" maxLength="50" onKeyPress="if(event.keyCode==13) fncLogin();"> 
				<button type="submit" id="loginBtn" tabindex="3" title="로그인" onclick="javascript:fncLogin();"></button></p>
		</div>
	

	<!--START: issue-->
	<c:if test="${not empty param.login_error || not empty exception}">
		<div class="issue" style="display:;">
			<table summary="issue">
					<tr>								
						<td width="25" valign="top"><img src="<c:url value='/images/ico_issue.gif'/>" width="20" height="18" alt="noti" /></td>
						<td><anyframe:message code="login.message"/> 
							<c:if test="${not empty param.login_error}"><c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/></c:if>
							<c:if test="${not empty exception}"><c:out value="${exception.message}"/></c:if>
						</td>
					</tr>
			</table>
		</div>
	</c:if>
	<!--END: issue-->
	</fieldset>
	</form>
	</div>
</div>
<script language="JavaScript">
	document.loginForm.j_username.focus();
</script>
</body>
</html>
