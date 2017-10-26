<%@ page import="java.util.*,java.net.*,java.io.*,javawebparts.request.*,javawebparts.session.*,javawebparts.filter.*,javawebparts.sampleapp.*" %>

<%
  // The following code is used to test the RequestHelpers.getCookieValue()
  // method.
  response.addCookie(new Cookie("myCookie", "Hello from myCookie!"));
%>

<html>
<head>
<title>Java Web Parts Sample Webapp - Home Page</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/styles.css" />
<%@ include file="/js/jumpToAnchor.js" %>
</head>

<body onLoad="jumpToAnchor();">
<%@ include file="/inc/header.inc" %>

  Hello and welcome to the home page of the Java Web Parts sample app!  This
  webapp is meant to do two things... first, to demonstrate all the various
  components available in Java Web Parts so you can see how they can be used,
  and second to help test them.
  <br/><br/>
  Java Web Parts is organized as a series of packages.  Each package is
  distributed individually as its own JAR.  Each of the packages has its own
  sub-page which you can access below by clicking the name of the package you
  are interested in.
  <br/></br>
  In addition, there is the "Everything" page which is basically all the
  package sub-pages blended together.  Note that loading this page may take a
  fair amount of time, so be patient!  This is a valuable page however as it
  demonstrates that there are no conflicts between all the various Java Web
  Parts.
  <br/><br/>
  <a href="./packages_jsp/ajaxparts">javawebparts.ajaxparts Package</a><br/>
  The ajaxparts package contains all AJAX-related parts, including the AjaxParts
  Taglib (formerly AjaxTags) which allows for purely declarative AJAX to be
  added to JSPs with no coding on your part!
  <br/><br/>
  <a href="./packages_jsp/context">javawebparts.context Package</a><br/>
  The context package contains components for working with a ServletContext.
  Things like finding the current size of context can be found there.
  <br/><br/>
  <a href="./packages_jsp/filter">javawebparts.filter Package</a><br/>
  The filter package contains servlet filters for various functions.
  Things like compression, IP access restrictions, concurrent
  session limiting and cross-site scripting protection can be found there.
  <br/><br/>
  <a href="./packages_jsp/listener">javawebparts.listener Package</a><br/>
  The listener package contains listeners, both context and
  session, for various functions.  Things like a simple way to load and make
  available application configuration information can be found there.
  <br/><br/>
  <a href="./packages_jsp/misc">javawebparts.misc Package</a><br/>
  The misc package contains some things that didn't really fit in any other
  package.  Things like the Chain Of Responsibility (CoR) pattern implementation
  can be found here.
  <br/><br/>
  <a href="./packages_jsp/request">javawebparts.request Package</a><br/>
  The request package comtaoms components for dealing with a ServletRequest.
  Things like dumping request parameter/headers/attribute to a log and
  easily getting a cookies can be found there.
  <br/><br/>
  <a href="./packages_jsp/response">javawebparts.response Package</a><br/>
  The response package contains components for dealing with a ServletResponse.
  Things like encoding output as HTML entities can be found there.
  <br/><br/>
  <a href="./packages_jsp/servlet">javawebparts.servlet Package</a><br/>
  The servlet package contains servlets for various functions.  Things
  like a random text rotation servlet can be found there.
  <br/><br/>
  <a href="./packages_jsp/session">javawebparts.session Package</a><br/>
  The session package contains components for working with an HttpSession.
  Things like calculating the size of a session and dumping all attribute
  to a log can be found there.
  <br/><br/>
  <a href="./packages_jsp/taglib">javawebparts.taglib Package</a><br/>
  The taglib package contains taglibs for various functions.  Things like
  a taglib to easily enable AJAX functionality on page elements and a taglib
  for performing simple but common string manipulations can be found there.
  <br/><br/>
  <a href="./packages_jsp/everythingPage">Everything Page</a><br/>
  Remember, this could take a while to load, so give it some time!  More than
  30 seconds probably indicates a problem.
  <br/>

<%@ include file="/inc/footer.inc" %>
</body>
</html>
