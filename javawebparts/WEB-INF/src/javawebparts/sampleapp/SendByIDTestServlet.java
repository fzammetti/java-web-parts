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
import java.io.StringReader;
import java.lang.reflect.Field;
import javawebparts.core.org.apache.commons.digester.Digester;
import javawebparts.request.RequestHelpers;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.xml.sax.SAXException;


/**
 * This servlet is used by a test of the AjaxParts Taglib.  It tests the
 * std:SendByID request handler.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class SendByIDTestServlet extends HttpServlet {


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

    // Get the body content.  This will be a string of XML from the AJAX
    // request.
    String bodyContent = RequestHelpers.getBodyContent(request);

    // Now parse the XML.  We'll use Digester to make life easier.  This
    // probably isn't the most efficient method to parse XML, but for the
    // purposes of this sample app it'll do just fine.
    Digester digester = new Digester();
    digester.setValidating(false);
    digester.addObjectCreate("person",
      "javawebparts.sampleapp.SendByIDTestServlet$Person");
    digester.addBeanPropertySetter("person/firstName");
    digester.addBeanPropertySetter("person/lastName");
    Person p = new Person();
    try {
      p = (Person)digester.parse(new StringReader(bodyContent));
    } catch (SAXException se) {
      se.printStackTrace();
      System.out.println("Problem parsing XML to create Person object: " + se);
    }

    // So, at this point we have a Person object p, with the first name and
    // last name sent to what was recieved as XML.  Now we'll just return
    // a simple snippet of Javascript so the user sees a greeting alert.
    PrintWriter out = response.getWriter();
    out.println("<script>alert(\"Hello, " + p.getFirstName() + " " +
      p.getLastName() + "\");</script>");

  } // End doPost().


  /**
   * This is an inner class that will be populated when a request is recieved.
   */
  public static class Person {


    /**
     * firstName.
     */
    private String firstName;


    /**
     * lastName.
     */
    private String lastName;


    /**
     * Accessor for firstName field.
     *
     * @return String Current value of firstName Field.
     */
    public String getFirstName() {

      return firstName;

    } // End getFirstName().


    /**
     * Mutator for firstName field.
     *
     * @param inFirstName New value of firstName field.
     */
    public void setFirstName(String inFirstName) {

      firstName = inFirstName;

    } // End setFirstName().


    /**
     * Accessor for lastName field.
     *
     * @return String Current value of lastName Field.
     */
    public String getLastName() {

      return lastName;

    } // End getLastName().


    /**
     * Mutator for lastName field.
     *
     * @param inLastName New value of lastName field.
     */
    public void setLastName(String inLastName) {

      lastName = inLastName;

    } // End setLastName().


    /**
     * Overriden toString method.
     *
     * @return A reflexively-built string representation of this bean.
     */
    public String toString() {

      String str = null;
      StringBuffer sb = new StringBuffer(1000);
      sb.append("[" + super.toString() + "]={");
      boolean firstPropertyDisplayed = false;
      try {
        Field[] fields = this.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
          if (firstPropertyDisplayed) {
            sb.append(", ");
          } else {
            firstPropertyDisplayed = true;
          }
          sb.append(fields[i].getName() + "=" + fields[i].get(this));
        }
        sb.append("}");
        str = sb.toString().trim();
      } catch (IllegalAccessException iae) {
        iae.printStackTrace();
      }
      return str;

    } // End toString().


  } // End Person inner class.


} // End class.
