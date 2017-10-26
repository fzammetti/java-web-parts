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


package javawebparts.listener;


import javawebparts.core.org.apache.commons.digester.Digester;


/**
 * This interface is the interface a class must implement to be used
 * to set custom Commons Digester rules for the AppConfigContextListener.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public interface AppConfigRuleset {


  /**
   * This method, when implemented, should set the applicable rules on the
   * Digester object passed in, and return it.
   *
   * @param  inDigester    Digester instance to set rules on.
   * @param  inRootElement The root element of the document.
   * @param  inConfigClass The class that will be populated.
   * @return               The Digester instance with all rules set.
   */
  public Digester setRules(Digester inDigester, String inRootElement,
    String inConfigClass);


} // End AppConfigRuleset interface.
