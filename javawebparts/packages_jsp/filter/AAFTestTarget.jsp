<html>
<head>
<title>Java Web Parts Sample Webapp - javawebparts.filter package</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/styles.css" type="text/css">
<%@ include file="/js/jumpToAnchor.js" %>
</head>

<body>
<%@ include file="/inc/header.inc" %>

  If you are seeing this, it means the AppAvailabilityFilter let you
  through, either because the clock on the system running this webapp
  was NOT set to a time between midnight and 6am, or because you clicked the
  second link to override the filter.
  <br/><br/>
  Click <a href="<%=request.getContextPath()%>/index.jsp">here</a> to return to the home page.
  <br/>

<%@ include file="/inc/footer.inc" %>
</body>
</html>