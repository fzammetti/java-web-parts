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


import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javawebparts.core.org.apache.commons.digester.Digester;
import javawebparts.core.org.apache.commons.digester.ExtendedBaseRules;
import org.xml.sax.SAXException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class is the top of the hierarchy, and all client application
 * interaction with the Chain implementation should occur through this class.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class ChainManager {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javawebparts.core.org.apache.commons.digester.Digester");
      Class.forName(
        "javawebparts.core.org.apache.commons.digester.ExtendedBaseRules");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("ChainManager" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * The default name of the configuration file.
   */
  public static final String DEFAULT_FILE_NAME = "/chain_config.xml";


  /**
   * When this class is asked to execute a chain, it will be passed a string
   * in the form CatalogID/ChainID.  The getCatalogOrChainID() takes a string
   * in this form and returns either the Catalog ID or Chain ID, depending on
   * the flag passed in.  This field is the value of the flag that specified
   * the caller wants the Catalog ID.
   */
  public static final int CATALOG_ID = 1;


  /**
   * When this class is asked to execute a chain, it will be passed a string
   * in the form CatalogID/ChainID.  The getCatalogOrChainID() takes a string
   * in this form and returns either the Catalog ID or Chain ID, depending on
   * the flag passed in.  This field is the value of the flag that specified
   * the caller wants the Chain ID.
   */
  public static final int CHAIN_ID = 2;


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(ChainManager.class);


  /**
   * The collection of Catalogs.
   */
  private static Map catalogs = new HashMap();


  /**
   * Flag to determine if initialization has taken place.
   */
  private static boolean configured;


  /**
   * Constructor.  Call ChainManager(null), where null is the configuration
   * file name (meaning either use the default name, or an overriding
   * environment variable, if present).
   */
  public ChainManager() {

    this(null);

  } // End ChainManager().


  /**
   * Constructor.  This constructor accepts a configuration file name.  The way
   * it works is this: If a non-null filename is passed in, use that.  If null
   * is passed in, look for the environment variable and use it if present.
   * If null is passed in and the environment variable is not found, use the
   * default name.
   *
   * @param inConfigFilename Name (and path technically) to the config file.
   */
  public ChainManager(String inConfigFilename) {

    log.info("ChainManager...");
    if (!configured) {

      String configFilename = inConfigFilename;
      if (configFilename == null) {
        // See if there is an environment variable named
        // JWP_CHAIN_CONFIG_FILE_NAME.  If there is, use that.
        String evFilename = System.getProperty("JWP_CHAIN_CONFIG_FILE_NAME");
        if (evFilename != null) {
          configFilename = evFilename;
        }
      }
      if (configFilename == null) {
        configFilename = DEFAULT_FILE_NAME;
      }

      Digester digester = new Digester();
      digester.setRules(new ExtendedBaseRules());
      digester.setValidating(false);

      // Define our Digester parse rules.

      // Create a Catalog object.
      digester.addObjectCreate("chainConfig/catalog",
        "javawebparts.misc.chain.Catalog");

      // Set the properties of the object corresponding to the attributes
      // of the <catalog> element in the config file.
      digester.addSetProperties("chainConfig/catalog");

      // Create a Chain object.
      digester.addObjectCreate("chainConfig/catalog/chain",
        "javawebparts.misc.chain.Chain");

      // Set the properties of the object corresponding to the attributes
      // of the <chain> element in the config file.
      digester.addSetProperties("chainConfig/catalog/chain");

      // Create a CommandConfig object.
      digester.addObjectCreate("chainConfig/catalog/chain/command",
        "javawebparts.misc.chain.CommandConfig");

      // Set the properties of the object corresponding to the attributes
      // of the <command> element in the config file.
      digester.addSetProperties("chainConfig/catalog/chain/command");

      // For any <property> elements nested under a <command> element,
      // set the property on the CommandConfig object on the top of the
      // stack.
      digester.addCallMethod("chainConfig/catalog/chain/command/property",
        "addProperty", 2);
      digester.addCallParam("chainConfig/catalog/chain/command/property", 0,
        "id");
      digester.addCallParam("chainConfig/catalog/chain/command/property", 1,
        "value");

      // When the CommandConfig object has been all configured, we need to
      // add it to the collection of CommandConfig objects maintained by
      // the Chain object on the top of the stack.
      digester.addSetNext("chainConfig/catalog/chain/command",
        "addCommand");

      // When the Catalog object has been all configured, we need to
      // add it to the collection of Chain objects maintained by
      // the Catalog object on the top of the stack.
      digester.addSetNext("chainConfig/catalog/chain", "addChain");

      // When the Catalog object has been all configured, we need to
      // add it to the collection of Catalog objects maintained by
      // this ChainManager class.
      digester.addSetNext("chainConfig/catalog", "addCatalog");

      ClassLoader     loader = Thread.currentThread().getContextClassLoader();
      StringTokenizer st     = new StringTokenizer(configFilename, ",");
      while (st.hasMoreTokens()) {
        String configFile = st.nextToken();
        log.info("Reading Chain config file " + configFile);
        InputStream stream = null;
        digester.push(this);
        try {
          stream = loader.getResourceAsStream(configFile);
          digester.parse(stream);
        } catch (IOException ioe) {
          log.error("Exception reading config file: " + ioe);
        } catch (SAXException se) {
          log.error("Exception parsing config file: " + se);
        } finally {
          if (stream != null) {
            try {
              stream.close();
            } catch (IOException ioe) {
              log.error("Exception closing config file stream: " + ioe);
            }
          }
        }
      }

      // Display configured object graph.
      log.info(catalogs);
      log.info("ChainManager configured");
      configured = true;

    } else {

      log.info("ChainManager already configured");

    }

  } // End Constructor.


  /**
   * Adds a Catalog to the collection of Catalogs.
   *
   * @param inCatalog The Catalog instance to add.
   */
  public void addCatalog(Catalog inCatalog) {

    catalogs.put(inCatalog.getId(), inCatalog);

  } // End addCatalog().


  /**
   * Looks up and returns a named Catalog.
   *
   * @param catalogID The ID of the Catalog to find.
   * @return          The Catalog instance, or null if not found.
   */
  public static Catalog findCatalog(String catalogID) {

    return (Catalog)catalogs.get(catalogID);

  } // End findCatalog().


  /**
   * Returns a new ChainContext instance.
   *
   * @return A new ChainContext instance.
   */
  public ChainContext createContext() {

    return new ChainContext();

  } // End createContext().


  /**
   * Executes a named Chain from a named Catalog.
   *
   * @param catalogChainID The ID of the Catalog/Chain to execute.
   * @param chainContext   The ChainContext instance this execution will use.
   */
  public void executeChain(String catalogChainID,
    ChainContext chainContext) {

    log.info("Getting ready to execute chain...");
    if (log.isDebugEnabled()) {
      log.debug("Incoming ChainContext: " + chainContext);
    }

    // The catalogChainID was presumably in the form CatalogID/ChainID.
    // So the first thing we need to do is get both constituent pieces.  If
    // the value was not in that form, chain terminates as FAILED.
    String catalogID = getCatalogOrChainID(catalogChainID, CATALOG_ID);
    String chainID   = getCatalogOrChainID(catalogChainID, CHAIN_ID);
    if (catalogID == null || chainID == null) {
      log.error("Chain execution failed because the specified ID " +
        "was not in the form CatalogID/ChainID");
        chainContext.setResult(new Result(Result.FAIL));
      return;
    }

    // Set the CatalogID and ChainID in the context and re-display the context.
    chainContext.setCatalogId(catalogID);
    chainContext.setChainId(chainID);
    if (log.isDebugEnabled()) {
      log.debug("ChainContext before chain began: " + chainContext);
    }

    // Now get the appropriate Catalog, FAIL the Chain if not found.
    Catalog catalog = findCatalog(catalogID);
    if (catalog == null) {
      log.error("Chain execution failed because the Catalog " +
        catalogID + " could not be found");
      chainContext.setResult(new Result(Result.FAIL));
      return;
    }
    if (log.isDebugEnabled()) {
      log.debug("Using Catalog: " + catalog);
    }

    // Now get the appropriate Chain, FAIL the Chain if not found.
    Chain chain = catalog.findChain(chainID);
    if (chain == null) {
      log.error("Chain execution failed because the Chain " +
        chainID + " could not be found");
      chainContext.setResult(new Result(Result.FAIL));
      return;
    }
    log.info("Using Chain: " + chain);

    // Execute the chain
    log.info("Beginning chain '" + chainID + "'...");
    Result chainResult = chain.execute(this, chainContext);

    // Store the result in context, display some final status messages and
    // we're done here.
    chainContext.setResult(chainResult);
    if (log.isDebugEnabled()) {
      log.debug("ChainContext after chain ended: " + chainContext);
    }
    log.info("Chain execution complete");

  } // End executeChain().


  /**
   * Returns either the Catalog ID or Chain ID from a string in the form
   * "xxxx/yyyy", where xxxx is the Catalog ID and yyyy is the Chain ID.  Which
   * part is returned is specified by the inWhich flag.
   *
   * @param catalogChainID The Catalog/Chain ID to break up.
   * @param which          Which part to return Catalog ID or Chain ID.
   * @return               The Catalog ID or Chain ID, according to which.
   */
  public static String getCatalogOrChainID(String catalogChainID, int which) {

    String retVal = null;
    // If there was no / in the passed in string, we don't want to do anything
    // and we want to return null.  But if it *IS* found, go ahead and parse.
    if (catalogChainID.indexOf("/") != -1) {
      StringTokenizer st = new StringTokenizer(catalogChainID, "/");
      String catalogID = st.nextToken();
      String chainID = st.nextToken();
      if (which == CATALOG_ID) {
        retVal = catalogID;
      }
      if (which == CHAIN_ID) {
        retVal = chainID;
      }
    }
    return retVal;

  } // End getCatalogOrChainID().


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