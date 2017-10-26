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


package javawebparts.listener;


import java.lang.reflect.Field;
import java.util.HashMap;
import javawebparts.core.org.apache.commons.digester.Digester;


/**
 * This class holds configuration information.  It is a simple
 * implementation that is the default for this listener if a custom class
 * is not specified.  Note that this does NOT support multiple elements with
 * the same name, nor does it support anything but a flat config file
 * format!
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class AppConfig {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javawebparts.core.org.apache.commons.digester.Digester");
    } catch (ClassNotFoundException e) {
      System.err.println("AppConfig" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * The configuration map.
   */
  private static HashMap configMap = new HashMap();


  /**
   * Reference to Digester populating this object.
   */
  private Digester digester;


  /**
   * This must be called before the object can be populated.  It stores a
   * reference to the Digester populating it.  Needed to get element names.
   *
   * @param inDigester Reference to Digester populating this object.
   */
  public void setDigester(Digester inDigester) {

    digester = inDigester;

  } // End setDigester().


  /**
   * Set an item in the configMap.
   *
   * @param val The value to store.
   */
  public void set(String val) {

    configMap.put(digester.getCurrentElementName(), val);

  } // End set().


  /**
   * The configuration map.
   *
   * @param  key The key of the value to retrieve, null if not found.
   * @return     The value for the requested key, or null if key not found.
   */
  public static String get(String key) {

    return (String)configMap.get(key);

  } // End get().


  /**
   * Overriden toString method.
   *
   * @return A reflexively-built string representation of this bean.
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
    } catch (IllegalAccessException iae) {
      iae.printStackTrace();
    }
    return str;

  } // End toString().


} // End AppConfig class.
