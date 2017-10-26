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


package javawebparts.filter;


import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class contains static methods used by the classes implementing
 * the session limiting facility.
 *
 * @author Tamas Szabo
 */
public final class SessionLimiterFilterHelper {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javax.servlet.ServletContext");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("SessionLimiterFilterHelper" +
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
   * Log instance.
   */
  private static Log log = LogFactory.getLog(SessionLimiterFilterHelper.class);


  /**
   * This is a utility class, so we want a private noarg constructor so
   * instances cannot be created.
   */
  private SessionLimiterFilterHelper() {
  } // End SessionLimiterFilterHelper().


  /**
   * Register a session if the maximum number of sessions isn't reached yet.
   *
   * @param  ctx         The servlet context.
   * @param  sessionId   The session ID.
   * @param  maxSessions The number of allowed sessions.
   * @return             true if the session was successfully registered,
   *                     false otherwise.
   */
  public static boolean registerSession(ServletContext ctx, String sessionId,
                                        int maxSessions) {

    log.debug("Registering session: " + sessionId);
    Set registeredSessions = null;
    synchronized (ctx) {
      registeredSessions = (Set)ctx.getAttribute(SET_KEY);
      if (registeredSessions == null) {
        registeredSessions = Collections.synchronizedSet(new TreeSet());
        ctx.setAttribute(SET_KEY, registeredSessions);
        log.debug("Putting the Set in the appkication context");
      }
    }
    if (log.isDebugEnabled()) {
      log.debug("Registered sessions: " + registeredSessions.size() +
          ". Max sessions: "+ maxSessions);
    }
    if (registeredSessions.size() < maxSessions) {
      log.debug("Adding session " + sessionId + " to the Set");
      registeredSessions.add(sessionId);
      return true;
    } else {
      log.debug("Not adding session " + sessionId + " to the Set");
      return false;
    }

  } // End registerSession().


  /**
   * Checks whether a session is registered (ie allowed).
   *
   * @param  ctx       The servlet context.
   * @param  sessionId The session ID.
   * @return           true if this session is registered, false otherwise.
   */
  public static boolean isSessionRegistered(ServletContext ctx,
                                            String sessionId) {

    Set registeredSessions = null;
    synchronized(ctx) {
      registeredSessions = (Set)ctx.getAttribute(SET_KEY);
    }
    if (registeredSessions == null) {
       // The Set is not even registered yet.
      return false;
    }

    return registeredSessions.contains(sessionId);

  } // End isSessionRegistered().


} // End class.
