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
import java.util.Iterator;


/**
 * This class represents a Catalog.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class Catalog {


  /**
   * The ID of this Catalog.
   */
  private String id = "";


  /**
   * The ID of the Catalog this Catalog extends, if any
   */
  private String extendsID;


  /**
   * The collection of Chains that belong to this Catalog..
   */
  private Map chains = new HashMap();


  /**
   * Constructor.  Calls the other constructor.
   */
  public Catalog() {

    this(null, null);

  } // End Catalog().


  /**
   * Constructor.
   *
   * @param inID        The ID that identifies this Catalog.
   * @param inExtendsID The ID of the Catalog that this Catalog extends.  Pass
   *                    null if not extending any Catalog.
   */
  public Catalog(String inID, String inExtendsID) {

    id = inID;
    setExtendsID(inExtendsID);

  } // End Catalog().


  /**
   * Returns a clone of the Chains collection for this Catalog.
   *
   * @return The Map of Chains that exist in this Catalog.
   */
  public Map cloneChains() {

    Map clonedChains = new HashMap();
    for (Iterator it = chains.keySet().iterator(); it.hasNext();) {
      String chainID = (String)it.next();
      Chain originalChain = (Chain)chains.get(chainID);
      Chain clonedChain = new Chain(chainID, null);
      // Need to clone the commands collection too.
      clonedChain.setCommands(originalChain.cloneCommands());
      clonedChains.put(chainID, clonedChain);
    }
    return clonedChains;

  } // End cloneChains().


  /**
   * Sets the ID of this Catalog.
   *
   * @param inID The ID of this Catalog.
   */
  public void setId(String inID) {

    id = inID;

  } // End setId().


  /**
   * Gets the ID of this Catalog.
   *
   * @return The ID of this Catalog.
   */
  public String getId() {

    return id;

  } // End getId().


  /**
   * Sets the ID of the Catalog that this Catalog extends.
   *
   * @param inExtendsID The ID of the Catalog this Catalog extends, if any.
   */
  public void setExtendsID(String inExtendsID) {

    extendsID = inExtendsID;

    // Deal with the case of this Catalog extending another, if applicable.
    if (extendsID != null) {
      // Now get a reference to the base Catalog.
      Catalog catalog = ChainManager.findCatalog(inExtendsID);
      chains = catalog.cloneChains();
    }

  } // End setExtendsID().


  /**
   * Gets the ID of the Catalog that this Catalog extends.
   *
   * @return The ID of the Catalog this Catalog extends, if any.
   */
  public String getExtendsID() {

    return extendsID;

  } // End getExtendsID().


  /**
   * Adds a Chain to this Catalog.
   *
   * @param inChain Adds the passed in Chain to this Catalog.
   */
  public void addChain(Chain inChain) {

    chains.put(inChain.getId(), inChain);

  } // End addChain().


  /**
   * Looks up and returns a named Chain from this catalog.
   *
   * @param inID The ID of the Chain to look up in this Catalog.
   * @return     A reference to the Chain, or null if not found.
   */
  public Chain findChain(String inID) {

    return (Chain)chains.get(inID);

  } // End findChain().


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