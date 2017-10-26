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
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.StringTokenizer;
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
 * This filter rejects or allows a request based on the IP address it comes
 * from.
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
 * <li><b>addressSpec</b> - This determines whether the list of IP addresses
 * is a list of addresses to allow or to disallow.  Required: Yes.
 * Default: None.
 * <br><br>
 * <li><b>matchType</b> - What type of address matching to perform.
 * The valid values are "standard" or "regex". Required: Yes.
 * Default: "standard".</li>
 * <br><br>
 * <li><b>addressList</b> - This is the list of IP address that will either be
 * denied access or allowed access, depending on the setting of addressSpec.
 * <i>Standard:</i> This is comma-separated list of values where each value can
 * be the special word "localhost", or a literal IP address in the form
 * 999.999.999.999, or a literal IP address in the form *.*.*.* where any of the
 * octets can be the wildcard character (*), or an IP range in the form
 * 999.999.999.999-999.999.999.999.<br>
 * <i>regex:</i> This is comma-separated list of regular expressions. e.g.
 * localhost, 127\.0\.0\.1, more generally you can match any IP using this kind
 * of pattern (25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.
 * ((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.
 * ){2}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)
 * </li>
 * <br><br>
 * <li><b>redirectTo</b> - The URL to redirect to if the request is denied.
 * Required: Yes (either this or forwardTo).  Default: None.</li>
 * <br><br>
 * <li><b>forwardTo</b> - The URL to forward to if the request is denied.
 * Required: Yes (either this or redirectTo).  Default: None.</li>
 * <br><br>
 * <li><b>headerName</b> - The header name to examine if behind load balancers.
 * Required: No.  Default: None.</li>
 * </ul>
 * <br>
 * Example configuration in web.xml:
 * <br><br>
 * &lt;filter&gt;<br>
 * &nbsp;&nbsp;&lt;filter-name&gt;IPAccessControlFilter&lt;/filter-name&gt;<br>
 * &nbsp;&nbsp;&lt;filter-class&gt;javawebparts.filter.
 * IPAccessControlFilter&lt;/filter-class&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;pathSpec&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;include&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;pathList&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;*&zwj;/IACTestTarget.jsp&lt;/
 * param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;addressSpec&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;include&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;matchType&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;regex&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;addressList&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;localhost&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;forwardTo&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;/IACReject.jsp&lt;/param-value&gt;
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;headerName&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;X-Client-IP&lt;/param-value&gt;
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &lt;/filter&gt;
 * <br><br>
 * &lt;filter-mapping&gt;<br>
 * &nbsp;&nbsp;&lt;filter-name&gt;IPAccessControlFilter&lt;/filter-name&gt;<br>
 * &nbsp;&nbsp;&lt;url-pattern&gt;/*&lt;/url-pattern&gt;<br>
 * &lt;/filter-mapping&gt;
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 * @author <a href="mailto:yhaudry@gmail.com">Yannick Haudry</a>.
 */
public class IPAccessControlFilter implements Filter {


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
      Class.forName("javax.servlet.ServletException");
      Class.forName("javax.servlet.ServletRequest");
      Class.forName("javax.servlet.ServletResponse");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("IPAccessControlFilter" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(IPAccessControlFilter.class);


  /**
   * Whether pathList includes or excludes.
   */
  private String pathSpec;


  /**
   * List of paths for filter functionality determination.
   */
  private ArrayList pathList = new ArrayList();


  /**
   * Whether the list of IP addresses is an "include" list or an "exclude" list.
   */
  private String addressSpec;


  /**
   * What type of address matching processing we want this filter to do.
   * Valid values are "standard", "regex".
   */
  private String matchType;


  /**
   * The comma-separated list of IP addresses to allow or deny.
   */
  private List addressList;


  /**
   * The list of Pattern to allow or deny IP addresses.
   */
  private Pattern[] addressPattern = new Pattern[0];


  /**
   * A path to redirect to when access is denied.
   */
  private String redirectTo;


  /**
   * A path to forward to when access is denied.
   */
  private String forwardTo;


  /**
   * Header name to examine instead of doing getRemoteAddr()
   * to retrieve Client IP.
   */
  private String headerName;


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
    redirectTo = FilterHelpers.initRedirectTo(filterConfig);
    forwardTo  = FilterHelpers.initForwardTo(filterConfig);
    FilterHelpers.checkRedirectForwardTo(getClass().getName(),
      redirectTo, forwardTo);

    // Get the matchType init parameter.
    matchType = filterConfig.getInitParameter("matchType");
    if (matchType == null || matchType.equalsIgnoreCase("")) {
      matchType = "standard";
    }
    log.info("matchType = " + matchType);

    // Get the addressSpec and addressList, and parse the addressList into
    // an ArrayList for later usage.
    addressSpec = filterConfig.getInitParameter("addressSpec");
    if (addressSpec == null || addressSpec.equalsIgnoreCase("")) {
      String es = getClass().getName() + " could not initialize " +
        "because required parameter addressSpec was not found";
      log.error(es);
      throw new ServletException(es);
    }
    log.info("addressSpec = " + addressSpec);
    String aList = filterConfig.getInitParameter("addressList");
    if (aList == null || aList.equalsIgnoreCase("")) {
      String es = getClass().getName() + " could not initialize " +
        "because required parameter addressList was not found";
      log.error(es);
      throw new ServletException(es);
    }
    addressList = new ArrayList();
    addressList = Arrays.asList(aList.split(",")); // ;-)

    // Process addressList to set addressPattern.
    if (matchType.equalsIgnoreCase("regex")) {

      ArrayList regexList = new ArrayList();
      for (Iterator it = addressList.iterator(); it.hasNext();) {

        String ipPattern = (String) it.next();

        // use word boundary (! maybe not really needed here)
        if (!ipPattern.startsWith("\\b")) {
          ipPattern = "\\b" + ipPattern;
        }
        if (!ipPattern.endsWith("\\b")) {
          ipPattern = ipPattern + "\\b";
        }

        try {
          regexList.add(Pattern.compile(ipPattern));
        } catch (PatternSyntaxException e) {
          String es = getClass().getName() + " could not initialize " +
          "Problem with pattern " + e.getPattern() +
          " at index " + e.getIndex();
          log.error(es);
          throw new ServletException(e);
        }

      }

      Pattern[] regexArray = new Pattern[regexList.size()];
      addressPattern = (Pattern[])regexList.toArray(regexArray);
    }

    // Get the headerName init parameter (if any).
    headerName = filterConfig.getInitParameter("headerName");

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
    FilterChain filterChain) throws ServletException, IOException {

    if (FilterHelpers.filterPath(request, pathList, pathSpec)) {

      log.info("IPAccessControlFilter firing...");

      // Get the server's IP address and the client's IP address.
      InetAddress ia         = InetAddress.getLocalHost();
      String      localAddr  = ia.getHostAddress();
      String remoteAddr = null;
      if (headerName == null) {
        remoteAddr = getRemoteAddr(request);
      } else {
        remoteAddr = getHeaderAddr(request);
      }

      log.info("localAddr = " + localAddr);
      log.info("remoteAddr = " + remoteAddr);

      // If the client's IP address is NOT covered by the address list and
      // the addressSpec is "include", then the request must not continue.
      // If the client's IP address IS covered by the address list and the
      // addressSpec is "exclude", then the request must not continue.
      // In all other cases, the request should continue.
      if (addressSpec.equalsIgnoreCase("include")) {
        if (matchType.equalsIgnoreCase("standard")) {
          if (!addressInList(localAddr, remoteAddr)) {
            FilterHelpers.redirectOrForward(redirectTo, forwardTo,
                request, response);
            return;
          }
        } else if (matchType.equalsIgnoreCase("regex")) {
          if (!addressInPattern(localAddr, remoteAddr)) {
            FilterHelpers.redirectOrForward(redirectTo, forwardTo,
                request, response);
            return;
          }
        }

      }
      if (addressSpec.equalsIgnoreCase("exclude")) {
        if (matchType.equalsIgnoreCase("standard")) {
          if (addressInList(localAddr, remoteAddr)) {
            FilterHelpers.redirectOrForward(redirectTo, forwardTo,
                request, response);
            return;
          }
        } else if (matchType.equalsIgnoreCase("regex")) {
          if (addressInPattern(localAddr, remoteAddr)) {
            FilterHelpers.redirectOrForward(redirectTo, forwardTo,
                request, response);
            return;
          }
        }

      }
    }

    // If we're here, the request continue.
    filterChain.doFilter(request, response);


  } // End doFilter().


  /**
   * Get Client IP using getRemoteAddr method.
   *
   * @param  request ServletRequest.
   * @return         request.getRemoteAddre().
   */
  protected String getRemoteAddr(ServletRequest request) {

    return request.getRemoteAddr();

  } // End getRemoteAddr().


  /**
   * Get Client IP through a custom header.
   *
   * @param  request ServletRequest.
   * @return         Header name.
   */
  protected String getHeaderAddr(ServletRequest request) {

    return ((HttpServletRequest)request).getHeader(headerName);

  } // End getHeaderAddr().


  /**
   * Called to determine if a given remote IP address is valid against the
   * regular expressions of addresses read in from config.
   *
   * @param  localAddr  The IP address of the server.
   * @param  remoteAddr The IP address of the client.
   * @return            True if the address matches one of the regex, false
   *                    if not.
   */
  private boolean addressInPattern(String localAddr, String remoteAddr) {

    // Scan the list of expressions configured and see if the remoteAddr
    // matches any.
    boolean ret = false;
    for (int i = 0; i < addressPattern.length; i++) {

      if (addressPattern[i].matcher(remoteAddr).matches()) {
        log.info("Match Pattern " + i);
        return true;
      }

    } // End iterator loop.

    return ret;

  } // End addressInPattern().


  /**
   * Called to determine if a given remote IP address is valid against the list
   * of addresses read in from config.
   *
   * @param  localAddr  The IP address of the server.
   * @param  remoteAddr The IP address of the client.
   * @return            True if the address is in the list, false if not.
   */
  private boolean addressInList(String localAddr, String remoteAddr) {

    // Scan the list of addresses configured and see if the remoteAddr
    // matches any.
    boolean ret = false;
    for (Iterator it = addressList.iterator(); it.hasNext();) {

      String nextAddr = (String)it.next();

      // Match test 1: localhost.
      if (nextAddr.equalsIgnoreCase("localhost")) {
        log.info("Match test 1");
        if (remoteAddr.equalsIgnoreCase(localAddr)) {
          ret = true;
        }
      }

      // Match test 2: exact address match.
      if (nextAddr.indexOf("*") == -1 & nextAddr.indexOf("-") == -1) {
        log.info("Match test 2");
        if (remoteAddr.equalsIgnoreCase(nextAddr)) {
          ret = true;
        }
      }

      // Match test 3: A single address with wildcards.
      if (nextAddr.indexOf("*") != -1) {
        log.info("Match test 3");
        // Get all four octets from both the next address in the list and
        // the remote address so we can examine them individually.
        StringTokenizer nt = new StringTokenizer(nextAddr,   ".");
        StringTokenizer rt = new StringTokenizer(remoteAddr, ".");
        String nextAddrOctet1   = (String)nt.nextToken();
        String nextAddrOctet2   = (String)nt.nextToken();
        String nextAddrOctet3   = (String)nt.nextToken();
        String nextAddrOctet4   = (String)nt.nextToken();
        String remoteAddrOctet1 = (String)rt.nextToken();
        String remoteAddrOctet2 = (String)rt.nextToken();
        String remoteAddrOctet3 = (String)rt.nextToken();
        String remoteAddrOctet4 = (String)rt.nextToken();
        // Now, for each octet, see if we have either an exact match or a
        // wildcard match, and if so set the appropriate octet flag.
        boolean octet1Ok = false;
        boolean octet2Ok = false;
        boolean octet3Ok = false;
        boolean octet4Ok = false;
        if (remoteAddrOctet1.equalsIgnoreCase(nextAddrOctet1) ||
            nextAddrOctet1.equalsIgnoreCase("*")) {
          octet1Ok = true;
        }
        if (remoteAddrOctet2.equalsIgnoreCase(nextAddrOctet2) ||
            nextAddrOctet2.equalsIgnoreCase("*")) {
          octet2Ok = true;
        }
        if (remoteAddrOctet3.equalsIgnoreCase(nextAddrOctet3) ||
            nextAddrOctet3.equalsIgnoreCase("*")) {
          octet3Ok = true;
        }
        if (remoteAddrOctet4.equalsIgnoreCase(nextAddrOctet4) ||
            nextAddrOctet4.equalsIgnoreCase("*")) {
          octet4Ok = true;
        }
        // Finally, if all four flags are true, the address is OK.
        if (octet1Ok & octet2Ok & octet3Ok & octet4Ok) {
          ret = true;
        }
      }

      // Match test 4: IP range.
      if (nextAddr.indexOf("-") != -1) {
        log.info("Match test 4");
        StringTokenizer st             = new StringTokenizer(nextAddr, "-");
        String          rangeStart     = st.nextToken();
        String          rangeEnd       = st.nextToken();
        long            rangeStartLong = ipToLong(rangeStart);
        long            rangeEndLong   = ipToLong(rangeEnd);
        long            remoteAddrLong = ipToLong(remoteAddr);
        if (remoteAddrLong >= rangeStartLong &&
            remoteAddrLong <= rangeEndLong) {
          ret = true;
        }
      }

    } // End iterator loop.

    return ret;

  } // End addressInList().


  /**
   * Method that converts an IP address to a long.
   *
   * @param  ip The IP address to convert.
   * @return    The IP address as a long.
   */
  private static long ipToLong(String ip) {

    StringTokenizer st  = new StringTokenizer(ip, ".");
    int o1 = Integer.parseInt((String)st.nextToken());
    int o2 = Integer.parseInt((String)st.nextToken());
    int o3 = Integer.parseInt((String)st.nextToken());
    int o4 = Integer.parseInt((String)st.nextToken());
    String o1S = Integer.toBinaryString(o1).trim();
    String o2S = Integer.toBinaryString(o2).trim();
    String o3S = Integer.toBinaryString(o3).trim();
    String o4S = Integer.toBinaryString(o4).trim();
    o1S = padBinByteStr(o1S);
    o2S = padBinByteStr(o2S);
    o3S = padBinByteStr(o3S);
    o4S = padBinByteStr(o4S);
    String bin = o1S + o2S + o3S + o4S;
    long res = 0;
    long j   = 2147483648L;
    for (int i = 0; i < 32; i++) {
      char c = bin.charAt(i);
      if (c == '1') {
        res = res + j;
      }
      j = j / 2;
    }
    return res;

  } // End ipToLong().


  /**
   * Method that pads (prefixes) a string representation of a byte with 0's.
   *
   * @param  binByte String of the byte (maybe less than 8 bits) to pad.
   * @return         String of the byte guaranteed to have 8 bits.
   */
  private static String padBinByteStr(String binByte) {

    if (binByte.length() == 8) {
      return binByte;
    }
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < (8 - binByte.length()); i++) {
      sb.append("0");
    }
    sb.append(binByte);
    return sb.toString();

  } // End padBinByteStr().


} // End class.
