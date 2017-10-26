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


package javawebparts.session;


import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class contains static methods that are too small to warrant their
 * own class.  General session-related utility methods.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>
 */
public final class SessionHelpers {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javax.servlet.http.HttpSession");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("SessionHelpers" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(SessionHelpers.class);


  /**
   * This is a utility class, so we want a private noarg constructor so
   * instances cannot be created.
   */
  private SessionHelpers() {
  } // End SessionHelpers().


  /**
   * This method is a convenience method that dumps all attributes to the log.
   *
   * @param session A valid HTTPSession object.
   */
  public static void logSessionAttributes(HttpSession session) {

    if (session != null) {
      log.info("logSessionAttributes: Attributes = " +
        getSessionAttributes(session));
    }

  } // End logSessionAttributes().


  /**
   * This method will return a Map that can be displayed which contains
   * all session attributes.
   *
   * @param  session A valid HttpSession object.
   * @return         A Map of all the attributes of the session object.
   *                 Returns null if session is null.
   */
  public static Map getSessionAttributes(HttpSession session) {

    HashMap attributes = null;
    if (session != null) {
      attributes = new HashMap();
      for (Enumeration e = session.getAttributeNames(); e.hasMoreElements();) {
        String next = (String)e.nextElement();
        attributes.put(next, session.getAttribute(next));
      }
    }
    return attributes;

  } // End getSessionAttributes().


} // End SessionUtils class.
