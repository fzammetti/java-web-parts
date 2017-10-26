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


package javawebparts.servlet;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javawebparts.core.org.apache.commons.lang.StringUtils;
import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This conrete implementation of the ResourceStream interface is used to
 * serve resources from a file system path, absolute or relative.  Note that
 * this implementation assumes the content type is set either using the value of
 * the defaultContentType init parameter of the ResourceServerServlet, or
 * via mapping file, so null is returned by getContentType().
 * <br><br>
 * streamParam1 and streamParam2 are not used by this implementation.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class ResourceStreamFromFileSystem implements ResourceStream {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javawebparts.core.org.apache.commons.lang.StringUtils");
      Class.forName("javax.servlet.ServletContext");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("ResourceStreamFromFileSystem" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log =
    LogFactory.getLog(ResourceStreamFromFileSystem.class);


  /**
   * getStream() is the method that returns the InputStream to the resource to
   * to be served.
   *
   * @param  inRequestedResource This is the resource that is being requested.
   *                             This should be a path specifier WITHOUT
   *                             leading slash.
   * @param  inParam1            First arbitrary parameter from web.xml.
   * @param  inParam2            Second arbitrary parameter from web.xml.
   * @param  inServletContext    The ServletContext of the calling
   *                             ResourceServerServlet.
   * @return                     An InputStream on the resource to be served.
   */
  public InputStream getStream(String inRequestedResource, String inParam1,
    String inParam2, ServletContext inServletContext) {

    String f =
      inServletContext.getRealPath(inRequestedResource) + File.separator;
    f = StringUtils.replace(f, "\\", "\\\\");
    InputStream is = null;
    try {
      is = new FileInputStream(f);
    } catch (FileNotFoundException fnfe) {
      is = null;
    }
    if (is == null) {
      log.error("Resource " + inRequestedResource + " not found");
    }
    return is;

  } // End getStream().


  /**
   * This implementation assumes the content type is set either using the value
   * of the defaultContentType init parameter of the ResourceServerServlet, or
   * via mapping file, so null is returned by getContentType().
   *
   * @param  inRequestedResource This is the resource that is being requested.
   *                             This should be a path specifier WITHOUT
   *                             leading slash.
   * @param  inParam1            First arbitrary parameter from web.xml.
   * @param  inParam2            Second arbitrary parameter from web.xml.
   * @param  inServletContext    The ServletContext of the calling
   *                             ResourceServerServlet.
   * @return                     The content type of the resource, null in
   *                             this case.
   */
  public String getContentType(String inRequestedResource, String inParam1,
    String inParam2, ServletContext inServletContext) {

    return null;

  } // End getContentYype().


} // End class.
