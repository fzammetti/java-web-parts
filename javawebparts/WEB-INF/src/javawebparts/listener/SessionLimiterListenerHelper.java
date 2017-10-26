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


import java.util.Set;
import javax.servlet.ServletContext;


/**
 * This class contains static methods used by the classes implementing
 * the session limiting facility.
 *
 * @author Tamas Szabo
 */
public final class SessionLimiterListenerHelper {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javax.servlet.ServletContext");
    } catch (ClassNotFoundException e) {
      System.err.println("SessionLimiterListenerHelper" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Attribute name of the Set containing the allowed session IDs in the
   * application context.
   */
  public static final String SET_KEY =
    "javawebparts.sessionlimiter.registeredSessions";


  /**
   * This is a utility class, so we want a private noarg constructor so
   * instances cannot be created.
   */
  private SessionLimiterListenerHelper() {
  } // End SessionLimiterListenerHelper().


  /**
   * Unregisters a session.
   *
   * @param ctx       the servlet context.
   * @param sessionId the session ID to unregister.
   */
  public static void unregisterSession(ServletContext ctx, String sessionId) {

    Set registeredSessions = null;
    synchronized(ctx) {
      registeredSessions = (Set)ctx.getAttribute(SET_KEY);
    }
    if (registeredSessions != null) {
      registeredSessions.remove(sessionId);
    }

  } // End unregisterSession().

} // End class.
