<%@ page import="java.util.*" %>

<html>
  <head>
    <title>ajaxIFrameTest</title>
  </head>
  <body style="font-size:10pt;">
    Contents of List in request:
    <br><br>
    <%
      List theList = (List)request.getAttribute("theList");
      for (Iterator it = theList.iterator(); it.hasNext();) {
        String next = (String)it.next();
        out.println(next + "<br>");
      }
    %>
  </body>
</html>