<%@ taglib prefix="ajax" uri="javawebparts/ajaxparts/taglib"%>

<html>
<head>
<title>Java Web Parts Cookbook: Dynamic Double Select</title>
</head>
<body>
Java Web Parts Cookbook
<br />
Dynamically adding lines 
<br />
<br />
This sample shows how to dynamically add a line with fields 
depending on a user event using APT. In this example the added 
lines also include an event which in it's turn uses APT.
So, this jsp file includes another jsp file (formLine.jsp) in which 
there are two selectboxes and one button. When the value in the 
first selectbox changes the second one gets populated (by means 
of APT, every time the servers gets called, there is another option added). 
When the button is clicked there is a call to the server 
(again, by means of APT) which does nothing more than to return 
a jsp (which is in fact again the formLine.jsp). Both of the calls
will be done using the &lt;ajax:manual&gt; tags and custom response 
handlers. The reason to use the AppendInnerHTML custom response handler 
is simple, there is no equivalent in APT, so a custom handler is a must here).
The reason for the CustomSelectBox custom response handler is that the newly 
added lines (from formLine.jsp) need unique id's which need to be used by 
the response handler. The CustomSelectBox handler is a copy from the StdSelectbox 
handler except for a fieldSuffix (the unique part of the id's). Please take a 
look at the code and if it is not clear, drop us a line in the forum. 
<br />
<br />
<form name="InputForm" method="post" action="#" id="SomeID">
<div id="formDetails">
<%@ include file="formLine.jsp"%>  
</div>
</form>
<ajax:manual ajaxRef="InputForm/TypeList" function="typeSelected"/>
<ajax:manual ajaxRef="InputForm/MoreButton" function="moreButtonClicked"/>
<ajax:enable debug="debug" logger="JWPWindowLogger" />
</body>
</html>
