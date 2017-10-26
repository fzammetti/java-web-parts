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
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class is a custom tag that lower-cases a string.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class LCaseTag extends BodyTagSupport {


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
      System.err.println("LCaseTag" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(LCaseTag.class);


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
    text = bcs.toLowerCase();
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
