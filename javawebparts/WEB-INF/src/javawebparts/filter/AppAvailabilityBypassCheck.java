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


import javax.servlet.ServletRequest;


/**
 * This interface is the interface a class must implement to be used
 * to do a bypass check.  A bypass check allows a request to continue even
 * when the app is not available (i.e., allowing administrators to still
 * access an unavailable application).  It can do any kind of check the
 * developer wants, it simply must implement the single doBypass() method
 * and return true if the filter should allow the request no matter what.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public interface AppAvailabilityBypassCheck {

/**
 * This method, when implemented, should return true if the filter should
 * allow the request anyway, effectively bypassing itself.
 *
 * @param  inRequest ServletRequest being serviced.
 * @return           True if the request should continue, false otherwise.
 */
  public boolean doBypass(ServletRequest inRequest);


} // End interface.
