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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class is a custom tag that renders the Calendar UI widget.
 * <br><br>
 *
 * The attribute this tag takes are:
 * <br>
 * <ul>
 * <li><b>minYear</b> - The beginning of the range of years to display in
 * the year dropdown.  Required: No.  Default: The current year.</ul>
 * <li><b>maxYear</b> - The end of the range of years to display in
 * the year dropdown.  Required: No.  Default: The current year.</ul>
 * <li><b>styleOverrides</b> - This is a comma-separated list of the style
 * selectors you will supply yourself.  If this attribute is not present, or
 * is an empty string, or is the value "none", then all the built-in
 * style selectors will be rendered on the page.  In other words, there will be
 * no way for you to style the calendar yourself.  You can override any subset
 * of the style selectors, or all of them.  To override all of them, set this
 * attribute to "all".  To override a subset of them, set this attribute to
 * a comma-separated list of the selectors you will override, taken from the
 * following list:
 * <br><br>
 * - cssCalendarOuter - This styles the outer border.<br>
 * - cssCalendarMain - This styles the main background portion of the
 * calendar.<br>
 * - cssCalendarButton - This styles the two buttons.<br>
 * - cssCalendarDropdown - This styles the two dropdowns.<br>
 * - cssCalendarDay - This styles days of the week (not weekends).<br>
 * - cssCalendarWeekend - This styles weekend days (not weekdays).<br>
 * - cssCalendarHover - This styles any day of the week when the mouse is
 * hovering over it.<br>
 * <br>
 * Because when you override a selector you must supply the entire selector,
 * i.e., you cannot override just the background-color attribute of the
 * buttons, you must instead supply the entire cssCalendarButton selector,
 * here are the default styles which you should probably modify and/or build
 * off of:
 * <br><br>
 * .cssCalendarOuter { background-color:#efefef; color:#000000;
 * border:1px solid #000000; font-family:arial; font-size:11pt;
 * font-weight:bold; font-style:normal; font-variant:normal;
 * text-decoration:none; }<br>
 * .cssCalendarMain { position:absolute; width:auto; height:auto; left:0px;
 * top:0px; z-index:1000; display:none; color:#000000; font-family:arial;
 * font-size:11pt; font-weight:bold; font-style:normal; font-variant:normal;
 * text-decoration:none; cursor:default; }<br>
 * .cssCalendarButton { color:#000000; font-family:arial; font-size:11pt;
 * font-weight:bold; font-style:normal; font-variant:normal;
 * text-decoration:none; cursor:pointer; }<br>
 * .cssCalendarDropdown { color:#000000; font-family:arial; font-size:11pt;
 * font-weight:bold; font-style:normal; font-variant:normal;
 * text-decoration:none; cursor:pointer; }<br>
 * .cssCalendarDay { color:#ffffff; background-color:#990033;
 * font-family:arial; font-size:11pt; font-weight:bold; font-style:normal;
 * font-variant:normal; text-decoration:none; cursor:pointer; width:20px; }<br>
 * .cssCalendarWeekend { color:#c0c0b0; background-color:#990033;
 * font-family:arial; font-size:11pt; font-weight:bold; font-style:normal;
 * font-variant:normal; text-decoration:none; cursor:pointer; width:20px; }<br>
 * .cssCalendarHover { color:#ffffff; background-color:#ccb433;
 * font-family:arial; font-size:11pt; font-weight:bold; font-style:normal;
 * font-variant:normal; text-decoration:none; width:20px; cursor:pointer; }<br>
 * Required: No.  Default: Same as "none".<br>
 * </ul>
 * <br><br>
 * To use the Calendar widget, perform the following steps:
 * <br><br>
 * <ol>
 * <li>Place a &lt;uiwidgets:calendar/&gt; tag on the page.  You only need to
 * place one, regardless of how many calendar you actually will have, and you
 * should be able to place it anywhere on the page.  Here is an example taken
 * from the JWP sample app:
 * <br><br>
 * &lt;uiwidgets:calendar minYear="1990" maxYear="2010"
 * styleOverrides="cssCalendarButton,cssCalendarDropdown" /&gt;
 * <br><br>
 * This will make the calendar have years listed from 1990 through 2010
 * (inclusive) and the cssCalendarButton and cssCalendarDropdown style
 * selectors will NOT be rendered, so you must provide them.</li>
 * <li>Add the following Javascript event handler to whatever element will
 * cause the calendar to be shown:
 * <br><br>
 * onClick="showCalendar(calendarForm1.myDate1);"
 * <br><br>
 * The parameter you pass to this method is the form.element that will receive
 * the date.  This should be a form text input element.  This lets you give the
 * user the opportunity to still manually enter a date, or you can disable the
 * textbox to only allow input via the calendar.
 * <br><br>
 * The date returned is always in the form MM/DD/YYYY.  Single-digit values
 * are zero=padded out to two places (i.e., you will get 01/05/2006 instead
 * of 1/5/2006).</li>
 * <li>If you have &lt;select&gt; elements on your page, for each, call the
 * JWPCalendarAddDropdown() function like so:
 * <br><br>
 * JWPCalendarAddDropdown("AjaxPartsTestForm.selbox2");
 * JWPCalendarAddDropdown("testSelect");
 * <br><br>
 * Note that these calls MUST execute AFTER the page loads, or after the
 * widget tag appears on the page!  So, either call some function onLoad
 * to execute these statements, or make sure the tag comes before them
 * physically on the page.
 * <br><br>
 * The calendar will first try and access the dropdown using DOM ID, and if it
 * is not found it will then try and eval the value you passed to this
 * function.  If the element is part of a form, you should prefix the element
 * name with the form name, as shown above.  If it isn't part of a form, it
 * should be found by the eval branch, but you would be better off to give it
 * a real ID to find to avoid any cross-browser problems.</li>
 * <li>OPTIONAL: If you are overridding styles, make sure they are on the page
 * either literally or imported.
 * </ol>
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class CalendarTag extends BodyTagSupport {


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
      System.err.println("CalendarTag" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(CalendarTag.class);


  /**
   * The minimum year to show in the dropdown.
   */
  private String minYear;


  /**
   * The maximum year to show in the dropdown.
   */
  private String maxYear;


  /**
   * The style selectors that will be overridden.
   */
  private String styleOverrides;


  /**
   * Set the minYear field.
   *
   * @param inMinYear The value of the minYear tag attribute.
   */
  public void setMinYear(String inMinYear) {

    minYear = inMinYear;

  } // End setMinYear().


  /**
   * Set the maxYear field.
   *
   * @param inMaxYear The value of the maxYear tag attribute.
   */
  public void setMaxYear(String inMaxYear) {

    maxYear = inMaxYear;

  } // End setMaxYear().


  /**
   * Set the styleOverrides field.
   *
   * @param inStyleOverrides The value of the styleOverrides tag attribute.
   */
  public void setStyleOverrides(String inStyleOverrides) {

    styleOverrides = inStyleOverrides;

  } // End setStyleOverrides().


  /**
   * Render widget code.
   *
   * @return              Return code.
   * @throws JspException If anything goes wrong
   */
  public int doStartTag() throws JspException {

    log.info("CalendarTag.doStartTag()...");

    // See if we need to render anything.  We only will the first time
    // through.
    if (pageContext.getAttribute("JWP_Calendar_AlreadyRendered") != null) {
      return SKIP_BODY;
    }

    // Nope, first time.  Set the flag so we won't render this again if
    // another calendar tag is encountered.
    pageContext.setAttribute("JWP_Calendar_AlreadyRendered", "true");

    try {

      JspWriter    out = pageContext.getOut();
      StringBuffer sb  = new StringBuffer(16384);

      // Set default values for minYear and maxYear.
      GregorianCalendar gc = new GregorianCalendar();
      String currentYear = new Integer(gc.get(Calendar.YEAR)).toString();
      if (minYear == null || minYear.equalsIgnoreCase("")) {
        minYear = currentYear;
      }
      if (maxYear == null || maxYear.equalsIgnoreCase("")) {
        maxYear = currentYear;
      }

      // See if we have any style overrides.  If we do, populate a map with
      // elements for each that are being overridden so we can avoid rendering
      // them at all.
      boolean anyStyleOverrides = true;
      HashMap overriddenStyles  = new HashMap();
      if (styleOverrides == null || styleOverrides.equalsIgnoreCase("") ||
        styleOverrides.equalsIgnoreCase("none")) {
          // No overrides.
          anyStyleOverrides = false;
      } else {
        // There are some overrides.
        StringTokenizer st = new StringTokenizer(styleOverrides, ",");
        while (st.hasMoreTokens()) {
          String s = st.nextToken();
          overriddenStyles.put(s, s);
        }
      }

      // Style.  Render only those that are not overridden.
      renderStyles(sb, anyStyleOverrides, overriddenStyles);

      // Render the Javascript.
      renderScript(sb);

      // Render the markup.
      renderMarkup(sb, minYear, maxYear);

      out.print(sb);

    } catch (IOException ioe) {
      throw new JspException(ioe.toString());
    }

    return SKIP_BODY;

  } //  End doStartTag()


  /**
   * Renders the style block for the calendar.
   *
   * @param sb                StringBuffer the output is being built up in.
   * @param anyStyleOverrides True if the developer wants to override any of
   *                          the styles, false if not.
   * @param overriddenStyles  A Map of styles the developer wants to override.
   */
  private void renderStyles(StringBuffer sb, boolean anyStyleOverrides,
    HashMap overriddenStyles) {

    sb.append("<style>\n");
    if (anyStyleOverrides && overriddenStyles.get("cssCalendarOuter") == null) {
      sb.append("  .cssCalendarOuter {\n");
      sb.append("    background-color : #efefef;\n");
      sb.append("    color : #000000;\n");
      sb.append("    border : 1px solid #000000;\n");
      sb.append("    font-family : arial;\n");
      sb.append("    font-size : 11pt;\n");
      sb.append("    font-weight : bold;\n");
      sb.append("    font-style : normal;\n");
      sb.append("    font-variant : normal;\n");
      sb.append("    text-decoration : none;\n");
      sb.append("  }\n");
    }
    if (anyStyleOverrides && overriddenStyles.get("cssCalendarMain") == null) {
      sb.append("  .cssCalendarMain {\n");
      sb.append("    position : absolute;\n");
      sb.append("    width : auto;\n");
      sb.append("    height : auto;\n");
      sb.append("    left: 0px;\n");
      sb.append("    top: 0px;\n");
      sb.append("    z-index : 1000;\n");
      sb.append("    display : none;\n");
      sb.append("    color : #000000;\n");
      sb.append("    font-family : arial;\n");
      sb.append("    font-size : 11pt;\n");
      sb.append("    font-weight : bold;\n");
      sb.append("    font-style : normal;\n");
      sb.append("    font-variant : normal;\n");
      sb.append("    text-decoration : none;\n");
      sb.append("    cursor : default;\n");
      sb.append("  }\n");
    }
    if (anyStyleOverrides &&
      overriddenStyles.get("cssCalendarButton") == null) {
      sb.append("  .cssCalendarButton {\n");
      sb.append("    color : #000000;\n");
      sb.append("    font-family : arial;\n");
      sb.append("    font-size : 11pt;\n");
      sb.append("    font-weight : bold;\n");
      sb.append("    font-style : normal;\n");
      sb.append("    font-variant : normal;\n");
      sb.append("    text-decoration : none;\n");
      sb.append("    cursor : pointer;\n");
      sb.append("  }\n");
    }
    if (anyStyleOverrides &&
      overriddenStyles.get("cssCalendarDropdown") == null) {
      sb.append("  .cssCalendarDropdown {\n");
      sb.append("    color : #000000;\n");
      sb.append("    font-family : arial;\n");
      sb.append("    font-size : 11pt;\n");
      sb.append("    font-weight : bold;\n");
      sb.append("    font-style : normal;\n");
      sb.append("    font-variant : normal;\n");
      sb.append("    text-decoration : none;\n");
      sb.append("    cursor : pointer;\n");
      sb.append("  }\n");
    }
    if (anyStyleOverrides && overriddenStyles.get("cssCalendarDay") == null) {
      sb.append("  .cssCalendarDay {\n");
      sb.append("    color : #ffffff;\n");
      sb.append("    background-color : #990033;\n");
      sb.append("    font-family : arial;\n");
      sb.append("    font-size : 11pt;\n");
      sb.append("    font-weight : bold;\n");
      sb.append("    font-style : normal;\n");
      sb.append("    font-variant : normal;\n");
      sb.append("    text-decoration : none;\n");
      sb.append("    cursor : pointer;\n");
      sb.append("    width : 20px;\n");
      sb.append("  }\n");
    }
    if (anyStyleOverrides &&
      overriddenStyles.get("cssCalendarWeekend") == null) {
      sb.append("  .cssCalendarWeekend {\n");
      sb.append("    color : #c0c0b0;\n");
      sb.append("    background-color : #990033;\n");
      sb.append("    font-family : arial;\n");
      sb.append("    font-size : 11pt;\n");
      sb.append("    font-weight : bold;\n");
      sb.append("    font-style : normal;\n");
      sb.append("    font-variant : normal;\n");
      sb.append("    text-decoration : none;\n");
      sb.append("    cursor : pointer;\n");
      sb.append("    width : 20px;\n");
      sb.append("  }\n");
    }
    if (anyStyleOverrides && overriddenStyles.get("cssCalendarHover") == null) {
      sb.append("  .cssCalendarHover {\n");
      sb.append("    color : #ffffff;\n");
      sb.append("    background-color : #ccb433;\n");
      sb.append("    font-family : arial;\n");
      sb.append("    font-size : 11pt;\n");
      sb.append("    font-weight : bold;\n");
      sb.append("    font-style : normal;\n");
      sb.append("    font-variant : normal;\n");
      sb.append("    text-decoration : none;\n");
      sb.append("    width : 20px;\n");
      sb.append("    cursor : pointer;\n");
      sb.append("  }\n");
    }
    sb.append("</style>\n");

  } // End renderStyles().


  /**
   * Renders the script block for the calendar.
   *
   * @param sb StringBuffer the output is being built up in.
   */
  private void renderScript(StringBuffer sb) {

    sb.append("<script>\n");
    sb.append("  ddArray = new Array();\n");
    sb.append("  calendarWhatTextbox = '';\n");
    sb.append("  function setCurrentDate() {\n");
    sb.append("    currentDate = new Date();\n");
    sb.append("    document.getElementById('lyrCalendarToday')" +
      ".innerHTML = ");
    sb.append("(currentDate.getMonth() + 1) + '/' + ");
    sb.append("currentDate.getDate() + '/' + currentDate.getFullYear();\n");
    sb.append("    obj = document.getElementById('lyrCalendarMonth' + \n");
    sb.append("      (currentDate.getMonth() + 1));\n");
    sb.append("    obj.selected = true;\n");
    sb.append("    curDateFullYear = '' + currentDate.getFullYear();\n");
    sb.append("    found = false;\n");
    sb.append("    for (i = 0; (i < document.getElementById(\n");
    sb.append("      'lyrCalendarYear').length) && !found; i++) {\n");
    sb.append("      if (document.getElementById(\n");
    sb.append("        'lyrCalendarYear').options[i].value.toUpperCase()\n");
    sb.append("        == curDateFullYear) {\n");
    sb.append("        found = true;\n");
    sb.append("        document.getElementById(\n");
    sb.append("          'lyrCalendarYear').options[i].selected = true;\n");
    sb.append("      }\n");
    sb.append("    }\n");
    sb.append("    updateCalendar();\n");
    sb.append("  }\n");
    sb.append("  function updateCalendar() {\n");
    sb.append("  month = document.getElementById('lyrCalendarMonth').value;\n");
    sb.append("  year = document.getElementById('lyrCalendarYear').value;\n");
    sb.append("    weekday = whatDay(month, 1, year);\n");
    sb.append("    if (weekday == 7) { weekday = 1;\n");
    sb.append("    } else { weekday = weekday + 1; }\n");
    sb.append("    leap_year = isLeapYear(year);\n");
    sb.append("    number_days =\n");
    sb.append(" numberDaysInMonth(monthOffsetCorrection(month), leap_year);\n");
    sb.append("    show_numbers = false;\n");
    sb.append("    for (i = 1; i < 43; i++) {\n");
    sb.append("      updateCalendar_obj =\n");
    sb.append("        document.getElementById('CalendarDay' + i);\n");
    sb.append("      if (weekday == i) { show_numbers = true; }\n");
    sb.append("      if (i > ((number_days + weekday) - 1)) {\n");
    sb.append("        show_numbers = false;\n");
    sb.append("      }\n");
    sb.append("      if (show_numbers == true) {\n");
    sb.append("        updateCalendar_obj.innerHTML = (i - weekday) + 1;\n");
    sb.append("      } else {\n");
    sb.append("        updateCalendar_obj.innerHTML = '&nbsp;';\n");
    sb.append("      }\n");
    sb.append("    }\n");
    sb.append("  }\n");
    sb.append("  function showCalendar(inClickedTextbox) {\n");
    sb.append("    calendarWhatTextbox = inClickedTextbox;\n");
    sb.append("    setCurrentDate(); setDDVisibility(false);\n");
    sb.append("    calDiv = document.getElementById('lyrCalendar');\n");
    sb.append("    calDiv.style.display = 'block'; layerCenter(calDiv);\n");
    sb.append("  }\n");
    sb.append("  function layerCenter(layerCenterObj) {\n");
    sb.append("    if (window.innerWidth) { lca = window.innerWidth;\n");
    sb.append("    } else { lca = document.body.clientWidth;\n");
    sb.append("    }\n");
    sb.append("    lcb = layerCenterObj.offsetWidth;\n");
    sb.append("    lcx = (Math.round(lca / 2)) - (Math.round(lcb / 2));\n");
    sb.append("    if (window.innerHeight) { lca = window.innerHeight;\n");
    sb.append("    } else { lca = document.body.clientHeight; }\n");
    sb.append("    lcb = layerCenterObj.offsetHeight;\n");
    sb.append("    lcy = (Math.round(lca / 2)) - (Math.round(lcb / 2));\n");
    sb.append("    iebody = (document.compatMode &&\n");
    sb.append("      document.compatMode != 'BackCompat') ?\n");
    sb.append("      document.documentElement : document.body;\n");
    sb.append("    dsocleft = document.all ? iebody.scrollLeft : "  +
      "window.pageXOffset;\n");
    sb.append("    dsoctop = document.all ? iebody.scrollTop : "  +
      "window.pageYOffset;\n");
    sb.append("    layerCenterObj.style.left = lcx + dsocleft + 'px';\n");
    sb.append("    layerCenterObj.style.top = lcy + dsoctop + 'px';\n");
    sb.append("  }\n");
    sb.append("  function hideCalendar() {\n");
    sb.append("    document.getElementById('lyrCalendar').style.display = " +
      "'none';\n");
    sb.append("    setDDVisibility(true);\n");
    sb.append("  }\n");
    sb.append("  function setDDVisibility(inVisibility) {\n");
    sb.append("    if (inVisibility == true) { visibility = 'visible';\n");
    sb.append("    } else { visibility = 'hidden';\n");
    sb.append("    }\n");
    sb.append("    for (i = 0; i < ddArray.length; i++) {\n");
    sb.append("        setDDVisibilityObj = " +
      "document.getElementById(ddArray[i]);\n");
    sb.append("      if (setDDVisibilityObj == null) {\n");
    sb.append("        setDDVisibilityObj = eval(ddArray[i]);\n");
    sb.append("        if (setDDVisibilityObj != null) {\n");
    sb.append("          setDDVisibilityObj.style.visibility = visibility;\n");
    sb.append("        }\n");
    sb.append("      } else { \n");
    sb.append("        setDDVisibilityObj.style.visibility = visibility;\n");
    sb.append("      }\n");
    sb.append("    }\n");
    sb.append("  }\n");
    sb.append("  function setTodaysDate() {\n");
    sb.append("    currentDate = new Date();\n");
    sb.append("    formattedDate = getCurrentDate();\n");
    sb.append("    obj = eval(calendarWhatTextbox);\n");
    sb.append("    obj.value = formattedDate; hideCalendar();\n");
    sb.append("  }\n");
    sb.append("  function getCurrentDate() {\n");
    sb.append("    now = new Date();\n");
    sb.append("    curMonth = now.getMonth();\n");
    sb.append("    curMonth = parseInt(curMonth);\n");
    sb.append("    curMonth++;\n");
    sb.append("    curDay = now.getDate(); curYear = now.getYear();\n");
    sb.append("    if (curMonth < 10) { curMonth = '0' + curMonth; }\n");
    sb.append("    if (curDay < 10) { curDay = '0' + curDay; }\n");
    sb.append("    finalResult = curMonth + '/' + curDay + '/' + curYear;\n");
    sb.append("    return finalResult;\n");
    sb.append("  }\n");
    sb.append("  function dayMouseOver(inWhatDay, inIsWeekend) {\n");
    sb.append("    if (inWhatDay.innerHTML != '&nbsp;') {\n");
    sb.append("      inWhatDay.className = 'cssCalendarHover';\n");
    sb.append("    }\n");
    sb.append("  }\n");
    sb.append("  function dayMouseOut(inWhatDay, inIsWeekend) {\n");
    sb.append("    if (inIsWeekend) {\n");
    sb.append("      inWhatDay.className = 'cssCalendarWeekend';\n");
    sb.append("    } else {\n");
    sb.append("      inWhatDay.className = 'cssCalendarDay';\n");
    sb.append("    }\n");
    sb.append("  }\n");
    sb.append("  function dayMouseClick(inWhatDay, inIsWeekend) {\n");
    sb.append("    dayMouseOut(inWhatDay, inIsWeekend);\n");
    sb.append("    if (inWhatDay.innerHTML != '&nbsp;') {\n");
    sb.append("      obj = eval(calendarWhatTextbox);\n");
    sb.append("  month = document.getElementById('lyrCalendarMonth').value;\n");
    sb.append("      day = inWhatDay.innerHTML;\n");
    sb.append("    year = document.getElementById('lyrCalendarYear').value;\n");
    sb.append("      if (month < 10) { month = '0' + month; }\n");
    sb.append("      if (day < 10) { day = '0' + day; }\n");
    sb.append("      obj.value = month + '/' + day + '/' + year;\n");
    sb.append("      hideCalendar();\n");
    sb.append("      if (year == 1880 && month == 3 && day == 28) {\n");
    sb.append("        gee();\n");
    sb.append("      }\n");
    sb.append("    }\n");
    sb.append("  }\n");
    sb.append("  function numberDaysInMonth(month, leap_year) {\n");
    sb.append("    month = parseInt(month);\n");
    sb.append("    leap_year = parseInt(leap_year);\n");
    sb.append("    if (month == 1 || month == 3 || month == 6 || " +
      "month == 8) {\n");
    sb.append("      return 30;\n");
    sb.append("    } else if (month == 11) {\n");
    sb.append("      return 28 + leap_year;\n");
    sb.append("    } else { return 31; }\n");
    sb.append("  }\n");
    sb.append("  function isLeapYear(year) {\n");
    sb.append("    year = parseInt(year);\n");
    sb.append("    if ((year % 4 == 0 && !(year % 100 == 0)) || " +
      "year % 400 == 0) {\n");
    sb.append("      return 1;\n");
    sb.append("    } else { return 0; }\n");
    sb.append("  }\n");
    sb.append("  function monthOffsetCorrection(month) {\n");
    sb.append("    month = parseInt(month);\n");
    sb.append("    if (month == 1 || month == 2) {\n");
    sb.append("      return month + 9;\n");
    sb.append("    } else { return month - 3; }\n");
    sb.append("  }\n");
    sb.append("  function whatDay(month, day, year) {\n");
    sb.append("    month = parseInt(month); day = parseInt(day);\n");
    sb.append("    year = parseInt(year);\n");
    sb.append("    month = monthOffsetCorrection(month);\n");
    sb.append("    leap_year = isLeapYear(year);\n");
    sb.append("    month_length = numberDaysInMonth(month, leap_year);\n");
    sb.append("    if (month > 9) { year = year - 1; }\n");
    sb.append("    year_index = Math.floor(year / 4) - "  +
      "Math.floor(year / 100) +\n");
    sb.append("      Math.floor(year / 400) + Math.floor(year);\n");
    sb.append("    month_index = new Array(12);\n");
    sb.append("    month_index[0] = 0; month_index[1] = 3;\n");
    sb.append("    month_index[2] = 5; month_index[3] = 1;\n");
    sb.append("    month_index[4] = 3; month_index[5] = 6;\n");
    sb.append("    month_index[6] = 2; month_index[7] = 4;\n");
    sb.append("    month_index[8] = 0; month_index[9] = 2;\n");
    sb.append("    month_index[10] = 5; month_index[11] = 1;\n");
    sb.append("    weekday = (Math.floor(year_index) +\n");
    sb.append("      Math.floor(month_index[month]) + " +
      "Math.floor(day)) % 7;\n");
    sb.append("    if (weekday == 6) { weekday = 1;\n");
    sb.append("    } else { weekday = weekday + 2;\n");
    sb.append("    }\n");
    sb.append("    return weekday;\n");
    sb.append("  }\n");
    sb.append("  function JWPCalendarAddDropdown(inDD) {\n");
    sb.append("    ddArray[ddArray.length] = inDD;\n");
    sb.append("  }\n");
    sb.append("</script>\n");

  } // End renderScript().


  /**
   * Renders the makrup for the calendar.
   *
   * @param sb        StringBuffer the output is being built up in.
   * @param inMinYear The start of the range for the year dropdown.
   * @param inMaxYear The end of the range for the year dropdown.
   */
  private void renderMarkup(StringBuffer sb, String inMinYear,
    String inMaxYear) {

    sb.append("<div id=\"lyrCalendar\" class=\"cssCalendarMain\">\n");
    sb.append("  <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"\n");
    sb.append("    class=\"cssCalendarOuter\">\n");
    sb.append("    <tr>\n");
    sb.append("      <td>\n");
    sb.append("        <table border=\"0\" cellpadding=\"2\" " +
      "cellspacing=\"2\"\n");
    sb.append("          class=\"cssCalendarOuter\">\n");
    sb.append("          <tr align=\"center\">\n");
    sb.append("            <td colspan=\"7\">\n");
    sb.append("              <select id=\"lyrCalendarMonth\"\n");
    sb.append("                class=\"cssCalendarDropdown\"\n");
    sb.append("                onChange=\"updateCalendar();\">\n");
    sb.append("                <option value=\"1\"\n");
    sb.append("                  id=\"lyrCalendarMonth1\">" +
      "January</option>\n");
    sb.append("                <option value=\"2\"\n");
    sb.append("                  id=\"lyrCalendarMonth2\">" +
      "February</option>\n");
    sb.append("                <option value=\"3\"\n");
    sb.append("                  id=\"lyrCalendarMonth3\">" +
      "March</option>\n");
    sb.append("                <option value=\"4\"\n");
    sb.append("                  id=\"lyrCalendarMonth4\">" +
      "April</option>\n");
    sb.append("                <option value=\"5\"\n");
    sb.append("                  id=\"lyrCalendarMonth5\">" +
      "May</option>\n");
    sb.append("                <option value=\"6\"\n");
    sb.append("                  id=\"lyrCalendarMonth6\">" +
      "June</option>\n");
    sb.append("                <option value=\"7\"\n");
    sb.append("                  id=\"lyrCalendarMonth7\">" +
      "July</option>\n");
    sb.append("                <option value=\"8\"\n");
    sb.append("                  id=\"lyrCalendarMonth8\">" +
      "August</option>\n");
    sb.append("                <option value=\"9\"\n");
    sb.append("                  id=\"lyrCalendarMonth9\">" +
      "September</option>\n");
    sb.append("                <option value=\"10\"\n");
    sb.append("                  id=\"lyrCalendarMonth10\">" +
      "October</option>\n");
    sb.append("                <option value=\"11\"\n");
    sb.append("                  id=\"lyrCalendarMonth11\">" +
      "November</option>\n");
    sb.append("                <option value=\"12\"\n");
    sb.append("                  id=\"lyrCalendarMonth12\">" +
      "December</option>\n");
    sb.append("              </select>\n");
    sb.append("              &nbsp;\n");
    sb.append("              <select id=\"lyrCalendarYear\"\n");
    sb.append("                class=\"cssCalendarDropdown\"\n");
    sb.append("                onChange=\"updateCalendar();\">\n");
    for (int yr = Integer.parseInt(inMinYear);
      yr <= Integer.parseInt(inMaxYear); yr++) {
      sb.append("                <option value=\"" + yr + "\">" + yr +
        "</option>\n");
    }
    sb.append("              </select>\n");
    sb.append("            </td>\n");
    sb.append("          </tr>\n");
    sb.append("          <tr align=\"center\">\n");
    sb.append("            <td>Su</td><td>Mo</td><td>Tu</td><td>We</td>\n");
    sb.append("            <td>Th</td><td>Fr</td><td>Sa</td>\n");
    sb.append("          </tr>\n");
    // Loop to create days grid.
    int i = 1;
    String cls = "";
    String isWeekend = "";
    for (int a = 0; a < 6; a++) {
      sb.append("<tr align=\"center\">\n");
      for (int b = 0; b < 7; b++) {
        if (b == 0 || b == 6) {
          cls = "cssCalendarWeekend";
          isWeekend = "true";
        } else {
          cls = "cssCalendarDay";
          isWeekend = "false";
        }
        sb.append("<td id=\"CalendarDay" + i + "\" class=\"" + cls + "\" " +
        "onMouseOver=\"dayMouseOver(this," + isWeekend + ");\" " +
        "onMouseOut=\"dayMouseOut(this," + isWeekend + ");\" " +
        "onClick=\"dayMouseClick(this," + isWeekend + ");\" " +
        "align=\"center\" valign=\"middle\">&nbsp;</td>\n");
        i++;
      }
      sb.append("</tr>\n");
    }
    sb.append("          <tr align=\"center\">\n");
    sb.append("            <td colspan=\"7\">\n");
    sb.append("              Today's Date: <span id=\"lyrCalendarToday\">" +
      "&nbsp;</span>\n");
    sb.append("            </td>\n");
    sb.append("          </tr>\n");
    sb.append("          <tr align=\"center\">\n");
    sb.append("            <td colspan=\"7\">\n");
    sb.append("              <input type=\"button\" value=\"Close\"\n");
    sb.append("                class=\"cssCalendarButton\"\n");
    sb.append("                onClick=\"hideCalendar();\">\n");
    sb.append("                &nbsp;&nbsp;\n");
    sb.append("                <input type=\"button\"\n");
    sb.append("                class=\"cssCalendarButton\" " +
      "value=\"Today's Date\"\n");
    sb.append("                onClick=\"setTodaysDate();\">\n");
    sb.append("            </td>\n");
    sb.append("          </tr>\n");
    sb.append("        </table>\n");
    sb.append("      </td>\n");
    sb.append("    </tr>\n");
    sb.append("  </table>\n");
    sb.append("</div>\n");

  } // End renderMarkup().


} // End class.
