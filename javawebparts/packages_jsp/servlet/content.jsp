  <!-- TextReturnerServlet Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="TextReturnerServletTest">TextReturnerServlet Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/servlet/TextReturnerServlet.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  This is a test of the TextReturnerServlet.  The following quote was returned
  by the servlet and used in an include.  Each refresh of this page will
  result in another quote being seen.  The quotes are defined in an XML file,
  and all of them will be seen before any repeat.  They can be returned
  randomly (psuedo-randomly really), in forward order or reverse order.  Click
  your browser's Refresh button to see a new quote.
  <br/><br/>
  <jsp:include page="/textReturner" />
  <br/><br/></div><br/><br/>

  <!-- ResourceServerServlet Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="ResourceServerServletServletTest">ResourceServerServlet Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/servlet/ResourceServerServlet.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  The ResourceServerServlet can be used to serve a resource from all kinds of
  different sources.  The server is extensible by implementing a ResourceStream
  interface to deal with other sources.  JWP ships with three implementations:
  ResourceStreamForJAR for serving resources from a JAR file in the classpath,
  ResourceStreamFromFileSystem for serving resources for the local file
  system (context-relative or absolute) and ResourceStreamFromURL for serving
  resources from any URL, local or remote.  The following three images are each
  served by a difference instance of the ResourceServerServlet, the first from
  a JAR, the second from the file system and the last from a URL.  The one from
  the file system uses a mapping file to define the mime type for the resource
  being served.  It also is set to only serve resources defined in the mapping
  file (try taking the URL you see in the source and change singer1.gif to
  another image in the /img folder and note that you don't get the image).
  Note that the last one WILL NOT WORK if you access the Internet via proxy.  If that is
  the case, you will need to modify the servlet init parameters to indicate
  the proxy information
  <br/><br/>
  <center><img src="<%=request.getContextPath()%>/serveResourceFromJAR?res=images/baby1.gif" /></center>
  <br/><br/>
  <center><img src="<%=request.getContextPath()%>/serveResourceFromFileSystem?fsres=img/singer1.gif" /></center>
  <br/><br/>
  <center><img src="<%=request.getContextPath()%>/serveResourceFromURL?ures=http://javawebparts.sourceforge.net/logo.jpg" /></center>
  <br/></div><br/><br/>

  <!-- RedirectReturnerServlet Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="RedirectReturnerServletTest">RedirectReturnerServlet Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/servlet/RedirectReturnerServlet.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  This is a test of the RedirectReturnerServlet.  The following image was returned
  by the servlet and used in an &lt;img&gt; tag.  Each refresh of this page will
  result in another image being seen.  The images are defined in an XML file,
  and all of them will be seen before any repeat.  They can be returned
  randomly (psuedo-randomly really), in forward order or reverse order.
  Although this demonstration shows images, you can actually return any file
  type you like.  In all cases, a redirect is returned to the appropriate resource URL.
  The URIs for each image can be a full URL (beginning with http://, ftp://,
  etc.) or can be local to the webapp it's a part of (begins with / or not).
  Click your browser's Refresh button to see a new image.
  <br/><br/>
  <center><img src="<%=request.getContextPath()%>/redirectReturner" /></center>
  <br/></div><br/><br/>

  <!-- TextRendererServlet Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="TextRendererServletTest">TextRendererServlet Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/servlet/TextRendererServlet.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  This is a test of the TextRendererServlet.  The image you see below was
  dynamically rendered by the servlet.  To play with it, enter some text in
  the box below and click the Render My Text button to see the updated image.
  Note that the border around the image is just a border attribute of the
  &lt;img&gt; tag... it IS NOT rendered by the servlet.  You can also set
  various font characteristics as well.
  <br/><br/>
  <form name="TextRendererServletTest" method="post" action="index.jsp">
    <table border="0" cellpadding="2" cellspacing="2">
      <tr>
        <td width="250">Text to render:</td><td><input type="text" name="text" value="<%=text==null?"":text%>"></td>
      </tr>
      <tr>
        <td>Text color (R,G,B):</td><td><input type="text" name="color" value="<%=color==null?"":color%>"></td>
      </tr>
      <tr>
        <td>Background color (R,G,B):</td><td><input type="text" name="backColor" value="<%=backColor==null?"":backColor%>"></td>
      </tr>
      <tr>
        <td>Font name:</td><td><input type="text" name="fontName" value="<%=fontName==null?"":fontName%>"></td>
      </tr>
      <tr>
        <td>Font point size:</td><td><input type="text" name="fontPoint" value="<%=fontPoint==null?"":fontPoint%>"></td>
      </tr>
      <tr>
        <td>Style:</td>
        <td>
          <input type="checkbox" name="stylePlain" value="true" <%=stylePlain==null?"":"checked"%>>Plain
          &nbsp;
          <input type="checkbox" name="styleBold" value="true" <%=styleBold==null?"":"checked"%>>Bold
          &nbsp;
          <input type="checkbox" name="styleItalic" value="true" <%=styleItalic==null?"":"checked"%>>Italic
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td><td><input type="submit" name="submit" value="Render My Text"></td>
      </tr>
    </table>
  </form>
  <img src="<%=request.getContextPath()%>/textRenderer<%=queryString%>" border="1">
  <br/><br/></div><br/><br/>

  <!-- FormSenderServlet Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="FormSenderServletTest">FormSenderServlet Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/servlet/FormSenderServlet.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  This is a test of the FormSenderServlet.  Fill out the form below, and a form
  letter will be sent which contains the form field values
  inserted into the template.  Be sure to put valid values in web.xml for the
  SMTP server, username, password and toAddress (you can set values for the other config
  items as well if you wish, but those three must be set to something valid for
  this to work).
  <br/><br/>
  <form name="FormSenderServletTest" method="post" action="<%=request.getContextPath()%>/formSender">
    <table border="0" cellpadding="2" cellspacing="2">
      <tr>
        <td>Your First Name:</td><td><input type="text" name="firstName"></td>
      </tr>
      <tr>
        <td>Your Last Name:</td><td><input type="text" name="lastName"></td>
      </tr>
      <tr>
        <td>Your Pet's Name:</td><td><input type="text" name="petName"></td>
      </tr>
      <tr>
        <td>&nbsp;</td><td><input type="submit" name="submit" value="Send eMail"></td>
      </tr>
    </table>
  </form>
  <br/></div>