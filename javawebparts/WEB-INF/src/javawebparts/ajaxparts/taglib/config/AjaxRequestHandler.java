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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class holds configuration information on a request handler defined for
 * a given event of an Ajax-enabled element.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>
 */
public class AjaxRequestHandler {


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
      System.err.println("AjaxRequestHandler" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(AjaxRequestHandler.class);


  /**
   * The type of a handler.
   */
  private String type;


  /**
   * The target a handler submits its request to if it is a request handler
   */
  private String target;


  /**
   * The parameter for a handler.  Meaning of value is handler-specific.
   */
  private String parameter;


  /**
   * The HTTP method for a handler.  Either GET or POST (default).
   */
  private String httpMethod;


  /**
   * Flag: Is the configuration frozen?
   */
  private boolean frozen;


  /**
   * The jsonp parameter to append to the query string, if any.
   */
  private boolean jsonp;


  /**
   * Sets the method of this event handler.
   *
   * @param inHttpMethod The HTTP method of this event handler
   */
  public void setHttpMethod(final String inHttpMethod) {

    if (!frozen) {
      httpMethod = inHttpMethod.toLowerCase();
    }

  } // End setHttpMethod().


  /**
   * Returns the HTTP method of this event handler.
   *
   * @return The HTTP method of this event handler
   */
  public String getHttpMethod() {

    return httpMethod;

  } // End getHttpMethod().


  /**
   * Sets the type of this event handler.
   *
   * @param inType The type of this event handler
   */
  public void setType(final String inType) {

    if (!frozen) {
      type = inType;
    }

  } // End setType().


  /**
   * Returns the type of this event handler.
   *
   * @return The type of this event handler
   */
  public String getType() {

    return type;

  } // End getType().


  /**
   * Sets the target (URL) of this handler.
   *
   * @param inTarget The URL this handler submits to
   */
  public void setTarget(final String inTarget) {

    if (!frozen) {
      target = inTarget;
    }

  } // End setTarget().


  /**
   * Returns the target (URL) of this handler.
   *
   * @return The URL this handler submits to
   */
  public String getTarget() {

    return target;

  } // End getTarget().


  /**
   * Sets the jsonp value of this handler.
   *
   * @param inJsonp True if doing JSON-P, false if not (false by default).
   */
  public void setJsonp(final boolean inJsonp) {

    if (!frozen) {
      jsonp = inJsonp;
    }

  } // End setJsonp().


  /**
   * Returns the value of the jsonp attribute, or false if not explicitly set.
   *
   * @return Whether the request handler is doing JSON-P or not.
   */
  public boolean getJsonp() {

    return jsonp;

  } // End getJsonp().


  /**
   * Sets the handler-specific parameter string for this handler.
   *
   * @param inParameter The parameter string for this handler
   */
  public void setParameter(final String inParameter) {

    if (!frozen) {
      parameter = inParameter;
    }

  } // End setParameter().


  /**
   * Returns the handler-specific parameter string for this handler.
   *
   * @return The parameter string for this handler
   */
  public String getParameter() {

    return parameter;

  } // End getParameter().


  /**
   * Freezes the configuration of this object.
   */
  public void freeze() {

    frozen = validate();

  } // End freeze().


  /**
   * Returns true if this onject's config is frozen, false otherwise.
   *
   * @return True if frozen, false if not.
   */
  public boolean isFrozen() {

    return frozen;

  } // End isFrozen().


  /**
   * This method is called when the object is frozen to ensure it is configured
   * in a legal way.
   *
   * @return True if validation passes, false if not.
   */
  private boolean validate() {

    boolean valid = true;
    if (type == null) {
      log.error("type attribute of <requestHandler> element is required");
      valid = false;
    }
    if (target == null) {
      log.error("target attribute of <requestHandler> element is required");
      valid = false;
    }
    if (httpMethod != null && !httpMethod.equalsIgnoreCase("head") &&
      !httpMethod.equalsIgnoreCase("get") &&
      !httpMethod.equalsIgnoreCase("post") &&
      !httpMethod.equalsIgnoreCase("put") &&
      !httpMethod.equalsIgnoreCase("delete") &&
      !httpMethod.equalsIgnoreCase("trace") &&
      !httpMethod.equalsIgnoreCase("options") &&
      !httpMethod.equalsIgnoreCase("connect")) {
      log.error("httpMethod attribute of <requestHandler> element must be " +
        "one of the values: head, get, post, put, delete, trace, options, " +
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

  } // End toString().


} // End class.