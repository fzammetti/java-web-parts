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


package javawebparts.misc.chain.commands;


import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This is a standard Command that ends a loop.
 */
public class LoopEndCommand implements Command {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javawebparts.misc.chain.ChainContext");
      Class.forName("javawebparts.misc.chain.Command");
      Class.forName("javawebparts.misc.chain.Result");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("LoopEndCommand" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(LoopEndCommand.class);


  /**
   * Init().
   *
   * @param  chainContext The Chain Context being used by the Chain executing
   *                      this command.
   * @return              The Result object indicating how this method
   *                      execution went.
   */
  public Result init(ChainContext chainContext) {


    return new Result(Result.SUCCESS);

  } // End init().


  /**
   * Execute().
   *
   * @param  chainContext The Chain Context being used by the Chain executing
   *                      this command.
   * @return              The Result object indicating how this method
   *                      execution went.
   */
  public Result execute(ChainContext chainContext) {

    // Construct value prefix (<catalogID>_<chainID>_)
    String prefix = chainContext.getCatalogId() + "_" +
      chainContext.getChainId() + "_";
    // Make sure we're in a loop.  If we aren't this Command is ignored.
    String inLoop = (String)chainContext.getAttribute(prefix + "inLoop");
    if (inLoop == null) {
      if (log.isDebugEnabled()) {
        log.debug(">>>>>>>> Not in loop, skipping Command");
      }
      return new Result(Result.SUCCESS);
    }
    // Ok, we're in a loop... get the current value of the loop index
    int loopIndex = ((Integer)chainContext.getAttribute(prefix +
      "indexVar")).intValue();
    loopIndex++;
    int indexEnd = ((Integer)chainContext.getAttribute(prefix +
      "indexEnd")).intValue();
    if (loopIndex > indexEnd) {
      if (log.isDebugEnabled()) {
        log.debug("Loop ending");
      }
      // The loop has run its course.  Now we need to ensure that the Chain
      // continues at the Command immediately following this Command.
      chainContext.setAttribute(prefix + "inLoop", null);
    } else {
      if (log.isDebugEnabled()) {
        log.debug("Loop continuing");
      }
      chainContext.setAttribute(prefix + "indexVar", new Integer(loopIndex));
      chainContext.setAttribute(prefix + "executionIndex",
        chainContext.getAttribute(prefix + "loopFirstCommandIndex"));
    }

    return new Result(Result.SUCCESS);

  } // End execute().


  /**
   * Cleanup().
   *
   * @param  chainContext The Chain Context being used by the Chain executing
   *                      this command.
   * @return              The Result object indicating how this method
   *                      execution went.
   */
  public Result cleanup(ChainContext chainContext) {

    return new Result(Result.SUCCESS);

  } // End cleanup().


} // End class.
