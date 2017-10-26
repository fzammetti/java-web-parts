<%@ page import="java.util.*,java.net.*,java.io.*,javawebparts.request.*,javawebparts.session.*,javawebparts.filter.*,javawebparts.misc.chain.*,javawebparts.sampleapp.*" %>

<%@ include file="/packages_jsp/filter/precontent.jsp" %>
<%@ include file="/packages_jsp/servlet/precontent.jsp" %>

<% String srcDoc = "everything"; %>

<html>
<head>
<title>Java Web Parts Sample Webapp - Eveything Page</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/styles.css" type="text/css">
<%@ include file="/js/jumpToAnchor.js" %>
</head>

<body onLoad="jumpToAnchor();">
<%@ include file="/inc/header.inc" %>

<%@ include file="/packages_jsp/ajaxparts/content.jsp" %>
<br/><br/>
<%@ include file="/packages_jsp/context/content.jsp" %>
<br/><br/>
<%@ include file="/packages_jsp/filter/content.jsp" %>
<br/><br/>
<%@ include file="/packages_jsp/listener/content.jsp" %>
<br/><br/>
<%@ include file="/packages_jsp/misc/content.jsp" %>
<br/><br/>
<%@ include file="/packages_jsp/request/content.jsp" %>
<br/><br/>
<%@ include file="/packages_jsp/response/content.jsp" %>
<br/><br/>
<%@ include file="/packages_jsp/servlet/content.jsp" %>
<br/><br/>
<%@ include file="/packages_jsp/session/content.jsp" %>
<br/><br/>
<%@ include file="/packages_jsp/taglib/content.jsp" %>

<%@ include file="/inc/footer.inc" %>
</body>
</html>