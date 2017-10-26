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
 * This class is a custom tag that adds ellipses to a a string.
 * <br><br>
 * This tag uses the following attributes:
 * <br><br>
 * leftEdge - The left edge of the source string.  Use 0 to start at the
 * beginning.  Anything greater than 4 will result in ellipses added to the
 * BEGINNING of the string.
 * <br>
 * maxSize - The maximum size of the resultant string.  This must always be
 * at least 4!
 * <br><br>
 * Here are some usage examples taken directly from the Commons Lang docs:
 * <br><br>
 * When the text is "abcdefghijklmno" and...
 * <br><br>
 * leftEdge is -1 and maxSize is 10 = "abcdefg..."<br>
 * leftEdge is 0 and maxSize is 10 = "abcdefg..."<br>
 * leftEdge is 1 and maxSize is 10 = "abcdefg..."<br>
 * leftEdge is 4 and maxSize is 10 = "abcdefg..."<br>
 * leftEdge is 5 and maxSize is 10 = "...fghi..."<br>
 * leftEdge is 6 and maxSize is 10 = "...ghij..."<br>
 * leftEdge is 8 and maxSize is 10 = "...ijklmno"<br>
 * leftEdge is 10 and maxSize is 10 = "...ijklmno"<br>
 * leftEdge is 12 and maxSize is 10 = "...ijklmno"<br>
 * leftEdge is 0 and mazSize is 3 = ""<br>
 * leftEdge is 5 and mazSize is 6 = ""<br>
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class AbbreviateTag extends BodyTagSupport {


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
      System.err.println("AbbreviateTag" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(AbbreviateTag.class);


  /**
   * This is the body content text to be altered.
   */
  private String text = "";


  /**
   * This is the attribute that defines the left edge of the string to use.
   */
  private String leftEdge;


  /**
   * This is the attribute that defines the maximum size of the resultant
   * string.
   */
  private String maxSize;


  /**
   * leftEdge mutator.
   *
   * @param inLeftEdge leftEdge.
   */
  public void setLeftEdge(String inLeftEdge) {

    leftEdge = inLeftEdge;

  } // End setLeftEdge().


  /**
   * maxSize mutator.
   *
   * @param inMaxSize maxSize.
   */
  public void setMaxSize(String inMaxSize) {

    maxSize = inMaxSize;

  } // End setMaxSize().


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
    // Get attribute as ints, set some defaults if they can't be parsed.
    int le = 0;
    try {
      le = Integer.parseInt(leftEdge);
    } catch (NumberFormatException nfe){
      le = 0;
    }
    // Get attribute as ints, set some defaults if they can't be parsed.
    int ms = 0;
    try {
      ms = Integer.parseInt(maxSize);
    } catch (NumberFormatException nfe) {
      ms = 4;
    }
    // Do abbreviation.
    try {
      text = StringUtils.abbreviate(bcs, le, ms);
    } catch (IllegalArgumentException iae) {
      text = "";
    }
    return SKIP_BODY;

  } // End doAfterBody()


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
