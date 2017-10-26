/*
 * Copyright 2005 Herman van Rosmalen
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


package javawebparts.sampleapp;


import java.io.IOException;
import java.io.PrintWriter;
import javawebparts.core.org.apache.commons.lang.StringUtils;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This servlet returns a XML string with options for a select box.  Each
 * invocation of the servlet returns options based on the given parameter.<br>
 * This is used to demonstrate the stdSelectbox for AjaxTag.
 * <br><br>
 * Example configuration in web.xml:
 * <br><br>
 * &lt;servlet&gt;<br>
 * &nbsp;&nbsp;&lt;servlet-name&gt;AjaxSelectTestServlet&lt;/servlet-name&gt;
 * <br>
 * &nbsp;&nbsp;&lt;servlet-class&gt;javawebparts.sampleapp.
 * SelectServlet&lt;/servlet-class&gt;<br>
 * &lt;/servlet&gt;<br>
 * &lt;servlet-mapping&gt;<br>
 * &nbsp;&nbsp;&lt;servlet-name&gt;AjaxSelectTestServlet&lt;/servlet-name&gt;
 * <br>
 * &nbsp;&nbsp;&lt;url-pattern&gt;/ajaxSelectTest&lt;/url-pattern&gt;<br>
 * &lt;/servlet-mapping&gt;<br>
 *
 * @author <a href="mailto:herros@gmail.com">Herman van Rosmalen</a>
 */
public class AjaxSelectTestServlet extends HttpServlet {


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(AjaxSelectTestServlet.class);


  /**
   * doGet.  Calls doPost() to do real work.
   *
   * @param  request          HTTPServletRequest.
   * @param  response         HTTPServletResponse.
   * @throws ServletException ServletException.
   * @throws IOException      IOException.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    doPost(request, response);

  } // End doGet().


  /**
   * doPost.
   *
   * @param request           HTTPServletRequest.
   * @param response          HTTPServletResponse.
   * @throws ServletException ServletException.
   * @throws IOException      IOException.
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String       searchKey = request.getParameter("keyParm");
    StringBuffer result    = new StringBuffer();

    if (!StringUtils.isEmpty(searchKey)) {
      result
        .append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>")
        .append("<list>");
      for (int i = 1; i < 6; i++) {
        result
          .append("<option value='")
          .append(searchKey)
          .append("-")
          .append(i)
          .append("'>")
          .append(searchKey)
          .append("- Option ")
          .append(i)
          .append("</option>");
      }
      result.append("</list>");
    }

    PrintWriter out = response.getWriter();
    response.setContentType("text/xml");
    response.setHeader("Cache-Control", "no-cache");
    out.println(result.toString());

  } // End doPost().


} // End class.