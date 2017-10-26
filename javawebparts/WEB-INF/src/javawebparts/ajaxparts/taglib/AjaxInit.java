/*
 * Copyright 2005 Frank W. Zammetti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package javawebparts.ajaxparts.taglib;


import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.StringTokenizer;
import javawebparts.ajaxparts.taglib.config.AjaxConfig;
import javawebparts.ajaxparts.taglib.config.AjaxHandlerConfig;
import javawebparts.core.org.apache.commons.digester.Digester;
import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;


/**
 * This class is responsible for all configuration tasks
 * to prepare for Ajax functionality.  It contains two main pieces of
 * functionality: (a) it parses the configuration file named by the
 * ajaxConfig context init parameter, and (b) adds the STD
 * handlers to the configuration information.  This is called from the
 * AjaxEventTag or AjaxEnableTag class, if the AjaxConfig has not been frozen.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>
 */
public class AjaxInit {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javawebparts.core.org.apache.commons.digester.Digester");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("AjaxInit" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(AjaxInit.class);


  /**
   * The container calls this when the context is initialzed.
   *
   * @param sc ServletContext instance reference.
   */
  public void init(ServletContext sc) {

    try {

      // Parse the config file.
      parseConfig(sc);

      // Add STD handlers.
      addSTDHandlers();

      // Freeze the config (all children are already frozen, only this remains).
      AjaxConfig.freeze();

      log.debug("handlers = " + AjaxConfig.getHandlers());
      log.debug("groups = "   + AjaxConfig.getGroups());

    } catch (SAXException se) {
      log.error("SAXException in AjaxInit.contextInitialized(): " + se);
    } catch (IOException ioe) {
      log.error("IOException in AjaxInit.contextInitialized(): " + ioe);
    }

  } // End init()


