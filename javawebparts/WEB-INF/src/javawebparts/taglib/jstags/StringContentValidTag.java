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
 * This class is a custom tag that renders the JWPStringContentValid()
 * Javascript function which can be called to determine if a string contains
 * only valid characters, or if a string does no contain any of a list of
 * valid characters.
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
 * JWPSCV_FROMLIST = 1;
 * JWPSCV_NOTFROMLIST = 2;
 * function JWPStringContentValid(jwpscv_inString, jwpscv_charList,
 *   jwpscv_FromExcept) {
 *   if (jwpscv_inString == null) {
 *     alert("Input string was null");
 *     return false;
 *   }
 *   if (jwpscv_charList == null) {
 *     alert("Character list was null");
 *     return false;
 *   }
 *   if (jwpscv_FromExcept == null) {
 *     alert("FROMLIST/NOTFROMLIST was null");
 *     return false;
 *   }
 *   if (jwpscv_FromExcept == JWPSCV_FROMLIST) {
 *     for (i = 0; i < jwpscv_inString.length; i++) {
 *       if (jwpscv_charList.indexOf(jwpscv_inString.charAt(i)) == -1) {
 *         return false;
 *       }
 *     }<br>
 *     return true;<br>
 *   }<br>
 *   if (jwpscv_FromExcept == JWPSCV_NOTFROMLIST) {<br>
 *     for (i = 0; i < jwpscv_inString.length; i++) {<br>
 *       if (jwpscv_charList.indexOf(jwpscv_inString.charAt(i)) != -1) {<br>
 *         return false;<br>
 *       }<br>
 *     }<br>
 *     return true;<br>
 *   }<br>
 * }<br>
 * <br>
 * Usage example:
 * <br><br>
 * <b>alert(JWPStringContentValid("This is a test", " thisae",
 * JWPSCV_FROMLIST));</b><br>
 * This will result in an alert box displaying "true" in it because all the
 * characters in the string "This is a test" are in the character list
 * " thisae", and the JWPSCV_FROMLIST flag was sent in.  If you replace one of
 * the characters with 'z' in the string being tested for instance, false would
 * be shown because 'z' is not in the list of allowed characters.
 * <br><br>
 * <b>alert(JWPStringContentValid("This is a test", "xyz",
 * JWPSCV_NOTFROMLIST));</b><br>
 * This will result in an alert box displaying "true" in it because none the
 * characters in the string "This is a test" are in the character list
 * " thisae", and the JWPSCV_NOTFROMLIST flag was sent in.  If you replace one
 * of the characters with 'z' in the string being tested for instance, false
 * would be shown because 'z' is in the list of characters not allowed.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class StringContentValidTag extends TagSupport {


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
      System.err.println("StringContentValidTag" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(StringContentValidTag.class);


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

    log.info("FullTrimTag.doStartTag()...");

    try {

      JspWriter    out    = pageContext.getOut();
      StringBuffer jsCode = new StringBuffer(2048);

      // Render opening <script> tag, if applicable.
      if (renderScriptTags == null ||
        renderScriptTags.equalsIgnoreCase("true")) {
        jsCode.append("<script>\n");
      }

      // Render function code.
      jsCode.append("JWPSCV_FROMLIST = 1;\n");
      jsCode.append("JWPSCV_NOTFROMLIST = 2;\n");
      jsCode.append("function JWPStringContentValid(jwpscv_inString, " +
        "jwpscv_charList,\n");
      jsCode.append("  jwpscv_FromExcept) {\n");
      jsCode.append("  if (jwpscv_inString == null) {\n");
      jsCode.append("    alert(\"Input string was null\");\n");
      jsCode.append("    return false;\n");
      jsCode.append("  }\n");
      jsCode.append("  if (jwpscv_charList == null) {\n");
      jsCode.append("    alert(\"Character list was null\");\n");
      jsCode.append("    return false;\n");
      jsCode.append("  }\n");
      jsCode.append("  if (jwpscv_FromExcept == null) {\n");
      jsCode.append("    alert(\"FROMLIST/NOTFROMLIST was null\");\n");
      jsCode.append("    return false;\n");
      jsCode.append("  }\n");
      jsCode.append("  if (jwpscv_FromExcept == JWPSCV_FROMLIST) {\n");
      jsCode.append("    for (i = 0; i < jwpscv_inString.length; i++) {\n");
      jsCode.append("      if (jwpscv_charList.indexOf(" +
        "jwpscv_inString.charAt(i)) == -1) {\n");
      jsCode.append("        return false;\n");
      jsCode.append("      }\n");
      jsCode.append("    }\n");
      jsCode.append("    return true;\n");
      jsCode.append("  }\n");
      jsCode.append("  if (jwpscv_FromExcept == JWPSCV_NOTFROMLIST) {\n");
      jsCode.append("    for (i = 0; i < jwpscv_inString.length; i++) {\n");
      jsCode.append("      if (jwpscv_charList.indexOf(" +
        "jwpscv_inString.charAt(i)) != -1) {\n");
      jsCode.append("        return false;\n");
      jsCode.append("      }\n");
      jsCode.append("    }\n");
      jsCode.append("    return true;\n");
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
