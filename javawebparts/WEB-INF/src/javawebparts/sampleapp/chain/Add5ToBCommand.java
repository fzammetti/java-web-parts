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


package javawebparts.sampleapp.chain;


import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Command for Chain example.
 */
public class Add5ToBCommand implements Command {


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(Add5ToBCommand.class);


  /**
   * Init().
   *
   * @param  chainContext The Chain Context being used by the Chain executing
   *                      this command.
   * @return              The Result object indicating how this method
   *                      execution went.
   */
  public Result init(ChainContext chainContext) {

    log.info("\n\n**START**************************************");
    log.info("Add5ToBCommand init()");
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

    log.info("Add5ToBCommand execute()...");
    log.info("a, b, answer == " + chainContext.getAttribute("a") + ", " +
      chainContext.getAttribute("b") + ", " +
      chainContext.getAttribute("answer"));
    Integer b = (Integer)chainContext.getAttribute("b");
    b = new Integer(b.intValue() + 5);
    chainContext.setAttribute("b", b);
    log.info("a, b, answer == " + chainContext.getAttribute("a") + ", " +
      chainContext.getAttribute("b") + ", " +
      chainContext.getAttribute("answer"));
    log.info("Add5ToBCommand execute() done");
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

    log.info("Add5ToBCommand cleanup()");
    log.info("**END****************************************\n\n");
    return new Result(Result.SUCCESS);

  } // End cleanup().


} // End class.
