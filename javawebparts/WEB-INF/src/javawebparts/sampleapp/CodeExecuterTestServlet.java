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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;


/**
 * This servlet is used by a test of the AjaxParts Taglib.  It tests the
 * std:CodeExecuter response handler.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class CodeExecuterTestServlet extends HttpServlet {


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

    PrintWriter out = response.getWriter();
    out.println("alert(\"What you are seeing is the result of\\n" +
      "some Javascript being returned by a servlet\\n" +
      "(a simple alert() function call) and executed by\\n" +
      "the stdCodeExecuter AjaxParts Taglib response handler.\\n\\n" +
      "Once you dismiss this message popup, the background color\\n" +
      "of the page will cycle through red, green, blue, black and\\n" +
      "finally settle back to white.  This is also code that is\\n" +
      "returned by the server and shows that you can do more\\n" +
      "complex things than just simple alert popups.\");");
    out.println("backColor=new Array();");
    out.println("dwellTime=500;");
    out.println("backColor[0]=\"#ff0000\";");
    out.println("backColor[1]=\"#00ff00\";");
    out.println("backColor[2]=\"#0000ff\";");
    out.println("backColor[3]=\"#000000\";");
    out.println("backColor[4]=\"#ffffff\";");
    out.println("d=dwellTime;");
    out.println("t=setTimeout(function(){document.bgColor=backColor[0];}," +
      "(d-d));");
    out.println("t=setTimeout(function(){document.bgColor=backColor[1];}," +
      "(d));");
    out.println("t=setTimeout(function(){document.bgColor=backColor[2];}," +
      "(d*2));");
    out.println("t=setTimeout(function(){document.bgColor=backColor[3];}," +
      "(d*3));");
    out.println("t=setTimeout(function(){document.bgColor=backColor[4];}," +
      "(d*4));");
    out.println("t=null;");

  } // End doPost().


} // End class.