  /**
   * This method is responsible for parsing the config file.
   *
   * @param  sc           ServletContext reference.
   * @throws SAXException If anything goes wrong.
   * @throws IOException  If anything goes wrong.
   */
  private void parseConfig(ServletContext sc) throws SAXException, IOException {

    Digester digester = new Digester();
    digester.setLogger(log);

    // Determine whether DTD validation will be performed based on config
    // parameter (this was added later, so if it isn't present, that's the
    // same as "true", which maintains backwards compatibility).
    String validateConfig =
      sc.getInitParameter("AjaxPartsTaglibValidateConfig");
    if (validateConfig == null || validateConfig.equalsIgnoreCase("true")) {
      digester.setValidating(true);
    }
    if (validateConfig != null && validateConfig.equalsIgnoreCase("false")) {
      digester.setValidating(false);
    }

    URL dtdurl = this.getClass().getResource(
      "/javawebparts/ajaxparts/taglib/resources/ajax-config.dtd");
    digester.register("ajax-config", dtdurl.toString());

    // ajaxConfig/handler.
    digester.addObjectCreate("ajaxConfig/handler",
      "javawebparts.ajaxparts.taglib.config.AjaxHandlerConfig");
    digester.addSetProperties("ajaxConfig/handler");
    digester.addBeanPropertySetter("ajaxConfig/handler/function", "function");
    digester.addBeanPropertySetter("ajaxConfig/handler/location", "location");
    digester.addSetNext("ajaxConfig/handler", "addHandler",
      "javawebparts.ajaxparts.taglib.config.AjaxHandlerConfig");

    // ajaxConfig/group.
    digester.addObjectCreate("ajaxConfig/group",
      "javawebparts.ajaxparts.taglib.config.AjaxGroup");
    digester.addSetProperties("ajaxConfig/group");
    digester.addSetNext("ajaxConfig/group", "addGroup",
      "javawebparts.ajaxparts.taglib.config.AjaxGroup");

    // ajaxConfig/group/errorHandler.
    digester.addObjectCreate("ajaxConfig/group/errorHandler",
      "javawebparts.ajaxparts.taglib.config.AjaxErrorHandler");
    digester.addSetProperties("ajaxConfig/group/errorHandler");
    digester.addSetNext("ajaxConfig/group/errorHandler", "addErrorHandler",
      "javawebparts.ajaxparts.taglib.config.AjaxErrorHandler");

    // ajaxConfig/group/element.
    digester.addObjectCreate("ajaxConfig/group/element",
      "javawebparts.ajaxparts.taglib.config.AjaxElement");
    digester.addSetProperties("ajaxConfig/group/element");
    digester.addSetNext("ajaxConfig/group/element", "addElement",
      "javawebparts.ajaxparts.taglib.config.AjaxElement");

    // ajaxConfig/group/element/errorHandler.
    digester.addObjectCreate("ajaxConfig/group/element/errorHandler",
      "javawebparts.ajaxparts.taglib.config.AjaxErrorHandler");
    digester.addSetProperties("ajaxConfig/group/element/errorHandler");
    digester.addSetNext("ajaxConfig/group/element/errorHandler",
      "addErrorHandler",
      "javawebparts.ajaxparts.taglib.config.AjaxErrorHandler");

    // ajaxConfig/group/element/event.
    digester.addObjectCreate("ajaxConfig/group/element/event",
      "javawebparts.ajaxparts.taglib.config.AjaxEvent");
    digester.addSetProperties("ajaxConfig/group/element/event");
    digester.addSetNext("ajaxConfig/group/element/event", "addEvent",
      "javawebparts.ajaxparts.taglib.config.AjaxEvent");

    // ajaxConfig/group/element/event/errorHandler.
    digester.addObjectCreate("ajaxConfig/group/element/event/errorHandler",
      "javawebparts.ajaxparts.taglib.config.AjaxErrorHandler");
    digester.addSetProperties("ajaxConfig/group/element/event/errorHandler");
    digester.addSetNext("ajaxConfig/group/element/event/errorHandler",
      "addErrorHandler",
      "javawebparts.ajaxparts.taglib.config.AjaxErrorHandler");

    // ajaxConfig/group/element/event/requestHandler.
    digester.addObjectCreate("ajaxConfig/group/element/event/requestHandler",
      "javawebparts.ajaxparts.taglib.config.AjaxRequestHandler");
    digester.addSetProperties("ajaxConfig/group/element/event/requestHandler");
    digester.addBeanPropertySetter(
      "ajaxConfig/group/element/event/requestHandler/target", "target");
    digester.addBeanPropertySetter(
      "ajaxConfig/group/element/event/requestHandler/parameter", "parameter");
    digester.addSetNext("ajaxConfig/group/element/event/requestHandler",
      "setRequestHandler",
      "javawebparts.ajaxparts.taglib.config.AjaxRequestHandler");

    // ajaxConfig/group/element/event/responseHandler.
    digester.addObjectCreate("ajaxConfig/group/element/event/responseHandler",
      "javawebparts.ajaxparts.taglib.config.AjaxResponseHandler");
    digester.addSetProperties("ajaxConfig/group/element/event/responseHandler");
    digester.addBeanPropertySetter(
      "ajaxConfig/group/element/event/responseHandler/parameter", "parameter");
    digester.addSetNext("ajaxConfig/group/element/event/responseHandler",
      "addResponseHandler",
      "javawebparts.ajaxparts.taglib.config.AjaxResponseHandler");

    // Parse config
    String          configFiles = sc.getInitParameter("AjaxPartsTaglibConfig");
    StringTokenizer st          = new StringTokenizer(configFiles, ",");
    AjaxConfig      ajaxConfig  = new AjaxConfig();
    while (st.hasMoreTokens()) {
      String configFile = st.nextToken();
      log.info("Reading AjaxParts Taglib config file " + configFile);
      InputStream stream = null;
      digester.push(ajaxConfig);
      try {
        stream = sc.getResourceAsStream(configFile);
        digester.parse(stream);
      } finally {
        if (stream != null) {
          try {
            stream.close();
          } catch (IOException ioe) {
            log.error("Exception closing config file stream: " + ioe);
          }
        }
      }
    }

  } // End parseConfig()


