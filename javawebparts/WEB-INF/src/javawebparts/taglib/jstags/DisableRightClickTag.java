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
 * This class is a custom tag that renders the JWPDisableRightClick()
 * Javascript function which can be called to disable (or enable if previously
 * disabled) the ability for a user to bring up a right-click context menu.
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
 * function JWPDisableRightClick(jwpdrc_inDisable) {<br>
 *   isIE = document.all;<br>
 *   isNN =! document.all && document.getElementById;<br>
 *   isN4 = document.layers;<br>
 *   if (jwpdrc_inDisable) {<br>
 *     if (isIE || isNN) {<br>
 *       document.oncontextmenu = JWPDisableRightClickHandler;<br>
 *     } else {<br>
 *       document.captureEvents(Event.MOUSEDOWN || Event.MOUSEUP);<br>
 *       document.onmousedown = JWPDisableRightClickHandler;<br>
 *     }<br>
 *   } else {<br>
 *     if (isIE || isNN) {<br>
 *       document.oncontextmenu = null;<br>
 *     } else {<br>
 *       document.onmousedown = null;<br>
 *     }<br>
 *   }<br>
 * }<br>
 * function JWPDisableRightClickHandler(jwpdrc_e) {<br>
 *   isN4 = document.layers;<br>
 *   if (isN4) {<br>
 *     if (jwpdrc_e.which==2 || jwpdrc_e.which==3) {<br>
 *       return false;<br>
 *     }<br>
 *   } else {<br>
 *     return false;<br>
 *   }<br>
 * }<br>
 * <br>
 * Usage example:
 * <br><br>
 * <b>JWPDisableRightClick(true);<b><br>
 * This will disable the right-click context menu.
 * <br><br>
 * <b>JWPDisableRightClick(false);<b><br>
 * This will enable the right-click context menu.  Note that passing nothing
 * will act the same as passing false.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class DisableRightClickTag extends TagSupport {


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
  private static Log log = LogFactory.getLog(DisableRightClickTag.class);


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
      jsCode.append("function JWPDisableRightClick(jwpdrc_inDisable) {\n");
      jsCode.append("  isIE = document.all;\n");
      jsCode.append("  isNN =! document.all && document.getElementById;\n");
      jsCode.append("  isN4 = document.layers;\n");
      jsCode.append("  if (jwpdrc_inDisable) {\n");
      jsCode.append("    if (isIE || isNN) {\n");
      jsCode.append("      document.oncontextmenu = " +
        "JWPDisableRightClickHandler;\n");
      jsCode.append("    } else {\n");
      jsCode.append("      document.captureEvents(Event.MOUSEDOWN || " +
        "Event.MOUSEUP);\n");
      jsCode.append("      document.onmousedown = " +
        "JWPDisableRightClickHandler;\n");
      jsCode.append("    }\n");
      jsCode.append("  } else {\n");
      jsCode.append("    if (isIE || isNN) {\n");
      jsCode.append("      document.oncontextmenu = null;\n");
      jsCode.append("    } else {\n");
      jsCode.append("      document.onmousedown = null;\n");
      jsCode.append("    }\n");
      jsCode.append("  }\n");
      jsCode.append("}\n");
      jsCode.append("function JWPDisableRightClickHandler(jwpdrc_e) {\n");
      jsCode.append("  isN4 = document.layers;\n");
      jsCode.append("  if (isN4) {\n");
      jsCode.append("    if (jwpdrc_e.which==2 || jwpdrc_e.which==3) {\n");
      jsCode.append("      return false;\n");
      jsCode.append("    }\n");
      jsCode.append("  } else {\n");
      jsCode.append("    return false;\n");
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
