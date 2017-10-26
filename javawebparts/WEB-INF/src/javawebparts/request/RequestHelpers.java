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


package javawebparts.request;


import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javawebparts.core.CaseInsensitiveMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class contains static methods that are too small to warrant their
 * own class.  General request-related utility methods.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>, with
 *         contributions from Wendy Smoak.
 */
public final class RequestHelpers {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javax.servlet.http.Cookie");
      Class.forName("javax.servlet.http.HttpServletRequest");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("RequestHelpers" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(RequestHelpers.class);


  /**
   * This is a utility class, so we want a private noarg constructor so
   * instances cannot be created.
   */
  private RequestHelpers() {
  } // End RequestHelpers().


  /**
   * This method is a convenience method that calls the other three and
   * dumps all headers, attributes and request to the log.
   *
   * @param request A valid HTTPServletRequest object.
   */
  public static void logAllRequestInfo(HttpServletRequest request) {

    if (request != null) {
      log.info("logAllRequestInfo: Attributes = " +
        getRequestAttributes(request));
      log.info("logAllRequestInfo: Parameters = " +
        getRequestParameters(request));
      log.info("logAllRequestInfo: Headers = " +
        getRequestHeaders(request));
    }

  } // End logAllRequestInfo().


  /**
   * This method is a convenience method that calls the other three and
   * returns a Map containing all request attributes, parameters and
   * headers.
   *
   * @param request A valid HTTPServletRequest object.
   * @return        A Map containing three HashMaps, one each for
   *                headers, attributes and parameters.  Returns null
   *                if request is null.
   */
  public static Map getAllRequestInfo(HttpServletRequest request) {

    HashMap hm = null;
    if (request != null) {
      hm = new HashMap();
      hm.put("attributes", getRequestAttributes(request));
      hm.put("parameters", getRequestParameters(request));
      hm.put("headers",    getRequestHeaders(request));
    }
    return hm;

  } // End getAllRequestInfo().


  /**
   * This method will return a Map that can be displayed which contains
   * all request attributes.
   *
   * @param request A valid HTTPServletRequest object.
   * @return        A Map of all request attributes.  Returns null if
   *                request is null;
   */
  public static Map getRequestAttributes(HttpServletRequest request) {

    HashMap hm = null;
    if (request != null) {
      hm = new HashMap();
      for (Enumeration e = request.getAttributeNames(); e.hasMoreElements();) {
        String next = (String)e.nextElement();
        hm.put(next, request.getAttribute(next));
      }
    }
    return hm;

  } // End getRequestAttributes().


  /**
   * This method will return a Map that can be displayed which contains
   * all request parameters.
   *
   * @param  request A valid HTTPServletRequest object.
   * @return         A Map of all request parameters.  Returns null if
   *                 request is null.
   */
  public static Map getRequestParameters(HttpServletRequest request) {

    HashMap hm = null;
    if (request != null) {
      hm = new HashMap();
      for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
        String next = (String)e.nextElement();
        hm.put(next, Arrays.asList(request.getParameterValues(next)));
      }
    }
    return hm;

  } // End getRequestParameters().


  /**
   * This method will return a Map that can be displayed which contains
   * all request headers.
   *
   * @param  request A valid HTTPServletRequest object.
   * @return         A Map of all request headers.  Returns null if request
   *                 is null.
   */
  public static Map getRequestHeaders(HttpServletRequest request) {

    Map headersMap = null;
    if (request != null) {
      headersMap = new CaseInsensitiveMap();
      for (Enumeration en = request.getHeaderNames(); en.hasMoreElements();) {
        String headerName = (String)en.nextElement();
        List headerValues = new ArrayList();
        for (Enumeration headerValuesEnum = request.getHeaders(headerName); 
            headerValuesEnum.hasMoreElements(); ) { 
          headerValues.add(headerValuesEnum.nextElement());
        }
        headersMap.put(headerName, headerValues);
      }
    }
    return Collections.unmodifiableMap(headersMap);

  } // End getRequestHeaders().


