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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;


/**
 * This class is used to do compression.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class CompressionResStream extends ServletOutputStream {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javax.servlet.http.HttpServletResponse");
      Class.forName("javax.servlet.ServletOutputStream");
    } catch (ClassNotFoundException e) {
      System.err.println("CompressionResStream" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * ByteArrayOutputStream ba.
   */
  private ByteArrayOutputStream ba;


  /**
   * Our output stream.  Will either be a GZipOutputStream (subclass of
   * DeflaterOutputStream) or an actual DeflaterOutputStream, depending on
   * what type of compression it is configured to do.
   */
  private DeflaterOutputStream ops;


  /**
   * Reference to the response being serviced.
   */
  private HttpServletResponse resp;


  /**
   * ServletOutputStream out.
   */
  private ServletOutputStream out;


  /**
   * What type of compression we are doing.
   */
  private String compressionType;


  /**
   * Constructor.
   *
   * @param  inResponse  The HTTPServletResponse being services.
   * @param  ct          What type of compression to perform.
   * @throws IOException IOException.
   */
  public CompressionResStream(HttpServletResponse inResponse, String ct)
                              throws IOException {

    super();
    resp = inResponse;
    out  = resp.getOutputStream();
    ba   = new ByteArrayOutputStream();
    compressionType = ct;
    if (ct.equalsIgnoreCase(CompressionResWrapper.GZIP)) {
      ops = new GZIPOutputStream(ba);
    }
    if (ct.equalsIgnoreCase(CompressionResWrapper.DEFLATE)) {
      ops = new DeflaterOutputStream(ba);
    }

  } // End CompressionResStream().


  /**
   * Writes a single byte to the output stream.
   *
   * @param  b           A single byte to write.
   * @throws IOException IOException.
   */
  public void write(int b) throws IOException {

    ops.write((byte)b);

  } // End write().


  /**
   * Clean up.
   *
   * @throws IOException IOException.
   */
  public void close() throws IOException {

    ops.finish();
    byte[] bytes = ba.toByteArray();
    resp.addHeader("Content-Length",   Integer.toString(bytes.length));
    resp.addHeader("Content-Encoding", compressionType);
    out.write(bytes);
    out.flush();
    out.close();

  } // End close().


  /**
   * Writes a byte array to the output stream.
   *
   * @param  bytes       Byte array.
   * @throws IOException IOException.
   */
  public void write(byte[] bytes) throws IOException {

    write(bytes, 0, bytes.length);

  } // End write().


  /**
   * Writes a byte array to the output stream.
   *
   * @param  bytes       Byte array.
   * @param  o           o.
   * @param  leng        Length.
   * @throws IOException IOEXception.
   */
  public void write(byte[] bytes, int o, int leng) throws IOException {

    ops.write(bytes, o, leng);

  }


  /**
   * Flush the output stream.
   * @throws IOException IOException.
   */
  public void flush() throws IOException {

    ops.flush();

  } // End flush().


} // End CompressionResStream class.
