<html>
<head>
<title>Java Web Parts Sample Webapp - javawebparts.filter package</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/styles.css" type="text/css">
<%@ include file="/js/jumpToAnchor.js" %>
</head>

<body>
<%@ include file="/inc/header.inc" %>

  If you are seeing this, it means the IPAccessControlFilter let you
  through because the request came from the same IP address as the server
  running Java Web Parts.
  <br/><br/>
  Click <a href="<%=request.getContextPath()%>/index.jsp">here</a> to return to the home page.
  <br/>

<%@ include file="/inc/footer.inc" %>
</body>
</html>