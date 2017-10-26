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


import java.io.InputStream;
import javax.servlet.ServletContext;


/**
 * The ResourceStream interface is implemented by classes used by the
 * ResourceServerServlet.  An implementing class' job is to return an
 * InputStream to a resource that the servlet will serve, and also optionally
 * report its content type.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public interface ResourceStream {


  /**
   * getStream() is the method that returns the InputStream to the resource to
   * to be served.
   *
   * @param  inRequestedResource This is the resource that is being requested.
   *                             This might be a URL, a file system path, or a
   *                             database identifier, whatever is appropriate
   *                             to the source of the resource.
   * @param  inParam1            First arbitrary parameter from web.xml.
   * @param  inParam2            Second arbitrary parameter from web.xml.
   * @param  inServletContext    The ServletContext of the calling
   *                             ResourceServerServlet.
   * @return                     An InputStream on the resource to be served.
   */
  public InputStream getStream(String inRequestedResource, String inParam1,
    String inParam2, ServletContext inServletContext);


  /**
   * getContentType() can be used to return the value that will be set as the
   * response' contentType header.  For instance, if you want to set up the
   * ResourceServerServlet to serve different types of resources from a
   * database, and you have a field in the database that contains whatthe
   * content type is, you can read that field here and return it.
   *
   * @param  inRequestedResource This is the resource that is being requested.
   *                             This might be a URL, a file system path, or a
   *                             database identifier, whatever is appropriate
   *                             to the source of the resource.
   * @param  inParam1            First arbitrary parameter from web.xml.
   * @param  inParam2            Second arbitrary parameter from web.xml.
   * @param  inServletContext    The ServletContext of the calling
   *                             ResourceServerServlet.
   * @return                     The content type of the resource.  YOU MUST
   *                             RETURN NULL IF THIS METHOD WILL NOT BE USED
   *                             TO SET THE CONTENT TYPE!
   */
  public String getContentType(String inRequestedResource, String inParam1,
    String inParam2, ServletContext inServletContext);


} // End interface.
