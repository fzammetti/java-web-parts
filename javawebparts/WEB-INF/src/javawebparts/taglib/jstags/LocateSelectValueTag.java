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
 * This class is a custom tag that renders the JWPLocateSelectValue() Javascript
 * function which takes in a reference to a &lt;select&gt; tag and locates the
 * value in it (and selects it) that is passed in.  It returns false if the
 * value was not found at all.  The third paramter in accepts determines
 * whether the value should be selected.  So if you simply want to know if a
 * value is present, pass in false, or true to go ahead and select the value if
 * found.  The fourth parameter determines whether the search is case-sensitive
 * or not.  Pass true to do a case-insensitive search.
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
 * function JWPLocateSelectValue(jwpsv_select, jwpsv_value, jwpsv_justFind,
 *   jwpsv_caseInsensitive) {<br>
 *   if (jwpsv_select == null ||<br>
 *     jwpsv_value == null || jwpsv_value == "" ||<br>
 *     jwpsv_caseInsensitive == null ||<br>
 *     jwpsv_justFind == null) {<br>
 *     return;<br>
 *   }<br>
 *   if (jwpsv_caseInsensitive) {<br>
 *     jwpsv_value = jwpsv_value.toLowerCase();<br>
 *   }<br>
 *   found = false;<br>
 *   for (i = 0; (i < jwpsv_select.length) && !found; i++) {<br>
 *     nextVal = jwpsv_select.options[i].value;<br>
 *     if (jwpsv_caseInsensitive) {<br>
 *       nextVal = nextVal.toLowerCase();<br>
 *     }<br>
 *     if (nextVal == jwpsv_value) {<br>
 *       found = true;<br>
 *       if (!jwpsv_justFind) {<br>
 *         jwpsv_select.options[i].selected = true;<br>
 *       }<br>
 *     }<br>
 *   }<br>
 *   return found;<br>
 * }<br>
 * <br>
 * Usage example:
 * <br><br>
 * <b>JWPLocateSelectValue(myForm.mySelect, "opt1");</b><br>
 * This results in the &lt;option&gt; element of the &lt;select&gt; element
 * named "mySelect" in the &lt;form&gt; named "myForm" to be selected, it
 * present.  Value matching will be case-sensitive.
 * <br><br>
 * <b>JWPLocateSelectValue(myForm.mySelect, "opt1", true);</b><br>
 * This will search the above mentioned dropdown and return true of "opt1" is
 * found, false if not.  Value matching will be case-sensitive.
 * <br><br>
 * <b>JWPLocateSelectValue(myForm.mySelect, "opt1", true, true);</b><br>
 * This will again find the value, if present, but will do so without regard
 * for case (i.e., opt1 == OPT1).
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class LocateSelectValueTag extends TagSupport {


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
      System.err.println("LocateSelectValueTag" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(LocateSelectValueTag.class);


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

    log.info("LocateSelectValueTag.doStartTag()...");

    try {

      JspWriter    out    = pageContext.getOut();
      StringBuffer jsCode = new StringBuffer(2048);

      // Render opening <script> tag, if applicable.
      if (renderScriptTags == null ||
        renderScriptTags.equalsIgnoreCase("true")) {
        jsCode.append("<script>\n");
      }

      // Render function code.
      jsCode.append("function JWPLocateSelectValue(jwpsv_select, " +
        "jwpsv_value, jwpsv_justFind, \n");
      jsCode.append("jwpsv_caseInsensitive) {\n");
      jsCode.append("  if (jwpsv_select == null ||\n");
      jsCode.append("    jwpsv_value == null || jwpsv_value == \"\" ||\n");
      jsCode.append("    jwpsv_caseInsensitive == null ||\n");
      jsCode.append("    jwpsv_justFind == null) {\n");
      jsCode.append("    return;\n");
      jsCode.append("  }\n");
      jsCode.append("  if (jwpsv_caseInsensitive) {\n");
      jsCode.append("    jwpsv_value = jwpsv_value.toLowerCase();\n");
      jsCode.append("  }\n");
      jsCode.append("  found = false;\n");
      jsCode.append("  for (i = 0; (i < jwpsv_select.length) && !found; i++) " +
        "{\n");
      jsCode.append("    nextVal = jwpsv_select.options[i].value;\n");
      jsCode.append("    if (jwpsv_caseInsensitive) {\n");
      jsCode.append("      nextVal = nextVal.toLowerCase();\n");
      jsCode.append("    }\n");
      jsCode.append("    if (nextVal == jwpsv_value) {\n");
      jsCode.append("      found = true;\n");
      jsCode.append("      if (!jwpsv_justFind) {\n");
      jsCode.append("        jwpsv_select.options[i].selected = true;\n");
      jsCode.append("      }\n");
      jsCode.append("    }\n");
      jsCode.append("  }\n");
      jsCode.append("  return found;\n");
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
