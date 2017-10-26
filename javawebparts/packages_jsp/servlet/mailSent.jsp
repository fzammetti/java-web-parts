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

The form has been sent as a mail message to the address configured.
<br><br>
Click <a href="<%=request.getContextPath()%>/packages_jsp/servlet">here</a> to return to the servlet test page.
<br>

<%@ include file="/inc/footer.inc" %>
</body>
</html>
