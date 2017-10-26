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


import java.io.InputStream;
import java.util.Iterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javawebparts.core.org.apache.commons.digester.Digester;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;


/**
 * This filter allows you to redirect incoming requests to another URL based
 * on requested URL.  For instance, you can redirect http://www.me.com/search
 * to http://www.google.com.  You can in fact redirect or forward, and all of
 * this is driven by XML config file.
 * <br><br>
 * Init parameters are:
 * <br>
 * <ul>
 * <li><b>configFile</b> - This is the context-relative path to the config
 * file for this filter..  Required: Yes.
 * Default: None.</li>
 * </ul>
 * <br>
 * Example configuration in web.xml:
 * <br><br>
 * &lt;filter&gt;<br>
 * &nbsp;&nbsp;&lt;filter-name&gt;URLRedirectFilter&lt;/filter-name&gt;<br>
 * &nbsp;&nbsp;&lt;filter-class&gt;javawebparts.filter.URLRedirectFilter&lt;/
 * filter-class&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;configFile&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;/WEB-INF/redirect_config.xml
 * &lt;/param-value&gt;<br>
 * &lt;/filter&gt;
 * <br><br>
 * &lt;filter-mapping&gt;<br>
 * &nbsp;&nbsp;&lt;filter-name&gt;URLRedirectFilter&lt;/filter-name&gt;<br>
 * &nbsp;&nbsp;&lt;url-pattern&gt;/*&lt;/url-pattern&gt;<br>
 * &lt;/filter-mapping&gt;
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class URLRedirectFilter implements Filter {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javawebparts.core.org.apache.commons.digester.Digester");
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
      System.err.println("URLRedirectFilter" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(URLRedirectFilter.class);


  /**
   * This is the collection of configured items read from the config file.
   */
  private ArrayList entries = new ArrayList();


  /**
   * Add an entry to the entries collection.
   *
   * @param type This is the type of the entry, either "redirect" or "forward".
   * @param from The URL we want to redirect from.
   * @param to   The URL we want to redirect to.
   */
  private void addEntry(String type, String from, String to) {

    // We need to replace any wildcard characters with a suitable regex.
    StringBuffer sb = new StringBuffer(from.length() + 10);
    for (int i = 0; i < from.length(); i++) {
      if (from.charAt(i) == '*') {
        sb.append(".*");
      } else {
        sb.append(from.charAt(i));
      }
    }
    String fromURL = sb.toString();
    HashMap hm = new HashMap();
    hm.put("type", type);
    hm.put("from", fromURL);
    hm.put("to", to);
    entries.add(hm);

  } // End addEntry().


  /**
   * Add a redirect item from the config file.
   *
   * @param from The URL we want to redirect from.
   * @param to   The URL we want to redirect to.
   */
  public void addRedirect(String from, String to) {

    addEntry("redirect", from, to);

  } // End addRedirect().


  /**
   * Add a forward item from the config file.
   *
   * @param from The URL we want to redirect from.
   * @param to   The URL we want to redirect to.
   */
  public void addForward(String from, String to) {

    addEntry("forward", from, to);

  } // End addForward().


  /**
   * Initialize this filter.
   *
   * @param  filterConfig     The configuration information for this filter.
   * @throws ServletException ServletException.
   */
  public void init(FilterConfig filterConfig) throws ServletException {

    try {

      // Set up Digester to do our work.
      Digester digester = new Digester();
      digester.setValidating(false);
      digester.setLogger(log);
      digester.push(this);

      // Rules for a <redirect> element.
      digester.addCallMethod("config/redirect", "addRedirect", 2);
      digester.addCallParam("config/redirect", 0, "from");
      digester.addCallParam("config/redirect", 1, "to");

      // Rules for a <forward> element.
      digester.addCallMethod("config/forward", "addForward", 2);
      digester.addCallParam("config/forward", 0, "from");
      digester.addCallParam("config/forward", 1, "to");

      // Get a reference to the specified config file and have our Digester
      // instance parse it.
      String configFile = filterConfig.getInitParameter("configFile");
      if (configFile == null || configFile.equalsIgnoreCase("")) {
        String es = getClass().getName() + " could not initialize " +
          "because mandatory configFile init " +
          "parameter was not found";
        log.error(es);
        throw new ServletException(es);
      }
      InputStream isConfigFile =
        filterConfig.getServletContext().getResourceAsStream(configFile);
      digester.parse(isConfigFile);
      log.info("URLRedirectFilter Entries = " + entries);

    // Handle any exceptions that might occur.
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (SAXException se) {
      se.printStackTrace();
    }

    log.info("init() completed");

  } // End init().


  /**
   * Destroy.
   */
  public void destroy() {

  } // End destroy.


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
    FilterChain filterChain) throws ServletException, IOException {

    log.debug("URLRewriteFilter firing...");

    // Get the requested URL.
    HttpServletRequest req = (HttpServletRequest)request;
    String path = req.getServletPath();
    String pathInfo = req.getPathInfo();
    if (pathInfo != null) {
      path += pathInfo;
    }

    // See if the requested URL matches any in the config file, and redirect
    // or forward as configured if it does.
    for (Iterator it = entries.iterator(); it.hasNext();) {
      HashMap hm   = (HashMap)it.next();
      String  from = (String)hm.get("from");
      Pattern p    = Pattern.compile(from);
      Matcher m    = p.matcher(path);
      if (m.matches()) {
        String type = (String)hm.get("type");
        String to   = (String)hm.get("to");
        log.debug("Incoming path " + path + " matched configured path " +
          from + " so " + type + "ing as configured to " + to);
        if (type.equalsIgnoreCase("redirect")) {
          FilterHelpers.redirectOrForward(to, null, request, response);
          return;
        } else {
          FilterHelpers.redirectOrForward(null, to, request, response);
          return;
        }
      }
    }

    // Continue the filter chain, we're done.
    filterChain.doFilter(request, response);

  } // End doFilter().


} // End class.
