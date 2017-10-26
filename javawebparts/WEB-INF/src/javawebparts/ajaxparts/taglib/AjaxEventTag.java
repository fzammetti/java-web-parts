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


package javawebparts.ajaxparts.taglib;


import java.io.IOException;
import javawebparts.ajaxparts.taglib.config.AjaxConfig;
import javawebparts.ajaxparts.taglib.config.AjaxElement;
import javawebparts.ajaxparts.taglib.config.AjaxGroup;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class is a custom tag (AjaxEventTag) that emits the small script
 * element following an element to be Ajax-enabled.  This script it emits
 * will create a tag that will be something of an "anchor" that will later be
 * used to attach AJAX code to the target element.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>
 */
public class AjaxEventTag extends TagSupport {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("AjaxEventTag" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(AjaxEventTag.class);


  /**
   * The Ajax reference ID.
   */
  private String ajaxRef;


  /**
   * The DOM ID of the element to attach the event to.
   */
  private String attachTo;


  /**
   * Constructor.
   */
  public AjaxEventTag() {

    super();

  } // End constructor.


  /**
   * Sets the Ajax reference ID.
   *
   * @param inAjaxRef ajaxRef.
   */
  public void setAjaxRef(final String inAjaxRef) {

    ajaxRef = inAjaxRef;

  } // End setAjaxRef().


  /**
   * Returns the Ajax reference ID.
   *
   * @return ajaxRef.
   */
  public String getAjaxRef() {

    return ajaxRef;

  } // End getAjaxRef().


  /**
   * Sets the DOM ID to attach to.
   *
   * @param inAttachTo attachTo.
   */
  public void setAttachTo(final String inAttachTo) {

    attachTo = inAttachTo;

  } // End setAttachTo().


  /**
   * Returns the DOM ID to attach to.
   *
   * @return attachTo.
   */
  public String getAttachTo() {

    return attachTo;

  } // End getAttachTo().


  /**
   * Render the results of the tag.
   *
   * @return              Return code.
   * @throws JspException If anything goes wrong
   */
  public int doStartTag() throws JspException {

    log.debug("Processing AjaxEnableTag tag " + ajaxRef + "...");

    // See if initialization has occurred yet, and if not, do it now.
    if (!AjaxConfig.isFrozen()) {
      new AjaxInit().init(pageContext.getServletContext());
    }

    // Make sure the ajaxRef is present and in an appropriate form.
    if (!AjaxUtils.validateAjaxRef(ajaxRef)) {
      log.error("Mandatory attribute ajaxRef on <ajax:event/> tag " +
        "must be in the form xxxx/yyyy where xxxx is the group's " +
        "ajaxRef and yyyy is the element's ajaxRef");
      return SKIP_BODY;
    }

    // Make sure the attachTo value is not blank/empty if given.
    if ((attachTo != null) && (attachTo.trim().length() == 0)) {
      log.error("Optional attribute attachTo on <ajax:event/> tag " +
        "must not be empty or blank");
      return SKIP_BODY;
    }

    // Parse the ajaxRef into the group and element parts
    String groupRef   = AjaxUtils.groupRefFromAjaxRef(ajaxRef);
    String elementRef = AjaxUtils.elementRefFromAjaxRef(ajaxRef);
    log.debug("ajaxRef of parent group = " + groupRef);
    log.debug("ajaxRef of element = " + elementRef);

    // Look up group in config.  If not found, abort.
    AjaxGroup aGroup = (AjaxGroup)AjaxConfig.getGroup(groupRef);
    if (aGroup == null) {
      log.error("Trying to render anchor, but couldn't find group ajaxRef = " +
        groupRef);
      return SKIP_BODY;
    }
    log.debug("aGroup = " + aGroup);

    // Look up element in config.  If not found, abort.
    AjaxElement aElement = (AjaxElement)aGroup.getElement(elementRef);
    if (aElement == null) {
      log.error("Trying to render anchor, but oouldn't find element " +
        "ajaxRef = " + elementRef);
      return SKIP_BODY;
    }
    log.debug("aElement = " + aElement);

    // Update the list of error handlers, request handlers, and response
    // handlers used on this page to include the new ones for this element,
    // and also add this ajaxRef to the collection encountered on this page.
    AjaxUtils.updatePageScopeVars(ajaxRef, pageContext, aGroup, aElement);

    // Create the anchor code to be rendered.
    String sCode = "<div style=\"display:none;\" id=\"" +
      ajaxRef.replace('/', '_') + "\"";
    if (attachTo != null) {
      sCode = sCode + " title=\"" + attachTo + "\"";
    }
    sCode = sCode + ">&nbsp;</div>";

    // Display it for debugging purposes.
    log.debug("Ready to emit: " + sCode);

    // Actually emit the code that has been built up to the page.
    JspWriter out = pageContext.getOut();
    try {
      out.print(sCode);
    } catch (IOException ioe) {
      throw new JspException(ioe.toString());
    }

    return SKIP_BODY;

  } //  End doStartTag()


} // End class
