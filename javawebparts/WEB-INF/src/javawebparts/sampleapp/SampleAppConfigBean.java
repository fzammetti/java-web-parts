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


  /**
   * This class holds configuration information.  It is an example of using
   * a custom class.
   *
   * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
   */
  public class SampleAppConfigBean {


    /**
     * Item1 value.
     */
    private static String item1;


    /**
     * Item2 value.
     */
    private static String item2;


    /**
     * Item3 value.
     */
    private static String item3;


    /**
     * Item1 mutator.
     *
     * @param val New value for Item1.
     */
    public void setItem1(String val) {

      item1 = val;

    } // End setItem1().


    /**
     * Item1 accessor.
     *
     * @return Item1 value.
     */
    public static String getItem1() {

      return item1;

    } // End getItem1().


    /**
     * Item2 mutator.
     *
     * @param val New value for Item2.
     */
    public void setItem2(String val) {

      item2 = val;

    } // End setItem2().


    /**
     * Item1 accessor.
     *
     * @return Item2 value.
     */
    public static String getItem2() {

      return item2;

    } // End getItem2().


    /**
     * Item3 mutator.
     *
     * @param val New value for Item3.
     */
    public void setItem3(String val) {

      item3 = val;

    } // End setItem3().


    /**
     * Item1 accessor.
     *
     * @return Item3 value.
     */
    public static String getItem3() {

      return item3;

    } // End getItem3().


  } // End SampleAppConfigBean class.
