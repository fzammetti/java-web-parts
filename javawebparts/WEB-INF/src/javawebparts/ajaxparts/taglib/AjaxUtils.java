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


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import javawebparts.ajaxparts.taglib.config.AjaxElement;
import javawebparts.ajaxparts.taglib.config.AjaxErrorHandler;
import javawebparts.ajaxparts.taglib.config.AjaxEvent;
import javawebparts.ajaxparts.taglib.config.AjaxGroup;
import javawebparts.ajaxparts.taglib.config.AjaxResponseHandler;
import javawebparts.core.org.apache.commons.lang.StringUtils;
import javax.servlet.jsp.PageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class contains utility functions used throughout the taglib.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>
 */
public final class AjaxUtils {


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(AjaxUtils.class);


  /**
   * This is a utility class, so we want a private noarg constructor so
   * instances cannot be created.
   */
  private AjaxUtils() {
  } // End AjaxUtils().


  /**
   * This method returns a String from a resource loaded from the
   * classpath, including within a JAR file.
   *
   * @param  inResName The name of the resource to load.  This should be a path
   *                   specifier WITHOUT a leading slash.
   * @return           A String of the request resource, or null if the
   *                   resource is not found.
   */
  public static String getResource(String inResName) {

    return stringFromInputStream(getStream(inResName));

  } // End getResource().


  /**
   * This method returns an InputStream on a resource loaded from the classpath,
   * including within a JAR file.
   *
   * @param  inResName The name of the resource to load.  This should be a path
   *                   specifier WITHOUT a leading slash.
   * @return           An InputStream on the request resource, or null if the
   *                   resource is not found.
   */
  private static InputStream getStream(String inResName) {

    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    InputStream is = loader.getResourceAsStream(inResName);
    if (is == null) {
      log.error("Resource '" + inResName + "' not found in classpath");
    }
    return is;

  } // End getStream().


  /**
   * This method takes as input an InputStream and returns it as a
   * String.
   *
   * @param  inIS The InputStream to "convert".
   * @return      The InputStream as a String, or null if the
   *              InputStream passed in was null.
   */
  private static String stringFromInputStream(InputStream inIS) {

    if (inIS == null) {
      return null;
    }
    StringBuffer      outBuffer = new StringBuffer();
    InputStreamReader isr       = null;
    BufferedReader    input     = null;
    try {
      String line = null;
      isr = new InputStreamReader(inIS);
      input = new BufferedReader(isr);
      while ((line = input.readLine()) != null) {
        if (line.indexOf("//") == -1) {
          outBuffer.append(line);
          outBuffer.append(System.getProperty("line.separator"));
        }
      }
    } catch (IOException ioe) {
      log.error("Unable to read from InputStream or write to output buffer");
      ioe.printStackTrace();
      outBuffer = null;
    }
    try {
      isr.close();
      input.close();
      inIS.close();
    } catch (IOException ioe) {
      log.error("InputStream could not be closed");
      ioe.printStackTrace();
    }
    if (outBuffer == null) {
      return null;
    } else {
      return outBuffer.toString();
    }

  } // End stringFromInputStream().


  /**
   * This method validates that a string purported to be an ajaxRef is in
   * the form xxxx/yyyy.
   *
   * @param  ajaxRef The purported ajaxRef to validate.
   * @return         True if it is in the proper xxxx/yyyy form, false if not.
   */
  public static boolean validateAjaxRef(String ajaxRef) {

    // Make sure the ajaxRef is present and in an appropriate form.
    boolean retVal = true;
    if (ajaxRef == null || StringUtils.countMatches(ajaxRef, "/") != 1) {
      retVal = false;
    }
    return retVal;

  } // End validateAjaxRef().


  /**
   * This method takes an ajaxRef (which is assumed to be in the correct
   * format) and returns just the group's ajaxRef.
   *
   * @param  ajaxRef The ajaxRef.
   * @return         The group ajaxRef.
   */
  public static String groupRefFromAjaxRef(String ajaxRef) {

    StringTokenizer st = new StringTokenizer(ajaxRef, "/");
    return st.nextToken();

  } // End groupRefFromAjaxRef().


