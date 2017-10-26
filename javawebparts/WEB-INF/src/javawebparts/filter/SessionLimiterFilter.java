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

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This filter can be used to limit the number of concurrent sessions allowed
 * by a webapp at any given time.
 * <br><br>
 * You have to specify the number of allowed sessions and a page where
 * you want to forward requests that would exceed the limit.
 * <br><br>
 * <b>Important!</b><br>
 * In order to use the session limiting facility you will also have to
 * configure the listener
 * <code>javawebparts.listener.SessionLimiterListener</code>
 * in your <code>web.xml</code>.
 * <br><br>
 * Init parameters are:
 * <br>
 * <ul>
 * <li><b>maxSessions</b> - A positive integer representing the number of
 * sessions allowed at the same time.  Required: Yes.  Default: N/A.</li>
 * <br><br>
 * <li><b>forwardTo</b> - This is the page where the request will be
 * forwarded if the number of concurrent sessions has been already reached,
 * so access is not allowed. Required: Yes.</li>
 * <br><br>
 * <li><b>filterExpired</b> - If set to true (the default), requests
 * that contain expired session IDs will be forwarded to the forwardTo page.
 * If set to false, the request is allowed through.  You would normaly set it
 * to false if another filter (that will fire after this filter) handles
 * expired sessions. Required: No.  Default: true.</li>
 * <br><br>
 * <li><b>pathSpec</b> - Either "include" or "exclude".  This determines whether
 * the list of paths in the pathList parameter is a list of paths to include in
 * filter functionality, or a list of paths to exclude.  Required: No.
 * Default: None.</li>
 * <br><br>
 * <li><b>pathList</b> - This is a comma-separated list of paths, which can use
 * asterisk for wildcard support, that denotes either paths to include or
 * exclude from the functioning of this filter (depending on what pathSpec
 * is set to).  The paths ARE case-senitive!  There is no limit to how many
 * items can be specified, although for performance reasons a developer will
 * probably want to specify as few as possible to get the job done (each
 * requested path is matched via regex).  Note also that you are of course
 * still required to specify a path for the filter itself as per the servlet
 * spec.  This parameter however, together with pathSpec, gives you more control
 * and flexibility than that setting alone.  Required: No.  Default: None.
 * <br><br>
 * General note on pathSpec and pathList:  If pathSpec is not specified but
 * pathList IS, then 'exclude' is assumed for pathSpec.  If pathSpec is
 * specified by pathList IS NOT, then the filter WILL NEVER EXECUTE (this is
 * technically a misconfiguration).  If NEITHER is defined then the generic
 * filter mapping will be in effect only.</li>
 * <br><br>
 * </ul>
 * <br>
 * Example configuration in web.xml:
 * <br><br>
 * &lt;filter&gt;<br>
 * &nbsp;&nbsp;&lt;filter-name&gt;SessionLimiterFilter&lt;
 * /filter-name&gt;<br>
 * &nbsp;&nbsp;&lt;filter-class&gt;javawebparts.filter.SessionLimiterFilter&lt;
 * /filter-class&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;maxSessions&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;1&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;forwardTo&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;/SLFTooMany.jsp.jpg,&lt;
 * /param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;pathSpec&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;exclude&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;pathList&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;/img/header1.gif&lt;
 * /param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &lt;/filter&gt;
 * <br><br>
 * &lt;filter-mapping&gt;<br>
 * &nbsp;&nbsp;&lt;filter-name&gt;SessionLimiterFilter&lt;
 * /filter-name&gt;<br>
 * &nbsp;&nbsp;&lt;url-pattern&gt;/*&lt;/url-pattern&gt;<br>
 * &lt;/filter-mapping&gt;
 *
 * @author Tamas Szabo
 */
