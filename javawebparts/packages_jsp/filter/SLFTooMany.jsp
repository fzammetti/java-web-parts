<html>
<head>
<title>Java Web Parts Sample Webapp - javawebparts.filter package</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/styles.css" type="text/css">
<%@ include file="/js/jumpToAnchor.js" %>
</head>

<body>
<%@ include file="/inc/header.inc" %>

  If you are seeing this, it means the SessionLimiterFilter determined
  that the maximum number of sessions for this application has been reached.
  You will not be able to access the application from a new session until
  the old one is closed.  To test, hit the logoff link from the Filter package
  page in the browser that represents the other session, then try again below
  and you should be allowed in.
  <br/><br/>
  Click <a href="<%=request.getContextPath()%>/index.jsp">here</a> to return to the home page.
  <br/>

<%@ include file="/inc/footer.inc" %>
</body>
</html>