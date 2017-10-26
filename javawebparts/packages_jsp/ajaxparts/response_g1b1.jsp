<%@ taglib prefix="ajax" uri="javawebparts/ajaxparts/taglib" %>
<html>
<head>
<title></title>
</head>
<body>


<input id="dynaButton" type="button" value="Now click me for alert" onclick="alert('Existing onClick!');"><ajax:event ajaxRef="TestGroup3/Button1"/>

<ajax:enable suppress="true" />

</body></html>
<%System.out.println("\n\n\n\n\n-----> response_g1b1.jsp\n\n");%>