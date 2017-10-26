<SCRIPT language="JavaScript" src="../../js/jsForJSCompressionFilter.js"></SCRIPT>
<!-- CacheControlFilter Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="CacheControlFilterTest">CacheControlFilter Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/filter/CacheControlFilter.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  The CacheControlFilter is configured to set cache headers on all requests
  to this webapp, except for the JavaWebParts image above (specified specifically),
  and the following image, or any other .JPGs in general
  (you can verify this by checking the logs):
  <br/><br/>
  <center><img src="<%=request.getContextPath()%>/img/nocacheheaders.jpg" /></center>
  <br/></div><br/><br/>

  <!-- CompressionFilter Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="CompressionFilterTest">CompressionFilter Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/filter/CompressionFilter.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  The following image was retrieved three times, once with no compression,
  once with GZip compression and once with Deflate compression.
  Here is the image, and the number of bytes received in each case:
  <br/><br/>
  <center><img src="<%=request.getContextPath()%>/img/bigimage1.gif" /></center>
  <br/>
  Uncompressed bytes: <%=uncompressedBytes%><br/>
  GZip compressed bytes: <%=compressedGZipBytes%><br/>
  Deflate compressed bytes: <%=compressedDeflateBytes%>
  <br/><br/></div><br/><br/>

  <!-- JSCompressionFilter Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="JSCompressionFilterTest">JSCompressionFilter Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/filter/JSCompressionFilter.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <script>
  function nonsenseJSFunction() {
     alert("hello, this function will not be called");
     alert("hello, this function will not be called");
     alert("hello, this function will not be called");
     alert("hello, this function will not be called");
     for (i=0 ; i<100; i++) {
     	alert("i is " + i);
     }
  }
  </script>
  <div class="sectionContent"><br/>
  This jsp references to a javascript file (just for the sake of this test).
  Also it includes some in-line javascript. Both are compressed with the 
  JSCompressorFilter. 
  Here are the results before compression and after compression:
  (the results are hard coded, check out your log to see the real results)
  <br/>
  Uncompressed bytes for JSP: 15721<br/>
  Compressed bytes for JSP: 15634<br/>
  Uncompressed bytes for .js: 793<br/>
  Compressed bytes for .js: 532<br/>
  <br/><br/></div><br/><br/>
  
  <!-- AppAvailabilityFilter Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="AppAvailabilityFilterTest">AppAvailabilityFilter Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/filter/AppAvailabilityFilter.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  This will test the AppAvailabilityFilter.  To do so, you must set the clock
  on the machine running this webapp to any time between midnight and 6am.
  Then, click the first link below.  The AppAvailabilityFilter will deny the
  request and redirect to another page telling you so.  The second link will
  do the same except that it passes in a parameter telling the filter to
  allow the request regardless of what time it is.
  <br/><br/>
  <a href="<%=request.getContextPath()%>/packages_jsp/filter/AAFTestTarget.jsp">Request that will be denied</a>
  <br/>
  <a href="<%=request.getContextPath()%>/packages_jsp/filter/AAFTestTarget.jsp?bypass=true">Request that should work regardless</a>
  <br/><br/></div><br/><br/>

  <!-- URLRedirectFilter Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="URLRedirectFilter Test">URLRedirectFilter Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/filter/URLRedirectFilter.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  This tests the URLRedirectFilter.  This filter allows you to redirect (or forward)
  an incoming request to another URL based on a mapping file.  To test it, click either of
  the links below.  The first does a redirect to www.google.com, the second does
  a forward to the home page of this application.
  <br/><br/>
  <a href="app/redirect_test/something.htm">Test redirect</a>
  <br/>
  <a href="app/forward/test.htm">Test forward</a>
  <br/></div><br/><br/>

  <!-- ParameterMungerFilter Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="TSETRETLIFREGNUMRETEMARAP">ParameterMungerFilter Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/filter/ParameterMungerFilter.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  This tests the ParameterMungerFilter.  To do so, simply click the Submit button
  below.  The form will be submitted and all parameters will be trimmed,
  converted to upper case and reversed.  The results will be displayed back here.
  <br/><br/>
  <form name="ParameterMungerFilterTestForm" method="post" action="<%=request.getContextPath()%>/parameterMungerFilterTest">
    <input type="hidden" name="srcDoc" value="<%=srcDoc%>"/>
    <table border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td width="200">First Name:&nbsp;</td>
        <td><input type="text" name="firstName" readonly="true" size="25" value="<%=request.getAttribute("firstName")!=null?request.getAttribute("firstName"):"  Frank  "%>"/></td>
      </tr>
      <tr>
        <td>Last Name:&nbsp;</td>
        <td><input type="text" name="lastName" readonly="true" size="25" value="<%=request.getAttribute("lastName")!=null?request.getAttribute("lastName"):"  Zammetti  "%>"/></td>
      </tr>
      <tr>
        <td>Famous Quote:&nbsp;</td>
        <td><input type="text" name="quote" readonly="true" size="70" value="<%=request.getAttribute("quote")!=null?request.getAttribute("quote"):"  If I had a billion dollars, I wouldn't be writing this code!  "%>"/></td>
      </tr>
      <tr><td>&nbsp;</td><td><input type="submit" value="Submit"/></td></tr>
    </table>
  </form>
  <br/></div><br/><br/>

  <!-- ElapsedTimeFilter Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="ElapsedTimeFilterTest">ElapsedTimeFilter Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/filter/ElapsedTimeFilter.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  The ElapsedTimeFilter calculates how long a request takes and logs the elapsed
  time.  Take a look at your logs and you should see the elapsed time for
  every request made to render this page.
  <br/><br/></div><br/><br/>

  <!-- CrossSiteScriptingFilter Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="CrossSiteScriptingFilterTest">CrossSiteScriptingFilter Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/filter/CrossSiteScriptingFilter.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  This tests the CrossSiteScriptingFilter.  To do so, click the button to submit the text which
  includes the "illegal" characters &lt;, &gt;, %, ( and ).  You should be
  thrown out to a reject page as a result.
  <br/><br/>
  <form name="CrossSiteScriptingFilterTestForm" method="post" action="<%=request.getContextPath()%>/crossSiteScriptingTest">
    <table border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td width="200">Some Text:&nbsp;</td>
        <td><input type="text" name="someText" readonly="true" size="50" value="This text (includes) some % bad characters."/></td>
      </tr>
      <tr><td>&nbsp;</td><td><input type="submit" value="Submit"/></td></tr>
    </table>
  </form>
  <br/></div><br/><br/>

  <!-- IPAccessControlFilter Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="IPAccessControlFilter">IPAccessControlFilter Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/filter/IPAccessControlFilter.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  This will test the IPAccessControlFilter.  Click the link below.  The request
  will be rejected unless it comes from the same IP as the server running
  Java Web Parts.
  <br/><br/>
  <a href="<%=request.getContextPath()%>/packages_jsp/filter/IACTestTarget.jsp">Request that will be denied unless the browser and the server are the same IP address</a>
  <br/><br/></div><br/><br/>

  <!-- SessionInactivityFilter Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="SessionInactivityFilterTest">SessionInactivityFilter Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/filter/SessionInactivityFilter.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  The SessionInactivityFilter is used to abort a request when a user's session
  times out.  To test it, click the first link below.  This will set the
  session timeout of your session to 1 second.  Then, on the page that appears,
  click the link to return to this page.  The request should be denied because
  it appears to the filter that your session has been inactive beyond the
  default timeout period of 5 minutes.
  <br/><br/>
  <a href="<%=request.getContextPath()%>/packages_jsp/filter/SIFTestTarget.jsp">Click here to make your session appear to be inactive</a>
  <br/>
  <br/></div><br/><br/>

  <!-- SessionLimiterFilter Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="SessionLimiterFilterTest">SessionLimiterFilter Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/filter/SessionLimiterFilter.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  The SessionLimiterFilter is used to restrict the number of sessions used
  at the same time. The filter is currently configured to allow only one
  session at a time.  To test it, you should access this page from a different session, which can
  be achieved by any of the following:
  <ol>
  	<li>Accessing this page from a different browser</li>
  	<li>Accessing this page from a different computer</li>
  	<li>Closing all windows of your browser, launching it again
  		and accessing this page again.
  	</li>
  </ol>
  Once you try and access the app from another session and see that you are
  stopped, come back to this browser and click the logoff link below, then
  try again from the other browser.  You should then get in.
  <br/><br/>
  <a href="<%=request.getContextPath()%>/packages_jsp/filter/SLFLogoff.jsp">Click here to logoff</a>
  <br/>
  <br/></div><br/><br/>

  <!-- DependencyFilter Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="DependencyFilterTest">DependencyFilter Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/filter/DependencyFilter.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  The DependencyFilter provides a simple and somewhat hybrid IoC capability to
  a web application.  It allows a developer to declare objects to be created
  on a per-request or per-session basis and includes facilities for initializing
  the object in a number of ways, as well as calling arbitrary methods on the
  object for any purpose you like.  The following objects were created by the
  filter and their values displayed below.  Note that the eye color attributes
  are set from the precontent.jsp for the filters package.  This demonstrates
  how the updateDependency() function of the DependencyFilter can be used.
  Also note that MyTestBean3 has a maximum age of 10 seconds, so if you refresh
  this page after 10 seconds you will see a new IQ value because that value is
  randomly set on that bean every time it is created.
  <br/>
  <% boolean didOne = false; %>
  <br/>
  <b>MyTestBean1 created per-request:</b>
  <br/>
  firstName = <%=dfbean1.getFirstName()%>
  <br/>
  lastName = <%=dfbean1.getLastName()%>
  <br/>
  DOB = <%=dfbean1.getDOB()%>
  <br/>
  Children = <% didOne = false; for (int i = 0; i < dfbean1.getChildren().size(); i++) { if (didOne) { out.print(", "); } out.print(dfbean1.getChildren().get(i)); didOne = true; } %>
  <br/>
  Height = <%=dfbean1.getDimensions().get("height")%>
  <br/>
  Weight = <%=dfbean1.getDimensions().get("weight")%>
  <br/>
  Waist = <%=dfbean1.getDimensions().get("waist")%>
  <br/>
  Certifications = <% didOne = false; for (int i = 0; i < dfbean1.getCertifications().length; i++) { if (didOne) { out.print(", "); } out.print(dfbean1.getCertifications()[i]); didOne = true; } %>
  <br/>
  Eye Color = <%=dfbean1.getEyeColor()%>
  <br/>
  IQ = <%=dfbean1.getIq()%>
  <br/><br/>
  <b>MyTestBean2 created per-session:</b>
  <br/>
  firstName = <%=dfbean2.getFirstName()%>
  <br/>
  lastName = <%=dfbean2.getLastName()%>
  <br/>
  DOB = <%=dfbean2.getDOB()%>
  <br/>
  Children = <% didOne = false; for (int i = 0; i < dfbean2.getChildren().size(); i++) { if (didOne) { out.print(", "); } out.print(dfbean2.getChildren().get(i)); didOne = true; } %>
  <br/>
  Height = <%=dfbean2.getDimensions().get("height")%>
  <br/>
  Weight = <%=dfbean2.getDimensions().get("weight")%>
  <br/>
  Waist = <%=dfbean2.getDimensions().get("waist")%>
  <br/>
  Certifications = <% didOne = false; for (int i = 0; i < dfbean2.getCertifications().length; i++) { if (didOne) { out.print(", "); } out.print(dfbean2.getCertifications()[i]); didOne = true; } %>
  <br/>
  Eye Color = <%=dfbean2.getEyeColor()%>
  <br/>
  IQ = <%=dfbean2.getIq()%>
  <br/><br/>
  <b>MyTestBean3 created per-session and "expires" after 10 seconds:</b>
  <br/>
  firstName = <%=dfbean3.getFirstName()%>
  <br/>
  lastName = <%=dfbean3.getLastName()%>
  <br/>
  DOB = <%=dfbean3.getDOB()%>
  <br/>
  Children = <% didOne = false; for (int i = 0; i < dfbean3.getChildren().size(); i++) { if (didOne) { out.print(", "); } out.print(dfbean3.getChildren().get(i)); didOne = true; } %>
  <br/>
  Height = <%=dfbean3.getDimensions().get("height")%>
  <br/>
  Weight = <%=dfbean3.getDimensions().get("weight")%>
  <br/>
  Waist = <%=dfbean3.getDimensions().get("waist")%>
  <br/>
  Certifications = <% didOne = false; for (int i = 0; i < dfbean3.getCertifications().length; i++) { if (didOne) { out.print(", "); } out.print(dfbean3.getCertifications()[i]); didOne = true; } %>
  <br/>
  Eye Color = <%=dfbean3.getEyeColor()%>
  <br/>
  IQ = <%=dfbean3.getIq()%>
  <br/><br/>
  <b>MyTestBean4 created per-request, and only for the path to this page.  To
  verify this, try changing the createForPaths attribute of the applicable
  &lt;dependency&gt; element in the dependencies_config.xml file... just change
  it to gibberish and notice how the bean is not created for this path.</b>
  <br/>
  firstName = <% if(dfbean4!=null){out.println(dfbean4.getFirstName());}else{out.println("BEAN NOT CREATED FOR THIS PATH");} %>
  <br/>
  lastName = <% if(dfbean4!=null){out.println(dfbean4.getLastName());}else{out.println("BEAN NOT CREATED FOR THIS PATH");} %>
  <br/>
  DOB = <% if(dfbean4!=null){out.println(dfbean4.getDOB());}else{out.println("BEAN NOT CREATED FOR THIS PATH");} %>
  <br/>
  Children = <% if(dfbean4!=null){ didOne = false; for (int i = 0; i < dfbean4.getChildren().size(); i++) { if (didOne) { out.print(", "); } out.print(dfbean4.getChildren().get(i)); didOne = true; } }else{out.println("BEAN NOT CREATED FOR THIS PATH");} %>
  <br/>
  Height = <% if(dfbean4!=null){out.println(dfbean4.getDimensions().get("height"));}else{out.println("BEAN NOT CREATED FOR THIS PATH");} %>
  <br/>
  Weight = <% if(dfbean4!=null){out.println(dfbean4.getDimensions().get("weight"));}else{out.println("BEAN NOT CREATED FOR THIS PATH");} %>
  <br/>
  Waist = <% if(dfbean4!=null){out.println(dfbean4.getDimensions().get("waist"));}else{out.println("BEAN NOT CREATED FOR THIS PATH");} %>
  <br/>
  Certifications = <% if(dfbean4!=null){ didOne = false; for (int i = 0; i < dfbean4.getCertifications().length; i++) { if (didOne) { out.print(", "); } out.print(dfbean4.getCertifications()[i]); didOne = true; } }else{out.println("BEAN NOT CREATED FOR THIS PATH"); }%>
  <br/>
  Eye Color = <% if(dfbean4!=null){out.println(dfbean4.getEyeColor());}else{out.println("BEAN NOT CREATED FOR THIS PATH");} %>
  <br/>
  IQ = <% if(dfbean4!=null){out.println(dfbean4.getIq());}else{out.println("BEAN NOT CREATED FOR THIS PATH");} %>
  <br/>
  <br/></div>
