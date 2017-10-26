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
 * This class is a custom tag that renders the JWPReplaceSubstr() Javascript
 * function which can be called to replace all occurances of a given
 * character or string from another.
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
 * function JWPReplaceSubstr(jwprs_inSrc, jwprs_inOld, jwprs_inNew) {<br>
 *   if (jwprs_inSrc == null || jwprs_inSrc == "" ||<br>
 *     jwprs_inOld == null || jwprs_inOld == "" ||<br>
 *     jwprs_inNew == null || jwprs_inNew == "") {<br>
 *     return "";<br>
 *   }<br>
 *   while (jwprs_inSrc.indexOf(jwprs_inOld) > -1) {<br>
 *     jwprs_inSrc = jwprs_inSrc.replace(jwprs_inOld, jwprs_inNew);<br>
 *   }<br>
 *   return jwprs_src;<br>
 * }<br>
 * <br>
 * Usage example:
 * <br><br>
 * <b>myString = JWPReplaceSubstr("This is a test", " ", "-");</b><br>
 * This will replace all occurances of the space character in the string
 * "This is a test" with dashes (resulting in "This-is-a-test");
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class ReplaceSubstrTag extends TagSupport {


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
      System.err.println("ReplaceSubstrTag" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(ReplaceSubstrTag.class);


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

    log.info("ReplaceSubstrTag.doStartTag()...");

    try {

      JspWriter    out    = pageContext.getOut();
      StringBuffer jsCode = new StringBuffer(2048);

      // Render opening <script> tag, if applicable.
      if (renderScriptTags == null ||
        renderScriptTags.equalsIgnoreCase("true")) {
        jsCode.append("<script>\n");
      }

      // Render function code.
      jsCode.append("function JWPReplaceSubstr(jwprs_inSrc, jwprs_inOld, " +
        "jwprs_inNew) {\n");
      jsCode.append("  if (jwprs_inSrc == null || jwprs_inSrc == \"\" ||\n");
      jsCode.append("    jwprs_inOld == null || jwprs_inOld == \"\" ||\n");
      jsCode.append("    jwprs_inNew == null || jwprs_inNew == \"\") {\n");
      jsCode.append("    return \"\";\n");
      jsCode.append("  }\n");
      jsCode.append("  while (jwprs_inSrc.indexOf(jwprs_inOld) > -1) {\n");
      jsCode.append("    jwprs_inSrc = jwprs_inSrc.replace(jwprs_inOld, " +
        "jwprs_inNew);\n");
      jsCode.append("  }\n");
      jsCode.append("  return jwprs_inSrc;\n");
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
