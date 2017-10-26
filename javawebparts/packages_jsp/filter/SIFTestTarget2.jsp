<%@ page session="false"%>

<%
  HttpSession session = request.getSession(false);
  if (session != null) {
    session.setMaxInactiveInterval(1);
  }
%>

<html>
<head>
<title>Java Web Parts Sample Webapp - javawebparts.filter package</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/styles.css" type="text/css">
<%@ include file="/js/jumpToAnchor.js" %>
</head>

<body>
<%@ include file="/inc/header.inc" %>

  If you are seeing this, it means the SessionInactivityFilter determined
  your session was timed out.
  <br/><br/>
  Click <a href="<%=request.getContextPath()%>/packages_jsp/filter/SIFTestTarget2.jsp">here</a> to test.
  <br/>

<%@ include file="/inc/footer.inc" %>
</body>
</html>