public class SessionLimiterFilter implements Filter {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javax.servlet.Filter");
      Class.forName("javax.servlet.FilterChain");
      Class.forName("javax.servlet.FilterConfig");
      Class.forName("javax.servlet.http.HttpServletRequest");
      Class.forName("javax.servlet.http.HttpServletResponse");
      Class.forName("javax.servlet.http.HttpSession");
      Class.forName("javax.servlet.ServletContext");
      Class.forName("javax.servlet.ServletException");
      Class.forName("javax.servlet.ServletRequest");
      Class.forName("javax.servlet.ServletResponse");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("SessionLimiterFilter" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(SessionLimiterFilter.class);


  /**
   * A path to forward to when access is denied.
   */
  private String forwardTo;


  /**
   * The ServletContext of the filter.
   */
  private ServletContext ctx;


  /**
   * Whether pathList includes or excludes.
   */
  private String pathSpec;


  /**
   * List of paths for filter functionality determination.
   */
  private ArrayList pathList = new ArrayList();


  /**
   * The number of session allowed.
   */
  private int maxSessions;


  /**
   * Filter request with expired session ID.
   */
  private boolean filterExpired = true;


  /**
   * Destroy.
   */
  public void destroy() {

  } // End destroy.


  /**
   * Initialize this filter.
   *
   * @param  filterConfig     The configuration information for this filter.
   * @throws ServletException ServletException.
   */
  public void init(FilterConfig filterConfig) throws ServletException {

    log.info("init() started");

    // Do pathSpec and pathList init work.
    pathSpec = FilterHelpers.initPathSpec(getClass().getName(), filterConfig);
    pathList = FilterHelpers.initPathList(getClass().getName(), filterConfig);

    ctx = filterConfig.getServletContext();

    forwardTo = filterConfig.getInitParameter("forwardTo");
    if (forwardTo == null) {
      String msg = "SessionLimiterFilter could not initialize " +
                   "because the mandatory forwardTo init parameter " +
                   "was not found ";
      log.error(msg);
      throw new ServletException(msg);
    }

    String value = filterConfig.getInitParameter("maxSessions");
    if (value == null) {
      String msg = "SessionLimiterFilter could not initialize " +
                   "because the mandatory maxSessions init parameter " +
                   "was not found ";
      log.error(msg);
      throw new ServletException(msg);
    }
    maxSessions = -1;
    try {
      maxSessions = Integer.parseInt(value);
    } catch (NumberFormatException e) {
      log.error("NumberFormatException parsing maxSessions");
    }
    if (maxSessions < 0) {
      String msg = "SessionLimiterFilter could not initialize " +
                   "because the maxSessions init parameter must be " +
                   "a positive integer.";
      log.error(msg);
      throw new ServletException(msg);
    }

    value = filterConfig.getInitParameter("filterExpired");
    if (value != null) {
      if (value.equals("true")) {
        filterExpired = true;
      } else if (value.equals("false")) {
        filterExpired = false;
      } else {
        String msg = "SessionLimiterFilter could not initialize " +
                     "because the filterExpired init parameter must be " +
                     "either 'true' or 'false'. '" +
                     value + "' was set";
        log.error(msg);
        throw new ServletException(msg);
      }
    }

    log.info("init() completed");

  } // End init().


  /**
   * Do filter's work.
   *
   * @param  request          The current request object.
   * @param  response         The current response object.
   * @param  filterChain      The current filter chain.
   * @throws ServletException ServletException.
   * @throws IOException      IOException.
   */
  public void doFilter(ServletRequest request, ServletResponse response,
                       FilterChain filterChain)
                       throws ServletException, IOException {

    log.info("doFilter() started");

    HttpServletRequest req = (HttpServletRequest)request;
    HttpServletResponse resp = (HttpServletResponse)response;

    // Get the path.
    String pathInfo = req.getPathInfo();
    if (pathInfo == null) {
      pathInfo = "";
    }
    String path = req.getServletPath() + pathInfo;
    log.debug("The path is: " + path);

    if (FilterHelpers.filterPath(request, pathList, pathSpec)) {

      log.debug("Filtering path");
      String requestedSessionId = req.getRequestedSessionId();

      // The request is not part of the existing session...
      if (requestedSessionId == null) {

        log.debug("No session ID in the request... creating a new session");
        // Create a session...
        HttpSession session = req.getSession();
        // Try to register the session.
        // If we cannot, forward and return.
        log.debug("trying to register the new session");
        if (!SessionLimiterFilterHelper.registerSession(ctx,
            session.getId(), maxSessions)) {
          log.debug("Couldn't register the new session: invalidate, redirect");
          session.invalidate();
          req.getRequestDispatcher(forwardTo).forward(req, resp);
          return;
        }

      // The request contains a session ID...
      } else {

        log.debug("Processing requested session: " + requestedSessionId);
        // If the session ID from the request is valid...
        if (req.isRequestedSessionIdValid()) {
          log.debug("Requested session ID is valid");
          // ... but not registered...
          if (!SessionLimiterFilterHelper.isSessionRegistered(ctx,
            requestedSessionId)) {
            log.debug("...but not registered");
            // We try to register it now.
            // But if we cannot because the max sessions
            // has been reached, we forward and return.
            log.debug("Trying to register it");
            if (!SessionLimiterFilterHelper.registerSession(ctx,
                requestedSessionId, maxSessions)) {
              log.debug("Couldn't register the new session: " +
                "invalidate, forward");
              req.getSession().invalidate();
              req.getRequestDispatcher(forwardTo).forward(req, resp);
              return;
            }
          }

        } else if (filterExpired) {
          req.getRequestDispatcher(forwardTo).forward(req, resp);
          return;
        }

      } // End if (requestedSessionId == null).

    } // End if (FilterHelpers.filterPath(path, pathList, pathSpec)).

    log.info("Continuing filter chain");
    filterChain.doFilter(request, response);

  } // End doFilter().


} // End class.
