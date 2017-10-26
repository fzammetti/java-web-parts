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


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import javawebparts.ajaxparts.taglib.config.AjaxConfig;
import javawebparts.ajaxparts.taglib.config.AjaxElement;
import javawebparts.ajaxparts.taglib.config.AjaxErrorHandler;
import javawebparts.ajaxparts.taglib.config.AjaxEvent;
import javawebparts.ajaxparts.taglib.config.AjaxGroup;
import javawebparts.ajaxparts.taglib.config.AjaxHandlerConfig;
import javawebparts.ajaxparts.taglib.config.AjaxRequestHandler;
import javawebparts.ajaxparts.taglib.config.AjaxResponseHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class is a custom tag (AjaxEnableTag) that must be placed on a page
 * AFTER all other Ajax-enabled tags.  Its job is to render Javascript or
 * external reference links for all the handlers required on the page.
 * Each Ajax-enabled tag that fires will add its request, response and error
 * handlers in PageContext.  In this class we iterate over those collections
 * and for each standard handler we render its code, or we render a link to the
 * external .js file for custom handlers, if their location is not local.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>
 */
public class AjaxEnableTag extends TagSupport {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("AjaxEnableTag" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(AjaxEnableTag.class);


  /**
   * Flag: When set to true, the JWPWindowLogger and AjaxPartsTaglib objects
   * will NOT be rendered, only the applicable handlers, and the attach()
   * calls, will be rendered.  This is used when using the taglib on a JSP
   * that is itself rendering a response for an AJAX call.
   */
  private boolean suppress;


  /**
   * What level of debugging should be done?
   */
  private String debug = "ERROR";


  /**
   * What logger implementation to use.
   */
  private String logger = "JWPAlertLogger";


  /**
   * Constructor.
   */
  public AjaxEnableTag() {

    super();

  } // End constructor.


  /**
   * Mutator for logger.
   *
   * @param inLogger New value for logger.
   */
  public void setLogger(final String inLogger) {

    if (logger.equalsIgnoreCase("JWPAlertLogger") ||
      logger.equalsIgnoreCase("JWPWindowLogger")) {
      logger = inLogger;
    } else {
      log.error("logger attribute of enable tag must be one of the values " +
        "'JWPAlertLogger' or 'JWPWindowLogger' (case DOES matter!)");
    }

  } // End setLogger().


  /**
   * Accessor for logger.
   *
   * @return Value of logger.
   */
  public String getLogger() {

    return logger;

  } // End getLogger().


  /**
   * Mutator for debug.
   *
   * @param inDebug New value for debug.
   */
  public void setDebug(final String inDebug) {

    if (!inDebug.equalsIgnoreCase("trace") &&
      !inDebug.equalsIgnoreCase("debug") &&
      !inDebug.equalsIgnoreCase("info") &&
      !inDebug.equalsIgnoreCase("error") &&
      !inDebug.equalsIgnoreCase("fatal")) {
      log.error("debug attribute or enable tag must be one of the following " +
        "values: TRACE, DEBUG. INFO, ERROR, FATAL (case is NOT important)");
      return;
    }

    debug = inDebug;

  } // End setDebug().


  /**
   * Accessor for debug.
   *
   * @return Value of debug.
   */
  public String getDebug() {

    return debug;

  } // End getDebug().


  /**
   * Set the suppress flag of the tag.
   *
   * @param inSuppressFlag suppress flag of the tag
   */
  public void setSuppress(final boolean inSuppressFlag) {

    suppress = inSuppressFlag;

  } // End setSuppress()


  /**
   * Render the results of the tag.
   *
   * @return              Return code.
   * @throws JspException If anything goes wrong
   */
  public int doStartTag() throws JspException {

    log.debug("AjaxEnableTag.doStartTag()...");

    // See if initialization has occurred yet, and if not, do it now.
    if (!AjaxConfig.isFrozen()) {
      log.info("Initializing AjaxParts Taglib...");
      new AjaxInit().init(pageContext.getServletContext());
    }

    // Get logger object code, if not suppressed.
    String jwpLogger = "";
    if (!suppress) {
      jwpLogger = AjaxUtils.getResource(
        "javawebparts/ajaxparts/taglib/resources/" + logger + ".js");
    }

    // Get AjaxPartsTaglib object code, if not suppressed.
    String ajaxPartsTaglib = "";
    if (!suppress) {
      ajaxPartsTaglib = AjaxUtils.getResource(
        "javawebparts/ajaxparts/taglib/resources/AjaxPartsTaglib.js");
    }

    // Perform any required token replacements.
    ajaxPartsTaglib = ajaxPartsTaglib.replaceAll("__CONTEXT__PATH__",
      ((HttpServletRequest)pageContext.getRequest()).getContextPath());
    ajaxPartsTaglib = ajaxPartsTaglib.replaceAll("__DEBUG_LEVEL__",
      debug.toUpperCase());
    ajaxPartsTaglib = ajaxPartsTaglib.replaceAll("__LOGGER__", logger);

    // Get the collections that contains all the error, request and
    // response configuration objects used previously on this JSP.
    HashSet handlersUsed = (HashSet)pageContext.getAttribute(
      "handlersUsed", PageContext.REQUEST_SCOPE);
    log.debug("handlersUsed = " + handlersUsed);
    if (handlersUsed == null) {
      log.info("No handlers are used on this page... nothing to do!");
      return SKIP_BODY;
    }

    // Also get the collection of manual function names and timer parameters.
    HashMap manFuncNames = (HashMap)pageContext.getAttribute(
      "manFuncNames", PageContext.REQUEST_SCOPE);
    HashMap timerParams = (HashMap)pageContext.getAttribute(
      "timerParams", PageContext.REQUEST_SCOPE);

    // Buffers to hold the emitted code for all the handlers.
    StringBuffer handlers = new StringBuffer(2048);
    StringBuffer importedHandlers = new StringBuffer(2048);
    // Buffer to hold all the registration calls
    StringBuffer regHandlerCalls = new StringBuffer(1024);

    // In case there is a std:QueryString, std:SendByID, std:SimpleXML
    // or std:Poster handler we also need the JWPGetElementValue
    if (handlersUsed.contains("std:QueryString") ||
      handlersUsed.contains("std:SendByID") ||
      handlersUsed.contains("std:SimpleXML") ||
      handlersUsed.contains("std:Poster")) {
      handlers.append(AjaxUtils.getResource("javawebparts/ajaxparts/taglib/" +
        "resources/JWPGetElementValue.js") + "\n\n");
    }

    // Iterate over the collection of handlers used.  For each, render the
    // code for the standard handlers, or a JS import for the custom ones.
    for (Iterator it = handlersUsed.iterator(); it.hasNext();) {
      String handlerType = (String)it.next();
      log.debug("About to render handler type '" + handlerType + "'");
      if (handlerType.substring(0, 4).equalsIgnoreCase("std:")) {
        log.debug(handlerType + " is a STD handler");
        String res = AjaxUtils.getResource("javawebparts/ajaxparts/taglib/" +
          "resources/Std" + handlerType.substring(4) + ".js");
        if (res == null) {
          log.error("Unable to render STD handler " + handlerType + "... " +
            "may be a typo in 'type' attribute of one of your config entreis");
        } else {
            AjaxHandlerConfig handler =
              (AjaxHandlerConfig)AjaxConfig.getHandler(handlerType);
            // if it is not an error handler we have to do the registration
            if (!"error".equals(handler.getType())) {
              regHandlerCalls.append(createRegisterCall(handler));
            }
          handlers.append(res + "\n\n");
        }
      } else {
        // It's a custom handler, so get the config info for this handler, and
        // if it isn't local, render a JS import.
        log.debug(handlerType + " is a CUSTOM handler");
        AjaxHandlerConfig handler =
          (AjaxHandlerConfig)AjaxConfig.getHandler(handlerType);
        if (handler == null) {
          log.error("Unable to find configuration for handler '" +
            handlerType + "'... is it configured in the config file?  Does " +
            "the 'type' attribute match what is shown here?");
        } else {
          log.debug("handler = " + handler);
          // if it is not a local custom handler we render a <script> tag and
          // also the registration code (the importedHandlers are added last
          // the page and we need a reference to the function)
          if (!handler.getLocation().equalsIgnoreCase("local")) {
            importedHandlers.append("<script src='" + handler.getLocation() +
            "'></script>\n\n");
            if (!"error".equals(handler.getType())) {
              importedHandlers.append("<script>\n" +
              createRegisterCall(handler) + "</script>");
            }
          } else {
            // if it is not an error handler we have to do the registration
            if (!"error".equals(handler.getType())) {
              regHandlerCalls.append(createRegisterCall(handler));
            }
          }
        }
      }
    }

    // Now, get the collection of ajaxRef's encountered on this JSP.
    ArrayList ajaxRefs = (ArrayList)pageContext.getAttribute(
      "ajaxRefs", PageContext.REQUEST_SCOPE);
    log.debug("ajaxRefs = " + ajaxRefs);

    // These maps will keep track of the error handlers rendered in each scope.
    // This is to avoid duplicates, most usually that can occur when rendering
    // error handlers for a group.
    HashMap groupErrorHandlersRendered = new HashMap();
    HashMap elementErrorHandlersRendered = new HashMap();
    HashMap eventErrorHandlersRendered = new HashMap();

    // Begin the buffers where our cnstructe code will go.
    StringBuffer attachCalls = new StringBuffer(1024);
    StringBuffer manualFuncs = new StringBuffer(1024);
    StringBuffer timerFuncs = new StringBuffer(1024);

    // Now iterate over the collection of ajaxRefs.  For each, get the
    // applicable config information and render the attach() call for it.  Also
    // render all applicable regErrHandler() calls.
    for (Iterator it = ajaxRefs.iterator(); it.hasNext();) {

      String ajaxRef = (String)it.next();

      // Parse the ajaxRef into the group and element parts
      StringTokenizer st = new StringTokenizer(ajaxRef, "/");
      String groupRef = st.nextToken();
      String elementRef  = st.nextToken();
      log.debug("ajaxRef of parent group = " + groupRef);
      log.debug("ajaxRef of element = " + elementRef);

      // Look up group in config.  If not found, abort.
      AjaxGroup aGroup = (AjaxGroup)AjaxConfig.getGroup(groupRef);
      if (aGroup == null) {
        log.error("Trying to render anchor, but couldn't " +
          "find group ajaxRef = " + groupRef);
        return SKIP_BODY;
      }
      log.debug("aGroup = " + aGroup);

      // Look up element in config.  If not found, abort.
      AjaxElement aElement = (AjaxElement)aGroup.getElement(elementRef);
      if (aElement == null) {
        log.error("Trying to render anchor, but couldn't find element " +
          "ajaxRef = " + elementRef);
        return SKIP_BODY;
      }
      log.debug("aElement = " + aElement);

      // Render regErrHandler() calls for the group.
      for (Iterator it1 = aGroup.getErrorHandlers().keySet().iterator();
        it1.hasNext();) {
        String errCode = (String)it1.next();
        if (groupErrorHandlersRendered.get(errCode) != null) {
          break;
        } else {
          groupErrorHandlersRendered.put(errCode, new Object());
        }
        AjaxErrorHandler errorHandler =
          (AjaxErrorHandler)aGroup.getErrorHandlers().get(errCode);
        AjaxHandlerConfig handler =
          (AjaxHandlerConfig)AjaxConfig.getHandler(errorHandler.getType());
        regHandlerCalls.append(createErrorRegisterCall(errorHandler.getCode(),
          groupRef, "", "", handler.getFunction()));
      }

      // Render regErrHandler() calls for the element.
      for (Iterator it1 = aElement.getErrorHandlers().keySet().iterator();
        it1.hasNext();) {
        String errCode = (String)it1.next();
        if (elementErrorHandlersRendered.get(errCode) != null) {
          break;
        } else {
          elementErrorHandlersRendered.put(errCode, new Object());
        }
        AjaxErrorHandler errorHandler =
          (AjaxErrorHandler)aElement.getErrorHandlers().get(errCode);
        AjaxHandlerConfig handler =
          (AjaxHandlerConfig)AjaxConfig.getHandler(errorHandler.getType());
        regHandlerCalls.append(createErrorRegisterCall(errorHandler.getCode(),
          groupRef, elementRef, "", handler.getFunction()));
      }

      // Now iterate over events for this element, render attach() call.
      for (Iterator it1 = aElement.getEvents().keySet().iterator();
        it1.hasNext();) {

        // Get the event and requestHandler.
        String eventType = (String)it1.next();
        AjaxEvent aEvent = (AjaxEvent)aElement.getEvent(eventType);
        AjaxRequestHandler requestHandler = aEvent.getRequestHandler();
        log.debug("Preparing to render attach() call for event: " + aEvent);

        // Render regErrHandler() calls for the event.
        for (Iterator it2 = aEvent.getErrorHandlers().keySet().iterator();
          it2.hasNext();) {
          String errCode = (String)it2.next();
          if (eventErrorHandlersRendered.get(errCode) != null) {
            break;
          } else {
            eventErrorHandlersRendered.put(errCode, new Object());
          }
          AjaxErrorHandler errorHandler =
            (AjaxErrorHandler)aEvent.getErrorHandlers().get(errCode);
          AjaxHandlerConfig handler =
            (AjaxHandlerConfig)AjaxConfig.getHandler(errorHandler.getType());
          regHandlerCalls.append(createErrorRegisterCall(errorHandler.getCode(),
            groupRef, elementRef, aEvent.getType(), handler.getFunction()));
        }

        // The following code builds up the following Javascript call...
        // ajaxPartsTaglib.attach("ajaxRef", "targURI",
        // response.encodeURL("reqHandler"), "reqParam",
        // [ "resHandParams-Type", "resHandParams-resParam",
        // "resHandParams-matchPattern" ], "httpMeth", theForm, "evtType",
        // "preProc", "postProc", async, jsonp);
        attachCalls.append("ajaxPartsTaglib.attach(");
        attachCalls.append("\"" + ajaxRef + "\", ");
        attachCalls.append("\"" +
          ((HttpServletResponse)pageContext.getResponse()).encodeURL(
            requestHandler.getTarget()) + "\", ");
        attachCalls.append("\"" +
          requestHandler.getType().replaceAll("std:", "Std") + "\", ");
        attachCalls.append("\"" + requestHandler.getParameter() + "\", [ ");
        boolean oneDone = false;
        for (Iterator it2 = aEvent.getResponseHandlers().iterator();
          it2.hasNext();) {
          if (oneDone) { attachCalls.append(", "); }
          oneDone = true;
          AjaxResponseHandler responseHandler = (AjaxResponseHandler)it2.next();
          attachCalls.append("\"" +
            responseHandler.getType().replaceAll("std:", "Std") + "\", ");
          attachCalls.append("\"" + responseHandler.getParameter() + "\", ");
          if (responseHandler.getMatchPattern() == null) {
            attachCalls.append("null");
          } else {
            attachCalls.append(responseHandler.getMatchPattern());
          }
        }
        // The next few elements can be defined on the group, element or event
        // levels, and in that order they override each other, so do that now.
        // Note that the default values are defined in the AjaxGroup class.
        String form = AjaxUtils.getScopedForm(aGroup, aElement, aEvent);
        String method = AjaxUtils.getScopedMethod(aGroup, aElement, aEvent);
        String async = AjaxUtils.getScopedAsync(aGroup, aElement, aEvent);
        String preProc = AjaxUtils.getScopedPreProc(aGroup, aElement, aEvent);
        String postProc = AjaxUtils.getScopedPostProc(aGroup, aElement, aEvent);
        attachCalls.append(" ], \"" + method + "\", " + "\"" + form + "\", ");
        attachCalls.append("\"" + aEvent.getType() + "\", ");
        attachCalls.append("\"" + preProc + "\", \"" + postProc + "\", ");
        attachCalls.append(async + ", " + requestHandler.getJsonp() + ");\n\n");

        // If event type is "manual", render function for the developer to call.
        if (aEvent.getType().equalsIgnoreCase("manual")) {
          // Now the function the developer will actually call.
          manualFuncs.append("function " + manFuncNames.get(ajaxRef) + "(){\n");
          manualFuncs.append("  ajaxPartsTaglib.execute(");
          manualFuncs.append("\"" + ajaxRef + aEvent.getType() + "\");\n");
          manualFuncs.append("}\n\n");
        }

        // If event type is "timer", render function for the developer to call.
        if (aEvent.getType().equalsIgnoreCase("timer")) {
          // Now the functions the developer will actually call.
          HashMap params = (HashMap)timerParams.get(ajaxRef);
          timerFuncs.append("  function start" + ajaxRef.replace('/', '_') +
            "() {\n");
          timerFuncs.append("    var evtDef = ajaxPartsTaglib.events[\"" +
            ajaxRef + aEvent.getType() + "\"];\n");
          timerFuncs.append("    evtDef.timerObj = setTimeout(\"" +
            "ajaxPartsTaglib.execute('" + ajaxRef + aEvent.getType() +
            "');\", " + params.get("frequency") + ");\n");
          timerFuncs.append("  }\n\n");
          timerFuncs.append("  function stop" + ajaxRef.replace('/', '_') +
            "() {\n");
          timerFuncs.append("    var evtDef = ajaxPartsTaglib.events[\"" +
            ajaxRef + aEvent.getType() + "\"];\n");
          timerFuncs.append("    clearTimeout(evtDef.timerObj);\n");
          timerFuncs.append("    evtDef.timerObj = null;\n");
          timerFuncs.append("  }\n\n");
          if ("true".equals(params.get("startOnLoad"))) {
            timerFuncs.append("  start" + ajaxRef.replace('/', '_') +
            "(); \n");
          }
        }

      } // End iteration of events.

    } // End iteration of ajaxRefs.

    // Display code to be emitted for debugging purposes.
    log.debug("Ready to emit:" +
      "\n\njwpLogger ............. \n" + jwpLogger +
      "\n\najaxPartsTaglib ....... \n" + ajaxPartsTaglib +
      "\n\nhandlers .............. \n" + handlers.toString() +
      "\n\nattachCalls ........... \n" + attachCalls.toString() +
      "\n\nregErrorHandlerCalls .. \n" + regHandlerCalls.toString() +
      "\n\nmanualFuncs ........... \n" + manualFuncs.toString() +
      "\n\ntimerFuncs ............ \n" + timerFuncs.toString() +
      "\n\nimportedHandlers ...... \n" + importedHandlers.toString() +
      "\n\n");

    // Write out that code.
    try {
      JspWriter out = pageContext.getOut();
      out.print("<script type=\"text/javascript\">\n//<![CDATA[\n" + jwpLogger);
      out.print(ajaxPartsTaglib + handlers.toString());
      out.print(attachCalls.toString() + regHandlerCalls.toString());
      out.print(manualFuncs.toString() + timerFuncs.toString());
      out.print("//]]>\n</script>" + importedHandlers.toString());
    } catch (IOException ioe) {
      throw new JspException(ioe.toString());
    }

    return SKIP_BODY;

  } //  End doStartTag()


  /**
   * Create the registration call for the handler.
   *
   * @param handler the handler configuration.
   * @return String the Call string.
   */
  private String createRegisterCall(AjaxHandlerConfig handler) {
    log.debug("Creating Registration call for handler: " +
            "\n\nName ......... " + handler.getName() +
            "\n\nFunction ..... " + handler.getFunction() +
            "\n\nType ......... " + handler.getType() +
            "\n\n");
    String type = handler.getType().substring(0,1).toUpperCase() +
                    handler.getType().substring(1,3);
    StringBuffer result = new StringBuffer();
    result.append("ajaxPartsTaglib.reg");
    result.append(type);
    result.append("Handler(\"");
    result.append(handler.getName().replaceAll("std:", "Std") + "\", ");
    result.append(handler.getFunction() + ");\n\n");
    return result.toString();
  } // End createRegisterCall()

  /**
   * Create the registration call for the error handler.
   *
   * @param code the error code.
   * @param groupRef the group reference.
   * @param elementRef the element reference.
   * @param type the type of event.
   * @param function the error function.
   * @return String the Call string.
   */
  private String createErrorRegisterCall(String code,
          String groupRef, String elementRef, String type, String function) {
    log.debug("Creating Registration call for error handler: " +
            "\n\nCode ......... " + code +
            "\n\nGroup ........ " + groupRef +
            "\n\nElement ...... " + elementRef +
            "\n\nType ......... " + type +
            "\n\nFunction ..... " + function +
            "\n\n");
    StringBuffer result = new StringBuffer();
    result.append("ajaxPartsTaglib.regErrHandler(");
    result.append("\""     + code       + "\", ");
    result.append("\""     + groupRef   + "\", ");
    result.append("\""     + elementRef + "\", ");
    result.append("\""     + type       + "\", ");
    result.append(function + ");\n\n");
    return result.toString();
  } // End createErrorRegisterCall()

} // End class
