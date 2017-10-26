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


package javawebparts.sampleapp;


import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;


/**
 * This servlet is used to test the ParameterMungerFilter.  It accepts a
 * form submission and simply takes the parameters and puts them in
 * request attributes for display on the page.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class ParameterMungerFilterTestServlet extends HttpServlet {


  /**
   * doGet.  Calls doPost() to do real work.
   *
   * @param  request          HTTPServletRequest.
   * @param  response         HTTPServletResponse.
   * @throws ServletException ServletException.
   * @throws IOException      IOExcpetion.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {

    doPost(request, response);

  } // End doGet().


  /**
   * doPost.
   *
   * @param  request          HTTPServletRequest.
   * @param  response         HTTPServletResponse.
   * @throws ServletException ServletException.
   * @throws IOException      IOExcpetion.
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
                     throws ServletException, IOException {

    String srcDoc = (String)request.getParameter("srcDoc");

    // Grab all parameters and put them in request as attributes for display.
    request.setAttribute("firstName",
                        (String)request.getParameter("firstName"));
    request.setAttribute("lastName",  (String)request.getParameter("lastName"));
    request.setAttribute("quote",     (String)request.getParameter("quote"));

    // Forward to the appropriate JSP.  Slightly different when we're coming
    // from the Everything page as opposed to the package page.
    String uri = "";
    if (srcDoc == null || srcDoc.equalsIgnoreCase("")) {
      uri = "/packages_jsp/filter/index.jsp" +
            "?hash=ParameterMungerFilterTest";
    } else {
      uri = "/packages_jsp/everythingPage/index.jsp" +
            "?hash=ParameterMungerFilterTest";
    }
    RequestDispatcher rd = request.getRequestDispatcher(uri);
    rd.forward(request, response);

  } // End doPost().


} // End class.
