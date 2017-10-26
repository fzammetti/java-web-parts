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
 * This class is a custom tag that renders the JWPGetParams() Javascript
 * function which can be called to retrieve the value of a named parameter,
 * or an array of all parameters, that were on the query string used to
 * access the page.  If there were no parameters, null will be returned in
 * either case.  Not that this function DOES NOT work on pages you manually
 * open from a browser (i.e., if you open a local HTML file with a query
 * string appended).
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
 * function JWPGetParams(jwpgp_inParamName) {<br>
 *   retVal = null;<br>
 *   varvals = unescape(location.search.substring(1));<br>
 *   if (varvals) {<br>
 *     search_array = varvals.split("&");<br>
 *     temp_array = new Array();<br>
 *     j = 0;<br>
 *     for (i = 0; i < search_array.length; i++) {<br>
 *       temp_array = search_array[i].split("=");<br>
 *       pName = temp_array[0];<br>
 *       pVal = temp_array[1];<br>
 *       if (jwpgp_inParamName == null) {<br>
 *         if (retVal == null) {<br>
 *           retVal = new Array();<br>
 *         }<br>
 *         retVal[j] = pName;<br>
 *         retVal[j + 1] = pVal;<br>
 *         j = j + 2;<br>
 *       } else {<br>
 *         if (pName == jwpgp_inParamName) {<br>
 *           retVal = pVal;<br>
 *           break;<br>
 *         }<br>
 *       }<br>
 *     }<br>
 *   }<br>
 *   return retVal;<br>
 * }<br>
 * <br>
 * Usage example:
 * <br><br>
 * <b>myParam = JWPGetParams("param1");</b><br>
 * This will return the value of the parameter named "param1", if present,
 * or null if not present.
 * <br><br>
 * <b>myParams = JWPGetParams();</b><br>
 * This will return an array in the variable myParams which contains all the
 * parameters and values passed to this page.  Elements of the array are
 * a repeating contiguous pattersn of name, value.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class GetParamsTag extends TagSupport {


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
      System.err.println("GetParamsTag" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(GetParamsTag.class);


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
      jsCode.append("function JWPGetParams(jwpgp_inParamName) {\n");
      jsCode.append("  retVal = null;\n");
      jsCode.append("  varvals = unescape(location.search.substring(1));\n");
      jsCode.append("  if (varvals) {\n");
      jsCode.append("    search_array = varvals.split(\"&\");\n");
      jsCode.append("    temp_array = new Array();\n");
      jsCode.append("    j = 0;\n");
      jsCode.append("    for (i = 0; i < search_array.length; i++) {\n");
      jsCode.append("      temp_array = search_array[i].split(\"=\");\n");
      jsCode.append("      pName = temp_array[0];\n");
      jsCode.append("      pVal = temp_array[1];\n");
      jsCode.append("      if (jwpgp_inParamName == null) {\n");
      jsCode.append("        if (retVal == null) {\n");
      jsCode.append("          retVal = new Array();\n");
      jsCode.append("        }\n");
      jsCode.append("        retVal[j] = pName;\n");
      jsCode.append("        retVal[j + 1] = pVal;\n");
      jsCode.append("        j = j + 2;\n");
      jsCode.append("      } else {\n");
      jsCode.append("        if (pName == jwpgp_inParamName) {\n");
      jsCode.append("          retVal = pVal;\n");
      jsCode.append("          break;\n");
      jsCode.append("        }\n");
      jsCode.append("      }\n");
      jsCode.append("    }\n");
      jsCode.append("  }\n");
      jsCode.append("  return retVal;\n");
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
