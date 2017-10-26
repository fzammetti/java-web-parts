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
 * This is a standard Command that begins a loop.
 */
public class LoopStartCommand implements Command {


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
      System.err.println("LoopStartCommand" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(LoopStartCommand.class);


  /**
   * The starting value for the index variable.
   */
  private Integer indexStart;


  /**
   * The ending value for the index variable.
   */
  private Integer indexEnd;


  /**
   * Setter for the indexStart property.
   *
   * @param inIndexStart Start value for the loop index.
   */
  public void setIndexStart(Integer inIndexStart) {

    indexStart = inIndexStart;

  } // End setIndexStart().


  /**
   * Setter for the indexEnd property.
   *
   * @param inIndexEnd End value for the loop index.
   */
  public void setIndexEnd(Integer inIndexEnd) {

    indexEnd = inIndexEnd;

  } // End setIndexEnd().


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
    // Get the index of the current Command.
    Integer executionIndex = (Integer)chainContext.getAttribute(prefix +
      "executionIndex");
    // When the endLoop Command is encountered, we need to loop, which means
    // we need the index of the Command immediately following this one.
    // So, we store the index of this Command because the index
    // will be incremented when the loop ends (in Chain.execute()),
    // putting us at the next Command in the Chain where the loop begins.
    chainContext.setAttribute(prefix + "loopFirstCommandIndex", executionIndex);
    // Set the value of the index variable and store the ending value.
    chainContext.setAttribute(prefix + "indexVar", indexStart);
    chainContext.setAttribute(prefix + "indexEnd", indexEnd);
    // Set flag to tell us we're in a loop.
    chainContext.setAttribute(prefix + "inLoop", "true");
    // Some logging.
    if (log.isDebugEnabled()) {
      log.debug("Beginng loop...");
      log.debug(chainContext.getAttribute(prefix + "loopFirstCommandIndex"));
      log.debug(chainContext.getAttribute(prefix + "indexVar"));
      log.debug(chainContext.getAttribute(prefix + "indexEnd"));
      log.debug(chainContext.getAttribute(prefix + "inLoop"));
    }
    // All done.
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
