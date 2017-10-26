  <!-- AppConfigContextListener Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="AppConfigContextListenerTest">AppConfigContextListener Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/listener/AppConfigContextListener.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  The AppConfigContextListener allows a developer to easily populate a bean
  from an XML configuration file when a contect (webapp) starts up.  It can
  essentially operate in three modes: Basic, Intermediate and Advanced.  In
  the Basic mode, the developer has only to specify an XML file and what
  the root element is.  As long as the XML file is "flat", i.e., elements are
  only nested under the root, a bean named AppConfig in the javawebparts.listener
  package will be populated and the values can be grabbed with
  AppConfig.get(key).  In Intermediate mode, the developer specifies a bean
  that they themselves supply to be populated.  The only requirement here
  is that the class follows the Javabean specification, most notably that
  the setters must be non-static.  The XML file must still be "flat" in this
  mode as well.  In Advanced mode, the developer supplies
  not only a custom bean class, but a class which implements the
  AppConfigRuleset interface from the javawebparts.listener package.  This interface
  provides a single method, in which rules are set on the referenced Commons
  Digester object instance.  This allows for much more complex config file
  formats to be supported.  Below is an example of the Intermediate mode,
  where a custom bean class was specified and the XML file in WEB-INF
  read in:
  <br/><br/>
  Item1 = <%=SampleAppConfigBean.getItem1()%><br/>
  Item2 = <%=SampleAppConfigBean.getItem2()%><br/>
  Item3 = <%=SampleAppConfigBean.getItem3()%>
  <br/><br/></div>