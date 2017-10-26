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


import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


/**
 * This class is a wrapper capable of doing various operations on
 * the request parameters.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class ParameterMungerResWrapper extends HttpServletRequestWrapper {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javax.servlet.http.HttpServletRequest");
      Class.forName("javax.servlet.http.HttpServletRequestWrapper");
    } catch (ClassNotFoundException e) {
      System.err.println("ParameterMungerResWrapper" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * The list of functions to be performed.
   */
  private ArrayList functionList;


  /**
   * Constructor.
   *
   * @param inRequest      HttpServletRequest being serviced.
   * @param inFunctionList ArrayList of functions to perform on parameters.
   */
  public ParameterMungerResWrapper(HttpServletRequest inRequest,
    ArrayList inFunctionList) {

    super(inRequest);
    functionList = inFunctionList;

  } // End constructor.


  /**
   * Called to retrieve a parameter that have been "munged".
   *
   * @param  parameter A String containing the name of the parameter
   *                   whose value is requested.
   * @return           A String objects containing the munger value.
   */
  public String getParameter(String parameter) {

    // Get the values for the request parameter.  If it's not found,
    // return null.
    String val = super.getParameter(parameter);
    if (val == null) {
      return null;
    }
    // Iterate over the list of functions to perform.
    for (Iterator it = functionList.iterator(); it.hasNext();) {
      // Perform whatever the next function is.
      String function = (String)it.next();
      if (function.equalsIgnoreCase("trim")) {
        val = val.trim();
      }
      if (function.equalsIgnoreCase("lcase")) {
        val = val.toLowerCase();
      }
      if (function.equalsIgnoreCase("ucase")) {
        val = val.toUpperCase();
      }
      if (function.equalsIgnoreCase("reverse")) {
        StringBuffer sb = new StringBuffer(val);
        val = sb.reverse().toString();
      }
    }
    return val;

  } // End getParameter().


  /**
   * Called to retrieve parameters that have been "munged".
   *
   * @param  parameter A String containing the name of the parameter
   *                   whose value is requested.
   * @return           An array of String objects containing the
   *                   parameter's values.
   */
  public String[] getParameterValues(String parameter) {

    // Get the values for the request parameter.  If it's not found,
    // return null;
    String[] vals = super.getParameterValues(parameter);
    if (vals == null) {
      return null;
    }
    // Iterate over the list of functions to perform.
    int c = vals.length;
    for (Iterator it = functionList.iterator(); it.hasNext();) {
      // Perform whatever the next function is.
      String function = (String)it.next();
      if (function.equalsIgnoreCase("trim")) {
        for (int i = 0; i < c; i++) {
          vals[i] = vals[i].trim();
        }
      }
      if (function.equalsIgnoreCase("lcase")) {
        for (int i = 0; i < c; i++) {
          vals[i] = vals[i].toLowerCase();
        }
      }
      if (function.equalsIgnoreCase("ucase")) {
        for (int i = 0; i < c; i++) {
          vals[i] = vals[i].toUpperCase();
        }
      }
      if (function.equalsIgnoreCase("reverse")) {
        for (int i = 0; i < c; i++) {
          StringBuffer sb = new StringBuffer(vals[i]);
          vals[i] = sb.reverse().toString();
        }
      }
    }
    return vals;

  } // End getParameterValues().


} // End MungerResponseWrapper class.
