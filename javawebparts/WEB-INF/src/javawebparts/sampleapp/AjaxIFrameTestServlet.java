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
import java.util.ArrayList;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This servlet puts a List of information in request and forwards to a JSP,
 * nothing else.  This is to demonstrate that the servlets used to demonstrate
 * AjaxParts Taglib in the sample app do not have to render a response in code.
 * All the others do, this one proves that the response can be rendered via JSP,
 * which is generally better.
 *
 * @author <a href="mailto:herros@gmail.com">Herman van Rosmalen</a>
 */
public class AjaxIFrameTestServlet extends HttpServlet {


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(AjaxIFrameTestServlet.class);


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

    log.info("AjaxIFrameTestServlet.doPost()...");
    ArrayList theList = new ArrayList();
    theList.add("Grand Mof Tarkin");
    theList.add("Darth Vader");
    theList.add("Emperor Palpatine");
    request.setAttribute("theList", theList);
    log.info("Done, forwarding to JSP to render response...");
    request.getRequestDispatcher(
      "/packages_jsp/ajaxparts/ajaxIFrameTest.jsp").forward(request, response);

  } // End doPost().


} // End class.