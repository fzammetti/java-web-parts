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


package javawebparts.sampleapp;


import javawebparts.filter.AppAvailabilityBypassCheck;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletRequest;


/**
 * This class is an example of a class that is called by the
 * AppAvailabilityFilter to do a bypass check.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class AAFBypassCheck implements AppAvailabilityBypassCheck {


/**
 * This is the sole method of the interface this class must implement.
 * Simply return true if the filter should allow the request, false if not.
 *
 * @param  request The request being serviced.
 * @return         True if the request should continue, false if not.
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
  public boolean doBypass(ServletRequest request) {

    // In this example we are just checking for the presence of a request
    // parameter named "bypass" with the value "true".  In real life you might
    // want to check for a username and password parameter, validate them,
    // and then look up in a database table to see if they are allowed to
    // bypass this filter.  Or you could do something else entirely.  As
    // long as this method returns true if an override should be done and
    // false if not, it's all good!
    String bypass = (String)
      ((HttpServletRequest)request).getParameter("bypass");
    if (bypass == null) {
      return false;
    } else {
      return bypass.equalsIgnoreCase("true");
    }

  } // End doBypass().


} // End class.