  /**
   * This method takes an ajaxRef (which is assumed to be in the correct
   * format) and returns just the element's ajaxRef.
   *
   * @param  ajaxRef The ajaxRef.
   * @return         The element ajaxRef.
   */
  public static String elementRefFromAjaxRef(String ajaxRef) {

    StringTokenizer st = new StringTokenizer(ajaxRef, "/");
    st.nextToken();
    return st.nextToken();

  } // End elementRefFromAjaxRef().


  /**
   * This method updates the two page-scope collections that is built up as
   * event, manual and timer tags are encountered and which store all the
   * ajaxREFs and handlers (request, response oe error) encountered on the JSP.
   *
   * @param ajaxRef     The ajaxRef of the tag being handled.
   * @param pageContext The PageContext of the JSP being rendered.
   * @param aGroup      The AjaxGroup instance that is the parent of the
   *                    element referenced by the tag that called this.
   * @param aElement    The AjaxElement instance that of the element referenced
   *                    by the tag that called this.
   */
  public static void updatePageScopeVars(String ajaxRef,
    PageContext pageContext, AjaxGroup aGroup, AjaxElement aElement) {

    // Get (or create) the page-scoped collection of ajaxRef's encountered
    // on the JSP, and add the current one to it.
    ArrayList ajaxRefs = (ArrayList)pageContext.getAttribute(
      "ajaxRefs", PageContext.REQUEST_SCOPE);
    if (ajaxRefs == null) {
      ajaxRefs = new ArrayList();
    }
    ajaxRefs.add(ajaxRef);
    pageContext.setAttribute("ajaxRefs", ajaxRefs, PageContext.REQUEST_SCOPE);

    // Get (or create) the page-scoped collection of handlers used on this JSP.
    HashSet handlersUsed = (HashSet)pageContext.getAttribute(
      "handlersUsed", PageContext.REQUEST_SCOPE);
    if (handlersUsed == null) {
      handlersUsed = new HashSet();
    }

    // First, add the error handlers used by this element's parent group.
    for (Iterator it = aGroup.getErrorHandlers().keySet().iterator();
      it.hasNext();) {
      String code = (String)it.next();
      AjaxErrorHandler errorHandler =
        (AjaxErrorHandler)aGroup.getErrorHandler(code);
      handlersUsed.add(errorHandler.getType());
    }

    // Now, add the error handlers used by this element.
    for (Iterator it = aElement.getErrorHandlers().keySet().iterator();
      it.hasNext();) {
      String code = (String)it.next();
      AjaxErrorHandler errorHandler =
        (AjaxErrorHandler)aElement.getErrorHandler(code);
      handlersUsed.add(errorHandler.getType());
    }

    // Now, add the request handler used by this element.
    for (Iterator it = aElement.getEvents().keySet().iterator();
      it.hasNext();) {
      String type = (String)it.next();
      AjaxEvent aEvent = (AjaxEvent)aElement.getEvent(type);
      handlersUsed.add(aEvent.getRequestHandler().getType());
    }

    // Now, add the response handlers and error handler used by each of the
    // events configured for this element.
    for (Iterator it = aElement.getEvents().keySet().iterator();
      it.hasNext();) {
      String type = (String)it.next();
      AjaxEvent aEvent = (AjaxEvent)aElement.getEvent(type);
      ArrayList responseHandlers = aEvent.getResponseHandlers();
      for (Iterator it1 = responseHandlers.iterator(); it1.hasNext();) {
        AjaxResponseHandler responseHandler = (AjaxResponseHandler)it1.next();
        handlersUsed.add(responseHandler.getType());
      }
      for (Iterator it2 = aEvent.getErrorHandlers().keySet().iterator();
        it2.hasNext();) {
        String code = (String)it2.next();
        AjaxErrorHandler errorHandler =
          (AjaxErrorHandler)aEvent.getErrorHandler(code);
        handlersUsed.add(errorHandler.getType());
      }
    }

    // Put the updated collection of handlers in scope.
    pageContext.setAttribute("handlersUsed", handlersUsed,
      PageContext.REQUEST_SCOPE);

  } // End updatePageScopeVars().


