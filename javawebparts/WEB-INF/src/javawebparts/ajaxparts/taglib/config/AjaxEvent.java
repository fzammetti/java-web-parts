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


package javawebparts.ajaxparts.taglib.config;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class holds configuration information on each event for an Ajax-enabled
 * element that has been defined as implementing Ajax functionality.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>
 */
public class AjaxEvent {


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
      System.err.println("AjaxEvent" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(AjaxEvent.class);


  /**
   * The type of an event.
   */
  private String type;


  /**
   * The request handler defined for an event.
   */
  private AjaxRequestHandler requestHandler;


  /**
   * The list of response handlers defined for an event.
   */
  private ArrayList responseHandlers = new ArrayList();


  /**
   * The form this event operates on, if any.
   */
  private String form;


  /**
   * The HTTP method the request handler for this event will use.
   */
  private String method;


  /**
   * Flag to determine if this request to be asynchronous or not.  Note: must be
   * a string, rather than the more logical boolean, otherwise overriding
   * wouldn't work properly.
   */
  private String async;


  /**
   * Collection of error handlers for this event.
   */
  private HashMap errorHandlers = new HashMap();


  /**
   * The preprocessor this event will use.
   */
  private String preProc;


  /**
   * The postprocessor this event will use.
   */
  private String postProc;


  /**
   * Flag to determine if the configuration frozen.
   */
  private boolean frozen;


  /**
   * Mutator for preProc.
   *
   * @param inPreProc New value for preProc.
   */
  public void setPreProc(final String inPreProc) {

    if (!frozen) {
      preProc = inPreProc;
    }

  } // End setPreProc().


  /**
   * Accessor for preProc.
   *
   * @return Value of preProc.
   */
  public String getPreProc() {

    return preProc;

  } // End getPreProc().


  /**
   * Mutator for postProc.
   *
   * @param inPostProc New value for postProc.
   */
  public void setPostProc(final String inPostProc) {

    if (!frozen) {
      postProc = inPostProc;
    }

  } // End setPostProc().


  /**
   * Accessor for postProc.
   *
   * @return Value of postProc.
   */
  public String getPostProc() {

    return postProc;

  } // End getPostProc().


  /**
   * Sets the form this event operates on.
   *
   * @param inForm The form this event operates on.
   */
  public void setForm(final String inForm) {

    if (!frozen) {
      form = inForm;
    }

  } // End setForm().


  /**
   * Returns the form this event operates on.
   *
   * @return The form this event operates on.
   */
  public String getForm() {

    return form;

  } // End getForm().


  /**
   * Sets the HTTP method this event's request handler will use.
   *
   * @param inMethod The HTTP method this event's request handler will use.
   */
  public void setMethod(final String inMethod) {

    if (!frozen) {
      method = inMethod.toLowerCase();
    }

  } // End setMethod().


  /**
   * Returns the HTTP method this event's request handler will use.
   *
   * @return The HTTP method this event's request handler will use.
   */
  public String getMethod() {

    return method;

  } // End getMethod().


  /**
   * Sets the type of the event.
   *
   * @param inType The type of the event
   */
  public void setType(final String inType) {

    if (!frozen) {
      type = inType.toLowerCase();
    }

  } // End setType()


  /**
   * Returns the type of the event.
   *
   * @return The type of the event
   */
  public String getType() {

    return type;

  } // End getType()


  /**
   * Sets whether this request is asynchronous or not.
   *
   * @param inAsync True if request is asynchronous, false if not.
   */
  public void setAsync(final String inAsync) {

    if (!frozen) {
      async = inAsync.toLowerCase();
    }

  } // End setAsync()


  /**
   * Returns whether the request is asynchronous or not.
   *
   * @return True if the request is asynchronous, false if not.
   */
  public String getAsync() {

    return async;

  } // End getAsync()


  /**
   * Sets the AjaxRequestHandler instance that will serve as the request handler
   * for this event.
   *
   * @param inRequestHandler AjaxRequestHandler instance
   */
  public void setRequestHandler(final AjaxRequestHandler inRequestHandler) {

    if (!frozen) {
      inRequestHandler.freeze();
      requestHandler = inRequestHandler;
    }

  } // End setRequestHandler()


  /**
   * Returns the AjaxRequestHandler instance that serves as the request handler
   * for this event.
   *
   * @return AjaxRequestHandler instance
   */
  public AjaxRequestHandler getRequestHandler() {

    return requestHandler;

  } // End AjaxRequestHandler()


  /**
   * Adds an AjaxResponseHandler instance that will serve as one of the response
   * handlers for this event.
   *
   * @param inResponseHandler AjaxResponseHandler instance
   */
  public void addResponseHandler(AjaxResponseHandler inResponseHandler) {

    if (!frozen) {
      inResponseHandler.freeze();
      responseHandlers.add(inResponseHandler);
    }

  } // End addResponseHandler()


  /**
   * Returns the list of AjaxRequestHandler instances that serves as the
   * response handlers for this event.
   *
   * @return The list of response handlers.
   */
  public ArrayList getResponseHandlers() {

    return responseHandlers;

  } // End getResponseHandlers()


  /**
   * Puts an error handler in the collection.
   *
   * @param errorHandler The error handler to put in the collection.
   */
  public void addErrorHandler(AjaxErrorHandler errorHandler) {

    if (!frozen) {
      errorHandler.freeze();
      errorHandlers.put(errorHandler.getCode(), errorHandler);
    }

  } // End addErrorHandler().


  /**
   * Gets an error handler from the collection.
   *
   * @param  inCode The HTTP response code of the error handler to retrieve.
   * @return           The corresponding AjaxErrorHandler.
   */
  public AjaxErrorHandler getErrorHandler(String inCode) {

    return (AjaxErrorHandler)errorHandlers.get(inCode);

  } // End getErrorHandler().


  /**
   * Gets the collection of error handlers for this event.
   *
   * @return The collection of error handlers for this event.
   */
  public HashMap getErrorHandlers() {

    return errorHandlers;

  } // End getErrorHandlers().


  /**
   * Freezes the configuration of this object.
   */
  public void freeze() {

    frozen = validate();

  } // End freeze()


  /**
   * Returns true if this onject's config is frozen, false otherwise.
   *
   * @return True if frozen, false if not.
   */
  public boolean isFrozen() {

    return frozen;

  } // End isFrozen()


  /**
   * This method is called when the object is frozen to ensure it is configured
   * in a legal way.
   *
   * @return True if validation passes, false if not.
   */
  private boolean validate() {

    boolean valid = true;
    if (type == null) {
      log.error("type attribute of <event> element is required");
      valid = false;
    }
    if (form != null && form.equalsIgnoreCase("")) {
      log.error("form attribute cannot be set to a blank string");
      valid = false;
    }
    if (method != null && method.equalsIgnoreCase("")) {
      log.error("method attribute cannot be set to a blank string");
      valid = false;
    }
    if (async != null && async.equalsIgnoreCase("")) {
      log.error("async attribute cannot be set to a blank string");
      valid = false;
    }
    if (requestHandler == null) {
      log.error("A request handler is mandatory for all events");
      valid = false;
    }
    if (responseHandlers.size() == 0) {
      log.error("At least one response handler is mandatory for all events");
      valid = false;
    }
    if (method != null && !method.equalsIgnoreCase("head") &&
      !method.equalsIgnoreCase("get") &&
      !method.equalsIgnoreCase("post") &&
      !method.equalsIgnoreCase("put") &&
      !method.equalsIgnoreCase("delete") &&
      !method.equalsIgnoreCase("trace") &&
      !method.equalsIgnoreCase("options") &&
      !method.equalsIgnoreCase("connect")) {
      log.error("method attribute of <event> element must be one of " +
        "the values: head, get, post, put, delete, trace, options, " +
        "connect (must be lower-case)");
      valid = false;
    }
    return valid;

  } // End validate().


  /**
   * Overriden toString method.
   *
   * @return A reflexively-built string representation of this bean
   */
  public String toString() {

    String str = null;
    StringBuffer sb = new StringBuffer(1000);
    sb.append("[" + super.toString() + "]={");
    boolean firstPropertyDisplayed = false;
    try {
      Field[] fields = this.getClass().getDeclaredFields();
      for (int i = 0; i < fields.length; i++) {
        if (firstPropertyDisplayed) {
          sb.append(", ");
        } else {
          firstPropertyDisplayed = true;
        }
        sb.append(fields[i].getName() + "=" + fields[i].get(this));
      }
      sb.append("}");
      str = sb.toString().trim();
    } catch (IllegalAccessException iae) { iae.printStackTrace(); }
    return str;

  } // End toString()


} // End class