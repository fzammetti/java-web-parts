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
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This filter allows for compressing the response from the server.
 * It allows for including or excluding paths from filter functionality.  It
 * also allows for defining what type of compression should be used, if
 * supported by the client, and in what order.
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
 * <li><b>compressType</b> - What type of compression to perform.  The valid
 * values are 'gzip_only' (GZip will be used, if supported), 'deflate_only'
 * (Deflate will be used, if supported), 'gzip_first' (GZip will be used if
 * supported, otherwise Deflate will be used, if supported), 'deflate_first'
 * (Deflate will be used if supported, otherwise GZip will be used if
 * supported).  Required: Yes.  Default: None (must be one of the valid values).
 * </li>
 * </ul>
 * <br>
 * Example configuration in web.xml:
 * <br><br>
 * &lt;filter&gt;<br>
 * &nbsp;&nbsp;&lt;filter-name&gt;CompressionFilter&lt;/filter-name&gt;<br>
 * &nbsp;&nbsp;&lt;filter-class&gt;javawebparts.filter.
 * CompressionFilter&lt;/filter-class&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;pathSpec&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;include&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;pathList&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;*&zwj;/bigimage2.gif&lt;/
 * param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;compressType&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;gzip_first&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &lt;/filter&gt;
 * <br><br>
 * &lt;filter-mapping&gt;<br>
 * &nbsp;&nbsp;&lt;filter-name&gt;CompressionFilter&lt;/filter-name&gt;<br>
 * &nbsp;&nbsp;&lt;url-pattern&gt;/*&lt;/url-pattern&gt;<br>
 * &lt;/filter-mapping&gt;
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class CompressionFilter implements Filter {


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
      System.err.println("CompressionFilter" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(CompressionFilter.class);


  /**
   * Whether pathList includes or excludes.
   */
  private String pathSpec;


  /**
   * List of paths for filter functionality determination.
   */
  private ArrayList pathList = new ArrayList();


  /**
   * What type of oompression we want this filter to do.  Valid values are
   * "gzip_only", "deflate_only", "gzip_first" and "deflate_first".  If
   * neither compression type is supported by the client, no compression is
   * attempted.  If the client supports both and this option is "gzip_first",
   * GZip be used if it is supported, or Deflate if not, and the exact
   * opposite is true when "deflate_first" is used.  If set to "gzip_first"
   * but only Deflate is supported, Deflate will be used, and vice-versa.
   */
  private String compressType;


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

    // Get the compressType init parameter.
    compressType = filterConfig.getInitParameter("compressType");
    if (compressType == null) {
      String es = getClass().getName() + " could not initialize " +
                  "because mandatory compressType init " +
                  "parameter was not found";
      log.error(es);
      throw new ServletException(es);
    }
    if (!compressType.equalsIgnoreCase("gzip_only") &&
        !compressType.equalsIgnoreCase("deflate_only") &&
        !compressType.equalsIgnoreCase("gzip_first") &&
        !compressType.equalsIgnoreCase("deflate_direct")) {
      String es = getClass().getName() + " could not initialize " +
                  "because compressType init " +
                  "parameter did not have a valid value (must be one of " +
                  "'gzip_only', 'deflate_only', 'gzip_first' or " +
                  "'deflate_first)";
      log.error(es);
      throw new ServletException(es);
    }
    log.info("compressType = " + compressType);

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

    HttpServletRequest req = (HttpServletRequest)request;
    boolean processChain = true;

    // See if the client supports compression.  If not, no sense doing any more.
    String acceptEncoding = req.getHeader("accept-encoding");
    if (acceptEncoding != null) {
      acceptEncoding = acceptEncoding.toLowerCase();
    }
    if (acceptEncoding != null && (acceptEncoding.indexOf("gzip") != -1 ||
        acceptEncoding.indexOf("deflate") != -1)) {

      String path = req.getServletPath();

      if (FilterHelpers.filterPath(request, pathList, pathSpec)) {
        log.info("CompressionFilter firing...");
        // Compression is supported and the path is eligible for compression,
        // so go we can go forward.  First, determine what type of compression
        // we're going to do based on what the client supports and the setting
        // of our init paramater.
        boolean gzipSupported    = false;
        boolean deflateSupported = false;
        if (acceptEncoding.indexOf("gzip") != -1) {
          gzipSupported = true;
        }
        if (acceptEncoding.indexOf("deflate") != -1) {
          deflateSupported = true;
        }
        // If both are supported...
        if (gzipSupported && deflateSupported) {
          // ... and configured to do GZip only or first, do GZip.
          if (compressType.equalsIgnoreCase("gzip_only") ||
              compressType.equalsIgnoreCase("gzip_first")) {
            log.info("Doing GZip compression (1) on path = " + path);
            CompressionResWrapper res =
              new CompressionResWrapper((HttpServletResponse)response,
                                        CompressionResWrapper.GZIP);
            filterChain.doFilter(req, res);
            res.complete();
            processChain = false;
          }
          // ... and configured to do Deflate only or first, do Deflate.
          if (compressType.equalsIgnoreCase("deflate_only") ||
              compressType.equalsIgnoreCase("deflate_first")) {
            log.info("Doing Deflate compression (1) on path = " + path);
            CompressionResWrapper res =
              new CompressionResWrapper((HttpServletResponse)response,
                                        CompressionResWrapper.DEFLATE);
            filterChain.doFilter(req, res);
            res.complete();
            processChain = false;
          }
        // If only GZip is supported...
        } else if (gzipSupported && !deflateSupported) {
          // ... and configured to anything except "deflate_only", do GZip.
          if (!compressType.equalsIgnoreCase("deflate_only")) {
            log.info("Doing GZip compression (2) on path = " + path);
            CompressionResWrapper res =
              new CompressionResWrapper((HttpServletResponse)response,
                                        CompressionResWrapper.GZIP);
            filterChain.doFilter(req, res);
            res.complete();
            processChain = false;
          }
        // If only Deflate is supported...
        } else if (!gzipSupported && deflateSupported) {
          // ... and configured to anything except "gzip_only", do Deflate.
          if (!compressType.equalsIgnoreCase("gzip_only")) {
            log.info("Doing Deflate compression (2) on path = " + path);
            CompressionResWrapper res =
              new CompressionResWrapper((HttpServletResponse)response,
                                        CompressionResWrapper.DEFLATE);
            filterChain.doFilter(req, res);
            res.complete();
            processChain = false;
          }
        }
      } // End if (!pathInCollection).

    } // End check for compression support.

    // If (a) compression is not supported or (b) the path is not eligible
    // for compression then processChain would not have been
    // set to false, so we need to continue the filter chain.
    if (processChain) {
      filterChain.doFilter(request, response);
    }

  } // End doFilter().


} // End class.
