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


package javawebparts.filter;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This filter allows for defining a time window for each day of the week
 * during which the application is available.  It allows for including or
 * excluding paths from filter functionality.  It also also allows for
 * bypassing filter functionality based on an application-defined criteria.
 * It allows for forwarding or redirecting to a defined page when the
 * application is not available.
 * <br><br>
 * Init parameters are:
 * <br>
 * <ul>
 * <li><b>pathSpec</b> - Either "include" or "exclude".  This determines whether
 * the list of paths in the pathList parameter is a list of paths to include in
 * filter functionality, or a list of paths to exclude.  Required: No.
 * Default: None.</li>
 * <br><br>
 * <li><b>pathList</b> - This is a comma-separated list of paths, which can use
 * asterisk for wildcard support, that denotes either paths to include or
 * exclude from the functioning of this filter (depending on what pathSpec
 * is set to).  The paths ARE case-senitive!  There is no limit to how many
 * items can be specified, although for performance reasons a developer will
 * probably want to specify as few as possible to get the job done (each
 * requested path is matched via regex).  Note also that you are of course
 * still required to specify a path for the filter itself as per the servlet
 * spec.  This parameter however, together with pathSpec, gives you more control
 * and flexibility than that setting alone.  Required: No.  Default: None.
 * <br><br>
 * General note on pathSpec and pathList:  If pathSpec is not specified but
 * pathList IS, then 'exclude' is assumed for pathSpec.  If pathSpec is
 * specified by pathList IS NOT, then the filter WILL NEVER EXECUTE (this is
 * technically a misconfiguration).  If NEITHER is defined then the generic
 * filter mapping will be in effect only.</li>
 * <br><br>
 * <li><b>bypassCheckClass</b> - This is a class you can optionally provide that
 * must implement the AppAvailabilityBypassCheck interface, to allow for the
 * capability to override the function of this filter.  For instance, you may
 * want a particular user to always be allowed in.  By supplying this class,
 * you can do that in whatever fashion is appropriate for your application
 * (i.e., maybe you need to grab a particular parameter from request and look
 * up the user in a database).  Required: Yes.  Default: None.</li>
 * <br><br>
 * <li><b>redirectTo</b> - The URL to redirect to if the app is not available.
 * You can either redirect to this URL or forward to it, but not both.
 * Required: Yes (either this or forwardTo).  Default: None.</li>
 * <br><br>
 * <li><b>forwardTo</b> - The URL to forward to if the app is not available.
 * You can either redirect to this URL or forward to it, but not both.
 * Required: Yes (either this or redirectTo).  Default: None.</li>
 * <br><br>
 * <li><b>monday</b> - The time range within which the application is
 * available on a Monday.  Required: Yes.  Default: None.</li>
 * <br><br>
 * <li><b>tuesday</b> - The time range within which the application is
 * available on a Tuesday.  Required: Yes.  Default: None.</li>
 * <br><br>
 * <li><b>wednesday</b> - The time range within which the application is
 * available on a Wednesday.  Required: Yes.  Default: None.</li>
 * <br><br>
 * <li><b>thursday</b> - The time range within which the application is
 * available on a Thursday.  Required: Yes.  Default: None.</li>
 * <br><br>
 * <li><b>friday</b> - The time range within which the application is
 * available on a Friday.  Required: Yes.  Default: None.</li>
 * <br><br>
 * <li><b>saturday</b> - The time range within which the application is
 * available on a Saturday.  Required: Yes.  Default: None.</li>
 * <br><br>
 * <li><b>sunday</b> - The time range within which the application is
 * available on a Sunday.  Required: Yes.  Default: None.</li>
 * <br><br>
 * General notes on the time ranges... the times are specified in 24-hour
 * format and the value of the parameter for each day is in the form 8888-9999,
 * where 8888 is the start time and 999 is the end time.  Start and end times
 * CAN span days, i.e., you can define that the application is available
 * from 11pm on Friday to 6am on Saturday (by setting friday to 2300-0600).
 * </ul>
 * <br>
 * Example configuration in web.xml:
 * <br><br>
 * &nbsp;&nbsp;&lt;filter&gt;<br>
 * &nbsp;&nbsp;&lt;filter-name&gt;AppAvailabilityFilter&lt;/filter-name&gt;<br>
 * &nbsp;&nbsp;&lt;filter-class&gt;javawebparts.filter.
 * AppAvailabilityFilter&lt;/filter-class&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;pathSpec&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;include&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;pathList&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;*&zwj;/AAFTestTarget.jsp
 * &lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;monday&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;0600-2359&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;tuesday&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;0600-2359&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;wednesday&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;0600-2359&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;thursday&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;0600-2359&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;friday&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;0600-2359&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;saturday&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;0600-2359&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;sunday&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;0600-2359&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;redirectTo&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;AAFReject.jsp&lt;
 * /param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;bypassCheckClass&lt;
 * /param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;javawebparts.sampleapp.
 * AAFBypassCheck&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &lt;/filter&gt;
 * <br><br>
 * &lt;filter-mapping&gt;<br>
 * &nbsp;&nbsp;&lt;filter-name&gt;AppAvailabilityFilter&lt;/filter-name&gt;<br>
 * &nbsp;&nbsp;&lt;url-pattern&gt;/*&lt;/url-pattern&gt;<br>
 * &lt;/filter-mapping&gt;
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class AppAvailabilityFilter implements Filter {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javax.servlet.Filter");
      Class.forName("javax.servlet.FilterChain");
      Class.forName("javax.servlet.FilterConfig");
      Class.forName("javax.servlet.ServletException");
      Class.forName("javax.servlet.ServletRequest");
      Class.forName("javax.servlet.ServletResponse");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("AppAvailabilityFilter" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(AppAvailabilityFilter.class);


  /**
   * Flag to retrieve start time for a date.
   */
  private static final int TIME_START = 1;


  /**
   * Flag to retrieve end time for a date.
   */
  private static final int TIME_END = 2;


  /**
   * Whether pathList includes or excludes.
   */
  private String pathSpec;


  /**
   * List of paths for filter functionality determination.
   */
  private ArrayList pathList = new ArrayList();


  /**
   * A path to redirect to when access is denied.
   */
  private String redirectTo;


  /**
   * A path to forward to when access is denied.
   */
  private String forwardTo;


  /**
   * A class that will be used to allow for bypassing this filter.
   */
  private String bypassCheckClass;


  /**
   * The start of the time window the app is available during on Monday.
   */
  private int mondayStart;


  /**
   * The end of the time window the app is available during on Monday.
   */
  private int mondayEnd;


  /**
   * The start of the time window the app is available during on Tuesday.
   */
  private int tuesdayStart;


  /**
   * The end of the time window the app is available during on Tuesday.
   */
  private int tuesdayEnd;


  /**
   * The start of the time window the app is available during on Wednesday.
   */
  private int wednesdayStart;


  /**
   * The end of the time window the app is available during on Wednesday.
   */
  private int wednesdayEnd;


  /**
   * The start of the time window the app is available during on Thursday.
   */
  private int thursdayStart;


  /**
   * The end of the time window the app is available during on Thursday.
   */
  private int thursdayEnd;


  /**
   * The start of the time window the app is available during on Friday.
   */
  private int fridayStart;


  /**
   * The end of the time window the app is available during on Friday.
   */
  private int fridayEnd;


  /**
   * The start of the time window the app is available during on Saturday.
   */
  private int saturdayStart;


  /**
   * The end of the time window the app is available during on Saturday.
   */
  private int saturdayEnd;


  /**
   * The start of the time window the app is available during on Sunday.
   */
  private int sundayStart;


  /**
   * The end of the time window the app is available during on Sunday.
   */
  private int sundayEnd;


  /**
   * Destroy.
   */
  public void destroy() {

  } // End destroy.


  /**
   * Initialize this filter.
   *
   * @param  filterConfig     The configuration information for this filter.
   * @throws ServletException ServletException.
   */
  public void init(FilterConfig filterConfig) throws ServletException {

    log.info("init() started");

    // Do pathSpec and pathList init work.
    pathSpec = FilterHelpers.initPathSpec(getClass().getName(), filterConfig);
    pathList = FilterHelpers.initPathList(getClass().getName(), filterConfig);

    // Get the bypassCheckClass init parameter.
    bypassCheckClass = filterConfig.getInitParameter("bypassCheckClass");
    log.info("bypassCheckClass = " + bypassCheckClass);

    // Get the redirectTo and forwardTo init parameters and validate them.
    redirectTo = FilterHelpers.initRedirectTo(filterConfig);
    forwardTo  = FilterHelpers.initForwardTo(filterConfig);
    FilterHelpers.checkRedirectForwardTo(getClass().getName(),
                                         redirectTo, forwardTo);

    // Get start/end times for all the days of the week.
    // Get the pathSpec init parameter.
    mondayStart    = getDayStartEnd(filterConfig, "monday",    TIME_START);
    mondayEnd      = getDayStartEnd(filterConfig, "monday",    TIME_END);
    tuesdayStart   = getDayStartEnd(filterConfig, "tuesday",   TIME_START);
    tuesdayEnd     = getDayStartEnd(filterConfig, "tuesday",   TIME_END);
    wednesdayStart = getDayStartEnd(filterConfig, "wednesday", TIME_START);
    wednesdayEnd   = getDayStartEnd(filterConfig, "wednesday", TIME_END);
    thursdayStart  = getDayStartEnd(filterConfig, "thursday",  TIME_START);
    thursdayEnd    = getDayStartEnd(filterConfig, "thursday",  TIME_END);
    fridayStart    = getDayStartEnd(filterConfig, "friday",    TIME_START);
    fridayEnd      = getDayStartEnd(filterConfig, "friday",    TIME_END);
    saturdayStart  = getDayStartEnd(filterConfig, "saturday",  TIME_START);
    saturdayEnd    = getDayStartEnd(filterConfig, "saturday",  TIME_END);
    sundayStart    = getDayStartEnd(filterConfig, "sunday",    TIME_START);
    sundayEnd      = getDayStartEnd(filterConfig, "sunday",    TIME_END);

    log.info("init() completed");

  } // End init().


  /**
   * Retrieves the start or stop time, as specified, from a time range in
   * the form 8888-9999 where 8888 is the start time in 24-hour time and
   * 9999 is the end time in 24-hour time.
   *
   * @param  filterConfig     The configuration information for this filter.
   * @param  day              What day of the week to retrieve.
   * @param  whatPart         Which part of the range to return (start or end).
   * @return                  The start or stop time, as specified, as an
   *                          int (24-hour form).
   * @throws ServletException If any problems occur, this gets thrown.
   */
  private int getDayStartEnd(FilterConfig filterConfig, String day,
                             int whatPart) throws ServletException {

    String startEnd = filterConfig.getInitParameter(day);
    // Range for indicated day was not found.
    if (startEnd == null) {
      String es = getClass().getName() + " could not initialize " +
                  "because mandatory " + day + " init parameter " +
                  "was not found";
      log.error(es);
      throw new ServletException(es);
    }
    log.info("day = " + day + " = " + startEnd);
    StringTokenizer st = new StringTokenizer(startEnd, "-");
    int time = 0;
    // Get either the start of the range or end of the range, as specified.
    if (whatPart == TIME_START) {
      time = Integer.parseInt(st.nextToken());
    }
    if (whatPart == TIME_END) {
      st.nextToken();
      time = Integer.parseInt(st.nextToken());
    }
    return time;

  } // End getDayStartEnd().


  /**
   * Do filter's work.
   *
   * @param  request          The current request object.
   * @param  response         The current response object.
   * @param  filterChain      The current filter chain.
   * @throws ServletException ServletException.
   * @throws IOException      IOException.
   */
  public void doFilter(ServletRequest request, ServletResponse response,
                       FilterChain filterChain)
                       throws ServletException, IOException {

    if (FilterHelpers.filterPath(request, pathList, pathSpec)) {

      log.info("AppAvailabilityFilter firing...");

      // See if a bypass class has been configured, and if so,
      // instantiate it and call its doBypass() method.
      if (bypassCheckClass != null) {
        try {
          Class clazz = Class.forName(bypassCheckClass);
          AppAvailabilityBypassCheck bc =
            (AppAvailabilityBypassCheck)clazz.newInstance();
          if (bc.doBypass(request)) {
            filterChain.doFilter(request, response);
            return;
          }
        // No exceptions should really occur here because at this point the
        // bypassCheckClass has already been validated during init, and
        // there shouldn't be anything else that can go wrong.  Famous
        // last words!
        } catch (ClassNotFoundException cnfe) {
          log.error(cnfe);
          throw new ServletException(cnfe);
        } catch (InstantiationException ie) {
          log.error(ie);
          throw new ServletException(ie);
        } catch (IllegalAccessException iae) {
          log.error(iae);
          throw new ServletException(iae);
        }
      }

      // If we're here it means the path was eligible for checking, and
      // a bypass was not done.  So, we check to see if we are within an
      // availability window.  If not, redirect (or forward) as configured.
      if (!isAppAvailable()) {
        FilterHelpers.redirectOrForward(redirectTo, forwardTo, request,
                                        response);
        return;
      } // End if (!isAppAvailable()).

    } // End if (FilterHelpers.filterPath(path, pathList, pathSpec)).

    // We'll only end up here if the path was not eligiible for checking.
    filterChain.doFilter(request, response);


  } // End doFilter().


  /**
   * This is called to determine if the app is available.  It does this by
   * determining if the current time on the server is within the availability
   * window configured for the current day of the week.
   *
   * @return True if the app is available, false if not.
   */
  private boolean isAppAvailable() {

    // Based on the day of the week, get the appropriate range times.
    GregorianCalendar currentDateTime = new GregorianCalendar();
    int               rangeStart      = 0;
    int               rangeEnd        = 0;
    switch (currentDateTime.get(Calendar.DAY_OF_WEEK)){
      case Calendar.MONDAY:
        rangeStart = mondayStart;
        rangeEnd   = mondayEnd;
      break;
      case Calendar.TUESDAY:
        rangeStart = tuesdayStart;
        rangeEnd   = tuesdayEnd;
      break;
      case Calendar.WEDNESDAY:
        rangeStart = wednesdayStart;
        rangeEnd   = wednesdayEnd;
      break;
      case Calendar.THURSDAY:
        rangeStart = thursdayStart;
        rangeEnd   = thursdayEnd;
      break;
      case Calendar.FRIDAY:
        rangeStart = fridayStart;
        rangeEnd   = fridayEnd;
      break;
      case Calendar.SATURDAY:
        rangeStart = saturdayStart;
        rangeEnd   = saturdayEnd;
      break;
      case Calendar.SUNDAY:
        rangeStart = sundayStart;
        rangeEnd   = sundayEnd;
      break;
      default:
        rangeStart = 0000;
        rangeEnd   = 2359;
      break;
    }

    // Do the actual check, send redirect if not within range.
    int currentTime = (currentDateTime.get(Calendar.HOUR_OF_DAY) * 100) +
                       currentDateTime.get(Calendar.MINUTE);
    return isTimeInRange(rangeStart, rangeEnd, currentTime);

  } // End isAppAvailable().


  /**
   * This method checks if a given 24-hour time is within a given 24-hour
   * time. Note that this should properly handle ranges that span days, i.e.,
   * if the start time is 2000 and the end time is 800 (8pm-8am the following
   * day), this should still work fine.
   *
   * @return             True if the checked time is within the range, false
   *                     if it isn't.
   * @param  rangeStart  Beginning of range.
   * @param  rangeEnd    End of range.
   * @param  timeToCheck The time to check against the range.
   */
  private boolean isTimeInRange(int rangeStart, int rangeEnd, int timeToCheck) {

      boolean result = false;
      // Special case: if the timeToCheck is equal to either the rangeStart
      // or rangeEnd, it falls within the range.  This check needs to be
      // done to catch certain situations that arose when the range spans
      // a day.  I'm honestly not sure why it didn't work without out this,
      // the logic seemed to be sound, but whatever, putting this in took care
      // of it, and it IS a valid check anyway.  So be it!
      if (timeToCheck == rangeStart || timeToCheck == rangeEnd) {
          result = true;
      } else {
          // If the range DOES NOT span a day, do it this way...
          if (rangeEnd > rangeStart) {
              if (timeToCheck >= rangeStart && timeToCheck <= rangeEnd) {
                  result = true;
              } else {
                  result = false;
              }
          }
          // If the range DOES span a day, do it this way...
          if (rangeStart > rangeEnd) {
              if (timeToCheck >= rangeEnd && timeToCheck <= rangeStart) {
                  result = false;
              } else {
                  result = true;
              }
          }
      }
      return result;

  } // End isTimeInRange().


} // End class.
