  <!-- SessionSize.getSessionSize() Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="SessionSizeGetSessionSizeTest">SessionSize.getSessionSize() Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/session/SessionSize.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  The following three links provide a test of the ability to determine the size
  of a user session.  The first link calls a servlet and puts some objects
  in session and returns the size.  The second does the same, but puts some
  objects in session which are not serializable, so that you can see what
  type of error information the SessionSize class provides in such cases.
  The third also contains non-serializable objects, but those will be ignored
  and the session size will still be calculated (although will be understated
  since the non-serializable objects are not counted).
  <br/>
  <%if(request.getAttribute("sessionSize")!=null){out.print(request.getAttribute("sessionSize"));}%>
  <br/>
  <a href="<%=request.getContextPath()%>/sessionSizeTest?fail=false&srcDoc=<%=srcDoc%>">Test with all serializable objects in session</a>
  <br/>
  <a href="<%=request.getContextPath()%>/sessionSizeTest?fail=true&srcDoc=<%=srcDoc%>">Test with some non-serializable objects in session</a>
  <br/>
  <a href="<%=request.getContextPath()%>/sessionSizeTest?fail=true&ignore=true&srcDoc=<%=srcDoc%>">Test with some non-serializable objects in session but ignore the non-serializable object</a>
  <br/><br/></div><br/><br/>

  <!-- SessionHelpers.getSessionAttributes() Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="SessionHelpersGetSessionAttributesTest">SessionHelpers.getSessionAttributes() Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/session/SessionHelpers.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  This uses the static getAllRequestInfo() method of the RequestHelpers class
  to display all headers, attributes and requests for the request that got
  this page.  Notice how different things are displayed depending on which
  test you perform from this page.
  <br/><br/>
  <i>Session Attributes</i><br/><%=SessionHelpers.getSessionAttributes(request.getSession())%>
  <br/><br/></div>