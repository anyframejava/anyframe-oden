<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>
<body>
<c:choose>
	<c:when test="${param.flag == 'L'}">
    	<jsp:forward page="/login.do"/>
    </c:when>
    <c:otherwise>
    	<jsp:forward page="/common/login.jsp"/>	   
    </c:otherwise>
</c:choose>
</body>
</html>