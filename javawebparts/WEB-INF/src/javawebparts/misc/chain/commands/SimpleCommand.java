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


/**
 * This is a standard Command that simply implements all
 * the required methods of the Command interface and
 * returns Result.SUCCESS from each.  This is handy if
 * you need to write a Command that only cares about
 * the execute() method, this way you can extend this
 * class instead of directly implementing Command and
 * only have to override execute(), init() and cleanup()
 * will take care of themselves.
 */
public class SimpleCommand implements Command {


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
    } catch (ClassNotFoundException e) {
      System.err.println("LoopEndCommand" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


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