  /**
   * This method is responsible for adding all the STD handlers to
   * the configuration.
   */
  private void addSTDHandlers() {

    // stdSimpleRequest handler.
    AjaxHandlerConfig stdSimpleRequestHandler = new AjaxHandlerConfig();
    stdSimpleRequestHandler.setName("std:SimpleRequest");
    stdSimpleRequestHandler.setType("request");
    stdSimpleRequestHandler.setFunction("StdSimpleRequest");
    stdSimpleRequestHandler.setLocation("local");
    stdSimpleRequestHandler.setSTD();
    stdSimpleRequestHandler.freeze();
    AjaxConfig.addHandler(stdSimpleRequestHandler);

    // stdQueryString handler.
    AjaxHandlerConfig stdQueryStringHandler = new AjaxHandlerConfig();
    stdQueryStringHandler.setName("std:QueryString");
    stdQueryStringHandler.setType("request");
    stdQueryStringHandler.setFunction("StdQueryString");
    stdQueryStringHandler.setLocation("local");
    stdQueryStringHandler.setSTD();
    stdQueryStringHandler.freeze();
    AjaxConfig.addHandler(stdQueryStringHandler);

    // stdPoster handler.
    AjaxHandlerConfig stdPoster = new AjaxHandlerConfig();
    stdPoster.setName("std:Poster");
    stdPoster.setType("request");
    stdPoster.setFunction("StdPoster");
    stdPoster.setLocation("local");
    stdPoster.setSTD();
    stdPoster.freeze();
    AjaxConfig.addHandler(stdPoster);

    // stdSimpleXML handler.
    AjaxHandlerConfig stdSimpleXML = new AjaxHandlerConfig();
    stdSimpleXML.setName("std:SimpleXML");
    stdSimpleXML.setType("request");
    stdSimpleXML.setFunction("StdSimpleXML");
    stdSimpleXML.setLocation("local");
    stdSimpleXML.setSTD();
    stdSimpleXML.freeze();
    AjaxConfig.addHandler(stdSimpleXML);

    // stdSendByID handler.
    AjaxHandlerConfig stdSendByID = new AjaxHandlerConfig();
    stdSendByID.setName("std:SendByID");
    stdSendByID.setType("request");
    stdSendByID.setFunction("StdSendByID");
    stdSendByID.setLocation("local");
    stdSendByID.setSTD();
    stdSendByID.freeze();
    AjaxConfig.addHandler(stdSendByID);

    // stdInnerHTML handler.
    AjaxHandlerConfig stdInnerHTMLHandler = new AjaxHandlerConfig();
    stdInnerHTMLHandler.setName("std:InnerHTML");
    stdInnerHTMLHandler.setType("response");
    stdInnerHTMLHandler.setFunction("StdInnerHTML");
    stdInnerHTMLHandler.setLocation("local");
    stdInnerHTMLHandler.setSTD();
    stdInnerHTMLHandler.freeze();
    AjaxConfig.addHandler(stdInnerHTMLHandler);

    // stdCodeExecuter handler.
    AjaxHandlerConfig stdCodeExecuter = new AjaxHandlerConfig();
    stdCodeExecuter.setName("std:CodeExecuter");
    stdCodeExecuter.setType("response");
    stdCodeExecuter.setFunction("StdCodeExecuter");
    stdCodeExecuter.setLocation("local");
    stdCodeExecuter.setSTD();
    stdCodeExecuter.freeze();
    AjaxConfig.addHandler(stdCodeExecuter);

    // stdTextboxArea handler.
    AjaxHandlerConfig stdTextboxArea = new AjaxHandlerConfig();
    stdTextboxArea.setName("std:TextboxArea");
    stdTextboxArea.setType("response");
    stdTextboxArea.setFunction("StdTextboxArea");
    stdTextboxArea.setLocation("local");
    stdTextboxArea.setSTD();
    stdTextboxArea.freeze();
    AjaxConfig.addHandler(stdTextboxArea);

    // stdXSLT handler.
    AjaxHandlerConfig stdXSLTHandler = new AjaxHandlerConfig();
    stdXSLTHandler.setName("std:XSLT");
    stdXSLTHandler.setType("response");
    stdXSLTHandler.setFunction("StdXSLT");
    stdXSLTHandler.setLocation("local");
    stdXSLTHandler.setSTD();
    stdXSLTHandler.freeze();
    AjaxConfig.addHandler(stdXSLTHandler);

    // stdDoNothing handler.
    AjaxHandlerConfig stdDoNothing = new AjaxHandlerConfig();
    stdDoNothing.setName("std:DoNothing");
    stdDoNothing.setType("response");
    stdDoNothing.setFunction("StdDoNothing");
    stdDoNothing.setLocation("local");
    stdDoNothing.setSTD();
    stdDoNothing.freeze();
    AjaxConfig.addHandler(stdDoNothing);

    // stdSelectbox handler.
    AjaxHandlerConfig stdSelectboxHandler = new AjaxHandlerConfig();
    stdSelectboxHandler.setName("std:Selectbox");
    stdSelectboxHandler.setType("response");
    stdSelectboxHandler.setFunction("StdSelectbox");
    stdSelectboxHandler.setLocation("local");
    stdSelectboxHandler.setSTD();
    stdSelectboxHandler.freeze();
    AjaxConfig.addHandler(stdSelectboxHandler);

    // stdAlerter handler.
    AjaxHandlerConfig stdAlerter = new AjaxHandlerConfig();
    stdAlerter.setName("std:Alerter");
    stdAlerter.setType("response");
    stdAlerter.setFunction("StdAlerter");
    stdAlerter.setLocation("local");
    stdAlerter.setSTD();
    stdAlerter.freeze();
    AjaxConfig.addHandler(stdAlerter);

    // stdIFrameDisplay handler.
    AjaxHandlerConfig stdIFrameDisplay = new AjaxHandlerConfig();
    stdIFrameDisplay.setName("std:IFrameDisplay");
    stdIFrameDisplay.setType("response");
    stdIFrameDisplay.setFunction("StdIFrameDisplay");
    stdIFrameDisplay.setLocation("local");
    stdIFrameDisplay.setSTD();
    stdIFrameDisplay.freeze();
    AjaxConfig.addHandler(stdIFrameDisplay);

    // stdFormManipulator handler.
    AjaxHandlerConfig stdFormManipulator = new AjaxHandlerConfig();
    stdFormManipulator.setName("std:FormManipulator");
    stdFormManipulator.setType("response");
    stdFormManipulator.setFunction("StdFormManipulator");
    stdFormManipulator.setLocation("local");
    stdFormManipulator.setSTD();
    stdFormManipulator.freeze();
    AjaxConfig.addHandler(stdFormManipulator);

    // stdWindowOpener handler.
    AjaxHandlerConfig stdWindowOpener = new AjaxHandlerConfig();
    stdWindowOpener.setName("std:WindowOpener");
    stdWindowOpener.setType("response");
    stdWindowOpener.setFunction("StdWindowOpener");
    stdWindowOpener.setLocation("local");
    stdWindowOpener.setSTD();
    stdWindowOpener.freeze();
    AjaxConfig.addHandler(stdWindowOpener);

    // stdRedirecter handler.
    AjaxHandlerConfig stdRedirecter = new AjaxHandlerConfig();
    stdRedirecter.setName("std:Redirecter");
    stdRedirecter.setType("response");
    stdRedirecter.setFunction("StdRedirecter");
    stdRedirecter.setLocation("local");
    stdRedirecter.setSTD();
    stdRedirecter.freeze();
    AjaxConfig.addHandler(stdRedirecter);

    // stdAlertErrorHandler handler.
    AjaxHandlerConfig stdAlertErrorHandler = new AjaxHandlerConfig();
    stdAlertErrorHandler.setName("std:AlertErrorHandler");
    stdAlertErrorHandler.setType("error");
    stdAlertErrorHandler.setFunction("StdAlertErrorHandler");
    stdAlertErrorHandler.setLocation("local");
    stdAlertErrorHandler.setSTD();
    stdAlertErrorHandler.freeze();
    AjaxConfig.addHandler(stdAlertErrorHandler);

    // stdFormSender handler.
    AjaxHandlerConfig stdFormSender = new AjaxHandlerConfig();
    stdFormSender.setName("std:FormSender");
    stdFormSender.setType("request");
    stdFormSender.setFunction("StdFormSender");
    stdFormSender.setLocation("local");
    stdFormSender.setSTD();
    stdFormSender.freeze();
    AjaxConfig.addHandler(stdFormSender);

  } // End addStdHandlers().


} // End class.