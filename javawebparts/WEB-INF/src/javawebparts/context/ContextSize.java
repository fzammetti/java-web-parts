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


package javawebparts.context;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javawebparts.core.JWPHelpers;
import javax.servlet.ServletContext;


/**
 * This class provides a way to get the size of a servlet context object.
 * This was suggested by Wendy Smoak.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class ContextSize {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javawebparts.core.JWPHelpers");
      Class.forName("javax.servlet.ServletContext");
    } catch (ClassNotFoundException e) {
      System.err.println("ContextSize" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Reference to servlet context this object is associated with.
   */
  private ServletContext context;


  /**
   * Collection of reasons why context size could not be determined.
   */
  private ArrayList failureReasons = new ArrayList();


  /**
   * This flag determines if the context size will continue to be calculated
   * even when a non-serializable objects are found.  This can be useful when
   * you know your context won't pass this tests here but want a size, and
   * understand it will always be an understated size, but don't care.
   * This feature was suggested by Wendy Smoak.
   */
  private boolean ignoreNonSerializable;



  /**
   * constructor.
   *
   * @param inContext object this object will be associated with.
   */
  public ContextSize(ServletContext inContext) {

    context = inContext;

  } // End ContextSize().


  /**
   * Mutator for the ignoreNonSerializable field.
   *
   * @param inSetting True to calculate context size regardless of whether
   *                  non-serializable objects are found in it, false
   *                  otherwise (false is the default).
   */
  public void setIgnoreNonSerializable(boolean inSetting) {

    ignoreNonSerializable = inSetting;

  } // End setIgnoreNonSerializable().


  /**
   * This method is used to get the total size of a current, valid
   * ServletContext object it is passed.
   *
   * @return The total size of context in bytes, or -1 if the size could not
   *         be determined.
   */
  public int getContextSize() {

    Enumeration en   = context.getAttributeNames();
    String      name = null;
    Object      obj  = null;
    String      serialOut;
    String      sizeDelimiter = "size=";
    int         sizeIndex;
    int         objSize;
    int         totalSize = 0;
    while (en.hasMoreElements()) {
      name      = (String)en.nextElement();
      obj       = context.getAttribute(name);
      serialOut = JWPHelpers.serializiableTest(obj);
      if ((sizeIndex = serialOut.lastIndexOf(sizeDelimiter)) > 0) {
        objSize = Integer.parseInt(serialOut.substring(sizeIndex +
                  sizeDelimiter.length(), serialOut.lastIndexOf(')')));
        totalSize += objSize;
      } else {
        failureReasons.add(serialOut);
      }
    }
    if (!failureReasons.isEmpty() && !ignoreNonSerializable) {
      return -1;
    }
    return totalSize;

  } // End getContextSize().


  /**
   * When getContextSize() returns -1, this method can be called to retrieve
   * the collection of reasons it failed.
   *
   * @return Collection of failures why the context size could not be
   *         determiend.
   */
  public List whyFailed() {

    return failureReasons;

  } // End whyFailed().


} // End class.
