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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This is a simple class used during the DependencyFilter test.  Multiple
 * instances of this bean will be created and intiialized by the filter and
 * "injected" into session or request scope as configured in the config file.
 * These object instances can then be used by the webapp code at any time.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class DFTestBean {


  /**
   * First Name.
   */
  private String firstName;


  /**
   * Last Name.
   */
  private String lastName;


  /**
   * Date Of Birth Month.
   */
  private String dobMonth;


  /**
   * Date Of Birth Day.
   */
  private String dobDay;


  /**
   * Date Of Birth Year.
   */
  private String dobYear;


  /**
   * Eye color.
   */
  private String eyeColor;


  /**
   * IQ.
   */
  private int iq;


  /**
   * Dimensions collection.
   */
  private HashMap dimensions = new HashMap();


  /**
   * Children collection.
   */
  private ArrayList children = new ArrayList();


  /**
   * Certifications array.
   */
  private String[] certifications;


  /**
   * Certifications mutator.
   *
   * @param inIndex Index in the array the value goes into.
   * @param inValue The value to store.
   */
  public void setCertifications(int inIndex, String inValue) {

    certifications[inIndex] = inValue;

  } // End setCertifications().


  /**
   * Certifications accessor.
   *
   * @param inIndex Index in the array to return.
   * @return        The value at the requested index.
   */
  public String getCertifications(int inIndex) {

    return certifications[inIndex];

  } // End getCertifications().


  /**
   * Certifications mutator.
   *
   * @param inArray The array of certifications.
   */
  public void setCertifications(String[] inArray) {

    certifications = inArray;

  } // End setCertifications().


  /**
   * Certifications accessor.
   *
   * @return The certifications array.
   */
  public String[] getCertifications() {

    return certifications;

  } // End getCertifications().


  /**
   * First Name mutator.
   *
   * @param inFirstName First name to set.
   */
  public void setFirstName(String inFirstName) {

    firstName = inFirstName;

  } // End setFirstName().


  /**
   * First Name accessor.
   *
   * @return First name.
   */
  public String getFirstName() {

    return firstName;

  } // End getFirstName().


  /**
   * Last Name mutator.
   *
   * @param inLastName Last name to set.
   */
  public void setLastName(String inLastName) {

    lastName = inLastName;

  } // End setLastName().


  /**
   * Last Name accessor.
   *
   * @return Last name.
   */
  public String getLastName() {

    return lastName;

  } // End getLastName().


  /**
   * Date Of Birth mutator.
   *
   * @param inMonth Month to set.
   * @param inDay   Day to set.
   * @param inYear  Year to set.
   */
  public void setDOB(String inMonth, String inDay, String inYear) {

    dobMonth = inMonth;
    dobDay   = inDay;
    dobYear  = inYear;

  } // End setDOB().


  /**
   * Date Of Birth accessor.
   *
   * @return Date Of Birth.
   */
  public String getDOB() {

    return dobMonth + " " + dobDay + ", " + dobYear;

  } // End getDOB().


  /**
   * Children mutator.
   *
   * @param child Name of child to add.
   */
  public void setChildren(String child) {

    children.add(child);

  } // End setChildren().


  /**
   * Children mutator.  Alternate version that purposely does not adhere
   * to Javabean spec.
   *
   * @param child Name of child to add.
   */
  public void addChild(String child) {

    children.add(child);

  } // End addChild().


  /**
   * Children accessor.
   *
   * @return Children collection.
   */
  public List getChildren() {

    return children;

  } // End getChildren().


  /**
   * Dimensions mutator.
   *
   * @param inKey   Key of value to add.
   * @param inValue Value to add.
   */
  public void setDimensions(String inKey, String inValue) {

    dimensions.put(inKey, inValue);

  } // End setDimensions().


  /**
   * Dimensions mutator.  Alternate version that purposely does not adhere
   * to Javabean spec.
   *
   * @param inKey   Key of value to add.
   * @param inValue Value to add.
   */
  public void addDimension(String inKey, String inValue) {

    dimensions.put(inKey, inValue);

  } // End addDimension().


  /**
   * Dimensions accessor.
   *
   * @return Dimensions collection.
   */
  public Map getDimensions() {

    return dimensions;

  } // End getDimensions().


  /**
   * Eye color mutator.
   *
   * @param inEyeColor Eye color to set.
   */
  public void setEyeColor(String inEyeColor) {

    eyeColor = inEyeColor;

  } // End setEyeColor().


  /**
   * Eye color accessor.
   *
   * @return Eye color.
   */
  public String getEyeColor() {

    return eyeColor;

  } // End getEyeColor().


  /**
   * IQ mutator.
   *
   * @param inIQ IQ to set.
   */
  public void setIq(int inIQ) {

     iq = inIQ;

  } // End setIq().


  /**
   * IQ accessor.
   *
   * @return IQ.
   */
  public int getIq() {

    return iq;

  } // End getIq().


} // End class.
