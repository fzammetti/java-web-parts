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


import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This HttpSessionListener has to be registered in order to use
 * the session limiting facility (see
 * <code>javawebparts.filter.SessionLimiterFilter</code>).
 * <br><br>
 * The listener is essential because when session are invalidated
 * (by the application code or they expire) we have to unregister them
 * from our registration of allowed sessions.
 * <br><br>
 * Example configuration in web.xml:
 * <br><br>
 * &lt;listener&gt;<br>
 * &nbsp;&nbsp;&lt;listener-class&gt;
 * javawebparts.listener.SessionLimiterListener&lt;/listener-class&gt;<br>
 * &lt;/listener&gt;
 *
 * @author Tamas Szabo
 *
 */
public class SessionLimiterListener implements HttpSessionListener {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javax.servlet.http.HttpSession");
      Class.forName("javax.servlet.http.HttpSessionEvent");
      Class.forName("javax.servlet.http.HttpSessionListener");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("SessionLimiterListener" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(SessionLimiterListener.class);


  /**
   * Unregistering the session when it is destroyed.
   *
   * @param sevent HttpSessionEvent.
   */
  public void sessionCreated(HttpSessionEvent sevent) {

    // Do nothing.
    if (log.isDebugEnabled()) {
      log.debug("Session created: " + sevent.getSession().getId());
    }

  } // End sessionCreated().


  /**
   * Unregistering the session when it is destroyed.
   *
   * @param sevent HttpSessionEvent.
   */
  public void sessionDestroyed(HttpSessionEvent sevent) {

    if (log.isDebugEnabled()) {
      log.debug("Session destroyed: " + sevent.getSession().getId());
    }
    HttpSession session = sevent.getSession();
    SessionLimiterListenerHelper.unregisterSession(session.getServletContext(),
      session.getId());

  } // End sessionDestroyed().


} // End class().
