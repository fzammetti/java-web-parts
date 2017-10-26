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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * This is the class that represents the configuration for a Command.  When the
 * configuration file is read in, instances of this class are created, and added
 * to an instance of the Chain class.  When the Chain is executed, the Command
 * class specified in this class will be instantiated and executed.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class CommandConfig {


  /**
   * ID of this Command.
   */
  private String id = "";


  /**
   * The class that implements this Command.
   */
  private String className = "";


  /**
   * The Chain that this Command references.  This is for when a Command
   * is actually a whole other Chain.
   */
  private String chain = "";


  /**
   * The Command that this Command replaces when this Chain extends another.
   */
  private String replaceID = "";


  /**
   * The collection of properties for this Command.
   */
  private List properties = new ArrayList();


  /**
   * Constructor.  Calls the other constructor.
   */
  public CommandConfig() {

    this(null, null, null, null);

  } // End CommandConfig().


  /**
   * Constructor.
   *
   * @param inID        The ID this Command will be known as.
   * @param inClassName The class that implements this Command.
   * @param inChain     The ID of the Chain this Command points to when the
   *                    Command is a subchain.
   * @param inReplaceID The ID of the Command this Command replaces in the
   *                    Chain, when the Chain extends another.
   */
  public CommandConfig(String inID, String inClassName, String inChain,
    String inReplaceID) {

    id        = inID;
    className = inClassName;
    chain     = inChain;
    replaceID = inReplaceID;

  } // End CommandConfig().


  /**
   * Returns a clone of the properties collection for this Command.
   *
   * @return The list of properties to be set on the Command.
   */
  public List cloneProperties() {

    List clonedProperties = new ArrayList();
    for (Iterator it = properties.iterator(); it.hasNext();) {
      Map originalProperty = (HashMap)it.next();
      Map clonedProperty   = new HashMap();
      clonedProperty.put("name",  originalProperty.get("name"));
      clonedProperty.put("value", originalProperty.get("value"));
      clonedProperties.add(clonedProperty);
    }
    return clonedProperties;

  } // End cloneProperties().


  /**
   * Setter for the ID of this Command.
   *
   * @param inID The ID of this Cmomand.
   */
  public void setId(String inID) {

    id = inID;

  } // End setId().


  /**
   * Getter for the ID of this Command.
   *
   * @return The ID of this Cmomand.
   */
  public String getId() {

    return id;

  } // End getId().


  /**
   * Setter for the class of this Command.
   *
   * @param inClassName The class that implements this Command.
   */
  public void setClassName(String inClassName) {

    className = inClassName;

  } // End setClassName().


  /**
   * Getter for the class of this Command.
   *
   * @return The class that implements this Command.
   */
  public String getClassName() {

    return className;

  } // End getClass().


  /**
   * Setter for the Chain of this Command.
   *
   * @param inChain The ID of the Chain this Command references when this
   *                Command is a sub-chain.
   */
  public void setChain(String inChain) {

    chain = inChain;

  } // End setChain().


  /**
   * Getter for the Chain of this Command.
   *
   * @return ID of the Chain this Command references.
   */
  public String getChain() {

    return chain;

  } // End getChain().


  /**
   * Setter for the replaceID of this Command.
   *
   * @param inReplaceID The ID of the Command this Command replaces when the
   *                    Chain extends another.
   */
  public void setReplaceID(String inReplaceID) {

    replaceID = inReplaceID;

  } // End setReplaceID().


  /**
   * Getter for the replaceID of this Command.
   *
   * @return The ID of the Command this one replaces.
   */
  public String getReplaceID() {

    return replaceID;

  } // End getReplaceID().


  /**
   * Add a property to the collection of properties for this Command.
   *
   * @param inName  The name of the property to set on this Command.
   * @param inValue The value of the property to set on this Command.
   */
  public void addProperty(String inName, String inValue) {

    Map m = new HashMap();
    m.put("name",  inName);
    m.put("value", inValue);
    properties.add(m);

  } // End addProperty().


  /**
   * Returns the collection of properties for this Command.
   *
   * @return The collection of properties to set on this Command.
   */
  public List getProperties() {

    return properties;

  } // End getProperties().


  /**
   * Sets the collection of properties for this Command.
   *
   * @param inProperties The collection of properties to set on this Command.
   */
  public void setProperties(List inProperties) {

    properties = inProperties;

  } // End setProperties().


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


} // End class().
