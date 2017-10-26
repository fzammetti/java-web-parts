<%@ page session="false"%>

<%
  // Although the above directive keeps this JSP from creating a session, the
  // idea of this page is that someone that has a session clicks the logoff
  // link on the main page and is sent here, where we want to log the user
  // off.  So, we get the current session from request and invalidate it.
  // If a request comes here when no session exists, no harm, nothing will
  // be done here.
  HttpSession session = request.getSession(false);
  if (session != null) {
    session.invalidate();
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

  If you are seeing this, it means you have been logged off and your session
  was invalidated.
  <br/><br/>
  Click <a href="<%=request.getContextPath()%>/index.jsp">here</a> to return to the home page.
  <br/>

<%@ include file="/inc/footer.inc" %>
</body>
</html>