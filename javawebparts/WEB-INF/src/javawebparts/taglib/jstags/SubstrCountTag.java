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


package javawebparts.taglib.jstags;


import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class is a custom tag that renders the JWPSubstrCount() Javascript
 * function which takes in a string and counts the number of times another
 * string (or single character) appears in it.
 * <br><br>
 * This tag uses the following attributes:
 * <br><br>
 * renderScriptTags - true/false - When set to true, the Javascript will be
 * rendered inside a &lt;script&gt; &lt;/script&gt; tag pair.  When set to
 * false, this will not be done and it is expected that the
 * &lt;jstags:fullTrim/&gt; tag appears inside a
 * &lt;script&gt; &lt;/script&gt; tag pair.  If this attribute is not present,
 * the script tags WILL be rendered.
 * <br><br>
 * It renders the following Javascript:
 * <br><br>
 * function JWPSubstrCount(jwpcc_inStr, jwpcc_inSearchStr) {<br>
 *   if (jwpcc_inStr == null || jwpcc_inStr == "" ||<br>
 *     jwpcc_inSearchStr == null || jwpcc_inSearchStr == "") {<br>
 *     return 0;<br>
 *   }<br>
 *   splitChars = jwpcc_inStr.split(jwpcc_inSearchStr);<br>
 *   if (splitChars.length == 0) {<br>
 *     return 0;<br>
 *   } else {<br>
 *     return splitChars.length - 1;<br>
 *   }<br>
 * }<br>
 * <br>
 * Usage example:
 * <br><br>
 * <b>i = JWPSubstrCount("This is a test", "s");</b><br>
 * This will result in i being equal to 3 because there are three lower-case
 * S's in the input string.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class SubstrCountTag extends TagSupport {


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
      Class.forName("javax.servlet.jsp.tagext.TagSupport");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("SubstrCountTag" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(SubstrCountTag.class);


  /**
   * Whether to render the opening and closing script tags around the
   * emitted Javascript.
   */
  private String renderScriptTags;


  /**
   * renderScriptTags mutator.
   *
   * @param inRenderScriptTags renderScriptTags.
   */
  public void setRenderScriptTags(String inRenderScriptTags) {

    renderScriptTags = inRenderScriptTags;

  } // End setRenderScriptTags().


  /**
   * Render the results of the tag.
   *
   * @return              Return code.
   * @throws JspException If anything goes wrong
   */
  public int doStartTag() throws JspException {

    log.info("SubstrCountTag.doStartTag()...");

    try {

      JspWriter    out    = pageContext.getOut();
      StringBuffer jsCode = new StringBuffer(2048);

      // Render opening <script> tag, if applicable.
      if (renderScriptTags == null ||
        renderScriptTags.equalsIgnoreCase("true")) {
        jsCode.append("<script>\n");
      }

      // Render function code.
      jsCode.append("function JWPSubstrCount(jwpcc_inStr, jwpcc_inSearchStr) " +
        "{\n");
      jsCode.append("  if (jwpcc_inStr == null || jwpcc_inStr == \"\" ||\n");
      jsCode.append("    jwpcc_inSearchStr == null || jwpcc_inSearchStr " +
      "== \"\") {\n");
      jsCode.append("    return 0;\n");
      jsCode.append("  }\n");
      jsCode.append("  splitChars = jwpcc_inStr.split(jwpcc_inSearchStr);\n");
      jsCode.append("  if (splitChars.length == 0) {\n");
      jsCode.append("    return 0;\n");
      jsCode.append("  } else {\n");
      jsCode.append("    return splitChars.length - 1;\n");
      jsCode.append("  }\n");
      jsCode.append("}\n");

      // Render closing </script> tag, if applicable.
      if (renderScriptTags == null ||
        renderScriptTags.equalsIgnoreCase("true")) {
        jsCode.append("</script>\n");
      }

      out.print(jsCode);

    } catch (IOException ioe) {
      throw new JspException(ioe.toString());
    }
    return SKIP_BODY;

  } //  End doStartTag()


} // End class.
