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
 * This class is a custom tag that renders the JWPStripChars() Javascript
 * function which takes in three strings.  The first is a string to
 * manipulate.  The second is either "strip" or "allow".  When "strip", then
 * the third string denotes a list of characters that will be stripped from
 * the first, all other are left.  It "allow", then any character NOT in the
 * third string will be removed from the first.
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
 * function JWPStripChars(jwpsc_inStr, jwpsc_stripOrAllow, jwpsc_charList) {<br>
 *   if (jwpsc_inStr == null || jwpsc_inStr == "" ||<br>
 *     jwpsc_charList == null || jwpsc_charList == "" ||<br>
 *     jwpsc_stripOrAllow == null || jwpsc_stripOrAllow == "") {<br>
 *      return "";<br>
 *   }<br>
 *   jwpsc_stripOrAllow = jwpsc_stripOrAllow.toLowerCase();<br>
 *   outStr = "";<br>
 *   for (i = 0; i < jwpsc_inStr.length; i++) {<br>
 *     nextChar = jwpsc_inStr.substr(i, 1);<br>
 *     keepChar = false;<br>
 *     for (j = 0; j < jwpsc_charList.length; j++) {<br>
 *       checkChar = jwpsc_charList.substr(j, 1);<br>
 *       if (jwpsc_stripOrAllow == "allow" && nextChar == checkChar) {<br>
 *         keepChar = true;<br>
 *       }<br>
 *       if (jwpsc_stripOrAllow == "strip" && nextChar != checkChar) {<br>
 *         keepChar = true;<br>
 *       }<br>
 *     }<br>
 *     if (keepChar == true) {<br>
 *       outStr = outStr + nextChar;<br>
 *     }<br>
 *   }<br>
 *   return outStr;<br>
 * }<br>
 * <br>
 * Usage example:
 * <br><br>
 * <b>myString = JWPStripChars("This is a test", "strip", " ");</b><br>
 * This will set myString equal to "Thisisatest", having strippg out all
 * spaces from the input string.
 * <br><br>
 * <b>myString = JWPStripChars("This is a test", "allow", "Tiat");</b><br>
 * This will set myString equal to "Tiiatt" because every character except
 * T, i, a, t (case-sensitive!) will be stripped out.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class StripCharsTag extends TagSupport {


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
      System.err.println("StripCharsTag" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(StripCharsTag.class);


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

    log.info("StripCharsTag.doStartTag()...");

    try {

      JspWriter    out    = pageContext.getOut();
      StringBuffer jsCode = new StringBuffer(2048);

      // Render opening <script> tag, if applicable.
      if (renderScriptTags == null ||
        renderScriptTags.equalsIgnoreCase("true")) {
        jsCode.append("<script>\n");
      }

      // Render function code.
      jsCode.append("function JWPStripChars(jwpsc_inStr, " +
        "jwpsc_stripOrAllow, jwpsc_charList) {\n");
      jsCode.append("  if (jwpsc_inStr == null || jwpsc_inStr == \"\" ||\n");
      jsCode.append("    jwpsc_charList == null || jwpsc_charList " +
        "== \"\" ||\n");
      jsCode.append("    jwpsc_stripOrAllow == null || jwpsc_stripOrAllow " +
        "== \"\") {\n");
      jsCode.append("    return \"\";\n");
      jsCode.append("  }\n");
      jsCode.append("  jwpsc_stripOrAllow = " +
        "jwpsc_stripOrAllow.toLowerCase();\n");
      jsCode.append("  outStr = \"\";\n");
      jsCode.append("  for (i = 0; i < jwpsc_inStr.length; i++) {\n");
      jsCode.append("    nextChar = jwpsc_inStr.substr(i, 1);\n");
      jsCode.append("    keepChar = false;\n");
      jsCode.append("    for (j = 0; j < jwpsc_charList.length; j++) {\n");
      jsCode.append("      checkChar = jwpsc_charList.substr(j, 1);\n");
      jsCode.append("      if (jwpsc_stripOrAllow == \"allow\" && nextChar " +
        "== checkChar) {\n");
      jsCode.append("        keepChar = true;\n");
      jsCode.append("      }\n");
      jsCode.append("      if (jwpsc_stripOrAllow == \"strip\" && nextChar " +
        "!= checkChar) {\n");
      jsCode.append("        keepChar = true;\n");
      jsCode.append("      }\n");
      jsCode.append("    }\n");
      jsCode.append("    if (keepChar == true) {\n");
      jsCode.append("      outStr = outStr + nextChar;\n");
      jsCode.append("    }\n");
      jsCode.append("  }\n");
      jsCode.append("  return outStr;\n");
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
