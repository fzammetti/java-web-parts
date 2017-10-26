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
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This filter checks all incoming request parameters, as well as all attributes
 * if desired,  for any characters usually associated with cross-site scripting
 * exploits.  It allows for including or excluding paths from filter
 * functionality.  It also allows a custom regex expression in case the
 * application might legitimately want to allow certain characters.
 * <br><br>
 * Init parameters are:
 * <br>
 * <ul>
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
 * <li><b>cssRegex</b> - This is the regular expression that will be used to
 * check parameter and String attribute values.  The default string is
 * [&lt;&gt;%()] which will effectively disallow any parameter or String
 * attribute  value with the characters <, >, %, (, or ) in them.  Required: No.
 * Default: [&lt;&gt;%()]</li>
 * <br><br>
 * <li><b>checkAttributes</b> - Either 'true' or 'false'.  When true, attributes
 * are checked as well IF they are Strings.  Attributes that do not pass an
 * instanceof String test will NOT be checked.  Required: No.  Default: false.
 * </li>
 * <br><br>
 * <li><b>redirectTo</b> - The URL to redirect to if an illegal character is
 * found in any value. You can either redirect to this
 * URL or forward to it, but not both. Required: Yes (either this or forwardTo).
 * Default: None.</li>
 * <br><br>
 * <li><b>forwardTo</b> - The URL to forward to if an illegal character is
 * found in any value. You can either redirect to this
 * URL or forward to it, but not both. Required: Yes (either this or
 * redirectTo).
 * Default: None.</li>
 * </ul>
 * <br>
 * Example configuration in web.xml:
 * <br><br>
 * &lt;filter&gt;<br>
 * &nbsp;&nbsp;&lt;filter-name&gt;CrossSiteScriptingFilter&lt;
 * /filter-name&gt;<br>
 * &nbsp;&nbsp;&lt;filter-class&gt;javawebparts.filter.
 * CrossSiteScriptingFilter&lt;/filter-class&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;forwardTo&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;/CSSReject.jsp&lt;/param-value&gt;
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &lt;/filter&gt;
 * <br><br>
 * &lt;filter-mapping&gt;<br>
 * &nbsp;&nbsp;&lt;filter-name&gt;CrossSiteScriptingFilter&lt;
 * /filter-name&gt;<br>
 * &nbsp;&nbsp;&lt;url-pattern&gt;/*&lt;/url-pattern&gt;<br>
 * &lt;/filter-mapping&gt;
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class CrossSiteScriptingFilter implements Filter {


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
      Class.forName("javax.servlet.ServletException");
      Class.forName("javax.servlet.ServletRequest");
      Class.forName("javax.servlet.ServletResponse");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("CrossSiteScriptingFilter" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(CrossSiteScriptingFilter.class);


  /**
   * Whether pathList includes or excludes.
   */
  private String pathSpec;


  /**
   * List of paths for filter functionality determination.
   */
  private ArrayList pathList = new ArrayList();


  /**
   * The regular expression to use to check parameter and String attributes.
   */
  private Pattern cssRegex = Pattern.compile("[<>%()]");


  /**
   * A path to redirect to when access is denied.
   */
  private String redirectTo;


  /**
   * A path to forward to when access is denied.
   */
  private String forwardTo;


  /**
   * Determines whether String attributes are checked or not.
   */
  private boolean checkAttributes;


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

    // Get the redirectTo and forwardTo init parameters and validate them.
    // Get the redirectTo and forwardTo init parameters and validate them.
    redirectTo = FilterHelpers.initRedirectTo(filterConfig);
    forwardTo  = FilterHelpers.initForwardTo(filterConfig);
    FilterHelpers.checkRedirectForwardTo(getClass().getName(),
                                         redirectTo, forwardTo);

    // Get the cssRegex to use as the check expression, overriding the default
    // if it is found.
    String cssRegex1 = filterConfig.getInitParameter("cssRegex");
    if (cssRegex1 != null) {
      cssRegex = Pattern.compile(cssRegex1);
    }
    log.info("cssRegex  = " + cssRegex.pattern());

    // Get the value to determine if String attributes will be checked or not.
    String checkAttrs = filterConfig.getInitParameter("checkAttributes");
    if (checkAttrs == null || checkAttrs.equalsIgnoreCase("false")) {
      checkAttributes = false;
    } else {
      checkAttributes = true;
    }
    log.info("checkAttributes  = " + checkAttributes);

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

    HttpServletRequest req  = (HttpServletRequest)request;

    if (FilterHelpers.filterPath(request, pathList, pathSpec)) {

      log.info("CrossSiteScriptingFilter firing...");

      // Check parameters.
      for (Enumeration e = req.getParameterNames(); e.hasMoreElements();) {
        String pName = (String)e.nextElement();
        String[] v = req.getParameterValues(pName);
        int i = 0;
        while (i < v.length) {
          Matcher m = cssRegex.matcher(v[i]);
          if (m.find()) {
            log.info("CrossSiteScripting filter found an illegal character " +
                     "in parameter '" + pName + "' (value=" + v[i] + ")");
            FilterHelpers.redirectOrForward(redirectTo, forwardTo, request,
                                            response);
            return;
          }
          i++;
        }
      }
      // Check String attributes, if applicable.
      if (checkAttributes) {
        for (Enumeration e = req.getAttributeNames(); e.hasMoreElements();) {
          Object o = (Object)req.getAttribute((String)e.nextElement());
          if (o instanceof String) {
            String s = (String)o;
            Matcher m = cssRegex.matcher(s);
            if (m.find()) {
            log.info("CrossSiteScripting filter found an illegal character " +
                     "in attribute '" + o + "' (value=" + s + ")");
            FilterHelpers.redirectOrForward(redirectTo, forwardTo, request,
                                            response);
            return;
            }
          }
        }
      }

    }

    filterChain.doFilter(request, response);


  } // End doFilter().


} // End class.
