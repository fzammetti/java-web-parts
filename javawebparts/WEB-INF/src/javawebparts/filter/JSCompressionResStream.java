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


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;


/**
 * This class is used to do compression.
 *
 * @author <a href="mailto:herros@gmail.com">Herman van Rosmalen</a>.
 */
public class JSCompressionResStream extends ServletOutputStream {


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
      System.err.println("JSCompressionResStream" +
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
   * Constructor.
   */
  public JSCompressionResStream() {

    super();
    ba   = new ByteArrayOutputStream();
  } // End JSCompressionResStream().


  /**
   * Writes a single byte to the output stream.
   *
   * @param  b           A single byte to write.
   * @throws IOException IOException.
   */
  public void write(int b) throws IOException {

    ba.write((byte)b);

  } // End write().


  /**
   * Clean up.
   *
   * @throws IOException IOException.
   */
  public void close() throws IOException {

    byte[] bytes = ba.toByteArray();
//do something
  } // End close().

  /**
   * return the collected output
   * 
   * @return byte[] the collected output 
   */
  public byte[] getData() {
    return ba.toByteArray();
  }
  
} // End JSCompressionResStream class.
