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


package javawebparts.taglib.basicstr;


import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Random;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class is a custom tag that trims a string.  This function can work
 * in a number of ways, defined by setting the following attributes:
 * <br><br>
 * type - This is the type of trim to perform.  This can be one of the following
 * values: left, right or both.  'left' trims characters from the start of a
 * string, 'right' trims them from the end and 'both' trims from the start
 * as well as the end.  Required: Yes.
 * <br>
 * chars - If specified, the characters listed will be trimmed, and only
 * them.  Note that these characters get inserted into a regular expressino,
 * so if you need to trim characters that have special meaning to a regex, you
 * will have to manually escape them properly.  Required: No (when this
 * attribute is not present, the default function of trimming only whitespace
 * will be used).
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class ScrambleTag extends BodyTagSupport {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javax.servlet.jsp.JspException");
      Class.forName("javax.servlet.jsp.JspWriter");
      Class.forName("javax.servlet.jsp.PageContext");
      Class.forName("javax.servlet.jsp.tagext.BodyTagSupport");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("ScrambleTag" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(ScrambleTag.class);


  /**
   * This is the body content text to be altered.
   */
  private String text = "";


  /**
   * Alter the body content.
   *
   * @return              Return code.
   * @throws JspException If anything goes wrong.
   */
  public int doAfterBody() throws JspException {

    String bcs = bodyContent.getString();
    if (bcs == null) {
      bcs = "";
    }
    byte[] b = bcs.getBytes();
    Random r = new Random(new GregorianCalendar().getTimeInMillis());
    for (int i = 0; i < b.length - 1; i++) {
      int k = r.nextInt(b.length - i);
      int temp = b[i + k];
      b[i + k] = b[i];
      b[i] = (byte)temp;
    }
    text = new String(b);
    return SKIP_BODY;

  } // End doAfterBody().


  /**
   * Render the altered body.
   *
   * @return              Return code.
   * @throws JspException If anything goes wrong.
   */
  public int doEndTag() throws JspException {

    try {
      JspWriter out = pageContext.getOut();
      out.print(text);
    } catch (IOException ioe) {
      throw new JspException(ioe.toString());
    }
    return EVAL_PAGE;

  } // doEndTag().


} // End class.
