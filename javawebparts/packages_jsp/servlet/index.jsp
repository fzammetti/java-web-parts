<%@ page import="java.util.*,java.net.*,java.io.*,javawebparts.request.*,javawebparts.session.*,javawebparts.filter.*,javawebparts.sampleapp.*" %>

<%@ include file="/packages_jsp/servlet/precontent.jsp" %>

<% String srcDoc = ""; %>

<html>
<head>
<title>Java Web Parts Sample Webapp - javawebparts.servlet package</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/styles.css" type="text/css">
<%@ include file="/js/jumpToAnchor.js" %>
</head>

<body onLoad="jumpToAnchor();">
<%@ include file="/inc/header.inc" %>

<%@ include file="/packages_jsp/servlet/content.jsp" %>

<%@ include file="/inc/footer.inc" %>
</body>
</html>
