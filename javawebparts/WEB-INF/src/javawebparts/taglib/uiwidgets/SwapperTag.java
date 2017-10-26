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


package javawebparts.taglib.uiwidgets;


import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class is a custom tag that renders the Swapper UI widget.  A Swapper
 * has two boxes separated by four buttons, one button to swap an element from
 * the left box to the right, one to swap from right to left, one to swap all
 * items from the left to the right and one to swap all from right to left.
 * It also has two buttons next to the right box that allows items to be moved
 * up or down in the list.  The left box is considered the "source" box and
 * it is meant for the user to construct a list on the right from the items
 * on the left.
 * <br><br>
 * This tag renders all necessary HTML and Javascript for this widget, including
 * the function JWP_UIWT_getList(), which can be called to retrieve the list
 * on either side of the swapper.  Pass it the ID that you set in the tag
 * iteself, and optionally the constant JWP_UIWT_LEFT or JWP_UIWT_RIGHT to
 * specify which side you want (if you don't pass a second parameter you will
 * get the list on the right).  This function returns an array of strings.
 * Here is an example to simply display the list on the right:
 * <br><br>
 * alert(JWP_UIWT_getList("swapper1"));
 * <br><br>
 * The contents of the tag (i.e., between the opening and closing tag) is for
 * you to fill out.  It should be the HTML for the &lt;option&gt; elements
 * of a &lt;select&gt; tag.  These options will be populated in the left box
 * of the swapper initially.  You can use whatever techniques you like to
 * generate this markup, or it can of course be static.  See the usage example
 * below for an illustration of how this all fits together (this is the same
 * usage example demonstrated in the sample app).
 * <br><br>
 * This tag uses the following attributes:
 * <br><br>
 * <ul>
 * <li><b>id</b> - Required - The Swapper widget is envlosed in a &lt;div&gt;
 * tag, and this is the ID that *lt;div*&gt; will be known as.</li>
 * <br><br>
 * <li><b>boxWidth</b> - Optional - This is the width of the two boxes of the
 * swapper, measured in pixels.  If you do not specify anything, the default
 * value of 100 pixels will be used.  It is your responsibility to ensure that
 * the size you specify is large enough to accomodate any content you may add
 * to the swapper.</li>
 * <br><br>
 * <li><b>boxHeight</b> - Optional - This is the height of the two boxes of the
 * swapper, measured in items (i.e., a value of 10 means it will be tall enough
 * to show 10 items at a time).  If you do not specify anything, the default
 * value of 10 items will be used.</li>
 * <br><br>
 * <li><b>divStyle</b> - Optional - This is an inline stylesheet specification
 * to be applied to the &lt;div&gt; surrounding the swapper.  Note that the
 * value "width:1px;" will be prepended to whatever you put here.  You can
 * override this with specification if you wish.  If you do not specify
 * anything, just the width specification will be rendered.</li>
 * <br><br>
 * <li><b>boxStyle</b> - Optional - This is an inline stylesheet specification
 * to be applied to the two boxes of the swapper.  Note that the
 * value "width:100px;" will be prepended to whatever you put here.  You can
 * override this with specification if you wish.  If you do not specify
 * anything, just the width specification will be rendered.</li>
 * <br><br>
 * <li><b>buttonStyle</b> - Optional - This is an inline stylesheet
 * specification to be applied to the buttons of the swapper.  Note that the
 * value "width:30px;" will be prepended to whatever you put here.  You can
 * override this with specification if you wish.  If you do not specify
 * anything, just the width specification will be rendered.</li>
 * <br><br>
 * <li><b>divClass</b> - Optional - This is an CSS classname to apply to the
 * &lt;div&gt; surrounding the swapper.  It you do not specify anything then
 * the class attribute will not be rendered at all.  Note that the class
 * attribute comes before the style attribute, if both are present, so you can
 * apply a class to this element and override it with the divStyle attribute.
 * </li>
 * <br><br>
 * <li><b>boxClass</b> - Optional - This is an CSS classname to apply to the
 * two boxes of the swapper.  It you do not specify anything then
 * the class attribute will not be rendered at all.  Note that the class
 * attribute comes before the style attribute, if both are present, so you can
 * apply a class to this element and override it with the boxStyle attribute.
 * </li>
 * <br><br>
 * <li><b>buttonClass</b> - Optional - This is an CSS classname to apply to the
 * buttons of the swapper.  It you do not specify anything then
 * the class attribute will not be rendered at all.  Note that the class
 * attribute comes before the style attribute, if both are present, so you can
 * apply a class to this element and override it with the buttonStyle attribute.
 * </li>
 * </ul>
 * Here is a usage example:
 * <br><br>
 * &lt;uiwidgets:swapper id=&quot;swapper1&quot; boxWidth=&quot;150&quot;
 * divStyle=&quot;border:2 solid #017fff;&quot;
 * boxStyle=&quot;color:#ff0000;&quot;&gt;<br>
 * &nbsp;&nbsp;&lt;option value=&quot;value1&quot;&gt;
 * Value 1 Text&lt;/option&gt;<br>
 * &nbsp;&nbsp;&lt;option value=&quot;value2&quot;&gt;
 * Value 2 Text&lt;/option&gt;<br>
 * &nbsp;&nbsp;&lt;option value=&quot;value3&quot;&gt;
 * Value 3 Text&lt;/option&gt;<br>
 * &nbsp;&nbsp;&lt;option value=&quot;value4&quot;&gt;
 * Value 4 Text&lt;/option&gt;<br>
 * &nbsp;&nbsp;&lt;option value=&quot;value5&quot;&gt;
 * Value 5 Text&lt;/option&gt;<br>
 * &lt;/uiwidgets:swapper&gt;<br>
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class SwapperTag extends BodyTagSupport {

  private static final String FUNCTIONS_DECLARED_ATTRIBUTE = 
      "javawebparts.taglib.uiwidget.SwapperTag.FunctionAreDefined";

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
      Class.forName("javax.servlet.jsp.tagext.BodyTagSupport");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("SwapperTag" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(SwapperTag.class);


  /**
   * The ID of the <div> the Swapper is in.
   */
  private String id;


  /**
   * The width, in pixels, of the two boxes of the Swapper.  This value should
   * be set to a value large enough to ensure that any value you may add to
   * the boxes will fit.  This will default to 100 if not present.
   */
  private String boxWidth;


  /**
   * The height, in number of items, of the two boxes of the Swapper.  This
   * is the same thing as the size attribute of a <select> tag and denotes
   * how many items at a time can be seen in the box.  This will default to 10
   * if not present.
   */
  private String boxHeight;


  /**
   * The style of the <div> element the Swapper is in.  The style attribute
   * will always be rendered with, minimally, the value "width:1px;".  If you
   * supply a value, that width setting will be prepended to what you
   * supply, so you will be able to override it if you so chooce.
   */
  private String divStyle;


  /**
   * The style of the two boxes of the Swapper.  If not supplied, no style
   * attribute will be rendered.
   */
  private String boxStyle;


  /**
   * The style of all the buttons of the Swapper.  The style attribute
   * will always be rendered with, minimally, the value "width:30px;".  If you
   * supply a value, that width setting will be prepended to what you
   * supply, so you will be able to override it if you so chooce.
   */
  private String buttonStyle;


  /**
   * The stylesheet class of the <div> the Swapper belongs in.  Note that
   * you *can* define both style and class, but style will override class
   * settings.
   */
  private String divClass;


  /**
   * The stylesheet class of the boxes of the Swapper.  Note that
   * you *can* define both style and class, but style will override class
   * settings.
   */
  private String boxClass;


  /**
   * The stylesheet class of all the buttons of the Swapper.  Note that
   * you *can* define both style and class, but style will override class
   * settings.
   */
  private String buttonClass;


  /**
   * Mutator for ID field.
   *
   * @param inID ID.
   */
  public void setId(String inID) {

    id = inID;

  } // End setId().


  /**
   * Mutator for boxWidth field.
   *
   * @param inBoxWidth boxWidth.
   */
  public void setBoxWidth(String inBoxWidth) {

    boxWidth = inBoxWidth;

  } // End setBoxWidth().


  /**
   * Mutator for boxHeight field.
   *
   * @param inBoxHeight boxHeight.
   */
  public void setBoxHeight(String inBoxHeight) {

    boxHeight = inBoxHeight;

  } // End setBoxHeight().


  /**
   * Mutator for divStyle field.
   *
   * @param inDivStyle divStyle.
   */
  public void setDivStyle(String inDivStyle) {

    divStyle = inDivStyle;

  } // End setDivStyle().


  /**
   * Mutator for boxStyle field.
   *
   * @param inBoxStyle setBoxStyle.
   */
  public void setBoxStyle(String inBoxStyle) {

    boxStyle = inBoxStyle;

  } // End setBoxStyle().


  /**
   * Mutator for buttonStyle field.
   *
   * @param inButtonStyle buttonStyle.
   */
  public void setButtonStyle(String inButtonStyle) {

    buttonStyle = inButtonStyle;

  } // End setButtonStyle().


  /**
   * Mutator for divClass field.
   *
   * @param inDivClass divClass.
   */
  public void setDivClass(String inDivClass) {

    divClass = inDivClass;

  } // End setDivClass().


  /**
   * Mutator for boxClass field.
   *
   * @param inBoxClass boxClass.
   */
  public void setBoxClass(String inBoxClass) {

    boxClass = inBoxClass;

  } // End setBoxClass().


  /**
   * Mutator for buttonClass field.
   *
   * @param inButtonClass buttonClass.
   */
  public void setButtonClass(String inButtonClass) {

    buttonClass = inButtonClass;

  } // End setButtonClass().


  /**
   * Render widget code.
   *
   * @return              Return code.
   * @throws JspException If anything goes wrong
   */
  public int doStartTag() throws JspException {

    log.info("SwapperTag.doStartTag()...");

    // Set defaults.
    if (boxWidth == null) {
      boxWidth = "100";
    }
    if (boxHeight == null) {
      boxHeight = "10";
    }
    if (divStyle == null) {
      divStyle = "";
    }
    divStyle = "width:1px;" + divStyle;
    if (boxStyle == null) {
      boxStyle = "";
    }
    boxStyle = "width:" + boxWidth + "px;" + boxStyle;
    if (buttonStyle == null) {
      buttonStyle = "";
    }
    buttonStyle = "width:30px;" + buttonStyle;
    if (divClass == null) {
      divClass = "";
    } else {
      divClass = " class=\"" + divClass + "\"";
    }
    if (boxClass == null) {
      boxClass = "";
    } else {
      boxClass = " class=\"" + boxClass + "\"";
    }
    if (buttonClass == null) {
      buttonClass = "";
    } else {
      buttonClass = " class=\"" + buttonClass + "\"";
    }

    try {

      
      JspWriter    jw  = pageContext.getOut();
      StringBuffer out = new StringBuffer(2048);

      if (pageContext.getAttribute(FUNCTIONS_DECLARED_ATTRIBUTE) == null) {
        outputFunctions(out);
        pageContext.setAttribute(FUNCTIONS_DECLARED_ATTRIBUTE, Boolean.FALSE);
      } 
      
      out.append("<div" + divClass + " style=\"" + divStyle + "\" id=\"" + id +
        "\">\n");
      out.append("  <table border=\"0\" cellpadding=\"6\" " +
        "cellspacing=\"0\">\n");
      out.append("    <tr>\n");
      out.append("      <td>\n");
      out.append("        <select id=\"JWP_UIWT_" + id + "_leftSelect\" " +
        "multiple=\"true\" size=\"" + boxHeight + "\"" + boxClass +
        " style=\"" + boxStyle + "\">\n");

      jw.print(out);

    } catch (IOException ioe) {
      throw new JspException(ioe.toString());
    }
    return EVAL_BODY_BUFFERED;

  } //  End doStartTag()

  /**
   * Writes the <tt>&lt;script&gt;</tt> containing the JavaScript function
   * to the passed in <tt>StringBuffer</tt>.
   * 
   * @param out the <tt>StringBuffer</tt> to write to.
   */
  private void outputFunctions(StringBuffer out) {
    out.append("<script>\n");
    out.append("  var JWP_UIWT_LEFT = 1;\n");
    out.append("  var JWP_UIWT_RIGHT = 2;\n");
    out.append("  var JWP_UIWT_UP = 3;\n");
    out.append("  var JWP_UIWT_DOWN = 4;\n");
    out.append("  function JWP_UIWT_swap(inID, inSrc, inAll) {\n");
    out.append("    var leftID = \"JWP_UIWT_\" + inID + \"_leftSelect\";\n");
    out.append("    var rightID = \"JWP_UIWT_\" + inID + \"_rightSelect\"" + 
      ";\n");
    out.append("    if (inSrc == JWP_UIWT_LEFT) {\n");
    out.append("      var oSrc = document.getElementById(leftID);\n");
    out.append("      var oDest = document.getElementById(rightID);\n");
    out.append("    } else {\n");
    out.append("      var oSrc = document.getElementById(rightID);\n");
    out.append("      var oDest = document.getElementById(leftID);\n");
    out.append("    }\n");
    out.append("    for (i = 0; i < oSrc.options.length; i++) {\n");
    out.append("      oOpt = oSrc.options[i];\n");
    out.append("      if (oOpt.selected || inAll) {\n");
    out.append("        oNew = new Option(oOpt.text, oOpt.value);\n");
    out.append("        oDest.options[oDest.options.length] = oNew;\n");
    out.append("        oSrc.remove(i);\n");
    out.append("        i--;\n");
    out.append("      }\n");
    out.append("    }\n");
    out.append("  }\n");
    out.append("  function JWP_UIWT_sort(inID, inDir) {\n");
    out.append("    oObj = document.getElementById(" +
      "\"JWP_UIWT_\" + inID + \"_rightSelect\");\n");
    out.append("    if (inDir == JWP_UIWT_UP) {\n");
    out.append("      for (i = 1; i < oObj.options.length; i++) {\n");
    out.append("        oOpt = oObj.options[i];\n");
    out.append("        if (oOpt.selected) {\n");
    out.append("          oOpt1 = new Option(oOpt.text, oOpt.value);\n");
    out.append("          oOpt2 = new Option(oObj.options[i - 1].text, " +
      "oObj.options[i - 1].value);\n");
    out.append("          oObj.options[i] = oOpt2;\n");
    out.append("          oObj.options[i - 1] = oOpt1;\n");
    out.append("          oObj.options[i - 1].selected = true;\n");
    out.append("        }\n");
    out.append("      }\n");
    out.append("    } else {\n");
    out.append("      for (i = oObj.options.length - 2; i >= 0; i--) {\n");
    out.append("        oOpt = oObj.options[i];\n");
    out.append("        if (oOpt.selected) {\n");
    out.append("          oOpt1 = new Option(oOpt.text, oOpt.value);\n");
    out.append("          oOpt2 = new Option(oObj.options[i + 1].text, " +
      "oObj.options[i + 1].value);\n");
    out.append("          oObj.options[i] = oOpt2;\n");
    out.append("          oObj.options[i + 1] = oOpt1;\n");
    out.append("          oObj.options[i + 1].selected = true;\n");
    out.append("        }\n");
    out.append("      }\n");
    out.append("    }\n");
    out.append("  }\n");
    out.append("  function JWP_UIWT_getList(inID, inWhich) {\n");
    out.append("    if (inWhich == null || JWP_UIWT_RIGHT) {\n");
    out.append("      oObj = document.getElementById(" +
      "\"JWP_UIWT_\" + inID + \"_rightSelect\");\n");
    out.append("    } else if (inWhich == JWP_UIWT_LEFT) {\n");
    out.append("      oObj = document.getElementById(" +
      "\"JWP_UIWT_\" + inID + \"_leftSelect\");\n");
    out.append("    } else {\n");
    out.append("      return null;\n");
    out.append("    }\n");
    out.append("    a = new Array();\n");
    out.append("    for (i = 0; i < oObj.options.length; i++) {\n");
    out.append("      a[i] = oObj.options[i].value;\n");
    out.append("    }\n");
    out.append("    return a;\n");
    out.append("  }\n");
    out.append("</script>\n");
  }

  /**
   * Render widget code.
   *
   * @return              Return code.
   * @throws JspException If anything goes wrong
   */
  public int doEndTag() throws JspException {

    log.info("SwapperTag.doEndTag()...");

    try {

      JspWriter    jw  = pageContext.getOut();
      StringBuffer out = new StringBuffer(2048);

      // Render the body content, which is supposed to be just <option> tags.

      // Complete the output.
      out.append(bodyContent.getString());
      out.append("        </select>\n");
      out.append("      </td>\n");
      out.append("      <td valign=\"middle\">\n");
      out.append("        <input type=\"button\" value=\"&gt;\"" + buttonClass +
        " style=\"" + buttonStyle +
        "\" onClick=\"JWP_UIWT_swap('" + id + "', JWP_UIWT_LEFT, " +
        "false);\"><br><input type=\"button\" value=\"&lt;\"" + buttonClass +
        " style=\"" + buttonStyle +
        "\" onClick=\"JWP_UIWT_swap('" + id + "', JWP_UIWT_RIGHT, false);\">" +
        "\n");
      out.append("        <br/>\n");
      out.append("        <input type=\"button\" value=\"&gt;&gt;\"" +
        buttonClass + " style=\"" + buttonStyle +
        "\" onClick=\"JWP_UIWT_swap('" + id + "', JWP_UIWT_LEFT, " +
        "true);\"><br><input type=\"button\" value=\"&lt;&lt;\"" + buttonClass +
        " style=\"" + buttonStyle +
        "\" onClick=\"JWP_UIWT_swap('" + id + "', JWP_UIWT_RIGHT, true);\">\n");
      out.append("      </td>\n");
      out.append("      <td>\n");
      out.append("        <select id=\"JWP_UIWT_" + id + "_rightSelect\" " +
        "multiple=\"true\" size=\"" + boxHeight + "\"" + boxClass +
        " style=\"" + boxStyle + "\"/>\n");
      out.append("      </td>\n");
      out.append("      <td>\n");
      out.append("        <input type=\"button\" value=\"&uarr;\"" +
        buttonClass + " style=\"" + buttonStyle +
        "\" onClick=\"JWP_UIWT_sort('" + id + "', JWP_UIWT_UP);\"><br>" +
        "<input type=\"button\" value=\"&darr;\"" + buttonClass + " style=\"" +
        buttonStyle + "\" " + "onClick=\"JWP_UIWT_sort('" + id + "', " +
        "JWP_UIWT_DOWN);\">\n");
      out.append("      </td>\n");
      out.append("    </tr>\n");
      out.append("  </table>\n");
      out.append("</div>\n");

      jw.print(out);

    } catch (IOException ioe) {
      throw new JspException(ioe.toString());
    }
    return EVAL_PAGE;

  } //  End doEndTag()


} // End class.
