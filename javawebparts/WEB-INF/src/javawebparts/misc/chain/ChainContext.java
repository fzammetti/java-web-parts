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


package javawebparts.misc.chain;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


/**
 * This class represents a ChainContext.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class ChainContext {


  /**
   * The collection of attributes in this context instance.
   */
  private Map attributes = new HashMap();


  /**
   * The ID of the Catalog the currently executing Chain that this context is
   * being used by belongs to.
   */
  private String catalogID = "";


  /**
   * The ID of the Chain that is currently executing that this context is
   * being used by.
   */
  private String chainID = "";


  /**
   * The result of the Chain execution that this context is being used by.
   */
  private Result result;


  /**
   * Sets an attribute in the context.
   *
   * @param inKey       The key of the attribute to set.
   * @param inAttribute The value of the attribute to set.
   */
  public void setAttribute(String inKey, Object inAttribute) {

    attributes.put(inKey, inAttribute);

  } // End setAttribute().


  /**
   * Gets an attribute from the context.
   *
   * @param  inKey The key of the attribute to get.
   * @return       The value of the attribute to get.
   */
  public Object getAttribute(String inKey) {

    return attributes.get(inKey);

  } // End getAttribute().


  /**
   * Setter for the ID of the Catalog this context is being used to service.
   *
   * @param inCatalogID The ID of the Catalog this context is being used
   *                    to service.
   */
  public void setCatalogId(String inCatalogID) {

    catalogID = inCatalogID;

  } // End setCatalogId().


  /**
   * Getter for the ID of the Catalog this context is being used to service.
   *
   * @return The ID of the Catalog this context is being used to service.
   */
  public String getCatalogId() {

    return catalogID;

  } // End getCatalogId().


  /**
   * Setter for the ID of the Chain this context is being used to service.
   *
   * @param inChainID The ID of the Chain this Context is being used to service.
   */
  public void setChainId(String inChainID) {

    chainID = inChainID;

  } // End setChainId().


  /**
   * Getter for the ID of the Chain this context is being used to service.
   *
   * @return The ID of the Chain this Context is being used to service.
   */
  public String getChainId() {

    return chainID;

  } // End getChainId().


  /**
   * Sets the result of the Chain execution that this context is being used by.
   *
   * @param inResult The Result object instance of the last Command (or
   *                 subchain) that executed.
   */
  public void setResult(Result inResult) {

    result = inResult;

  } // End setResult().


  /**
   * Gets the result of the Chain execution that this context is being used by.
   *
   * @return The Result object instance of the last Command (or subchain)
   *         that executed.
   */
  public Result getResult() {

    return result;

  } // End getResult().


  /**
   * Overridden toString method.
   *
   * @return String representation of this bean.
   */
  public String toString() {

    String       str = null;
    StringBuffer sb  = new StringBuffer(1000);
    sb.append("[" + super.toString() + "]={\n");
    boolean firstPropertyDisplayed = false;
    try {
      Field[] fields = this.getClass().getDeclaredFields();
      for (int i = 0; i < fields.length; i++) {
        if (firstPropertyDisplayed) {
          sb.append("\n");
        } else {
          firstPropertyDisplayed = true;
        }
        sb.append(fields[i].getName() + "=" + fields[i].get(this));
      }
      sb.append("\n}");
      str = sb.toString().trim();
    } catch (IllegalAccessException iae) {
      iae.printStackTrace();
    }
    return str;

  } // End toString().


} // End class.