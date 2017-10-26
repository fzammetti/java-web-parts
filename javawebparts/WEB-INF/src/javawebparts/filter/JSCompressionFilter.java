/*
 * Copyright 2006 Herman van Rosmalen
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

import javawebparts.filter.JSMin.UnterminatedCommentException;
import javawebparts.filter.JSMin.UnterminatedRegExpLiteralException;
import javawebparts.filter.JSMin.UnterminatedStringLiteralException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This filter does some simple javascript compressing in the response.
 * It uses a slightly modified version of JSMin.java from John Reilly which,
 * in it's turn, is a java version of Douglas Crockford's jsmin.c. If you
 * want to know how it works and what is does: www.crockford.com
 * It allows for including or excluding paths from filter functionality. <br>
 * <br>
 * Init parameters are: <br>
 * <ul>
 * <li><b>pathSpec </b>- Either "include" or "exclude". This determines
 * whether the list of paths in the pathList parameter is a list of paths to
 * include in filter functionality, or a list of paths to exclude. Required: No.
 * Default: None.</li>
 * <br>
 * <br>
 * <li><b>pathList </b>- This is a comma-separated list of paths, which can
 * use asterisk for wildcard support, that denotes either paths to include or
 * exclude from the functioning of this filter (depending on what pathSpec is
 * set to). The paths ARE case-senitive! There is no limit to how many items can
 * be specified, although for performance reasons a developer will probably want
 * to specify as few as possible to get the job done (each requested path is
 * matched via regex). Note also that you are of course still required to
 * specify a path for the filter itself as per the servlet spec. This parameter
 * however, together with pathSpec, gives you more control and flexibility than
 * that setting alone. Required: No. Default: None. <br>
 * <br>
 * General note on pathSpec and pathList: If pathSpec is not specified but
 * pathList IS, then 'exclude' is assumed for pathSpec. If pathSpec is specified
 * by pathList IS NOT, then the filter WILL NEVER EXECUTE (this is technically a
 * misconfiguration). If NEITHER is defined then the generic filter mapping will
 * be in effect only.</li>
 * </ul>
 * <br>
 * Example configuration in web.xml: <br>
 * <br>
 * &lt;filter&gt; <br>
 * &nbsp;&nbsp;&lt;filter-name&gt;JSCompressionFilter&lt;/filter-name&gt; <br>
 * &nbsp;&nbsp;&lt;filter-class&gt;javawebparts.filter.
 * JSCompressionFilter&lt;/filter-class&gt; <br>
 * &nbsp;&nbsp;&lt;init-param&gt; <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;pathSpec&lt;/param-name&gt; <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;include&lt;/param-value&gt; <br>
 * &nbsp;&nbsp;&lt;/init-param&gt; <br>
 * &nbsp;&nbsp;&lt;init-param&gt; <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;pathList&lt;/param-name&gt; <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;*&zwj;/smalljavascript.js&lt;/
 * param-value&gt; <br>
 * &nbsp;&nbsp;&lt;/init-param&gt; <br>
 * &lt;/filter&gt; <br>
 * <br>
 * &lt;filter-mapping&gt; <br>
 * &nbsp;&nbsp;&lt;filter-name&gt;JSCompressionFilter&lt;/filter-name&gt; <br>
 * &nbsp;&nbsp;&lt;url-pattern&gt;/*.js&lt;/url-pattern&gt; <br>
 * &lt;/filter-mapping&gt;
 *
 * @author <a href="mailto:herros@gmail.com">Herman van Rosmalen </a>.
 */
public class JSCompressionFilter implements Filter {

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
      Class.forName("javax.servlet.ServletException");
      Class.forName("javax.servlet.ServletRequest");
      Class.forName("javax.servlet.ServletResponse");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err
          .println("JSCompressionFilter"
              + " could not be loaded by classloader because classes it depends"
              + " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }

  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(JSCompressionFilter.class);

  /**
   * Whether pathList includes or excludes.
   */
  private static String pathSpec;

  /**
   * List of paths for filter functionality determination.
   */
  private static ArrayList pathList = new ArrayList();

  /**
   * Destroy.
   */
  public void destroy() {

  } // End destroy.

  /**
   * Initialize this filter.
   *
   * @param filterConfig
   *            The configuration information for this filter.
   * @throws ServletException
   *             ServletException.
   */
  public void init(FilterConfig filterConfig) throws ServletException {

    log.info("init() started");

    // Do pathSpec and pathList init work.
    pathSpec = FilterHelpers.initPathSpec(getClass().getName(),
        filterConfig);
    pathList = FilterHelpers.initPathList(getClass().getName(),
        filterConfig);

    log.info("init() completed");

  } // End init().

  /**
   * Do filter's work.
   *
   * @param request
   *            The current request object.
   * @param response
   *            The current response object.
   * @param filterChain
   *            The current filter chain.
   * @throws ServletException
   *             ServletException.
   * @throws IOException
   *             IOException.
   */
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    HttpServletRequest req = (HttpServletRequest) request;

    // if in filterPath
    if (FilterHelpers.filterPath(request, pathList, pathSpec)) {
      log.info("JSCompressionFilter firing...");

      // Capture the stream which writes response to the client
      ServletOutputStream out = response.getOutputStream();

      JSCompressionResWrapper wrappedResp =
        new JSCompressionResWrapper((HttpServletResponse)response);
      filterChain.doFilter(request, wrappedResp);

      wrappedResp.getWriter().close();
      // Get content type, if it isn't text/html or javascript,
      // we will not change a thing
      String ct = wrappedResp.getContentType();

      log.debug("Request content type: " + ct);
      byte[] bytes =
        ((JSCompressionResStream) wrappedResp.getOutputStream())
          .getData();
      if (ct != null &&
          (ct.indexOf("text/html") != -1 ||
           ct.indexOf("javascript") != -1)) {
        boolean isHtml = ct.indexOf("text/html") >= 0;
        log.info("Length before compression " + bytes.length);
        byte[] bytesOut = compress(bytes, isHtml);
        out.write(bytesOut);
        wrappedResp.setContentLength(bytesOut.length);
        log.info("Length after compression " + bytesOut.length);
      } else {
        out.write(bytes);
      }
    } else {
    // if not in filterPath
      filterChain.doFilter(request, response);
    }
  } // End doFilter().

  /**
   * Do compression.
   *
   * @param bytes   The text to check
   * @param isHtml  Are we compressing Html (or a .js file)
   * @return byte[] The compressed result
   */
  private byte[] compress(byte[] bytes, boolean isHtml) {
    StringBuffer result = new StringBuffer();
    try {
      String text = new String(bytes);
      JSMin jsmin = new JSMin();
      if (!isHtml) {
        result.append(jsmin.compress(text));
      } else {
        int from = text.toLowerCase().indexOf("<script>");
        int to = 0;
        while (from > 0) {
          result.append(text.substring(0, from));
          to = text.toLowerCase().indexOf("</script>", from) + 9;
          String compressed = jsmin.compress(text.substring(from, to));
          result.append(compressed);
          text = text.substring(to);
          from = text.toLowerCase().indexOf("<script>");
        }
        result.append(text);
      }
    } catch (IOException e) {
        return bytes;
    } catch (UnterminatedRegExpLiteralException e) {
        return bytes;
    } catch (UnterminatedCommentException e) {
        return bytes;
    } catch (UnterminatedStringLiteralException e) {
        return bytes;
    }
    return result.toString().getBytes();
  }


} // End class.
