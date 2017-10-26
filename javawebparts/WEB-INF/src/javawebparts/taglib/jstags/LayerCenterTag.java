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
 * This class is a custom tag that renders the JWPLayerCenter() Javascript
 * function which takes in a reference to a &lt;div&gt; or &lt;layer&gt; tag
 * and centers it, taking into account how the page is scrolled.
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
 * function JWPLayerCenter(jwplc_inLayer) {<br>
 *   if (jwplc_inLayer == null)<br>
 *     return;<br>
 *   }<br>
 *   jwplc_inLayer.style.position = "absolute";<br>
 *   lca = document.body.clientWidth;<br>
 *   lcb = jwplc_inLayer.offsetWidth;<br>
 *   lcx = (Math.round(lca / 2)) - (Math.round(lcb / 2));<br>
 *   lca = document.body.clientHeight;<br>
 *   lcb = jwplc_inLayer.offsetHeight;<br>
 *   lcy = (Math.round(lca / 2)) - (Math.round(lcb / 2));<br>
 *   jwplc_inLayer.style.left = lcx + document.body.scrollLeft;<br>
 *   jwplc_inLayer.style.top = lcy + document.body.scrollTop;<br>
 * }<br>
 * <br>
 * Usage example:
 * <br><br>
 * <b>JWPLayerCenter(document.getElementById("myLayer"));</b><br>
 * This results in the layer (either &lt;div&gt; or &lt;span&gt; being
 * centered in the browser window).
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class LayerCenterTag extends TagSupport {


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
      System.err.println("LayerCenterTag" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(LayerCenterTag.class);


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

    log.info("LayerCenterTag.doStartTag()...");

    try {

      JspWriter    out    = pageContext.getOut();
      StringBuffer jsCode = new StringBuffer(2048);

      // Render opening <script> tag, if applicable.
      if (renderScriptTags == null ||
        renderScriptTags.equalsIgnoreCase("true")) {
        jsCode.append("<script>\n");
      }

      // Render function code.
      jsCode.append("function JWPLayerCenter(jwplc_inLayer) {\n");
      jsCode.append("  if (jwplc_inLayer == null)<br>\n");
      jsCode.append("    return;<br>\n");
      jsCode.append("  }<br>\n");
      jsCode.append("  jwplc_inLayer.style.position = \"absolute\";\n");
      jsCode.append("  lca = document.body.clientWidth;\n");
      jsCode.append("  lcb = jwplc_inLayer.offsetWidth;\n");
      jsCode.append("  lcx = (Math.round(lca / 2)) - (Math.round(lcb / 2));\n");
      jsCode.append("  lca = document.body.clientHeight;\n");
      jsCode.append("  lcb = jwplc_inLayer.offsetHeight;\n");
      jsCode.append("  lcy = (Math.round(lca / 2)) - (Math.round(lcb / 2));\n");
      jsCode.append("  jwplc_inLayer.style.left = lcx + " +
        "document.body.scrollLeft;\n");
      jsCode.append("  jwplc_inLayer.style.top = lcy + " +
        "document.body.scrollTop;\n");
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
