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


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.ServletOutputStream;


/**
 * This class is used to do compression.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class CompressionResWrapper extends HttpServletResponseWrapper {


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
      System.err.println("CompressionResWrapper" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * String identifier for GZip compression.
   */
  public static final String GZIP = "gzip";


  /**
   * String identifier for Deflate compression.
   */
  public static final String DEFLATE = "deflate";


  /**
   * The servlet response being services.
   */
  private HttpServletResponse resp;


  /**
   * The servlet output stream we will write to.
   */
  private ServletOutputStream strm;


  /**
   * PrintWriter used for writing.
   */
  private PrintWriter wrtr;


  /**
   * What type of compression this wrapper will do.
   */
  private String compressionType;


  /**
   * Constructor.
   *
   * @param inResp HttpServletResponse being serviced.
   * @param ct     Compression type to do.
   */
  public CompressionResWrapper(HttpServletResponse inResp, String ct) {

    super(inResp);
    resp = inResp;
    compressionType = ct;

  } // End CompressionResWrapper().


  /**
   * Returns the type of compression this wrapper is doing.
   *
   * @return The current compression type for this object.
   */
  public String getCompressionType() {

    return compressionType;

  } // End getCompressionType().


  /**
   * Creates a suitable custom ServletOutputStream.
   *
   * @return             A ServletOutputStream supporting the correct
   *                     compression type.
   * @throws IOException IOException.
   */
  private ServletOutputStream createOutputStream() throws IOException {

    if (strm == null) {
      return new CompressionResStream(resp, compressionType);
    } else {
      return strm;
    }

  } // End ServletOutputStream().


  /**
   * Gets a PrintWriter off the output stream.
   *
   * @return             PrintWriter on the OutputStream created.
   * @throws IOException IOException.
   */
  public PrintWriter getWriter() throws IOException {

    strm = createOutputStream();
    if (wrtr == null) {
      wrtr = new PrintWriter(new OutputStreamWriter(strm, "UTF-8"));
    }
    return wrtr;

  } // End getWriter().


  /**
   * Clean up.
   *
   * @throws IOException IOException.
   */
  public void complete() throws IOException {

    if (wrtr != null) {
      wrtr.close();
    } else if (strm != null) {
      strm.close();
    }

  } // End complete().


  /**
   * Creates a suitable custom ServletOutputStream.
   *
   * @return             A ServletOutputStream.
   * @throws IOException IOException.
   */
  public ServletOutputStream getOutputStream() throws IOException {

    strm = createOutputStream();
    return strm;

  } // End ServletOutputStream().


  /**
   * Flush the stream.
   *
   * @throws IOException IOException.
   */
  public void flushBuffer() throws IOException {

    strm.flush();

  } // End flushBuffer().


} // End CompressionResWrapper class.
