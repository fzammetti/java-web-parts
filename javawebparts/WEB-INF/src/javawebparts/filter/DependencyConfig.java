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


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class is a bean that represents a &lt;dependency&gt; element
 * in the config file for the DependencyFilter.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class DependencyConfig {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("DependencyConfig" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(DependencyConfig.class);


  /**
   * Scope the configured dependency will be under (request or session).
   */
  private String scope;


  /**
   * Name the configured dependency will be under.
   */
  private String name;


  /**
   * Name of the class of the configured dependency.
   */
  private String className;


  /**
   * Name of the class that will be used to initialize the object upon creation.
   */
  private String initClass;


  /**
   * Method of the initClass that will be called to initialize the object.
   */
  private String initMethod;


  /**
   * This is the collection of paths for which the dependency will be created.
   */
  private ArrayList createForPathsAL;


  /**
   * This is the collection of paths for which the dependency will be created.
   * THIS IS ONLY NEEDED TO ALLOW DIGESTER TO HAVE SOMETHING TO SET.  It
   * should probably not be needed after initial population, createForPathsAL
   * should be all that is needed.
   */
  private String createForPaths;


  /**
   * The maximum age in seconds for a session-scoped dependency.  If a
   * created object is older than this value, it will be re-created.
   */
  private long maxAge;


  /**
   * List of simple properties that will be configured on the object.
   */
  private ArrayList initProps = new ArrayList();


  /**
   * List of methods that will be called on the object after creation.
   */
  private ArrayList methodCalls = new ArrayList();


  /**
   * List of List properties that will be configured on the object.  This is
   * the version used during parsing of the config file.
   */
  private ArrayList initListsTemp = new ArrayList();


  /**
   * List of List properties that will be configured on the object.
   */
  private ArrayList initLists = new ArrayList();


  /**
   * List of Map properties that will be configured on the object.  This is
   * the version used during parsing of the config file.
   */
  private ArrayList initMapsTemp = new ArrayList();


  /**
   * List of Map properties that will be configured on the object.
   */
  private ArrayList initMaps = new ArrayList();


  /**
   * List of Array properties that will be configured on the object.  This is
   * the version used during parsing of the config file.
   */
  private ArrayList initArraysTemp = new ArrayList();


  /**
   * List of Array properties that will be configured on the object.
   */
  private ArrayList initArrays = new ArrayList();


  /**
   * createForPaths mutator.
   *
   * @param inCreateForPaths The comma-separeted list of paths for which the
   *                         dependency will be created.  Note that this string
   *                         will be parsed and an ArrayList created from it,
   *                         which will become the value of createForPathsAL.
   */
  public void setCreateForPaths(String inCreateForPaths) {

    if (inCreateForPaths != null) {
      createForPaths   = inCreateForPaths;
      createForPathsAL = new ArrayList();
      log.info("inCreateForPaths = " + inCreateForPaths);
      StringTokenizer st = new StringTokenizer(inCreateForPaths, ",");
      // Parse the CSV.
      while (st.hasMoreTokens()) {
        String s  = st.nextToken();
        StringBuffer sb = new StringBuffer(s.length() + 10);
        // We need to replace any wildcard characters with a suitable regex.
        for (int i = 0; i < s.length(); i++) {
          if (s.charAt(i) == '*') {
            sb.append(".*");
          } else {
            sb.append(s.charAt(i));
          }
        }
        // Modified paths can now be added to the collection.
        createForPathsAL.add(sb.toString());
      }
    }
    log.info("createForPathsAL = " + createForPathsAL);

  } // End setCreateForPaths().


  /**
   * createForPaths accessor.
   *
   * @return The collection of paths for which the dependency will be created
   *         in comma-separated String form.  THIS METHOD SHOULD NOT BE NEEDED,
   *         EXCEPT TO MAKE DIGESTER POPULATE THIS PROPERTY (without out,
   *         Digester sees that it's a read-only property and does not
   *         set it).
   */
  public String getCreateForPaths() {

    return createForPaths;

  } // End getCreateForPaths().


  /**
   * createForPathsAL accessor.
   *
   * @return The collection of paths for which the dependency will be created.
   *         THIS IS THE ONE CLIENT CODE SHOULD USE!
   */
  public ArrayList getCreateForPathsAL() {

    return createForPathsAL;

  } // End getCreateForPathsAL().


  /**
   * Max Age mutator.
   *
   * @param inMaxAge Max age for session-scoped object to be considered "fresh".
   */
  public void setMaxAge(long inMaxAge) {

    maxAge = inMaxAge;

  } // End setMaxAge().


  /**
   * Max Age accessor.
   *
   * @return Max age for session-scoped object to be considered "fresh".
   */
  public long getMaxAge() {

    return maxAge;

  } // End getMaxAge().


  /**
   * Scope mutator.
   *
   * @param inScope Scope of the dependency.
   */
  public void setScope(String inScope) {

    scope = inScope;

  } // End setScope().


  /**
   * Scope accessor.
   *
   * @return The scope of the dependency.
   */
  public String getScope() {

    return scope;

  } // End getScope().


  /**
   * Name mutator.
   *
   * @param inName Name the dependency will be stored under.
   */
  public void setName(String inName) {

    name = inName;

  } // End setName().


  /**
   * Name accessor.
   *
   * @return Name the dependency will be stored under.
   */
  public String getName() {

    return name;

  } // End getName().


  /**
   * Class Name mutator.
   *
   * @param inClassName The name of the class for this dependency.
   */
  public void setClassName(String inClassName) {

    className = inClassName;

  } // End setClassName().


  /**
   * Class Name accessor.
   *
   * @return The name of the class for this dependency.
   */
  public String getClassName() {

    return className;

  } // End getClassName().


  /**
   * Init Class mutator.
   *
   * @param inInitClass Name of the class used to initialize the object.
   */
  public void setInitClass(String inInitClass) {

    initClass = inInitClass;

  } // End setInitClass().


  /**
   * Init Class accessor.
   *
   * @return Name of the class used to initialize the object.
   */
  public String getInitClass() {

    return initClass;

  } // End getInitClass().


  /**
   * Init Method mutator.
   *
   * @param inInitMethod Method of initClass that will be called.
   */
  public void setInitMethod(String inInitMethod) {

    initMethod = inInitMethod;

  } // End setInitMethod().


  /**
   * Init Method accessor.
   *
   * @return Method of initClass that will be called
   */
  public String getInitMethod() {

    return initMethod;

  } // End getInitMethod().


  /**
   * Used to add a new simple property initialization to the collection.
   *
   * @param inName  The name fo the property to set.
   * @param inValue The value to set the property to.
   */
  public void addInitProp(String inName, String inValue) {

    HashMap hm = new HashMap();
    hm.put("name",  inName);
    hm.put("value", inValue);
    initProps.add(hm);

  } // End addInitProp().


  /**
   * Returns the collection of simple property initializations.
   *
   * @return List of simple property initializations.
   */
  public ArrayList getInitProps() {

    return initProps;

  } // End getInitProps().


  /**
   * Used to add a new method call to the collection.
   *
   * @param inName      The name fo the method call.
   * @param inArguments Comma-separated list of arguments (Strings only!) to
   *                    be passed to the method.
   */
  public void addMethodCall(String inName, String inArguments) {

    HashMap hm = new HashMap();
    hm.put("name", inName);
    StringTokenizer st = new StringTokenizer(inArguments, ",");
    ArrayList al = new ArrayList();
    while (st.hasMoreTokens()) {
      String argument = st.nextToken();
      al.add(argument);
    }
    hm.put("arguments", al);
    methodCalls.add(hm);

  } // End addMethodCall().


  /**
   * Returns the collection of method calls.
   *
   * @return List of method calls.
   */
  public ArrayList getMethodCalls() {

    return methodCalls;

  } // End getMethodCalls().


  /**
   * Used to add a new List property initialization to the collection.  This
   * is the version called during config file parsing to build up the temporary
   * collection before the real one is constructed.
   *
   * @param inValue The value that will be added to the object's property.
   */
  public void addInitListTemp(String inValue) {

    initListsTemp.add(inValue);

  } // End addInitListTemp().


  /**
   * Used to add a new List property initialization to the collection.
   *
   * @param inMethod Name of the method used to set the property.
   * @param inName   The name of the List property to set.
   */
  public void addInitList(String inMethod, String inName) {

    for (Iterator it = initListsTemp.iterator(); it.hasNext();) {
      String val = (String)it.next();
      HashMap hm = new HashMap();
      hm.put("method", inMethod);
      hm.put("name",   inName);
      hm.put("value",  val);
      initLists.add(hm);
    }
    initListsTemp = new ArrayList();

  } // End addInitList().


  /**
   * Returns the collection of List property initializations.
   *
   * @return List of List property initializations.
   */
  public ArrayList getInitLists() {

    return initLists;

  } // End getInitLists().



  /**
   * Used to add a new Array property initialization to the collection.  This
   * is the version called during config file parsing to build up the temporary
   * collection before the real one is constructed.
   *
   * @param inValue The value to be added to the object's property.
   */
  public void addInitArrayTemp(String inValue) {

    initArraysTemp.add(inValue);

  } // End addInitArrayTemp().


  /**
   * Used to add a new Array property initialization to the collection.
   *
   * @param inName The name of the Array property to set.
   */
  public void addInitArray(String inName) {

    for (Iterator it = initArraysTemp.iterator(); it.hasNext();) {
      String val = (String)it.next();
      HashMap hm = new HashMap();
      hm.put("name",   inName);
      hm.put("value",  val);
      initArrays.add(hm);
    }
    initArraysTemp = new ArrayList();

  } // End addInitArray().


  /**
   * Returns the collection of Array property initializations.
   *
   * @return List of Array property initializations.
   */
  public ArrayList getInitArrays() {

    return initArrays;

  } // End getInitArrays().


  /**
   * Used to add a new Map property initialization to the collection.  This
   * is the version called during config file parsing to build up the temporary
   * collection before the real one is constructed.
   *
   * @param inKey    The key to be added to the collection.
   * @param inValue  The value to be added to the collection.
   */
  public void addInitMapTemp(String inKey, String inValue) {

    HashMap hm = new HashMap();
    hm.put("key", inKey);
    hm.put("value", inValue);
    initMapsTemp.add(hm);

  } // End addInitMapTemp().


  /**
   * Used to add a new Map property initialization to the collection.
   *
   * @param inMethod Name of the method used to set the property.
   * @param inName   The name fo the Map property to set.
   */
  public void addInitMap(String inMethod, String inName) {

    for (Iterator it = initMapsTemp.iterator(); it.hasNext();) {
      HashMap nextHM = (HashMap)it.next();
      HashMap hm = new HashMap();
      hm.put("method",  inMethod);
      hm.put("name",    inName);
      hm.put("key",     (String)nextHM.get("key"));
      hm.put("value",   (String)nextHM.get("value"));
      initMaps.add(hm);
    }
    initMapsTemp = new ArrayList();

  } // End addInitMap().


  /**
   * Returns the collection of Map property initializations.
   *
   * @return List of Map property initializations.
   */
  public ArrayList getInitMaps() {

    return initMaps;

  } // End getInitMaps().


  /**
   * Overridden toString method.
   *
   * @return String representation of this bean.
   */
  public String toString() {

    String str = null;
    StringBuffer sb = new StringBuffer(1000);
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


} // End dependencyConfig class.