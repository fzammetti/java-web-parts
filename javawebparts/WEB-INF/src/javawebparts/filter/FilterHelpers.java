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
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.StringTokenizer;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class contains static methods that are used by a number of different
 * filters.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public final class FilterHelpers {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javax.servlet.FilterConfig");
      Class.forName("javax.servlet.http.HttpServletResponse");
      Class.forName("javax.servlet.http.HttpServletRequest");
      Class.forName("javax.servlet.ServletException");
      Class.forName("javax.servlet.ServletRequest");
      Class.forName("javax.servlet.ServletResponse");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("FilterHelpers" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(FilterHelpers.class);


  /**
   * This is a utility class, so we want a private noarg constructor so
   * instances cannot be created.
   */
  private FilterHelpers() {
  } // End FilterHelpers().


  /**
   * This is a common function used by a number of filters to get the init
   * parameter that describes the path list.
   * @param  filterName       The name of the filter calling this function.
   *                          This is for logging purposes.
   * @param  filterConfig     The filterConfig instance associated with the
   *                          filter.
   * @return                  The pathSpec value.
   * @throws ServletException If anything goes wrong.
   */
  public static String initPathSpec(String filterName,
    FilterConfig filterConfig) throws ServletException {

    // Get the pathSpec init parameter.
    String pathSpec = filterConfig.getInitParameter("pathSpec");
    if (pathSpec != null) {
      if (!pathSpec.equalsIgnoreCase("include") &&
          !pathSpec.equalsIgnoreCase("exclude")) {
        String es = filterName + " could not initialize " +
          "because pathSpec init parameter " +
          "was not a valid value (must be 'include' or 'exclude')";
        log.error(es);
        throw new ServletException(es);
      }
    }
    log.info("pathSpec = " + pathSpec);
    return pathSpec;

  } // End initPathSpec().


  /**
   * This is a common function used by a number of filters to get the list
   * of paths the filter will function or not function on.
   * @param  filterName       The name of the filter calling this function.
   *                          This is for logging purposes.
   * @param  filterConfig     The filterConfig instance associated with the
   *                          filter.
   * @return                  The list of paths configured for the filter.
   */
  public static ArrayList initPathList(String filterName,
    FilterConfig filterConfig) {

    // Get the comma-separater list of paths pathList init parameter.
    String    csvPathList = filterConfig.getInitParameter("pathList");
    ArrayList al          = new ArrayList();
    if (csvPathList != null) {
      log.info("csvPathList = " + csvPathList);
      StringTokenizer st = new StringTokenizer(csvPathList, ",");
      // Parse the CSV.
      while (st.hasMoreTokens()) {
        String s  = st.nextToken();
        StringBuffer sb = new StringBuffer(s.length() + 10);
        // We need to replace any wildcard characters with a suitable regex.
        for (int i = 0; i < s.length(); i++) {
          if (s.charAt(i) == '*') {
            sb.append(".*");
          } else {
            sb.append(s.charAt(i));
          }
        }
        // Modified paths can now be added to the collection.
        al.add(sb.toString());
      }
    }
    log.info("pathList = " + al);
    return al;

  } // End initPathList().


  /**
   * Gets the redirectTo init parameter that is common to many filters.
   *
   * @param  filterConfig The filterConfig instance associated with the filter.
   * @return              The value of the redirectTo init param.
   */
  public static String initRedirectTo(FilterConfig filterConfig) {

    String redirectTo = filterConfig.getInitParameter("redirectTo");
    log.info("redirectTo = " + redirectTo);
    return redirectTo;

  } // End initRedirectTo().


  /**
   * Gets the forwardTo init parameter that is common to many filters.
   *
   * @param  filterConfig The filterConfig instance associated with the filter.
   * @return              The value of the forwardTo init param.
   */
  public static String initForwardTo(FilterConfig filterConfig) {

    String forwardTo = filterConfig.getInitParameter("forwardTo");
    log.info("forwardTo  = " + forwardTo);
    return forwardTo;

  } // End initForwardTo().


  /**
   * For filters that support the redirectTo and forwardTo init parameters,
   * only one or the other can be specified, never both, and at least one
   * must be specified.  This method performs that check.
   *
   * @param  filterName       The name of the filter calling this check.
   * @param  redirectTo       The value of the redirectTo init param.
   * @param  forwardTo        The value of the forwardTo init param.
   * @throws ServletException If the tests are not passed.
   */
  public static void checkRedirectForwardTo(String filterName,
    String redirectTo, String forwardTo) throws ServletException {

    if (redirectTo == null & forwardTo == null) {
      String es = filterName + " could not initialize " +
        "because one of mandatory redirectTo or " +
        "forwardTo init parameters was not found";
      log.error(es);
      throw new ServletException(es);
    }
    if (redirectTo != null & forwardTo != null) {
      String es = filterName + " could not initialize " +
        "because both redirectTo and forwardTo " +
        "init parameters were defined when only " +
                  "one or the other is allowed";
      log.error(es);
      throw new ServletException(es);
    }

  } // End checkRedirectForwardTo().


  /**
   * Determines whether a filter should do its work.  It is passed an
   * ArrayList containing a list of paths, with wildcard support, and whether
   * those paths should be included or excluded by the filter.
   *
   * @param  request    ServlerRequest object.
   * @param  pathList   The ArrayList containing the lists of paths to either
   *                    include or exclude.
   * @param  inPathSpec Whether the paths are included or excluded.
   * @return            True if the calling filter should apply itself to the
   *                    request, false if not.
   */
  public static boolean filterPath(ServletRequest request, ArrayList pathList,
    String inPathSpec) {


    String pathSpec = inPathSpec;

    // Quick check #1: if pathSpec and pathList are both null, return true
    // because the generic filter mapping is in effect only.
    if (pathList == null && pathSpec == null) {
      return true;
    }

    // Quick check #2: If pathSpec is null but pathList IS NOT null, then
    // we're going to pretend pathSpec is 'exclude'.
    if (pathSpec == null && pathList != null) {
      pathSpec = "exclude";
    }

    // Quick check #3: If pathSpec is not null and pathList IS null, then
    // we're going to just return false since this is really technically
    // a configuration error and we don't know for sure what to do here.
    if (pathSpec != null && pathList == null) {
      return false;
    }

    // Getting the requested path
    HttpServletRequest req = (HttpServletRequest)request;
    String path = req.getServletPath();
    String pathInfo = req.getPathInfo();
    if (pathInfo != null) {
      path += pathInfo;
    }
    // See if the requested path matches any (or multiple) paths in the
    // pathList collection.
    boolean pathInCollection = false;
    for (Iterator it = pathList.iterator(); it.hasNext();) {
      String  np = (String)it.next();
      Pattern p  = Pattern.compile(np);
      Matcher m  = p.matcher(path);
      if (m.matches()) {
        pathInCollection = true;
        break;
      }
    }

    // If the path was in the collection and the pathSpec is include, or if
    // the path was NOT in the collection and pathSpec is exclude, then
    // we want the calling filter to do its work, so return true, otherwise
    // return false.
    boolean retVal = false;
    if ((pathInCollection && pathSpec.equalsIgnoreCase("include")) ||
       (!pathInCollection && pathSpec.equalsIgnoreCase("exclude"))) {
      retVal = true;
    } else {
      retVal = false;
    }
    return retVal;

  } // Ebd filterPath().


  /**
   * This is called by any filter after it determines that a request should
   * be aborted.  It determines whether to do a forward or redirect as
   * specified in the filter's config, and does it.
   *
   * @param  redirectTo       The URL to redirect to, if specified.
   * @param  forwardTo        The URL to forward to, if specified.
   * @param  request          The current request.
   * @param  response         The current response.
   * @throws ServletException If anything goes wrong.
   * @throws IOException      If anything goes wrong.
   */
  public static void redirectOrForward(String redirectTo, String forwardTo,
    ServletRequest request, ServletResponse response) throws
    ServletException, IOException {

    log.info("redirectOrForward()...");
    if (forwardTo != null) {
      log.info("forwardTo = " + forwardTo);
      request.getRequestDispatcher(forwardTo).forward(request, response);
    }
    if (redirectTo != null) {
      log.info("redirectTo = " + redirectTo);
      ((HttpServletResponse)response).sendRedirect(redirectTo);
    }
    log.info("Done");

  } // End redirectOrForward().


} // End class.
