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
import java.util.HashMap;
import java.util.List;
import javawebparts.context.ContextSize;
import javawebparts.filter.CacheControlFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;


/**
 * This servlet is used to test the ContextSize.getContextSize() method.
 * Pass in a parameter "fail" set to either "true" or "false".  When "true",
 * a non-serializable object will be added to context to display what
 * error information the ContextSize class provides.  If "false", the test
 * will succeed.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class ContextSizeTestServlet extends HttpServlet {


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

    // Get the parameter that will tell us whether we are going to make the
    // test fail or not.
    String fail = (String)request.getParameter("fail");

    // Get the parameter that will tell us whether we are going to calculate
    // the context size regardless of the non-serializable objects.
    String ignore = (String)request.getParameter("ignore");

    // Get the current context.
    ServletContext context = getServletContext();

    // We need to clean up the context attributes that are non-serializable if
    // they exist already.  This is done so that this test can be done
    // multiple times.  We assume nothing is added to context other than what
    // the other tests, and this one, add (i.e., no one has altered the
    // sample app).
    if (context.getAttribute("notSerializable") != null) {
      context.removeAttribute("notSerializable");
    }

    // Put some serializable objects in context.
    context.setAttribute("TestString", "This is a test string");
    HashMap hm = new HashMap();
    hm.put("item1", "This is the first item");
    hm.put("item2", "This is the second item");
    context.setAttribute("myHashMap", hm);

    // Put a non-serializable object in context, if doing a failure test.
    if (fail.equalsIgnoreCase("true")) {
      context.setAttribute("notSerializable", new CacheControlFilter());
    }

    // Set an attribute in request indicating the Context size.
    ContextSize cs = new ContextSize(context);
    if (ignore != null && ignore.equalsIgnoreCase("true")) {
      cs.setIgnoreNonSerializable(true);
    }
    int contextSize = cs.getContextSize();
    String out = "";
    if (contextSize == -1) {
      // Context size could not be determined, so display error information.
      List reason = cs.whyFailed();
      out = "<br/>Could not get context size: " + reason + "<br/>";
    } else {
      // Context size was determined, display it.
      out = "<br/>contextSize = " + contextSize + "<br/>";
    }
    request.setAttribute("contextSize", out);

    // Forward to the appropriate JSP.  Slightly different when we're coming
    // from the Everything page as opposed to the package page.
    String uri = "";
    if (srcDoc == null || srcDoc.equalsIgnoreCase("")) {
      uri = "/packages_jsp/context/index.jsp" +
            "?hash=ContextSizeGetContextSizeTest";
    } else {
      uri = "/packages_jsp/everythingPage/index.jsp" +
            "?hash=ContextSizeGetContextSizeTest";
    }
    RequestDispatcher rd = request.getRequestDispatcher(uri);
    rd.forward(request, response);

  } // End doPost().


} // End class.
