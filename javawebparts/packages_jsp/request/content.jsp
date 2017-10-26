  <!-- RequestHelpers.getAllRequestInfo() Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="RequestHelpersGetAllRequestInfoTest">RequestHelpers.getAllRequestInfo() Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/request/RequestHelpers.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  This uses the static getAllRequestInfo() method of the RequestHelpers class
  to display all headers, attributes and requests for the request that got
  this page.  Notice how different things are displayed depending on which
  test you perform from this page.
  <br/><br/>
  <% Map hm = RequestHelpers.getAllRequestInfo(request); %>
  <i>Request Headers</i><br/><%=hm.get("headers")%><br/><br/>
  <i>Request Attributes</i><br/><%=hm.get("attributes")%><br/><br/>
  <i>Request Parameters</i><br/><%=hm.get("parameters")%>
  <br/><br/></div><br/><br/>

  <!-- RequestHelpers.getCookieValue() Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="RequestHelpersGetCookieValueTest">RequestHelpers.getCookieValue() Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/request/RequestHelpers.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  This will test the getCookieValue() method of the RequestHelpers class.
  It allows you to get the value of a named cookie.  A cookie named
  myCookie was set in this JSP, and its value is displayed below.  Note that
  getCookieValue() calls getCookie(), which is another method of
  RequestHelpers you can use, so this effectively demonstrates and tests both.
  Also shown  below is the getCookies() method listing all cookies.
  <br/><br/>
  myCookie = <%=RequestHelpers.getCookieValue(request, "myCookie")%>
  <br/>
  All Cookies = <%=RequestHelpers.getCookies(request)%>
  <br/><br/></div><br/><br/>

  <!-- RequestHelpers.generateGUID() Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="RequestHelpersGenerateGUID">RequestHelpers.generateGUID() Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/request/RequestHelpers.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  This will test the generateGUID() method of the RequestHelpers class.
  This generates a 32 character string that uniquely identifies an incoming
  request.  This can be helpful for instance when logging messages in a busy
  webapp to keep track of what log statements belong to what requests.
  <br/><br/>
  GUID = <%=RequestHelpers.generateGUID(request)%>
  <br/><br/></div>