  /**
   * This method returns the form for a given event based on scope rules.
   * This value can be defined on the group, element or event levels, and in
   * that order they override each other.  Note that the default values are
   * defined in the AjaxGroup class.
   *
   * @param  aGroup   The AjaxGroup that is parent to this event.
   * @param  aElement The AjaxElement that is parent to this event.
   * @param  aEvent   The AjaxEvent that is parent to this event.
   * @return          The value for the form attribute.
   */
  public static String getScopedForm(AjaxGroup aGroup, AjaxElement aElement,
    AjaxEvent aEvent) {

    String form = aGroup.getForm();
    if (aElement.getForm() != null) {
      form = aElement.getForm();
    }
    if (aEvent.getForm() != null) {
      form = aEvent.getForm();
    }
    return form;

  } // End getScopedForm().


  /**
   * This method returns the method for a given event based on scope rules.
   * This value can be defined on the group, element or event levels, and in
   * that order they override each other.  Note that the default values are
   * defined in the AjaxGroup class.
   *
   * @param  aGroup   The AjaxGroup that is parent to this event.
   * @param  aElement The AjaxElement that is parent to this event.
   * @param  aEvent   The AjaxEvent that is parent to this event.
   * @return          The value for the form attribute.
   */
  public static String getScopedMethod(AjaxGroup aGroup, AjaxElement aElement,
    AjaxEvent aEvent) {

    String method = aGroup.getMethod();
    if (aElement.getMethod() != null) {
      method = aElement.getMethod();
    }
    if (aEvent.getMethod() != null) {
      method = aEvent.getMethod();
    }
    return method;

  } // End getScopedMethod().


  /**
   * This method returns the async for a given event based on scope rules.
   * This value can be defined on the group, element or event levels, and in
   * that order they override each other.  Note that the default values are
   * defined in the AjaxGroup class.
   *
   * @param  aGroup   The AjaxGroup that is parent to this event.
   * @param  aElement The AjaxElement that is parent to this event.
   * @param  aEvent   The AjaxEvent that is parent to this event.
   * @return          The value for the form attribute.
   */
  public static String getScopedAsync(AjaxGroup aGroup, AjaxElement aElement,
    AjaxEvent aEvent) {

    String async = aGroup.getAsync();
    if (aElement.getAsync() != null) {
      async = aElement.getAsync();
    }
    if (aEvent.getAsync() != null) {
      async = aEvent.getAsync();
    }
    return async;

  } // EndgetScopedAsync().


  /**
   * This method returns the preProc for a given event based on scope rules.
   * This value can be defined on the group, element or event levels, and in
   * that order they override each other.  Note that the default values are
   * defined in the AjaxGroup class.
   *
   * @param  aGroup   The AjaxGroup that is parent to this event.
   * @param  aElement The AjaxElement that is parent to this event.
   * @param  aEvent   The AjaxEvent that is parent to this event.
   * @return          The value for the form attribute.
   */
  public static String getScopedPreProc(AjaxGroup aGroup, AjaxElement aElement,
    AjaxEvent aEvent) {

    String preProc = aGroup.getPreProc();
    if (aElement.getPreProc() != null) {
      preProc = aElement.getPreProc();
    }
    if (aEvent.getPreProc() != null) {
      preProc = aEvent.getPreProc();
    }
    return preProc;

  } // End getScopedPreProc().


  /**
   * This method returns the postPRoc for a given event based on scope rules.
   * This value can be defined on the group, element or event levels, and in
   * that order they override each other.  Note that the default values are
   * defined in the AjaxGroup class.
   *
   * @param  aGroup   The AjaxGroup that is parent to this event.
   * @param  aElement The AjaxElement that is parent to this event.
   * @param  aEvent   The AjaxEvent that is parent to this event.
   * @return          The value for the form attribute.
   */
  public static String getScopedPostProc(AjaxGroup aGroup, AjaxElement aElement,
    AjaxEvent aEvent) {

    String postProc = aGroup.getPostProc();
    if (aElement.getPostProc() != null) {
      postProc = aElement.getPostProc();
    }
    if (aEvent.getPostProc() != null) {
      postProc = aEvent.getPostProc();
    }
    return postProc;

  } // End getScopedPostProc().


} // End class.