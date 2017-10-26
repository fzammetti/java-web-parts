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
import java.util.ArrayList;
import java.util.Map;
import java.lang.reflect.InvocationTargetException;
import javawebparts.core.org.apache.commons.beanutils.BeanUtils;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class represents a Chain.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class Chain {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javawebparts.core.org.apache.commons.beanutils.BeanUtils");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("Chain" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(Chain.class);


  /**
   * The collection of Commands that belong to this Chain.
   */
  private List commands = new ArrayList();


  /**
   * The index of the currently executing Command in this Chain.
   */
  private int executionIndex;


  /**
   * The ID of this Chain.
   */
  private String id = "";


  /**
   * The ID of the Catalog this Catalog extends, if any
   */
  private String extendsID;


  /**
   * Constructor.  Calls the other constructor.
   */
  public Chain() {

    this(null, null);

  } // End Chain().


  /**
   * Constructor.
   * @param inID        The ID of this Chain.
   * @param inExtendsID The ID of the Chain this one extends.
   */
  public Chain(String inID, String inExtendsID) {

    id = inID;
    setExtendsID(inExtendsID);

  } // End Chain().


  /**
   * Returns a clone of the commands collection for this Chain.
   *
   * @return The collection of Commands cloned from this Chain.
   */
  public List cloneCommands() {

    List clonedCommands = new ArrayList();
    for (Iterator it = commands.iterator(); it.hasNext();) {
      CommandConfig originalCommandConfig = (CommandConfig)it.next();
      CommandConfig clonedCommandConfig =
        new CommandConfig(originalCommandConfig.getId(),
          originalCommandConfig.getClassName(),
          originalCommandConfig.getChain(),
          originalCommandConfig.getReplaceID());
        clonedCommandConfig.setProperties(
          originalCommandConfig.cloneProperties());
      clonedCommands.add(clonedCommandConfig);
    }
    return clonedCommands;

  } // End cloneCommands().



  /**
   * Sets the ID of this Chain.
   *
   * @param inID The ID of this Chain.
   */
  public void setId(String inID) {

    id = inID;

  } // End setId().


  /**
   * Gets the ID of this Chain.
   *
   * @return The ID of this Chain.
   */
  public String getId() {

    return id;

  } // End getId().


  /**
   * Sets the ID of the Chain that this Chain extends.
   *
   * @param inExtendsID The ID of the Chain this one extends.
   */
  public void setExtendsID(String inExtendsID) {

    extendsID = inExtendsID;

    // Deal with the case of this Chain extending another, if applicable.
    String catalogID = null;
    String chainID = null;
    if (extendsID != null) {
      // This Chain extends another, so get the Catalog the base Chain is from.
      catalogID = ChainManager.getCatalogOrChainID(inExtendsID,
        ChainManager.CATALOG_ID);
      chainID   = ChainManager.getCatalogOrChainID(inExtendsID,
        ChainManager.CHAIN_ID);
      if (catalogID == null ||  chainID == null) {
        log.error("extends attribute of chain element must be " +
          "in the form CatalogID/ChainID");
      } else {
        // Now get a reference to the base Chain.
        Catalog catalog = ChainManager.findCatalog(catalogID);
        Chain     chain = catalog.findChain(chainID);
        commands = chain.cloneCommands();
      }
    }

  } // End setExtendsID().


  /**
   * Gets the ID of the Chain that this Chain extends.
   *
   * @return The ID of the Chain this one extends.
   */
  public String getExtendsID() {

    return extendsID;

  } // End getExtendsID().


  /**
   * Adds a Command to this Chain.
   *
   * @param inCommand The CommandConfig to add to this Chain.
   */
  public void addCommand(CommandConfig inCommand) {

    String replaceID = inCommand.getReplaceID();
    if (replaceID == null) {
      // This Command doesn't replace any, so just add it to the end.
      commands.add(inCommand);
    } else {
      // This Command replaces another, so find it and replace it.
      int i = 0;
      for (Iterator it = commands.iterator(); it.hasNext();) {
        CommandConfig commandConfig = (CommandConfig)it.next();
        if (replaceID.equals(commandConfig.getId())) {
          commands.set(i, inCommand);
          break;
        }
        i++;
      }
    }

  } // End addCommand().


  /**
   * Sets the entire commands collection at once.
   *
   * @param inCommands The collection of CommandConfigs to add to this Chain.
   */
  public void setCommands(List inCommands) {

    commands = inCommands;

  } // End setCommands().


  /**
   * Executes the Chain.
   *
   * @param chainManager The ChainManager instance executing this Chain.
   * @param chainContext The ChainContext instance being used by this Chain.
   * @return             Result instance.
   */
  public Result execute(ChainManager chainManager,
    ChainContext chainContext) {

    // theResult will be returned no matter what, and we always assume the
    // Chain will succeed initially.
    Result theResult = new Result(Result.SUCCESS);

    try {

      // Loop through the Commands in this Chain.
      executionIndex = 0;
      chainLoop: while (executionIndex < commands.size()) {

        // Add general Chain execution info to chainContext in case a
        // Command needs it.
        String prefix = chainContext.getCatalogId() + "_" +
          chainContext.getChainId() + "_";
        chainContext.setAttribute(prefix + "executionIndex",
          new Integer(executionIndex));

        // Get the config for the next Command in the chain.
        CommandConfig commandConfig =
          (CommandConfig)commands.get(executionIndex);
        log.info("Next Command to execute config = " + commandConfig);

        // Determine if its a Command or another Chain and branch accordingly...
        if (commandConfig.getClassName() == null) {

          // It's a Chain...
          String chainID = commandConfig.getChain();
          if (chainID.indexOf("/") == -1) {
            // The specified chain ID did not include a Catalog ID, so the
            // current catalog will be appended as we assume that's where
            // the Chain is.
            chainID = chainContext.getCatalogId() + "/" + chainID;
          }
          chainManager.executeChain(chainID, chainContext);
          theResult = chainContext.getResult();
          // To restart the Chain, set executionIndex to -1 because when it is
          // incremented next, it will be 0, and the Chain starts over.
          if (theResult.getCode() == Result.RESTART_CHAIN) {
            executionIndex = -1;
            chainContext.setAttribute(prefix + "executionIndex",
              new Integer(executionIndex));
          }
          // To redo the Command (which could be a Chain), decrement
          // executionIndex so that when it is incremented next, it will be
          // again what it is now.
          if (theResult.getCode() == Result.REDO_COMMAND) {
            executionIndex--;
            chainContext.setAttribute(prefix + "executionIndex",
              new Integer(executionIndex));
          }
          // Fail or Abort, just return that result, and propogate extra info.
          if (theResult.getCode() == Result.FAIL ||
            theResult.getCode() == Result.ABORT) {
            break chainLoop;
          }

        } else {

          // It's a Command...
          // Instantiate the configured Command class.
          String  className = commandConfig.getClassName();
          Class   clazz     = Class.forName(className);
          Command command   = (Command)clazz.newInstance();

          // Go through and set all properties defined for this Command.
          for (Iterator it = commandConfig.getProperties().iterator();
            it.hasNext();) {
            Map    m     = (Map)it.next();
            String name  = (String)m.get("name");
            String value = (String)m.get("value");
            BeanUtils.setProperty(command, name, value);
          }

          // Call init() on Command, FAIL or ABORT Chain if applicable.
          theResult = command.init(chainContext);
          if (theResult.getCode() == Result.FAIL ||
            theResult.getCode() == Result.ABORT) {
            break chainLoop;
          }

          // Execute the Commend and get the result.
          theResult = command.execute(chainContext);

          // To restart the Chain, set executionIndex to -1 because when it is
          // incremented next, it will be 0, and the Chain starts over.
          if (theResult.getCode() == Result.RESTART_CHAIN) {
            executionIndex = -1;
            chainContext.setAttribute(prefix + "executionIndex",
              new Integer(executionIndex));
          }

          // To redo the Command (which could be a Chain), decrement
          // executionIndex so that when it is incremented next, it will be
          // again what it is now.
          if (theResult.getCode() == Result.REDO_COMMAND) {
            executionIndex--;
            chainContext.setAttribute(prefix + "executionIndex",
              new Integer(executionIndex));
          }

          // To jump to a Command we need to find the ID returned and get
          // its index in this Chain, then set the executionIndex to that.
          // If the Command can't be found, or one wasn't returned from the
          // Command reguesting the jump, we FAIL the Chain.
          if (theResult.getCode() == Result.JUMP_TO_COMMAND) {
            String targetCommand = theResult.getTargetCommand();
            if (targetCommand == null) {
              log.error("JUMP_TO_COMMAND returned but targetCommand was " +
                "null, so cannot perform jump, Chain execution will FAIL.");
              command.cleanup(chainContext);
              theResult = new Result(Result.FAIL, "JUMP_TO_COMMAND requested " +
                "but target command was not set by Command");
              break chainLoop;
            }
            int     newIndex = 0;
            boolean foundIt  = false;
            while (newIndex < commands.size()) {
              CommandConfig findCC = (CommandConfig)commands.get(newIndex);
              if (targetCommand.equals(findCC.getId())) {
                foundIt = true;
                // Remember that the increment of executionIndex is going to
                // happen next, so to get to the right Command we need to
                // subtract 1 from what would be the correct value here.
                executionIndex = newIndex - 1;
                chainContext.setAttribute(prefix + "executionIndex",
                  new Integer(executionIndex));
                if (log.isDebugEnabled()) {
                  log.debug("JUMP_TO_COMMAND targetCommand = " + targetCommand +
                    " found, jumping to index " + newIndex + "...");
                }
                break;
              }
              newIndex++;
            }
            if (!foundIt) {
              log.error("JUMP_TO_COMMAND targetCommand = " + targetCommand +
                " *NOT* found, Chain execution will FAIL.");
              theResult = new Result(Result.FAIL, "JUMP_TO_COMMAND requested " +
                "but target command '" + targetCommand + "' could not be " +
                "found in Chain");
              break chainLoop;
            }
          }

          // If FAIL or ABORT, return that result.
          if (theResult.getCode() == Result.FAIL ||
            theResult.getCode() == Result.ABORT) {
            // Still need to call cleanup(), but we'll ignore the result in
            // this case since we already know we're stopping the Chain.
            command.cleanup(chainContext);
            break chainLoop;
          }

          // Call cleanup() on Command, FAIL or ABORT Chain if applicable.
          theResult = command.cleanup(chainContext);
          if (theResult.getCode() == Result.FAIL ||
            theResult.getCode() == Result.ABORT) {
            break chainLoop;
          }

        } // End Command-Chain branch if.

        // Move on to the next Command in the Chain.
        executionIndex = ((Integer)chainContext.getAttribute(prefix +
          "executionIndex")).intValue();
        executionIndex++;

      } // End while loop.

    // If any exceptions occur, return a Result instance with some details
    // in the extraInfo.
    } catch (ClassNotFoundException cnfe) {
      log.error("Chain execution FAILED due to Exception - " + cnfe);
      theResult = new Result(Result.FAIL, "ClassNotFoundException");
    } catch (InstantiationException ie) {
      log.error("Chain execution FAILED due to Exception - " + ie);
      theResult = new Result(Result.FAIL, "InstantiationException");
    } catch (IllegalAccessException iae) {
      log.error("Chain execution FAILED due to Exception - " + iae);
      theResult = new Result(Result.FAIL, "IllegalAccessException");
    } catch (InvocationTargetException ite) {
      log.error("Chain execution FAILED due to Exception - " + ite);
      theResult = new Result(Result.FAIL, "InvocationTargetException");
    }

    // Finally, return our result, regardless of what happened above.
    return theResult;

  } // End execute().


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