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
 * This class is a custom tag that renders the getCookie() and setCookie()
 * Javascript functions which can be called to get or set a cookie.
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
 * function setCookie(inName, inValue, inExpiry) {<br>
 *   document.cookie = inName + \"=\" + " +<br>
 *     escape(inValue) + <br>
 *     \"; expires=\" + inExpiry.toGMTString();<br>
 *   docCookies = document.cookie;<br>
 * }<br>
 * function getCookie(name) {<br>
 *   docCookies = document.cookie;<br>
 *   index = docCookies.indexOf(name + \"=\");<br>
 *   if (index == -1) {    <br>
 *     return null;<br>
 *   }<br>
 *   index = docCookies.indexOf(\"=\", index) + 1;<br>
 *   endstr = docCookies.indexOf(\";\", index);<br>
 *   if (endstr == -1) {<br>
 *     endstr = docCookies.length;<br>
 *   }<br>
 *   return unescape(docCookies.substring(index, " +<br>
 *     endstr));<br>
 * }<br>
 * <br>
 * Usage example:
 * <br><br>
 * <b>setCookie("myCookie", "Hello!", new Date("25 Dec 2006 06:00:00 EST"));<b>
 * <br>
 * This will set a cookie named "myCookie" with the value "Hello!" which
 * expires on December 25, 2006 at 6pm EST.
 * <br><br>
 * <b>getCookie("myCookie");<b><br>
 * This retrieves the value of the cookie named "myCookie".
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class CookieFunctionsTag extends TagSupport {


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
      System.err.println("DisableRightClickTag" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(CookieFunctionsTag.class);


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
      jsCode.append("function setCookie(inName, inValue, inExpiry) {\n");
      jsCode.append("  document.cookie = inName + \"=\" + " +
        "escape(inValue) + \n");
      jsCode.append("    \"; expires=\" + inExpiry.toGMTString();\n");
      jsCode.append("  docCookies = document.cookie;\n");
      jsCode.append("}\n");
      jsCode.append("function getCookie(name) {\n");
      jsCode.append("  docCookies = document.cookie;\n");
      jsCode.append("  index = docCookies.indexOf(name + \"=\");\n");
      jsCode.append("  if (index == -1) {\n");
      jsCode.append("    return null;\n");
      jsCode.append("  }\n");
      jsCode.append("  index = docCookies.indexOf(\"=\", index) + 1;\n");
      jsCode.append("  endstr = docCookies.indexOf(\";\", index);\n");
      jsCode.append("  if (endstr == -1) {\n");
      jsCode.append("    endstr = docCookies.length;\n");
      jsCode.append("  }\n");
      jsCode.append("  return unescape(docCookies.substring(index, " +
        "endstr));\n");
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
