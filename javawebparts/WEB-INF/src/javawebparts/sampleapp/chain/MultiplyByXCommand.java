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
public class MultiplyByXCommand implements Command {


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(MultiplyByXCommand.class);


  /**
   * Factor to multiply by (X in the above equation).
   */
  private int factor;


  /**
   * Setter for the factor property.
   *
   * @param inFactor Factor property value.
   */
  public void setFactor(int inFactor) {

    factor = inFactor;

  } // End setFactor().


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
    log.info("MultiplyByXCommand init()");
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

    log.info("MultiplyByXCommand execute()...");
    log.info("a, b, answer == " + chainContext.getAttribute("a") + ", " +
      chainContext.getAttribute("b") + ", " +
      chainContext.getAttribute("answer"));
    Integer answer = (Integer)chainContext.getAttribute("answer");
    answer = new Integer(answer.intValue() * factor);
    chainContext.setAttribute("answer", answer);
    log.info("a, b, answer == " + chainContext.getAttribute("a") + ", " +
      chainContext.getAttribute("b") + ", " +
      chainContext.getAttribute("answer"));
    log.info("MultiplyByXCommand execute() done");
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

    log.info("MultiplyByXCommand cleanup()");
    log.info("**END****************************************\n\n");
    return new Result(Result.SUCCESS);

  } // End cleanup().


} // End class.
