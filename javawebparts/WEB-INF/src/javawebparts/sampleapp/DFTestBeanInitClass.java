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


import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import javawebparts.core.JWPHelpers;
import javawebparts.core.org.apache.commons.beanutils.PropertyUtils;


/**
 * This class is used during the DependencyFilter test.  It is used to
 * initialize an object created by that filter to show how an initialization
 * class can be used during object creation.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class DFTestBeanInitClass {


  /**
   * Method called to initialize a given bean.
   *
   * @param obj Instance of DFTestBean to initialize.
   */
  public void initBean(DFTestBean obj) {

    try {

      obj.setFirstName("Traci");
      obj.setLastName("Almeda");
      obj.setEyeColor("Hazel");
      obj.setIq(JWPHelpers.randomNumber(100, 150));
      obj.setDOB("January", "6", "1974");
      obj.setDimensions("height", "4ft, 7in");
      obj.setDimensions("weight", "120lbs");
      obj.setDimensions("waist", "24in");
      obj.setChildren("Melissa");
      obj.setChildren("John");
      Class type = String.class;
      Object certArray = Array.newInstance(type, 3);
      Array.set(certArray, 0, "CNE");
      Array.set(certArray, 1, "SCJD");
      Array.set(certArray, 2, "i-Net+");
      PropertyUtils.setProperty(obj, "certifications", certArray);

    } catch (NoSuchMethodException nsme) {
      nsme.printStackTrace();
    } catch (IllegalAccessException iae) {
      iae.printStackTrace();
    } catch (InvocationTargetException ite) {
      ite.printStackTrace();
    }

  } // End initBean().


} // End class.
