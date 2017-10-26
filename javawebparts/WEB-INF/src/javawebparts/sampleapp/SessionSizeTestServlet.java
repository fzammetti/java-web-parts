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
import javawebparts.filter.CacheControlFilter;
import javawebparts.session.SessionSize;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;


/**
 * This servlet is used to test the SessionSize.getSessionSize() method.
 * Pass in a parameter "fail" set to either "true" or "false".  When "true",
 * a non-serializable object will be added to session to display what
 * error information the SessionSize class provides.  If "false", the test
 * will succeed.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class SessionSizeTestServlet extends HttpServlet {


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
    // the session size regardless of the non-serializable objects.
    String ignore = (String)request.getParameter("ignore");

    // Get the current session, creating one if it doesn't exist.
    HttpSession session = request.getSession(true);

    // Get rid of that session and create a new one.  This is done so that
    // this servlet being called twice from the test page back-to-back
    // will work (i.e., if we did the failure test first, a session with a
    // non-serializable object would be present, so trying the no failure test
    // right after would still fail).
    session.invalidate();
    session = request.getSession(true);

    // Put some serializable objects in session.
    session.setAttribute("TestString", "This is a test string");
    HashMap hm = new HashMap();
    hm.put("item1", "This is the first item");
    hm.put("item2", "This is the second item");
    session.setAttribute("myHashMap", hm);

    // Put a non-serializable object in session, if doing a failure test.
    if (fail.equalsIgnoreCase("true")) {
      session.setAttribute("notSerializable", new CacheControlFilter());
    }

    // Set an attribute in request indicating the session size.
    SessionSize ss = new SessionSize(session);
    if (ignore != null && ignore.equalsIgnoreCase("true")) {
      ss.setIgnoreNonSerializable(true);
    }
    int sessionSize = ss.getSessionSize();
    String out = "";
    if (sessionSize == -1) {
      // Session size could not be determined, so display error information.
      List reason = ss.whyFailed();
      out = "<br/>Could not get session size: " + reason + "<br/>";
    } else {
      // Session size was determined, display it.
      out = "<br/>sessionSize = " + sessionSize + "<br/>";
    }
    request.setAttribute("sessionSize", out);

    // Forward to the appropriate JSP.  Slightly different when we're coming
    // from the Everything page as opposed to the package page.
    String uri = "";
    if (srcDoc == null || srcDoc.equalsIgnoreCase("")) {
      uri = "/packages_jsp/session/index.jsp" +
            "?hash=SessionSizeGetSessionSizeTest";
    } else {
      uri = "/packages_jsp/everythingPage/index.jsp" +
            "?hash=SessionSizeGetSessionSizeTest";
    }
    RequestDispatcher rd = request.getRequestDispatcher(uri);
    rd.forward(request, response);

  } // End doPost().


} // End class.
