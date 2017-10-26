/*
 * Copyright 2006 Herman van Rosmalen
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


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


/**
 * This class is used to do compression.
 *
 * @author <a href="mailto:herros@gmail.com">Herman van Rosmalen</a>.
 */
public class JSCompressionResWrapper extends HttpServletResponseWrapper {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javax.servlet.http.HttpServletResponse");
      Class.forName("javax.servlet.http.HttpServletResponseWrapper");
      Class.forName("javax.servlet.ServletOutputStream");
    } catch (ClassNotFoundException e) {
      System.err.println("JSCompressionResWrapper" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }

  /**
   * The servlet output stream we will write to.
   */
  private JSCompressionResStream strm;


  /**
   * PrintWriter used for writing.
   */
  private PrintWriter wrtr;


  /**
   * PrintWriter used for writing.
   */
  private String contentType;


  /**
   * Constructor.
   *
   * @param inResp HttpServletResponse being serviced.
   */
  public JSCompressionResWrapper(HttpServletResponse inResp) {

    super(inResp);
    strm = new JSCompressionResStream();
    wrtr = new PrintWriter(strm);

  } // End JSCompressionResWrapper().


  /**
   * Gets the PrintWriter off the output stream.
   *
   * @return             PrintWriter on the OutputStream created.
   * @throws IOException IOException.
   */
  public PrintWriter getWriter() throws IOException {

    return wrtr;

  } // End getWriter().


  /**
   * Returns a suitable custom ServletOutputStream.
   *
   * @return             A ServletOutputStream.
   * @throws IOException IOException.
   */
  public ServletOutputStream getOutputStream() throws IOException {

    return strm;

  } // End getOutputStream().


  /**
   * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
   */
  public void setContentType(String contentType) {
    this.contentType = contentType;
    super.setContentType(contentType);
  } // End setContentType().

  
  /**
   * Returns the contentType which has been set by the overridden setContentType
   * 
   * @return             The content type of the stream.
   */
  public String getContentType() {
    return this.contentType;
  } // End getContentType().

  
} // End JSCompressionResWrapper class.