  /**
   * This method will return the Cookie with the provided name, if it is
   * present in the request.  Contributed by Wendy Smoak.  Null is returned
   * if the cookie is not found.
   *
   * @param  request    A valid HTTPServletRequest object.
   * @param  cookieName The name of the Cookie
   * @return            The Cookie with the provided name.
   *                    Returns null if request is null or the Cookie is not
   *                    present.
   */
  public static Cookie getCookie(HttpServletRequest request,
    String cookieName) {


    Cookie c = null;
    if (request != null) {
      Cookie[] cookies = request.getCookies();
      boolean found = false;
      if (cookies != null && cookies.length > 0) {
        for (int i = 0; i < cookies.length && !found; i++) {
          if (cookies[i].getName().equals(cookieName)) {
            c = cookies[i];
            found = true;
          }
        }
      }
    }
    return c;

  } // End getCookie().


  /**
   * This method will return the value of the Cookie with the provided name,
   * if it is present in the request.  Contributed by Wendy Smoak.
   *
   * @param  request    A valid HTTPServletRequest object.
   * @param  cookieName The name of the Cookie
   * @return            The String value of the Cookie with the provided name.
   *                    Returns null if request is null or the Cookie is not
   *                    present.
   */
  public static String getCookieValue(HttpServletRequest request,
    String cookieName) {

    Cookie c = getCookie(request, cookieName);
    String value = null;
    if (c != null) {
      value = c.getValue();
    }
    return value;

  } // End getCookieValue().


  /**
   * This method will return a Map of all cookies in the request.
   *
   * @param  request A valid HTTPServletRequest object.
   * @return         Map of all cookies.
   */
  public static Map getCookies(HttpServletRequest request) {

    Cookie[] cookies = request.getCookies();
    HashMap hm = new HashMap();
    if (cookies != null) {
      for (int i = 0; i < cookies.length; i++) {
        hm.put(cookies[i].getName(), cookies[i].getValue());
      }
    }
    return hm;

  } // End getCookies().


  /**
   * Returns a 32 character string uniquely identifying the given request
   * object.
   *
   * @param  request A valid HTTPServletRequest object.
   * @return         Map of all cookies.
   */
  public static String generateGUID(HttpServletRequest request) {

    String out = "";
    try {
      // Construct a string that is comprised of:
      // Remote IP Address + Host IP Address + Date (yyyyMMdd) +
      // Time (hhmmssSSa) + Requested Path + Session ID +
      // HashCode Of ParameterMap
      StringBuffer sb = new StringBuffer(1024);
      sb.append(request.getRemoteAddr());
      InetAddress ia = InetAddress.getLocalHost();
      sb.append(ia.getHostAddress());
      sb.append(new SimpleDateFormat("yyyyMMddhhmmssSSa").format(new Date()));
      String path = request.getServletPath();
      String pathInfo = request.getPathInfo();
      if (pathInfo != null) {
        path += pathInfo;
      }
      sb.append(path);
      sb.append(request.getSession(false));
      sb.append(request.getParameterMap().hashCode());
      String str = sb.toString();
      // Now encode the string using an MD5 encryption algorithm.
      MessageDigest md = MessageDigest.getInstance("md5");
      md.update(str.getBytes());
      byte[] digest = md.digest();
      StringBuffer hexStr = new StringBuffer(1024);
      for (int i = 0; i < digest.length; i++) {
        str = Integer.toHexString(0xFF & digest[i]);
        if (str.length() < 2) {
          str = "0" + str;
        }
        hexStr.append(str);
      }
      out = hexStr.toString();
    } catch (NoSuchAlgorithmException nsae) {
      log.error(nsae);
    } catch (UnknownHostException uhe) {
      log.error(uhe);
    }
    // Return the encrypted string.  It should be unique based on the
    // components that comprise the plain text string, and should always be
    // 32 characters thanks to the MD5 algorithm.
    return out;

  } // End generateGUID().


  /**
   * This method will return the body content of an HTTP request as a String.
   *
   * @param  request     A valid HTTPServletRequest object.
   * @return             A String containing the body content of the request.
   * @throws IOException Catch-all exception.
   */
  public static String getBodyContent(HttpServletRequest request)
    throws IOException {

    BufferedReader br          = request.getReader();
    String         nextLine    = "";
    StringBuffer   bodyContent = new StringBuffer();
    nextLine = br.readLine();
    while (nextLine != null) {
      bodyContent.append(nextLine);
      nextLine = br.readLine();
    }
    return bodyContent.toString();

  } // End getBodyContent().


} // End class.
