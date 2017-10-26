  <!-- ResponseHelpers.encodeEntities() Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="ResponseHelpersEncodeEntitiesTest">ResponseHelpers.encodeEntities() Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/response/ResponseHelpers.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  The encodeEntities() method of ResponseHelpers takes in a string and encodes
  any characters that have a corresponding HTML entity with that entity.  It
  also allows you to specify characters which will be excluded, that is, NOT
  have entities inserted for them.  When you click the link below, the
  following string will be encoded with entities (except space, which will
  be excluded from encoding):
  <br/><br/>
  <% if (request.getAttribute("encodedString") == null) { %>
  Bill & Ted's Excellent Adventure, © 1989 Some company! ... Some random chars: ä Ä ª æ
  <% } else { %>
  <%="<font color=#ff0000>" + request.getAttribute("encodedString") + "</font>"%>
  <%}%>
  <br/><br/>
  Note that the above may not initially display in your browser correctly because it
  has the characters to be encoded embedded directly, and some browser don't
  like that.  You will have to view source to see the string before encoding as
  well as after, but when it is encoded it will appear in red.
  <br/><br/>
  <a href="<%=request.getContextPath()%>/encodeEntitiesTest?srcDoc=<%=srcDoc%>">Click to encode</a>
  <br/><br/></div>