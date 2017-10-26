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


/**
 * This class is returned by a Command or Chain.  It can be returned by any
 * of the three methds in the Command interface, and can optionally include
 * additional information for the callers' use.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class Result {


  /**
   * This value is returned by a Command to indicate the Chain should continue,
   * and it is also returned by a Chain when it completes successfully.  It is
   * also returned by a Command when its init() or cleanup() method is called
   * and it executes successfully.
   */
  public static final int SUCCESS = 1;


  /**
   * This value is returned by a Command when the Chain should be stopped due
   * to an unexpected failure reason, and it is also returned by a Chain when
   * any Command returns this value.  Note that a failure is different from an
   * Abort because a failure is an unexpected condition that is not supposed to
   * happen, where an abort is when the logic of a Command needs to terminate
   * the Chain.  It is also returned by a Command when its init() or cleanup()
   * method is called and it executes successfully.
   */
  public static final int FAIL = 2;


  /**
   * This value is returned by a Command when the Chain should be stopped due
   * to an expected logical reason, and it is also returned by a Chain when
   * any Command returns this value.  Note that a failure is different from an
   * Abort because a failure is an unexpected condition that is not supposed to
   * happen, where an abort is when the logic of a Command needs to terminate
   * the Chain.  It is also returned by a Command when its init() or cleanup()
   * method is called and it executes successfully.
   */
  public static final int ABORT = 3;


  /**
   * This value is returned by a Command when the Chain should be restarted.
   */
  public static final int RESTART_CHAIN = 4;


  /**
   * This value is returned by a Command when the Command should execute
   * again.  This should of course be used with caution as it can easily result
   * in an endless loop.
   */
  public static final int REDO_COMMAND = 5;


  /**
   * This value is returned by a Command when the Chain should jump to a
   * named Command.  Think of this like a goto statement.
   */
  public static final int JUMP_TO_COMMAND = 6;


  /**
   * The result of the Command execution.
   */
  private int code;


  /**
   * A command ID to be returned along with the result code.  Some results
   * will need this, like JUMP_TO_COMMAND and CALL_COMMAND for instance.
   */
  private String targetCommand = "";


  /**
   * This is arbitrary extra information that a Command can return for the
   * callers' use.
   */
  private String extraInfo = "";


  /**
   * Construct a Result with the given result code and no additional
   * information.
   *
   * @param inCode The result code to be stored in this Result.
   */
  public Result(int inCode) {

    code = inCode;

  } // End Result().


  /**
   * Construct a Result with the given result code and one piece of
   * additional information.
   *
   * @param inCode      The result code to be stored in this Result.
   * @param inExtraInfo The extra info to be stored in this Result.
   */
  public Result(int inCode, String inExtraInfo) {

    this(inCode);
    extraInfo = inExtraInfo;

  } // End Result().


  /**
   * Construct a Result with the given result code and one piece of
   * additional information and a target Command to jump to or call.
   *
   * @param inCode          The result code to be stored in this Result.
   * @param inExtraInfo     The extra info to be stored in this Result.
   * @param inTargetCommand The target Command to be stored in this Result.
   */
  public Result(int inCode, String inExtraInfo, String inTargetCommand) {

    this(inCode, inExtraInfo);
    targetCommand = inTargetCommand;

  } // End Result().


  /**
   * Sets the result code of this Resule.
   *
   * @param inCode The result code to be stored in this Result.
   */
  public void setCode(int inCode) {

    code = inCode;

  } // End setCode().


  /**
   * Gets the result code stored in this Result.
   *
   * @return The result code stored in this Result.
   */
  public int getCode() {

    return code;

  } // End getCode().


  /**
   * Sets the extra info stored in this Result.
   *
   * @param inExtraInfo The extra info to be stored in this Result.
   */
  public void setExtraInfo(String inExtraInfo) {

    extraInfo = inExtraInfo;

  } // End setExtraInfo().


  /**
   * Gets the extra info stored in this Result.
   *
   * @return The extra info stored in this Result.
   */
  public String getExtraInfo() {

    return extraInfo;

  } // End getExtraInfo().


  /**
   * Sets the target Command stored in this Result.
   *
   * @param inTargetCommand The target command to be stored in this Result.
   */
  public void setTargetCommand(String inTargetCommand) {

    targetCommand = inTargetCommand;

  } // End setTargetCommand().


  /**
   * Gets the target Command to be stored in this Result.
   *
   * @return The target Command stored in this Result.
   */
  public String getTargetCommand() {

    return targetCommand;

  } // End getTargetCmomand().


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