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


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javawebparts.ajaxparts.taglib.config.AjaxConfig;
import javawebparts.ajaxparts.taglib.config.AjaxElement;
import javawebparts.ajaxparts.taglib.config.AjaxEvent;
import javawebparts.ajaxparts.taglib.config.AjaxGroup;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class is a custom tag (AjaxTimerTag) that emits the script
 * necessary to set up a timed AJAX event.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>
 */
public class AjaxTimerTag extends TagSupport {


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
   * Should the timer be started immediately upon page load, or will the
   * developer take responsibility for kicking off a timer?
   */
  private String startOnLoad;


  /**
   * This stores the frequency in milliseconds that the AJAX event should fire.
   */
  private String frequency;


  /**
   * Constructor.
   */
  public AjaxTimerTag() {

    super();

  } // End constructor.


  /**
   * Accessor for frequency field.
   *
   * @return String Current value of frequency Field.
   */
  public String getFrequency() {

    return frequency;

  } // End getFrequency().


  /**
   * Mutator for frequency field.
   *
   * @param inFrequency New value of frequency field.
   */
  public void setFrequency(final String inFrequency) {

    frequency = inFrequency;

  } // End setFrequency().


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
   * Sets the startOnLoad field.
   *
   * @param inStartOnLoad startOnLoad.
   */
  public void setStartOnLoad(final String inStartOnLoad) {

    startOnLoad = inStartOnLoad;

  } // End setStartOnLoad().


  /**
   * Returns the startOnLoad field value.
   *
   * @return startOnLoad.
   */
  public String getStartOnLoad() {

    return startOnLoad;

  } // End getStartOnLoad().


  /**
   * Render the results of the tag.
   *
   * @return              Return code.
   * @throws JspException If anything goes wrong
   */
  public int doStartTag() throws JspException {

    log.debug("Processing AjaxTimerTag tag " + ajaxRef + "...");

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

    // Parse the ajaxRef into the group and element parts
    String groupRef   = AjaxUtils.groupRefFromAjaxRef(ajaxRef);
    String elementRef = AjaxUtils.elementRefFromAjaxRef(ajaxRef);
    log.debug("ajaxRef of parent group = " + groupRef);
    log.debug("ajaxRef of element = " + elementRef);

    // Look up group in config.  If not found, abort.
    AjaxGroup aGroup = (AjaxGroup)AjaxConfig.getGroup(groupRef);
    if (aGroup == null) {
      log.error("Trying to render timer, but ouldn't find group ajaxRef = " +
        groupRef);
      return SKIP_BODY;
    }
    log.debug("aGroup = " + aGroup);

    // Look up element in config.  If not found, abort.
    AjaxElement aElement = (AjaxElement)aGroup.getElement(elementRef);
    if (aElement == null) {
      log.error("Trying to render timer, but couldn't find element " +
        "ajaxRef = " + elementRef);
      return SKIP_BODY;
    }
    log.debug("aElement = " + aElement);

    // Look through the list of events configured for this element and see if
    // any of them are "timer", and throw an error if not.
    HashMap events   = aElement.getEvents();
    int     manFound = 0;
    for (Iterator it = events.entrySet().iterator(); it.hasNext();) {
      Map.Entry e      = (Map.Entry)it.next();
      AjaxEvent aEvent = (AjaxEvent)e.getValue();
      if (aEvent.getType().equalsIgnoreCase("timer")) {
        manFound++;
      }
    }
    if (manFound != 1) {
      log.error("For timer tag with ajaxRef '" + ajaxRef + "', there must " +
        "be one and only one event of type 'timer' defined in config file.");
      return SKIP_BODY;
    }

    // Update the list of error handlers, request handlers, and response
    // handlers used on this page to include the new ones for this element,
    // and also add this ajaxRef to the collection encountered on this page.
    AjaxUtils.updatePageScopeVars(ajaxRef, pageContext, aGroup, aElement);

    // Also add to page-scoped list of timer parameter HashMaps keyed by
    // ajaxRef.
    HashMap timerParams = (HashMap)pageContext.getAttribute(
      "timerParams", PageContext.REQUEST_SCOPE);
    if (timerParams == null) {
      timerParams = new HashMap();
    }
    HashMap params = new HashMap();
    params.put("startOnLoad", startOnLoad);
    params.put("frequency",   frequency);
    timerParams.put(ajaxRef,  params);
    pageContext.setAttribute("timerParams", timerParams,
      PageContext.REQUEST_SCOPE);

    // That's it, we're done!  The enable tag will render the function the
    // developer will call.
    return SKIP_BODY;

  } //  End doStartTag()


} // End class
