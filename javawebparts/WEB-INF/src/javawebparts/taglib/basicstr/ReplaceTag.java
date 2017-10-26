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
import javawebparts.core.org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class is a custom tag that replaces substrings within a string.
 * <br><br>
 * This tag uses the following attributes:
 * <br><br>
 * match - This is the string to find within the text
 * <br>
 * replaceWidth - As the name implies, any occurances of the string defined
 * by the match parameter will be replaced in the text
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class ReplaceTag extends BodyTagSupport {


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
      Class.forName("javawebparts.core.org.apache.commons.lang.StringUtils");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("ReplaceTag" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(ReplaceTag.class);


  /**
   * This is the body content text to be altered.
   */
  private String text = "";


  /**
   * This is the attribute that defines the substring to search for.
   */
  private String match;


   /**
   * This is the attribute that defines what to replace substring with.
   */
  private String replaceWith;


  /**
   * match mutator.
   *
   * @param inMatch match string.
   */
  public void setMatch(String inMatch) {

    match = inMatch;

  } // End setMatch().


  /**
   * replaceWith mutator.
   *
   * @param inReplaceWith replaceWith string.
   */
  public void setReplaceWith(String inReplaceWith) {

    replaceWith = inReplaceWith;

  } // End setReplaceWith().


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
    text = StringUtils.replace(bcs, match, replaceWith);
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
