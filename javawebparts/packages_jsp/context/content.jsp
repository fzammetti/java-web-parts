  <!-- ContextSize.getContextSize() Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="ContextSizeGetContextSizeTest">ContextSize.getContextSize() Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/context/ContextSize.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  The following three links provide a test of the ability to determine the size
  of a servlet context.  The first link calls a servlet and puts some objects
  in context and returns the size.  The second does the same, but puts some
  objects in context which are not serializable, so that you can see what
  type of error information the ContextSize class provides in such cases.
  The third also contains non-serializable objects, but those will be ignored
  and the context size will still be calculated (although will be understated
  since the non-serializable objects are not counted).
  <br/>
  <%if(request.getAttribute("contextSize")!=null){out.print(request.getAttribute("contextSize"));}%>
  <br/>
  <a href="<%=request.getContextPath()%>/contextSizeTest?fail=false&srcDoc=<%=srcDoc%>">Test with all serializable objects in context</a>
  <br/>
  <a href="<%=request.getContextPath()%>/contextSizeTest?fail=true&srcDoc=<%=srcDoc%>">Test with some non-serializable objects in context</a>
  <br/>
  <a href="<%=request.getContextPath()%>/contextSizeTest?fail=true&ignore=true&srcDoc=<%=srcDoc%>">Test with some non-serializable objects in context but ignore the non-serializable object</a>
  <br/><br/></div>