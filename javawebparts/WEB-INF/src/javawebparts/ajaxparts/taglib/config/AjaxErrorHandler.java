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
 * This class holds configuration information on an error handler.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>
 */
public class AjaxErrorHandler {


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
      System.err.println("AjaxErrorHandler" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(AjaxErrorHandler.class);


  /**
   * The HTTP response code this handler will handle.
   */
  private String code;


  /**
   * The type of the handler (either the name of a custom handler, or a
   * standard handler).
   */
  private String type;


  /**
   * Flag: Is the configuration frozen?
   */
  private boolean frozen;


  /**
   * name mutator.
   *
   * @param inCode Code to set.
   */
  public void setCode(final String inCode) {

    if (!frozen) {
      code = inCode;
    }

  } // End setCode()


  /**
   * code accessor.
   *
   * @return The code.
   */
  public String getCode() {

    return code;

  } // End getCide()


  /**
   * type mutator.
   *
   * @param inType Type to set.
   */
  public void setType(final String inType) {

    if (!frozen) {
      type = inType;
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
    if (code == null) {
      log.error("code attribute of <errorHandler> element is required");
      valid = false;
    }
    if (type == null) {
      log.error("type attribute of <errorHandler> element is required");
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