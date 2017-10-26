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
import java.io.PrintWriter;
import java.util.HashMap;
import javawebparts.request.RequestHelpers;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This servlet is used by a test of the AjaxParts Taglib.  It tests the
 * std:FormSender request handler.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class FormSenderTestServlet extends HttpServlet {


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(FormSenderTestServlet.class);


  /**
   * doGet.
   *
   * @param  request          HTTPServletRequest.
   * @param  response         HTTPServletResponse.
   * @throws ServletException ServletException.
   * @throws IOException      IOExcpetion.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    processRequest(request, response, "get");

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

    processRequest(request, response, "POST");

  } // End doPost().


  /**
   * processRequest().  This is called from doPost() and doGet().  The reason
   * for this is so we can determine whether the request was a GET or POST,
   * and based on this we know what the incoming data type is because in the
   * example app, the "parameters" example used GET while the "json" and "xml"
   * versions use POST (we can differentiate "json" and "xml" by looking at the
   * first character in the post body, it'll always be < for "xml").  This is
   * clearly not something you'd ever do in a real app, but for our JWP example
   * app it's nice, simple and will do the trick.  Also not that the approach to
   * "parsing" both the XML and JSON is in no way shape or form condoned as
   * anything but a massive hack... however, again, because this is only used
   * in the sample app, we can get away with it.
   *
   * @param  request          HTTPServletRequest.
   * @param  response         HTTPServletResponse.
   * @throws ServletException ServletException.
   * @throws IOException      IOExcpetion.
   */
  private void processRequest(HttpServletRequest request,
    HttpServletResponse response, String inMethod)
    throws ServletException, IOException {

    HashMap formValues = new HashMap();
    String bodyContent = RequestHelpers.getBodyContent(request);
    log.debug("inMethod = " + inMethod);
    log.debug("bodyContent = " + bodyContent);
    String testCode = request.getParameter("testCode");

    // Plain old parameters (that's the only time testCode would come through
    // as a request parameter, otherwise it'd be part of JSON or XML).
    if (testCode != null) {

      formValues.put("firstName", request.getParameter("firstName"));
      formValues.put("lastName", request.getParameter("lastName"));
      formValues.put("attractiveness", request.getParameter("attractiveness"));
      formValues.put("marryingMaterial",
        request.getParameter("marryingMaterial"));
      formValues.put("neverHappen", request.getParameter("neverHappen"));

    // JSON or XML.
    } else {

      // XML.
      if (bodyContent.charAt(0) == '<') {

        int start = 0;
        int end = 0;
        start = bodyContent.indexOf("<firstName>");
        end = bodyContent.indexOf("</firstName>");
        formValues.put("firstName", bodyContent.substring(start + 11, end));
        start = bodyContent.indexOf("<lastName>");
        end = bodyContent.indexOf("</lastName>");
        formValues.put("lastName", bodyContent.substring(start + 10, end));
        start = bodyContent.indexOf("<attractiveness>");
        end = bodyContent.indexOf("</attractiveness>");
        formValues.put("attractiveness", bodyContent.substring(start + 16, end));
        start = bodyContent.indexOf("<marryingMaterial>");
        end = bodyContent.indexOf("</marryingMaterial>");
        formValues.put("marryingMaterial", bodyContent.substring(start + 18, end));
        start = bodyContent.indexOf("<neverHappen>");
        end = bodyContent.indexOf("</neverHappen>");
        formValues.put("neverHappen", bodyContent.substring(start + 13, end));

      // JSON.
      } else {

        int start = 0;
        int end = 0;
        start = bodyContent.indexOf("\"firstName\"");
        end = bodyContent.indexOf(",", start);
        formValues.put("firstName", bodyContent.substring(start + 15, end - 1));
        start = bodyContent.indexOf("\"lastName\"");
        end = bodyContent.indexOf(",", start);
        formValues.put("lastName", bodyContent.substring(start + 14, end - 1));
        start = bodyContent.indexOf("\"attractiveness\"");
        end = bodyContent.indexOf(",", start);
        formValues.put("attractiveness", bodyContent.substring(start + 20,
          end - 1));
        start = bodyContent.indexOf("\"marryingMaterial\"");
        end = bodyContent.indexOf(",", start);
        formValues.put("marryingMaterial", bodyContent.substring(start + 22,
          end - 1));
        start = bodyContent.indexOf("\"neverHappen\"");
        end = bodyContent.indexOf("}", start);
        formValues.put("neverHappen", bodyContent.substring(start + 17, end - 2));

      } // End JSON or XML.

    }

    // Form response and that's that (and yes, I know all the string cats are
    // bad, so I'll just repeat my refrain: it's an example app, not a
    // production-quality bit of code, so it'll do just fine!).
    log.debug("formValues = " + formValues);
    if (((String)formValues.get("neverHappen")).equalsIgnoreCase("true")) {
      formValues.put("neverHappen", "are at least realistic but still");
    } else {
      formValues.put("neverHappen", "are delusional and");
    }
    PrintWriter out = response.getWriter();
    out.println("It is a known fact that " + formValues.get("firstName") +
      " " + formValues.get("lastName") + " is " +
      formValues.get("attractiveness") + " and " +
      formValues.get("marryingMaterial") + " marrying material.  Regardless, " +
      "it is just as well-known a fact that you " +
      formValues.get("neverHappen") + " do not have any sort of chance in " +
      "this life, or any other, so just deal with it!"
    );

  } // End processRequest().


} // End class.
