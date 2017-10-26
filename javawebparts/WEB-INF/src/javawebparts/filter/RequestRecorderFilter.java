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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import javawebparts.core.JWPHelpers;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javawebparts.core.org.apache.commons.codec.EncoderException;
import javawebparts.core.org.apache.commons.codec.net.URLCodec;
import javawebparts.core.org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This is a filter that is used to record each incoming request and write it
 * out to a CSV file.  Each line of the CSV file is a request, and each line
 * contains all the name/value pairs of the request parameters URL-encoded.
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
 * <li><b>saveFile</b> - This is the file to save the requests to.  If the
 * file exists, it will be overwritten.  This is a relative path and filename,
 * relative to the webapp root.  Required: No.
 * Default: RequestRecorderFilterSaveFile.csv (in root of webapp).
 * </ul>
 * <br>
 * Example configuration in web.xml:
 * <br><br>
 * &lt;filter&gt;<br>
 * &nbsp;&nbsp;&lt;filter-name&gt;RequestRecorderFilter&lt;/filter-name&gt;<br>
 * &nbsp;&nbsp;&lt;filter-class&gt;javawebparts.filter.
 * RequestRecorderFilter&lt;/filter-class&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;saveFile&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;RCFSaveFile.csv&lt;/
 * param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &lt;/filter&gt;
 * <br><br>
 * &lt;filter-mapping&gt;<br>
 * &nbsp;&nbsp;&lt;filter-name&gt;RequestRecorderFilter&lt;/filter-name&gt;<br>
 * &nbsp;&nbsp;&lt;url-pattern&gt;/*&lt;/url-pattern&gt;<br>
 * &lt;/filter-mapping&gt;
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class RequestRecorderFilter implements Filter {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javawebparts.core.JWPHelpers");
      Class.forName("javax.servlet.Filter");
      Class.forName("javax.servlet.FilterChain");
      Class.forName("javax.servlet.FilterConfig");
      Class.forName("javax.servlet.http.HttpServletRequest");
      Class.forName("javax.servlet.ServletException");
      Class.forName("javax.servlet.ServletRequest");
      Class.forName("javax.servlet.ServletResponse");
      Class.forName(
        "javawebparts.core.org.apache.commons.codec.EncoderException");
      Class.forName("javawebparts.core.org.apache.commons.codec.net.URLCodec");
      Class.forName("javawebparts.core.org.apache.commons.lang.StringUtils");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("RequestRecorderFilter" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(RequestRecorderFilter.class);


  /**
   * Whether pathList includes or excludes.
   */
  private String pathSpec;


  /**
   * List of paths for filter functionality determination.
   */
  private ArrayList pathList = new ArrayList();


  /**
   * Path to the file to write results to.
   */
  private String saveFile;


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

    // Get file to save results to.
    saveFile = filterConfig.getInitParameter("saveFile");
    if (saveFile == null || saveFile.equalsIgnoreCase("")) {
      saveFile = "RequestRecorderFilterSaveFile.csv";
    }

    // Get the full path to the save file
    saveFile = filterConfig.getServletContext().getRealPath(saveFile) +
               File.separator;
    saveFile = StringUtils.replace(saveFile, "\\", "\\\\");

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

    // Only operate on paths spcified in filter config.
    if (FilterHelpers.filterPath(request, pathList, pathSpec)) {
      StringBuffer sb = new StringBuffer(1024);
      // Append the method used and URL the client requested.  For the URL, we
      // want everything EXCEPT the query string, if any.
      sb.append(req.getMethod() + ",");
      int queryStart = req.getRequestURL().toString().indexOf('?');
      if (queryStart == -1) {
        sb.append(req.getRequestURL());
      } else {
        sb.append(req.getRequestURL().substring(0, queryStart));
      }
      // Append each parameter name/value pair.
      Enumeration e = req.getParameterNames();
      while (e.hasMoreElements()) {
        String pName = (String)e.nextElement();
        String[] pValues = req.getParameterValues(pName);
        for (int i = 0; i < pValues.length; i++) {
          sb.append("," + pName + "=");
          // URLEncode the value.
          try {
            sb.append(new URLCodec().encode(pValues[i]));
          } catch (EncoderException ee) {
            sb.append("??EncoderException??");
          }
        }
      }
      // If we got something, which we really always should, write it to file.
      // Note that JWPHelpers.writeToFile() is synchronized, so this filter
      // has obvious performance implications and should only be used in
      // development or for production debugging purposes.  By the way, if
      // this looks a little odd, it's probably because writeToFile() accepts
      // an array of strings, each string generally a line in a file, that's
      // why we're dealing in an array here, even though we know it's only
      // going to be one element.
      String[] sa = new String[1];
      sa[0] = sb.toString();
      if (sa[0] != null && !sa[0].equalsIgnoreCase("")) {
        JWPHelpers.writeToFile(saveFile, sa, true);
      }
    }

    filterChain.doFilter(request, response);


  } // End doFilter().


} // End class.
