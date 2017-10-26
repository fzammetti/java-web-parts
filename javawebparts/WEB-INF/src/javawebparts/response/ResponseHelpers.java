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


package javawebparts.response;


import javawebparts.core.JWPHelpers;


/**
 * This class contains static methods that are too small to warrant their
 * own class.  General response-related utility methods.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public final class ResponseHelpers {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javawebparts.core.JWPHelpers");
    } catch (ClassNotFoundException e) {
      System.err.println("ResponseHelpers" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * This is a utility class, so we want a private noarg constructor so
   * instances cannot be created.
   */
  private ResponseHelpers() {
  } // End ResponseHelpers().


  /**
   * This method replaces any character in a given string that has a
   * corresponding HTML entity with that entity.  You can also pass it a
   * string containing characters you DO NOT want entities inserted for.
   * In actuality, the method that does the actual work is in JWPHelpers,
   * but is called from here.  The code in JWPHelpers was originally here
   * only, but it was moved into JWPHelpers to eliminate a cross-package
   * dependency between the taglib package (because of basicstr taglib)
   * and the response package.
   *
   * @param str        The string to insert entities into.
   * @param exclusions Characters you DO NOT wanted entities inserted for.
   *                   Pass null for no exclusions.
   * @return           A new string with HTML entities replacing all
   *                   charactercs, except those excluded, that have
   *                   corresponding entities.
   */
  public static String encodeEntities(String str, String exclusions) {

    return JWPHelpers.encodeEntities(str, exclusions);

  } // End encodeEntities().


} // End class.
