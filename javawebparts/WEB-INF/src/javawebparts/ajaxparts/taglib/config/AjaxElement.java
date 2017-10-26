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
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class holds configuration information on each element of a form
 * that has been Ajax-enabled.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>
 */
public class AjaxElement {


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
      System.err.println("AjaxElement" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(AjaxElement.class);


  /**
   * Ajax reference ID.
   */
  private String ajaxRef;


  /**
   * The name of the form child events will operate on.
   */
  private String form;


  /**
   * The HTTP method child events will use.
   */
  private String method;


  /**
   * Flag to determine if child event requests be asynchronous.  Note: must be
   * a string, rather than the more logical boolean, otherwise overriding
   * wouldn't work properly.
   */
  private String async;


  /**
   * The preprocessor this event will use.
   */
  private String preProc;


  /**
   * The postprocessor this event will use.
   */
  private String postProc;


  /**
   * The collection of events defined for an element.
   */
  private HashMap events = new HashMap();


  /**
   * Collection of error handlers for this element.
   */
  private HashMap errorHandlers = new HashMap();


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
   * Sets the ajaxRef for the element.
   *
   * @param inAjaxRef The ajaxRef top assign to this element
   */
  public void setAjaxRef(final String inAjaxRef) {

    if (!frozen) {
      ajaxRef = inAjaxRef;
    }

  } // End setAjaxRef()


  /**
   * Returns the ajaxRef for the element.
   *
   * @return The ajaxRef for the element
   */
  public String getAjaxRef() {

    return ajaxRef;

  } // End getAjaxRef()


  /**
   * Mutator for form.
   *
   * @param inForm New value for form.
   */
  public void setForm(final String inForm) {

    if (!frozen) {
      form = inForm;
    }

  } // End setForm().


  /**
   * Accessor for form.
   *
   * @return Value of form.
   */
  public String getForm() {

    return form;

  } // End getForm().


  /**
   * Mutator for method.
   *
   * @param inMethod New value for method.
   */
  public void setMethod(final String inMethod) {

    if (!frozen) {
      method = inMethod.toLowerCase();
    }

  } // End setMethod().


  /**
   * Accessor for method.
   *
   * @return Value of method.
   */
  public String getMethod() {

    return method;

  } // End getMethod().


  /**
   * Mutator for async.
   *
   * @param inAsync New value for async.
   */
  public void setAsync(final String inAsync) {

    if (!frozen) {
      async = inAsync.toLowerCase();
    }

  } // End setAsync().


  /**
   * Accessor for async.
   *
   * @return Value of async.
   */
  public String getAsync() {

    return async;

  } // End getAsync().


  /**
   * Adds an AjaxEvent instance to the collection for the element.
   *
   * @param event The AjaxEvenet instance to add
   */
  public void addEvent(AjaxEvent event) {

    if (!frozen) {
      event.freeze();
      events.put(event.getType(), event);
    }

  } // End addEvent()


  /**
   * Returns the AjaxEvent instance for the named UI event.
   *
   * @param  type The event type.
   * @return      The AjaxEvent for the names UI event
   */
  public AjaxEvent getEvent(String type) {

    return (AjaxEvent)events.get(type);

  } // End getEvent()


  /**
   * Returns the collection of AjaxEvent instances for this element.
   *
   * @return The collection of AjaxEvent instances for this element.
   */
  public HashMap getEvents() {

    return events;

  } // End getEvents()


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
   * Gets the collection of error handlers for this element.
   *
   * @return The collection of error handlers for this element.
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
    if (ajaxRef == null) {
      log.error("ajaxRef attribute of <element> element is required");
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
    if (method != null && !method.equalsIgnoreCase("head") &&
      !method.equalsIgnoreCase("get") &&
      !method.equalsIgnoreCase("post") &&
      !method.equalsIgnoreCase("put") &&
      !method.equalsIgnoreCase("delete") &&
      !method.equalsIgnoreCase("trace") &&
      !method.equalsIgnoreCase("options") &&
      !method.equalsIgnoreCase("connect")) {
      log.error("method attribute of <element> element must be one of " +
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