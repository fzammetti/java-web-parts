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
 * This class is a custom tag that renders the JWPFormToXML() Javascript
 * function which can be called to generate an XML document (technically, a
 * string of XML, not a true XML document) from an HTML form.
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
 * function JWPFormToXML(jwpftx_inForm, jwpftx_inRootElement) {<br>
 *   if (jwpftx_inForm == null) {<br>
 *     alert("JWPFormToXML: Form not passed in, or value was null");<br>
 *     return null;<br>
 *   }<br>
 *   if (jwpftx_inRootElement == null) {<br>
 *     alert("JWPFormToXML: Root element passed in was null");<br>
 *     return null;<br>
 *   }<br>
 *   outXML = "<" + jwpftx_inRootElement + ">";<br>
 *   for (i = 0; i < jwpftx_inForm.length; i++) {<br>
 *     ofe = jwpftx_inForm[i];<br>
 *     ofeType = ofe.type.toUpperCase();<br>
 *     ofeName = ofe.name;<br>
 *     ofeValue = ofe.value;<br>
 *     if (ofeType == "TEXT" || ofeType == "HIDDEN" ||<br>
 *       ofeType == "PASSWORD" || ofeType == "SELECT-ONE" ||<br>
 *       ofeType == "TEXTAREA") {
 *       outXML += "<" + ofeName + ">" + ofeValue + "</" + ofeName + ">"<br>
 *     }<br>
 *     if (ofeType == "RADIO" && ofe.checked == true) {<br>
 *       outXML += "<" + ofeName + ">" + ofeValue + "</" + ofeName + ">"<br>
 *     }<br>
 *     if (ofeType == "CHECKBOX") {<br>
 *       if (ofe.checked == true) {<br>
 *         cbval = "true";<br>
 *       } else {<br>
 *         cbval = "false";<br>
 *       }<br>
 *       outXML = outXML + "<" + ofeName + ">" + cbval + "</" + ofeName + ">"
 * <br>
 *     }<br>
 *     outXML += "";<br>
 *   }<br>
 *   outXML += "</" + jwpftx_inRootElement + ">";<br>
 *   return outXML;<br>
 * }<br>
 * <br>
 * Usage example:
 * <br><br>
 * <b>myXML = JWPFormToXML(document.forms['testform'],'Person');</b><br>
 * This will return a string of XML with the element &lt;Person&gt; as the
 * root, and all the fields from the form named "testForm" as child elements.
 * For instance, if there is
 * &lt;input type="text" name="firstName" value="Frank"&gt; on the form. the
 * resultant XML would be <Person><firstName>Frank</firstName></Person>.
 * The XML is one continuous string, there are no linebreaks and no tab
 * indents anywhere.  Only form elements of type TEXT, HIDDEN, PASSWORD,
 * SELECT-ONE, TEXTAREA, RADIO and CHECKBOX will result in elements being
 * added to the XML.  For RADIO types, any selected radio's value will be
 * sent.  For Checkboxes, any which are checked are sent, those which are not
 * checked are not sent.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class FormToXMLTag extends TagSupport {


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
      System.err.println("FormToXMLTag" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(FormToXMLTag.class);


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
      jsCode.append("function JWPFormToXML(jwpftx_inForm, " +
        "jwpftx_inRootElement) {\n");
      jsCode.append("  if (jwpftx_inForm == null) {\n");
      jsCode.append("    alert(\"JWPFormToXML: Form not passed in, or " +
        "value was null\");\n");
      jsCode.append("    return null;\n");
      jsCode.append("  }\n");
      jsCode.append("  if (jwpftx_inRootElement == null) {\n");
      jsCode.append("    alert(\"JWPFormToXML: Root element passed in was " +
        "null\");\n");
      jsCode.append("    return null;\n");
      jsCode.append("  }\n");
      jsCode.append("  outXML = \"<\" + jwpftx_inRootElement + \">\";\n");
      jsCode.append("  for (i = 0; i < jwpftx_inForm.length; i++) {\n");
      jsCode.append("    ofe = jwpftx_inForm[i];\n");
      jsCode.append("    ofeType = ofe.type.toUpperCase();\n");
      jsCode.append("    ofeName = ofe.name;\n");
      jsCode.append("    ofeValue = ofe.value;\n");
      jsCode.append("    if (ofeType == \"TEXT\" || ofeType == \"HIDDEN\" " +
        "|| ofeType == \"PASSWORD\" ||\n");
      jsCode.append("      ofeType == \"SELECT-ONE\" || ofeType == " +
        "\"TextArea\") {\n");
      jsCode.append("      outXML += \"<\" + ofeName + \">\" + ofeValue + " +
        "\"</\" + ofeName + \">\"\n");
      jsCode.append("    }\n");
      jsCode.append("    if (ofeType == \"RADIO\" && ofe.checked == true) {\n");
      jsCode.append("      outXML += \"<\" + ofeName + \">\" + ofeValue + " +
        "\"</\" + ofeName + \">\"\n");
      jsCode.append("    }\n");
      jsCode.append("    if (ofeType == \"CHECKBOX\") {\n");
      jsCode.append("      if (ofe.checked == true) {\n");
      jsCode.append("        cbval = \"true\";\n");
      jsCode.append("      } else {\n");
      jsCode.append("        cbval = \"false\";\n");
      jsCode.append("      }\n");
      jsCode.append("      outXML = outXML + \"<\" + ofeName + \">\" + " +
        "cbval + \"</\" + ofeName + \">\"\n");
      jsCode.append("    }\n");
      jsCode.append("    outXML += \"\";\n");
      jsCode.append("  }\n");
      jsCode.append("  outXML += \"</\" + jwpftx_inRootElement + \">\";\n");
      jsCode.append("  return outXML;\n");
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
