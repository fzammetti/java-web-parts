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


import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import javawebparts.core.org.apache.commons.beanutils.BeanUtils;
import javawebparts.core.org.apache.commons.beanutils.Converter;
import javawebparts.core.org.apache.commons.beanutils.ConvertUtils;
import javawebparts.core.org.apache.commons.beanutils.MethodUtils;
import javawebparts.core.org.apache.commons.beanutils.PropertyUtils;
import javawebparts.core.org.apache.commons.digester.Digester;
import javawebparts.core.org.apache.commons.digester.ExtendedBaseRules;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;


/**
 * This filter is a very simple hybrid IoC (Inversion Of Control)
 * implementation.  It is hybrid in that the dependencies are not strictly
 * speaking "injected", as is usually the case when discussing IoC.  They are
 * created as needed, and made available to client code in a simple manner,
 * so while the dependencies aren't injected, it is still very useful.
 * They are in a sense injected into either the incoming request or session
 * objects though... I'm sure you can see why I call it "hybrid" IoC! :)  It
 * allows you to define objects via XML configuration file that will be
 * created per-session or per-request, and initialzed as you determine is
 * neccessary.
 * <br><br>
 * Init parameters are:
 * <br>
 * <ul>
 * <li><b>createSession</b> - When this filter fires, it gets a reference
 * to the current session.  If none exists, by default it creates it.  However,
 * a developer may determine this should not happen.  In that case, setting this
 * parameter to false will stop a session from being created.  However, it is
 * important to realize that in such a case, a call to getDependency() will
 * result in null being returned, and it becomes the developers' responsibility
 * to deal with this.  Required: No.  Default: true.</li>
 * <br><br>
 * <li><b>configFile</b> - Context-relative path to the config file that
 * defines the dependencies.  Reqired: Yes.  Default: None.</li>
 * </ul>
 * Sample Configuration File:
 * <br><br>
 * &lt;dependencies&gt;<br>
 * &nbsp;&nbsp;&lt;dependency scope="request" name="MyTestBean1" maxAge="10"
 * createForPaths="/myPath.do"&gt;
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;className&gt;
 * javawebparts.sampleapp.DFTestBean&lt;/className&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;callMethod
 * name="setDOB" arguments="February,17,1973" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;initClass
 * name="myInitClass" mathod="initBean" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;initProp
 * name="firstName" value="Frank" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;initProp
 * name="lastName" value="Zammetti" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;initList
 * name="children"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;
 * listItem value="Andrew" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;
 * listItem value="Michael" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/initList&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;initList
 * name="children" method="addChild"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;
 * listItem value="Ashley" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/initList&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;
 * initMap name="dimensions"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;
 * mapItem key="height" value="5ft, 9in" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/initMap&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;initMap
 * name="dimensions" method="addDimension"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;
 * mapItem key="weight" value="185lbs" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/initMap&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;<br>
 * &lt;/dependencies&gt;
 * <br><br>
 * The simplest possible config file would be this:
 * <br><br>
 * &lt;dependencies&gt;<br>
 * &nbsp;&nbsp;&lt;dependency scope=&quot;?AA?&quot; name=&quot;
 * ?BB?&quot;&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;className&gt;?CC?&lt;
 * /className&gt;<br>
 * &nbsp;&nbsp;&lt;/dependency&gt;<br>
 * &lt;/dependencies&gt;
 * <br><br>
 * ?AA? would be either "request" or "session", ?BB? would be the name of
 * the object as it will be referenced from your code, and ?CC? is the class
 * to create.  The object will be created per-request or per-session as
 * specified, and will not be initialized in any way.  Note that if it is
 * request-scoped, it will be created for EVERY request!  You will be able to
 * retrieve it by using:
 * <br><br>
 * ?CC? obj = (?CC?)DependencyFilter.getDependency("?BB?", request);
 * <br><br>
 * ?CC? is again the name of the class, ?BB? is the name and request is the
 * current request object.  You DO NOT need to specify whether the object is
 * in request or session scope, it will be found either way.
 * <br><br>
 * More explanation on the config file:
 * <br>
 * <ul>
 * <li><b>scope attribute</b> - The scope attribute of the &lt;dependency&gt;
 * element determines where the created object will be placed, either "request"
 * or "session".  Obviously, if placed in request-scope, the object will not
 * persist beyond the current request, whereas it will in session-scope.
 * Required: Yes.  Default: None.</li>
 * <br><br>
 * <li><b>name attribute</b> - The name attribute of the &lt;dependency&gt;
 * element defines what the name of the created object will be.  This is the
 * name you will reference when getting the object.  It is a request or
 * session attribute key, depending on the specified scope.
 * Required: Yes.  Default: None.<li>
 * <br><br>
 * <li><b>createForPaths attribute</b> - The createForPaths attribute of the
 * &lt;dependency&gt; element is an optional attribute which specifies
 * what paths the object will be created for *IF* the specified scope is
 * "request" (if scope if "session", the object will be created with the first
 * path requested).  This is a comma-separated list of paths, including
 * wildcard support (in the same way as all the other filters in JWP).
 * If you want the object to be created for all request paths, DO NOT include
 * this attribute at all.  Required: No.  Default: All request paths.</li>
 * <br><br>
 * <li><b>maxAge attribute</b> - The maxAge attribute of the &lt;dependency&gt;
 * element is an optional attribute which specifies the maximum age, in
 * seconds, for the dependency to be considered "fresh".  In other words, if
 * you want to maintain a bean in a users' session that contains a list of
 * values read from a database, but you want the list to be refreshed every
 * five minutes, set the maxAge attribute of the dependency to 300
 * (5 minutes * 60 seconds) and each request that the filter handles will
 * check the age of the existing object instance in session and will
 * recreate it if its age exceeds this value.  To mark a dependency as
 * never expiring, i.e., it will be created once per session only, set
 * maxAge to 0, or simply leave out the maxAge attribute.</li>
 * <br><br>
 * <li><b>&lt;className&gt;</b> - This is the class of the object to be
 * created.  This element IS required.</li>
 * <br><br>
 * <li><b>&lt;initClass&gt;</b> - You can have a single one of these as a child
 * of the &lt;dependency&gt; element.  This defines a class (via the name
 * attribute) and method (via the method attribute) that will be called after
 * the object has been instantiated.  The object itself will be passed as the
 * only parameter to the method, and you can do whatever kind of initialization
 * you want.  This method SHOULD NOT return a value as it will be ignored.
 * This element IS NOT required.</li>
 * <br><br>
 * <li><b>&lt;callMethod&gt;</b> - You can have one or more of these as a child
 * of the &lt;dependency&gt; element.  This element specifies a method of the
 * object being created that will be called AFTER all initialization has
 * taken place.  The name attribute specifies the name of the method that
 * will be called and the arguments attribute is a list of arguments
 * (comma-separated) that will be passed.  NOTE THAT ONLY STRING ARGUMENTS
 * ARE SUPPORTED!  This element IS NOT required.</li>
 * <br><br>
 * <li><b>&lt;initProp&gt;</b> - You can have one or more of these as a child
 * of the &lt;dependency&gt; element.  This will initialize a property of
 * the object being created (as named by the name attribute) to the value
 * specified by the value attribute.  This element IS NOT required.</li>
 * <br><br>
 * <li><b>&lt;initList&gt;</b> - You can have one or more of these as a child
 * of the &lt;dependency&gt; element.  This will initialize a List property of
 * the object being created (as named by the name attribute).  You can
 * optionally specify a method attribute.  If NOT present, then a Javabean
 * spec-compliant method matching the name attribute must be present (i.e.,
 * if the property is named firstName, then setFirstName() must be an
 * accessible method).  If the method attribute is present, that method will
 * be called instead.  Within each &lt;initList&gt; element can be any number
 * of child &lt;listItem&gt; elements.  For each of these simply specify the
 * value attribute as the value to be added to the list.
 * This element IS NOT required.</li>
 * <br><br>
 * <li><b>&lt;initMap&gt;</b> - You can have one or many of these as a child
 * of the &lt;dependency&gt; element.  This will initialize a Map property of
 * the object being created (as named by the name attribute).  You can
 * optionally specify a method attribute.  If NOT present, then a Javabean
 * spec-compliant method matching the name attribute must be present (i.e.,
 * if the property is named firstName, the setFirstName() must be an
 * accessible method).  If the method attribute is present, that method will
 * be called instead.  Within each &lt;initMap&gt; element can be any number
 * of child &lt;mapItem&gt; elements.  For each of these simply specify the
 * key attribute as the key to be added to the list and the value attribute as
 * the value to be added to the list.
 * This element IS NOT required.</li>
 * </ul>
 * Note that because Commons Beanutils is used to set the various properties,
 * you can use whatever types it supports and conversions will be automatically
 * handled.
 * <br><br>
 * The order things occur in when an object is created is: <br><br>
 * (1) The object is created<br>
 * (2) The simple properties (&lt;initProp&gt; elements) are done, if present
 * <br>
 * (3) The List properties (&lt;initList&gt; element) are done, if present<br>
 * (4) The Map properties (&lt;initMap&gt; element) are done, if present<br>
 * (5) The &lt;initClass&gt; element is done, if present<br>
 * (6) The &lt;callMethod&gt; element(s) are done, if present<br>
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class DependencyFilter implements Filter {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javawebparts.core.org.apache.commons.beanutils.BeanUtils");
      Class.forName("javawebparts.core.org.apache.commons.beanutils.Converter");
      Class.forName(
        "javawebparts.core.org.apache.commons.beanutils.ConvertUtils");
      Class.forName(
        "javawebparts.core.org.apache.commons.beanutils.MethodUtils");
      Class.forName(
        "javawebparts.core.org.apache.commons.beanutils.PropertyUtils");
      Class.forName("javawebparts.core.org.apache.commons.digester.Digester");
      Class.forName(
        "javawebparts.core.org.apache.commons.digester.ExtendedBaseRules");
      Class.forName("javax.servlet.Filter");
      Class.forName("javax.servlet.FilterChain");
      Class.forName("javax.servlet.FilterConfig");
      Class.forName("javax.servlet.http.HttpServletRequest");
      Class.forName("javax.servlet.http.HttpSession");
      Class.forName("javax.servlet.ServletException");
      Class.forName("javax.servlet.ServletRequest");
      Class.forName("javax.servlet.ServletResponse");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("DependencyFilter" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(DependencyFilter.class);


  /**
   * Collection of DependencyConfig objects.
   */
  private static HashMap dependencyConfigs = new HashMap();


  /**
   * Flag: should sessions be created by this filter if one does not exist
   * already, or not.
   */
  private boolean createSession = true;


  /**
   * Destroy.
   */
  public void destroy() {

  } // End destroy.


  /**
   * Initialize this filter.  This amounts to reading in the specifid config
   * file and creating a bunch of DependencyConfig objects from it and
   * adding them to the collection for later use.
   *
   * @param  filterConfig     The configuration information for this filter.
   * @throws ServletException ServletException.
   */
  public void init(FilterConfig filterConfig) throws ServletException {

    log.info("init() started");

    // createSession init param.  The default is to create session, and only
    // this parameter being set to "false" will change that
    String cs = filterConfig.getInitParameter("createSession");
    if (cs != null && cs.equalsIgnoreCase("false")) {
      createSession = false;
    }

    try {

      // Instantiate a Digester and set it up... enable extended rules, no
      // document validation, logger for this class and push this filter class
      // onto the stack so we can save the DependencyConfig objects that are
      // created during the parsing of the config file.
      Digester digester = new Digester();
      digester.setRules(new ExtendedBaseRules());
      digester.setValidating(false);
      digester.setLogger(log);
      digester.push(this);

      // Create a DependencyConfig object.
      digester.addObjectCreate("dependencies/dependency",
        "javawebparts.filter.DependencyConfig");

      // Set the properties of the object corresponding to the attributes
      // of the <dependency> element in the config file.
      digester.addSetProperties("dependencies/dependency");

      // Set the dependency class name.
      digester.addBeanPropertySetter("dependencies/dependency/className",
                                     "className");

      // Set the init class and method properties, if present.
      digester.addSetProperties("dependencies/dependency/initClass",
        new String[] {"name", "method"},
        new String[] {"initClass", "initMethod"});

      // Set the method call properties, if present.
      digester.addCallMethod("dependencies/dependency/callMethod",
                             "addMethodCall", 2);
      digester.addCallParam("dependencies/dependency/callMethod", 0, "name");
      digester.addCallParam("dependencies/dependency/callMethod", 1,
                            "arguments");

      // Set the initProp properties, if present.
      digester.addCallMethod("dependencies/dependency/initProp",
                             "addInitProp", 2);
      digester.addCallParam("dependencies/dependency/initProp", 0, "name");
      digester.addCallParam("dependencies/dependency/initProp", 1, "value");

      // Set the initList properties, if present.
      digester.addCallMethod("dependencies/dependency/initList/listItem",
                             "addInitListTemp", 1);
      digester.addCallParam("dependencies/dependency/initList/listItem", 0,
                            "value");
      digester.addCallMethod("dependencies/dependency/initList",
                             "addInitList", 2);
      digester.addCallParam("dependencies/dependency/initList", 0, "method");
      digester.addCallParam("dependencies/dependency/initList", 1, "name");

      // Set the initMap properties, if present.
      digester.addCallMethod("dependencies/dependency/initMap/mapItem",
                             "addInitMapTemp", 2);
      digester.addCallParam("dependencies/dependency/initMap/mapItem", 0,
                            "key");
      digester.addCallParam("dependencies/dependency/initMap/mapItem", 1,
                            "value");
      digester.addCallMethod("dependencies/dependency/initMap",
                             "addInitMap", 2);
      digester.addCallParam("dependencies/dependency/initMap", 0, "method");
      digester.addCallParam("dependencies/dependency/initMap", 1, "name");

      // Set the initArray properties, if present.
      digester.addCallMethod("dependencies/dependency/initArray/arrayItem",
                             "addInitArrayTemp", 1);
      digester.addCallParam("dependencies/dependency/initArray/arrayItem", 0,
                            "value");
      digester.addCallMethod("dependencies/dependency/initArray",
                             "addInitArray", 1);
      digester.addCallParam("dependencies/dependency/initArray", 0, "name");

      // When the DependencyConfig object has been all configured, we need to
      // add it to the collection of DependencyConfig objects maintained by
      // this filter class.
      digester.addSetNext("dependencies/dependency", "addDependency");

      // Get a reference to the specified config file and have our Digester
      // instance parse it.
      String      configFile  = filterConfig.getInitParameter("configFile");
      InputStream isConfigFile =
        filterConfig.getServletContext().getResourceAsStream(configFile);
      digester.parse(isConfigFile);

      // Display the configured DependencyConfig objects in the collection.
      log.info("\n");
      for (Iterator it = dependencyConfigs.keySet().iterator(); it.hasNext();) {
        String name = (String)it.next();
        DependencyConfig dc = (DependencyConfig)dependencyConfigs.get(name);
        log.info(dc + "\n");
      }

    // Handle any exceptions that might occur.
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (SAXException se) {
      se.printStackTrace();
    }

    log.info("init() completed");

  } // End init().


  /**
   * This method is called during config file parsing by Digester to add the
   * configured DepedencyConfig object to the collection.
   *
   * @param dc The fully-configured DependencyConfig object.
   */
  public void addDependency(DependencyConfig dc) {

    dependencyConfigs.put(dc.getName(), dc);

  } // End addDependency().


  /**
   * Do filter's work.
   *
   * @param  request          The current request object.
   * @param  response         The current response object.
   * @param  filterChain      The current filter chain.
   * @throws ServletException ServletException.
   * @throws IOException      IOException.
   */
  public void doFilter(ServletRequest request, ServletResponse response,
                       FilterChain filterChain)
                       throws ServletException, IOException {

    try {

      // We'll need HTTP request and session objects throughout.
      HttpServletRequest req = (HttpServletRequest)request;
      HttpSession        ses = req.getSession(createSession);

      // Go through each dependency and deal with it.
      for (Iterator it0 = dependencyConfigs.keySet().iterator();
           it0.hasNext();) {

        // Get the DependencyConfig object and some values from it.
        DependencyConfig dc =
          (DependencyConfig)dependencyConfigs.get((String)it0.next());
        String    scope          = dc.getScope();
        String    name           = dc.getName();
        ArrayList createForPaths = dc.getCreateForPathsAL();
        log.info("Object name: " + name + " ... scope: " + scope +
          "createForPaths: " + createForPaths);

        // The first thing we do is see if (a) this is a session-scoped
        // dependency, or (b) there was no createForPaths attribute specified
        // for this dependency, or (c) whether it's requesr-scoped and the
        // requested path matches one of the createForPaths elements.  Only in
        // those three cases do we actually continue our work for this
        // dependency.
        if (scope.equalsIgnoreCase("session") | createForPaths == null |
          FilterHelpers.filterPath(request, createForPaths, "include")) {

          // Reference to the object that will be created or restored.
          Object  o                  = null;
          boolean alreadyInitialized = false;

          // If it's request scope, simply create a new instance.
          if (scope.equalsIgnoreCase("request")) {
            log.info("Creating request-scoped object named " + name);
            Class clazz = Class.forName(dc.getClassName());
            o = clazz.newInstance();
          } else if (scope.equalsIgnoreCase("session")) {
            // If it's session scope, see if there is already an instance and
            // get a reference to it if so.
            if (ses != null) {
              o = ses.getAttribute(name);
              // Calculate how old the existing object is, if there is one.
              long age = 0;
              if (o != null) {
                age = (new GregorianCalendar().getTimeInMillis() -
                  ((Long)ses.getAttribute("jwpdf_" + name +
                  "_createdtime_millis")).longValue()) / 1000;
              }
              // If there isn't an existing object instance, or if the existing
              // instance is older than maxAge, create a new one.  Note that a
              // maxAge of 0 indicates the object does not expire.
              if (o == null || (dc.getMaxAge() > 0 && age > dc.getMaxAge())) {
                log.info("Creating session-scoped object named " + name);
                Class clazz = Class.forName(dc.getClassName());
                o = clazz.newInstance();
                ses.setAttribute("jwpdf_" + name + "_createdtime_millis",
                  new Long(new GregorianCalendar().getTimeInMillis()));
              } else {
                // We don't want to initialize the object twice, and that can
                // only happen if it is session-scoped and already was
                // created and it's age didn't exceed maxAge, so in this case
                // only we set a flag so we'll skip initialization below.
                log.debug("Object named " + name + " already in session and " +
                  "initialized");
                alreadyInitialized = true;
              }
            } else {
              // If session if null, we can't inject this dependency.
              log.debug("Session was null, object named " + name + " cannot " +
                "be retrieved or created");
            }
          }

          // Now we'll either have a reference to a valid object, or we'll have
          // a null reference if it was session-scoped and there was no session.
          // If not null, now we need to deal with initialization.
          if (o != null && !alreadyInitialized) {
            log.info("Initializing object named " + name);
            Iterator it;

            // First, the simple properties are initialized.
            for (it = dc.getInitProps().iterator(); it.hasNext();) {
              HashMap hm     = (HashMap)it.next();
              String  pName  = (String)hm.get("name");
              String  pValue = (String)hm.get("value");
              log.debug("Initializing simple property: " + pName + "=" +
                        pValue + " of object named " + name);
              BeanUtils.setProperty(o, pName, pValue);
            }

            // Next, the List properties are initialized.
            for (it = dc.getInitLists().iterator(); it.hasNext();) {
              HashMap hm         = (HashMap)it.next();
              String  lMethod    = (String)hm.get("method");
              String  lName      = (String)hm.get("name");
              String  lValue     = (String)hm.get("value");
              if (lMethod == null) {
                lMethod = "set" + lName.substring(0, 1).toUpperCase() +
                                  lName.substring(1);
              }
              log.debug("Initializing List property: " + lName + " += " +
                        lValue + " of object named " + name + " via call " +
                        "to method " + lMethod + "()");
              MethodUtils.invokeExactMethod(o, lMethod, lValue);
            }

            // Next, the Array properties are initialized.
            String    arrayName = null;
            ArrayList nextAL    = new ArrayList();
            for (it = dc.getInitArrays().iterator(); it.hasNext();) {
              HashMap hm         = (HashMap)it.next();
              String  aName      = (String)hm.get("name");
              String  aValue     = (String)hm.get("value");
              if (arrayName == null) {
                // If this is the first time through, we need to be sure and
                // set the arrayName.
                arrayName = aName;
              }
              if (aName.equals(arrayName)) {
                // We're still building the next array, so just add this value.
                nextAL.add(aValue);
              } else {
                // We've just encountered a new array name, and the arrayName
                // variable was NOT null, so we need to now set this array
                // in the object.
                log.debug("Initializing Array property: " + aName +
                          " of object named " + name);
                Class type = PropertyUtils.getPropertyType(o, aName);
                Object arr = Array.newInstance(type, nextAL.size());
                Converter converter = ConvertUtils.lookup(type);
                for (int i = 0; i < nextAL.size(); i++) {
                  Object value = converter.convert(type, nextAL.get(i));
                  Array.set(arr, i, value);
                }
                PropertyUtils.setProperty(o, aName, arr);
                // And lastly, we need to set up to continue building the
                // next array (which is the one we now encountered that
                // triggered setting the last one).
                nextAL = new ArrayList();
                arrayName = aName;
                nextAL.add(aValue);
              }
            }
            // One final (*slightly* important) step... we have an array
            // constructed at this point that needs to be set because the last
            // elemet would not have triggered the add block above, so we have
            // to do it now.  However, we have to be sure arrayName is not null,
            // which would be the case for an object that didn't have any array
            // initializations at all.
            if (arrayName != null) {
              String aName = arrayName;
                      log.debug("Initializing Array property: " + aName +
                                " of object named " + name);
              Class type = PropertyUtils.getPropertyType(o, aName);
              Object arr = Array.newInstance(type, nextAL.size());
              Converter converter = ConvertUtils.lookup(type);
              for (int i = 0; i < nextAL.size(); i++) {
                Object value = converter.convert(type, nextAL.get(i));
                Array.set(arr, i, value);
              }
              PropertyUtils.setProperty(o, aName, arr);
            }

            // Next, the Map properties are initialized.
            for (it = dc.getInitMaps().iterator(); it.hasNext();) {
              HashMap hm         = (HashMap)it.next();
              String  mMethod    = (String)hm.get("method");
              String  mName      = (String)hm.get("name");
              String  mKey       = (String)hm.get("key");
              String  mValue     = (String)hm.get("value");
              if (mMethod == null) {
                mMethod = "set" + mName.substring(0, 1).toUpperCase() +
                                  mName.substring(1);
              }
              log.debug("Initializing Map property: " + mName + " += " +
                        mKey + "=" + mValue + " of object named " + name +
                        " via call " + "to method " + mMethod + "()");
              Object[] args = {mKey, mValue};
              MethodUtils.invokeExactMethod(o, mMethod, args);
            }

            // Next, the init class/method is called, if any
            if (dc.getInitClass() != null && dc.getInitMethod() != null) {
              Class  clazz = Class.forName(dc.getInitClass());
              Object ic    = clazz.newInstance();
              log.debug("Calling method " + dc.getInitMethod() + "() of init " +
                        "class " + dc.getInitClass() + " for object named " +
                        name);
              MethodUtils.invokeExactMethod(ic, dc.getInitMethod(), o);
            }

            // Finally, execution of any declared method calls are made.
            for (it = dc.getMethodCalls().iterator(); it.hasNext();) {
              HashMap   hm    = (HashMap)it.next();
              String    mName = (String)hm.get("name");
              ArrayList al    = (ArrayList)hm.get("arguments");
              log.debug("Executing method " + mName + " of object named " +
                        name);
              MethodUtils.invokeExactMethod(o, mName, al.toArray());
            }

            // At this point the object is all initialized.  All that's left to
            // do is put it in the appropriate scope.
            if (scope.equalsIgnoreCase("request")) {
              log.debug("Putting object named " + name + " in request scope");
              req.setAttribute(name, o);
            } else if (scope.equalsIgnoreCase("session")) {
              if (ses != null) {
                log.debug("Putting object named " + name + " in session scope");
                ses.setAttribute(name, o);
              }
            }

          } // End if (o != null).

        } else {

          log.info("Request-scope dependency path not matched, so skipped");

        } // End check if requeat path matches dependency list.

      } // End dependencyConfigs iterator.

    } catch (NoSuchMethodException nsme) {
      nsme.printStackTrace();
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    } catch (InstantiationException ie) {
      ie.printStackTrace();
    } catch (IllegalAccessException iae) {
      iae.printStackTrace();
    } catch (InvocationTargetException ite) {
      ite.printStackTrace();
    } finally {
      filterChain.doFilter(request, response);
    }

  } // End doFilter().


  /**
   * This method is called by client code to get a reference to a particular
   * created object.  The caller does not need to specify or even necessarily
   * care where the object is (request or session).
   *
   * @param  name    The name of the object as specified in the config file.
   * @param  request The current ServletRequest instance.
   * @return         Reference to the specified object, or null if not present,
   *                 or if object was in session but session was null.
   */
  public static Object getDependency(String name, ServletRequest request) {

    Object o = null;
    if (name != null && request != null) {
      HttpServletRequest req = (HttpServletRequest)request;
      DependencyConfig dc = (DependencyConfig)dependencyConfigs.get(name);
      if (dc != null) {
        log.debug("Retrieving object named " + name + " from scope " +
                  dc.getScope());
        String scope = dc.getScope();
        if (scope.equalsIgnoreCase("request")) {
          o = req.getAttribute(name);
        } else if (scope.equalsIgnoreCase("session")) {
          HttpSession ses = req.getSession(false);
          if (ses != null) {
            o = ses.getAttribute(name);
          }
        }
      } else {
        log.debug("Couldn't find dependency config for object named " + name);
      }
    }
    if (o == null) {
      log.debug("Dependency object named " + name + " was not found, " +
                "returning null to caller");
    }
    return o;

  } // End getDependency().


  /**
   * This method is called by client code to update a dependent object.  In
   * other words, if a caller gets a reference to a bean in session, makes
   * some changes to it and wants to save it again, they can call this
   * method.  This way, the caller doesn't have to know or care whether the
   * object was in request or session.
   *
   * @param  inName    The name of the object as specified in the config file.
   * @param  inObj     The object to store.
   * @param  inRequest Current request being services.
   */
  public static void updateDependency(String inName, Object inObj,
                                      ServletRequest inRequest) {

      if (inName != null) {
        log.debug("Updating dependency named " + inName);
        DependencyConfig dc = (DependencyConfig)dependencyConfigs.get(inName);
        if (dc != null) {
          HttpServletRequest req = (HttpServletRequest)inRequest;
          String scope = dc.getScope();
          log.debug("Scope = " + scope);
          if (scope.equalsIgnoreCase("request")) {
            req.setAttribute(inName, inObj);
          } else if (scope.equalsIgnoreCase("session")) {
            HttpSession ses = req.getSession(false);
            if (ses != null) {
              ses.setAttribute(inName, inObj);
            }
          }
        } else {
          log.debug("Couldn't find dependency config for object named " +
                    inName);
        }
      } else {
        log.debug("name passed in was null, so not doing anything");
      }

  } // End updateDependency().


} // End class.
