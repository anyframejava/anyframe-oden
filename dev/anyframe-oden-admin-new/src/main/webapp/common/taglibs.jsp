<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.anyframejava.org/tags" prefix="anyframe" %>
<%@ taglib uri="/WEB-INF/anyframe-iam.tld" prefix="iam" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="package" value="org.anyframe.oden.admin"/>
<c:set var="datePattern"><fmt:message key="date.format"></fmt:message></c:set>
