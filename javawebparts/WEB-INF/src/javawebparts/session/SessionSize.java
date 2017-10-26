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


package javawebparts.session;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javawebparts.core.JWPHelpers;
import javax.servlet.http.HttpSession;


/**
 * This class provides a way to get the size of a session object.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class SessionSize {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javawebparts.core.JWPHelpers");
      Class.forName("javax.servlet.http.HttpSession");
    } catch (ClassNotFoundException e) {
      System.err.println("SessionSize" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Reference to session this object is associated with.
   */
  private HttpSession session;


  /**
   * Collection of reasons why session size could not be determined.
   */
  private ArrayList failureReasons = new ArrayList();


  /**
   * This flag determines if the session size will continue to be calculated
   * even when a non-serializable objects are found.  This can be useful when
   * you know your session won't pass this tests here but want a size, and
   * understand it will always be an understated size, but don't care.
   * This feature was suggested by Wendy Smoak.
   */
  private boolean ignoreNonSerializable;



  /**
   * constructor.
   *
   * @param inSession object this object will be associated with.
   */
  public SessionSize(HttpSession inSession) {

    session = inSession;

  } // End SessionSize().


  /**
   * Mutator for the ignoreNonSerializable field.
   *
   * @param inSetting True to calculate session size regardless of whether
   *                  non-serializable objects are found in it, false
   *                  otherwise (false is the default).
   */
  public void setIgnoreNonSerializable(boolean inSetting) {

    ignoreNonSerializable = inSetting;

  } // End setIgnoreNonSerializable().


  /**
   * This method is used to get the total size of a current, valid HttpSession
   * object it is passed.
   *
   * @return The total size of session in bytes, or -1 if the size could not
   *         be determined.
   */
  public int getSessionSize() {

    Enumeration en   = session.getAttributeNames();
    String      name = null;
    Object      obj  = null;
    String      serialOut;
    String      sizeDelimiter = "size=";
    int         sizeIndex;
    int         objSize;
    int         totalSize = 0;
    while (en.hasMoreElements()) {
      name      = (String)en.nextElement();
      obj       = session.getAttribute(name);
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

  } // End getSessionSize().


  /**
   * When getSessionSize() returns -1, this method can be called to retrieve
   * the collection of reasons it failed.
   *
   * @return Collection of failures why the session size could not be
   *         determiend.
   */
  public List whyFailed() {

    return failureReasons;

  } // End whyFailed()


} // End class.
