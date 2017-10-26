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


import java.io.InputStream;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javawebparts.core.org.apache.commons.digester.Digester;
import javawebparts.core.org.apache.commons.digester.ExtendedBaseRules;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;


/**
 * Most applications of even modest complexity require configuration
 * information.  A common method of providing this information is via XML
 * file.  Often times a developer will want to expose this information via
 * a simple Javabean throughout the rest of the application.  This
 * ServletContextListener provides a simple way to read in an XML configuration
 * file and populate a bean, either one the developer creates or a simple
 * "default" bean.
 * <br><br>
 * Init parameters are:
 * <br>
 * <ul>
 * <li><b>configFile</b> - The context-relative path to the XML configuration
 * file for the context.  This is the only required parameter, but
 * there are some limitations if it is all you supply.  See
 * the comments after this parameter list for those details.
 * Required: yes.  Default: none.</li>
 * <br><br>
 * <li><b>rootElement</b> - Defines what the root element of the config file is.
 * If none is specified, &lt;config&gt; is used by default.  Required: no.
 * Default: config.</li>
 * <br><br>
 * <li><b>configClass</b> - This parameter names the class that will act as the
 * configuration information holder.  Typically, this class will expose
 * getters and setters for each element in the configuration file and store all
 * the data in static members.  In other words, something like this is typical:
 * <br><br>
 * <u>For an XML config file like this:</u><br>
 * &lt;config&gt;<br>
 * &nbsp;&nbsp;&lt;firstName&gt;Frank&lt;/firstName&gt;<br>
 * &lt;/config&gt;<br>
 * <br>
 * <u>The following bean could be used:</u><br>
 * public class MyConfigBean {<br>
 * &nbsp;&nbsp;private static String firstName;<br>
 * &nbsp;&nbsp;public void setFirstName(String inFirstName) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;firstName = inFirstName;<br>
 * &nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;public String getFirstName() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;return firstName;<br>
 * &nbsp;&nbsp;}<br>
 * }<br>
 * <br>
 * If this parameter is not present, the AppConfig
 * class will be used.  The AppConfig class defines a single
 * String get(String key) method for your use, whereas a custom class can
 * expose real setters and getters for each element, or even more complex
 * constructs if required (i.e., maybe one of your parameters is a
 * comma-separated list, and you want your setter to parse it into a List).
 * Required: no.  Default: AppConfig class.</li>
 * <br><br>
 * <li><b>rulesetClass</b> - When a custom configClass is used, the developer
 * can also create a class that sets Commons Digester rules.  This
 * allows for parsing of more complex XML files.  This is only applicable when
 * configClass is used, and even then is optional.  If a rulesetClass is NOT
 * defined, then the config file must follow the same rules as when a custom
 * appConfig class is not used, namely that the root element must be
 * &lt;config&gt; and it must be "flat" (i.e., all elements must be direct
 * children of &lt;config&gt;).  Required: no. Default: none.</li>
 * </ul>
 * Three "modes" of operation are suppported and you can think of them as
 * Basic, Intermediate and Advanced.  The Basic configuration requires that
 * you specify the configFile parameter only.  In this mode, the AppConfig
 * object will be used and the root element of the config file MUST be
 * "config".  The config file must be "flat", that is, elements can only be
 * nested under the root, not under any other child nodes of root. In
 * Intermediate mode, you will specify the configFile, as well
 * as the root element and the configClass.  Your class must expose setters
 * for each element of the config file.  In Advanced mode, you will also
 * specify a class that sets Commons Digester rules to use to parse the XML.
 * This is good if you have a more complex structure that you want to support
 * (i.e., repeating elements for a collection or multi-level nesting).
 * <br><br>
 * Example configuration in web.xml:
 * <br><br>
 * &lt;context-param&gt;<br>
 * &nbsp;&nbsp;&lt;param-name&gt;configFile&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&lt;param-value&gt;/WEB-INF/app_config.xml&lt;
 * /param-value&gt;&lt;/context-param&gt;<br>
 * &lt;context-param&gt;<br>
 * &nbsp;&nbsp;&lt;param-name&gt;rootElement&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&lt;param-value&gt;config&lt;/param-value&gt;
 * &lt;/context-param&gt;<br>
 * &lt;context-param&gt;<br>
 * &nbsp;&nbsp;&lt;param-name&gt;configClass&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&lt;param-value&gt;javawebparts.sampleapp.
 * SampleAppConfigBean&lt;/param-value&gt;<br>
 * &lt;/context-param&gt;<br>
 * &lt;context-param&gt;<br>
 * &nbsp;&nbsp;&lt;param-name&gt;AjaxPartsTaglibConfig&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&lt;param-value&gt;/WEB-INF/ajax_config.xml&lt;/param-value&gt;
 * <br>&lt;/context-param&gt;
 * <br><br>
 * &lt;listener&gt;<br>
 * &nbsp;&nbsp;&lt;listener-class&gt;javawebparts.listener.
 * AppConfigContextListener&lt;/listener-class&gt;<br>
 * &lt;/listener&gt;
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class AppConfigContextListener implements ServletContextListener {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javax.servlet.ServletContext");
      Class.forName("javax.servlet.ServletContextEvent");
      Class.forName("javax.servlet.ServletContextListener");
      Class.forName("javawebparts.core.org.apache.commons.digester.Digester");
      Class.forName(
        "javawebparts.core.org.apache.commons.digester.ExtendedBaseRules");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("AppConfigContextListener" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(AppConfigContextListener.class);


  /**
   * Path to the config file.
   */
  private String configFile;


  /**
   * The root element of the config file.
   */
  private String rootElement;


  /**
   * The class that holds our config information.
   */
  private String configClass;


  /**
   * The class that is called to configure the Digester ruleset.
   */
  private String rulesetClass;


  /**
   * The container calls this when the context is initialzed.
   *
   * @param sce ServletContextEvent instance reference
   */
  public void contextInitialized(ServletContextEvent sce) {

    log.info("init() started");

    ServletContext sc = sce.getServletContext();

    // Get the path to the config file.
    configFile  = sc.getInitParameter("configFile");
    if (configFile == null) {
      String es = "AppConfigContextListener could not initialize " +
                  "because mandatory configFile init " +
                  "parameter was not found";
      log.error(getClass().getName() + ".init(): " + es);
      throw new IllegalArgumentException(es);
    }
    log.info("configFile = " + configFile);

    // Get the document root element, default if not present.
    rootElement = sc.getInitParameter("rootElement");
    if (rootElement == null) {
      rootElement = "config";
    }
    log.info("rootElement = " + rootElement);

    // Get the config class to use.
    configClass = sc.getInitParameter("configClass");
    log.info("configClass = " + configClass);

    // Get the ruleset class to use.
    rulesetClass = sc.getInitParameter("rulesetClass");
    log.info("rulesetClass = " + rulesetClass);

    // Now use Digester to populate our config object.
    Digester digester = new Digester();
    digester.setRules(new ExtendedBaseRules()); // For wildcard support.
    digester.setValidating(false); // This could be overriden by custom rules.
    digester.setLogger(log);

    try {

      // Using the default class, which means there is no possibility of
      // a custom ruleset, so it's pretty straight-forward here.
      if (configClass == null) {

        // Instantiate default class and push on stack.
        AppConfig ac = new AppConfig();
        ac.setDigester(digester);
        digester.push(ac);
        // Set the appropriate rules.
        digester.addCallMethod(rootElement + "/?", "set", 0);
        // Do the actual work.
        InputStream isConfigFile = sc.getResourceAsStream(configFile);
        ac = (AppConfig)digester.parse(isConfigFile);
        log.info("Config Object = " + ac);

      // Using a custom class, and maybe a custom ruleset too.
      } else {

        if (rulesetClass == null) {
          digester.addObjectCreate(rootElement, configClass);
          digester.addBeanPropertySetter(rootElement + "/?");
        } else {
          // We're using a custom ruleset too, so instantiate the named class,
          // check its type and call the setRules() method.
          try {
            Class clazz = Class.forName(rulesetClass);
            AppConfigRuleset rc = (AppConfigRuleset)clazz.newInstance();
            rc.setRules(digester, rootElement, configClass);
          } catch (ClassNotFoundException cnfe) {
            log.error(cnfe);
            throw new RuntimeException(cnfe);
          } catch (InstantiationException ie) {
            log.error(ie);
            throw new RuntimeException(ie);
          } catch (IllegalAccessException iae) {
            log.error(iae);
            throw new RuntimeException(iae);
          }
        } // End if (rulesetClass == null).
        // Do the actual work.
        InputStream isConfigFile = sc.getResourceAsStream(configFile);
        digester.parse(isConfigFile);
        log.info("Custom class populated with configuration information");

      } // End if (configClass == null).

    } catch (IOException ioe) {
      log.error(ioe);
    } catch (SAXException saxe) {
      log.error(saxe);
    }

    log.info("init() completed");

  } // End init().


  /**
   * Doesn't do anything, but needed to be here.
   *
   * @param sce ServletContextEvent instance reference
   */
  public void contextDestroyed(ServletContextEvent sce) { }


} // End class.
