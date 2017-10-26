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
 * This class holds configuration information on a handler, standard or custom.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>
 */
public class AjaxHandlerConfig {


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
      System.err.println("AjaxHandlerConfig" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(AjaxHandlerConfig.class);


  /**
   * The name this handler will be known and reference by.
   */
  private String name;


  /**
   * The type of the handler (request or response).
   */
  private String type;


  /**
   * The Javascript function name this handler will be called as.
   */
  private String function;


  /**
   * Location of Javascript for this handler (local if the developer will
   * include the script himself, or the path to an external .js file)
   */
  private String location;


  /**
   * Is this a standard or custom handler?
   */
  private String stdOrCustom = "custom";


  /**
   * Flag: Is the configuration frozen?
   */
  private boolean frozen;


  /**
   * name mutator.
   *
   * @param inName Name to set.
   */
  public void setName(final String inName) {

    if (!frozen) {
      name = inName;
    }

  } // End setName()


  /**
   * name accessor.
   *
   * @return The name.
   */
  public String getName() {

    return name;

  } // End getName()


  /**
   * type mutator.
   *
   * @param inType Type to set.
   */
  public void setType(final String inType) {

    if (!frozen) {
      if (inType.equalsIgnoreCase("request") ||
        inType.equalsIgnoreCase("response") ||
        inType.equalsIgnoreCase("error")) {
        type = inType.toLowerCase();
      } else {
        log.error("The type attribute of the custom <handler> element in the " +
          "AjaxParts Taglib must have a value of 'request', 'response' " +
          "or 'error' (value found was '" + inType + "'");
      }
    }

  } // End setType()


  /**
   * type accessor.
   *
   * @return The type.
   */
  public String getType() {

    return type;

  } // End getType()


  /**
   * function mutator.
   *
   * @param inFunction Function to set.
   */
  public void setFunction(final String inFunction) {

    if (!frozen) {
      function = inFunction;
    }

  } // End setFunction()


  /**
   * name accessor.
   *
   * @return The function.
   */
  public String getFunction() {

    return function;

  } // End getFunction()


  /**
   * location mutator.
   *
   * @param inLocation Location to set.
   */
  public void setLocation(final String inLocation) {

    if (!frozen) {
      location = inLocation;
    }

  } // End setLocation()


  /**
   * name accessor.
   *
   * @return The location.
   */
  public String getLocation() {

    return location;

  } // End getLocation()


  /**
   * Flags that the handler instance is a standard type.
   */
  public void setSTD() {

    if (!frozen) {
      stdOrCustom = "std";
    }

  } // End setSTD()


  /**
   * Flags that the handler instance is a custom type.
   */
  public void setCustom() {

    if (!frozen) {
      stdOrCustom = "custom";
    }

  } // End setCustom()


  /**
   * Indicates if the handler instance is a standard type.
   *
   * @return True if it is a standard type, false if not.
   */
  public boolean isSTD() {

    return stdOrCustom.equalsIgnoreCase("std");

  } // End isSTD()


  /**
   * Indicates if the handler instance is a custom type.
   *
   * @return True if it is a custom type, false if not.
   */
  public boolean isCustom() {

    return stdOrCustom.equalsIgnoreCase("custom");

  } // End isCustom()


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
    if (name == null) {
      log.error("name attribute of <handler> element is required");
      valid = false;
    }
    if (type == null) {
      log.error("type attribute of <handler> element is required");
      valid = false;
    }
    if (type != null && !type.equalsIgnoreCase("request") &&
      !type.equalsIgnoreCase("response") && !type.equalsIgnoreCase("error")) {
      log.error("type attribute of <handler> element must be one of the " +
        "values request, response, error (must be lower-case)");
      valid = false;
    }
    if (function == null) {
      log.error("function child of <handler> element is required");
      valid = false;
    }
    if (location == null) {
      log.error("location child of <handler> element is required");
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